import {eventMixin, Util, Dom} from '/resources/core-assets/essential_index.js';
/**
 * 
 */
class FormElement {
	constrctor(selector){
		if (Util.isEmpty(selector)) {
			throw new Error("유효한 Element가 아닙니다.");
		}
		const element = Dom.getElement(selector);
		if (!element) {
			throw new Error("유효한 Element가 아닙니다.");
		}
		this.type = element.type;
		this.name = element.name;
		this.element = element;
		this.defaultValue = this.getValue();
	}
	getValue(){
		const element = this.element;
		switch (this.type) {
			case 'radio':
			case 'checkbox':
				return (element.checked)?element.value:null;
			case 'select-one':
				return Array.from(elements[0].selectedOptions, opt => opt.value)?.pop();
			case 'select-multiple':
				return Array.from(elements[0].selectedOptions, opt => opt.value);
			default:
				return element.value;
		}
	}
	setValue(value){
		const element = this.element;
		switch (this.type) {
			case 'radio':
			case 'checkbox':
				if(value instanceof boolean){
					element.checked = value;
				}else if(value){
					element.checked = element.value === value;
				}else{
					element.checked = false;
				}
				break;
			case 'select-one':
			case 'select-multiple':
				for (let option of element.options) {
					if (option.value === value) {
						option.selected = true;
					} else {
						option.selected = false;
					}
				}
				break;
			default:
				return element.value = value;
		}
	}
	setDefaultValue(value){
		this.setValue(value);
		this.defaultValue = value;
	}
	getElement(){
		return this.element;
	}
	setDisabled(isDisabled){
		this.element.disabled = isDisabled;
	}
	reset(){
		this.setValue(...this.defaultValue);
	}
}

class TextFormElement extends FormElement{
	constrctor(selector){
		if (Util.isEmpty(selector)) {
			throw new Error("유효한 Element가 아닙니다.");
		}
		const element = Dom.getElement(selector);
		if (!element) {
			throw new Error("유효한 Element가 아닙니다.");
		}
		this.type = element.type;
		this.name = element.name;
		this.element = element;
		this.defaultValue = this.getValue();
	}
	getValue(){
		return element.value;
	}
	setValue(value){
		element.value = value;
	}
	setDefaultValue(value){
		this.setValue(value);
	}
	getElement(){
		return this.element;
	}
	setDisabled(isDisabled){
		this.element.disabled = isDisabled;
	}
}

class ChoiceFormElement extends FormElement {
	constrctor(elements){
		if (Util.isEmpty(elements)) {
			throw new Error("유효한 Element가 아닙니다.");
		}
		this.type = elements[0].type;
		this.name = elements[0].name;
		this.elements = elements;
		this.defaultValue = this.getValues();
	}
	getValues(){
		return elements.filter(element => element.checked).map(element => element.value);
	}
	getValue(){
		const values = this.getValues();
		return (values.length > 1)?values:values.pop();
	}
	setValue(...values){
		for(const value of values){
			this.elements.forEach(element => {
				if(typeof value === 'boolean'){
					element.checked = value;
				}else if(value){
					element.checked = element.value === value;
				}else{
					element.checked = false;
				}
			});
		}
	}
	setDefaultValue(...values){
		this.setValue(...values);
		this.defaultValue = values;
	}
	getElement(){
		return (this.elements.length > 1)?this.elements:this.elements[0];
	}
	setDisabled(flag){
		this.elements.forEach(element => element.disabled = flag);
	}
	getCheckedElements(){
		return this.elements.filter(element => element.checked);
	}
	getUnCheckedElements(){
		return this.elements.filter(element => !element.checked);
	}
}

class SelectFormElement extends FormElement{
	constrctor(selector){
		if (Util.isEmpty(selector)) {
			throw new Error("유효한 Element가 아닙니다.");
		}
		const element = Dom.getElement(selector);
		if (!element) {
			throw new Error("유효한 Element가 아닙니다.");
		}
		this.type = element.type;
		this.name = element.name;
		this.element = element;
		this.defaultValue = this.getValue();
	}
	getValue(){
		const values = Array.from(this.element.selectedOptions, opt => opt.value);
		if(this.type == 'select-one'){
			return values.pop();
		}else{
			return values;
		}
	}
	setValue(...values){
		for (let option of element.options) {
			if (values.includes(option.value)) {
				option.selected = true;
			} else {
				option.selected = false;
			}
		}
	}
	setDefaultValue(...values){
		this.setValue(...values);
		this.defaultValue = values;
	}
	getElement(){
		return this.element;
	}
	setDisabled(isDisabled){
		this.element.disabled = isDisabled;
	}
	getSelectedText(){
		
	}
}


