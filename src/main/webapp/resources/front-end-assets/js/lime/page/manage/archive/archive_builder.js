/**
 * 
 */

import {eventMixin, Util, Modal} from '/resources/core-assets/essential_index.js';
import {Final} from '/resources/front-end-assets/js/lime/comm/module_index.js';
import {MeetingCall as $MEETING} from '/resources/front-end-assets/js/lime/ajax/ajax_index.js';

/**
 * 파일함 페이지 버튼 제어
 */
export const archiveEvtHandler = {
	__proto__: eventMixin,
	init(){
		this.enableCheckboxBtn();
		this.setFileBtn();
	},
	enableCheckboxBtn(){// 
		const $checkAll = Util.getElement("#chckall");
		if($checkAll){
			const $listBox = Util.getElement("#listBox");
			$checkAll.onchange = () => {
				if($checkAll.checked == true){
					const $rowList = $listBox.children;
					Array.from($rowList).forEach($child => {
						$child.checkAll();
					});
				}else{
					const $rowList = $listBox.children;
					Array.from($rowList).forEach($child => {
						$child.uncheckAll();
					});
				}
				this.validFileBtn();
			}
		}
	},
	setFileBtn(){
		const $listBox = Util.getElement("#listBox");
		
		const $downBtn = Util.getElement("#fileDownBtn");
		if($downBtn){
			$downBtn.onclick = () => {
				const $rowList = $listBox.children;
				const selectedFileList = Array.from($rowList).flatMap($row => $row.getCheckedFileList());
				$MEETING.Get.downloadFileList(selectedFileList.map(file => file.fileId));
				Array.from($rowList).forEach($row => $row.uncheckAll());
			}
		}
		const $delBtn = Util.getElement("#fileDelBtn");
		if($delBtn){
			$delBtn.onclick = async () => {
				const reply = await Modal.confirm({
					msg: "선택한 파일을 삭제하시겠습니까?<br>삭제한 파일은 복구할 수 없습니다."
				});
				if(reply == "OK"){
					const $rowList = $listBox.children;
					const selectedFileList = Array.from($rowList).flatMap($row => $row.getCheckedFileList());
					
					Modal.startLoading();
					try{
						const delGroup = selectedFileList.reduce((acc, curr) => {
							const {
								meetingId
							} = curr;
							if(acc[meetingId]){
								acc[meetingId].push(curr);
							}else{
								acc[meetingId] = [curr];
							}
							return acc;
						}, {});
						for (const [key, values] of Object.entries(delGroup)) {
							const delRes = await $MEETING.Delete.meetingFileList({
								meetingId: key,
								fileList: values.map(file => file.fileId)
							});
							if(delRes.status == 200){
								selectedFileList.forEach(file => file.node.remove());
							}else{
								throw delRes;
							}
						}
					}catch(err){
						console.error("err", err);
						Modal.error({response: err});
					}finally{
						Modal.endLoading();
					}
				}
			}
		}
	},
	validFileBtn(){
		const $listBox = Util.getElement("#listBox");
		const $rowList = $listBox.children;
		const $hasCheckedFileRowList = Array.from($rowList).filter($row => $row.hasCheckedFile());
		
		let isDownloadable = false;
		let isDeletable = false;
		if($hasCheckedFileRowList.length != 0){
			isDownloadable = $hasCheckedFileRowList.reduce((downloadable, $row) => downloadable && $row.isSelectedDowloadable(), true);
			isDeletable = $hasCheckedFileRowList.reduce((deletable, $row) => deletable && $row.isSelectedDeletable(), true);
		}
		const $downBtn = Util.getElement("#fileDownBtn");
		if($downBtn){
			if(isDownloadable){
				$downBtn.disabled = false;
			}else{
				$downBtn.disabled = true;
			}
		}
		
		
		const $delBtn = Util.getElement("#fileDelBtn");
		if($delBtn){
			if(isDeletable){
				$delBtn.disabled = false;
			}else{
				$delBtn.disabled = true;
			}
		}
	},
	enableScrollPagination(){
		const $listBox = Util.getElement(".fileListDiv");
		let prevScroll = 0;
		$listBox.onscroll = async () => {
			const scrollHeight = $listBox.scrollHeight;
			const maxScrollY = $listBox.offsetHeight;
			const currentScrollY = $listBox.scrollTop;
	        if(maxScrollY + currentScrollY >= scrollHeight && (currentScrollY - prevScroll) > 0 ){
	        	this.trigger("scrollBottom");
	        }
	        prevScroll = currentScrollY;
		}
	},
	disableScrollPagination(){
		const $listBox = Util.getElement(".fileListDiv");
		$listBox.onscroll = "";
	}
}
/**
 * 파일함 테이블 로우($row) 생성하여 테이블 노드에 추가.
 * 각 회의 로우 별 보유한 파일에 대한 선택을 제어할 접근 함수 제공
 * 
 * $row.checkAll() : 해당 회의에 속한 모든 파일 선택. 다운로드와 삭제 모두 불가능한 파일은 선택 불가
 * $row.uncheckAll() : 해당 회의에 속한 모든 파일에 대한 선택 취소. 다운로드와 삭제 모두 불가능한 파일은 선택 불가
 * $row.getCheckedFileList() return [{fileId: 1, originalName: abcd...}, ...] : 선택된 모든 파일 객체리스트 반환
 * 
 * 각 체크박스에 이벤트가 발생 할 때 마다 archiveEvtHandler의 validDownBtn()과 validDeleteBtn() 호출.
 */
export const archiveRowGenerator = {
	clear(){
		const $listBox = Util.getElement("#listBox");
		$listBox.innerHTML = "";
	},
	getContainer(){
		const $listBox = Util.getElement("#listBox");
		return $listBox;
	},
	generate(archiveList){
		const $listBox = Util.getElement("#listBox");
		for(const archive of archiveList){
			const $row = this.createRow(archive);
			$listBox.appendChild($row);
		}
		archiveEvtHandler.validFileBtn?.();
	},
	createRow(archive){
		const $row = Util.createElement("div", "row");
		//번호 칼럼
		const createNoColumn = () => {
			const $column = Util.createElement("div", "item", "no");
			$column.innerHTML = archive.scheduleId;
			
			$row.appendChild($column);
		}
		//해당 회의 모든 파일 대상 체크박스 칼럼
		const createCheckboxColumn = () => {
			const $column = Util.createElement("div", "item", "checkAll");
			const $checkbox = Util.createElement("input");
			$checkbox.type = "checkbox";
			$column.appendChild($checkbox);
			
			$row.appendChild($column);
			
			$checkbox.onchange = () => {
				const $fileNode = $row.getFileNode();
				const $fileRowList = Array.from($fileNode.children);
				if($checkbox.checked == true){
					$fileRowList.forEach($child => {
						$child.check();
					});
				}else{
					$fileRowList.forEach($child => {
						$child.uncheck();
					});
				}
				archiveEvtHandler.validFileBtn?.();
			}
			$row.switchAllChk = () => {
				const $fileNode = $row.getFileNode();
				
			}
			$row.checkAll = () => {
				$checkbox.checked = true;
				const $fileNode = $row.getFileNode();
				const $fileRowList = Array.from($fileNode.children);
				$fileRowList.forEach($child => {
					$child.check();
				});
			}
			$row.uncheckAll = () => {
				$checkbox.checked = false;
				const $fileNode = $row.getFileNode();
				const $fileRowList = Array.from($fileNode.children);
				$fileRowList.forEach($child => {
					$child.uncheck();
				});
			}
			// 선택된 파일 조회
			$row.getCheckedFileList = () => {
				const keyList = [];
				const $fileNode = $row.getFileNode();
				const $fileRowList = Array.from($fileNode.children);
				return $fileRowList.filter($child => $child.isChecked()).map($child => $child.getFile());
			}
			// 선택된 파일 존재 여부 확인
			$row.hasCheckedFile = () => {
				const $fileNode = $row.getFileNode();
				const $fileRowList = Array.from($fileNode.children);
				const $chkList = $fileRowList.filter($child => $child.isChecked());
				if($chkList.length == 0){
					return false;
				}else{
					return true;
				}
			}
			// 선택된 파일 목록이 다운로드 가능한지 판별
			$row.isSelectedDowloadable = () => {
				const $fileNode = $row.getFileNode();
				const $fileRowList = Array.from($fileNode.children);
				const $chkList = $fileRowList.filter($child => $child.isChecked());
				if($chkList.length == 0){
					return false;
				}
				return $chkList.reduce((downloadable, $child) => downloadable && $child.downloadable, true);
			}
			// 선택된 파일 목록이 삭제 가능한지 판별
			$row.isSelectedDeletable = () => {
				const $fileNode = $row.getFileNode();
				const $fileRowList = Array.from($fileNode.children);
				const $chkList = $fileRowList.filter($child => $child.isChecked());
				if($chkList.length == 0){
					return false;
				}
				if(pageType == 'admin'){
					return true;
				}
				return $chkList.reduce((deletable, $child) => deletable && $child.deletable, true);
			}
		}
		// 회의일시 칼럼
		const createScheduleColumn = () => {
			const $column = Util.createElement("div", "item", "dateTime");
			
			const $dateSpan = Util.createElement("div");
			$dateSpan.innerHTML = moment(archive.holdingDate).format("YYYY.MM.DD");
			const $timeSpan = Util.createElement("div");
			$timeSpan.innerHTML = moment(archive.beginDateTime).format("HH:mm")+"~"+moment(archive.finishDateTime).format("HH:mm")
			$column.appendChild($dateSpan);
			$column.appendChild($timeSpan);
			
			$row.appendChild($column);
		}
		// 장소구분 칼럼
		const createRoomTypeColumn = () => {
			const $column = Util.createElement("div", "item", "type");
			const $span = Util.createElement("span");
			$column.appendChild($span);
			if(archive.roomType == "MEETING_ROOM"){
				Util.addClass($row, "mr");
			}else if(archive.roomType == "EDU_ROOM"){
				Util.addClass($row, "lr");
			}else if(archive.roomType == "HALL"){
				Util.addClass($row, "at");
			}
			
			$row.appendChild($column);
		}
		// 회의제목/주관자 칼럼
		const createTitleColumn = () => {
			const $column = Util.createElement("div", "item", "title", "flex-flow-column", "align-items-start");
			//const $a = Util.createElement("a");
			//$a.href = "/lime/room/"+archive.roomType+"/assign/"+archive.reqKey;
			
			const $title = Util.createElement("div", "href-link");
			//$a.appendChild($title);
			$title.innerHTML = archive.title;
			$column.appendChild($title);
			$title.onclick = () => {
				archiveEvtHandler.trigger("titleClick", {
					scheduleId: archive.scheduleId,
				});
			}
			const $host = Util.createElement("div");
			const $span = Util.createElement("span");
			$host.appendChild($span);
			$span.innerHTML = archive.scheduleHost;
			$column.appendChild($host);
			
			$row.appendChild($column);
		}
		// 파일 칼럼
		const createFileColumn = () => {
			const $column = Util.createElement("div", "item", "file");
			const $ul = Util.createElement("ul");
			$column.appendChild($ul);
			
			$row.getFileNode = () => {
				return $ul;
			}
			const createFileRow = (file) => {
				const $li = Util.createElement("li");
				const $fileCheckbox = Util.createElement("input");
				$fileCheckbox.type = "checkbox";
				$li.appendChild($fileCheckbox);
				
				/****** 파일 타입 및 상태별 아이콘과 다운로드/삭제 가능 여부 ******/
				const {
					icon,
					elementClass,
					deletable,
					downloadable,
					viewable,
				} = Final.getFileMetadata(file);
				
				$li.deletable = deletable;
				$li.downloadable = downloadable;
				$li.viewable = viewable;
				
				const $link = Util.createElement("div", "link", elementClass);
				$li.appendChild($link);
				
				const $tipDiv = Util.createElement("div", "tip");
				$link.appendChild($tipDiv);
				
				const $nameDiv = Util.createElement("div");
				$link.appendChild($nameDiv);
				const $iconDiv = Util.createElement("span", "my-auto", "mr-2");
				const $icon = Util.createElement("i", ...icon);
				$iconDiv.appendChild($icon);
				$nameDiv.appendChild($iconDiv);
				const $nameSpan = Util.createElement("span");
				$nameSpan.innerHTML = file.uploadedFileName;
				$nameDiv.appendChild($nameSpan);
				
				if($li.viewable == true){
					Util.addClass($link, "cursor-pointer");
					$link.onclick = () =>{
						$MEETING.Get.viewFile({
							fileCategory: file.fileCategory,
							meetingId: file.relatedEntityId,
							fileId: file.fileId
						});
					}
				}else{
					Util.addClass($link, "text-muted");
				}
				if(!$li.downloadable && !$li.deletable){
					$fileCheckbox.disabled = true;
				}
				$li.getFile = () => {
					return {
						...file,
						node: $li
					};
				}
				$li.isChecked = () => {
					return $fileCheckbox.checked;
				}
				$li.check = () => {
					if(!$fileCheckbox.disabled){
						$fileCheckbox.checked = true;
					}
				}
				$li.uncheck = () => {
					if(!$fileCheckbox.disabled){
						$fileCheckbox.checked = false;
					}
				}
				$fileCheckbox.onchange = () => {
					//$row.switchAllChk?.();
					archiveEvtHandler.validFileBtn?.();
				}
				
				return $li;
			}
			const files = archive.files;
			for(const file of files){
				const $file = createFileRow(file);
				$ul.appendChild($file);
			}
			$row.appendChild($column);
		}
		createNoColumn();
		createCheckboxColumn();
		createScheduleColumn();
		createRoomTypeColumn();
		createTitleColumn();
		createFileColumn();
		return $row;
	},
}


