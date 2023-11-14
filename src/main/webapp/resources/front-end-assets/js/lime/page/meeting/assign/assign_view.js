/**
 * 
 */

import {eventMixin, Util, Modal, FormHelper, AjaxBuilder} from '/resources/core-assets/essential_index.js';
import {attendeeHandler} from './module_index.js';
import {Final, FileManager} from '/resources/front-end-assets/js/lime/comm/module_index.js';
import {MeetingCall as $MEETING, RoomCall as $RM, AssignCall as $AS} from '/resources/front-end-assets/js/lime/ajax/ajax_index.js';

window.onload = async () => {
	await domHandler.init();
	evtHandler.init();
};
const formHelper = new FormHelper();
const domHandler = {
	async init(){
		this.setForm();
		await this.setData();
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
		attendeeHandler.init({
			editable: false
		});
		attendeeHandler.on("change", (cnt) => {
			formHelper.getForm("attendeeCnt").setValue(cnt);
		});
		FileManager.init({
			editable: false,
			deletable: false,
		}).on("click", (file) => {
			$MEETING.Get.viewFile({
				...file,
				meetingId: formHelper.getForm("meetingId").getValue(),
			});
		});
	},
	async setData(){
		const assign = await $MEETING.Get.assignOne(scheduleId);
		if(assign){
			console.log("assign", assign)
			formHelper.setDefaultValues(assign);
			$("input[name='title']").parent().parent().children(".item").children(".input-limit").text(assign.title.length + "/30");
			$("input[name='scheduleHost']").parent().parent().children(".item").children(".input-limit").text(assign.scheduleHost.length + "/10");
			$("textarea[name='contents']").parent().parent().children(".item").children(".input-limit").text(assign.contents.length + "/100");
			formHelper.setDefaultValues(assign.room);
			formHelper.setDefaultValues(assign.writer);
			if(assign.elecYN == 'Y'){
				//const attendeeList = await $MEETING.Get.attendeeSimpleListByMeeting({meetingId: assign.meetingId});
				//attendeeHandler.initAttendeeList(attendeeList);
				const fileList = await $MEETING.Get.meetingAllFileList(assign.meetingId);
				FileManager.setFiles(fileList);
			}
			const attendeeList = await $MEETING.Get.attendeeSimpleListByMeeting({meetingId: assign.meetingId});
			attendeeHandler.initAttendeeList(attendeeList);
		}else{
			location.href = history.back();
		}
	},
}

const evtHandler = {
	init(){
		const scheduleId = formHelper.getForm("scheduleId").getValue();
		const meetingId = formHelper.getForm("meetingId").getValue();
		this.enableBackBtn();
		this.enableUpdateBtn(scheduleId);
		this.enableCancelBtn(scheduleId);
		this.enableDeleteBtn(scheduleId);
		this.enableReportBtn(meetingId);
		this.enableFileInfoBtn();
		this.enableDownloadBtn(meetingId);
	},
	enableBackBtn(){
		const $btn = Util.getElement("#backBtn");
		if($btn){
			$btn.onclick = () => {
				history.back();
			}
		}
		Util.getElement("#backArrow").onclick = () => {
			history.back();
		}
	},
	enableUpdateBtn(scheduleId){
		const $btn = Util.getElement("#updateBtn");
		if($btn){
			$btn.onclick = () => {
				location.href = `/lime/meeting/assign/${scheduleId}/post`;
			}
		}
	},
	enableCancelBtn(){
		const $btn = Util.getElement("#cancelBtn");
		if($btn){
			$btn.onclick = async () => {
				const reply = await Modal.confirm({
					msg: "사용신청을 취소하시겠습니까?"
				});
				if(reply == "OK"){
					AjaxBuilder({
						request: $AS.Post.assignApproval,
						param: {
							scheduleId: scheduleId,
							status: "CANCELED",
							comment: '신청자 취소',
						},
						exception: 'success-only',
					}).success(res => {
						Modal.startLoading();
						setTimeout(() => {
							location.reload();
						}, 5000);
					}).error(err => {
						Modal.error({response: err});
					}).finally(() => {
					}).exe();
				}
			}
		}
	},
	enableDeleteBtn(){
		const $btn = Util.getElement("#deleteBtn");
		if($btn){
			$btn.onclick = async () => {
				const reply = await Modal.confirm({
					msg: "사용신청을 삭제하시겠습니까? 삭제처리후 해당 사용신청에 대한 조회 및 수정이 불가합니다."
				});
				if(reply == "OK"){
					AjaxBuilder({
						request: $AS.Post.assignApproval,
						param: {
							scheduleId: scheduleId,
							status: "DELETE",
							comment: '신청자 삭제',
						},
						exception: 'success-only',
					}).success(res => {
						Modal.startLoading();
						setTimeout(() => {
							history.pushState({page: 'list'}, null, null);
							location.href = "/lime/home";
						}, 5000);
					}).error(err => {
						Modal.error({response: err});
					}).finally(() => {
					}).exe();
				}
			}
		}
	},
	enableReportBtn(meetingId){
		const $btn = Util.getElement("#reportBtn");
		if($btn){
			$btn.onclick = () => {
				location.href = `/lime/meeting/${meetingId}/report`;
			}
		}
	},
	enableFileInfoBtn(){
		const $btn = Util.getElement("#fileInfoBtn");
		if($btn){
			$btn.onclick = () => {
				Modal.get("fileStateModal").show();
			}
		}
	},
	enableDownloadBtn(meetingId){
		const $btn = Util.getElement("#fileDownBtn");
		if($btn){
			$btn.onclick = () => {
				const selectedFileList = FileManager.getCheckedFileList();
				const idList = selectedFileList.map(file => file.fileId);
				$MEETING.Get.downloadFileList(idList);
			}
		}
	}
}