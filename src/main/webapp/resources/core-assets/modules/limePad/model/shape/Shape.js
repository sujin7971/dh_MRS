/**
 * 
 */
export default class Shape {
	constructor(data = {}) {
		const {
			type = undefined,
			posX = 0,
			posY = 0,
			width = 0,
			height = 0,
			
			stroke = "#000000",
			strokeOpacity = 1,
			strokeWidth = 2,
			strokeDasharray = null,
			strokeLinejoin = "round",
			strokeLinecap = "round",
			fill = "none",
			fillOpacity = 0,
			
			level = 1,
		} = data
		this.type = type;
		
		this.posX = posX;
		this.posY = posY;
		this.width = width;
		this.height = height;
		
		this.stroke = stroke;
		this.strokeOpacity = strokeOpacity;
		this.strokeWidth = strokeWidth;
		this.strokeDasharray = strokeDasharray;
		this.strokeLinejoin = strokeLinejoin;
		this.strokeLinecap = strokeLinecap;
		
		this.fill = fill;
		this.fillOpacity = fillOpacity;
		
		this.level = level;
	}
	update(data = {}){
		const {
			type = this.type,
			
			posX = this.posX,
			posY = this.posY,
			width = this.width,
			height = this.height,
			
			stroke = this.stroke,
			strokeOpacity = this.strokeOpacity,
			strokeWidth = this.strokeWidth,
			strokeDasharray = this.strokeDasharray,
			fill = this.fill,
			fillOpacity = this.fillOpacity,
			
			level = this.level,
		} = data
		this.type = type;
		this.posX = posX;
		this.posY = posY;
		this.width = width;
		this.height = height;
		
		this.stroke = stroke;
		this.strokeOpacity = strokeOpacity;
		this.strokeWidth = strokeWidth;
		this.strokeDasharray = strokeDasharray;
		
		this.level = level;
		
		this.trigger("update");
	}
	getDependencyAttribute(){
		return {
			type: this.type,
		}
	}
	getStyleAttribute(){
		return {
			stroke: this.stroke,
			"stroke-opacity": this.strokeOpacity,
			"stroke-width": this.strokeWidth,
			"stroke-dasharray": this.strokeDasharray,
			'stroke-linejoin': this.strokeLinejoin,
			'stroke-linecap': this.strokeLinecap,
			"fill": this.fill,
			"fill-opacity": this.fillOpacity,
		}
	}
	getCoordinateAttribute(){
		return {
			posX: this.posX,
			posY: this.posY,
			width: this.width,
			height: this.height,
		}
	}
	getProperties(){
		return {
			type: this.type,
			...this.getCoordinateAttribute(),
			stroke: this.stroke,
			strokeOpacity: this.strokeOpacity,
			strokeWidth: this.strokeWidth,
			strokeDasharray: this.strokeDasharray,
			strokeLinejoin: this.strokeLinejoin,
			strokeLinecap: this.strokeLinecap,
			fill: this.fill,
			fillOpacity: this.fillOpacity,
		}
	}
	setStroke(color){
		this.stroke = color;
	}
	setStrokeWidth(width){
		this.strokeWidth = width;
	}
	setStrokeOpacity(opacity){
		this.strokeOpacity = opacity;
	}
	setStrokeDasharray(dasharray){
		this.strokeDasharray = dasharray;
	}
	setFill(color){
		this.fill = color;
	}
	setFillOpacity(opacity){
		this.fillOpacity = opacity;
	}
	getLevel(){
		return this.level;
	}
}

function getUUID(length) {
	if(!length){
		length = 16;
	}
	let uuid = ([1e7]+-1e3+-4e3+-8e3+-1e11).replace(/[018]/g, c =>
		(c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
	)
	return uuid.substring(0, length);
}
