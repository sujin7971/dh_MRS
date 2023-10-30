/**
 * 
 */
import {Util, Modal, AjaxBuilder} from '/resources/core-assets/essential_index.js';
import Semaphore from '/resources/library/async-mutex/es6/Semaphore.js';
import {MeetingCall as $MEETING, OrgCall as $ORG} from '/resources/front-end-assets/js/lime/ajax/ajax_index.js';

$(() => {
	calendarHandler.init({
		date: date
	});
	evtHandler.init();
});
/**
 * 달력 관련 제어
 */
const calendarHandler = {
	init(data = {}){
		this.today = moment().format('YYYY-MM-DD');
		const {
			date = today,
		} = data;
		moment.locale("ko");
		const onSelect = async (dateText, inst) => {
			if(!this.selectedDate){
				const selectedMonth = moment(dateText);
				await dataHandler.updateDailyStat(selectedMonth);
			}
			this.selectedDate = dateText;
			this.setPageDayTitle();
			this.setScheduleCard();
		}
		const onChangeMonthYear = async (year, month, inst) => {
			const selectedMonth = moment().year(year).month(month).subtract(1, 'month');
			await dataHandler.updateDailyStat(selectedMonth);
		}
		const beforeShowDay = (date) => {
			let count = dataHandler.dailyStat[moment(date).format('YYYY-MM-DD')];
			count = (count == undefined)?0:count;
			if(count == 0){
				return [true, "schedule-none", ""];
			}else{
				return [true, "schedule", count+""];
			}
		}
		$("#datepicker").datepicker({
			dateFormat: "yy-mm-dd",
			defaultDate: this.selectedDate,
			onSelect : onSelect,
			onChangeMonthYear : onChangeMonthYear,
			beforeShowDay: beforeShowDay
		});
		onSelect(date);
	},
	setPageDayTitle(){
		const $calTit = document.querySelector("#calendarTit");
		$calTit.innerHTML = "";
		let date = "";
		if(this.selectedDate != this.today) {
			date = moment(this.selectedDate).format('MM월 DD일');
		}else {
			date= "오늘";
		}
		const $date = Util.createElement("span", "mr-1");
		$date.innerHTML = date+"의 스케줄";
		$calTit.appendChild($date);
	},
	async setScheduleCard(){
		const $assignBtnList = Util.getElementAll('[data-btn=assign]');
		$assignBtnList.forEach($btn => {
			$btn.style.display = "none";
			$btn.onlick = '';
		});
		await dataHandler.updateScheduleList(this.selectedDate);
		if(this.today <= this.selectedDate){
			$assignBtnList.forEach($btn => {
				$btn.style.display = "";
				$btn.onclick = () => {
					location.href = "/lime/meeting/assign/status/timetable"+"?holdingDate="+this.selectedDate;
				}
			});
		}
	},
}
/**
 * 스케줄 카드 생성
 */
const scheduleCardGenerator = {
	generate(scheduleList){
		const $cardBox = document.querySelector("#cardBox");
		$cardBox.innerHTML = "";
		if(scheduleList && scheduleList.length > 0){
			for(const assign of scheduleList){
				const $card = this.createRow(assign);
				assign.cardElem = $card;
				$cardBox.appendChild($card);
			}
			timerHandler.start(scheduleList);
		}else{
			timerHandler.clear();
		}
	},
	clear(){
		const $cardBox = document.querySelector("#cardBox");
		$cardBox.innerHTML = "";
	},
	loading(count){
		const $cardBox = document.querySelector("#cardBox");
		count = (count)?count:1;
		this.clear();
		while(count--){
			const $loadingRow = this.createLoadingRow();
			$cardBox.appendChild($loadingRow);
		}
	},
	createLoadingRow(){
		const $tempDiv = 	'<div class="cardDiv placeholder-wave placeholder">' +
							'<div class="mPropertyDiv">' +
							'<div class="mDivision placeholder"></div>' +
							'<div class="electDoc placeholder" title="전자회의"></div>' +
							'<div class="myTab placeholder"></div>' +
							'<div class="cancelTab placeholder"></div>' +
							'<div class="regTab placeholder"></div>' +
							'<div class="attend mandatory placeholder"></div>' +
							'<div class="attend option placeholder"></div>' +
							'<div class="attend finishIn placeholder"></div>' +
							'<div class="attend finishNo placeholder"></div>' +
							'<div class="count placeholder"></div>' +
							'</div>' +
							'<div class="timePlaceDiv">' +
							'<div class="meetTime placeholder"><span></span></div>' +
							'<div class="meetPlace placeholder"></div>' +
							'</div>' +
							'<div class="pubSecDiv placeholder w-20">' +
							'<span></span>' +
							'</div>' +
							'<div class="meetTit placeholder w-80"></div>' +
							'<div class="meetHost placeholder w-60"><span class="placeholder w-20"></span><span class="placeholder w-20"></span><span class="placeholder w-20"></span></div>' +
							'</div>';
		return Util.createFromString($tempDiv);
	},
	createRow(assign){
		const $cardDiv = Util.createElement("div", "cardDiv");
		const setPropClass = () => {
			const propClassSet = new Set();
			const roomType = assign.roomType;
			propClassSet.add(this.getRoomTypeProp(roomType));
			const approvalStatus = assign.approvalStatus;
			if(approvalStatus == "APPROVED"){
				propClassSet.add(this.getMeetingStatusProp(assign.meetingStatus));
			}else{
				propClassSet.add(this.getSkdStatusProp(approvalStatus));
			}
			
			if(assign.secretYN == 'Y'){
				propClassSet.add("secu");
			}
			if(assign.elecYN == 'Y'){
				propClassSet.add("elec");
				if(assign.writerId == loginId){
					propClassSet.add("writer");
				}
				if(assign.attendeeList && assign.attendeeList.length != 0){
					const attendee = assign.attendeeList[0];
					if(attendee.attendRole == "FACILITATOR"){
						propClassSet.add("my");
					}
					if(assign.meetingStatus == "END"){
						if(attendee.attendYN == 'Y'){
							propClassSet.add("a3");
						}else{
							propClassSet.add("a4");
						}
					}else{
						if(attendee.attendRole == "FACILITATOR"){
							propClassSet.add("a1");
						}else if(attendee.attendRole == "ATTENDEE"){
							propClassSet.add("a1");
						}else{
							propClassSet.add("a2");
						}
					}
				}
			}else{
				if(assign.writerId == loginId){
					propClassSet.add("my");
				}
			}
			Util.addClass($cardDiv, ...propClassSet);
		}
		const createPropertyDiv = () => {
			const $propertyDiv = Util.createElement("div", "mPropertyDiv");
			
			const $mDivision = Util.createElement("div", "mDivision");
			const $electDoc = Util.createElement("div", "electDoc");
			const $myTab = Util.createElement("div", "myTab");
			const $cancelTab = Util.createElement("div", "cancelTab");
			const $regTab = Util.createElement("div", "regTab");
			//const $mandatory = Util.createElement("div", "attend", "mandatory");
			const $option = Util.createElement("div", "attend", "option");
			const $finishIn = Util.createElement("div", "attend", "finishIn");
			const $finishNo = Util.createElement("div", "attend", "finishNo");
			const $count = Util.createElement("div", "count");
			
			$propertyDiv.appendChild($mDivision);
			$propertyDiv.appendChild($electDoc);
			/* cancelTab regTabm myTab: 뒤에 오는 탭이 제일 우선됨*/
			$propertyDiv.appendChild($cancelTab);
			$propertyDiv.appendChild($regTab);
			$propertyDiv.appendChild($myTab);
			//$propertyDiv.appendChild($mandatory);
			$propertyDiv.appendChild($option);
			$propertyDiv.appendChild($finishIn);
			$propertyDiv.appendChild($finishNo);
			$propertyDiv.appendChild($count);
			$cardDiv.showCount = () => {
				$count.style.visibility = "visible";
			}
			$cardDiv.hideCount = () => {
				$count.style.visibility = "hidden";
			}
			$cardDiv.removeCount = () => {
				$count.remove();
			}
			$cardDiv.setCount = (text) => {
				$count.innerHTML = text;
			}
			
			return $propertyDiv;
		}
		const createScheduleDiv = () => {
			const $scheduleDiv = Util.createElement("div", "timePlaceDiv");
			
			const $dateDiv = Util.createElement("div", "meetDate");
			const holdingDate = moment(assign.holdingDate).format("YYYY.MM.DD");
			$dateDiv.innerHTML = holdingDate;
			
			const $timeDiv = Util.createElement("div", "meetTime");
			const stime = moment(assign.beginDateTime).format("HH:mm");
			$timeDiv.innerHTML = stime + " ";
			
			const $etime = Util.createElement("span");
			const etime = moment(assign.finishDateTime).format("HH:mm");
			$etime.innerHTML = "~ " + etime;
			
			$timeDiv.appendChild($etime);
			
			const $roomDiv = Util.createElement("div", "meetPlace");
			const room = assign.room;
			$roomDiv.innerHTML = room?.roomName;
			
			$scheduleDiv.appendChild($dateDiv);
			$scheduleDiv.appendChild($timeDiv);
			$scheduleDiv.appendChild($roomDiv);
			
			return $scheduleDiv;
		}
		const createSecuDiv = () => {
			const $secuDiv = Util.createElement("div", "pubSecDiv");
			const $span = Util.createElement("span");
			$secuDiv.appendChild($span);
			
			return $secuDiv;
		}
		const createTitleDiv = () => {
			const $titleDiv = Util.createElement("div", "meetTit");
			$titleDiv.innerHTML = assign.title;
			return $titleDiv;
		}
		const createHostDiv = () => {
			const $hostDiv = Util.createElement("div", "meetTit");
			$hostDiv.innerHTML = assign.scheduleHost;
			return $hostDiv;
		}
		setPropClass();
		const $propertyDiv = createPropertyDiv();
		$cardDiv.appendChild($propertyDiv);
		const $scheduleDiv = createScheduleDiv();
		$cardDiv.appendChild($scheduleDiv);
		const $secuDiv = createSecuDiv();
		$cardDiv.appendChild($secuDiv);
		const $titleDiv = createTitleDiv();
		$cardDiv.appendChild($titleDiv);
		const $hostDiv = createHostDiv();
		$cardDiv.appendChild($hostDiv);
		
		$cardDiv.onclick = () => {
			const {scheduleId} = assign;
			location.href = `/lime/meeting/assign/${scheduleId}`;
		}
		
		return $cardDiv;
	},
	getRoomTypeProp(roomType){
		if(roomType == "MEETING_ROOM"){
			return "mr";
		}else if(roomType == "EDU_ROOM"){
			return "lr";
		}else if(roomType == "HALL"){
			return "at";
		}
	},
	getSkdStatusProp(status){
		switch(status){
		  case "REQUEST":
		    return "s0";
		  case "APPROVED":
		    return this.getMeetingStatusProp(assign.meetingStatus);
		  case "CANCELED":
		    return "s4";
		  case "REJECTED":
		    return "s5";
		}
	},
	getMeetingStatusProp(status){
		switch(status){
			case "FINISH":
			case "CLOSING":
			case "END":
				return "s3";
				break;
			case "OPENING":
			case "APPROVED":
				return "s1";
				break;
			default:
				return "s2";
				break;
		}
	},
	getSecuProp(secuLvl){
		if(secuLvl == 1){
			return "secu";
		}
	}
}

const evtHandler = {
	init(){
		this.setHelpModal();
		this.setStatModal();
	},
	setHelpModal(){
		const $btn = Util.getElement("#scheduleHelpBtn");
		if($btn){
			$btn.onclick = () => {
				Modal.get("scheduleHelpModal").show();
			}
		}
	},
	setStatModal(){
		const $btn = Util.getElement("#statHelpBtn");
		if($btn){
			$btn.onclick = () => {
				Modal.get("statHelpModal").show();
			}
		}
	},
}
/**
 * 회의 진행상황에 대한 카운트다운 처리. 
 * 선택된 일자의 스케줄 리스트를 대상으로 타이머가 작동하고 타이머가 필요한 스케줄이 없는 경우 작동 중지.
 */
const timerHandler = {
	start(list){
		this.clear();
		this.assignSet = new Set(list);
		this.semaphore = new Semaphore(list.length);
		const syncSchedule = async (assign) => {
			const nowMtime = moment();
			switch(assign.approvalStatus){
				case "REQUEST":
				case "CANCELED":
				case "REJECTED":
					assign.cardElem.removeCount();
					this.assignSet.delete(assign);
					break;
				case "APPROVED":
					const beginMtime = moment(assign.beginDateTime);
					const diffTime = beginMtime.diff(nowMtime, 'seconds');
					if(diffTime < 0) {
						try{
							AjaxBuilder({
								request: $MEETING.Get.userAuthorityForMeeting,
								param: assign.meetingId,
								loading: false,
							}).success(authorities => {
								if(authorities.includes("FUNC_VIEW")){
									assign.cardElem.setCount("");
									Util.addClass(assign.cardElem, "s2");
								}else{
									assign.cardElem.onclick = () => {
										//Modal.info({msg: "회의를 조회할 권한이 없습니다."});
										Modal.get("authorityInfoModal").show();
									};
									//Util.addClass(assign.cardElem, "disabled");
								}
							}).finally(() => {
								this.assignSet.delete(assign);
							}).exe();
						}catch(err){
							console.log(err);
							this.clear();
							Modal.error({response: err});
						}
					}else if(diffTime < 3600){
						assign.cardElem.showCount();
						assign.cardElem.setCount((Math.floor(diffTime / 60) + 1)+"분전");
					}else{
						assign.cardElem.hideCount();
					}
					break;
			}
		}
		const assignCountdown = async () => {
			if(this.assignSet.size == 0){
				this.clear();
				return;
			}
			this.assignSet.forEach(async (assign) => {
				const [value, release] = await this.semaphore.acquire();
				await syncSchedule(assign);
				release();
			});
		}
		assignCountdown();
		this.intervalTask = setInterval(assignCountdown, 1000);
	},
	clear(){
		if(this.intervalTask){
			clearInterval(this.intervalTask);
			this.intervalTask = null;
		}
	},
}

const dataHandler = {
	dailyStat: {},
	async updateDailyStat(momentMonth) {
		return new Promise((resolve, reject) => {
			const getMonthlyPeriod = (momentMonth) => {
				const startOfMonth = momentMonth.startOf('month').format("YYYY-MM-DD");
				const endOfMonth   = momentMonth.endOf('month').format("YYYY-MM-DD");
				return {
					startDate: startOfMonth,
					endDate: endOfMonth
				}
			}
			AjaxBuilder({
				request: $MEETING.Get.assignStatForPlanned,
				param: getMonthlyPeriod(momentMonth),
				exceptionModal: false,
				loading: false
			}).success((list) => {
				list.forEach(stat => {
					this.dailyStat[stat.refDate] = stat.statValue1;
				});
			}).error((err) => {
				this.dailyStat = {};
			}).finally(() => {
				$("#datepicker").datepicker("refresh");
				resolve();
			}).exe();
		});
	},
	async updateScheduleList(selectedDate){
		if(this.dailyStat[selectedDate] == undefined){
			scheduleCardGenerator.clear();
			return;
		}
		scheduleCardGenerator.loading(this.dailyStat[selectedDate]);
		AjaxBuilder({
			request: $MEETING.Get.assignListForPlanned,
			param: {
				startDate: selectedDate,
				endDate: selectedDate
			},
			loading: false
		}).success((list) => {
			scheduleCardGenerator.generate(list);
		}).finally(() => {
			$("#datepicker").datepicker("refresh");
		}).exe();
	}
}

