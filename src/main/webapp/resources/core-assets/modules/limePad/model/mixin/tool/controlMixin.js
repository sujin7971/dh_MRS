import {Comm} from '../../../comm/limePad.method.js';
import {toolMixin} from '../toolMixin.js';
export const controlMixin = {
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
		//console.log("pointerEnter", evt);
		const tool = this;
		evt.preventDefault();
		const currentTarget = evt.currentTarget;
		const scale = $(currentTarget).attr("scale");
		$(currentTarget).unbind("wheel");
		$(currentTarget).css("cursor","grab");
		
		const controlServ = new ControlService({
			tool: tool,
			scale: scale
		});
		$(currentTarget).on("pointerdown", function(evt){
			evt.stopPropagation();
			evt.preventDefault();
			$(currentTarget).css("cursor","grabbing");
			controlServ.controlStart(evt);
		});
		$(currentTarget).on("pointermove", function(evt){
			evt.stopPropagation();
			evt.preventDefault();
			$(currentTarget).css("cursor","grab");
			controlServ.controlMove(evt);
		});
		$(currentTarget).on("pointerup", function(evt){
			evt.stopPropagation();
			evt.preventDefault();
			$(currentTarget).css("cursor","grabbing");
			controlServ.controlEnd(evt);
		});
		/*
		$(currentTarget).on("wheel", function(evt){
			const timeStamp = evt.timeStamp;
			if (evt.originalEvent.wheelDelta > 0 || evt.originalEvent.detail < 0) {
				controlServ.scroll("top", timeStamp);
			}else{
				controlServ.scroll("bottom", timeStamp);
			}
		});
		*/
	},
};
class ControlService{
	constructor(data = {}){
		const {
			tool,
			scale = 1
		} = data;
		this.tool = tool;
		this.evCache = [];
		this.lastPos = [];
		this.totalMove = 0;
		this.grab = false;
	}
	controlStart(evt, tool){
		//console.log("controlStart", evt)
		this.prevDiff = -1;
		
		this.startTime = evt.timeStamp;
		const currentTarget = evt.currentTarget;
		
		const startPos = Comm.getPointerPos(evt, 1);
		this.totalMove = 0;
		this.lastPos = startPos;
		this.evCache.push(evt);
		//console.log("controlStart", this.evCache.map(e => e.pointerId));
		this.tool.controlStart?.(evt, startPos);
	}
	controlMove(evt, tool){
		//console.log("controlMove", evt)
		evt.preventDefault();
		evt.stopPropagation();
		const evCache = this.evCache;
		if(evCache.length == 0){
			return;
		}
		for (let i = 0; i < evCache.length; i++) {
		    if (evt.pointerId === evCache[i].pointerId) {
			    evCache[i] = evt;
			    break;
		    }
		}
		//console.log("evCache", evCache);
		if (evCache.length === 2) {
			   // Calculate the distance between the two pointers
			const curDiff = Math.abs(evCache[0].clientX - evCache[1].clientX);
			const prevDiff = this.prevDiff;
			if (prevDiff > 0) {
		    	if (curDiff > prevDiff) {
		    		this.tool.zoom("in", curDiff - prevDiff);
		    		// The distance between the two pointers has increased
		    	}
		    	if (curDiff < prevDiff) {
		    		// The distance between the two pointers has decreased
		    		this.tool.zoom("out", prevDiff - curDiff);
		    	}
			}
			// Cache the distance for the next move event
			this.prevDiff = curDiff;
		}else{
			//console.log("controlMove", "evCache", evCache, evCache.length)
			const lastPos = this.lastPos;
			const endPos = Comm.getPointerPos(evt, 1);
			if(!lastPos) {
				this.lastPos = endPos;
				return;
			}
			let distance = Comm.getDistance(lastPos, endPos);
			if(distance <= 0){
				return;
			}
			this.totalMove += distance;
			this.tool.controlMove?.(evt, lastPos, endPos, distance);
			this.direction = (lastPos.y - endPos.y > 0)?"bottom":"top";
			this.lastPos = null;
		}
	}
	controlEnd(evt, tool) {
		evt.preventDefault();
		evt.stopPropagation();
		const endPointerId = evt.pointerId;
		//console.log("controlEnd", "id", endPointerId);
		if(!endPointerId){
			return;
		}
		remove_event(evt, this.evCache);
		if (this.evCache.length < 2) {
		    this.prevDiff = -1;
		}
		this.lastPos = null;
		//const currentTarget = evt.currentTarget;
		//$(currentTarget).unbind("pointerup", controlEnd);
		//$(currentTarget).unbind("pointermove", controlMove);
		
		const endPos = Comm.getPointerPos(evt, 1);
		const endTime = evt.timeStamp;
		const duration = Math.floor(endTime - this.startTime);
		this.tool.controlEnd?.(endPos, this.totalMove, this.direction, duration);
	}
	scroll(direction, timeStamp){
		this.tool.scroll(direction, Comm.roundToTwo(timeStamp));
	}
}
function remove_event(ev, evCache) {
	//console.log("remove_event", ev);
	 // Remove this event from the target's cache
	 for (let i = 0; i < evCache.length; i++) {
	 	if (evCache[i].pointerId === ev.pointerId) {
	 		evCache.splice(i, 1);
	 		break;
		}
	}
	// console.log("remove_event", evCache);
}

export const highlightMixin = {
	__proto__: controlMixin,
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
		const tool = this;
		evt.preventDefault();
		const currentTarget = evt.currentTarget;
		$(currentTarget).css("cursor", "default");
		$(currentTarget).unbind("click wheel pointermove pointerdown");
		$(currentTarget).on("pointerdown", function(evt){
			evt.stopPropagation();
			evt.preventDefault();
			tool.pointerDown(evt);
		});
	},
	pointerDown(downEvt) {
		const tool = this;
		const startTime = downEvt.timeStamp;
		const currentTarget = downEvt.currentTarget;
		currentTarget.setPointerCapture(downEvt.pointerId);
		
		const startPos = Comm.getPointerPos(downEvt, 1);
		let lastPos = startPos;
		$(currentTarget).on("pointermove", controlMove);
		$(currentTarget).on("pointerup", controlEnd);
		tool.laserStart?.(startPos);
		function controlMove(moveEvt){
			moveEvt.preventDefault();
			moveEvt.stopPropagation();
			const nowPos = Comm.getPointerPos(moveEvt, 1);
			const distance = Comm.getDistance(lastPos, nowPos);
			if(distance <= 1){
				return;
			}
			tool.laserMove?.(nowPos);
			lastPos = nowPos;
		}
		
		function controlEnd(upEvt) {
			currentTarget.releasePointerCapture(downEvt.pointerId);
			upEvt.preventDefault();
			upEvt.stopPropagation();
			$(currentTarget).unbind("pointerup", controlEnd);
			$(currentTarget).unbind("pointermove", controlMove);
			
			tool.laserEnd?.();
		}
	},
	createInterface(){
		return super.createInterface();
	},
}
