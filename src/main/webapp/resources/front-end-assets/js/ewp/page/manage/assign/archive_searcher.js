/**
 * 
 */

import {Util, Modal, AjaxBuilder} from '/resources/core-assets/essential_index.js';
import {SearchBar} from '/resources/front-end-assets/js/ewp/comm/module_index.js';
import {archiveEvtHandler, archiveRowGenerator} from './archive_builder.js';
import {AssignCall as $AS} from '/resources/front-end-assets/js/ewp/comm/ajax_index.js';

$(function(){
	archiveEvtHandler.init();
	archiveEvtHandler.on("scrollBottom", async () => {
		await searchHandler.nextPage();
	});
	archiveEvtHandler.on("titleClick", (param) => {
		const {
			skdKey,
		} = param;
		location.href = getViewAPI(skdKey);
	});
	searchHandler.init({
		target: target,
		searchWord: searchWord,
		roleType: roleType,
		startDate: startDate,
		endDate: endDate,
	});
});

const getRequestAPI = () => {
	switch(pageType){
		case 'user':
			return $AS.Get.userArchiveList;
		case 'dept':
			return $AS.Get.deptArchiveList;
		case 'admin':
			return $AS.Get.adminArchiveList;
	}
}

const getViewAPI = (skdKey) => {
	switch(pageType){
		case 'user':
		case 'dept':
			return `/ewp/meeting/assign/${skdKey}`;
		case 'admin':
			return "/ewp/admin/system/meeting/assign/"+skdKey;
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
			roleType,
			startDate,
			endDate,
			pageNo = 1,
			pageCnt = 30,
		} = data;
		this.pageNo = pageNo;
		this.pageCnt = pageCnt;
		SearchBar.init({
			searchTarget: searchTarget,
			searchWord: searchWord,
			roleType: roleType,
			startDate: startDate,
			endDate: endDate,
		});
		SearchBar.initSearchTarget();
		SearchBar.initSearchWord();
		SearchBar.initPeriodPicker();
		SearchBar.initFileType();
		SearchBar.on("search", (data = {}) => {
			this.search(data);
		});
		SearchBar.on("reset", (data = {}) => {
			this.reset(data);
		});
		this.search(SearchBar.getSearchInput());
	},
	async search(data = {}){
		const {
			searchTarget = this.searchTarget,
			searchWord = this.searchWord,
			roleType = this.roleType,
			startDate = this.startDate,
			endDate = this.endDate,
		} = data;
		this.searchTarget = searchTarget;
		this.searchWord = searchWord;
		this.startDate = startDate;
		this.endDate = endDate;
		this.roleType = roleType;
		this.pageNo = 1;
		archiveRowGenerator.clear();
		await this.showList();
	},
	reset(data){
		this.search(data);
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
		const searchTarget = this.searchTarget;
		const param = {
				roleType: this.roleType,
				startDate: this.startDate,
				endDate: this.endDate,
				pageNo: this.pageNo,
				pageCnt: this.pageCnt,
			}
		param[searchTarget] = this.searchWord;
		
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