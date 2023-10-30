/**
 * 
 */

import {Util, Modal, AjaxBuilder} from '/resources/core-assets/essential_index.js';
import {AssignCall as $AS, ScheduleCall as $SKD, AttendeeCall as $ATT, FileCall as $FILE} from '/resources/front-end-assets/js/ewp/comm/ajax_index.js';
import {paramHandler} from './module_index.js';

$(async function(){
	await paramHandler.init({
		editable: false,
	});
	domHandler.init();
});
const domHandler = {
	async init(){
		try{
			await this.setAssignData();
		}catch(err){
			console.error(err);
			await Modal.error({response: err});
			location.href = "/ewp/home";
		}
	},
	async setAssignData(){
		const assign = await $AS.Get.assignOne(skdKey);
		if(assign){
			paramHandler.initAttendeeHandler({
				officeCode: assign.officeCode,
				editable: false,
			});
			paramHandler.initStatusParam(assign);
			paramHandler.initRoomParam(assign.room);
			paramHandler.initScheduleParam(assign);
			paramHandler.initMeetingParam(assign);
			paramHandler.initWriterParam(assign.writer);
			const attendeeList = await $ATT.Get.attendeeSimpleListByMeeting({meetingKey: assign.meetingKey});
			paramHandler.initAttendeeParam(attendeeList);
			const fileList = await $FILE.Get.meetingAllFileList(assign.meetingKey);
			paramHandler.setFiles(fileList);
			
			evtHandler.init({
				skdKey: assign.skdKey,
				meetingKey: assign.meetingKey,
			});
		}else{
			throw {
				status: 404
			}
		}
	}
}

const evtHandler = {
	init(data = {}){
		const {
			skdKey,
			meetingKey,
		} = data;
		this.enableBackBtn();
		this.enableUpdateBtn(skdKey);
		this.enableCancelBtn(skdKey);
		this.enableDeleteBtn(skdKey);
		this.enableFinishBtn();
		this.enableReportBtn(meetingKey);
		this.enableFileInfoBtn();
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
	enableUpdateBtn(skdKey){
		const $btn = Util.getElement("#updateBtn");
		if($btn){
			$btn.onclick = () => {
				location.href = `/ewp/meeting/assign/${skdKey}/post`;
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
							skdKey: skdKey,
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
							skdKey: skdKey,
							status: "DELETE",
							comment: '신청자 삭제',
						},
						exception: 'success-only',
					}).success(res => {
						Modal.startLoading();
						setTimeout(() => {
							history.pushState({page: 'list'}, null, null);
							location.href = "/ewp/home";
						}, 5000);
					}).error(err => {
						Modal.error({response: err});
					}).finally(() => {
					}).exe();
				}
			}
		}
	},
	enableFinishBtn(){
		const $btn = Util.getElement("#finishBtn");
		if($btn){
			$btn.onclick = async () => {
				const reply = await Modal.confirm({
					msg: "회의를 종료하시겠습니까?"
				});
				if(reply == "OK"){
					AjaxBuilder({
						request: $SKD.Put.scheduleFinish,
						param: {
							skdKey: skdKey,
						},
						exception: 'success-only',
					}).success(res => {
						Modal.startLoading();
						setTimeout(() => {
							history.pushState({page: 'list'}, null, null);
							location.reload();
						}, 1000);
					}).error(err => {
						Modal.error({response: err});
					}).finally(() => {
					}).exe();
				}
			}
		}
	},
	enableReportBtn(meetingKey){
		const $btn = Util.getElement("#reportBtn");
		if($btn){
			$btn.onclick = () => {
				location.href = "/ewp/meeting/"+meetingKey+"/report";
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
	}
}