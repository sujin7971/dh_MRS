/**
 * 
 */
import PathShape from '../PathShape.js';
import {eventMixin} from '../../mixin/eventMixin.js';
export default class Path extends PathShape{
	constructor(data = {}) {
		super({
			...data,
			type: "path",
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
Object.assign(Path.prototype, eventMixin);
