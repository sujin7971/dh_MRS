/**
 * 
 */

import {Util, Modal, AjaxBuilder, FormHelper} from '/resources/core-assets/essential_index.js';
import {DateInputHelper} from '/resources/core-assets/module_index.js';
import {archiveEvtHandler, archiveRowGenerator} from './archive_builder.js';
import {MeetingCall as $MEETING} from '/resources/front-end-assets/js/lime/ajax/ajax_index.js';

$(function(){
	archiveEvtHandler.init();
	archiveEvtHandler.on("scrollBottom", async () => {
		await searchHandler.nextPage();
	});
	archiveEvtHandler.on("titleClick", (param) => {
		const {
			scheduleId,
		} = param;
		location.href = getViewAPI(scheduleId);
	});
	searchHandler.init({
		searchTarget: searchTarget,
		searchWord: searchWord,
		relationType: relationType,
		startDate: startDate,
		endDate: endDate,
	});
});

const getRequestAPI = () => {
	switch(pageType){
		case 'user':
			return $MEETING.Get.userArchiveList;
		case 'dept':
			return $MEETING.Get.deptArchiveList;
		case 'admin':
			return $MEETING.Get.adminArchiveList;
	}
}

const getViewAPI = (scheduleId) => {
	switch(pageType){
		case 'user':
		case 'dept':
			return `/lime/meeting/assign/${scheduleId}`;
		case 'admin':
			return `/lime/admin/system/meeting/assign/${scheduleId}`;
	}
}

/**
 * 파일함 검색
 */
const searchHandler = {
	init(data = {}){
		const {
			searchTarget,
			searchWord,
			relationType,
			startDate,
			endDate,
			pageNo = 1,
			pageCnt = 30,
		} = data;
		this.pageNo = pageNo;
		this.pageCnt = pageCnt;
		const searchHelper = this.searchHelper = new FormHelper();
		searchHelper.addFormElements("#searchForm").on("change", async (data = {}) => {
			const {
				name,
				value,
				element,
			} = data;
			if(name == "searchTarget"){
				const $searchWord = searchHelper.getForm("searchWord")?.getElement();
				if(value == "none"){
					$searchWord.disabled = true;
				}else{
					$searchWord.disabled = false;
				}
			}
		});
		searchHelper.setDefaultValues({
			searchTarget: searchTarget,
			searchWord: searchWord,
			relationType: relationType,
			startDate: startDate,
			endDate: endDate,
		});
		const dateInputHelper = new DateInputHelper();
		dateInputHelper.addInput(searchHelper.getForm("startDate").getElement()).addInput(searchHelper.getForm("endDate").getElement());
		this.search(searchHelper.getFormValues());
		
		searchHelper.on({
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
						this.search(searchHelper.getFormValues());
						break;
				}
			}
		});
	},
	async search(data = {}){
		console.log("search", data);
		const {
			searchTarget = this.searchTarget,
			searchWord = this.searchWord,
			relationType = this.relationType,
			startDate = this.startDate,
			endDate = this.endDate,
		} = data;
		this.searchTarget = searchTarget;
		this.searchWord = searchWord;
		this.startDate = startDate;
		this.endDate = endDate;
		this.relationType = relationType;
		this.pageNo = 1;
		archiveRowGenerator.clear();
		await this.showList();
	},
	reset(data){
		this.searchHelper.reset();
		this.search(this.searchHelper.getFormValues());
	},
	async nextPage(){
		this.pageNo = this.pageNo + 1;
		await this.showList();
	},
	async showList(){
		archiveEvtHandler.disableScrollPagination();
		Modal.startLoading({
			animation: "spinner", 
			target: archiveRowGenerator.getContainer(),
		});
		const param = {
			relationType: this.relationType,
			startDate: this.startDate,
			endDate: this.endDate,
			pageNo: this.pageNo,
			pageCnt: this.pageCnt,
		}
		if(this.searchTarget != "none"){
			param[this.searchTarget] = this.searchWord;
		}
		
		const archiveList = await AjaxBuilder({
			request: getRequestAPI(),
			param: param,
			loading: false,
		}).exe();
		Modal.endLoading();
		archiveEvtHandler.enableScrollPagination();
		if(archiveList){
			if(archiveList.length < this.pageCnt){
				archiveEvtHandler.disableScrollPagination();
			}
			archiveRowGenerator.generate(archiveList);
		}else{
			archiveRowGenerator.clear();
		}
	}
}