/**
 * -배정현황, 파일함, 사용신청목록(관리포함) 페이지 검색창 제어 스크립트. 다음과 같은 파라미터의 입력 제어
 * 1. officeCode: 사업소 키. 초기화 함수 initOffice 지원
 * 2. approvalStatus: 결재 상태. 초기화 함수 initApprovalStatus 지원
 * 3. roleType: 파일 유형. 초기화 함수 initFileType 지원
 * 4. roomType: 장소 분류. 초기화 함수 initRoomTypeSelect, initRoomTypeCheck 지원
 * 5. roomId: 장소 선택. 초기화 함수 initRoom 지원
 * 6. elecYN: 전자회의 여부. 초기화 함수 initElecYN 지원
 * 7. secretYN: 기밀회의 여부. 초기화 함수 initSecretYN 지원
 * 8. title: 제목. 초기화 함수 initTitle 지원
 * 9. host: 주관자. 초기화 함수 initHost 지원
 * 10. attnedee: 참석자명. 초기화 함수 initAttnedee 지원
 * 11. searchTarget: 검색 대상. 초기화 함수 initSearchTarget 지원
 * 		-title: 제목
 * 		-host: 주관자
 * 		-originalName: 파일명
 * 12. searchWord: 검색어. 초기화 함수 initSearchWord 지원
 * 13. startDate, endDate: 시작/종료 일자. 초기화 함수 initPeriodPicker 지원
 * 14. holdingDate: 해당 일자. 초기화 함수 initDatePicker
 * 
 * -이벤트 트리거
 * 1. search({key: value}): 현재 활성화된 입력과 입력된 값을 맵 형식으로 반환
 * 2. reset({key: value}): 현재 활성화된 입력의 값을 기본 값으로 초기화 하고 초기화된 입력 값을 맵 형식으로 반환
 * 3. change(name, value): 현재 활성화된 입력의 값이 변경될 때 마다 해당 입력명과 변경된 값을 반환
 * 
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 * @see /resources/front-end-assets/js/ewp/page/manage/admin/approval_manage.js
 * @see /resources/front-end-assets/js/ewp/page/manage/archive/archive_user.js
 */
import {eventMixin, Util, Modal} from '/resources/core-assets/essential_index.js';

export default {
	__proto__: eventMixin,
	init(data ={}){
		const {
			officeCode = "1000",
			approvalStatus = 0,
			meetingStatus = 0,
			roomType = "ALL_ROOM",
			roomId,
			title = "",
			host = "",
			attendeeName = "",
			searchTarget = "title",
			searchWord = "",
			holdingDate = moment().format("YYYY-MM-DD"),
			startDate = moment().format("YYYY-MM-DD"),
			endDate = moment().add(7, "d").format("YYYY-MM-DD"),
		} = data;
		this.officeCode = officeCode;
		this.approvalStatus = approvalStatus;
		this.meetingStatus = meetingStatus;
		this.roomType = roomType;
		this.roomId = roomId;
		this.title = title;
		this.host = host;
		this.attendeeName = attendeeName;
		this.searchTarget = searchTarget;
		this.searchWord = searchWord;
		this.holdingDate = holdingDate;
		this.startDate = startDate;
		this.endDate = endDate;
		
		this.enableSearchBtn();
		this.enableResetBtn();
		this.enableSearchToggleBtn();
	},
	initOffice(){
		const $officeSelect = Util.getElement("#officeSelect");
		if(!$officeSelect){
			this.getOffice = () => {
				return this.officeCode;
			}
		}else{
			const $officeLabel = Util.getElement("#officeLabel");
			const selectOption = (value) => {
				const $option = $officeSelect.querySelector('[value="'+value+'"]');
				$option.selected = true;
				const name = $option.text;
				$officeLabel.innerText = name;
			}
			$officeSelect.onchange = (evt) => {
				const value = evt.target.value;
				selectOption(value);
				this.trigger("change", "office", value);
			}
			this.selectOffice = selectOption;
			this.getOffice = () => {
				return ($officeSelect.value == 0)?null:$officeSelect.value;
			}
			this.selectOffice(this.officeCode);
		}
	},
	initApprovalStatus(){// 결재 상태
		const $approvalStatusSelect = Util.getElement("#approvalStatusSelect");
		const $approvalStatusLabel = Util.getElement("#approvalStatusLabel");
		const selectOption = (value) => {
			const $option = $approvalStatusSelect.querySelector('[value="'+value+'"]');
			$option.selected = true;
			const name = $option.text;
			$approvalStatusLabel.innerText = name;
		}
		$approvalStatusSelect.onchange = (evt) => {
			const value = evt.target.value;
			selectOption(value);
			this.trigger("change", "approvalStatus", value);
		}
		this.selectAppStatus = selectOption;
		this.getAppStatus = () => {
			return ($approvalStatusSelect.value == 0)?null:$approvalStatusSelect.value;
		}
		this.selectAppStatus(this.approvalStatus);
	},
	initMeetingStatus(){// 결재 상태
		const $meetingStatusSelect = Util.getElement("#meetingStatusSelect");
		const $meetingStatusLabel = Util.getElement("#meetingStatusLabel");
		const selectOption = (value) => {
			const $option = $meetingStatusSelect.querySelector('[value="'+value+'"]');
			$option.selected = true;
			const name = $option.text;
			$meetingStatusLabel.innerText = name;
		}
		$meetingStatusSelect.onchange = (evt) => {
			const value = evt.target.value;
			selectOption(value);
			this.trigger("change", "meetingStatus", value);
		}
		this.selectMeetingStatus = selectOption;
		this.getMeetingStatus = () => {
			return ($meetingStatusSelect.value == 0)?null:$meetingStatusSelect.value;
		}
		this.selectMeetingStatus(this.meetingStatus);
	},
	initSearchTarget(){// 검색 대상
		const $searchTargetSelect = Util.getElement("#searchTargetSelect");
		const $searchTargetLabel = Util.getElement("#searchTargetLabel");
		const selectOption = (value) => {
			const $option = $searchTargetSelect.querySelector('[value="'+value+'"]');
			$option.selected = true;
			const name = $option.text;
			$searchTargetLabel.innerText = name;
		}
		$searchTargetSelect.onchange = (evt) => {
			const value = evt.target.value;
			selectOption(value);
			this.trigger("change", "searchTarget", value);
		}
		this.selectTarget = selectOption;
		this.getSearchTarget = () => {
			return $searchTargetSelect.value;
		}
		this.selectTarget(this.searchTarget);
	},
	initSearchWord(){// 검색어
		const $searchWord = Util.getElement("#searchWord");
		$searchWord.maxLength = 60;
		$searchWord.oninput = () => {
			Util.acceptWithinLength($searchWord);
		}
		$searchWord.onkeyup = (evt) => {
			if (evt.keyCode == 13) {
				this.trigger("search", this.getSearchInput());
		    }
		}
		$searchWord.onchange = () => {
			this.trigger("change", "searchWord", $searchWord.value);
		}
		this.setSearchWord = (value) => {
			$searchWord.value = value;
		}
		this.getSearchWord = () => {
			return ($searchWord.value == "")?null:$searchWord.value;
		}
		this.setSearchWord(this.searchWord);
	},
	initPeriodPicker(options = {}){// 기간 선택 달력
		const {
			maxDate
		} = options;
		const $startDateDiv = Util.getElement("#startDateDiv");
		const $startDateInput = Util.getElement("#startDateInput");
		$startDateInput.value = this.startDate;
		const $endDateDiv = Util.getElement("#endDateDiv");
		const $endDateInput = Util.getElement("#endDateInput");
		$endDateInput.value = this.endDate;
		this.setStartDate = (date) => {
			$startDateInput.value = date;
		}
		this.setEndDate = (date) => {
			$endDateInput.value = date;
		}
		this.getStartDate = (date) => {
			return $startDateInput.value;
		}
		this.getEndDate = (date) => {
			return $endDateInput.value;
		}
		const $datepicker = $("#datepicker");
		$datepicker.datepicker({
			dateFormat: "yy-mm-dd",
		});
		if(moment(maxDate).isValid() == true){
			$datepicker.datepicker("option", "maxDate", maxDate);
		}
		const $datePickerModal =  Util.getElement("#datePickerModal");
		const $dateOkBtn = $datePickerModal.querySelector(".btn-blue");
		const $dateCancelBtn = $datePickerModal.querySelector(".btn-silver");
		$startDateDiv.onclick = async () => {
			const startDate = this.getStartDate();
			const endDate = this.getEndDate();
			if(moment(startDate).isValid() == true){
				$datepicker.datepicker("setDate", startDate);
			}
			const dateRes = await Modal.get("datePickerModal").show();
			if(dateRes == "OK"){
				const newdate = $datepicker.datepicker("getDate");
				const newDateMoment = moment(newdate);
				const formattedDate = newDateMoment.format("YYYY-MM-DD");
				if(newDateMoment.isAfter(endDate)) {
			        this.setEndDate(formattedDate);
			        this.trigger("change", "endDate", formattedDate);
			    }
				this.setStartDate(formattedDate);
				this.trigger("change", "startDate", formattedDate);
			}
		}
		$endDateDiv.onclick = async () => {
			const startDate = this.getStartDate();
			const endDate = this.getEndDate();
			if(moment(endDate).isValid() == true){
				$datepicker.datepicker("setDate", endDate);
			}
			const dateRes = await Modal.get("datePickerModal").show();
			if(dateRes == "OK"){
				const newdate = $datepicker.datepicker("getDate");
				const newDateMoment = moment(newdate);
				const formattedDate = newDateMoment.format("YYYY-MM-DD");
				if(newDateMoment.isBefore(startDate)) {
			        this.setStartDate(formattedDate);
			        this.trigger("change", "startDate", formattedDate);
			    }
				this.setEndDate(formattedDate);
				this.trigger("change", "endDate", formattedDate);
			}
		}
	},
	initDatePicker(){// 날짜 선택 달력
		const $dateDiv = Util.getElement("#dateDiv");
		const $dateInput = Util.getElement("#dateInput");
		$dateInput.value = this.holdingDate;
		this.setDate = (date) => {
			$dateInput.value = date;
		}
		this.getDate = (date) => {
			return $dateInput.value;
		}
		const $datepicker = $("#datepicker");
		$datepicker.datepicker({
			dateFormat: "yy-mm-dd",
		});
		
		const today = moment().format("YYYY-MM-DD");
		$dateDiv.onclick = async () => {
			$datepicker.datepicker("option", "minDate", today);
			
			const date = this.getDate();
			if(moment(date).isValid() == true){
				$datepicker.datepicker("setDate", date);
			}
			const dateRes = await Modal.get("datePickerModal").show();
			if(dateRes == "OK"){
				const newdate = $datepicker.datepicker("getDate");
				const dateM = moment(newdate).format('YYYY-MM-DD');
				this.setDate(dateM);
				this.trigger("change", "holdingDate", dateM);
			}
		}
	},
	initFileType(){// 파일 유형(현재는 판서본 포함 옵션)
		const $fileSwitch = Util.getElement("#fileType");
		if(!$fileSwitch){
			return;
		}
		$fileSwitch.onchange = () => {
			const value = this.getFileType();
			this.trigger("change", "roleType", value);
		}
		this.switchFileType = (bool) => {
			$fileSwitch.checked = bool;
		}
		this.getFileType = () => {
			return ($fileSwitch.checked)?$fileSwitch.value:null;
		}
	},
	initRoomTypeSelect(){// 장소 분류 선택
		const $roomTypeSelect = Util.getElement("#roomTypeSelect");
		if($roomTypeSelect) {
			const $roomTypeLabel = Util.getElement("#roomTypeLabel");
			const selectOption = (value) => {
				this.trigger("change", "roomType", value);
				$roomTypeSelect.value = value;
				const name = $roomTypeSelect.options[$roomTypeSelect.selectedIndex].text
				$roomTypeLabel.innerText = name;
			}
			$roomTypeSelect.onchange = (evt) => {
				const value = evt.target.value;
				selectOption(value);
			}
			this.selectRoomType = selectOption;
			this.getRoomType = () => {
				return $roomTypeSelect.value;
			}
			this.selectRoomType(this.roomType);
		} else {
			this.getRoomType = () => {
				return this.roomType;
			}
		}
	},
	initRoomTypeCheck(){// 장소 분류 체크
		const $roomTypeRadioList = Util.getElementAll('input[type=radio][name="roomType"]');
		$roomTypeRadioList.forEach($radio => $radio.addEventListener('change', async () => {
			const value = $radio.value;
			this.trigger("change", "roomType", value);
			this.loadRoomList?.();
		}));
		this.checkRoomType = (value) => {
			$roomTypeRadioList.forEach($radio => {
				if($radio.value == value){
					$radio.checked = true;
				}else{
					$radio.checked = false;
				}
			});
		}
		this.getRoomType = () => {
			for(const $radio of $roomTypeRadioList){
				if($radio.checked == true){
					return $radio.value;
				}
			}
		}
		this.checkRoomType(this.roomType);
	},
	initRoomSelect(){// 장소 선택
		const $roomSelect = Util.getElement("#roomSelect");
		const $roomLabel = Util.getElement("#roomLabel");
		const createOption = (value, text) => {
			const $roomOption = document.createElement("option");
			$roomOption.value = (value)?value:-1;
			$roomOption.innerText = text;
			
			return $roomOption;
		}
		const selectOption = (value) => {
			if(value){
				const $option = $roomSelect.querySelector('[value="'+value+'"]');
				$option.selected = true;
				const name = $option.text;
				$roomLabel.innerText = name;
			}else{
				$roomSelect.selectedIndex = null;
				$roomSelect.value = -1;
				$roomLabel.innerText = "전체";
			}
			this.trigger("change", "roomId", value);
		}
		$roomSelect.onchange = (evt) => {
			const value = evt.target.value;
			selectOption(value);
		}
		this.selectRoom = selectOption;
		this.getRoom = () => {
			return ($roomSelect.value == -1)?undefined:$roomSelect.value;
		}
		this.setRoomSelectBox = (roomList) => {
			$roomSelect.innerHTML = "";
			const $allOption = createOption(null, "전체");
			$roomSelect.appendChild($allOption);
			for(const room of roomList){
				const $roomOption = createOption(room.roomId, room.roomName);
				$roomSelect.appendChild($roomOption);
			}
		}
		const $allOption = createOption(null, "전체");
		$roomSelect.appendChild($allOption);
		this.selectRoom(this.roomId);
	},
	initElecYN(){// 파일 유형(현재는 판서본 포함 옵션)
		const $elecSwitch = Util.getElement("#switchElec");
		
		$elecSwitch.onchange = () => {
			const value = this.getElecYN();
			this.trigger("change", "elecYN", value);
		}
		this.switchElecYN = (bool) => {
			$elecSwitch.checked = bool;
		}
		this.getElecYN = () => {
			return ($elecSwitch.checked == true)?'Y':null;
		}
	},
	initSecretYN(){// 파일 유형(현재는 판서본 포함 옵션)
		const $secretSwitch = Util.getElement("#switchSecret");
		$secretSwitch.onchange = () => {
			const value = this.getSecretYN();
			this.trigger("change", "secretYN", value);
		}
		this.switchSecretYN = (bool) => {
			$secretSwitch.checked = bool;
		}
		this.getSecretYN = () => {
			return ($secretSwitch.checked == true)?'Y':null;
		}
	},
	initTitle(){// 제목 검색
		const $titleInput = Util.getElement("#titleInput");
		if(!$titleInput){
			return;
		}
		$titleInput.oninput = () => {
			Util.acceptWithinLength($titleInput);
		}
		$titleInput.onkeyup = (evt) => {
			if (evt.keyCode == 13) {
				this.trigger("search", this.getSearchInput());
		    }
		}
		$titleInput.onchange = () => {
			this.trigger("change", "title", $titleInput.value);
		}
		this.setTitle = (value) => {
			$titleInput.value = value;
		}
		this.getTitle = () => {
			return ($titleInput.value == "")?null:$titleInput.value;
		}
		this.setTitle(this.title);
	},
	initHost(){// 주관자 검색
		const $hostInput = Util.getElement("#hostInput");
		$hostInput.oninput = () => {
			Util.acceptExcludeSpecial($hostInput);
		}
		$hostInput.onkeyup = (evt) => {
			if (evt.keyCode == 13) {
				this.trigger("search", this.getSearchInput());
		    }
		}
		$hostInput.onchange = () => {
			this.trigger("change", "host", $hostInput.value);
		}
		this.setHost = (value) => {
			$hostInput.value = value;
		}
		this.getHost = () => {
			return ($hostInput.value == "")?null:$hostInput.value;
		}
		this.setHost(this.host);
	},
	initAttendeeName(){// 참석자 검색
		const $attendeeInput = Util.getElement("#attendeeInput");
		$attendeeInput.oninput = () => {
			Util.acceptText($attendeeInput);
		}
		$attendeeInput.onkeyup = (evt) => {
			if (evt.keyCode == 13) {
				this.trigger("search", this.getSearchInput());
		    }
		}
		$attendeeInput.onchange = () => {
			this.trigger("change", "attendee", $attendeeInput.value);
		}
		this.setAttendeeName = (value) => {
			$attendeeInput.value = value;
		}
		this.getAttendeeName = () => {
			return ($attendeeInput.value == "")?null:$attendeeInput.value;
		}
		this.setAttendeeName(this.attendeeName);
	},
	getSearchInput(){
		const officeCode = this.getOffice?.();
		const searchTarget = this.getSearchTarget?.();
		const searchWord = this.getSearchWord?.();
		const approvalStatus = this.getAppStatus?.();
		const meetingStatus = this.getMeetingStatus?.();
		const holdingDate = this.getDate?.();
		const startDate = this.getStartDate?.();
		const endDate = this.getEndDate?.();
		const roomType = this.getRoomType?.();
		const roomId = this.getRoom?.();
		const roleType = this.getFileType?.();
		const elecYN = this.getElecYN?.();
		const secretYN = this.getSecretYN?.();
		const title = this.getTitle?.();
		const host = this.getHost?.();
		const attendeeName = this.getAttendeeName?.();
		return {
			officeCode: officeCode,
			approvalStatus: approvalStatus,
			meetingStatus: meetingStatus,
			searchTarget: searchTarget,
			searchWord: searchWord,
			holdingDate: holdingDate,
			startDate: startDate,
			endDate: endDate,
			roomType: roomType,
			roomId: roomId,
			roleType: roleType,
			elecYN: elecYN,
			secretYN: secretYN,
			title: title,
			host: host,
			attendeeName: attendeeName,
		}
	},
	initSearchInput(options = {}){
		const nowDateM = moment();
		const {
			holdingDate = nowDateM.format("YYYY-MM-DD"),
			startDate = nowDateM.clone().add(-2, "w").format("YYYY-MM-DD"),
			endDate = nowDateM.clone().add(2, "w").format("YYYY-MM-DD"),
		} = options;
		this.selectOffice?.(this.officeCode);
		this.setSearchTarget?.("title");
		this.setSearchWord?.("");
		this.selectAppStatus?.("0");
		this.selectMeetingStatus?.("0");
		this.setDate?.(holdingDate);
		this.setStartDate?.(startDate);
		this.setEndDate?.(endDate);
		this.selectRoomType?.("ALL_ROOM");
		this.checkRoomType?.("MEETING_ROOM");
		this.selectRoom?.();
		this.switchFileType?.(false);
		this.switchElecYN?.(false);
		this.switchSecretYN?.(false);
		this.setTitle?.("");
		this.setHost?.("");
		this.setAttendeeName?.("");
	},
	enableSearchBtn(){
		const $btn = Util.getElement("#searchBtn");
		if($btn){
			$btn.onclick = () => {
				this.trigger("search", this.getSearchInput());
			}
		}
	},
	enableResetBtn(options = {}){
		const $btn = Util.getElement("#resetBtn");
		if($btn){
			$btn.onclick = () => {
				this.initSearchInput(options);
				this.trigger("reset", this.getSearchInput());
			}
		}
		const $mbbtn = Util.getElement("#resetMobileBtn");
		if($mbbtn){
			$mbbtn.onclick = () => {
				this.initSearchInput(options);
				this.trigger("reset", this.getSearchInput());
				const $srchDiv = Util.getElement(".listSrchDiv");
				if($srchDiv.style.display == "flex"){
					$srchDiv.style.display = "none";
				}else{
					$srchDiv.style.display = "flex";
				}
			}
		}
	},
	enableSearchToggleBtn(){
		const $btn = Util.getElement(".mobileSrchBtn");
		if($btn){
			$btn.onclick = () => {
				const $srchDiv = Util.getElement(".listSrchDiv");
				if($srchDiv.style.display == "flex"){
					$srchDiv.style.display = "none";
				}else{
					$srchDiv.style.display = "flex";
				}
			}
		}
	},
}