/**
 * 배정현황 타임테이블 페이지 스크립트
 * 
 * @author mckim
 */

import {Util, Modal, AjaxBuilder} from '/resources/core-assets/essential_index.js';
import {SearchBar} from '/resources/front-end-assets/js/ewp/comm/module_index.js';
import {UserCall as $USER, RoomCall as $RM, AssignCall as $AS} from '/resources/front-end-assets/js/ewp/comm/ajax_index.js';

window.onload = () => {
	moment.locale("ko");
	reserveHandler.init({
		interval: INTERVAL_MINUTE,
	});
	evtHandler.init();
	searchHandler.init({
		interval: INTERVAL_MINUTE,
		officeCode: officeCode,
		roomType: roomType,
		roomKey: roomKey,
		holdingDate: holdingDate,
	});
};

const searchHandler = {
	async init(data = {}){
		const {
			officeCode,
			roomType,
			roomKey,
			holdingDate,
			interval,
		} = data;
		this.interval = interval;
		SearchBar.init({
			officeCode: officeCode,
			roomType: roomType,
			holdingDate: holdingDate,
		});
		SearchBar.initOffice();
		SearchBar.initRoomTypeCheck();
		SearchBar.initDatePicker();
		SearchBar.on("change", async (input, value) => {
			if(input == "office"){
				this.setApprovalManager();
				await this.setRoomList();
			}
			if(input == "roomType"){
				this.setApprovalManager();
				await this.setRoomList();
			}
			if(input == "holdingDate"){
				await this.search({
					holdingDate: value
				});
			}
		});
		this.setApprovalManager();
		await this.setRoomList();
	},
	async setApprovalManager(){
		const {
			officeCode,
			roomType,
		} = SearchBar.getSearchInput();
		await AjaxBuilder({
			request: $USER.Get.approvalManagerPublicList,
			param: {
				officeCode: officeCode,
				roomType: roomType,
			}
		}).success(list => {
			const $nameDiv = Util.getElement("#managerName");
			const $telDiv = Util.getElement("#managerTel");
			if(!Util.isEmpty(list)){
				const manager = list[0];
				$nameDiv.innerHTML = manager.userName;
				$telDiv.innerHTML = manager.officeDeskPhone;
			}else{
				$nameDiv.innerHTML = "미지정";
				$telDiv.innerHTML = "-";
			}
			
		}).error(() => {
		}).exe();
	},
	//해당 장소 분류의 현재 사용신청 가능한 목록 조회하고 타임테이블을 생성하여 표시하고 해당 장소의 배정신청 현황 검색 요청
	async setRoomList(){
		const {
			officeCode,
			roomType,
			holdingDate,
		} = SearchBar.getSearchInput();
		AjaxBuilder({
			request: $RM.Get.rentableList,
			param: {
				officeCode: officeCode,
				roomType: roomType,
				rentYN: 'Y'
			}
		}).success(async list => {
			this.roomList = list;
			roomBoxGenerator.generate({
				openTime: OPEN_TIME,
				closeTime: CLOSE_TIME,
				interval: this.interval,
				holdingDate: this.holdingDate,
				roomList: this.roomList,
			});
			await this.search();
		}).exe();
	},
	//장소 분류, 일자 변경시 배정신청 현황 검색 및 표시
	async search(data = {}){
		const {
			holdingDate = this.holdingDate,
		} = SearchBar.getSearchInput();
		this.holdingDate = holdingDate;
		const nowTimeM = moment();
		const isToday = (nowTimeM.format("YYYYMMDD") == moment(holdingDate).format("YYYYMMDD"))?true:false;
		const openTimeM = moment(OPEN_TIME, "HH:mm");
		const closeTimeM = moment(CLOSE_TIME, "HH:mm");
		reserveHandler.init({
			holdingDate: this.holdingDate,
		});
		for(const room of this.roomList){
			const $roomTable = roomBoxGenerator.getTable(room.roomKey);
			$roomTable.init();
			if(isToday){
				reserveHandler.disableInRange($roomTable, openTimeM.clone(), nowTimeM.clone());
			}
			//기존 배정신청 현황 표시
			$AS.Get.assignListForDisplay({
				roomType: room.roomType,
				roomKey: room.roomKey,
				startDate: this.holdingDate,
				endDate: this.holdingDate,
			}).then((list) => {
				if(!list || list.length == 0){
					return;
				}
				const holdingDateM = moment(this.holdingDate);
				const validStatus = ["REQUEST", "APPROVED"];
				for(const assign of list){
					assign.room = room;
					if(!validStatus.includes(assign.appStatus)){
						//결재대기,승인 을 제외한 사용신청은 표시 안함 
						continue;
					}
					const startTimeM = moment(assign.beginDateTime);
					const startTimeNum = startTimeM.hour() * 100 + startTimeM.minute();
					const endTimeM = moment(assign.finishDateTime);
					const endTimeNum = endTimeM.hour() * 100 + endTimeM.minute();
					const minTimeM = startTimeM.clone().add(-this.interval, "m");
					const $rowList = $roomTable.getAllRow();
					const lockedRowSet = new Set();
					//테이블에서 사용신청 시간 범위내 모두 찾아 체크
					for(const $row of $rowList){
						const rowTime = $row.time;
						const timeM = moment(rowTime, "HH:mm");
						holdingDateM.set({
						    hour:   timeM.get('hour'),
						    minute: timeM.get('minute')
						});
						if(holdingDateM.isSame(endTimeM)){
							//신청 범위 끝과 같으면 종료
							break;
						}
						if(holdingDateM.isAfter(endTimeM)){
							//신청 범위를 넘으면 종료
							break;
						}
						if(holdingDateM.isSameOrBefore(minTimeM)){
							//신청 범위 이전값은 무시
							continue;
						}
						lockedRowSet.add($row);
					}
					let $headRow;
					lockedRowSet.forEach($row => {
						if($row.locked != true){
							if(!$headRow){
								$headRow = $row;
							}
							$row.style.display = "none";
							$row.lockingTime();
							$row.onclick = () => {
								evtHandler.showInfoModal(assign);
							}
						}
					});
					try{
						const size = lockedRowSet.size;
						$headRow.style.flexBasis = size * 50 - (size - 1) + "px";
						$headRow.style.display = "";
						$headRow.username.innerHTML = assign.skdHost;
					}catch(err){
						//console.log("assign", assign, "startTimeNum", startTimeNum, "endTimeNum", endTimeNum);
						//console.log("lockedRowList", lockedRowSet, lockedRowSet[0])
						//console.error(err);
					}
				}
			});
		}
	},
}

const evtHandler = {
	init(){
		this.setCancelBtn();
		this.setInitBtn();
		this.setNextBtn();
	},
	//기존 예약내역 선택시 상세 정보 표시 모달 팝업
	showInfoModal(assign){
		const $infoModal = Modal.get("infoReservedMeeting");
		const $title = $infoModal.querySelector("#titleInput");
		$title.innerHTML = assign.title;
		const $holdingDate = $infoModal.querySelector("#holdingDateInput");
		$holdingDate.innerHTML = moment(assign.holdingDate).format("YYYY.MM.DD.dd");
		const $schedule = $infoModal.querySelector("#scheduleInput");
		$schedule.innerHTML = moment(assign.beginDateTime).format("HH:mm")+" ~ "+moment(assign.finishDateTime).format("HH:mm");
		const $roomName = $infoModal.querySelector("#roomNameInput");
		$roomName.innerHTML = assign.room?.roomName;
		const $host = $infoModal.querySelector("#hostInput");
		$host.innerHTML = assign.skdHost;
		const $writerNameplate = $infoModal.querySelector("#writerNameplateInput");
		$writerNameplate.innerHTML = assign.writer?.nameplate;
		const $writerTel = $infoModal.querySelector("#writerTelInput");
		$writerTel.innerHTML = assign.writer?.officeDeskPhone;
		//const $writerMail = $infoModal.querySelector("#writerMailInput");
		//$writerMail.innerHTML = assign.writer?.email;
		$infoModal.show();
	},
	//현재 예약선택 상황에 맞춰 초기화/다음 버튼 활성/비활성화
	switchReserveBtn(timeSelected){
		const $initBtn = Util.getElement("#initBtn");
		if($initBtn){
			$initBtn.disabled = !timeSelected;
		}
		const $nextBtn = Util.getElement("#nextBtn");
		if($nextBtn){
			$nextBtn.disabled = !timeSelected;
		}
	},
	//취소 버튼 설정
	setCancelBtn(){
		const $btn = Util.getElement("#cancelBtn");
		if($btn){
			$btn.onclick = () => {
				location.href = "/ewp/home";
			}
		}
	},
	//초기화 버튼 설정
	setInitBtn(){
		const $btn = Util.getElement("#initBtn");
		if($btn){
			$btn.disabled = true;
			$btn.onclick = () => {
				reserveHandler.init();
				roomBoxGenerator.getAllTable().forEach($table => $table.clear());
			}
		}
	},
	//다음 버튼 설정
	setNextBtn(){
		const $btn = Util.getElement("#nextBtn");
		if($btn){
			$btn.disabled = true;
			$btn.onclick = () => {
				const reserveData = reserveHandler.getReserveData();
				if(reserveData){
					const {
						roomType,
						roomKey
					} = reserveData.room;
					const {
						holdingDate,
						startTime,
						endTime
					} = reserveData;
					location.href = `/ewp/meeting/assign/post?roomType=${roomType}&roomKey=${roomKey}&holdingDate=${holdingDate}&startTime=${startTime}&endTime=${endTime}`;
				}
			}
		}
	},
}

const reserveHandler = {
	init(data = {}){
		const {
			interval = this.interval,
			holdingDate = this.holdingDate,
		} = data;
		this.interval = interval;
		this.holdingDate = holdingDate;
		this.room = null;
		this.selected = [];
		this.initReserveDetail();
	},
	getReserveData(){
		if(this.room){
			return {
				room: this.room,
				holdingDate: this.holdingDate,
				startTime: this.startTime,
				endTime: this.endTime,
			}
		}else{
			return null;
		}
	},
	selectTime(room, $row, $table){//테이블 클릭시 처리
		const roomKey = room.roomKey;
		if($row.locked == true || loginType != "SSO"){
			return;
		}
		if(this.room && roomKey != this.room.roomKey){
			this.selected = [];
			roomBoxGenerator.getAllTable().filter($table => $table.roomKey != roomKey).forEach($table => $table.clear());
		}
		const nowSelectedRow = this.selected;
		const selectedLength = nowSelectedRow.length;
		let newSelectedRow = [];
		let selectedDirection = 1;
		if(selectedLength >= 1){// 선택한 시간이 2개 이상이 될 경우
			let $startRow = nowSelectedRow[0];
			let $endRow = nowSelectedRow[selectedLength - 1];
			if($row == $startRow){//맨 처음 선택
				nowSelectedRow.splice(0, 1);
				newSelectedRow = nowSelectedRow;
			}else if($row == $endRow){//맨 끝 선택
				nowSelectedRow.splice(selectedLength - 1, 1);
				newSelectedRow = nowSelectedRow;
			}else{
				if($row.timeNum < nowSelectedRow[0].timeNum){//선택된 시간이 기존 시작시간보다 작음
					$startRow = $row;
					$endRow = nowSelectedRow[selectedLength - 1];
					if($table.hasLock($startRow.timeNum, $endRow.timeNum)){
						//선택 범위내 잠금된 Row가 있는 경우 지금까지의 선택을 취소하고 새로 선택한 앞점으로 변경
						$endRow = $startRow;
					}
				}else if($row.timeNum > nowSelectedRow[selectedLength - 1].timeNum){//선택된 시간이 기존 종료시간보다 큼
					$startRow = nowSelectedRow[0];
					$endRow = $row;
					if($table.hasLock($startRow.timeNum, $endRow.timeNum)){
						//선택 범위내 잠금된 Row가 있는 경우 지금까지의 선택을 취소하고 새로 선택한 끝점으로 변경
						$startRow = $endRow;
					}
				}else{
					$startRow = nowSelectedRow[0];
					$endRow = $row;
				}
				//시간범위내 모든 칸 선택
				newSelectedRow = $table.getRowInRange($startRow.timeNum, $endRow.timeNum);
			}
		}else{// 첫 선택
			newSelectedRow.push($row);
		}
		if(newSelectedRow.length > 1){
			//시간순 정렬
			newSelectedRow.sort(($rowA, $rowB) => {
				if($rowA.timeNum  > $rowB.timeNum){
					return 1;
				}else{
					return -1;
				}
			});
		}
		this.reserveSelectedTime(room, newSelectedRow);
	},
	reserveSelectedTime(room, newSelectedRow){//선택된 시간으로 예약설정
		this.selected = [];
		const $table = roomBoxGenerator.getTable(room.roomKey);
		$table.clear();
		for(const $row of newSelectedRow){
			if($row.locked != true){
				$row.selectTime();
				this.selected.push($row);
			}else{
				break;
			}
		}
		const $reserveRoomName = Util.getElement("#reserveRoomNameInput");
		const $reserveSchedule = Util.getElement("#reserveScheduleInput");
		if(this.selected.length == 0){// 선택 없음
			this.room = null;
			this.initReserveDetail();
		}else{// 예약할 테이블 선택
			this.room = room;
			this.startTime = this.selected[0].time;
			this.endTime = moment(this.selected[this.selected.length - 1].time, "HH:mm").add(this.interval, "m").format("HH:mm");
			this.setReserveDetail();
		}
	},
	disableInRange($table, startTimeM, endTimeM){
		const sTimeNum = startTimeM.hour() * 100 + startTimeM.minute();
		endTimeM.add(-this.interval, "m");
		const eTimeNum = endTimeM.hour() * 100 + endTimeM.minute();
		const $rowList = $table.getRowInRange(sTimeNum, eTimeNum);
		$rowList.forEach($row => $row.disableTime());
	},
	setReserveDetail(){
		const $reserveRoomName = Util.getElement("#reserveRoomNameInput");
		if($reserveRoomName){
			$reserveRoomName.innerHTML = this.room.roomName;
		}
		const $reserveSchedule = Util.getElement("#reserveScheduleInput");
		if($reserveSchedule){
			$reserveSchedule.innerHTML = this.startTime +"~"+this.endTime;
		}
		evtHandler.switchReserveBtn(true);
	},
	initReserveDetail(){
		const $reserveRoomName = Util.getElement("#reserveRoomNameInput");
		if($reserveRoomName){
			$reserveRoomName.innerHTML = "";
		}
		const $reserveSchedule = Util.getElement("#reserveScheduleInput");
		if($reserveSchedule){
			$reserveSchedule.innerHTML = "";
		}
		evtHandler.switchReserveBtn(false);
	},
}

const roomBoxGenerator = {
	getAllTable(){// 모든 타임테이블 반환
		return this.roomTableList;
	},
	getTable(roomKey){// 해당 장소의 타임테이블 반환
		return this.roomTableList.find($table => $table.roomKey == roomKey);
	},
	generate(data = {}){
		const {
			holdingDate,
			openTime = "08:00",
			closeTime = "23:00",
			interval = 10,
			roomList
		} = data;
		this.holdingDate = holdingDate;
		this.openTime = openTime;
		this.closeTime = closeTime;
		this.interval = interval;
		const roomTableList = [];
		const $roomContainer = Util.getElement("#roomContainer");
		$roomContainer.innerHTML = "";
		for(const room of roomList){
			const $tableBox = this.createRoomTimeTableBox();
			const $roomTitle = this.createRoomTitle(room);
			$tableBox.appendChild($roomTitle);
			const $roomTable = this.createRoomTable(room);
			$tableBox.appendChild($roomTable);
			$roomContainer.appendChild($tableBox);
			$roomTable.roomKey = room.roomKey;
			roomTableList.push($roomTable);
		}
		this.roomTableList = roomTableList;
		/*
		const $roomTitContainer = Util.getElement("#roomTitContainer");
		$roomTitContainer.innerHTML = "";
		const $roomTimeContainer = Util.getElement("#roomTimeContainer");
		$roomTimeContainer.innerHTML = "";
		for(const room of roomList){
			const $roomTitle = this.createRoomTitle(room);
			$roomTitContainer.appendChild($roomTitle);
			const $roomTable = this.createRoomTable(room);
			$roomTimeContainer.appendChild($roomTable);
			$roomTable.roomKey = room.roomKey;
			roomTableList.push($roomTable);
		}
		this.roomTableList = roomTableList;
		*/
	},
	createRoomTimeTableBox(){
		const $tableBox = Util.createElement("div", "p-1", "flex-1", "roomTitContainer", "roomTimeContainer", "flex-column", "maxw-10", "h-100");
		return $tableBox;
	},
	createRoomTitle(room){
		const $title = Util.createElement("div", "roomTit", "w-100");
		$title.style.height = "60px"
		const $namespan = Util.createElement("span", "roomName", "my-1", "d-flex", "align-items-center");
		$namespan.innerHTML = room.roomLabel;
		$namespan.style.height = "50px"
		const $detailDiv = Util.createElement("div", "roomInfo", "d-flex", "flex-row", "w-100", "mb-1", "align-items-center");
		$detailDiv.style.height = "10px"
		const $floorspan = Util.createElement("div", "text-detail", "fs-7", "w-50");
		if(room.roomFloor){
			$floorspan.innerHTML = room.roomFloor + "층";
		}
		const $chairspan = Util.createElement("div", "text-detail", "fs-7", "ml-auto", "text-right", "w-50");
		if(room.roomSize){
			$chairspan.innerHTML = room.roomSize + "인실";
		}
		$detailDiv.appendChild($floorspan);
		$detailDiv.appendChild($chairspan);
		$title.appendChild($namespan);
		$title.appendChild($detailDiv);
		return $title;
	},
	createRoomTable(room){
		/**
		 * 테이블 시간별 Element생성
		 * 
		 * #data-time: HH:mm형의 시간 문자열
		 * #data-timeNum: Hmm형의 시간 숫자
		 * #time: data-time 접근자
		 * #timeNum: data-timeNum 접근자
		 * #locked: 잠금 여부
		 * #selected: 선택 여부
		 * #username: 텍스트(예약 작성자명 등)가 위치할 Element 접근자
		 * #lockingTime(): 해당 시간 Element를 선택 불가능하도록 변경
		 * #selectTime(): 해당 시간 Element 예약
		 * #releaseTime(): 해당 시간 Element의 선택 불가와 예약 모두 해제
		 * #onclick(): 클릭 이벤트. reserveHandler.selectTime(회의실 객체, 시간 Element, 테이블 Element)호출
		 */
		const createTimeRow = (time) => {
			const $row = Util.createElement("div", "timeRow", "selectable");
			const timeM = moment(time, "HH:mm");
			$row.hour = timeM.hour();
			$row.minute = timeM.minute();
			$row.dataset.time = time;
			$row.dataset.timeNum = $row.hour * 100 + $row.minute;
			const $text = Util.createElement("div", "timeText");
			$row.appendChild($text);
			
			const $minute = Util.createElement("span", "minute");
			$minute.innerHTML = time;
			const $username = Util.createElement("span", "username");
			$text.appendChild($minute);
			$text.appendChild($username);
			return $row;
		}
		/**
		 * 테이블 시간별 ELement를 담을 컨테이너 ELement
		 * #clear() 소속된 모든 Row Element에서 사용자 선택 취소
		 * #init() 소속된 모든 Row Element에서 예약 잠금 및 사용자 선택 취소
		 * #getAllRow() 소속된 모든 Row Element 반환
		 * #getRowInRange(sTimeNum, eTimeNum) 주어진 시간 범위내 모든 Row 반환
		 * hasLock(sTimeNum, eTimeNum) 범위내 잠금된 Row가 있는지 판별
		 */
		if(!this.tableTemplate){// 미리 테이블 템플릿을 생성하여 장소별 테이블 Element를 찍어냄
			const $tableTemplate = Util.createElement("div", "room");
			const openM = moment(this.openTime, "HH:mm");
			const closeM = moment(this.closeTime, "HH:mm");
			while(openM.isBefore(closeM)){
				const $row = createTimeRow(openM.format("HH:mm"));
				$tableTemplate.appendChild($row);
				openM.add(this.interval, "m");
			}
			this.tableTemplate = $tableTemplate;
		}
		const createTemplate = () => {
			const $tableTemplate = Util.createElement("div", "room");
			const openM = moment(room.openTime, "HH:mm");
			const closeM = moment(room.closeTime, "HH:mm");
			while(openM.isBefore(closeM)){
				const $row = createTimeRow(openM.format("HH:mm"));
				$tableTemplate.appendChild($row);
				openM.add(this.interval, "m");
			}
			return $tableTemplate;
		}
		const tableEvtBind = ($table ,room) => {// 복사된 템플릿에 이벤트 지정
			const $childRowList = Array.from($roomTable.children);
			$childRowList.forEach($row => {
				$row.locked = false;
				$row.selected = false;
				$row.disabled = false;
				$row.time = $row.dataset.time;
				$row.timeNum = $row.dataset.timeNum * 1;
				const $text = $row.querySelector(".timeText");
				const $username = $row.querySelector(".username");
				$row.username = $username;
				$row.disableTime = () => {
					Util.removeClass($row, "selectable");
					Util.removeClass($text, "reserved");
					$row.disabled = true;
					$row.onclick = null;
				}
				$row.lockingTime = () => {
					Util.removeClass($row, "selectable");
					Util.addClass($text, "reserved");
					$row.selected = false;
					$row.locked = true;
					$row.disabled = false;
				}
				$row.selectTime = () => {
					Util.addClass($text, "selected");
					$row.selected = true;
					$row.locked = false;
					$row.disabled = false;
				}
				$row.releaseTime = () => {
					Util.addClass($row, "selectable");
					Util.removeClass($text, "reserved", "selected");
					$row.selected = false;
					$row.locked = false;
					$row.disabled = false;
					$row.username.innerHTML = "";
					$row.onclick = () => {
						reserveHandler.selectTime(room, $row, $table);
					}
				}
			});
			// 타임테이블 사용자 선택 제거
			$table.clear = () => {
				$childRowList.forEach($row => {
					if($row.locked == false && $row.disabled == false){
						$row.releaseTime();
					}
				});
			}
			// 타임테이블 모든 변경 초기화
			$table.init = () => {
				$childRowList.forEach($row => {
					$row.releaseTime();
					$row.style.flexBasis = "";
					$row.style.display = "";
				});
			}
			// 모든 Row 반환
			$table.getAllRow = () => {
				return $childRowList;
			}
			// 주어진 시간 범위내 모든 Row 반환
			$table.getRowInRange = (sTimeNum, eTimeNum) => {
				return $childRowList.filter($row => $row.timeNum >= sTimeNum && $row.timeNum <= eTimeNum );
			}
			// 범위내 잠금된 Row가 있는지 판별
			$table.hasLock = (sTimeNum, eTimeNum) => {
				for(const $row of $childRowList){
					if($row.timeNum < sTimeNum){
						continue;
					}
					if($row.timeNum > eTimeNum){
						break;
					}
					if($row.locked == true){
						return true;
					}
				}
				return false;
			}
		}
		//const $roomTable = this.tableTemplate.cloneNode(true);
		const $roomTable = createTemplate()
		tableEvtBind($roomTable, room);
		return $roomTable;
	},
}
