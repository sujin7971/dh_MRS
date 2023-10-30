/**
 * 
 */

import {Util, Dom, ModalService, FormHelper} from '/resources/core-assets/essential_index.js';
import {setupSecurityAgreementModal} from '/resources/front-end-assets/js/ewp/partials/modal/modal_provider_index.js';
import DateInputHelper from '/resources/core-assets/modules/jquery.dateInputHelper.js';
import {assignEvtHandler, assignRowGenerator} from './history_builder.js';
import {AssignCall as $AS} from '/resources/front-end-assets/js/ewp/comm/ajax_index.js';

window.onload = () => {
	assignEvtHandler.init();
	assignEvtHandler.on("scrollBottom", async () => {
		searchHandler.nextPage();
	});
	searchHandler.init({
		//approvalStatus: approvalStatus,
		meetingStatus: meetingStatus,
		roomType: roomType,
		title: title,
		host: host,
		attendeeName: attendeeName,
		elecYN: elecYN,
		secretYN: secretYN,
		startDate: startDate,
		endDate: endDate,
	});
	setupSecurityAgreementModal();
}

/**
 * 사용신청목록 검색
 */
const searchHandler = {
	async init(data = {}){
		const {
			//approvalStatus,
			meetingStatus,
			roomType,
			title,
			host,
			attendeeName,
			elecYN,
			secretYN,
			startDate,
			endDate,
			pageNo = 1,
			pageCnt = 30,
		} = data;
		this.pageNo = pageNo;
		this.pageCnt = pageCnt;
		const searchHelper = this.searchHelper = new FormHelper({
			valueProcessor: {
				meetingStatus: (value) => {
					return (value == "0")?null:value;
				},
				title: (value) => {
					return (Util.isEmpty(value))?null:value;
				},
				host: (value) => {
					return (Util.isEmpty(value))?null:value;
				},
				attendeeName: (value) => {
					return (Util.isEmpty(value))?null:value;
				},
				elecYN: (value) => {
					return (value != "Y")?null:value;
				},
				secretYN: (value) => {
					return (value != "Y")?null:value;
				},
			}
		});
		searchHelper.addFormElements("#searchForm").on({
			change: (event, instance) => {
				const {
					name,
					value,
					element,
					form
				} = instance;
				switch(name){
					case "meetingStatus":
						const $meetingStatusLabel = Dom.getElement("#meetingStatusLabel");
						$meetingStatusLabel.innerText = form.getSelectedText();
						break;
					case "roomType":
						const $roomTypeLabel = Dom.getElement("#roomTypeLabel");
						$roomTypeLabel.innerText = form.getSelectedText();
						break;
				}
			}, 
			click: (event, instance) => {
				const {
					name
				} = instance;
				switch(name){
					case "mobileReset":
					case "reset":
						this.reset();
						break;
					case "search":
						this.search();
						break;
				}
			}
		});
		searchHelper.setDefaultValues({
			//approvalStatus: approvalStatus,
			meetingStatus: meetingStatus,
			roomType: roomType,
			title: title,
			host: host,
			attendeeName: attendeeName,
			elecYN: elecYN,
			secretYN: secretYN,
			startDate: startDate,
			endDate: endDate,
		});
		const dateInputHelper = new DateInputHelper();
		dateInputHelper.addInput(searchHelper.getForm("startDate").getElement()).addInput(searchHelper.getForm("endDate").getElement());
		this.search();
	},
	async search(){
		const {
			//approvalStatus = this.approvalStatus,
			meetingStatus = this.meetingStatus,
			roomType = this.roomType,
			title = this.title,
			host = this.host,
			attendeeName = this.attendeeName,
			elecYN = this.elecYN,
			secretYN = this.secretYN,
			startDate = this.startDate,
			endDate = this.endDate,
		} = this.searchHelper.getFormValues();
		//this.approvalStatus = approvalStatus;
		this.meetingStatus = meetingStatus;
		this.roomType = roomType;
		this.title = title;
		this.host = host;
		this.attendeeName = attendeeName;
		this.elecYN = elecYN;
		this.secretYN = secretYN;
		this.startDate = startDate;
		this.endDate = endDate;
		this.pageNo = 1;
		assignRowGenerator.clear();
		await this.showList();
	},
	reset(){
		this.pageNo = 1;
		this.searchHelper.reset();
		this.search();
	},
	async nextPage(){
		this.pageNo = this.pageNo + 1;
		await this.showList();
	},
	async showList(){
		assignEvtHandler.disableScrollPagination();
		assignRowGenerator.addPlaceHolder();
		const requestAPI = this.getRequestAPI();
		try{
			const assignList = await requestAPI({
				//approvalStatus = this.approvalStatus,
				meetingStatus: this.meetingStatus,
				roomType: this.roomType,
				title: this.title,
				host: this.host,
				attendeeName: this.attendeeName,
				elecYN: this.elecYN,
				secretYN: this.secretYN,
				startDate: this.startDate,
				endDate: this.endDate,
				pageNo: this.pageNo,
				pageCnt: this.pageCnt
			});
			assignRowGenerator.deletePlaceHolder();
			assignEvtHandler.enableScrollPagination();
			if(assignList){
				if(assignList.length < this.pageCnt){
					assignEvtHandler.disableScrollPagination();
				}
				assignRowGenerator.generate(assignList);
			}else{
				assignRowGenerator.clear();
			}
		}catch(err){
			ModalService.errorBuilder(err).build().show();
		}
	},
	getRequestAPI(){
		switch(pageType){
			case 'user':
				return $AS.Get.userAssignList;
			case 'dept':
				return $AS.Get.deptAssignList;
		}
	}
}