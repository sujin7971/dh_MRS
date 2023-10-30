import {Util} from '/resources/core-assets/essential_index.js';
import {Textarea} from '../shape/index.js';
export class Graphic{
	constructor(data = {}) {
		const {
			id = null,
			shape = null,
			packedElem = null,
			selected = false,
			visible = true,
			level = 1,
		} = data;
		this.id = id;
		this.shape = shape;
		this.packedElem = packedElem;
		this.selected = selected;
		this.visible = visible;
		this.level = level;
	}
	select(){
		this.selected = true;
	}
	deselect(){
		this.selected = false;
	}
	isSelected(){
		return this.selected;
	}
	show(){
		this.packedElem.removeAttribute("visibility");
		this.visible = true;
	}
	hide(){
		this.packedElem.setAttribute("visibility","hidden");
		this.visible = false;
	}
	isVisible(){
		return this.visible;
	}
	setLevel(level){
		this.level = level;
	}
	getLevel(){
		return this.level;
	}
	destroy(){
		delete this.shape;
		this.packedElem?.remove();
		delete this.packedElem;
	}
	getShape(){
		return this.shape;
	}
	getPackedElem(){
		return this.packedElem;
	}
	getCoordElem(){
		return this.packedElem.coordNode;
	}
	getGraphicElem(){
		return this.packedElem.graphicNode;
	}
	getForeignElem(){
		return this.packedElem.foreignNode;
	}
	getEditTool(){
		if(this.shape instanceof Textarea){
			
		}
	}
	toContext(){
		return {
			id: this.id,
			level: this.level,
			shape: this.shape.toObject()
		}
	}
}
/* 구현 보류 */
const generateEditTool = {
	buildTemplate(data = {}){
		const {
			paleteTool = false,
			fontTool = false,
			delTool = true,
		} = data;
		const $editBox = Util.createElement("div");
		$editBox.id = "editTool";
		const $headerBox = Util.createElement("div", "editToolHeader");
		$editBox.appendChild($headerBox);
		const $toolBox = Util.createElement("div", "editToolProperty", "subDiv");
		$toolBox.style.display = "none";
		$editBox.appendChild($toolBox);
		if(paleteTool){
			const $paleteIcon = Util.createElement("i", "fas", "fa-palette");
			$headerBox.appendChild($paleteIcon);
			const $paleteTool = Util.createElement("colorDiv")
		}
		if(fontTool){
			const $fontIcon = Util.createElement("i", "fas", "fa-text-size");
			$headerBox.appendChild($fontIcon);
		}
		if(delTool){
			const $delIcon = Util.createElement("i", "fas", "fa-trash-alt");
			$headerBox.appendChild($delIcon);
		}
	},
	textTool(){
		if(!this.textTool){
			this.textTool = this.buildTemplate({
				paleteTool: true,
				fontTool: true,
				delTool: true,
			});
		}
		return this.textTool;
	},	
}
