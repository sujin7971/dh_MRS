/**
 * 
 */
import {Util, Dom, FormHelper, ModalService} from '/resources/core-assets/essential_index.js';
import {setupApprovalProcessModal} from '/resources/front-end-assets/js/ewp/partials/modal/modal_provider_index.js';
import {attendeeHandler} from './module_index.js';
import {AssignCall as $AS, AttendeeCall as $ATT, AdminCall as $ADMIN} from '/resources/front-end-assets/js/ewp/comm/ajax_index.js';

window.onload = () => {
	setupApprovalProcessModal();
}
const getStatusValue = (sts) => {
	switch(sts){
		case "REQUEST": return {cls: "s0", label: "승인대기"};
		case "APPROVED": return {cls: "s1", label: "승인완료"};
		case "CANCELED": return {cls: "s4", label: "사용취소"};
		case "REJECTED": return {cls: "s5", label: "승인불가"};
	}
}
const infoHandler = await ( async (meetingHelper) => {
	const formHelper = meetingHelper();
	const skdKey = formHelper.getValue("skdKey");
	formHelper.on({
		change: (event, instance) => {
			const {
				name,
				value,
				element,
				form
			} = instance;
			switch(name){
				case "appStatus":{
					const {cls} = getStatusValue(value);
					const $row = Dom.getElement("#approvalRow");
					Dom.removeClass($row, "s0", "s1", "s4", "s5");
					Dom.addClass($row, cls);
				}
					break;
				case "appComment":{
					const $comment = Dom.getElement("#approvalComment");
					$comment.innerText = (Util.isEmpty(value))?"":value;
				}
					break;
				case "officeCode":{
					const text = form.getSelectedText();
					Dom.getElement("#officeLabel").innerText = text;
				}
					break;
				case "roomType":{
					const text = form.getSelectedText();
					Dom.getElement("#roomTypeLabel").innerText = text;
				}
					break;
				case "elecYN": {
					const $writeDiv = Util.getElement(".scheduleWriteDiv");
					if(value == 'Y'){
						Util.addClass($writeDiv, "elect");
					}else{
						Util.removeClass($writeDiv, "elect");
					}
				}
					break;
			}
		},
		click: async (event, instance) => {
			const {
				name,
				element,
				form
			} = instance;
			const approvalProcessModal = ModalService.get("approvalProcess");
			switch(name){
				case "approveBtn":
				case "rejectBtn":{
					const approvalStatus = (name == "approveBtn")?"APPROVED":"REJECTED"
					const response = await approvalProcessModal.show({id: skdKey, request: approvalStatus});
					const {action, status} = response;
					if(action == "PROCESS" && status == "SUCCESS") {
						location.reload();
					}
				}
			}
		}
	});
	const assign = await $ADMIN.Get.assignOneForApproval(skdKey);
	formHelper.setDefaultValues(assign);
	formHelper.setDefaultValues(assign.room);
	formHelper.setDefaultValues(assign.writer);
	if(assign.elecYN == 'Y'){
		const attendeeList = assign.attendeeList;
		attendeeHandler.initAttendeeList(attendeeList);
	}
	return formHelper;
})(() => {
	const formHelper = new FormHelper({
		valueMutator: {
			beginTime: (value) =>{
				return moment(value, "HH:mm:ss").format("HH:mm");
			},
			finishTime: (value) =>{
				return moment(value, "HH:mm:ss").format("HH:mm");
			}
		}
	});
	formHelper.addFormElements("#meetingForm");
	return formHelper;
});
const pageBtnHandler = ((btnHelper) => {
	const formHelper = btnHelper();
	const skdKey = infoHandler.getValue("skdKey");
	const $editBtn = formHelper.getButton("editBtn");
	const $saveBtn = formHelper.getButton("saveBtn");
	const $title = infoHandler.getForm("title").getElement();
	const $skdHost = infoHandler.getForm("skdHost").getElement();
	let preTitle = $title.value;
	let preSkdHost = $skdHost.value;
	$saveBtn.style.display = "none";
	formHelper.on({
		click: async (event, instance) => {
			const {
				name,
				element,
			} = instance;
			switch(name){
				case "backBtn":{
				}
					break;
				case "editBtn":{
					$editBtn.style.display = "none";
					$saveBtn.style.display = "";
					
					$title.readOnly = false;
					$skdHost.readOnly = false;
					$title.focus();
					preTitle = $title.value;
					preSkdHost = $skdHost.value;
				}
					break;
				case "saveBtn":{
					$editBtn.style.display = "";
					$saveBtn.style.display = "none";
					
					$title.readOnly = true;
					$skdHost.readOnly = true;
					
					const newTitle = $title.value;
					const newSkdHost = $skdHost.value;
					if(newTitle != preTitle || newSkdHost != preSkdHost){
						const loading = ModalService.loadingBuilder().build();
						loading.setMessage("잠시만 기다려주세요...").show();
						try{
							await $ADMIN.Put.meetingAssignTitleAndHost({
								skdKey: skdKey,
								title: newTitle,
								skdHost: newSkdHost,
							});
						}catch(err){
							ModalService.errorBuilder(err).build().show();
						}finally{
							loading.hide();
						}
					} 
				}
					break;
				case "deleteBtn":{
					const {action} = await ModalService.deleteBuilder()
					.setTitle()
					.setMessage("사용신청을 삭제하시겠습니까? 삭제처리후 해당 사용신청에 대한 조회 및 결재가 불가합니다.")
					.build().show();
					if(action == "YES"){
						try{
							const loading = ModalService.loadingBuilder().build();
							loading.show();
							const response = await $ADMIN.Post.assignApproval({
								skdKey: skdKey,
								status: "DELETE",
								comment: '배정 담당자 삭제',
							});
							if(response.status == 200){
								loading.setMessage("삭제 처리중입니다...");
								setTimeout(() => {
									loading.setMessage("삭제 완료. 잠시만 기다려주세요...");
									history.pushState({page: 'list'}, null, null);
									location.href = "/ewp/home";
								}, 5000);
							}else{
								throw response;				
							}
						}catch(err){
							ModalService.errorBuilder(err).build().show();
						}
					}
				}
					break;
			}
		}
	});
	return formHelper;
})(() => {
	const formHelper = new FormHelper();
	formHelper.addFormElements("#pageBtnForm");
	return formHelper;
});