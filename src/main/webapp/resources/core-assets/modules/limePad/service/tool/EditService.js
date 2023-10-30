/**
 * 
 */
import {Util, Modal, eventMixin} from '/resources/core-assets/essential_index.js';
import {HTML} from '../../comm/limePad.html.js';
import {GraphicFactory} from '../../model/graphic/GraphicFactory.js';
import Textarea from '../../model/shape/basic/Textarea.js';
export default class EditService{
	constructor(data = {}){
		const {
			rootNode = null,
			svgNode = null,
		} = data;
		this.rootNode = rootNode;
		this.svgNode = svgNode;
	}
	startEdit(graphic){
		this.stopEdit();
		this.graphic = graphic;
		if(graphic){
			const shape = graphic.getShape();
			if(shape?.type == "textarea"){
				const foreignElem = graphic.getForeignElem();
				this.textareaElem = foreignElem.querySelector("textarea");
			}
		}
		_deployEditTool.call(this);
		_deployEditBox.call(this);
		_setEditEvt.call(this);
		_syncWithScreen.call(this);
	}
	stopEdit(){
		const graphic = this.graphic;
		if(graphic){
			const shape = graphic.getShape();
			if(shape?.type == "textarea"){
				const foreignElem = graphic.getForeignElem();
				const textareaElem = foreignElem.querySelector("textarea");
				textareaElem.blur();
				textareaElem.setAttribute("disabled", true);
			}
		}
		
		this.boundaryNode?.remove();
		delete this.boundaryNode;
		
		this.editTool?.remove();
		delete this.editTool;
		
		this.observer?.disconnect();
		delete this.observer;
	}
	show(){
		this.editTool?.show();
	}
	hide(){
		this.editTool?.hide();
	}
}

function _deployEditTool(){
	const _class = this;
	const rootNode = _class.rootNode;
	
	const graphic = _class.graphic;
	const shape = graphic.getShape();
	
	const sizelist = [28, 32, 36, 40, 44];
	const colorlist = ['#000000', '#e90000', '#0076e3', '#00b100', '#ffba00', '#ffffff'];
	
	setEditor();
	setObserver();
	
	function setEditor(){
		const html = HTML.editTool;
		
		const $mainCon = $(html.con);
		_class.editTool = $mainCon;
		rootNode.appendChild($mainCon[0]);
		
		const header = html.header;
		const $headercon = $(header.con);
		$mainCon.append($headercon);
		if(shape instanceof Textarea){
			setPalete();
		}
		setDelete();
		//팔레트 설정
		function setPalete(){
			const palete = html.palete;
			const $palete = $(palete.con);
			
			const $font = $(palete.font.con);
			for(const [index, size] of sizelist.entries()){
				const $item = $(palete.font.item);
				$item.text(index + 1);//폰트 실제 크기가 아닌 단계 숫자로 표시
				$font.append($item);
				$item.on("click", function(){
					_class.trigger("changeFontSize", graphic, size);
				});
			}
			$palete.append($font);
			
			const $color = $(palete.color.con);
			
			for(const color of colorlist){
				const $item = $(palete.color.item);
				$item.css({
					border: "1px solid #999", 
					"background-color": color
				});
				$color.append($item);
				$item.on("click", function(){
					$paleteIcon.css("color", color);
					_class.trigger("changeFontColor", graphic, color);
				});
			}
			$palete.append($color);
			
			const $close = $(palete.close.con);
			const $closeicon = $(palete.close.icon);
			$close.append($closeicon);
			$close.on("click", function(){
				$palete.slideUp(100);
			});
			$palete.append($close);
			
			$mainCon.append($palete);
			
			const $paleteIcon = $(header.paleteIcon);
			$paleteIcon.css("color", shape.color);
			$paleteIcon.on("click", function(evt){
				if ($palete.is(':visible')) {
					$palete.slideUp(100);
				}else{
					$palete.slideDown(100);
				}
			});
			$headercon.append($paleteIcon);
		}
		function setDelete(){
			const $delIcon = $(header.delIcon);
			$headercon.append($delIcon);
			//Element 삭제
			$delIcon.on("click", async (evt) => {
				evt.stopPropagation();
				evt.preventDefault();
				const res = await Modal.confirm({
					msg: "텍스트 입력을 삭제합니다.",
					delMode: true,
				});
				if(res == "DELETE"){
					_class.trigger("deleteElem", graphic.id);
				}else{
					_class.textareaElem.focus();
				}
			});
		}
	}
	//편집 모드로 설정된 Element의 변화 감지 및 그에 맞춘 편집 도구 위치 조정
	function setObserver(){
		const observeAttributes = ["x", "y", "width", "height", "scale"];
		const callback = function(mutationsList, observer) {
		    // Use traditional 'for loops' for IE 11
		    for(const mutation of mutationsList) {
		        if (mutation.type === 'childList') {
		            console.log('A child node has been added or removed.');
		        }
		        else if (mutation.type === 'attributes') {
		        	const attributeName = mutation.attributeName;
		        	if(observeAttributes.includes(attributeName)){
		        		_syncWithScreen.call(_class, attributeName);
		        	}
		        }
		    }
		};
		const observer = new MutationObserver(callback);
		// 감시자의 설정:
		var config = { attributes: true, childList: true, characterData: true };
		// 감시자 옵션 포함, 대상 노드에 전달
		const observeTarget = graphic.getCoordElem();
		observer.observe(observeTarget, config);
		observer.observe(_class.rootNode, config);
		_class.observer = observer;
	}
}

function _deployEditBox(){
	const _class = this;
	
	const graphic = _class.graphic;
	const editTargetElem = graphic.getPackedElem();
	const paintingElem = graphic.getGraphicElem();
	const shape = graphic.getShape();
	
	const boundaryNode = GraphicFactory.createEditBoundary({
		x: 0,
		y: 0,
		width: 0,
		height: 0,
	});
	_class.svgNode.appendChild(boundaryNode);
	_class.boundaryNode = boundaryNode;
}

function _setEditEvt(){
	const _class = this;
	
	const graphic = _class.graphic;
	const shape = graphic.shape;
	if(shape.type == "textarea"){
		const foreignElem = graphic.getForeignElem();
		const textareaElem = foreignElem.querySelector("textarea");
		$(textareaElem).unbind();
		$(textareaElem).on("change", function(evt){
			textareaElem.innerHTML = textareaElem.value;
			_class.trigger("changeText", graphic, textareaElem.value);
		});
		$(textareaElem).on("focus", function(evt){
			const text = textareaElem.innerHTML;
		});
		$(textareaElem).on("keyup", function(evt){
			const keyCode = evt.keyCode;
			switch(keyCode){
			case 8://백스페이스
				break;
			case 13://엔터
				break;
			}
			textareaElem.innerHTML = textareaElem.value;
			_class.trigger("changeText", graphic, textareaElem.value);
		});
		$(textareaElem).on("blur", function(evt){
			const text = textareaElem.innerHTML;
		});
		textareaElem.removeAttribute("disabled");
		textareaElem.focus();
	}
}

function _syncWithScreen(attributeName){
	const _class = this;
	
	const graphic = _class.graphic;
	const shape = graphic.shape;
	
	const rootNode = _class.rootNode;
	const containerRect = rootNode.getBoundingClientRect();
	const coordElem = graphic.getCoordElem();
	
	const targetRect = coordElem.getBoundingClientRect();
	const targetBox = coordElem.getBBox();
	const x = coordElem.getAttribute("x") * 1;
	const y = coordElem.getAttribute("y") * 1;
	const width = (shape.width)?shape.width:targetBox.width;
	const height = (shape.height)?shape.height:targetBox.height;
	// 선(테두리) 굵기에 맞춰 영역 조절
	const scale = rootNode.getAttribute("scale");
	const borderWidth = 4;
	const strokeWidth = shape.strokeWidth * scale;
	const correctionVal = (borderWidth + strokeWidth) * scale;
	
	const offsetX = (targetRect.left - containerRect.left);
	const offsetY = (targetRect.top - containerRect.top);
	const offsetLeft = offsetX;
	const offsetTop = (offsetY < 40)?40:offsetY;
	
	const editTool = _class.editTool;
	editTool.css("transform", "scale("+(1/scale)+")");
	switch(attributeName){
    	case "x":
    		editTool.css("left", (x - correctionVal/2) + "px");
    		break;
    	case "y":
    		editTool.css("top", (y - correctionVal/2) + "px");
    		break;
    	case "width":
    		break;
    	case "height":
    		break;
    	default: 
    		editTool.css("left", (x - correctionVal/2) + "px");
    		editTool.css("top", (y - correctionVal/2) + "px");
    		break;
	}
	_class.boundaryNode.update({
		x: x - strokeWidth / 2,
		y: y - strokeWidth / 2,
		width: width + strokeWidth,
		height: height + strokeWidth,
		scale: scale
	});
}
Object.assign(EditService.prototype, eventMixin);