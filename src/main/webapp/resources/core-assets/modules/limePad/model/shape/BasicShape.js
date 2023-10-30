/**
 * 
 */
import Shape from './Shape.js';
export default class BasicShape extends Shape{
	constructor(data = {}) {
		super(data);
		const {
			startPos = [0, 0],
			endPos = [0, 0],
		} = data;
		this.startPos = startPos;
		this.endPos = endPos;
	}
	update(data = {}){
		super.update(data);
		const {
			startPos = this.startPos,
			endPos = this.endPos,
		} = data;
		
		this.startPos = startPos;
		this.endPos = endPos;
	}
	getStyleAttribute(){
		return {
			"stroke": this.stroke,
			"stroke-opacity": this.strokeOpacity,
			"stroke-width": this.strokeWidth,
			"stroke-dasharray": this.strokeDasharray,
			"fill": this.fill,
			"fill-opacity": this.fillOpacity,
		}
	}
	getCoordinateAttribute(){
		return {
			...super.getCoordinateAttribute(),
			startPos: this.startPos,
			endPos: this.endPos,
			pointGroup: [this.startPos, this.endPos]
		}
	}
}