import {Comm} from '../../../comm/limePad.method.js';
import {toolMixin} from '../toolMixin.js';
export const eraserMixin = {
	__proto__: toolMixin,
	createInterface(){
		return super.createInterface();
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
		evt.preventDefault();
		const currentTarget = evt.currentTarget;
		
		const tool = this;
		const scale = $(currentTarget).attr("scale");
		currentTarget.style.cursor = "none";
		let cursorElem = currentTarget.closest('div').querySelector(".cursor");
		if(cursorElem){
			cursorElem.remove();
		}
		cursorElem = document.createElement("div");
		cursorElem.classList.add("cursor", "draw");
		cursorElem.style.visibility = "visible";
		
		cursorElem.style.width = 20 + "px";
		cursorElem.style.height = 20 + "px";
		cursorElem.style.borderColor = "#999999";
		
		currentTarget.closest('div').appendChild(cursorElem);
		$(currentTarget).unbind("pointermove pointerdown wheel");
		$(currentTarget).on("pointermove", drawCursorMove);
		$(currentTarget).on("pointerdown", function(event){
			tool.pointerDown(event, scale);
		});
		function drawCursorMove(evt){
			evt.preventDefault();
			const pointerPos = Comm.getPointerPos(evt, 1, true);
			const bounding = cursorElem.getBoundingClientRect();
			const top = pointerPos.y - 20 / 2;
			const left = pointerPos.x - 20 / 2;
			cursorElem.style.top = top + "px";
			cursorElem.style.left = left + "px";
		}
		tool.cursorRemove = function(){
			cursorElem.remove();
		}
	},
	pointerDown(evt, scale) {
		const tool = this;
		evt.preventDefault();
		if (evt.target.hasPointerCapture(evt.pointerId)) {
			//모바일에서의 터치 기본동작 해제. pointer-events:none 설정도 무시하기때문에 명시적으로 해제해줘야함.
			evt.target.releasePointerCapture(evt.pointerId);
		}
		if(evt.isPrimary != undefined && !evt.isPrimary) {
			return;
		}
		const startPointerType = evt.pointerType;
		if(startPointerType == "touch"){
			return;
		}
		const currentTarget = evt.currentTarget;
		const target = evt.target;
		let nowPos = Comm.getPointerPos(evt, 1, true);
		if(target != currentTarget){
			tool.erase([nowPos.x, nowPos.y], target);
		}
		$(currentTarget).on("pointermove", drawMove);
		$(currentTarget).on("pointerup", drawEnd);
		
		function drawMove(moveEvt){
			moveEvt.preventDefault();
			const target = moveEvt.target;
			nowPos = Comm.getPointerPos(moveEvt, 1, true);
			//if(target != currentTarget){
				tool.erase([nowPos.x, nowPos.y], target);
			//}
		}
		
		function drawEnd(endEvt) {
			endEvt.preventDefault();
			$(currentTarget).unbind("pointerup", drawEnd);
			$(currentTarget).unbind("pointermove", drawMove);
			
			tool.eraseEnd();
		}
	},
};
