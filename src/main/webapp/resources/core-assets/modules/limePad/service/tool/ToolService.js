/**
 * 
 */
import {PathShape, BasicShape} from '../../model/tool/index.js';
import {Tool, EraserTool, ControlTool, ZoomTool, FitTool, HighlightTool} from '../../model/tool/index.js';
import {Pen, Marker, Fancy, Square, Textbox, Eraser} from '../../model/tool/index.js';
import {Laser, Pointer, ZoomOut, ZoomIn, FitVertical, FitHorizontal} from '../../model/tool/index.js';
export default class ToolService{
	constructor(data = {}){
		const {
			unuse = [Fancy, Square]
		} = data;
		const allCollection = [Pen, Marker, Fancy, Square, Textbox, Eraser, Laser, Pointer, ZoomOut, ZoomIn, FitVertical, FitHorizontal];
		const banCollection = unuse;
	
		const toolCollection = allCollection.filter(t => !banCollection.includes(t));
		
		this.toolCollection = toolCollection.map(t => copy(t));
		this.pathToolCollection = this.toolCollection.filter(t => t instanceof PathShape);
		this.basicToolCollection = this.toolCollection.filter(t => t instanceof BasicShape);
		this.eraserToolCollection = this.toolCollection.filter(t => t instanceof EraserTool);
		this.controlToolCollection = this.toolCollection.filter(t => t instanceof ControlTool);
		this.resizeToolCollection = this.toolCollection.filter(t => t instanceof ZoomTool || t instanceof FitTool);
		const _class = this;
		this.pathToolCollection.forEach(t => t.on("select", function(){
			_class.pathToolCollection.filter(t => t != this).forEach(t => t.unselect());
			_class.basicToolCollection.forEach(t => t.unselect());
			_class.eraserToolCollection.forEach(t => t.unselect());
			_class.controlToolCollection.forEach(t => t.unselect());
			t.select();
		}));
		this.basicToolCollection.forEach(t => t.on("select", function(){
			_class.pathToolCollection.forEach(t => t.unselect());
			_class.basicToolCollection.filter(t => t != this).forEach(t => t.unselect());
			_class.eraserToolCollection.forEach(t => t.unselect());
			_class.controlToolCollection.forEach(t => t.unselect());
			t.select();
		}));
		this.eraserToolCollection.forEach(t => t.on("select", function(){
			_class.pathToolCollection.forEach(t => t.unselect());
			_class.basicToolCollection.forEach(t => t.unselect());
			_class.eraserToolCollection.filter(t => t != this).forEach(t => t.unselect());
			_class.controlToolCollection.forEach(t => t.unselect());
			t.select();
		}));
		this.controlToolCollection.forEach(t => t.on("select", function(){
			_class.pathToolCollection.forEach(t => t.unselect());
			_class.basicToolCollection.forEach(t => t.unselect());
			_class.eraserToolCollection.forEach(t => t.unselect());
			_class.controlToolCollection.filter(t => t != this).forEach(t => t.unselect());
			t.select();
		}));
		this.resizeToolCollection.forEach(t => t.on("select", function(){
			_class.resizeToolCollection.filter(t => t != this).forEach(t => t.unselect());
			t.select();
		}));
		
		this.interfaceCollection = this.toolCollection.map(t => t.createInterface());
	}
	selectTool(name){
		this.unselectAll();
		const tool = this.toolCollection.find(t => t.name == name);
		if(tool){
			tool.trigger("select");
			return tool;
		}else{
			return Pen;
		}
	}
	unselectAll(){
		this.toolCollection.forEach(t => t.unselect());
	}
	getTool(name){
		return this.toolCollection.find(t => t.name == name);
	}
	getAllTool(){
		return this.toolCollection;
	}
	getPathTool(){
		return this.pathToolCollection;
	}
	getBasicTool(){
		return this.basicToolCollection;
	}
	getEraserTool(){
		return this.eraserToolCollection;
	}
	getControlTool(){
		return this.controlToolCollection;
	}
	getResizeTool(){
		return this.resizeToolCollection;
	}
	getToolInterface(){
		return this.interfaceCollection;
	}
}
function copy(instance){
	const clone = Object.assign(Object.create(Object.getPrototypeOf(instance)), instance);
	return clone;
}
