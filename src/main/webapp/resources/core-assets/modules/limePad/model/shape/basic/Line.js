/**
 * 
 */
import BasicShape from '../BasicShape.js';
import {eventMixin} from '../../mixin/eventMixin.js';
export default class Line extends BasicShape{
	constructor(data = {}) {
		super({
			...data,
			type: "line",
		});
	}
	update(data = {}){
		super.update(data);
	}
	toObject(){
		return super.getProperties();
	}
}
Object.assign(Line.prototype, eventMixin);