import {Comm} from '../../../comm/limePad.method.js';
import {toolMixin} from '../toolMixin.js';
export const pathMixin = {
	__proto__: toolMixin,
	enable(){
		super.enable();
	},
	disable(){
		super.disable();
	},
	show(){
		super.show();
	},
	hide(){
		super.hide();
	},
	toContext(){
		const context = {
			id: "G"+Comm.getUUID(6),
			action: "N",
			shape: new this.constructor(this)
		}
		return context;
	},
	createInterface(icon, sizeOption, colorOption){
		if(this.element){
			return;
		}
		const tool = this;
		const html = {
			toolCon : '<div class="tool"></div>',
			showBtn : '<div class="sDown"></div>',
			icon : '<i></i>',
			optionCon : '<div class="subDiv penSubDiv"></div>',
			option : {
				overlay: '<div class="overlay"></div>',
				optionText : '<div class="item"></div>',
				sizeCon : '<div class="sizeDiv">',
				sizeOpt : '<div class="tt"><div></div></div>',
				colorCon : '<div class="colorDiv">',
				colorOpt : '<div class="cc"></div>'
			},
			closeBtn : '<div class="closeBtnDiv sUp"><i class="fal fa-angle-up"></i></div>'
		}
		const $toolCon = $(html.toolCon);
		$toolCon.attr("title", tool.title);
		
		let $showBtn = $(html.showBtn);
		let $optionCon = $(html.optionCon);
		let $icon = $(html.icon);
		$icon.addClass(icon);
		$showBtn.html($icon);
		$toolCon.append($showBtn);
		$toolCon.append($optionCon);
		
		/* 도구 아이콘 클릭시 이벤트 설정 */
		$toolCon.on("click", function(evt) {
			$(".subDiv").not($optionCon).slideUp(100);
			if ($optionCon.is(':visible')) {
				$optionCon.slideUp(100);
			}else{
				if($(this).hasClass("selected")) {
					$(this).addClass("selected");
					$optionCon.slideDown(100);
				}else{
					$(this).addClass("selected");
				}
			}
			tool.trigger("select");
		});
		
		/* 두께 옵션 설정 */
		if(sizeOption) {
			let $sizeCon = $(html.option.sizeCon);
			$sizeCon.addClass("z-100");
			let $sizeText = $(html.option.optionText);
			$sizeText.html("두께");
			$sizeCon.append($sizeText);
			
			let sizeValues = sizeOption.list;
			let clsNames = ["t1","t2","t3","t4","t5","t6"];
			
			for(let i = 0; i < sizeValues.length; i++) {
				let $sizeOpt = $(html.option.sizeOpt);
				if(i == sizeOption.def) {
					$sizeOpt.addClass("selected");
					tool.setStrokeWidth(sizeValues[i]);
				}
				$sizeOpt.addClass(clsNames[i]);
				$sizeCon.append($sizeOpt);
				
				$sizeOpt.on("click", function(evt) {
					evt.stopPropagation();
					$sizeCon.find("div").removeClass("selected");
					$(this).addClass("selected");
					tool.setStrokeWidth(sizeValues[i]);
				});
			}
			
			$optionCon.append($sizeCon);
		}
		
		/* 색상 옵션 설정 */
		if(colorOption) {
			let $colorCon = $(html.option.colorCon);
			let $colorText = $(html.option.optionText);
			$colorText.html("색상");
			$colorCon.append($colorText);
			let rgbValues = colorOption.list;
			let clsNames = ["cBlack","cRed","cBlue","cGreen","cYellow","cWhite"];
			
			for(let i = 0; i < rgbValues.length; i++) {
				let $colorOpt = $(html.option.colorOpt);
				if(i == colorOption.def) {
					$icon.addClass(clsNames[i]);
					tool.setStroke(rgbValues[i]);
				}
				$colorOpt.addClass(clsNames[i]);
				$colorCon.append($colorOpt);
				
				$colorOpt.on("click", function(evt) {
					evt.stopPropagation();
					$icon.removeClass(clsNames);
					$icon.addClass(clsNames[i]);
					tool.setStroke(rgbValues[i]);
				});
			}
			
			$optionCon.append($colorCon);
		}
		
		/* 닫기 버튼 클릭시 슬라이드 닫힘 */
		let $closeBtn = $(html.closeBtn);
		$closeBtn.addClass("z-100");
		$closeBtn.on("click", function(evt) {
			$(".subDiv").slideUp(100);
			evt.stopPropagation();
		});
		
		$optionCon.append($closeBtn);
		$optionCon.hide();
		tool.open = function(){
			$toolCon.addClass("selected");
			$optionCon.slideDown(100);
		}
		tool.close = function(){
			$optionCon.slideUp(100);
		}
		this.element = $toolCon;
		return $toolCon;
	},
	select(){
		const $element = this.element;
		if($element){
			$element.addClass("selected");
		}
	},
	unselect(){
		const $element = this.element;
		if($element){
			$element.removeClass("selected");
			$element.find(".subDiv")?.slideUp();
		}
	},
	pointerEnter(evt){
		evt.preventDefault();
		const tool = this;
		const style = tool.getStyleAttribute();
		const currentTarget = evt.currentTarget;
		const scale = $(currentTarget).attr("scale");
		currentTarget.style.cursor = "none";
		let cursorElem = currentTarget.closest('div').querySelector(".cursor");
		if(cursorElem){
			cursorElem.remove();
		}
		cursorElem = document.createElement("div");
		cursorElem.classList.add("cursor", "draw");
		
		const cursorSize = style["stroke-width"];
		cursorElem.style.width = cursorSize + "px";
		cursorElem.style.height = cursorSize + "px";
		cursorElem.style.visibility = "hidden";
		const cursorColor = style["stroke"];
		cursorElem.style.borderColor = cursorColor;
		
		currentTarget.closest('div').appendChild(cursorElem);
		$(currentTarget).unbind("pointermove pointerdown wheel")
		$(currentTarget).on("pointermove", drawCursorMove);
		$(currentTarget).on("pointerdown", function(event){
			tool.pointerDown(event);
		});
		function drawCursorMove(evt){
			evt.preventDefault();
			const pointerPos = Comm.getPointerPos(evt, 1, true);
			const bounding = cursorElem.getBoundingClientRect();
			const top = pointerPos.y - cursorSize / 2;
			const left = pointerPos.x - cursorSize / 2;
			cursorElem.style.top = top + "px";
			cursorElem.style.left = left + "px";
			cursorElem.style.visibility = "visible";
		}

		/*function drawCursorLeave(evt){
			evt.preventDefault();
			if(tool.pointerId != null){
				const leavePointerId = evt.pointerId;
				const leavePointerType = evt.pointerType;
				tool.pointerType = leavePointerType;
				tool.pointerId = leavePointerId;
			}
			cursorElem.remove();
			currentTarget.style.cursor = "default";
			$(currentTarget).trigger("pointerup");
			$(currentTarget).unbind("pointerleave pointermove pointerdown");
		}*/
		tool.cursorRemove = function(){
			cursorElem.remove();
		}
	},
	
	pointerDown(evt) {
		evt.preventDefault();
		if(evt.isPrimary != undefined && !evt.isPrimary) {
			return;
		}
		const tool = this;
		tool.close();
		const currentTarget = evt.currentTarget;
		const scale = $(currentTarget).attr("scale");
		const startPointerId = evt.pointerId;
		const startPointerType = evt.pointerType;
		if(startPointerType == "touch"){
			return;
		}
		const pointGroup = [];
		if(this.pointerType){
			switch(startPointerType) {
				case "mouse":
				case "pen":
					pointGroup.length = 0;
					tool.drawCancel();
					tool.pointerId = startPointerId;
					tool.pointerType = startPointerType;
					break;
				case "touch":
					return;
					break;
			}
		}else{
			tool.pointerId = startPointerId;
			tool.pointerType = startPointerType;
		}
		let lastPos = Comm.getPointerPos(evt, 1);
		pointGroup.push([lastPos.x, lastPos.y]);
		tool.drawStart([lastPos.x, lastPos.y]);
		$(currentTarget).on("pointermove", drawMove);
		$(currentTarget).on("pointerup", drawEnd);
		
		function drawMove(moveEvt){
			const elementFromPoint = document.elementFromPoint(moveEvt.pageX - window.pageXOffset, moveEvt.pageY - window.pageYOffset);
			if(elementFromPoint != currentTarget){
				drawEnd(moveEvt);
			}
			moveEvt.preventDefault();
			let movePointerId = moveEvt.pointerId;
			if(movePointerId != tool.pointerId){
				return;
			}
			let nowPos = Comm.getPointerPos(moveEvt, 1);
			let pointDistance = Comm.getDistance(lastPos, nowPos);
			let minDistance = (tool.strokeWidth / 2 < 5)?5:(tool.strokeWidth / 2);
			if(pointDistance < minDistance) {
				return;
			}
			pointGroup.push([nowPos.x, nowPos.y]);
			lastPos = nowPos;
			
			tool.drawMove([nowPos.x, nowPos.y], pointGroup.slice(pointGroup.length - 2, pointGroup.length), pointGroup);
		}
		
		function drawEnd(endEvt) {
			endEvt.preventDefault();
			$(currentTarget).unbind("pointerup", drawEnd);
			$(currentTarget).unbind("pointermove", drawMove);
			
			const endPointerId = endEvt.pointerId;
			const endPointerType = endEvt.pointerType;
			if(pointGroup.length > 1 && (!endPointerId || tool.pointerId == endPointerId)) {
				tool.drawEnd(pointGroup);
			}else{
				tool.drawCancel();
			}
			delete tool.pointerType;
			delete tool.pointerId;
		}
	},
	setName(name){
		this.name = name
	},
	setTitle(title){
		this.title = title
	}
};
export const penMixin = {
	__proto__: pathMixin,
	icon: "fas fa-pen",
	size: {
		list : [2, 4, 6, 8, 10, 12],
		def: 1,
	},
	color: {
		list: ["#000000", "#e90000", "#0076e3", "#00b100", "#ffba00", "#ffffff"],
		def: 0 
	},
	toContext(){
		return super.toContext();
	},
	select(){
		super.select();
	},
	unselect(){
		super.unselect();
	},
	enable(){
		super.enable();
	},
	disable(){
		super.disable();
	},
	show(){
		super.show();
	},
	hide(){
		super.hide();
	},
	pointerEnter(evt){
		super.pointerEnter(evt);
	},
	pointerDown(evt){
		super.pointerDown(evt);
	},
	createInterface(){
		return super.createInterface(this.icon, this.size, this.color);
	},
	setName(name){
		super.setName(name);
	},
	setTitle(title){
		super.setTitle(title);
	}
}
export const markerMixin = {
	__proto__: pathMixin,
	icon: "fas fa-highlighter",
	size: {
		list : [10, 25, 50, 75, 100],
		def: 1,
	},
	color: {
		list: ["#000000", "#ff0000", "#a1d9ff", "#a1ffa1", "#ffea8c"],
		def: 3 
	},
	toContext(){
		return super.toContext();
	},
	select(){
		super.select();
	},
	unselect(){
		super.unselect();
	},
	enable(){
		super.enable();
	},
	disable(){
		super.disable();
	},
	show(){
		super.show();
	},
	hide(){
		super.hide();
	},
	pointerEnter(evt){
		super.pointerEnter(evt);
	},
	pointerDown(evt){
		super.pointerDown(evt);
	},
	createInterface(){
		return super.createInterface(this.icon, this.size, this.color);
	},
	setName(name){
		super.setName(name);
	},
	setTitle(title){
		super.setTitle(title);
	}
}
export const fancyMixin = {
	__proto__: pathMixin,
	icon: "fas fa-pen-fancy",
	size: {
		list : [5, 10, 15, 20, 25],
		def: 0,
	},
	color: {
		list: ["#000000", "#ff0000", "#a1d9ff", "#a1ffa1", "#ffea8c"],
		def: 0 
	},
	toContext(){
		return super.toContext();
	},
	select(){
		super.select();
	},
	unselect(){
		super.unselect();
	},
	enable(){
		super.enable();
	},
	disable(){
		super.disable();
	},
	show(){
		super.show();
	},
	hide(){
		super.hide();
	},
	pointerEnter(evt){
		super.pointerEnter(evt);
	},
	pointerDown(evt){
		super.pointerDown(evt);
	},
	createInterface(){
		return super.createInterface(this.icon, this.size, this.color);
	},
	setName(name){
		super.setName(name);
	},
	setTitle(title){
		super.setTitle(title);
	}
}
