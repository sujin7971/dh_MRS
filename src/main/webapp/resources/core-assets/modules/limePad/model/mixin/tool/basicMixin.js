import {Comm} from '../../../comm/limePad.method.js';
import {toolMixin} from '../toolMixin.js';
export const basicMixin = {
	__proto__: toolMixin,	
	toContext(){
		const context = {
			id: "G"+Comm.getUUID(6),
			action: "N",
			shape: new this.constructor(this)
		}
		return context;
	},
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
		const tool = this;
		evt.preventDefault();
		const currentTarget = evt.currentTarget;
		const scale = $(currentTarget).attr("scale");
		$(currentTarget).css("cursor", "crosshair");
		
		$(currentTarget).unbind("pointermove pointerdown wheel")
		$(currentTarget).on("pointerdown", function(event){
			tool.pointerDown(event);
		});
	},
	pointerDown(evt) {
		evt.preventDefault();
		if(evt.isPrimary != undefined && !evt.isPrimary) {
			return;
		}
		const tool = this;
		const currentTarget = evt.currentTarget;
		const scale = $(currentTarget).attr("scale");
		const beginPos = Comm.getPointerPos(evt, 1);
		tool.drawStart?.(beginPos);
		$(currentTarget).unbind("pointerdown", drawEnd);
		$(currentTarget).on("pointermove", drawMove);
		$(currentTarget).on("pointerup", drawEnd);
		
		function drawMove(evt){
			evt.preventDefault();
			const nowPos = Comm.getPointerPos(evt, 1);
			tool.drawMove?.(beginPos, nowPos);
		}
		
		function drawEnd(evt) {
			evt.preventDefault();
			$(currentTarget).unbind("pointerup", drawEnd);
			$(currentTarget).unbind("pointermove", drawMove);
			
			const nowPos = Comm.getPointerPos(evt, 1);
			tool.drawEnd?.(nowPos);
		}
	},
	setName(name){
		this.name = name
	},
	setTitle(title){
		this.title = title
	}
};
export const textareaMixin = {
	__proto__: basicMixin,
	icon: "far fa-text",
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
		return super.createInterface(this.icon);
	},
	setName(name){
		super.setName(name);
	},
	setTitle(title){
		super.setTitle(title);
	}
}
export const rectangleMixin = {
	__proto__: basicMixin,
	icon: "far fa-square-full",
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
		return super.createInterface(this.icon);
	},
	setName(name){
		super.setName(name);
	},
	setTitle(title){
		super.setTitle(title);
	}
}
