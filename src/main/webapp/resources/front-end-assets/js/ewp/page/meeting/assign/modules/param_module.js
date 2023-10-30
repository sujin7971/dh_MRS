/**
 * 배정신청과 관련된 모든 input 데이터를 관리할 모듈(스케줄/회의 정보, 참석자)
 * 
 * 스케줄 및 회의정보는 inputHandler, 참석자 정보는 attendeehandler 가 담당
 * 
 * @author mckim
 */
import {Util, Modal} from '/resources/core-assets/essential_index.js';
import {Final, FileManager} from '/resources/front-end-assets/js/ewp/comm/module_index.js';
import {inputHandler, attendeeHandler} from '../module_index.js';
import {FileCall as $FILE} from '/resources/front-end-assets/js/ewp/comm/ajax_index.js';

export default {
	async init(options = {}){
		this.initInputHandler(options);
		this.initFileHandler(options);
	},
	initInputHandler(options = {}){
		const {
			editable = true
		} = options;
		this.editable = editable;
		inputHandler.init({
			editable: editable,
		})
		if(editable){
			timeHandler.init({
				openTime: OPEN_TIME,
				closeTime: CLOSE_TIME,
				interval: INTERVAL_MINUTE,
			});
		}
		inputHandler.on("textInput", (param) => {
			const name = param.name;
			if(name == "holdingDate" || name == "beginTime" || name == "finishTime"){
				Util.removeHangulInput(param.input);
				return;
			}
			if(name == "attendeeCnt"){
				Util.acceptNumber(param.input);
				return;
			}
			if(name == "skdHost"){
				Util.acceptWithinLength(param.input);
			}
			const $input = param.input;
			const limit = param.size;
			const $lengthInput = Util.getElement("#"+name+"Length");
			if(limit && $lengthInput){
				$lengthInput.innerHTML = $input.value.length + "/" + limit;
			}
		});
		inputHandler.on("checkboxChange", (param) => {
			const name = param.name;
			const checked = param.input.checked;
			switch(name){
				case "elecYN":
					if(checked == true){
						$(".scheduleWriteDiv").addClass("elect");
						inputHandler.writableAttendeeCnt(true);
					}else{
						$(".scheduleWriteDiv").removeClass("elect");
						inputHandler.writableAttendeeCnt(false);
					}
					break;
				case "secretYN":
					if(checked == true){
						$(".secuMoff").hide();
				        $(".secuMon").show();
					}else{
						$(".secuMoff").show();
				        $(".secuMon").hide();
					}
					break;
			}
		});
	},
	getInputHandler(){
		return inputHandler;
	},
	initAttendeeHandler(options = {}){
		const {
			officeCode = '1000',
			editable = true,
		} = options;
		attendeeHandler.init({
			officeCode: officeCode,
			editable: editable
		});
		attendeeHandler.on("change", (cnt) => {
			inputHandler.setAttendeeCnt(cnt);
		});
	},
	initFileHandler(options = {}){
		const {
			editable = true,
			deletable = true
		} = options;
		FileManager.init({
			editable: editable,
			deletable: deletable,
		});
	},
	initHiddenParam(param){
		if(param == null){
			return;
		}
		inputHandler.initSkdKey?.(param.skdKey);
		inputHandler.initMeetingKey?.(param.meetingKey);
	},
	initStatusParam(param = {}){
		const {
			appStatus = "REQUEST",
			appComment = "",
		} = param;
		const $skdStatus = document.querySelector(".statusRow");
		switch(appStatus){
			case "REQUEST":
				Util.addClass($skdStatus, "s0");
				break;
			case "APPROVED":
				Util.addClass($skdStatus, "s1");
				//inputHandler.disableHoldingDate?.();
				//inputHandler.disableBeginTime?.();
				//inputHandler.disableFinishTime?.();
				//inputHandler.disableElecYN?.();
				break;
			case "CANCELED":
				Util.addClass($skdStatus, "s4");
				break;
			case "REJECTED":
				Util.addClass($skdStatus, "s5");
				break;
		}
		inputHandler.initSkdComment?.(appComment);
	},
	initRoomParam(param){
		if(param == null){
			return;
		}
		inputHandler.initOfficeCode(param.officeCode);
		inputHandler.initRoomType(param.roomType);
		inputHandler.initRoomKey?.(param.roomKey);
		inputHandler.initRoomName(param.roomName);
		
		this.getRoomParam = () => {
			return {
				officeCode: inputHandler.getOfficeCode(),
				roomType: inputHandler.getRoomType(),
				roomKey: inputHandler.getRoomKey(),
			}
		}
	},
	initScheduleParam(param = {}){
		let {
			holdingDate,
			beginTime,
			finishTime,
			beginDateTime,
			finishDateTime,
			skdHost,
			attendeeCnt,
		} = param;
		beginTime = (beginDateTime)?moment(beginDateTime).format('HH:mm'):beginTime;
		finishTime = (finishDateTime)?moment(finishDateTime).format('HH:mm'):finishTime;
		if(holdingDate){
			inputHandler.initHoldingDate(holdingDate);
			inputHandler.initEndDate?.(holdingDate);
		}
		if(beginTime){
			inputHandler.initBeginTime(beginTime);
		}
		if(finishTime){
			inputHandler.initFinishTime(finishTime);
		}
		
		const beginDT = moment(holdingDate+'T'+beginTime, 'YYYY-MM-DDTHH:mm:ss');
		const finishDT = moment(holdingDate+'T'+finishTime, 'YYYY-MM-DDTHH:mm:ss');
		inputHandler.initBeginDateTime?.(beginDT.format("YYYY-MM-DDTHH:mm:ss"));
		inputHandler.initFinishDateTime?.(finishDT.format("YYYY-MM-DDTHH:mm:ss"));
		
		inputHandler.initSkdHost(skdHost);
		inputHandler.initAttendeeCnt(attendeeCnt);
		if(this.editable){
			timeHandler.setTimeLimit();
		}
	},
	setScheduleParam(param = {}){
		let {
			holdingDate = inputHandler.getHoldingDate(),
			beginTime,
			finishTime,
			beginDateTime,
			finishDateTime,
			skdHost,
			attendeeCnt,
		} = param;
		
		beginTime = (beginDateTime)?moment(beginDateTime).format('HH:mm'):beginTime;
		finishTime = (finishDateTime)?moment(finishDateTime).format('HH:mm'):finishTime;
		if(holdingDate){
			inputHandler.setHoldingDate(holdingDate);
			inputHandler.setEndDate?.(holdingDate);
		}
		if(beginTime){
			inputHandler.setBeginTime(beginTime);
		}
		if(finishTime){
			inputHandler.setFinishTime(finishTime);
		}
		
		const beginDT = moment(holdingDate+'T'+beginTime, 'YYYY-MM-DDTHH:mm:ss');
		const finishDT = moment(holdingDate+'T'+finishTime, 'YYYY-MM-DDTHH:mm:ss');
		inputHandler.setBeginDateTime?.(beginDT.format("YYYY-MM-DDTHH:mm:ss"));
		inputHandler.setFinishDateTime?.(finishDT.format("YYYY-MM-DDTHH:mm:ss"));
		
		inputHandler.setSkdHost(skdHost);
		inputHandler.setAttendeeCnt(attendeeCnt);
		if(this.editable){
			timeHandler.setTimeLimit();
		}
	},
	setDateTimeInput(data= {}){
		const {
			holdingDate = inputHandler.getHoldingDate(),
		} = data;
		inputHandler.setHoldingDate(holdingDate);
		timeHandler.setDateTimeInput();
	},
	initMeetingParam(param = {}){
		const {
			title,
			contents,
			elecYN,
			messengerYN,
			mailYN,
			smsYN,
			secretYN,
			hostSecuLvl,
			attendeeSecuLvl,
			observerSecuLvl
		} = param;
		inputHandler.initTitle(title);
		inputHandler.initContents(contents);
		inputHandler.initElecYN?.(elecYN);
		inputHandler.initMessengerYN?.(messengerYN);
		inputHandler.initMailYN?.(mailYN);
		inputHandler.initSmsYN?.(smsYN);
		inputHandler.initSecretYN?.(secretYN);
		inputHandler.initHostSecuLvl?.(hostSecuLvl);
		inputHandler.initAttendeeSecuLvl?.(attendeeSecuLvl);
		inputHandler.initObserverSecuLvl?.(observerSecuLvl);
	},
	setMeetingParam(param){
		const {
			title = inputHandler.getTitle(),
			contents = inputHandler.getContents(),
			elecYN = inputHandler.getElecYN(),
			messengerYN = inputHandler.getMessengerYN(),
			mailYN = inputHandler.getMailYN(),
			smsYN = inputHandler.getSmsYN(),
			secretYN = inputHandler.getSecretYN(),
			hostSecuLvl = inputHandler.getHostSecuLvl(),
			attendeeSecuLvl = inputHandler.getAttendeeSecuLvl(),
			observerSecuLvl = inputHandler.getObserverSecuLvl()
		} = param;
		inputHandler.setTitle(param.title);
		inputHandler.setContents(param.contents);
		inputHandler.switchElecYN?.(param.elecYN);
		inputHandler.switchMessengerYN?.(param.messengerYN);
		inputHandler.switchMailYN?.(param.mailYN);
		inputHandler.switchSmsYN?.(param.smsYN);
		inputHandler.switchSecretYN?.(param.secretYN);
		inputHandler.setHostSecuLvl?.(param.hostSecuLvl);
		inputHandler.setAttendeeSecuLvl?.(param.attendeeSecuLvl);
		inputHandler.setObserverSecuLvl?.(param.observerSecuLvl);
	},
	initWriterParam(param){
		if(param == null){
			return;
		}
		inputHandler.initWriter(param.nameplate);
		inputHandler.initWriterTel(param.officeDeskPhone);
	},
	initAttendeeParam(data){
		attendeeHandler.initAttendeeList(data);
	},
	setAttendeeParam(data){
		attendeeHandler.setAttendeeList(data);
	},
	clearAttendeeParam(){
		attendeeHandler.clearAttendeeList();
	},
	setFiles(files){
		FileManager.setFiles(files);
	},
	getAddedFiles(){
		if(inputHandler.getElecYN() == 'Y'){
			return FileManager.getAddedFiles();
		}else{
			return null;
		}
	},
	getDeletedFiles(){
		if(inputHandler.getElecYN() == 'Y'){
			return FileManager.getDeletedFiles();
		}else{
			return null;
		}
	},
	getInputParam(){
		return inputHandler.getForm();
	},
	getChangedInputParam(){
		return inputHandler.getChangedForm();
	},
	getAttendeeParam(){
		if(inputHandler.getElecYN() == 'Y'){
			return attendeeHandler.getChangeResult();
		}else{
			return null;
		}
	},
	validation(){
		const inputValidationResult = inputHandler.validation();
		const result = {
			valid: true,	
		}
		if(inputValidationResult.length != 0){
			result.valid = false;
			const invalidParam = inputValidationResult[0];
			switch(invalidParam.name){
				case "title":
					result.msg = "제목을 입력해 주세요.";
					break;
				case "skdHost":
					result.msg = "주관자를 입력해 주세요.";
					break;
			}
		}
		if(inputHandler.getElecYN() == 'Y'){
			const attendeeValidationResult = attendeeHandler.validation();
			if(!attendeeValidationResult){
				result.valid = false;
				result.msg = "회의 진행자를 선택해 주세요.";
			}
		}
		return result;
	}
}

const timeHandler = {
	init(data = {}){
		const {
			openTime,
			closeTime,
			interval,
		} = data;
		this.openTime = openTime;
		this.closeTime = closeTime;
		this.minTime = openTime;
		this.maxTime = closeTime;
		
		this.interval = interval;
		this.$holdingDate = Util.getElement("#holdingDateInput");
		this.$endDate = Util.getElement("#endDateInput");
		this.$beginTime = Util.getElement("#beginTimeInput");
		this.$finishTime = Util.getElement("#finishTimeInput");
		const defaultDate = moment().format('YYYY-MM-DD');
		
		this.setHoldingDatePicker();
		this.setBeginTimePicker();
		this.setFinishTimePicker();
		this.setDateTimeInput();
		this.$holdingDate.value = (defaultDate);
		this.$beginTime.value = (this.minTime);
		this.$finishTime.value = (this.maxTime);
		this.$holdingDate.onkeydown = (evt) => {
			evt.preventDefault();
			return false;
		};
		this.$beginTime.onkeydown = (evt) => {
			evt.preventDefault();
			return false;
		};
		this.$finishTime.onkeydown = (evt) => {
			evt.preventDefault();
			return false;
		};
		this.setTimeLimit();
		if(this.$endDate){
			this.setEndDatePicker();
			this.$endDate.value = (defaultDate);
			this.$endDate.onkeydown = (evt) => {
				evt.preventDefault();
				return false;
			};
		}
	},
	setHoldingDatePicker(){
		const today = moment().format('YYYY-MM-DD');
		$("#datepicker").datepicker({
			dateFormat: "yy-mm-dd",
			minDate: this.$holdingDate.value,
		});
		Util.getElement("#holdingDateDiv").onclick = async () => {
			$("#datepicker").datepicker("option", "minDate", today);
			const endDate = inputHandler.getEndDate?.();
			if(this.$holdingDate.disabled != true){
				const date = inputHandler.getHoldingDate();
				if(moment(date).isValid() == true){
					$("#datepicker").datepicker("setDate", date);
				}
				const dateRes = await Modal.get("datePickerModal").show();
				if(dateRes == "OK"){
					const newdate = $("#datepicker").datepicker("getDate");
					const newDateMoment = moment(newdate);
					const startDate = newDateMoment.format('YYYY-MM-DD');
					if(endDate && newDateMoment.isAfter(endDate)){
						inputHandler.setEndDate(startDate);
					}
					inputHandler.setHoldingDate(startDate);
					this.setDateTimeInput();
					this.setTimeLimit();
				}
			}
		}
	},
	setEndDatePicker(){
		const today = moment().format('YYYY-MM-DD');
		$("#datepicker").datepicker({
			dateFormat: "yy-mm-dd",
			minDate: this.$endDate.value,
		});
		Util.getElement("#endDateDiv").onclick = async () => {
			$("#datepicker").datepicker("option", "minDate", today);
			const startDate = inputHandler.getEndDate();
			if(this.$endDate.disabled != true){
				const date = inputHandler.getEndDate();
				if(moment(date).isValid() == true){
					$("#datepicker").datepicker("setDate", date);
				}
				const dateRes = await Modal.get("datePickerModal").show();
				if(dateRes == "OK"){
					const newdate = $("#datepicker").datepicker("getDate");
					const newDateMoment = moment(newdate);
					const endDate = newDateMoment.format('YYYY-MM-DD');
					if(newDateMoment.isBefore(startDate)) {
						inputHandler.setHoldingDate(endDate);
				    }
					inputHandler.setEndDate(endDate);
					this.setDateTimeInput();
					this.setTimeLimit();
				}
			}
		}
	},
	setTimeLimit(){
		const holdingDate = inputHandler.getHoldingDate();
		const nowTimeM = moment();
		const minute = nowTimeM.minute();
		nowTimeM.minutes(Math.floor(minute/this.interval) * this.interval);
		const isToday = (nowTimeM.format("YYYYMMDD") == moment(holdingDate).format("YYYYMMDD"))?true:false;
		//if(isToday){
		//	this.minTime = nowTimeM.format("HH:mm");
		//}else{
			this.minTime = this.openTime;
		//}
		this.setBeginTimePicker();
		this.setFinishTimePicker();
	},
	setBeginTimePicker(){
		if(!this.beginTimePickerInit){
			this.beginTimePickerInit = true;
			$(this.$beginTime).timepicker({
				step: this.interval,
				'timeFormat': 'H:i'
			});
			$(this.$beginTime).on('changeTime', () => {
				this.setFinishTimePicker();
				this.setDateTimeInput();
			});
			setTimeout(() => {
				this.setBeginTimePicker();
			});
			return;
		}
		const minTime = moment(this.minTime, "HH:mm");
		const maxTime = moment(this.maxTime, "HH:mm").add(this.interval * -1, "m");
		
		let beginTime = moment(this.$beginTime.value, "HH:mm");
		const finishTime = moment(this.$finishTime.value, "HH:mm");
		
		if(beginTime.isAfter(finishTime) || beginTime.isSame(finishTime)){
			this.$beginTime.value = (finishTime.add(this.interval * -1, "m").format("HH:mm"));
		}
		beginTime = moment(this.$beginTime.value, "HH:mm");
		if(beginTime.isBefore(minTime)){
			this.$beginTime.value = this.minTime;
		}
		$(this.$beginTime).timepicker('option', {
			'minTime': minTime.format('hh:mm A'),
			'maxTime': maxTime.format('hh:mm A'),
		});
		
	},
	setFinishTimePicker(){
		if(!this.finishTimePickerInit){
			this.finishTimePickerInit = true;
			$(this.$finishTime).timepicker({
				step: this.interval,
				'timeFormat': 'H:i'
			});
			$(this.$finishTime).on('changeTime', () => {
				this.setBeginTimePicker();
				this.setDateTimeInput();
			});
			setTimeout(() => {
				this.setFinishTimePicker();
			});
			return;
		}
		const minTime = moment(this.minTime, "HH:mm").add(this.interval, "m");
		const maxTime = moment(this.maxTime, "HH:mm");
		
		const beginTime = moment(this.$beginTime.value, "HH:mm");
		let finishTime = moment(this.$finishTime.value, "HH:mm");
		/*
		if(beginTime.isBefore(minTime)){
			this.$beginTime.value = this.minTime;
		}
		if(finishTime.isBefore(beginTime) || finishTime.isSame(beginTime)){
			this.$finishTime.value = (beginTime.add(this.interval, "m").format("HH:mm"));
		}
		*/
		if(finishTime.isBefore(beginTime) || finishTime.isSame(beginTime)){
			this.$finishTime.value = (beginTime.add(this.interval, "m").format("HH:mm"));
		}
		finishTime = moment(this.$finishTime.value, "HH:mm");
		if(finishTime.isAfter(maxTime)){
			this.$finishTime.value = this.maxTime;
		}
		$(this.$finishTime).timepicker('option', {
			'minTime': minTime.format('hh:mm A'),
			'maxTime': maxTime.format('hh:mm A'),
		});
	},
	setDateTimeInput(){
		const holdingDate = this.$holdingDate.value;
		const beginTime = this.$beginTime.value;
		const finishTime = this.$finishTime.value;
		const beginDT = moment(holdingDate+'T'+beginTime, 'YYYY-MM-DDTHH:mm:ss');
		const finishDT = moment(holdingDate+'T'+finishTime, 'YYYY-MM-DDTHH:mm:ss');
		
		inputHandler.setBeginDateTime(beginDT.format("YYYY-MM-DDTHH:mm:ss"));
		inputHandler.setFinishDateTime(finishDT.format("YYYY-MM-DDTHH:mm:ss"));
	}
}