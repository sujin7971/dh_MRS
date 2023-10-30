/**
 * 
 */
import {Util, Modal, AjaxBuilder} from '/resources/core-assets/essential_index.js';
import {AssignCall as $AS, AdminCall as $ADMIN} from '/resources/front-end-assets/js/lime/ajax/ajax_index.js';
import {paramHandler} from './module_index.js';

$(async function(){
	paramHandler.init({
		editable: false,
	});
	await domHandler.init();
	approvalHandler.init();
});
const domHandler = {
	async init(){
		await this.setAssignData();
	},
	async setAssignData(){
		const assign = await $ADMIN.Get.assignOneForApproval(scheduleId);
		if(assign){
			const room  = assign.room;
			approvalHandler.approvalStatus = assign.approvalStatus;
			paramHandler.initStatusParam(assign);
			paramHandler.initRoomParam(room);
			paramHandler.initScheduleParam(assign);
			paramHandler.initMeetingParam(assign);
			paramHandler.initWriterParam(assign.writer);
		}else{
			throw {
				status: 404
			}
		}
	}
}

const approvalHandler = {
	init(){
		this.setApprovalRadio();
		this.enableApprovalBtn();
		this.enableDeleteBtn();
	},
	setApprovalRadio(){
		const approvalStatus = this.approvalStatus;
		
		const $reqRadio = document.querySelector("#app_request");
		const $appRadio = document.querySelector("#app_approval");
		const $cancelRadio = document.querySelector("#app_cancel");
		const $rejectRadio = document.querySelector("#app_reject");
		const $radioList = document.querySelectorAll("input[name=app]");
		$radioList.value = approvalStatus;
		$radioList.forEach($radio => {
			if($radio.value == approvalStatus){
				$radio.checked = true;
				$radio.disabled = true;
			}
			$radio.onchange = () => {
				const value = $radio.value;
				this.approvalStatus = value;
				this.enableSaveBtn();
			}
		});
		$reqRadio?.addEventListener
	},
	enableApprovalBtn(){
		const $saveBtn = document.querySelector("#saveBtn");
		const $comment = document.querySelector("#comment");
		if(!$saveBtn){
			return;
		}
		$saveBtn.disabled = true;
		this.enableSaveBtn = () => {
			$saveBtn.disabled = false;
		}
		
		$saveBtn.onclick = async () => {
			const approvalStatus = this.approvalStatus;
			const comment = $comment.value;
			AjaxBuilder({
				request: $ADMIN.Post.assignApproval,
				param: {
					scheduleId: scheduleId,
					status: approvalStatus,
					comment: comment,
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
	},
	enableDeleteBtn(){
		const $btn = document.querySelector("#deleteBtn");
		if($btn){
			$btn.onclick = async () => {
				const reply = await Modal.confirm({
					msg: "사용신청을 삭제하시겠습니까? 삭제처리후 해당 사용신청에 대한 조회 및 결재가 불가합니다."
				})
				if(reply == "OK"){
					AjaxBuilder({
						request: $ADMIN.Post.assignApproval,
						param: {
							scheduleId: scheduleId,
							status: "DELETE",
							comment: '승인 담당자 삭제',
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
}
