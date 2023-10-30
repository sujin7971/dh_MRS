/**
 * 
 */
import BasicShape from '../BasicShape.js';
import {eventMixin} from '../../mixin/eventMixin.js';
export default class Textarea extends BasicShape{
	constructor(data = {}) {
		super({
			...data,
			type: "textarea",
		});
		const {
			text = "",
			color = "#000000",
			fontSize = 28,
			fontFamily = "sans-serif",
		} = data;
		this.text = text;
		this.color = color;
		this.fontSize = fontSize;
		this.fontFamily = fontFamily;
	}
	update(data = {}){
		super.update(data);
		const {
			text = this.text,
			color = this.color,
			fontSize = this.fontSize,
			fontFamily = this.fontFamily,
		} = data;
		this.text = text;
		this.color = color;
		this.fontSize = fontSize;
		this.fontFamily = fontFamily;
	}
	getFontAttribute(){
		return {
			"color": this.color,
			"font-size": this.fontSize,
			"font-family": this.fontFamily,
		}
	}
	getText(){
		return this.text;
	}
	setText(text){
		this.text = text;
	}
	setFontColor(color){
		this.color = color;
	}
	setFontSize(size){
		this.fontSize = size;
	}
	setFontFamily(fontFamily){
		this.fontFamily = fontFamily;
	}
	toObject(){
		return {
			...super.getProperties(),
			text: this.text,
			color: this.color,
			fontSize: this.fontSize,
			fontFamily: this.fontFamily,
		}
	}
	static builder(data = {}){
		return new Path(data);
	}
}
Object.assign(Textarea.prototype, eventMixin);