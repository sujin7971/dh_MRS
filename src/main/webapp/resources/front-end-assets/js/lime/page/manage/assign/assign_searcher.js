/**
 * 
 */

import {Util, Modal, AjaxBuilder, FormHelper} from '/resources/core-assets/essential_index.js';
import {DateInputHelper} from '/resources/core-assets/module_index.js';
import {assignEvtHandler, assignRowGenerator} from './assign_builder.js';
import {MeetingCall as $MEETING} from '/resources/front-end-assets/js/lime/ajax/ajax_index.js';

$(async () => {
	assignEvtHandler.init();
	assignEvtHandler.on("scrollBottom", async () => {
		await searchHandler.nextPage();
	});
	await searchHandler.init({
		//approvalStatus: approvalStatus,
		meetingStatus: meetingStatus,
		roomType: roomType,
		title: title,
		scheduleHost: scheduleHost,
		attendeeName: attendeeName,
		elecYN: elecYN,
		secretYN: secretYN,
		startDate: startDate,
		endDate: endDate,
	});
});

const getRequestAPI = () => {
	switch(pageType){
		case 'user':
			return $MEETING.Get.userAssignList;
		case 'dept':
			return $MEETING.Get.deptAssignList;
	}
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
			scheduleHost,
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
		const searchHelper = this.searchHelper = new FormHelper();
		searchHelper.addFormElements("#searchForm");
		searchHelper.setDefaultValues({
			meetingStatus: meetingStatus,
			roomType: roomType,
			title: title,
			scheduleHost: scheduleHost,
			attendeeName: attendeeName,
			elecYN: elecYN,
			secretYN: secretYN,
			startDate: startDate,
			endDate: endDate,
		});
		const dateInputHelper = new DateInputHelper();
		dateInputHelper.addInput(searchHelper.getForm("startDate").getElement()).addInput(searchHelper.getForm("endDate").getElement());
		searchHelper.on("search", (data = {}) => {
			this.search(data);
		});
		searchHelper.on("reset", (data = {}) => {
			this.reset(data);
		});
		this.search(searchHelper.getFormValues());
	},
	async search(data = {}){
		const {
			//approvalStatus = this.approvalStatus,
			meetingStatus = this.meetingStatus,
			roomType = this.roomType,
			title = this.title,
			scheduleHost = this.scheduleHost,
			attendeeName = this.attendeeName,
			elecYN = this.elecYN,
			secretYN = this.secretYN,
			startDate = this.startDate,
			endDate = this.endDate,
		} = data;
		//this.approvalStatus = approvalStatus;
		this.meetingStatus = (meetingStatus == "ALL")?null:meetingStatus;
		this.roomType = roomType;
		this.title = title;
		this.scheduleHost = scheduleHost;
		this.attendeeName = attendeeName;
		this.elecYN = elecYN;
		this.secretYN = secretYN;
		this.startDate = startDate;
		this.endDate = endDate;
		this.pageNo = 1;
		assignRowGenerator.clear();
		await this.showList();
	},
	reset(data){
		this.pageNo = 1;
		this.search(data);
	},
	async nextPage(){
		this.pageNo = this.pageNo + 1;
		await this.showList();
	},
	async showList(){
		assignEvtHandler.disableScrollPagination();
		Modal.startLoading({
			animation: "spinner", 
			target: assignRowGenerator.getContainer(),
		});
		const assignList = await AjaxBuilder({
			request: getRequestAPI(),
			param: {
				//approvalStatus = this.approvalStatus,
				meetingStatus: this.meetingStatus,
				roomType: this.roomType,
				title: this.title,
				scheduleHost: this.scheduleHost,
				attendeeName: this.attendeeName,
				elecYN: this.elecYN,
				secretYN: this.secretYN,
				startDate: this.startDate,
				endDate: this.endDate,
				pageNo: this.pageNo,
				pageCnt: this.pageCnt,
			},
			loading: false,
		}).exe();
		Modal.endLoading();
		assignEvtHandler.enableScrollPagination();
		if(assignList){
			if(assignList.length < this.pageCnt){
				assignEvtHandler.disableScrollPagination();
			}
			assignRowGenerator.generate(assignList);
		}else{
			assignRowGenerator.clear();
		}
	}
}