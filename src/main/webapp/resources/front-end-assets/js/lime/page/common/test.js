/**
 * 
 */
import {eventMixin, Util, Modal, AjaxBuilder} from '/resources/core-assets/essential_index.js';
import {SearchBar} from '/resources/front-end-assets/js/lime/comm/module_index.js';
import {Final, FileManager} from '/resources/front-end-assets/js/lime/comm/module_index.js';
import {NoticeCall as $NOTICE, AdminCall as $ADM, FileCall as $FILE} from '/resources/front-end-assets/js/lime/ajax/ajax_index.js';

$(async () => {
	noticeRowGenerator.init();
	searchHandler.init({
		officeCode: officeCode,
		startDate: startDate,
		endDate: endDate,
		pageNo: pageNo
	});
	domHandler.init();
	evtHandler.init();
});

const domHandler = {
	init(){
		
	},
	
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
			noticeModalHandler.showAddModal();
		}
	},
	setFileUploadBtn(){
		FileManager.init();
	},
}

const searchHandler = {
	init(data = {}){
		const {
			officeCode = 0,
			title,
			startDate ="",
			endDate ="",
			pageNo = 1,
			pageCnt = 10,
		} = data;
		this.pageNo = 1;
		this.pageCnt = 10;
		this.pagination = new Pagination({
		    container: $("#pagination"),
		    maxVisibleElements: 9,
		    enhancedMode: false,
		    pageClickCallback: (page) => {
		    	this.nonfixedPageMove(page);
		    }
		});
		
		SearchBar.init({
			officeCode: officeCode,
			title: title,
			startDate: startDate,
			endDate: endDate,
		});
		SearchBar.initOffice();
		SearchBar.initTitle();
		SearchBar.initPeriodPicker();
		const nowDateM = moment();
		SearchBar.enableResetBtn({
			startDate: "",
			endDate: "",
		});
		SearchBar.on("search", (data = {}) => {
			this.search(data);
		});
		SearchBar.on("reset", (data = {}) => {
			this.reset(data);
		});
		this.search(SearchBar.getSearchInput());
	},
	reset(data){
		this.pageNo = 1;
		this.search(data);
	},
	async search(data = {}){
		const {
			officeCode = this.officeCode,
			title = this.title,
			startDate = this.startDate,
			endDate = this.endDate,
		} = data;
		this.officeCode = officeCode;
		this.title = title;
		this.startDate = startDate;
		this.endDate = endDate;
		this.totalCnt = null;
		await this.loadFixedPage();
		await this.loadNonfixedPage();
	},
	async loadFixedPage(){
		const showList = async () => {
			await AjaxBuilder({
				request: await $NOTICE.Get.noticeList,
				param: {
					officeCode: this.officeCode,
					title: this.title,
					fixYN: 'Y',
					startDate: this.startDate,
					endDate: this.endDate,
				}
			}).success(list => {
				noticeRowGenerator.generateFixNotice(list);
			}).exe();
		}
		await showList();
	},
	async loadNonfixedPage(){
		const setPagination = async () =>{
			let result = true;
			//noticeRowGenerator.clear();
			$("#pagination").empty();
			await AjaxBuilder({
				request: $NOTICE.Get.noticeListCnt,
				param: {
					officeCode: this.officeCode,
					title: this.title,
					fixYN: 'N',
					startDate: this.startDate,
					endDate: this.endDate,
					pageNo: this.pageNo,
					pageCnt: this.pageCnt,
				},
			}).success(cnt => {
				this.totalCnt = cnt;
				this.pageNo = 1;
				this.pagination.make(this.totalCnt, this.pageCnt);
			}).error(() => {
				this.pageNo = null;
				result = false;
			}).exe();
			return result;
		}
		const showList = async () => {
			await AjaxBuilder({
				request: await $NOTICE.Get.noticeList,
				param: {
					officeCode: this.officeCode,
					title: this.title,
					fixYN: 'N',
					startDate: this.startDate,
					endDate: this.endDate,
					pageNo: this.pageNo,
					pageCnt: this.pageCnt,
				}
			}).success(list => {
				noticeRowGenerator.generateNonfixNotice(list);
			}).exe();
		}
		if(!this.totalCnt){
			const result = await setPagination();
			if(!result){
				return;
			}
		}
		await showList();
	},
	async nonfixedPageMove(pageNo){
		this.pageNo = pageNo;
		await this.loadNonfixedPage();
	},
}
const noticeBuilder = (notice) => {
	console.log("notice", notice)
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
		$column.innerHTML = notice.title;
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
		noticeModalHandler.showUpdateModal(notice);
	}
	createNoColumn();
	createOfficeColumn();
	createTitleColumn();
	createRegDateColumn();
	
	return $row;
}

const noticeRowGenerator = {
	init(){
		this.fixedNoticeListBox = Util.getElement("#fixedNoticeListBox");
		this.nonfixedNoticeListBox = Util.getElement("#nonfixedNoticeListBox");
	},
	clearFixNotice(){
		this.fixedNoticeListBox.innerHTML = "";
	},
	generateFixNotice(noticeList){
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
	generateNonfixNotice(noticeList){
		this.clearNonfixNotice();
		const $listBox = this.nonfixedNoticeListBox;
		for(const notice of noticeList){
			const $row = noticeBuilder(notice);
			$listBox.appendChild($row);
		}
	},
}

const noticeModalHandler = {
	init(){
		const $modal = Modal.get("boardModal");
		const $closeBtn = $modal.querySelector("[data-modal-btn=CLOSE]");
		const $saveBtn = $modal.querySelector("[data-modal-btn=SAVE]");
		const $deleteBtn = $modal.querySelector("[data-modal-btn=DELETE]");
		
		const $officeInput = $modal.querySelector("select[name='officeCode']");
		const $fixYNInput = $modal.querySelector("input[name='fixYN']");
		const $titleInput = $modal.querySelector("input[name='title']");
		const $contentsInput = $modal.querySelector("textarea[name='contents']");
		const createFormData = () => {
			const formData = new FormData();
			const officeCode = $officeInput.value;
			const fixYN = $fixYNInput.checked ? "Y" : "N";
			const title = $titleInput.value;
			const contents = $contentsInput.value;
			if (!title) throw new Error("제목을 입력해 주세요");
			if (!contents) throw new Error("내용을 입력해 주세요");
			formData.append("officeCode", officeCode);
		  	formData.append("fixYN", fixYN);
		  	formData.append("title", title);
		  	formData.append("contents", contents);

		  	return formData;
		}
		const setFormData = (notice) => {
			$officeInput.value = notice.officeCode;
			$fixYNInput.checked = (notice.fixYN == 'Y')?true:false;
			$titleInput.value = notice.title;
			$contentsInput.value = notice.contents;
		}
		const initFormData = () => {
			$officeInput.children[0].selected = true;
			$fixYNInput.checked = false;
			$titleInput.value = "";
			$contentsInput.value = "";
		}
		const handleFormSubmit = async (request, noticeId = null) => {
			try {
			    const formData = createFormData();
			    const requestOptions = {
					request: request,
				    param: { noticeId, formData },
				    exception: "success-only",
			    };
			    const createForm = (fileList) => {
					const formData = new FormData;
					fileList.forEach(file => {
						formData.append("list", file);
					});
					return formData;
				}
			    AjaxBuilder(requestOptions).success(async (res) => {
			    	const resourceId = (noticeId)?noticeId:res.data;
			    	const fileList = FileManager.getAddedFiles();
			    	if(!Util.isEmpty(fileList)){
			    		const fileUploadRes = await $FILE.Post.fileList({
			    			fileList: createForm(fileList)
			    		});
			    		console.log("파일 업로드 결과", fileUploadRes);
			    		const successIdList = fileUploadRes.data;
			    		const registResult = await AjaxBuilder({request: $NOTICE.Post.fileList, param: {noticeId: resourceId, fileIdList: successIdList}}).exe();
			    		console.log("파일 등록 결과", registResult);
			    	}
			    	await Modal.info({ msg: res.message });
			        $modal.close();
			        searchHandler.reset();
			    }).error(async (err) => {
			    	Modal.error({response: err});
			    }).exe();
			} catch (error) {
			    console.error("Form submission error:", error);
			    if (error.message) {
			    	Modal.info({msg: error.message});
			      //alert(`Please provide input for ${error.message}`);
			    }
			}
		}
		$modal.switchAddMode = () => {
			$deleteBtn.style.display = "none";
			$saveBtn.innerHTML = "작 성";
			initFormData();
			$saveBtn.onclick = async () => {
				handleFormSubmit($NOTICE.Post.noticeOne);
			}
		}
		$modal.switchUpdateMode = (notice) => {
			console.log("switchUpdateMode", notice);
			$deleteBtn.style.display = "";
			$saveBtn.innerHTML = "수 정";
			setFormData(notice);
			$saveBtn.onclick = async () => {
				handleFormSubmit($NOTICE.Put.noticeOne, notice.noticeId);
			}
		}
		return $modal;
	},
	get(){
		if(!this.modal){
			this.modal = this.init();
		}
		return this.modal;
	},
	showAddModal(){
		const $modal = this.get();
		$modal.switchAddMode();
		$modal.show();
	},
	showUpdateModal(notice){
		const $modal = this.get();
		$modal.switchUpdateMode(notice);
		$modal.show();
	},
}