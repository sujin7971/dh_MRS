/**
 * 
 */
import {eventMixin, Util, Modal, Uploader} from '/resources/core-assets/essential_index.js';
import {Final} from '/resources/front-end-assets/js/lime/comm/module_index.js';

export default {
	__proto__: eventMixin,
	init(data = {}){
		const {
			editable = true,// 파일 목록 dom 스타일
			deletable = true,// 파일 삭제 가능 여부
			addConfirm = false,
			delConfirm = false,
			autoDisplay = true,
			sizeLimit = 314572800,
			uploadLimit = 10,
		} = data;
		this.editable = editable;
		this.addConfirm = addConfirm;
		this.delConfirm = delConfirm;
		this.deletable = deletable;
		this.sizeLimit = sizeLimit;
		this.uploadLimit = uploadLimit;
		this.oldList = [];
		this.newList = [];
		this.setBtnEvt();
		Uploader.on("added", async (data) => {
			const {
				files,
				size,
				count
			} = data;
			if(this.addConfirm){
				const fileNames = files.map(file => file.name);
				const res = await Modal.confirm({title:"파일 업로드", msg: "선택한 파일을 업로드하시겠습니까?", detail: fileNames});
				if(res != "OK"){
					return;
				}
			}
			const nowSize = Uploader.getFileSize(this.newList);
			const nowCount = this.newList.length;
			
			const totalSize = size + nowSize;
			const totalCount = count + nowCount;
			if(totalSize > this.sizeLimit){
				Modal.info({
					msg: "업로드 허용 용량을 초과했습니다"
				});
			}else if(totalCount > this.uploadLimit){
				Modal.info({
					msg: "업로드 허용 개수를 초과했습니다"
				});
			}else{
				this.newList.push(...files);
				this.setFileBox(files);
				this.setLimitInfo();
				this.trigger("added", {
					files: files
				});
			}
		});
		return this;
	},
	update(data = {}){
		const {
			editable = this.editable,
			deletable = this.deletable,
			delConfirm = this.delConfirm,
			sizeLimit = this.sizeLimit,
			uploadLimit = this.uploadLimit,
		} = data;
		this.editable = editable;
		this.delConfirm = delConfirm;
		this.deletable = deletable;
		this.sizeLimit = sizeLimit;
		this.uploadLimit = uploadLimit;
		return this;
	},
	setBtnEvt(){
		const setAllChk = ($chk) => {
			this.switchAllChk = () => {
				const chkList = this.getCheckedFileList();
				if(chkList.length == 0){
					$chk.checked = false;
				}
			}
			$chk.onchange = () => {
				if($chk.checked == true){
					this.checkAll();
				}else{
					this.uncheckAll();
				}
				this.validDownBtn?.();
				this.validDelBtn?.();
			}
		}
		const setDelBtnEvt = ($btn) => {
			this.validDelBtn = () => {
				const bool = this.isSelectedDeletable();
				$btn.disabled = !(bool);
			}
			$btn.onclick = () => {
				const selectedFileList = this.getCheckedFileList();
				console.log("delete", selectedFileList);
			}
		}
		// 전체 삭제 버튼 이벤트 등록
		const setDelAllBtnEvt = ($btn) => {
			$btn.onclick = () => {
				this.clearFiles();
			}
		}
		// 전체 삭제 버튼 이벤트 등록
		const setUploadBtnEvt = ($btn) => {
			$btn.onclick = () => {
				Uploader.fileInput();
			}
		}
		// 전체 선택 체크박스 이벤트 등록
		const $allChk = Util.getElement("#allChk");
		if($allChk){
			setAllChk($allChk);
		}
		// 파일 삭제 이벤트 등록
		const $fileDelBtn = Util.getElement("#fileDelBtn");
		if($fileDelBtn){
			$fileDelBtn.disabled = true;
			setDelBtnEvt($fileDelBtn);
		}
		// 파일 전체삭제 이벤트 등록
		const $fileDeleteAll = Util.getElement("#fileDeleteAll");
		if($fileDeleteAll){
			setDelAllBtnEvt($fileDeleteAll);
		}
		// 파일 업로드 이벤트 등록
		const $uploadBtn = Util.getElement("#uploadBtn");
		if($uploadBtn){
			setUploadBtnEvt($uploadBtn);
		}
	},
	//업로드 개수, 용량 제한 표시
	setLimitInfo(){
		if(this.editable == false){
			return ;
		}
		const nowSize = Uploader.getFileSize(this.newList);
		const nowCount = this.newList.length;
		const $sizeInput = Util.getElement("#fileSize");
		const $countInput = Util.getElement("#fileCount");
		if($sizeInput){
			$sizeInput.innerHTML = Util.getReadableFileSize(nowSize);
		}
		if($countInput){
			$countInput.innerHTML = nowCount + " 개";
		}
	},
	setFiles(files){
		if(files){
			this.oldList = [...files];
			this.newList = [...files];
			this.clearFileBox();
			this.setFileBox(files);
			this.setLimitInfo();
		}
	},
	// 파일 표시
	setFileBox(files){
		//state: [deletable, downloadable, viewable]
		//수정 페이지 파일 노드
		const editableElemGenerator = (file) => {
			const fileMetadata = Final.getFileMetadata(file);
			const $li = Util.createElement("li");
			if(file.isNew){
				const $badge = Util.createElement("span", "badge", "bg-info", "mr-1");
				$badge.innerHTML = "NEW";
				$li.appendChild($badge);
			}else{
				if(file.roleType){
					const $iconDiv = Util.createElement("span", "my-auto", "mr-2");
					const $icon = Util.createElement("i", ...fileMetadata.icon);
					$iconDiv.appendChild($icon);
					$li.appendChild($iconDiv);
				}
			}
			
			const $a = Util.createElement("a");
			$a.innerHTML = (file.originalName)?file.originalName:file.uploadedFileName;
			
			$li.appendChild($a);
			if(this.deletable == true){
				const $del = Util.createElement("span", "btn-del", "ml-1");
				$del.setAttribute("title", "삭제");
				$a.appendChild($del);
				
				$del.onclick = async () => {
					if(this.delConfirm){
						const res = await Modal.confirm({title:"파일 삭제", msg: "해당 파일을 삭제합니다.", detail: ["삭제된 파일에 대한 참석자들의 판서 내용도 함께 삭제됩니다."], delMode: true});
						if(res != "DELETE"){
							return;
						}
					}
					$li.remove();
					const fileId = file.fileId;
					this.deleteFiles(fileId);
					this.trigger("deleted", {
						files: [fileId],
					});
				}
			}
			return $li;
		}
		//보기 페이지 파일 노드
		const viewElemGenerator = (file) => {
			const $tr = Util.createElement("tr");
			//파일 상태(-2: 만료, 1-: 변환실패, 0: 대기, 1:변환진행, 2:정상)
			const {
				icon,
				elementClass,
				deletable,
				downloadable,
				viewable,
			} = Final.getFileMetadata(file);
			$tr.deletable = deletable;
			$tr.downloadable = downloadable;
			$tr.viewable = viewable;
			const $chkTd = Util.createElement("td", "text-align-center");
			$tr.appendChild($chkTd);
			const $chk = Util.createElement("input");
			$chk.type = "checkbox";
			
			$chkTd.appendChild($chk);
			
			const $typeTd = Util.createElement("td", "text-align-center", elementClass);
			$tr.appendChild($typeTd);
			const $typeDiv = Util.createElement("div");
			$typeTd.appendChild($typeDiv);
			const $tipDiv = Util.createElement("div", "tip");
			$typeDiv.appendChild($tipDiv);
			
			const $nameTd = Util.createElement("td", "text-align-left");
			$tr.appendChild($nameTd);
			const $nameDiv = Util.createElement("div", "d-flex", "flex-row");
			$nameTd.appendChild($nameDiv);
			
			const $iconDiv = Util.createElement("span", "my-auto", "mr-2");
			const $icon = Util.createElement("i", ...icon);
			$iconDiv.appendChild($icon);
			$nameDiv.appendChild($iconDiv);
			const $nameSpan = Util.createElement("span");
			$nameSpan.innerHTML = file.uploadedFileName;
			$nameDiv.appendChild($nameSpan);
			
			if($tr.viewable == true){
				Util.addClass($nameTd, "cursor-pointer");
				$nameTd.onclick = () =>{
					this.trigger("click", {
						fileCategory: file.fileCategory,
						fileId: file.fileId,
					});
				}
			}else{
				Util.addClass($nameTd, "text-muted");
			}
			
			$tr.getFile = () => {
				return file;
			}
			$tr.isChecked = () => {
				return $chk.checked;
			}
			$tr.check = () => {
				$chk.checked = true;
			}
			$tr.uncheck = () => {
				$chk.checked = false;
			}
			$chk.onchange = () => {
				this.switchAllChk?.();
				this.validDownBtn?.();
				this.validDelBtn?.();
			}
			return $tr;
		}
		
		const $fileBox = Util.getElement("#fileContainer");// 현재 업로드된 파일을 표시할 노드
		files.forEach(file => {
			const $elem = (this.editable)?editableElemGenerator(file):viewElemGenerator(file);
			$fileBox.appendChild($elem);
		});
	},
	// 파일 박스 노드 비움
	clearFileBox(){
		const $fileBox = Util.getElement("#fileContainer");
		$fileBox.innerHTML = "";
	},
	// 파일 전체 선택
	checkAll(){
		const $fileBox = Util.getElement("#fileContainer");
		const children = $fileBox.children;
		Array.from(children).forEach($child => {
			$child.check();
		});
	},
	// 파일 선택 모두 해제
	uncheckAll(){
		const $fileBox = Util.getElement("#fileContainer");
		const children = $fileBox.children;
		Array.from(children).forEach($child => {
			$child.uncheck();
		});
	},
	// 선택된 파일 조회
	getCheckedFileList() {
		const $fileBox = Util.getElement("#fileContainer");
		const keyList = [];
		const children = $fileBox.children;
		return Array.from(children).filter($child => $child.isChecked()).map($child => $child.getFile());
	},
	// 선택된 파일 목록이 다운로드 가능한지 판별
	isSelectedDowloadable(){
		const $fileBox = Util.getElement("#fileContainer");
		const children = $fileBox.children;
		const $chkList = Array.from(children).filter($child => $child.isChecked());
		if($chkList.length == 0){
			return false;
		}
		return $chkList.reduce((downloadable, $child) => downloadable && $child.downloadable, true);
	},
	// 선택된 파일 목록이 삭제 가능한지 판별
	isSelectedDeletable(){
		const $fileBox = Util.getElement("#fileContainer");
		const children = $fileBox.children;
		const $chkList = Array.from(children).filter($child => $child.isChecked());
		if($chkList.length == 0){
			return false;
		}
		return $chkList.reduce((deletable, $child) => deletable && $child.deletable, true);
	},
	deleteFiles(...keys){
		this.newList = this.newList.filter(file => !keys.includes(file.fileId));
		this.setLimitInfo();
	},
	clearFiles(){
		this.newList = [];
		this.setLimitInfo();
		this.clearFileBox();
	},
	// 추가된 파일 목록 요청
	getAddedFiles(){
		const oldListKeys = this.oldList.map(file => file.fileId);
		return this.newList.filter(file => !oldListKeys.includes(file.fileId));
	},
	// 삭제된 파일키 목록 요청
	getDeletedFiles(){
		const newListKeys = this.newList.map(file => file.fileId);
		return this.oldList.filter(file => !newListKeys.includes(file.fileId)).map(file => file.fileId);
	},
}