/**
 * 사용신청 처리 페이지 스크립트
 * 
 * @author mckim
 */
import {Util, Modal, AjaxBuilder} from '/resources/core-assets/essential_index.js';
import {SearchBar} from '/resources/front-end-assets/js/lime/comm/module_index.js';
import {AssignCall as $AS, AdminCall as $ADM} from '/resources/front-end-assets/js/lime/ajax/ajax_index.js';

$(() => {
	searchHandler.init({
		officeCode: officeCode,
		approvalStatus: approvalStatus,
		roomType: roomType,
		host: host,
		startDate: startDate,
		endDate: endDate,
		pageNo: pageNo
	});
	evtHandler.init({
		officeCode: officeCode,
	});
});

const evtHandler = {
	init(data = {}){
		const {
			officeCode
		} = data;
		this.setAutoApprovalSwitch(officeCode);
	},
	//사용신청 자동승인 체크박스 설정
	async setAutoApprovalSwitch(officeCode){
		const $checkbox = Util.getElement("#autoConfirm");
		$checkbox.disabled = true;
		if($checkbox){
			$checkbox.onchange = async () => {
				$checkbox.disabled = true;
				let autoYN = 'N'
				if($checkbox.checked == true){
					autoYN = 'Y';
				}
				try{
					$ADM.Put.officeAutoApproval({
						officeCode: officeCode,
						autoYN: autoYN,
					});
				}catch(err){
					Modal.error({response: err});
				}finally{
					if($checkbox.checked == true){ // 자동승인 on
						Modal.info({
							msg: "예약등록되는 건들을 시스템이 자동승인처리 합니다."
						});
					} else { // 자동승인 off
						Modal.info({
							msg: "예약 자동승인을 해제 하시면 예약등록되는 건들을 수동으로 승인처리 하셔야 합니다."
						});
					}
					$checkbox.disabled = false;
				}
			}
		}
		/* 서버로부터 현재 자동결재 여부 전달받아 설정 완료 후 체크박스 활성화 */
		const approvalPolicy = await $ADM.Get.officeApprovalPolicy(officeCode);
		if(approvalPolicy == "AUTH_AUTO"){
			$checkbox.checked = true;
		}else{
			$checkbox.checked = false;
		}
		$checkbox.disabled = false;
	},
}

const searchHandler = {
	init(data = {}){
		const {
			officeCode,
			approvalStatus,
			roomType,
			host,
			startDate,
			endDate,
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
		    	this.pageMove(page);
		    }
		});
		
		SearchBar.init({
			officeCode: officeCode,
			approvalStatus: approvalStatus,
			roomType: roomType,
			host: host,
			startDate: startDate,
			endDate: endDate,
		});
		SearchBar.initOffice();
		SearchBar.initApprovalStatus();
		SearchBar.initPeriodPicker();
		SearchBar.initRoomTypeSelect();
		SearchBar.initHost();
		const nowDateM = moment();
		SearchBar.enableResetBtn({
			startDate: nowDateM.clone().format("YYYY-MM-DD"),
			endDate: nowDateM.clone().add(7, "d").format("YYYY-MM-DD"),
		});
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
			officeCode = this.officeCode,
			approvalStatus = this.approvalStatus,
			roomType = this.roomType,
			startDate = this.startDate,
			endDate = this.endDate,
			host = this.host
		} = data;
		this.officeCode = officeCode;
		this.approvalStatus = approvalStatus;
		this.roomType = roomType;
		this.startDate = startDate;
		this.endDate = endDate;
		this.roomType = roomType;
		this.host = host;
		this.totalCnt = null;
		await this.loadPage();
	},
	reset(data){
		this.pageNo = 1;
		this.search(data);
	},
	async pageMove(pageNo){
		this.pageNo = pageNo;
		await this.loadPage();
	},
	async loadPage(){
		if(!this.totalCnt){
			const result = await this.setPagination();
			if(!result){
				return;
			}
		}
		await this.showList();
	},
	async setPagination(){
		let result = true;
		assignRowGenerator.clear();
		$("#pagination").empty();
		await AjaxBuilder({
			request: $AS.Get.assignListCnt,
			param: {
				officeCode: this.officeCode,
				approvalStatus: this.approvalStatus,
				roomType: this.roomType,
				startDate: this.startDate,
				endDate: this.endDate,
				host: this.host,
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
	},
	async showList(){
		await AjaxBuilder({
			request: await $AS.Get.assignList,
			param: {
				officeCode: this.officeCode,
				approvalStatus: this.approvalStatus,
				roomType: this.roomType,
				startDate: this.startDate,
				endDate: this.endDate,
				host: this.host,
				pageNo: this.pageNo,
				pageCnt: this.pageCnt,
			}
		}).success(list => {
			assignRowGenerator.generate(this.totalCnt, list);
		}).exe();
	}
}

const assignRowGenerator = {
	clear(){
		const $listBox = Util.getElement("#listBox");
		$listBox.innerHTML = "";
	},
	generate(totalCnt, assignList){
		this.clear();
		const $listBox = Util.getElement("#listBox");
		for(const assign of assignList){
			const $row = this.createRow(totalCnt, assign);
			$listBox.appendChild($row);
		}
	},
	createRow(totalCnt, assign){
		const $row = Util.createElement("div", "row");
		const createNoColumn = () => {
			const $column = Util.createElement("div", "item", "no");
			$column.innerHTML = assign.scheduleId;
			
			$row.appendChild($column);
		}
		// 결재 상태 칼럼
		const createStatusColumn = () => {
			const $column = Util.createElement("div", "item", "status");
			const $span = Util.createElement("span");
			$column.appendChild($span);
			const getAppStatusCls = (status) => {
				switch(status){
				  case "REQUEST":
				    return "s0";
				  case "APPROVED":
				    return "s1";
				  case "CANCELED":
				    return "s4";
				  case "REJECTED":
				    return "s5";
				}
			}
			Util.addClass($row, getAppStatusCls(assign.approvalStatus));
			$row.appendChild($column);
		}
		// 장소구분 칼럼
		const createRoomTypeColumn = () => {
			const $column = Util.createElement("div", "item", "type");
			const $span = Util.createElement("span");
			$column.appendChild($span);
			if(assign.room.roomType == "MEETING_ROOM"){
				Util.addClass($row, "mr");
			}else if(assign.room.roomType == "EDU_ROOM"){
				Util.addClass($row, "lr");
			}else if(assign.room.roomType == "HALL"){
				Util.addClass($row, "at");
			}
			
			$row.appendChild($column);
		}
		// 일시/장소 칼럼
		const createScheduleColumn = () => {
			const $column = Util.createElement("div", "item", "dateTimeRoom");
			
			const $dateTimeDiv = Util.createElement("div");
			const $dateSpan = Util.createElement("span", "date");
			$dateSpan.innerHTML = moment(assign.holdingDate).format("YYYY.MM.DD");
			const $timeSpan = Util.createElement("span", "time");
			$timeSpan.innerHTML = moment(assign.beginDateTime).format("HH:mm")+"~"+moment(assign.finishDateTime).format("HH:mm")
			$dateTimeDiv.appendChild($dateSpan);
			$dateTimeDiv.appendChild($timeSpan);
			$column.appendChild($dateTimeDiv);
			
			const $roomDiv = Util.createElement("div");
			$roomDiv.innerHTML = assign.room?.roomName;
			$column.appendChild($roomDiv);
			
			$row.appendChild($column);
		}
		// 제목 칼럼
		const createTitleColumn = () => {
			const $column = Util.createElement("div", "item", "title");
			const $a = Util.createElement("a");
			$column.appendChild($a);
			$a.innerHTML = assign.title;
			$a.href = "/lime/manager/approval/meeting/assign/"+assign.scheduleId;
			$row.appendChild($column);
		}
		// 주관자 칼럼
		const createHostColumn = () => {
			const $column = Util.createElement("div", "item", "host");
			const $tipSpan = Util.createElement("span", "headerTip");
			$tipSpan.innerHTML = "주관자: ";
			$column.appendChild($tipSpan);
			
			const $nameSpan = Util.createElement("div");
			$nameSpan.innerHTML = assign.scheduleHost;
			$column.appendChild($nameSpan);

			$row.appendChild($column);
		}
		// 등록일 칼럼
		const createRegDateColumn = () => {
			const $column = Util.createElement("div", "item", "regDate");
			const $tipSpan = Util.createElement("span", "headerTip");
			$tipSpan.innerHTML = "등록일: ";
			$column.appendChild($tipSpan);
			
			const regDateM = moment(assign.regDateTime);
			const $dateSpan = Util.createElement("span", "date");
			$dateSpan.innerHTML = regDateM.format("YYYY.MM.DD");
			const $timeSpan = Util.createElement("span", "time");
			$timeSpan.innerHTML = regDateM.format("HH:mm");
			$column.appendChild($dateSpan);
			$column.appendChild($timeSpan);
			
			$row.appendChild($column);
		}
		createNoColumn();
		createStatusColumn();
		//createRoomTypeColumn();
		createScheduleColumn();
		createTitleColumn();
		createHostColumn();
		createRegDateColumn();
		
		return $row;
	},
}
