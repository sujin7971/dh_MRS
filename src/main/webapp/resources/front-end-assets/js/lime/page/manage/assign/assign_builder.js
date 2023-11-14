/**
 * 
 */

import {eventMixin, Util, Modal} from '/resources/core-assets/essential_index.js';
import {MeetingCall as $MEETING} from '/resources/front-end-assets/js/lime/ajax/ajax_index.js';

/**
 * 회의목록 페이지 버튼 제어
 */
export const assignEvtHandler = {
	__proto__: eventMixin,
	init(){
		
	},
	enableScrollPagination(){
		const $listBox = Util.getElement(".meetingListMyDiv");
		Util.setVerticalScroll($listBox, () => {
			this.trigger("scrollBottom");
		});
	},
	disableScrollPagination(){
		const $listBox = Util.getElement(".meetingListMyDiv");
		$listBox.unsetVerticalScroll?.();
	}
}
/**
 * 사용신청목록 테이블 로우($row) 생성하여 테이블 노드에 추가.
 */
export const assignRowGenerator = {
	clear(){
		const $listBox = Util.getElement("#listBox");
		$listBox.innerHTML = "";
	},
	getContainer(){
		const $listBox = Util.getElement("#listBox");
		return $listBox;
	},
	generate(assignList){
		const $listBox = Util.getElement("#listBox");
		for(const assign of assignList){
			const $row = this.createRow(assign);
			$listBox.appendChild($row);
		}
	},
	createRow(assign){
		const $row = Util.createElement("div", "row");
		// 반호 칼럼
		const createNoColumn = () => {
			const $column = Util.createElement("div", "item", "no");
			$column.innerHTML = assign.scheduleId;
			
			$row.appendChild($column);
		}
		// 결재상태 칼럼
		const createApprovalStatusColumn = () => {
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
		// 진행상태 칼럼
		const createMeetingStatusColumn = () => {
			const $column = Util.createElement("div", "item", "status");
			const $span = Util.createElement("span");
			$column.appendChild($span);
			const statusCls = {
				"UNAPPROVAL": "s0",
				"APPROVED": "s1",
				"START": "s2",
				"END": "s3",
				"CANCEL": "s4",
				"DROP": "s5"
			}
			const getStatusCls = (status) => {
				switch(status){
					case "UNAPPROVAL": 
						return "s0";
					case "APPROVED":
						return "s1";
					case "START":
						return "s2";
					case  "END": 
						return "s3";
					case "CANCEL":
						return "s4";
					case "DROP":
						return "s5";
					default:
						return "s2";
				}
			}
			Util.addClass($row, getStatusCls(assign.meetingStatus));
			$row.appendChild($column);
		}
		// 장소구분 칼럼
		const createRoomTypeColumn = () => {
			const $column = Util.createElement("div", "item", "type");
			const $span = Util.createElement("span");
			$column.appendChild($span);
			switch(assign.room?.roomType){
				case "MEETING_ROOM":
					Util.addClass($row, "mr");
					break;
				case "EDU_ROOM":
					Util.addClass($row, "lr");
					break;
				case "HALL":
					Util.addClass($row, "at");
					break;
			}
			//$row.appendChild($column);
		}
		// 전자회의 칼럼
		const createElecColumn = () => {
			const $column = Util.createElement("div", "item", "elecDocs");
			const $span = Util.createElement("span");
			$column.appendChild($span);
			if(assign.elecYN == 'Y'){
				Util.addClass($row, "elec");
			}
			//$row.appendChild($column);
		}
		// 일시/장소 칼럼
		const createScheduleColumn = () => {
			const $column = Util.createElement("div", "item", "dateTimeRoom");
			
			const $dateTimeDiv = Util.createElement("div");
			$column.appendChild($dateTimeDiv);
			const $dateSpan = Util.createElement("span", "date");
			$dateSpan.innerHTML = moment(assign.holdingDate).format("YYYY.MM.DD");
			const $timeSpan = Util.createElement("span", "time");
			$timeSpan.innerHTML = moment(assign.beginDateTime).format("HH:mm")+"~"+moment(assign.finishDateTime).format("HH:mm")
			$dateTimeDiv.appendChild($dateSpan);
			$dateTimeDiv.appendChild($timeSpan);
			
			const $roomDiv = Util.createElement("div");
			$column.appendChild($roomDiv);
			$roomDiv.innerHTML = assign.room?.roomName;
			
			$row.appendChild($column);
		}
		// 보안설정 칼럼
		const createSecretColumn = () => {
			const $column = Util.createElement("div", "item", "security");
			const $span = Util.createElement("span");
			$column.appendChild($span);
			if(assign.secretYN == 'Y'){
				Util.addClass($row, "secu");
			}
			$row.appendChild($column);
		}
		// 제목 칼럼
		const createTitleColumn = () => {
			const $column = Util.createElement("div", "item", "title");
			const $title = Util.createElement("div", "href-link");
			$column.appendChild($title);
			$title.innerHTML = assign.title;
			$title.onclick = async () => {
				const authorities = await $MEETING.Get.userAuthorityForMeeting(assign.meetingId);
				if(!authorities.includes("FUNC_VIEW")){
					//Util.removeClass($title, "href-link");
					//$title.onclick = "";
					Modal.get("authorityInfoModal").show();
				}else{
					const {scheduleId} = assign;
					location.href = `/lime/meeting/assign/${scheduleId}`;
				}
			}
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
		// 참석자 칼럼
		const createAttendeeColumn = () => {
			const $column = Util.createElement("div", "item", "attendUser");
			const $tipSpan = Util.createElement("span", "headerTip");
			$tipSpan.innerHTML = "참석자: ";
			$column.appendChild($tipSpan);
			//if(assign.elecYN == 'Y'){
				const attendeeList = assign.attendeeList;
				console.log("attendeeList", attendeeList);
				for(const attendee of attendeeList){
					const $attendee = Util.createElement("span");
					$attendee.innerHTML = attendee.userName;
					$column.appendChild($attendee);
				}
			//}
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
		//결재 메모 칼럼
		const createAppCommentColumn = () => {
			const $column = Util.createElement("div", "item", "approvalComment");
			$column.innerHTML = assign.approvalComment;

			$row.appendChild($column);
		}
		createNoColumn();
		//createStatusColumn();
		createMeetingStatusColumn();
		createRoomTypeColumn();
		createElecColumn();
		createScheduleColumn();
		createSecretColumn();
		createTitleColumn();
		createHostColumn();
		createAttendeeColumn();
		createRegDateColumn();
		createAppCommentColumn();
		return $row;
	},
}
