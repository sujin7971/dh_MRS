/**
 * 
 */
import BasicShape from '../BasicShape.js';
import {eventMixin} from '../../mixin/eventMixin.js';
export default class Rectangle extends BasicShape{
	constructor(data = {}) {
		super({
			...data,
			type: "rect",
		});
	}
	update(data = {}){
		super.update(data);
	}
	getFontAttribute(){
		return {
			"color": this.color,
			"font-size": this.fontSize,
			"font-family": this.fontFamily,
		}
	}
	toObject(){
		return super.getProperties();
	}
}
Object.assign(Rectangle.prototype, eventMixin);