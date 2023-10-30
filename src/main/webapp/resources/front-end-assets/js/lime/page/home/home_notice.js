/**
 * 
 */
import {eventMixin, Util, Modal, AjaxBuilder} from '/resources/core-assets/essential_index.js';
import {createNoticeSearchManager, createNoticeModalManager} from '/resources/front-end-assets/js/lime/page/board/notice_module.js';
import {NoticeCall as $NOTICE, FileCall as $FILE} from '/resources/front-end-assets/js/lime/ajax/ajax_index.js';

$(async () => {
	noticeRowRenderer.init();
	const noticeSearchManager = createNoticeSearchManager(noticeRowRenderer);
	noticeSearchManager.init({
		officeCode: officeCode,
		pageNo: 1
	});
	objectManager.setSearchManager(noticeSearchManager);
	const noticeModalManager = createNoticeModalManager(noticeSearchManager);
	objectManager.setModalManager(noticeModalManager);
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
const noticeBuilder = (notice) => {
	const $row = Util.createElement("div", "row");
	if(notice.fixYN == 'Y'){
		Util.addClass($row, "fixTop");
	}
	const $message = Util.createElement("div", "message");
	$row.appendChild($message);
	// 제목 칼럼
	const createMainColumn = () => {
		const $mainColumn = Util.createElement("div", "d-flex", "flex-column");

		const $officeColumn = Util.createElement("div", "mb-1");
		const $office = Util.createElement("span", "branch");
		$office.innerHTML = notice.officeName;
		$officeColumn.appendChild($office);
		$mainColumn.appendChild($officeColumn);
		
		const $titleColumn = Util.createElement("div");
		if(!Util.isEmpty(notice.fileList)){
			notice.fileList.forEach(file => file.fileKey = file.fileId);
			const $clipIcon = Util.createElement("i", "fas", "fa-paperclip", "mr-2");
			$titleColumn.appendChild($clipIcon);
		}
		const $title = Util.createTextNode(Util.unescape(notice.title));
		$titleColumn.appendChild($title);
		$mainColumn.appendChild($titleColumn);
		
		$message.appendChild($mainColumn);
	}
	// 등록일 칼럼
	const createRegDateColumn = () => {
		const $column = Util.createElement("span", "regDate");
		const regDateM = moment(notice.regDateTime);
		$column.innerHTML = regDateM.format("YYYY.MM.DD HH:mm");
		$message.appendChild($column);
	}
	$row.onclick = () => {
		objectManager.getModalManager().showViewModal(notice);
	}
	createMainColumn();
	createRegDateColumn();
	
	return $row;
}

const noticeRowRenderer = {
	init(){
		this.fixedNoticeListBox = Util.getElement("#fixedNoticeListBox");
		this.nonfixedNoticeListBox = Util.getElement("#nonfixedNoticeListBox");
		this.clearFixNotice();
		this.clearNonfixNotice();
	},
	clearFixNotice(){
		this.fixedNoticeListBox.innerHTML = "";
	},
	renderFixedNotices(noticeList){
		const $listBox = this.fixedNoticeListBox;
		for(const notice of noticeList){
			const $row = noticeBuilder(notice);
			$listBox.appendChild($row);
		}
		this.enableScrollPagination();
	},
	clearNonfixNotice(){
		this.nonfixedNoticeListBox.innerHTML = "";
	},
	renderNonFixedNotices(noticeList){
		const $listBox = this.nonfixedNoticeListBox;
		for(const notice of noticeList){
			const $row = noticeBuilder(notice);
			$listBox.appendChild($row);
		}
		this.enableScrollPagination();
	},
	enableScrollPagination(){
		const $listBox = Util.getElement("#noticeContainer");
		Util.setVerticalScroll($listBox, () => {
			this.disableScrollPagination();
			objectManager.getSearchManager().nonfixedPageMove();
		});
	},
	disableScrollPagination(){
		const $listBox = Util.getElement("#noticeContainer");
		$listBox.unsetVerticalScroll?.();
	}
}
