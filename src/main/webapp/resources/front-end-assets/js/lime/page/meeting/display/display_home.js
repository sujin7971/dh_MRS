/**
 * 배정현황 목록보기 페이지 스크립트
 * 
 * @author mckim
 */

import {Util, Modal, AjaxBuilder} from '/resources/core-assets/essential_index.js';
import {SearchBar} from '/resources/front-end-assets/js/lime/comm/module_index.js';
import {UserCall as $USER, RoomCall as $RM, AssignCall as $AS} from '/resources/front-end-assets/js/lime/ajax/ajax_index.js';

$(() => {
	searchHandler.init({
		officeCode: officeCode,
		roomType: roomType,
		startDate: startDate,
		endDate: endDate,
	});
	const $officeDisplayMonitoringBtn = Util.getElement("#officeDisplayMonitoringBtn");
	$officeDisplayMonitoringBtn.onclick = () => {
		const {
			officeCode,
		} = SearchBar.getSearchInput();
		location.href = `/lime/display/${officeCode}/main`;
	}
});

const searchHandler = {
	async init(data = {}){
		const {
			officeCode,
			roomType,
			startDate,
			endDate,
		} = data;
		SearchBar.init({
			officeCode: officeCode,
			roomType: roomType,
			startDate: startDate,
			endDate: endDate,
		});
		SearchBar.initOffice();
		SearchBar.initRoomTypeCheck();
		SearchBar.initRoomSelect();
		SearchBar.initPeriodPicker();
		const nowDateM = moment();
		SearchBar.enableResetBtn({
			startDate: nowDateM.clone().format("YYYY-MM-DD"),
			endDate: nowDateM.clone().add(7, "d").format("YYYY-MM-DD"),
		});
		SearchBar.on("change", async (input, value) => {
			Modal.startLoading();
			switch(input){
				case "office":
					SearchBar.selectRoomType?.("MEETING_ROOM");
				case "roomType":
					await this.setApprovalManager();
					await this.setRoomList();
					await this.search(SearchBar.getSearchInput());
			}
			Modal.endLoading();
		});
		SearchBar.on("search", (data = {}) => {
			this.search(data);
		});
		SearchBar.on("reset", (data = {}) => {
			this.reset(data);
		});
		this.setApprovalManager();
		await this.setRoomList();
		this.search(SearchBar.getSearchInput());
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
	async setRoomList(){
		const {
			officeCode,
			roomType,
		} = SearchBar.getSearchInput();
		await AjaxBuilder({
			request: $RM.Get.publicListForDisplay,
			param: {
				officeCode: officeCode,
				roomType: roomType,
			}
		}).success(list => {
			this.roomList = list;
		}).error(() => {
			roomBoxBuilder.clean();
		}).exe();
	},
	async search(data = {}){
		const {
			startDate = this.startDate,
			endDate = this.endDate,
		} = data;
		this.startDate = startDate;
		this.endDate = endDate;
		let roomList = this.roomList;
		roomBoxBuilder.build({
			startDate: this.startDate,
			endDate: this.endDate,
			roomList: roomList,
		})
	},
	async reset(data){
		//SearchBar.initSearchInput();
		await this.setRoomList();
		this.search(data);
	},
}

const roomBoxBuilder = {
	clean(){
		const $resultBox = document.querySelector("#resultBox");
		$resultBox.innerHTML = "";
	},
	build(data = {}){
		const {
			startDate,
			endDate,
			roomList
		} = data;
		this.startDate = startDate;
		this.endDate = endDate;
		const $resultBox = document.querySelector("#resultBox");
		$resultBox.innerHTML = "";
		for(const room of roomList){
			const $roomBox = this.createRoomBox(room);
			$resultBox.appendChild($roomBox);
		}
	},
	createRoomBox(room){
		const $table = Util.createElement("div", "tableDiv");
		const createBoxTitle = () => {
			const $titleBox = Util.createElement("div", "tableTitDiv");
			
			const $title = Util.createElement("div", "tableTit");
			$title.innerHTML = room.roomName;
			$titleBox.appendChild($title);
			
			const $btnBox = Util.createElement("div", "tableBtn");
			const $postBtn = Util.createElement("button", "btn", "btn-blue", "btn-sm");
			const startDate = this.startDate;
			$postBtn.onclick = () => {
				const {
					officeCode,
					roomType,
					roomId,
				} = room;
				location.href = `/lime/display/${officeCode}/${roomType}/${roomId}`;
			}
			$postBtn.innerHTML = "현황판";
			$btnBox.appendChild($postBtn);
			$titleBox.appendChild($btnBox);
			$table.appendChild($titleBox);
		}
		const createRoomStatusListBox = () => {
			const createListHeader = () =>{
				const $headerBox = Util.createElement("div", "listHeaderDiv");
				const $headerRow = Util.createElement("div", "row");
				$headerBox.appendChild($headerRow);
				
				const $noItem = Util.createElement("div", "item", "no");
				$noItem.innerHTML = "No";
				$headerRow.appendChild($noItem);
				
				const $holdingDateItem = Util.createElement("div", "item", "dateTime", "justify-content-start");
				$holdingDateItem.innerHTML = "사용일시";
				$headerRow.appendChild($holdingDateItem);
				
				const $titleItem = Util.createElement("div", "item", "title");
				$titleItem.innerHTML = "제 목";
				$headerRow.appendChild($titleItem);
				
				const $writerItem = Util.createElement("div", "item", "host", "justify-content-start");
				$writerItem.innerHTML = "신청자";
				$headerRow.appendChild($writerItem);
				
				const $hostItem = Util.createElement("div", "item", "host", "justify-content-start");
				$hostItem.innerHTML = "주관자";
				$headerRow.appendChild($hostItem);
				
				const $statusItem = Util.createElement("div", "item", "status");
				$statusItem.innerHTML = "상 태";
				$headerRow.appendChild($statusItem);
				
				const $regDateItem = Util.createElement("div", "item", "regDate", "justify-content-start");
				$regDateItem.innerHTML = "등록일";
				$headerRow.appendChild($regDateItem);
				
				$table.appendChild($headerBox);
			}
			const createListBody = () => {
				const createListRow = (assign) => {
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
					const $rowBox = Util.createElement("div", "row", getAppStatusCls(assign.approvalStatus));
					
					const $noItem = Util.createElement("div", "item", "no");
					$noItem.innerHTML = assign.scheduleId;
					$rowBox.appendChild($noItem);
					
					const $dateTimeItem = Util.createElement("div", "item", "dateTime");
					$rowBox.appendChild($dateTimeItem);
					const $dateItem = Util.createElement("span", "date");
					$dateItem.innerHTML = assign.holdingDate;
					$dateTimeItem.appendChild($dateItem);
					const $timeItem = Util.createElement("span", "time");
					$timeItem.innerHTML = moment(assign.beginDateTime).format("HH:mm") + "~" + moment(assign.finishDateTime).format("HH:mm");
					$dateTimeItem.appendChild($timeItem);
					$rowBox.appendChild($dateTimeItem);
					
					const $titleItem = Util.createElement("div", "item", "title");
					$titleItem.innerHTML = assign.title;
					$titleItem.onclick = () => {
						//location.href = "/lime/room/"+this._roomType+"/assign/"+assign.seqReq;
					}
					$rowBox.appendChild($titleItem);
					
					const $writerItem = Util.createElement("div", "item", "host");
					const $writerToolTip = Util.createElement("span", "headerTip");
					$writerToolTip.innerHTML = "신청자 : ";
					$writerItem.appendChild($writerToolTip);
					const $writerNameplate = Util.createElement("div");
					$writerNameplate.innerHTML = assign.writer.nameplate;
					$writerItem.appendChild($writerNameplate);
					$rowBox.appendChild($writerItem);
					
					const $hostItem = Util.createElement("div", "item", "host");
					const $toolTip = Util.createElement("span", "headerTip");
					$toolTip.innerHTML = "주관자 : ";
					$hostItem.appendChild($toolTip);
					const $nameplate = Util.createElement("div");
					$nameplate.innerHTML = assign.scheduleHost;
					$hostItem.appendChild($nameplate);
					$rowBox.appendChild($hostItem);
					
					const $statusItem = Util.createElement("div", "item", "status");
					$statusItem.appendChild(Util.createElement("span"));
					$rowBox.appendChild($statusItem);
					const $regDateItem = Util.createElement("div", "item", "regDate");
					$regDateItem.innerHTML = moment(assign.regDateTime).format("YYYY.MM.DD");
					$rowBox.appendChild($regDateItem);
					return $rowBox;
				}
				const $listBody = Util.createElement("div", "listBodyDiv");
				$AS.Get.assignPublicListForDisplay({
					roomType: room.roomType,
					roomId: room.roomId,
					startDate: this.startDate,
					endDate: this.endDate,
				}).then((list) => {
					if(!list || list.length == 0){
						//$listBody.deleteHeader();
						return;
					}
					for(const assign of list){
						const $row = createListRow(assign);
						$listBody.appendChild($row);
					}
				});
				$table.appendChild($listBody);
			}
			createListHeader();
			createListBody();
		}
		createBoxTitle();
		createRoomStatusListBox();
		return $table;
	},
}
