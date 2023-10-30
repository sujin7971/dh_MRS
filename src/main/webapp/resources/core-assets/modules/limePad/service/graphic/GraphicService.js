/**
 * 
 */
import {eventMixin} from '../../model/mixin/eventMixin.js';
import {GraphicFactory} from '../../model/graphic/GraphicFactory.js';
import Path from '../../model/shape/path/Path.js';
import Polyline from '../../model/shape/path/Polyline.js';
import Line from '../../model/shape/basic/Line.js';
import Textarea from '../../model/shape/basic/Textarea.js';
import Rectangle from '../../model/shape/basic/Rectangle.js';
export default class GraphicService{
	constructor(data = {}) {
		const {
			instance = null,
		} = data;
		this.instance = instance;
		this.empty();
	}
	empty(){
		this.instance.svgNode.clearGraphic();
		this._display?.map(graphic => graphic.destroy());
		this._display = [];
		this._garbage?.map(graphic => graphic.destroy());
		this._garbage = [];
	}
	isEmpty(){
		return (this._display.length == 0)?true:false;
	}
	createShapeModel(type, data){
		let shape;
		switch(type){
			case "line":
				shape = new Line(data);
				break;
			case "rectangle":
				shape = new Rectangle(data);
				break;
			case "textarea":
				shape = new Textarea(data);
				break;
			case "path":
				shape = new Path(data);
				break;
			case "polyline":
				shape = new Polyline(data);
				break;
		}
		return shape;
	}
	createUpdateModel(id, attributes = {}){
		return {
			id: id,
			action: "U",
			shape: {
				...attributes
			}
		}
	}
	createDeleteModel(id){
		return {
			id: id,
			action: "D",
		}
	}
	create(context){
		const graphic = GraphicFactory.molding(context);
		return graphic;
	}
	insert(graphic){
		if(this.get(graphic.id)){
			return graphic;
		}
		this._display.push(graphic);
		this.instance.svgNode.querySelector("#"+graphic.id)?.remove();
		this.instance.svgNode.addGraphic({element: graphic.getPackedElem(), level: graphic.getLevel()});
		return graphic;
	}
	load(data){
		const context = {
			...data
		}
		const action = context.action;
		switch(action){
			case "N": {
					context.shape = this.createShapeModel(context.shape.type, context.shape);
					const graphic = GraphicFactory.molding(context);
					this.insert(graphic);
				}
				break;
			case "U": {
					const graphic = this.get(context.id);
					graphic?.shape?.update(context.shape);
				}
				break;
			case "D": {
				this.remove(context.id);
				}
				break;
		}
	}
	remove(id){
		const graphic = this.get(id);
		if(graphic){
			this._display = this._display.filter(graphic => graphic.id !== id);
			this._garbage.push(graphic);
			graphic.hide();
		}
		return graphic;
	}
	runGarbageCollector(){
		const filtered = this._display.filter(g => !g.isVisible() || !g.packedElem);
		this._garbage = this._garbage.concat(filtered);
		this._garbage.forEach(graphic => graphic.destroy());
		this._garbage = [];
	}
	get(id){
		return this._display.find(graphic => graphic.id == id);
	}
	getAll() {
	    return this._display;
	}
	select(id) {
		this._display.forEach(graphic => graphic.deselect());
		const graphic = this.get(id);
		graphic?.select();
		return graphic;
	}
	getSelected() {
	    const graphic = this._display.find(graphic => graphic.isSelected());
	    return graphic;
	}
	restore(snapshot){
		this.empty();
		const contextList = JSON.parse(snapshot);
		contextList.forEach(context => {
			context.shape = this.createShapeModel(context.shape.type, context.shape)
		})
		contextList.forEach(context => {
			const graphic = this.create(context);
			this.insert(graphic);
		});
	}
}
Object.assign(GraphicService.prototype, eventMixin);