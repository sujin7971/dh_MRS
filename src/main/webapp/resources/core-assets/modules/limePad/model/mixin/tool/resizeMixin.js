import {Comm} from '../../../comm/limePad.method.js';
import {toolMixin} from '../toolMixin.js';

export const zoomMixin = {
	__proto__: toolMixin,
	createInterface(){
		return super.createInterface();
	},
	select(){
		
	},
	unselect(){
		
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
}

export const fitMixin = {
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
}
