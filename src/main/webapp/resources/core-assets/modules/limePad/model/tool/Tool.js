/**
 * 
 */
import {eventMixin} from '../mixin/eventMixin.js';
export class Tool {
	constructor(data = {}) {
		const {
			type = undefined,
			name = "",
			title = "",
			icon = "fas fa-tools"
		} = data
		
		this.type = type;
		this.name = name;
		this.title = title;
		this.icon = icon;
	}
	setType(type){
		this.type = type;
	}
}
Object.assign(Tool.prototype, eventMixin);

export class EraserTool extends Tool {
	constructor(data = {}) {
		super({
			...data,
			type: "eraser",
		})
	}
}

export class ControlTool extends Tool {
	constructor(data = {}) {
		super({
			...data,
			type: "control",
		})
	}
}

export class ZoomTool extends Tool {
	constructor(data = {}) {
		super({
			...data,
			type: "zoom",
		})
		const {
			zoom = "",
		} = data;
		this.zoom = zoom;
	}
}

export class FitTool extends Tool {
	constructor(data = {}) {
		super({
			...data,
			type: "fit",
		})
		const {
			fit = "vertical"
		} = data;
		this.fit = fit;
	}
}
