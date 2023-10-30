/**
 * 
 */
import PathShape from '../PathShape.js';
import {eventMixin} from '../../mixin/eventMixin.js';
export default class PolyLine extends PathShape{
	constructor(data = {}) {
		super({
			...data,
			type: "polyline",
		});
		const {
			
		} = data;
	}
	update(data = {}){
		super.update(data);
	}
	toObject(){
		return super.getProperties();
	}
}
Object.assign(PolyLine.prototype, eventMixin);
