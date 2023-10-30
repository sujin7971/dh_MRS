/**
 * 구현 필요
 * 1.스크롤바 disable 옵션 설정 필요
 */
/**
 * SVG기반 판서 라이브러리.
 * _붙은 Method는 반드시 클래스 instance 객체가 해당 Method의 this필요. 
 * 
 * 
 * @author mckim
 * @version 3.1
 * @since 1.0
 */
import Mutex from '/resources/library/async-mutex/es6/Mutex.js';
import {eventMixin} from './model/mixin/eventMixin.js';
import {GraphicHistory, GraphicService, ScreenService, ToolService, EditService} from './service/index.js';
import {GraphicMachine} from './model/graphic/GraphicMachine.js';
import {Constant} from './comm/limePad.constant.js';
import {Comm} from './comm/limePad.method.js';
import {HTML} from './comm/limePad.html.js';

import {PathShape, BasicShape, Path, Polyline} from './model/tool/index.js';
import {Tool, EraserTool, ControlTool, ZoomTool, FitTool, HighlightTool} from './model/tool/index.js';
export default class LimePad{
	constructor(data = {}) {
		const optionsDefault = {
				selectable: true,
				disableTool: [],
				hideTool: ["highlight"],
			}
		const {
			width = 300,
			height = 150,
			section = null,
			options = optionsDefault,
		} = data;
		this.options = options;
		this.mutex = new Mutex();
		if(!section || section == ""){
			console.error("캔버스가 위치할 선택자 혹은 DOM이 설정되어 있지 않습니다.");
            return null;//can't start if not initialized.
		}
		if(typeof section == "string") {
			this.sectionNode = document.querySelector("#"+section);
		}else{
			this.sectionNode = section;
		}
		_deployPadNode.call(this);
		_setGraphicService.call(this);
		_setToolService.call(this);
		
		_setScreenService.call(this, {
			width: width,
			height: height
		});
		_setPadOptions.call(this, options);
	}
	update(data = {}){
		const {
			options = {},
		} = data;
		_setPadOptions.call(this, options);
	}
	start(){
		const _class = this;
		const containerNode = this.containerNode;
		$(containerNode).on({
			pointerenter: function(evt){
				evt.stopPropagation();
				evt.preventDefault();
				if(_class.nowEnter == true){
					return;
				}
				_deployToolEvt.call(_class, evt);
				_class.nowEnter = true;
			},
			pointerleave: function(evt){
				evt.stopPropagation();
				evt.preventDefault();
				_undeployToolEvt.call(_class, evt);
				_class.nowEnter = false;
			}
		});
		const selectedTool = this.eventTool;
		if(selectedTool){
			const evt = this.eventObject;
			_deployToolEvt.call(this, evt);
		}
	}
	stop(){
		const _class = this;
		return new Promise((resolve, rejct) => {
			const containerNode = _class.containerNode;
			containerNode.style.cursor = "default";
			
			_class.laserEnd();
			_class.eventTool?.cursorRemove?.();
			_class.editService.stopEdit();
			
			$(containerNode).trigger("pointerup");
			$(containerNode).unbind("pointerdown pointerenter pointermove pointerup pointerleave");
			//판서도중 화면제어 요청이 들어왔을때 판서 종료 완료를 보장하기위한 임시방편
			setTimeout(() => resolve());
		});
	}
	clear(){
		this.editService.stopEdit();
		this.graphicService.empty();
		this.screenService.transformScale();
		this.screenService.setBackgroundColor();
	}
	fitScreenToWindow(){
		this.screenService.fitScreenToWindow();
	}
	fitVerticalOrientation(){
		this.screenService.fitVerticalOrientation();
	}
	fitHorizontalOrientation(){
		this.screenService.fitHorizontalOrientation();
	}
	setFocus(){
		
	}
	setBackground(data = {}){
		return new Promise(async (resolve, reject) => {
			const {
				url = null,
				color = "white",
			} = data;
			if(url){
				try{
					await this.screenService.setBackgroundImage(url);
				}catch(err){
					reject(err);
				}
			}else{
				this.screenService.setBackgroundColor(color);
			}
			resolve();
		});
	}
	getSourceImg() {
		return this.screenService.image;
	}
	/* 판서한 내용이 있는지 확인 */
	isEmpty() {
		return this.graphicService.isEmpty();
	}
	/* 서버에 저장할 판서 이미지 반환 */
	getPadImage() {
		const _class = this;
		return new Promise((resolve, reject) => {
			_class.editService.stopEdit();
			const backgroundImage = _class.getSourceImg();
			_convertSvgToCanvas.call(_class).then(function(data = {}){
				const {
					canvas,
					ctx
				} = data;
				if(backgroundImage) {
					ctx.globalCompositeOperation = "destination-over"; 
					ctx.drawImage(backgroundImage, 0, 0, canvas.width, canvas.height);
				}
				let image = canvas.toDataURL();
		    	resolve(image);
			})
		});
	}
	selectTool(name){
		const tool = this.toolService.selectTool(name);
		_undeployToolEvt.call(this);
		_deployToolEvt.call(this);
	}
	disableTool(nameList){
		const _class = this;
		nameList.forEach(name => {
			const tool = _class.toolService.getTool
		})
	}
	getToolInterface(){
		return this.toolService.getToolInterface();
	}
	loadGraphicContext(contextCollection){
		return new Promise((resolve, reject) => {
			this.graphicService.empty();
			for(const graphicContext of contextCollection){
				this.graphicHistory.take();
				this.graphicService.load(graphicContext);
			}
			resolve(this.graphicService.getAll());
		});
	}
	getGraphicContext(){
		const graphicList = this.graphicService.getAll();
		return graphicList.map(g => JSON.stringify(g));
	}
	undo(){
		this.mutex.runExclusive(async () => {
			this.editService.stopEdit();
			this.graphicHistory.undo();
		});
	}
	redo(context){
		this.mutex.runExclusive(async () => {
			this.editService.stopEdit();
			if(this.graphicHistory.hasRedo()){
				this.graphicHistory.redo();
			}else{
				this.graphicHistory.take();
				//redo의 경우 페이지 변경시 history가 모두 초기화되기때문에 redoStack이 없는 경우 새로 그려줘야함
				this.graphicService.load(context);
			}
		});
	}
	laserStart(pos){
		const laser = this.toolService.getTool("laser");
		laser?.update({
			node: this.containerNode
		});
		laser?.updatePos({
			posX: pos.x,
			posY: pos.y
		});
		laser?.startDraw();
		this.laser = laser;
	}
	laserMove(pos){
		// 레이저 사용 중 중간진입인 경우 레이저 생성
		if($(this.containerNode).children("#laserCircle").length <= 0) {
			this.laserStart(pos);
			return;
		}
		this.laser?.updatePos({
			posX: pos.x,
			posY: pos.y
		});
	}
	laserEnd(){
		this.laser?.endDraw();
		delete this.laser;
	}
}

function _deployPadNode(){
	const sectionNode = this.sectionNode;
	sectionNode.setAttribute("node-role", "section");
	
	const screenNode = document.createElement("div");
	screenNode.setAttribute("node-role", "screen");
	sectionNode.appendChild(screenNode);
	sectionNode.screenNode = screenNode;
	this.screenNode = screenNode;
	
	const containerNode = document.createElement("div");
	containerNode.setAttribute("node-role", "container");
	screenNode.appendChild(containerNode);
	screenNode.containerNode = containerNode;
	this.containerNode = containerNode;
	
	const svgNode = GraphicMachine.createElementNS("svg", {
		role: "area",
	});
	svgNode.style.overflow = "hidden";
	svgNode.setAttribute("node-role", "deploy");
	const depthLow = GraphicMachine.createElementNS("g", {
		role: "depth",
		"depth-level": 1,
	});
	const depthMid = GraphicMachine.createElementNS("g", {
		role: "depth",
		"depth-level": 2,
	});
	const depthHigh = GraphicMachine.createElementNS("g", {
		role: "depth",
		"depth-level": 3,
	});
	const depthNodeArr = [depthLow, depthMid, depthHigh];
	svgNode.appendChild(depthLow);
	svgNode.appendChild(depthMid);
	svgNode.appendChild(depthHigh);
	svgNode.getDeployNode = (level) => {
		if(level > 3){
			level = 3;
		}else if(level < 1){
			level = 1;
		}
		return depthNodeArr[level - 1];
	}
	svgNode.addGraphic = (param = {}) => {
		const {
			element,
			level = 1
		} = param;
		if(level > 3){
			level = 3;
		}else if(level < 1){
			level = 1;
		}
		const deployNode = depthNodeArr[level - 1];
		deployNode.appendChild(element);
	}
	svgNode.clearGraphic = () => {
		depthNodeArr.forEach(node => node.innerHTML = "");
	}
	containerNode.appendChild(svgNode);
	containerNode.svgNode = svgNode;
	this.svgNode = svgNode;
}

function _deployToolEvt(evt){
	const selectedTool = this.eventTool;
	if(evt){
		this.eventObject = evt;
	}
	if(this.eventObject){
		this.eventCallback?.call(selectedTool, this.eventObject);
	}
}

function _undeployToolEvt(evt){
	const selectedTool = this.eventTool;
	
	selectedTool?.cursorRemove?.();
	
	const containerNode = this.containerNode;
	$(containerNode).trigger("pointerup");
	$(containerNode).unbind("pointerdown pointermove pointerup");
	$(containerNode).css("cursor","default");
	
}

function _setPadOptions(options = {}){
	const {
		selectable = this.options.selectable,
		disableTool = this.options.disableTool,
		hideTool = this.options.hideTool,
	} = options;
	this.options.selectable = selectable;
	this.options.disableTool = disableTool;
	this.options.hideTool = hideTool;
	const svgNode = this.svgNode;
	if(selectable == true){
		const svgCollection = document.querySelectorAll("svg");
		svgCollection.forEach(svg => svg.setAttribute("pointer-events", "visiblePainted"));
	}else{
		const svgCollection = document.querySelectorAll("svg");
		svgCollection.forEach(svg => svg.setAttribute("pointer-events", "none"));
	}
	const toolCollection = this.toolService.getAllTool();
	toolCollection.forEach(tool => {tool.enable(); tool.show()});
	const disableCollection = toolCollection.filter(tool => disableTool.includes(tool.type));
	disableCollection.forEach(tool => tool.disable());
	const hideCollection = toolCollection.filter(tool => hideTool.includes(tool.type));
	hideCollection.forEach(tool => tool.hide());
}

function _setScreenService(data = {}){
	const _class = this;
	const {
		width,
		height,
	} = data;
	const screenService = new ScreenService({
		instance: _class,
		width: width,
		height: height,
	});
	screenService.on("transformScale", function(scale){
		//_class.editService.positioning?.();
		_class.trigger("transformScale", scale);
	});
	this.screenService = screenService;
}

function _setGraphicService(){
	const graphicService = new GraphicService({
		instance: this,
	});
	this.graphicService = graphicService;
	
	const graphicHistory = new GraphicHistory(graphicService);
	this.graphicHistory = graphicHistory;
}

function _setToolService(){
	const _class = this;
	this.editService = new EditService({
		rootNode: _class.containerNode,
		svgNode: _class.svgNode
	});
	this.toolService = new ToolService();
	setEditService();
	setToolService();
	function setEditService(){
		_class.editService.on("deleteElem", id => {
			_class.editService.stopEdit();
			_class.graphicHistory.take();
			_class.graphicService.remove(id);
			_class.trigger("addContext", {
				id: id,
				action: "D"
			});
		});
		_class.editService.on("changeFontSize", (selectedGraphic, size) => {
			_class.graphicHistory.take();
			selectedGraphic.shape.update({
				fontSize: size
			});
			_class.trigger("addContext", {
				id: selectedGraphic.id,
				action: "U",
				shape: {
					fontSize: size
				}
			});
		});
		_class.editService.on("changeFontColor", (selectedGraphic, color) => {
			_class.graphicHistory.take();
			selectedGraphic.shape.update({
				color: color
			});
			_class.trigger("addContext", {
				id: selectedGraphic.id,
				action: "U",
				shape: {
					color: color
				}
			});
		});
		_class.editService.on("changeText", (selectedGraphic, newText) => {
			const oldText = selectedGraphic.shape.getText();
			if(oldText == newText){
				return;
			}
			_class.graphicHistory.take();
			selectedGraphic.shape.update({
				text: newText
			});
			_class.trigger("addContext", {
				id: selectedGraphic.id,
				action: "U",
				shape: {
					text: newText
				}
			});
		});
	}
	function setToolService(){
		const toolCollection = _class.toolService.getAllTool();
		toolCollection.forEach(tool => tool.on("select", function(){
			if(tool instanceof ZoomTool){
				if(tool.zoom == "in"){
					_class.screenService.zoomin();
				}else if(tool.zoom == "out"){
					_class.screenService.zoomout();
				}
			}else if(tool instanceof FitTool){
				if(tool.fit == "horizontal"){
					_class.screenService.fitHorizontalOrientation();
				}else if(tool.fit == "vertical"){
					_class.screenService.fitVerticalOrientation();
				}
			}else{
				_class.editService.stopEdit();
				_class.screenService.switchCtrlmode(false);
				_class.screenService.switchErasermode(false);
				if(tool instanceof Path){
					_class.eventTool = tool;
					_class.eventCallback = tool.pointerEnter;
					_setPathToolEvent.call(_class, tool);
				}else if(tool instanceof Polyline){
					_class.eventTool = tool;
					_class.eventCallback = tool.pointerEnter;
					_setPolylineToolEvent.call(_class, tool);
				}else if(tool instanceof BasicShape){
					_class.eventTool = tool;
					_class.eventCallback = tool.pointerEnter;
					_setBasicToolEvent.call(_class, tool);
				}else if(tool instanceof EraserTool){
					_class.eventTool = tool;
					_class.eventCallback = tool.pointerEnter;
					_class.screenService.switchErasermode(true);
					_setEraserToolEvent.call(_class, tool);
				}else if(tool instanceof ControlTool){
					_class.eventTool = tool;
					_class.eventCallback = tool.pointerEnter;
					_class.screenService.switchCtrlmode(true);
					if(tool instanceof HighlightTool){
						_setHightlightToolEvent.call(_class, tool);
					}else{
						_setControlToolEvent.call(_class, tool);
					}
				} 
			}
		}));
	}
}

/* 선 그리기 이벤트 설정 */
function _setPathToolEvent(tool){
	const _class = this;
	const svgNode = _class.svgNode;
	let addTimeout;
	let context;
	let graphic;
	let pointGroupList, nowPointGroup;
	//그리기 시작
	tool.drawStart = function(point){
		let nowPointGroup = [];
		nowPointGroup.push(point);
		if(addTimeout){
			clearTimeout(addTimeout);
			addTimeout = null;
			pointGroupList.push(nowPointGroup);
		}else{
			_class.graphicHistory.dump();
			pointGroupList = [];
			pointGroupList.push(nowPointGroup);
			const context = tool.toContext();
			graphic = _class.graphicService.create(context);
			graphic.setLevel(tool.getLevel());
			svgNode.appendChild(graphic.getPackedElem());
		}
		//그리기
		tool.drawMove = function(point, pointSet, pointGroup){
			nowPointGroup.push(point);
			graphic.shape.update({
				pointGroup: pointGroupList,
			});
		}
		//그리기 종료
		tool.drawEnd = function(pointGroup){
			addTimeout = setTimeout(() => {
				const context = graphic.toContext();
				_class.graphicHistory.take();
				_class.graphicService.insert(graphic);
				_class.trigger("addContext", {
					...context,
					action: "N"
				});
				addTimeout = null;
			}, 200);
		}
		tool.drawCancel = function(){
			pointGroupList.pop();
			graphic.shape.update({
				pointGroup: pointGroupList,
			});
		}
	}
}

/* 선 그리기 이벤트 설정 */
function _setPolylineToolEvent(tool){
	const _class = this;
	const svgNode = _class.svgNode;
	//그리기 시작
	tool.drawStart = function(point){
		_class.graphicHistory.dump();
		const context = tool.toContext();
		const graphic = _class.graphicService.create(context);
		graphic.setLevel(tool.getLevel());
		svgNode.appendChild(graphic.getPackedElem());
		//그리기
		tool.drawMove = function(point, pointSet, pointGroup){
			graphic.shape.update({
				pointGroup: pointGroup,
			});
		}
		//그리기 종료
		tool.drawEnd = function(pointGroup){
			const context = graphic.toContext();
			_class.graphicHistory.take();
			_class.graphicService.insert(graphic);
			_class.trigger("addContext", {
				...context,
				action: "N"
			});
		}
		tool.drawCancel = function(){
			graphic.destroy();
		}
	}
}

/* 도형 그리기 이벤트 설정 */
function _setBasicToolEvent(tool){
	const _class = this;
	const svgNode = _class.svgNode;
	//그리기 시작
	tool.drawStart = function(beginPos){
		_class.graphicHistory.dump();
		const context = tool.toContext();
		const graphic = _class.graphicService.create(context);
		graphic.setLevel(tool.getLevel());
		svgNode.appendChild(graphic.getPackedElem());
		let width = 0, height = 0;
		//그리기
		tool.drawMove = function(beginPos, nowPos){
			width = Math.abs(nowPos.x - beginPos.x);
			height = Math.abs(nowPos.y - beginPos.y);
			graphic.shape.update({
				startPos: [beginPos.x, beginPos.y],
				endPos: [nowPos.x, nowPos.y],
				strokeDasharray: (width < 40 || height < 20)?"6 4":""
			});
		}
		//그리기 종료
		tool.drawEnd = function(nowPos){
			graphic.shape.update({
				stroke: "none",
			});
			if(width < 40 || height < 20){
				graphic.shape.update({
					startPos: [beginPos.x, beginPos.y],
					endPos: [beginPos.x + 300, beginPos.y + 100],
					strokeDasharray: ""
				});
			}
			const context = graphic.toContext();
			_class.graphicHistory.take();
			_class.graphicService.insert(graphic);
			_class.trigger("addContext", {
				...context,
				action: "N"
			});
			_class.selectTool("pointer");
			_class.graphicService.select(graphic.id);
			_class.editService.startEdit(graphic);
		}
		tool.drawCancel = function(){
			trigger("cancelStroke");
		}
	}
}

function _setEraserToolEvent(tool){
	const _class = this;
	const svgNode = _class.svgNode;
	tool.erase = function(point, target){
		_class.mutex.runExclusive(async () => {
			if(target.nodeName != "path" && target.nodeName != "polyline"){
				return;
			}
			const containerNode = target.closest('g[role="container"]');
			const id = containerNode.id;
			_class.graphicHistory.dump();
			const deletedGraphic = _class.graphicService.remove(id);
			if(deletedGraphic){
				_class.graphicHistory.take();
				_class.trigger("addContext", {
					id: id,
					action: "D"
				});
			}else{
				containerNode.remove();
			}
		});
	}
	tool.eraseEnd = function(){
		_class.graphicService.runGarbageCollector();
	}
}

function _setHightlightToolEvent(tool){
	const _class = this;
	tool.laserStart = function(startPos){
		const laser = tool;
		laser.update({
			node: _class.containerNode
		});
		laser.updatePos({
			posX: startPos.x,
			posY: startPos.y
		});
		laser.startDraw();
		_class.trigger("laserStart", startPos);
		
		tool.laserMove = function(nowPos){
			laser.updatePos({
				posX: nowPos.x,
				posY: nowPos.y
			});
			_class.trigger("laserMove", nowPos);
		}
		
		tool.laserEnd = function(){
			laser.endDraw();
			_class.trigger("laserEnd");
		}
	}
}

/* 컨트롤 도구 이벤트 설정 */
function _setControlToolEvent(tool){
	const _class = this;
	const svgNode = _class.svgNode;
	
	let startScroll = null;
	let timeout = null;
	let startStamp = null;
	tool.scroll = function(direction, timeStamp){
		if(timeout){
			clearTimeout(timeout);
		}else{
			startStamp = timeStamp;
			startScroll = _class.sectionNode.scrollTop;
		}
		timeout = setTimeout( () => {
			const duration = timeStamp - startStamp;
			timeout = null;
			const scrollTop = _class.sectionNode.scrollTop;
			if(scrollTop == startScroll && duration < 40){
				_class.trigger("swipe", direction);
			}
		}, 100)
	};
	tool.controlStart = function(evt, refPos){
		const target = evt.target;
		const role = target.getAttribute("role");
		let lastScroll = _class.sectionNode.scrollTop;
		if(target == _class.containerNode || target == _class.svgNode){
			setFocusEvt();
		}else if(role == "boundary-vertex"){
			_class.sectionNode.style.overflow = "none";
			_class.sectionNode.style.touchAction = "none";
			const direction = target.getAttribute("resize-direction");
			setResizeEvt(evt, refPos, direction);
		}else if(role == "boundary-box"){
			_class.sectionNode.style.overflow = "none";
			_class.sectionNode.style.touchAction = "none";
			setMoveEvt(evt, refPos);
		}else{
			try{
				delete tool.controlMove;
				delete tool.controlEnd;
				const conGroupElem = target.closest('g[role="container"]');
				const selectedGraphic = _class.graphicService.select(conGroupElem.id);
				_class.editService.startEdit(selectedGraphic);
			}catch(error){
				
			}
		}
		/* 화면 이동 이벤트 설정 */
		function setFocusEvt(){
			/*delete tool.controlMove;
			delete tool.controlEnd;
			
			_class.sectionNode.style.overflow = "auto";
			_class.sectionNode.style.touchAction = "auto";*/
			
			_class.editService.stopEdit();
			tool.controlMove = function(evt, startPos, endPos, distance){
				//console.log("focusMove", evt);
				const scale = _class.screenService.scale;
				const moveX = startPos.x - endPos.x;
				const moveY = startPos.y - endPos.y;
				_class.sectionNode.scrollLeft += moveX * scale;
				_class.sectionNode.scrollTop += moveY * scale;
				
				_class.trigger("pointerMove", tool, [endPos.x, endPos.y]);
			};
			tool.controlEnd = function(endPos, distance, direction, duration){
				//console.log("controlEnd", "endPos", endPos, "distance", distance, "direction", direction, "duration", duration);
				_class.editService.show();
				let nowScroll = _class.sectionNode.scrollTop;
				if(nowScroll == lastScroll && duration < 125 && distance > 100){
					_class.trigger("swipe", direction);
				}
				//console.log("controlEnd", "nowScroll", nowScroll, "lastScroll", lastScroll);
				lastScroll = nowScroll;
			};
			tool.zoom = function(direction, distance){
				if(direction == "in"){
					_class.screenService.zoomin(0.01);
				}else{
					_class.screenService.zoomout(0.01);
				}
			}
		}
		/* Element 이동 이벤트 설정 */
		function setMoveEvt(evt, refPos){
			_class.graphicHistory.dump();
			_class.editService.hide();
			
			const graphic = _class.graphicService.getSelected();
			const targetElem = graphic.getPackedElem();
			const targetBBox = targetElem.getBBox();
			
			const shape = graphic.shape;
			
			const posX = shape.posX;
			const posY = shape.posY;
			
			const boxX = targetBBox.x;
			const boxY = targetBBox.y;
			const startX = boxX - posX;
			const startY = boxY - posY;
			const width = targetBBox.width;
			const height = targetBBox.height;
			tool.controlMove = function(evt, startPos, endPos, distance){
				const moveX = (endPos.x - refPos.x);
				const moveY = (endPos.y - refPos.y);
				const boundWidth = _class.screenService.width;
				const boundHeight = _class.screenService.height;
				let newX = posX + moveX;
				let newY = posY + moveY;
				
				newX = (startX + newX < 0)?startX*-1:newX;
				newY = (startY + newY < 0)?startY*-1:newY;
				newX = (newX + startX + width > boundWidth)?(boundWidth - (width + startX)):newX;
				newY = (newY + startY + height > boundHeight)?(boundHeight - (height + startY)):newY;
				graphic.shape.update({
					posX: newX,
					posY: newY
				});
			};
			tool.controlEnd = function(endPos, distance, duration){
				_class.editService.show();
				_class.graphicHistory.take();
				_class.trigger("addContext", {
					id: graphic.id,
					action: "U",
					shape: {
						posX: shape.posX,
						posY: shape.posY
					}
				});
			};
		}
		/* Element 확대/축소 이벤트 설정 */
		function setResizeEvt(evt, refPos, direction){
			_class.graphicHistory.dump();
			_class.editService.hide();
			
			const graphic = _class.graphicService.getSelected();
			const targetElem = graphic.getPackedElem();
			const targetBBox = targetElem.getBBox();
			
			const shape = graphic.shape;
			
			const posX = shape.posX;
			const posY = shape.posY;
			
			const boxX = targetBBox.x;
			const boxY = targetBBox.y;
			const startX = boxX - posX;
			const startY = boxY - posY;
			const width = targetBBox.width;
			const height = targetBBox.height;
			const resizeFactor = Constant.ResizeFactor(direction);
			if(shape.type == "path"){
				setPathShapeResizeEvt();
			}else{
				setBasicShapeResizeEvt();
			}
			function setPathShapeResizeEvt(){
				tool.controlMove = function(evt, startPos, endPos, distance){
					const boundWidth = _class.screenService.width;
					const boundHeight = _class.screenService.height;
					const moveX = (endPos.x - refPos.x);
					const moveY = (endPos.y - refPos.y);
					const newX = posX + (moveX * resizeFactor.x);
					const newY = posY + (moveY * resizeFactor.y);
					const newWidth = width + (moveX * resizeFactor.width);
					const newHeight = height + (moveY * resizeFactor.height);
					if(newX + startX < 0 || newY + startY < 0){
						return;
					}
					if(newWidth < 40 || newHeight < 20){
						return;
					}
					if((newX + startX + newWidth > boundWidth) || (newY + startY + newHeight > boundHeight)){
						return;
					}
					graphic.shape.update({
						posX: newX,
						posY: newY,
						width: newWidth,
						height: newHeight
					});
				};
				tool.controlEnd = function(endPos, distance, duration){
					_class.editService.show();
					_class.graphicHistory.take();
					_class.trigger("addContext", {
						id: graphic.id,
						action: "U",
						shape: {
							posX: shape.posX,
							posY: shape.posY,
							width: shape.width,
							height: shape.height
						}
					});
				};
			}
			function setBasicShapeResizeEvt(){
				tool.controlMove = function(evt, startPos, endPos, distance){
					const boundWidth = _class.screenService.width;
					const boundHeight = _class.screenService.height;
					let moveX = (endPos.x - refPos.x);
					let moveY = (endPos.y - refPos.y);
					
					let newX = boxX + (moveX * resizeFactor.x);
					let newY = boxY + (moveY * resizeFactor.y);
					let newWidth = width + (moveX * resizeFactor.width);
					let newHeight = height + (moveY * resizeFactor.height);
					let newStartPos = [newX, newY];
					let newEndPos = [newX + newWidth, newY + newHeight];
					if(newX < 0 || newY < 0){
						return;
					}
					if(newWidth < 40 || newHeight < 20){
						return;
					}
					if((newX + newWidth > boundWidth) || (newY + newHeight > boundHeight)){
						return;
					}
					graphic.shape.update({
						posX: 0,
						posY: 0,
						startPos: newStartPos,
						endPos: newEndPos
					});
				};
				tool.controlEnd = function(endPos, distance, duration){
					_class.editService.show();
					_class.graphicHistory.take();
					_class.trigger("addContext", {
						id: graphic.id,
						action: "U",
						shape: {
							posX: 0,
							posY: 0,
							startPos: shape.startPos,
							endPos: shape.endPos
						}
					});
				};
			}
		}
	}
}

/* SVG 엘리먼트를 CANVAS에 그리기  */
function _convertSvgToCanvas() {
	const _class = this;
	return new Promise((resolve, reject) => {
		const svgNode = _class.svgNode;
		
		const canvas = document.createElement("canvas");                               
	    canvas.width = svgNode.getAttribute("width");                                            
	    canvas.height = svgNode.getAttribute("height");                                                         
	    const ctx = canvas.getContext("2d");
	    
		const graphicCollection = _class.graphicService.getAll();
		for(const graphic of graphicCollection){
			const shape = graphic.getShape();
			const shapeType = shape.type;
			if(shapeType == "textarea"){
				const graphicElem = graphic.getGraphicElem();
				const foreignElem = graphic.getForeignElem();
				const textareaElem = foreignElem.querySelector("textarea");
				const computedStyle = window.getComputedStyle(textareaElem);
				Array.from(computedStyle).forEach(
					    key => textareaElem.style.setProperty(key, computedStyle.getPropertyValue(key), 'important')
				)
				textareaElem.innerHTML = textareaElem.value;
			}
		}
		const img = new Image();
		img.crossOrigin = '*';
		img.onload = function() {
			ctx.drawImage(this, 0, 0, canvas.width, canvas.height);
		    resolve({canvas, ctx});
		};
		const url = 'data:image/svg+xml; charset=utf8, ' + encodeURIComponent(serializeString(svgNode));
		img.src = url;
		/**
		 * https://observablehq.com/@bumbeishvili/foreignobject-exporting-issue
		 */
		function serializeString(svg) {
	        const xmlns = 'http://www.w3.org/2000/xmlns/';
	        const xlinkns = 'http://www.w3.org/1999/xlink';
	        const svgns = 'http://www.w3.org/2000/svg';
	        svg = svg.cloneNode(true);
	        const fragment = window.location.href + '#';
	        const walker = document.createTreeWalker(svg, NodeFilter.SHOW_ELEMENT, null, false);
	        while (walker.nextNode()) {
	          for (const attr of walker.currentNode.attributes) {
	            if (attr.value.includes(fragment)) {
	              attr.value = attr.value.replace(fragment, '#');
	            }
	          }
	        }
	        svg.setAttributeNS(xmlns, 'xmlns', svgns);
	        svg.setAttributeNS(xmlns, 'xmlns:xlink', xlinkns);
	        const serializer = new XMLSerializer();
	        const string = serializer.serializeToString(svg);
	        return string;
		}
	});
}
Object.assign(LimePad.prototype, eventMixin);