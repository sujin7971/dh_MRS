/**
 * 
 */
import Shape from './Shape.js';
export default class PathShape extends Shape{
	constructor(data = {}) {
		super(data);
		const {
			pointGroup = [[[0, 0]]],
		} = data;
		this.pointGroup = pointGroup;
	}
	update(data = {}){
		super.update(data);
		const {
			pointGroup = this.pointGroup,
		} = data;
		this.pointGroup = pointGroup;
	}
	getCoordinateAttribute(){
		return {
			...super.getCoordinateAttribute(),
			pointGroup: this.pointGroup
		}
	}
	getProperties(){
		return {
			...super.getProperties(),
			pointGroup: this.pointGroup
		}
	}
}
