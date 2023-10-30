import { Util, Dom, eventMixin } from '/resources/core-assets/essential_index.js';
/**
 * Form요소 제어 관련 업무를 돕는 클래스입니다.
 * 
 * @author mckim
 * @version 1.0
 * @since 2023. 06. 01
 * @see 이 모듈 또는 클래스의 코드는 함부로 수정하거나 변경해서는 안 됩니다. 만약 이 모듈이나 클래스에 기능을 추가하거나 수정이 필요하다면, 개발1팀에 문의하시기 바랍니다.
 */
class FormHelper {
	/**
	 * 생성자에서 form들을 보관할 객체를 초기화합니다.
	 * @param triggerEvents 이는 FormHelper가 트리거하게 될 이벤트의 종류를 설정합니다.
	 * 이 옵션을 통해, 특정 form 요소에서 발생하는 이벤트들 중, FormHelper가 재전파할 이벤트를 선별할 수 있습니다.
	 * 기본적으로, FormHelper는 등록된 요소에서 "change"와 "click" 이벤트가 발생했을 때, 이를 재전파하도록 설정되어 있습니다.
	 * ["input"] 이 주어지는 경우, 모든 form요소에서 'change','click','input'이벤트가 발생할 때 마다 FormHelper가 동일 이벤트를 트리거합니다.
	 * @param nonSubmitNames getFormData 메서드가 FormData를 생성할때 제외할 요소를 요소 name 을 통해 설정합니다. getFormValues와 같은 단순한 값 조회 등은 정상적으로 적용됩니다.
	 * ["name1", "name2"] 이 주어지는 겨우, FormData생성시 등록된 Form 요소중 이름이 "name1", "name2"인 Form은 제외됩니다.
	 * @param valueProcessor name요소의 값을 가져올때 지정된 함수를 실행하여 반환된 값으로 설정합니다.
	 * 예시:
	 * {
	 *  name: (originalValue) => {
	 * 		...
	 * 		return modifiedValue;
	 * 	}
	 * }
	 * @param valueMutator 설정할 값에 대해 변형 함수를 실행하여 반환된 값 또는 프로미스 해결 값으로 설정합니다.
	 * 프로미스를 사용하는 경우, 이 함수는 프로미스가 완료될 때까지 기다립니다.
	 * 예시:
	 * {
	 *  name: async (originalValue) => {
	 *    ...
	 *    return await modifiedValuePromise;
	 *  }
	 * }
	 *
	 * 'modifiedValuePromise'는 값을 변경하는 프로미스입니다.
	 */
	constructor({ triggerEvents = [], nonSubmitNames = [], valueProcessor = {}, valueMutator = {} } = {}) {
		this.clearForm();
		this.triggerEvents = triggerEvents;
		this.nonSubmitNames = nonSubmitNames;
		this.valueProcessor = valueProcessor;
		this.valueMutator = valueMutator;
	}
	clearForm() {
		this.forms = {};
		this.buttons = {};
	}
	/**
	 * 특정 양식 내의 모든 요소들을 클래스에 추가하는 메서드입니다.
	 * 해당 양식에 value가 설정되어있는 경우, 해당 값을 기본값으로 하여 추가합니다.
	 * 양식 등록 후, autoRegisterButtons를 자동으로 호출합니다.
	 */
	addFormElements(formSelector) {
		const form = Dom.getElement(formSelector);
		if (!form) {
			console.error('Form not found:', formSelector);
			return;
		}
		let formElements;
		if (form instanceof HTMLFormElement) {
			formElements = Array.from(form.elements);
		} else {
			formElements = form.querySelectorAll('input, textarea, select, button');
		}
		formElements.forEach(element => {
			if (element.tagName === 'BUTTON') {
				this.addButtonForm(element);
			} else {
				this.addSubmitForm(element);
			}
		});
		return this;
	}
	/**
	 * 선택한 양식을 클래스에 추가하는 메서드입니다.
	 * 양식의 기본값도 설정합니다.
	 */
	addSubmitForm(selector) {
		const element = Dom.getElement(selector);
		if (element && element.name) {
			const eventCallback = async (event) => {
				const name = element.name;
				const form = this.getForm(name);
				await this.trigger(event.type, event, { 
					name: name, 
					element: element, 
					type: element.type, 
					value: element.value, 
					form: form
				});
			}
			if (!this.forms[element.name]) {
				this.forms[element.name] = {
					type: element.type,
					elements: [],
				};
			}
			this.forms[element.name].elements.push(element);
			const nowValue = ((formElement) => {
				const type = formElement.type;
				switch(type){
					case 'radio':
					case 'checkbox':
						return (formElement.checked)?formElement.value:null;
					case 'select-one':
						return Array.from(element.selectedOptions, opt => opt.value).pop();
					case 'select-multiple':
						return Array.from(element.selectedOptions, opt => opt.value);
					default:
						return formElement.value;
				}
			})(element);
			if(this.forms[element.name].defaultValue){
				const defaultValue = this.forms[element.name].defaultValue;
				this.forms[element.name].defaultValue = [].concat(defaultValue).concat(nowValue);
			}else{
				this.forms[element.name].defaultValue = nowValue;
			}
			["change", "click"].concat(this.triggerEvents).forEach(async type => {
				element.removeEventListener(type, eventCallback);
				element.addEventListener(type, eventCallback);
			});
		}
		return this;
	}
	/**
	 * 선택한 버튼을 클래스에 추가하는 메서드입니다.
	 */
	addButtonForm(buttonSelector) {
		const button = Dom.getElement(buttonSelector);
		if (button && button.name && button.tagName === 'BUTTON') {
			const eventCallback = (event) => {
				this.trigger('click', event, { name: button.name, element: button, type: button.type });
			}
			button.removeEventListener('click', eventCallback);
			button.addEventListener('click', eventCallback);
			this.buttons[button.name] = button;
		}
		return this;
	}
	/**
	 * 특정 양식 내의 모든 요소들을 클래스에서 제거하는 메서드입니다.
	 */
	removeFormElements(formSelector) {
		const form = Dom.getElement(formSelector);
		if (!form) {
			console.error('Form not found:', formSelector);
			return;
		}
		let formElements;
		if (form instanceof HTMLFormElement) {
			formElements = form.elements;
		} else {
			formElements = form.querySelectorAll('input, textarea, select, button');
		}
		formElements.forEach(element => {
			if (element.tagName === 'BUTTON') {
				this.removeButtonForm(element);
			} else {
				this.removeSubmitForm(element);
			}
		});
		return this;
	}
	/**
	 * 선택할 양식을 클래스에서 제거하는 메서드입니다.
	 */
	removeSubmitForm(selector) {
		const element = Dom.getElement(selector);
		if (element && element.name) {
			if (this.forms[element.name]) {
				delete this.forms[element.name];
			}
		}
		return this;
	}
	/**
	 * 특정 양식 내의 모든 버튼을 자동으로 검색하거나 초기화 버튼으로 등록하는 메서드입니다.
	 */
	removeButtonForm(buttonSelector) {
		const button = Dom.getElement(buttonSelector);
		if (button && button.name && button.tagName === 'BUTTON') {
			delete this.buttons[button.name];
		}
		return this;
	}
	// 클래스에 추가된 모든 양식의 현재 값을 가져오는 메서드입니다.
	getFormValues(passNonSubmitValues) {
		const values = {};
		for (let name in this.forms) {
			if (passNonSubmitValues && this.nonSubmitNames.includes(name)) {
				continue;
			}
			values[name] = this.getForm(name).getValue();
		}
		return values;
	}
	getFormData() {
		const formData = new FormData();
		const formValues = this.getFormValues();
		for (let name in formValues) {
			if (this.nonSubmitNames.includes(name)) {
				continue;
			}
			const value = formValues[name];
			if (value !== null) {
				if (Array.isArray(value)) {
					// If the value is an array, append each item individually
					value.forEach(item => formData.append(name, item));
				} else {
					formData.append(name, value);
				}
			}
		}
		return formData;
	}
	// 특정 이름의 양식을 가져와 그 값을 설정하는 메서드를 반환합니다.
	getForm(name) {
		const forms = this.forms[name];
		if (!forms) {
			return null;
		}
		const { elements, defaultValue, type } = forms;
		const getValue = () => {
			if (!elements) {
				return null;
			}
			let values = (() => {
				switch (type) {
					case 'radio':
					case 'checkbox':
						return elements.filter(element => element.checked).map(element => element.value);
					case 'select-one':
						return Array.from(elements[0].selectedOptions, opt => opt.value);
					case 'select-multiple':
						return Array.from(elements[0].selectedOptions, opt => opt.value);
					default:
						return [elements[0].value];
				}
			})(elements);
			values = (values.length > 1)?values:values.pop();
			if (typeof this.valueProcessor[name] === 'function') {
				values = this.valueProcessor[name](values);
			}
			return values;
		}
		const setValue = async (value, events = ['change']) => {
			if (typeof this.valueMutator[name] === 'function') {
				value = await this.valueMutator[name](value);
			}
			const targetElements = [];
			switch (type) {
				case 'radio':
				case 'checkbox':{
					if(Util.isEmpty(value)){
						elements.forEach(element => {
							element.checked = false;
							targetElements.push(element);
						});
					}else{
						elements.forEach(element => {
							element.checked = element.value === value;
							if(element.checked){
								targetElements.push(element);
							}
						});
					}
				}
					break;
				case 'select-one':
				case 'select-multiple':{
					for (let option of elements[0].options) {
						if (option.value === value) {
							option.selected = true;
						} else {
							option.selected = false;
						}
					}
					targetElements.push(elements[0]);
				}
					break;
				default:{
					elements[0].value = value;
					targetElements.push(elements[0]);
				}
			}
			events.forEach(async eventType => {
				const event = new Event(eventType);
				targetElements.forEach(targetElement => targetElement.dispatchEvent(event));
			});
		}
		const setDefault = (value, events = ['change']) => {
			forms.defaultValue = value;
			setValue(value);
		}
		const getElement = () => {
			switch(type){
				case "radio":
				case "checkbox":{
					return elements;
				}
				default: 
					return elements[0];
			}
		}
		const setAttribute = (attr, value) => {
			for (let element of elements) {
				element.setAttribute(attr, value);
			}
		}
		const removeAttribute = (attr) => {
			for (let element of elements) {
				element.removeAttribute(attr);
			}
		}
		const setDisabled = (isDisabled) => {
			for (let element of elements) {
				element.disabled = isDisabled;
			}
			if (isDisabled) {
				this.nonSubmitNames.push(name);
			} else {
				this.nonSubmitNames = this.nonSubmitNames.filter(nonName => nonName != name);
			}
		}
		let firstElement = elements[0];
		let formMethods = {
			setValue,
			setDefault,
			getValue,
			getElement,
			setAttribute,
			removeAttribute,
			setDisabled,
		};
		// If the first element is a SELECT, add the getSelectedText method
		if (firstElement.tagName === 'SELECT') {
			formMethods.getSelectedText = () => {
				return (firstElement.selectedIndex != -1) ? firstElement.options[firstElement.selectedIndex].text : null;
			}
			formMethods.isEmpty = () => {
				return (Util.isEmpty(firstElement.options)) ? true : false;
			}
			formMethods.getOptionValues = () => {
				return Array.from(firstElement.options).map(option => option.value).filter(value => !Util.isEmpty(value));
			}
		}
		return formMethods;
	}
	getForms(...names) {
		return names.map(name => this.getForm(name));
	}
	getButton(name) {
		return this.buttons[name];
	}
	setValues(values, events = ['change']) {
		for (let name in values) {
			this.getForm(name)?.setValue(values[name], events);
		}
	}
	setDefaultValues(values, events = ['change']) {
		for (let name in values) {
			this.getForm(name)?.setDefault(values[name], events);
		}
	}
	setAttribute(name, attr, value) {
		this.getForm(name)?.setAttribute(attr, value);
	}
	removeAttribute(name, attr) {
		this.getForm(name)?.removeAttribute(attr);
	}
	getValue(name) {
		return this.getForm(name)?.getValue();
	}
	setDisabled = (name, isDisabled) => {
		this.getForm(name)?.setDisabled(isDisabled);
	}
	// 클래스에 추가된 모든 양식의 값을 초기값으로 재설정하는 메서드입니다.
	reset(events = ['change']) {
		for (let name in this.forms) {
			const { elements, defaultValue } = this.forms[name];
			this.getForm(name)?.setValue(defaultValue, events);
		}
	}
}
Object.assign(FormHelper.prototype, eventMixin);
export default FormHelper;