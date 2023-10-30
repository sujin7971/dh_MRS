/**
 * 
 */
import {eventMixin, Util, Modal, AjaxBuilder} from '/resources/core-assets/essential_index.js';
import {SearchBar} from '/resources/front-end-assets/js/ewp/comm/module_index.js';
import {Final, FileManager} from '/resources/front-end-assets/js/ewp/comm/module_index.js';
import {NoticeCall as $NOTICE, AdminCall as $ADM, FileCall as $FILE} from '/resources/front-end-assets/js/ewp/comm/ajax_index.js';


export const createNoticeSearchManager = (noticeRowRenderer) => {
	return {
		init(data = {}){
			const {
				officeCode = 0,
				title,
				startDate ="",
				endDate ="",
				pageNo = 1,
				pageCnt = 10,
				pagination = false,
			} = data;
			this.pageNo = 1;
			this.pageCnt = 10;
			this.pagination = pagination;
			if(this.pagination){
				this.pagingRenderer = new Pagination({
					container: $("#pagination"),
					maxVisibleElements: 9,
					enhancedMode: false,
					pageClickCallback: (page) => {
						this.nonfixedPageMove(page);
					}
				});
			}
			
			SearchBar.init({
				officeCode: officeCode,
				title: title,
				startDate: startDate,
				endDate: endDate,
			});
			SearchBar.initOffice();
			SearchBar.initTitle();
			/*SearchBar.initPeriodPicker();*/
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
					noticeRowRenderer.renderFixedNotices(list);
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
					this.pagingRenderer?.make(this.totalCnt, this.pageCnt);
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
					noticeRowRenderer.renderNonFixedNotices(list);
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
		async nonfixedPageMove(pageNo = this.pageNo + 1){
			this.pageNo = pageNo;
			await this.loadNonfixedPage();
		},
	}
}

export const createNoticeModalManager = (noticeSearchManager) => {
	return {
		initViewModal(){
			const $viewModal = Modal.get("noticeViewModal");
			
			const $officeInput = $viewModal.querySelector("#office");
			const $titleInput = $viewModal.querySelector("#title");
			const $contentsInput = $viewModal.querySelector("#contents");
			
			const $fileViewBox = $viewModal.querySelector("#fileViewBox");
			const $writerViewBox = $viewModal.querySelector("#writerViewBox");
			
			const $updateBtn = $viewModal.querySelector("[data-modal-btn=UPDATE]");
			const initFileViewBox = () => {
				$fileViewBox.show = () => {
					$fileViewBox.style.display = "";
				}
				$fileViewBox.hide = () => {
					$fileViewBox.style.display = "none";
				}
				$fileViewBox.setFileCount = (count) => {
					const $fileCount = $fileViewBox.querySelector("#attachedCount");
					$fileCount.innerHTML = count + "개";
				}
				$fileViewBox.clearFile = () => {
					const $listGroup = $fileViewBox.querySelector("ul");
					$listGroup.innerHTML = "";
				}
				$fileViewBox.addFile = (file) => {
					const $listGroup = $fileViewBox.querySelector("ul");
					const $li = Util.createElement("li", "list-group-item", "d-flex");
					const $nameNode = Util.createElement("span");
					$nameNode.innerHTML = file.fileLabel + "." + file.fileExt;
					$li.appendChild($nameNode);
					
					const $sizeNode = Util.createElement("span", "ml-2", "fw-light");
					$sizeNode.innerHTML = "("+Util.getReadableFileSize(file.fileSize)+")";
					$li.appendChild($sizeNode);
					if(loginType == "SSO"){
						const $downBtn = Util.createElement("i", "fas", "fa-download", "cursor-pointer", "ml-auto");
						$downBtn.title = "다운로드";
						$downBtn.onclick = () => {
							const fileId = file.fileId;
							$FILE.Get.downloadOne(fileId);
						}
						$li.appendChild($downBtn);
					}
					
					$listGroup.appendChild($li);
					if(file.pdfGeneratedYN == 'Y' || file.webpGeneratedYN == 'Y'){
						Util.addClass($nameNode, "href-link");
						$nameNode.onclick = () => {
							const fileId = file.fileId;
							const fileCategory = file.fileCategory
							$FILE.Get.viewFileOne({
								fileId: fileId,
								fileCategory: fileCategory
							});
						}
					}else{
						Util.addClass($nameNode, "text-muted");
					}
				}
			}
			const initWriterViewBox = () => {
				const $writerData = Util.createElement("span", "mx-1");
				$writerViewBox.appendChild($writerData);
				const $writerName = Util.createElement("span", "mx-1");
				$writerViewBox.appendChild($writerName);
				const $writerDeskPhone = Util.createElement("span", "mx-1", "interphone");
				$writerViewBox.appendChild($writerDeskPhone);
				$writerViewBox.setWriteDate = (writeDate) => {
					$writerData.innerHTML = moment(writeDate).format("YYYY.MM.DD");
				}
				$writerViewBox.setWriterName = (writeName) => {
					$writerName.innerHTML = writeName;
				}
				$writerViewBox.setWriterDeskPhone = (phone) => {
					$writerDeskPhone.innerHTML = phone;
				}
			}
			initFileViewBox();
			initWriterViewBox();
			$viewModal.showFormData = (notice) => {
				$viewModal.getFormData = () => {
					return notice;
				}
				const setBasicInfo = () => {
					$fileViewBox.style.display = "";
					$officeInput.innerHTML = notice.officeName;
					$titleInput.innerText = Util.unescape(notice.title);
					$contentsInput.innerText = Util.unescape(notice.contents);
				}
				const setAttachedFileInfo = () => {
					$fileViewBox.clearFile();
					if(!Util.isEmpty(notice.fileList)){
						const fileList = notice.fileList;
						$fileViewBox.setFileCount(fileList.length);
						fileList.forEach(file => {
							$fileViewBox.addFile(file);
						});
						$fileViewBox.show();
					}else{
						$fileViewBox.hide();
					}
				}
				const setWriterInfo = () => {
					$writerViewBox.setWriteDate(notice.regDateTime);
					$writerViewBox.setWriterName(notice.writer?.nameplate);
					$writerViewBox.setWriterDeskPhone(notice.writer?.officeDeskPhone);
				}
				setBasicInfo();
				setAttachedFileInfo();
				setWriterInfo();
				if($updateBtn){
					if(notice.writerId == loginKey){
						$updateBtn.style.display = "";
					}else{
						$updateBtn.style.display = "none";
					}
				}
				//위와 같이 수정 버튼에 이벤트를 등록하는경우 단 한번만 이벤트가 트리거됨
				/*$viewModal.onclick = (event) => {
					if (event.target === $updateBtn) {
						this.showUpdateModal(notice);
					}
				};*/
			}
			
			$updateBtn?.addEventListener('click', () => {
				const notice = $viewModal.getFormData();
				this.showUpdateModal(notice);
			});
			return $viewModal;
		},
		initWriteModal(){
			const $writeModal = Modal.get("noticeWriteModal");
			const $closeBtn = $writeModal.querySelector("[data-modal-btn=CLOSE]");
			const $saveBtn = $writeModal.querySelector("[data-modal-btn=SAVE]");
			const $deleteBtn = $writeModal.querySelector("[data-modal-btn=DELETE]");
			
			const $officeInput = $writeModal.querySelector("select[name='officeCode']");
			const $fixYNInput = $writeModal.querySelector("input[name='fixYN']");
			const $titleInput = $writeModal.querySelector("input[name='title']");
			const $contentsInput = $writeModal.querySelector("textarea[name='contents']");
			
			const $fileUploadInfoBox = $writeModal.querySelector("#fileUploadInfoBox");
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
				$titleInput.value = Util.unescape(notice.title);
				$contentsInput.value = Util.unescape(notice.contents);
				FileManager.setFiles(notice.fileList);
			}
			const initFormData = () => {
				$officeInput.children[0].selected = true;
				$fixYNInput.checked = false;
				$titleInput.value = "";
				$contentsInput.value = "";
				FileManager.clearFiles();
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
				    	const uploadFileList = FileManager.getAddedFiles();
				    	if(!Util.isEmpty(uploadFileList)){
				    		const fileUploadRes = await $FILE.Post.fileList({
				    			fileList: createForm(uploadFileList)
				    		});
				    		const successIdList = fileUploadRes.data;
				    		const registResult = await AjaxBuilder({request: $NOTICE.Post.fileList, param: {noticeId: resourceId, fileIdList: successIdList}}).exe();
				    	}
				    	const deleteFileList = FileManager.getDeletedFiles();
				    	if(!Util.isEmpty(deleteFileList)){
				    		const fileUploadRes = await AjaxBuilder({request: $NOTICE.Delete.fileList, param: {noticeId: resourceId, fileIdList: deleteFileList}}).exe();
				    	}
				    	await Modal.info({ msg: res.message });
				        $writeModal.close();
				        noticeSearchManager.reset?.();
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
			$writeModal.switchAddMode = () => {
				$deleteBtn.style.display = "none";
				$saveBtn.style.display = "";
				$saveBtn.innerHTML = "작 성";
				initFormData();
				$saveBtn.onclick = async () => {
					handleFormSubmit($NOTICE.Post.noticeOne);
				}
			}
			$writeModal.switchUpdateMode = (notice) => {
				$deleteBtn.style.display = "";
				$saveBtn.style.display = "";
				$saveBtn.innerHTML = "저 장";
				setFormData(notice);
				$saveBtn.onclick = async () => {
					handleFormSubmit($NOTICE.Put.noticeOne, notice.noticeId);
				}
				$deleteBtn.onclick = async () => {
					const reply = await Modal.confirm({msg: "공지사항을 삭제하시겠습니까?", delMode: true});
					if(reply != "DELETE"){
						return;
					}
					AjaxBuilder({
						request: $NOTICE.Delete.noticeOne,
						param: notice.noticeId,
						exception: "success-only",
					}).success(async (res) => {
				    	await Modal.info({ msg: res.message });
				        $writeModal.close();
				        noticeSearchManager.reset?.();
				    }).error(async (err) => {
				    	Modal.error({response: err});
				    }).exe();
				}
			}
			return $writeModal;
		},
		getViewModal(){
			if(!this.viewModal){
				this.viewModal = this.initViewModal();
			}
			return this.viewModal;
		},
		getWriteModal(){
			if(!this.writeModal){
				this.writeModal = this.initWriteModal();
			}
			return this.writeModal;
		},
		showViewModal(notice){
			const $viewModal = this.getViewModal();
			$viewModal.showFormData(notice);
			$viewModal.show();
		},
		showAddModal(){
			const $writeModal = this.getWriteModal();
			$writeModal.switchAddMode();
			$writeModal.show();
		},
		showUpdateModal(notice){
			const $writeModal = this.getWriteModal();
			$writeModal.switchUpdateMode(notice);
			$writeModal.show();
		},
	}
}
