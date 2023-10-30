/**
 * 스케줄 및 회의정보의 submit할 input을 관리할 모듈.
 *
 * @author mckim
 */
import {eventMixin, Util} from '/resources/core-assets/essential_index.js';

/**
 * 관리할 파라미터 모음
 * name: 파라미터 명
 * submit: FORM에 담아 전송할 파라미터 여부(true: 전송, false: 비전송)
 * required: FORM에 반드시 포함되어야 할 파라미터 여부(true: 필수, false: 선택)
 * readOnly: 사용자가 수정 가능한 파라미터 여부(true: 수정 불가, false: 수정 가능)
 * input: 사용자에게 표시할 INPUT ELEMENT
 * size: 값 제한(text: 길이, number: 크기)
 */
const paramCollection = [
	{
		name: "skdKey",
		input: Util.getElement("#skdKeyInput"),
		submit: true,
		required: true,
		readOnly: true,
	},
	{
		name: "meetingKey",
		input: Util.getElement("#meetingKeyInput"),
		submit: true,
		required: true,
		readOnly: true,
	},
	{
		name: "writerKey",
		input: Util.getElement("#writerKeyInput"),
		submit: true,
		required: true,
		readOnly: true,
	},
	{
		name: "writer",
		input: Util.getElement("#writerInput"),
		submit: false,
		required: false,
		readOnly: true,
	},
	{
		name: "writerTel",
		input: Util.getElement("#writerTelInput"),
		submit: false,
		required: false,
		readOnly: true,
	},
	{
		name: "skdStatus",
		input: Util.getElement("#skdStatusInput"),
		submit: false,
		required: false,
		readOnly: true,
	},
	{
		name: "skdComment",
		input: Util.getElement("#commentInput"),
		submit: false,
		required: false,
		readOnly: true,
	},
	{
		name: "officeCode",
		input: Util.getElement("#officeSelect"),
		submit: true,
		required: true,
		readOnly: true,
	},
	{
		name: "roomType",
		input: Util.getElement("#roomTypeSelect"),
		submit: true,
		required: true,
		readOnly: true,
	},
	{
		name: "roomKey",
		input: Util.getElement("#roomKeyInput"),
		submit: true,
		required: true,
		readOnly: true,
	},
	{
		name: "roomName",
		input: Util.getElement("#roomInput"),
		submit: false,
		required: false,
		readOnly: true,
	},
	{
		name: "holdingDate",
		input: Util.getElement("#holdingDateInput"),
		submit: false,
		nullable: false,
		required: false,
		readOnly: false,
	},
	{
		name: "endDate",
		input: Util.getElement("#endDateInput"),
		submit: false,
		nullable: false,
		required: false,
		readOnly: false,
	},
	{
		name: "beginTime",
		input: Util.getElement("#beginTimeInput"),
		submit: false,
		nullable: false,
		required: false,
		readOnly: false,
	},
	{
		name: "finishTime",
		input: Util.getElement("#finishTimeInput"),
		submit: false,
		nullable: false,
		required: false,
		readOnly: false,
	},
	{
		name: "beginDateTime",
		input: Util.getElement("#beginDateTimeInput"),
		submit: true,
		required: false,
		readOnly: true,
	},
	{
		name: "finishDateTime",
		input: Util.getElement("#finishDateTimeInput"),
		submit: true,
		required: false,
		readOnly: true,
	},
	{
		name: "title",
		input: Util.getElement("#titleInput"),
		submit: true,
		nullable: false,
		required: false,
		readOnly: false,
		size: 30,
	},
	{
		name: "skdHost",
		input: Util.getElement("#skdHostInput"),
		submit: true,
		nullable: false,
		required: false,
		readOnly: false,
		size: 10,
	},
	{
		name: "attendeeCnt",
		input: Util.getElement("#attendeeCntInput"),
		submit: true,
		nullable: true,
		required: false,
		size: 100,
	},
	{
		name: "contents",
		input: Util.getElement("#contentsInput"),
		submit: true,
		nullable: true,
		required: false,
		readOnly: false,
		size: 100,
	},
	{
		name: "elecYN",
		input: Util.getElement("#switchElec"),
		submit: true,
		required: false,
		readOnly: false,
	},
	{
		name: "messengerYN",
		input: Util.getElement("#switchMessenger"),
		submit: true,
		required: false,
		readOnly: false,
	},
	/*{
		name: "mailYN",
		input: Util.getElement("#switchMail"),
		submit: true,
		required: false,
		readOnly: false,
	},*/
	{
		name: "smsYN",
		input: Util.getElement("#switchSms"),
		submit: true,
		required: false,
		readOnly: false,
	},
	{
		name: "secretYN",
		input: Util.getElement("#switchSecret"),
		submit: true,
		required: false,
		readOnly: false,
	},
	{
		name: "hostSecuLvl",
		input: [Util.getElement("#switchHostSecuLvl"), Util.getElement("#switchHostDeptSecuLvl")],
		submit: true,
		required: false,
		readOnly: false,
	},
	{
		name: "attendeeSecuLvl",
		input: [Util.getElement("#switchAttendeeSecuLvl"), Util.getElement("#switchAttendeeDeptSecuLvl")],
		submit: true,
		required: false,
		readOnly: false,
	},
	/*{
		name: "observerSecuLvl",
		input: [Util.getElement("#switchObserverSecuLvl"), Util.getElement("#switchObserverDeptSecuLvl")],
		submit: true,
		required: false,
		readOnly: false,
	},*/
];
const setInputFix = (...$inputList) => {
	$inputList.forEach($input => {
		if($input){
			if($input.type == "text"){
				$input.readOnly = true;
			}else{
				$input.disabled = true;
			}
		}
	});
}
const generateForm = (inputList) => {
	if(inputList.length == 0){
		return null;
	}
	const $form = Util.createElement("form");
	inputList.forEach($input => {
		$form.appendChild($input);
	});
	return $form;
}
export default {
	__proto__: eventMixin,
	init(data = {}){
		const {
			editable = true,
		} = data;
		paramCollection.forEach(param => {
			param.generateHidden = () => {
				const $hidden = Util.createElement("input");
				$hidden.setAttribute("name", param.name);
				$hidden.setAttribute("type", "hidden");
				$hidden.setAttribute("value", param.get());
				console.log("generateHidden", param.name, param.get());
				return $hidden;
			}
			this[Util.camelize("update "+param.name)] = (options) => {
				Object.assign(param, options);
			}
			if(!editable || param.readOnly){
				const $input = param.input;
				if(!$input){
					return;
				}
				if(Array.isArray($input)){
					setInputFix(...$input);
				}else{
					setInputFix($input);
				}
			}
			this.setInput(param);
		});
	},
	validation(){
		//return paramCollection.filter(param => param.required).reduce((bool, param) => (bool && (param.get()?true:false))?true:false, true);
		return paramCollection.filter(param => param.submit).filter(param => !param.isValid());
	},
	setInput(param){
		const $input = param.input;
		if($input){
			let inputType = param.input.type;
			if(!inputType && param.input.length == 2){
				inputType = "multi-checkbox";
			}
			switch(inputType){
				case "checkbox":
					this.setCheckboxInput(param);
					break;
				case "multi-checkbox":
					this.setMultiCheckboxInput(param);
					break;
				case "select-one":
					this.setSelectInput(param);
					break;
				case "textarea":
				case "text":
					this.setTextInput(param);
					break;
				case "hidden":
					this.setHiddenInput(param);
					break;
				default :
					this.setSpanInput(param);
					break;
			}
		}
	},
	setSelectInput(param){
		const $input = param.input;
		const setLabel = () => {
			const inputId = $input.getAttribute("id");
			const label = Util.getElement("label[for="+inputId+"]");
			label.innerHTML = $input.options[$input.selectedIndex].text
		}
		this[Util.camelize("enable "+param.name)] = (value) => {
			$input.disabled = false;
			param.submit = true;
		}
		this[Util.camelize("disable "+param.name)] = (value) => {
			$input.disabled = true;
			param.submit = false;
		}
		this[Util.camelize("init "+param.name)] = (value) => {
			$input.value = value;
			param.initial = value;
			setLabel();
		}
		this[Util.camelize("set "+param.name)] = (value) => {
			$input.value = value;
			setLabel();
		}
		param.get = this[Util.camelize("get "+param.name)] = () => {
			return $input.value;
		}
		param.isChanged = () => {
			return param.get() != param.initial;
		}
		param.isValid = () => {
			return true;
		}
		$input.onchange = () => {
			setLabel();
		}
	},
	setCheckboxInput(param){
		const $input = param.input;
		this[Util.camelize("enable "+param.name)] = (value) => {
			$input.disabled = false;
			param.submit = true;
		}
		this[Util.camelize("disable "+param.name)] = (value) => {
			$input.disabled = true;
			param.submit = false;
		}
		this[Util.camelize("init "+param.name)] = (switchYN) => {
			const bool = (switchYN == 'Y')?true:false;
			$input.checked = bool;
			param.initial = switchYN;
			this.trigger("checkboxChange", param);
		}
		this[Util.camelize("switch "+param.name)] = (switchYN) => {
			const bool = (switchYN == 'Y')?true:false;
			$input.checked = bool;
			this.trigger("checkboxChange", param);
		}
		param.get = this[Util.camelize("get "+param.name)] = () => {
			return ($input.checked == true)?'Y':'N';
		}
		if(param.onchange){
			$input.onchange = param.onchange;
		}
		param.isChanged = () => {
			return param.get() != param.initial;
		}
		param.isValid = () => {
			return true;
		}
		$input.onchange = () => {
			this.trigger("checkboxChange", param);
		}
	},
	setMultiCheckboxInput(param){
		const $inputP = param.input[0];
		const $inputC = param.input[1];
		if(!$inputP || !$inputC){
			return;
		}
		this[Util.camelize("init "+param.name)] = (lvl) => {
			switch(lvl){
				case 1:
					$inputP.checked = false;
					$inputC.checked = false;
					param.initial = 1;
					break;
				case 2:
					$inputP.checked = true;
					$inputC.checked = false;
					param.initial = 2;
					break;
				case 3:
					$inputP.checked = true;
					$inputC.checked = true;
					param.initial = 3;
					break;
			}
		}
		this[Util.camelize("set "+param.name)] = (lvl) => {
			switch(lvl){
				case 1:
					$inputP.checked = false;
					$inputC.checked = false;
					break;
				case 2:
					$inputP.checked = true;
					$inputC.checked = false;
					break;
				case 3:
					$inputP.checked = true;
					$inputC.checked = true;
					break;
			}
		}
		param.get = this[Util.camelize("get "+param.name)] = () => {
			let lvl = 1;
			if($inputP.checked){
				lvl += 1;
			}
			if($inputC.checked){
				lvl += 1;
			}
			return lvl;
		}
		param.isChanged = () => {
			return param.get() != param.initial;
		}
		param.isValid = () => {
			return true;
		}
	},
	setTextInput(param){
		const $input = param.input;
		param.initial = "";
		this[Util.camelize("enable "+param.name)] = (value) => {
			$input.readOnly = false;
			$input.disabled = false;
			param.submit = true;
		}
		this[Util.camelize("disable "+param.name)] = (value) => {
			$input.readOnly = true;
			$input.disabled = true;
			$input.onclick = null;
			param.submit = false;
		}
		this[Util.camelize("writable "+param.name)] = (value) => {
			$input.readOnly = value;
		}
		this[Util.camelize("init "+param.name)] = (value) => {
			if(Util.isEmpty(value)){
				return;
			}
			$input.value = Util.unescape(value);
			param.initial = value;
			this.trigger("textInput", param);
		}
		this[Util.camelize("set "+param.name)] = (value) => {
			if(Util.isEmpty(value)){
				return;
			}
			$input.value = Util.unescape(value);
			this.trigger("textInput", param);
		}
		this[Util.camelize("clear "+param.name)] = () => {
			$input.value = "";
			this.trigger("textInput", param);
		}
		param.get= this[Util.camelize("get "+param.name)] = () => {
			return $input.value;
		}
		param.isChanged = () => {
			return param.get() != param.initial;
		}
		param.isValid = () => {
			if(param.nullable == false && Util.isEmpty(param.get())){
				return false;
			}else{
				return true;
			}
		}
		$input.oninput = () => {
			this.trigger("textInput", param);
		}
		$input.onchange = () => {
			this.trigger("textInput", param);
		}
		//$input.value = param.value;
	},
	setSpanInput(param){
		const $input = param.input;
		param.initial = "";
		this[Util.camelize("init "+param.name)] = (value) => {
			$input.innerHTML = value;
			param.initial = value;
		}
		this[Util.camelize("set "+param.name)] = (value) => {
			$input.innerHTML = value;
		}
		param.get = this[Util.camelize("get "+param.name)] = () => {
			return $input.innerHTML;
		}
		param.isChanged = () => {
			return $input.innerHTML != param.innerHTML;
		}
		param.isValid = () => {
			return true;
		}
		//$input.value = param.value;
	},
	setHiddenInput(param){
		const $input = param.input;
		this[Util.camelize("init "+param.name)] = (value) => {
			$input.value = value;
		}
		this[Util.camelize("set "+param.name)] = (value) => {
			$input.value = value;
		}
		param.get = this[Util.camelize("get "+param.name)] = () => {
			return $input.value;
		}
		param.isChanged = () => {
			return ($input.value)?true:false;
		}
		param.isValid = () => {
			return true;
		}
	},
	getForm(){
		const allParamList = paramCollection.filter(param => param.submit && param.isValid());
		const $inputList = allParamList.map(param => param.generateHidden());
		return generateForm($inputList);
	},
	getChangedForm(){
		const changedParamList = paramCollection.filter(param => param.submit && (param.required || param.isChanged()));
		const $inputList = changedParamList.map(param => param.generateHidden());
		return generateForm($inputList);
	},
}