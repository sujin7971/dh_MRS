/**
 * 
 */
import {eventMixin, Util, Modal, AjaxBuilder} from '/resources/core-assets/essential_index.js';
import {createNoticeSearchManager, createNoticeModalManager} from '/resources/front-end-assets/js/ewp/page/board/notice_module.js';
import {SearchBar} from '/resources/front-end-assets/js/ewp/comm/module_index.js';
import {Final, FileManager} from '/resources/front-end-assets/js/ewp/comm/module_index.js';
import {NoticeCall as $NOTICE, AdminCall as $ADM, FileCall as $FILE} from '/resources/front-end-assets/js/ewp/comm/ajax_index.js';

$(async () => {
	noticeRowRenderer.init();
	const noticeSearchManager = createNoticeSearchManager(noticeRowRenderer);
	noticeSearchManager.init({
		pagination: true,
		officeCode: officeCode,
		startDate: startDate,
		endDate: endDate,
		pageNo: pageNo
	});
	objectManager.setSearchManager(noticeSearchManager);
	const noticeModalManager = createNoticeModalManager(noticeSearchManager);
	objectManager.setModalManager(noticeModalManager);
	evtHandler.init();
});

const objectManager = {
	setSearchManager(noticeSearchManager){
		this.noticeSearchManager = noticeSearchManager;
	},
	getSearchManager(){
		return this.noticeSearchManager;
	},
	setModalManager(noticeModalManager){
		this.noticeModalManager = noticeModalManager;
	},
	getModalManager(){
		return this.noticeModalManager;
	}
}
const evtHandler = {
	init(){
		this.setWriteModalBtn();
		this.setFileUploadBtn();
	},
	setWriteModalBtn(){
		const $btn = Util.getElement("#showWriteModalBtn");
		if(!$btn){
			return;
		}
		$btn.onclick = () => {
			objectManager.getModalManager().showAddModal();
		}
	},
	setFileUploadBtn(){
		FileManager.init();
	},
}
const noticeBuilder = (notice) => {
	const $row = Util.createElement("div", "row");
	const createNoColumn = () => {
		const $column = Util.createElement("div", "item", "col-1", "justify-content-center");
		if(notice.fixYN == 'Y'){
			const $icon = Util.createElement("i", "fas", "fa-thumbtack");
			$column.appendChild($icon);
		}else{
			$column.innerHTML = notice.noticeId
		}
		
		$row.appendChild($column);
	}
	// 제목 칼럼
	const createOfficeColumn = () => {
		const $column = Util.createElement("div", "item", "col-1", "justify-content-center", "ellipsis");
		$column.innerHTML = notice.officeName;
		$row.appendChild($column);
	}
	// 제목 칼럼
	const createTitleColumn = () => {
		const $column = Util.createElement("div", "item", "col-8");
		if(!Util.isEmpty(notice.fileList)){
			notice.fileList.forEach(file => file.fileKey = file.fileId);
			const $clipIcon = Util.createElement("i", "fas", "fa-paperclip", "mr-2");
			$column.appendChild($clipIcon);
		}
		const $title = Util.createTextNode(Util.unescape(notice.title));
		$column.appendChild($title);
		$row.appendChild($column);
	}
	// 등록일 칼럼
	const createRegDateColumn = () => {
		const $column = Util.createElement("div", "item", "col-2", "justify-content-center");
		const regDateM = moment(notice.regDateTime);
		const $dateSpan = Util.createElement("span", "date");
		$dateSpan.innerHTML = regDateM.format("YYYY.MM.DD");
		const $timeSpan = Util.createElement("span", "time", "ml-2");
		$timeSpan.innerHTML = regDateM.format("HH:mm");
		$column.appendChild($dateSpan);
		$column.appendChild($timeSpan);
		
		$row.appendChild($column);
	}
	$row.onclick = () => {
		objectManager.getModalManager().showViewModal(notice);
	}
	createNoColumn();
	createOfficeColumn();
	createTitleColumn();
	createRegDateColumn();
	
	return $row;
}

const noticeRowRenderer = {
	init(){
		this.fixedNoticeListBox = Util.getElement("#fixedNoticeListBox");
		this.nonfixedNoticeListBox = Util.getElement("#nonfixedNoticeListBox");
	},
	clearFixNotice(){
		this.fixedNoticeListBox.innerHTML = "";
	},
	renderFixedNotices(noticeList){
		this.clearFixNotice();
		const $listBox = this.fixedNoticeListBox;
		for(const notice of noticeList){
			const $row = noticeBuilder(notice);
			$listBox.appendChild($row);
		}
	},
	clearNonfixNotice(){
		this.nonfixedNoticeListBox.innerHTML = "";
	},
	renderNonFixedNotices(noticeList){
		this.clearNonfixNotice();
		const $listBox = this.nonfixedNoticeListBox;
		for(const notice of noticeList){
			const $row = noticeBuilder(notice);
			$listBox.appendChild($row);
		}
	},
}
