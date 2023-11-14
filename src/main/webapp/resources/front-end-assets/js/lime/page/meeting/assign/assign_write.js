/**
 * 
 */
import {eventMixin, Util, Modal, FormHelper} from '/resources/core-assets/essential_index.js';
import {DateInputHelper} from '/resources/core-assets/module_index.js';
import {attendeeHandler} from './module_index.js';
import {Final, FileManager} from '/resources/front-end-assets/js/lime/comm/module_index.js';
import {MeetingCall as $MEETING, RoomCall as $RM} from '/resources/front-end-assets/js/lime/ajax/ajax_index.js';

window.onload = async () => {
	domHandler.init();
	evtHandler.init();
};
const formHelper = new FormHelper({
	valueProcessor: {
		elecYN: (value) => {return (value)?value:'N'},
		secretYN: (value) => {return (value)?value:'N'},
		attendeeCnt: (value) => {return (value)?value:0},
		beginDateTime: (value) => {return (value)?value:(() => {
			const holdingDate = formHelper.getForm("holdingDate").getValue();
			const beginTime = formHelper.getForm("beginTime").getValue();
			return moment(holdingDate+'T'+beginTime, 'YYYY-MM-DDTHH:mm:ss').format("YYYY-MM-DDTHH:mm:ss");
		})()},
		finishDateTime: (value) => {return (value)?value:(() => {
			const holdingDate = formHelper.getForm("holdingDate").getValue();
			const finishTime = formHelper.getForm("finishTime").getValue();
			return moment(holdingDate+'T'+finishTime, 'YYYY-MM-DDTHH:mm:ss').format("YYYY-MM-DDTHH:mm:ss");
		})()},
	},
	triggerEvents: ['input'],
});
const domHandler = {
	async init(){
		this.setForm();
		if(isInsert){
			await this.setInsertMode();
		}else{
			await this.setUpdateMode();
		}
	},
	setForm(){
		formHelper.addFormElements("#meetingForm1").addFormElements("#meetingForm2");
		formHelper.on("change", (data = {}) => {
			const {
				name,
				value,
				element,
			} = data;
			switch(name){
				case "elecYN": {
						if(value == 'Y'){
							const $writeDiv = Util.getElement(".scheduleWriteDiv");
							Util.addClass($writeDiv, "elect");
							formHelper.setAttribute("attendeeCnt", "readonly", true);
						}else{
							const $writeDiv = Util.getElement(".scheduleWriteDiv");
							Util.removeClass($writeDiv, "elect");
							formHelper.setAttribute("attendeeCnt", "readonly", false);
						}
					}
					break;
				case "secretYN": {
						const $secretOnInfo = Util.getElement("#secretOnInfo");
						const $secretOffInfo = Util.getElement("#secretOffInfo");
						if(value == 'Y'){
							$secretOnInfo.style.display = "";
							$secretOffInfo.style.display = "none";
						}else{
							$secretOnInfo.style.display = "none";
							$secretOffInfo.style.display = "";
						}
					}
					break;
				case "approvalStatus": {
						const $approvalStatusDiv = Util.getElement("#approvalStatusDiv");
						switch(value){
							case "REQUEST":
								Util.addClass($approvalStatusDiv, "s0");
								break;
							case "APPROVED":
								Util.addClass($approvalStatusDiv, "s1");
								break;
							case "CANCELED":
								Util.addClass($approvalStatusDiv, "s4");
								break;
							case "REJECTED":
								Util.addClass($approvalStatusDiv, "s5");
								break;
						}
					}
					break;
			}
		});
		const dateInputHelper = new DateInputHelper();
		dateInputHelper.addInput(formHelper.getForm("holdingDate").getElement(), {minDate: moment().format("YYYY-MM-DD")}).on("change", async (data = {}) => {
			const {
				name,
				value,
				element,
			} = data;
			formHelper.getForm("holdingDate").setValue(value);
		});
		timeHandler.init({
			openTime: OPEN_TIME,
			closeTime: CLOSE_TIME,
			interval: INTERVAL_MINUTE,
		});
		attendeeHandler.init({
			editable: true
		});
		attendeeHandler.on("change", (cnt) => {
			formHelper.getForm("attendeeCnt").setValue(cnt);
		});
		FileManager.init({
			editable: true,
			deletable: true,
		});
	},
	async setInsertMode(){
		const room = await $RM.Get.roomOne({
			roomId: roomId,
		});
		formHelper.setDefaultValues(room);
		let momentDate = moment(holdingDate).format('YYYY-MM-DD');
		if(momentDate == "Invalid date"){
			momentDate = moment().format('YYYY-MM-DD');
		}
		formHelper.setDefaultValues({
			holdingDate: momentDate,
			beginTime: startTime,
			finishTime: endTime,
			attendeeCnt: 0,
		});
	},
	async setUpdateMode(){
		const assign = await $MEETING.Get.assignOne(scheduleId);
		if(assign){
			formHelper.setDefaultValues(assign);
			$("input[name='title']").parent().parent().children(".item").children(".input-limit").text(assign.title.length + "/30");
			$("input[name='scheduleHost']").parent().parent().children(".item").children(".input-limit").text(assign.scheduleHost.length + "/10");
			$("textarea[name='contents']").parent().parent().children(".item").children(".input-limit").text(assign.contents.length + "/100");
			formHelper.setDefaultValues(assign.room);
			if(assign.elecYN == 'Y'){
				//const attendeeList = await $MEETING.Get.attendeeSimpleListByMeeting({meetingId: assign.meetingId});
				//attendeeHandler.initAttendeeList(attendeeList);
				const fileList = await $MEETING.Get.meetingMaterialFileList(assign.meetingId);
				FileManager.setFiles(fileList);
			}
			const attendeeList = await $MEETING.Get.attendeeSimpleListByMeeting({meetingId: assign.meetingId});
			attendeeHandler.initAttendeeList(attendeeList);
		}else{
			location.href = history.back();
		}
	},
}

const requestHandler = {
	validation(){
		const title = formHelper.getForm("title").getValue();
		if(Util.isEmpty(title)){
			throw {
				status: 404,
				message: "필수 입력 누락",
				detail: "제목을 입력해 주세요."
			}
		}
		const scheduleHost = formHelper.getForm("scheduleHost").getValue();
		if(Util.isEmpty(scheduleHost)){
			throw {
				status: 404,
				message: "필수 입력 누락",
				detail: "주최자를 입력해 주세요."
			}
		}
		const elecYN = formHelper.getForm("elecYN").getValue();
		if(elecYN == 'Y'){
			const attendeeValidationResult = attendeeHandler.validation();
			if(!attendeeValidationResult){
				throw {
					status: 404,
					message: "필수 입력 누락",
					detail: "회의 진행자를 선택해 주세요."
				}
			}
		}
	},
	async insertMeeting(){
		Modal.startLoading();
		try{
			this.validation();
			const assignResult = await this.insertAssign();
			const meetingId = assignResult.data.meetingId;
			const elecYN = formHelper.getForm("elecYN").getValue();
			if(elecYN == 'Y'){
				//await this.updateAttendee(meetingId);
				await this.updateFile(meetingId);
			}
			console.log("참석자 추가");
			await this.updateAttendee(meetingId);
			console.log("참석자 추가 끝");
			Modal.endLoading();
			await Modal.info({
				msg: "회의가 등록되었습니다."
			});
			history.replaceState(null, null, '/lime/dashboard');
			const scheduleId = assignResult.data.scheduleId;
			location.href  = `/lime/meeting/assign/${scheduleId}`;
		}catch(error){
			if(error.detail == "회의 일정 생성에 실패했습니다.") error.detail = error.detail + "<br/>회의실 사용일정이 겹치지 않는지 확인해주세요."
			Modal.error({response:error});
		}finally{
			Modal.endLoading();
		}
	},
	async updateMeeting(){
		Modal.startLoading();
		try{
			this.validation();
			const scheduleId = formHelper.getForm("scheduleId").getValue();
			const meetingId = formHelper.getForm("meetingId").getValue();
			const assignResult = await this.updateAssign(scheduleId);
			const elecYN = formHelper.getForm("elecYN").getValue();
			if(elecYN == 'Y'){
				//await this.updateAttendee(meetingId);
				await this.updateFile(meetingId);
			}
			console.log("참석자 추가");
			await this.updateAttendee(meetingId);
			console.log("참석자 추가 끝");
			Modal.endLoading();
			await Modal.info({
				msg: "회의가 수정되었습니다."
			});
			history.replaceState(null, null, '/lime/dashboard');
			location.href  = `/lime/meeting/assign/${scheduleId}`;
		}catch(error){
			if(error.detail == "회의 일정 생성에 실패했습니다.") error.detail = error.detail + "<br/>회의실 사용일정이 겹치지 않는지 확인해주세요."
			Modal.error({response:error});
		}finally{
			Modal.endLoading();
		}
	},
	async insertAssign(){
		try{
			const formData = formHelper.getFormData();
			const result = await $MEETING.Post.assignOne({formData: formData});
			if(result.status != 200){
				throw result;
			}
			return result;
		}catch(error){
			throw error;
		}
	},
	async updateAssign(scheduleId){
		try{
			$("input[name='beginDateTime']").val($("input[name='beginDateTime']").val().split("T")[0] + "T" + $("input[name='beginTime']").val() + ":00");
			$("input[name='finishDateTime']").val($("input[name='finishDateTime']").val().split("T")[0] + "T" + $("input[name='finishTime']").val() + ":00");
			const formData = formHelper.getFormData();
			
			const result = await $MEETING.Put.assignOne({scheduleId: scheduleId, formData: formData});
			if(result.status != 200){
				throw result;
			}
			return result;
		}catch(error){
			throw error;
		}
	},
	async updateAttendee(meetingId){
		const attendeeParam = attendeeHandler.getChangeResult();
		const delResult = await $MEETING.Delete.attendeeList({meetingId: meetingId, list: attendeeParam?.deleteList.map(attendee => attendee.attendId)});
		if(delResult.status != 200){
			throw delResult;
		}
		const updateResult = await $MEETING.Put.attendeeList({meetingId: meetingId, list: attendeeParam?.updateList});
		if(updateResult.status != 200){
			throw updateResult;
		}
		const insertResult = await $MEETING.Post.attendeeList({meetingId: meetingId, list: attendeeParam?.insertList});
		if(insertResult.status != 200){
			throw insertResult;
		}
	},
	async updateFile(meetingId){
		const createForm = (fileList) => {
			const formData = new FormData;
			fileList.forEach(file => {
				formData.append("list", file);
			});
			return formData;
		}
		try{
			const fileList = FileManager.getAddedFiles();
			console.log("fileList", fileList);
			if(!Util.isEmpty(fileList)){
				const postRes = await $MEETING.Post.meetingFileList({
					meetingId: meetingId, 
					fileList: createForm(fileList)
				});
				if(postRes.status != 200){
					throw postRes;
				}
			}
			
			const keyList = FileManager.getDeletedFiles();
			if(!Util.isEmpty(keyList)){
				const deleteRes = await $MEETING.Delete.meetingFileList({
					meetingId: meetingId, 
					fileList: keyList
				});
				if(deleteRes.status != 200){
					throw deleteRes;
				}
			}
		}catch(err){
			throw err;
		}
	},
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
		const beginTimeForm = formHelper.getForm("beginTime");
		const finishTimeForm = formHelper.getForm("finishTime");
		this.setBeginTimePicker();
		this.setFinishTimePicker();
		beginTimeForm.getElement().onkeydown = (evt) => {
			evt.preventDefault();
			return false;
		};
		finishTimeForm.getElement().onkeydown = (evt) => {
			evt.preventDefault();
			return false;
		};
		this.setTimeLimit();
	},
	setTimeLimit(){
		const holdingDate = formHelper.getForm("holdingDate").getValue();
		const nowTimeM = moment();
		const minute = nowTimeM.minute();
		nowTimeM.minutes(Math.floor(minute/this.interval) * this.interval);
		const isToday = (nowTimeM.format("YYYYMMDD") == moment(holdingDate).format("YYYYMMDD"))?true:false;
		this.minTime = this.openTime;
		this.setBeginTimePicker();
		this.setFinishTimePicker();
	},
	setBeginTimePicker(){
		const beginTimeForm = formHelper.getForm("beginTime");
		const finishTimeForm = formHelper.getForm("finishTime");
		const $beginTime = beginTimeForm.getElement();
		const $finishTime = finishTimeForm.getElement();
		if(!this.beginTimePickerInit){
			this.beginTimePickerInit = true;
			$($beginTime).timepicker({
				step: this.interval,
				'timeFormat': 'H:i'
			});
			$($beginTime).on('changeTime', () => {
				this.setFinishTimePicker();
			});
			setTimeout(() => {
				this.setBeginTimePicker();
			});
			return;
		}
		const minTime = moment(this.minTime, "HH:mm");
		const maxTime = moment(this.maxTime, "HH:mm").add(this.interval * -1, "m");
		
		let beginTime = moment($beginTime.value, "HH:mm");
		const finishTime = moment($finishTime.value, "HH:mm");
		
		if(beginTime.isAfter(finishTime) || beginTime.isSame(finishTime)){
			beginTimeForm.setValue(finishTime.add(this.interval * -1, "m").format("HH:mm"));
		}
		beginTime = moment($beginTime.value, "HH:mm");
		if(beginTime.isBefore(minTime)){
			beginTimeForm.setValue(this.minTime);
		}
		$($beginTime).timepicker('option', {
			'minTime': minTime.format('hh:mm A'),
			'maxTime': maxTime.format('hh:mm A'),
		});
		
	},
	setFinishTimePicker(){
		const beginTimeForm = formHelper.getForm("beginTime");
		const finishTimeForm = formHelper.getForm("finishTime");
		const $beginTime = beginTimeForm.getElement();
		const $finishTime = finishTimeForm.getElement();
		if(!this.finishTimePickerInit){
			this.finishTimePickerInit = true;
			$($finishTime).timepicker({
				step: this.interval,
				'timeFormat': 'H:i'
			});
			$($finishTime).on('changeTime', () => {
				this.setBeginTimePicker();
			});
			setTimeout(() => {
				this.setFinishTimePicker();
			});
			return;
		}
		const minTime = moment(this.minTime, "HH:mm").add(this.interval, "m");
		const maxTime = moment(this.maxTime, "HH:mm");
		
		const beginTime = moment($beginTime.value, "HH:mm");
		let finishTime = moment($finishTime.value, "HH:mm");
		if(finishTime.isBefore(beginTime) || finishTime.isSame(beginTime)){
			finishTimeForm.setValue(beginTime.add(this.interval, "m").format("HH:mm"));
		}
		finishTime = moment($finishTime.value, "HH:mm");
		if(finishTime.isAfter(maxTime)){
			finishTimeForm.setValue(this.maxTime);
		}
		$($finishTime).timepicker('option', {
			'minTime': minTime.format('hh:mm A'),
			'maxTime': maxTime.format('hh:mm A'),
		});
	},
}

const evtHandler = {
	init(){
		this.enableWriteBtn();
		this.enableCancelBtn();
		this.enableTitleClearBtn();
		this.enableScheduleHostClearBtn();
		this.enableContentsClearBtn();
	},
	enableWriteBtn(){
		const $btn = Util.getElement("#writeBtn");
		const text = (isInsert)?"등 록":"수 정";
		$btn.innerText = text;
		$btn.onclick = () => {
			if(isInsert){
				requestHandler.insertMeeting();
			}else{
				requestHandler.updateMeeting();
			}
		}
	},
	enableCancelBtn(){
		const $btn = Util.getElement("#cancelBtn");
		$btn.onclick = () => {
			if(isInsert){
				history.back();
			}else{
				location.href = `/lime/meeting/assign/${scheduleId}`;
			}
		}
	},
	enableTitleClearBtn(){
		const $btn = Util.getElement("#titleInputClear");
		if($btn){
			$btn.onclick = () => {
				formHelper.getForm("title").setValue("");
			}
		}
	},
	enableScheduleHostClearBtn(){
		const $btn = Util.getElement("#scheduleHostInputClear");
		if($btn){
			$btn.onclick = () => {
				formHelper.getForm("scheduleHost").setValue("");
			}
		}
	},
	enableContentsClearBtn(){
		const $btn = Util.getElement("#contentsInputClear");
		if($btn){
			$btn.onclick = () => {
				formHelper.getForm("contents").setValue("");
			}
		}
	},
}