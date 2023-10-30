/**
 * 사용신청 처리 페이지 스크립트
 * 
 * @author mckim
 */
import { Util, Dom, ModalService, FormHelper } from '/resources/core-assets/essential_index.js';
import DateInputHelper from '/resources/core-assets/modules/jquery.dateInputHelper.js';
import { AssignCall as $AS, AdminCall as $ADM, RoomCall as $RM } from '/resources/front-end-assets/js/ewp/comm/ajax_index.js';

window.onload = () => {
	searchHandler.init({
		officeCode: officeCode,
		approvalStatus: approvalStatus,
		roomType: roomType,
		host: host,
		startDate: startDate,
		endDate: endDate,
		pageNo: pageNo
	});
}

const setAutoApprovalSwitch = async () => {
	const $checkbox = Dom.getElement("#autoConfirm");
	$checkbox.disabled = true;
	const officeCode = searchHandler.getValue("officeCode");
	console.log("setAutoApprovalSwitch", officeCode);
	if (Util.isEmpty(officeCode)) {
		$checkbox.onchange = null;
		$checkbox.checked = false;
		return;
	}
	$checkbox.onchange = async () => {
		$checkbox.disabled = true;
		let autoYN = 'N'
		if ($checkbox.checked == true) {
			autoYN = 'Y';
		}
		try {
			await $ADM.Put.officeAutoApproval({
				officeCode: officeCode,
				autoYN: autoYN,
			});
			const messageModal = ModalService.infoBuilder().build();
			if ($checkbox.checked == true) { // 자동승인 on
				messageModal.setMessage("예약등록되는 건들을 시스템이 자동승인처리 합니다.").show();
			} else { // 자동승인 off
				messageModal.setMessage("예약 자동승인을 해제 하시면 예약등록되는 건들을 수동으로 승인처리 하셔야 합니다.").show();
			}
		} catch (err) {
			console.error(err);
			$checkbox.checked = !$checkbox.checked;
			ModalService.errorBuilder(err).build().show();
		} finally {
			$checkbox.disabled = false;
		}
	}
	/* 서버로부터 현재 자동결재 여부 전달받아 설정 완료 후 체크박스 활성화 */
	try {
		const approvalPolicy = await $ADM.Get.officeApprovalPolicy(officeCode);
		if (approvalPolicy == "AUTH_AUTO") {
			$checkbox.checked = true;
		} else {
			$checkbox.checked = false;
		}
		$checkbox.disabled = false;
	} catch (err) {
		ModalService.errorBuilder(err).build().show();
	}
}

const roomListProvider = {
	async init(codeList, roomType) {
		const roomMap = {};
		await Promise.all(
			codeList
				.filter(code => code !== "0")
				.map(async code => {
					const rentableList = await $RM.Get.rentableList({ officeCode: code, roomType: roomType });
					roomMap[code] = rentableList;
				})
		);
		this.roomMap = roomMap;
	},
	getRoomList(code){
		return this.roomMap[code];
	},
	getRoomListAll(){
		return Object.values(this.roomMap).reduce((acc, arr) => acc.concat(arr), []);
	}
}

const searchHandler = {
	async init(data = {}) {
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
		const searchHelper = this.searchHelper = new FormHelper({
			valueProcessor: {
				officeCode: (value) => {
					return (value == "0") ? null : value;
				},
				approvalStatus: (value) => {
					return (value == "0") ? null : value;
				},
				roomKey: (value) => {
					return (value == "0") ? null : value;
				},
				title: (value) => {
					return (Util.isEmpty(value)) ? null : value;
				},
				host: (value) => {
					return (Util.isEmpty(value)) ? null : value;
				},
				attendeeName: (value) => {
					return (Util.isEmpty(value)) ? null : value;
				},
				elecYN: (value) => {
					return (value != "Y") ? null : value;
				},
				secretYN: (value) => {
					return (value != "Y") ? null : value;
				},
			}
		});
		searchHelper.addFormElements("#searchForm").on({
			change: (event, instance) => {
				const {
					name,
					value,
					element,
					form
				} = instance;
				switch (name) {
					case "officeCode":
						const $officeLabel = Dom.getElement("#officeLabel");
						$officeLabel.innerText = form.getSelectedText();
						setAutoApprovalSwitch();
						
						const roomKeyForm = searchHelper.getForm("roomKey");
						const $roomKeySelect = roomKeyForm.getElement();
						const $defaultOption = Dom.createDefaultOption({value: "0", label: "전체", selectable: true});
						$roomKeySelect.innerHTML = "";
						$roomKeySelect.appendChild($defaultOption);
						
						const roomList = (value == "0")?roomListProvider.getRoomListAll():roomListProvider.getRoomList(value);
						roomList.forEach(room => {
							const $option = Dom.createOption({value: room.roomKey, label: room.roomName});
							$roomKeySelect.appendChild($option);
						});
						break;
					case "approvalStatus":
						const $approvalStatusLabel = Dom.getElement("#approvalStatusLabel");
						$approvalStatusLabel.innerText = form.getSelectedText();
						break;
				}
			},
			click: (event, instance) => {
				const {
					name
				} = instance;
				switch (name) {
					case "mobileReset":
					case "reset":
						this.reset();
						break;
					case "search":
						this.search();
						break;
				}
			}
		});
		const officeCodeForm = searchHelper.getForm("officeCode");
		await roomListProvider.init(officeCodeForm.getOptionValues(), roomType);
		searchHelper.setDefaultValues({
			officeCode: officeCode,
			approvalStatus: approvalStatus,
			roomType: roomType,
			title: "",
			host: host,
			attendeeName: "",
			elecYN: "",
			secretYN: "",
			startDate: startDate,
			endDate: endDate,
		});
		const dateInputHelper = new DateInputHelper();
		dateInputHelper.addInput(searchHelper.getForm("startDate").getElement()).addInput(searchHelper.getForm("endDate").getElement());
		this.search();
	},
	getValue(name) {
		return this.searchHelper.getValue(name);
	},
	async search() {
		const {
			officeCode = this.officeCode,
			approvalStatus = this.approvalStatus,
			title = this.title,
			host = this.host,
			attendeeName = this.attendeeName,
			roomType = this.roomType,
			roomKey = this.roomKey,
			elecYN = this.elecYN,
			secretYN = this.secretYN,
			startDate = this.startDate,
			endDate = this.endDate,
		} = this.searchHelper.getFormValues();
		this.officeCode = officeCode;
		this.approvalStatus = approvalStatus;
		this.roomType = roomType;
		this.roomKey = roomKey;
		this.title = title;
		this.host = host;
		this.attendeeName = attendeeName;
		this.elecYN = elecYN;
		this.secretYN = secretYN;
		this.startDate = startDate;
		this.endDate = endDate;
		this.totalCnt = null;
		await this.loadPage();
	},
	reset() {
		this.pageNo = 1;
		this.searchHelper.reset();
		this.search();
	},
	async pageMove(pageNo) {
		this.pageNo = pageNo;
		await this.loadPage();
	},
	async loadPage() {
		if (!this.totalCnt) {
			const result = await this.setPagination();
			if (!result) {
				return;
			}
		}
		await this.showList();
	},
	async setPagination() {
		assignRowGenerator.clear();
		$("#pagination").empty();
		try {
			const cnt = await $AS.Get.assignListCnt({
				officeCode: this.officeCode,
				approvalStatus: this.approvalStatus,
				roomType: this.roomType,
				roomKey: this.roomKey,
				title: this.title,
				host: this.host,
				attendeeName: this.attendeeName,
				elecYN: this.elecYN,
				secretYN: this.secretYN,
				startDate: this.startDate,
				endDate: this.endDate,
				pageNo: this.pageNo,
				pageCnt: this.pageCnt,
			});
			this.totalCnt = cnt;
			this.pageNo = 1;
			this.pagination.make(this.totalCnt, this.pageCnt);
			return true;
		} catch (err) {
			ModalService.errorBuilder(err).build().show();
			this.pageNo = null;
			return false;
		}
	},
	async showList() {
		try {
			const list = await $ADM.Get.assignListForApproval({
				officeCode: this.officeCode,
				approvalStatus: this.approvalStatus,
				roomType: this.roomType,
				roomKey: this.roomKey,
				title: this.title,
				host: this.host,
				attendeeName: this.attendeeName,
				elecYN: this.elecYN,
				secretYN: this.secretYN,
				startDate: this.startDate,
				endDate: this.endDate,
				pageNo: this.pageNo,
				pageCnt: this.pageCnt,
			});
			assignRowGenerator.generate(list);
		} catch (err) {
			ModalService.errorBuilder(err).build().show();
		}
	}
}

const assignRowGenerator = {
	clear() {
		const $listBox = Dom.getElement("#listBox");
		$listBox.innerHTML = "";
	},
	getContainer() {
		const $listBox = Dom.getElement("#listBox");
		return $listBox;
	},
	generate(assignList) {
		this.clear();
		const $listBox = Dom.getElement("#listBox");
		for (const assign of assignList) {
			const $row = this.createRow(assign);
			$listBox.appendChild($row);
		}
	},
	createRow(assign) {
		const $row = Dom.createElement("div", "row");
		// 반호 칼럼
		const createNoColumn = () => {
			const $column = Dom.createElement("div", "item", "no");
			$column.innerHTML = assign.skdKey;

			$row.appendChild($column);
		}
		// 결재상태 칼럼
		const createApprovalStatusColumn = () => {
			const $column = Dom.createElement("div", "item", "status");
			const $span = Dom.createElement("span");
			$column.appendChild($span);
			const getAppStatusCls = (status) => {
				switch (status) {
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
			Dom.addClass($row, getAppStatusCls(assign.appStatus));
			$row.appendChild($column);
		}
		// 진행상태 칼럼
		const createMeetingStatusColumn = () => {
			const $column = Dom.createElement("div", "item", "status");
			const $span = Dom.createElement("span");
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
				switch (status) {
					case "UNAPPROVAL":
						return "s0";
					case "APPROVED":
						return "s1";
					case "START":
						return "s2";
					case "END":
						return "s3";
					case "CANCEL":
						return "s4";
					case "DROP":
						return "s5";
					default:
						return "s2";
				}
			}
			Dom.addClass($row, getStatusCls(assign.meetingStatus));
			$row.appendChild($column);
		}
		// 장소구분 칼럼
		const createRoomTypeColumn = () => {
			const $column = Dom.createElement("div", "item", "type");
			const $span = Dom.createElement("span");
			$column.appendChild($span);
			switch (assign.room?.roomType) {
				case "MEETING_ROOM":
					Dom.addClass($row, "mr");
					break;
				case "EDU_ROOM":
					Dom.addClass($row, "lr");
					break;
				case "HALL":
					Dom.addClass($row, "at");
					break;
			}
			$row.appendChild($column);
		}
		// 전자회의 칼럼
		const createElecColumn = () => {
			const $column = Dom.createElement("div", "item", "elecDocs");
			const $span = Dom.createElement("span");
			$column.appendChild($span);
			if (assign.elecYN == 'Y') {
				Dom.addClass($row, "elec");
			}
			$row.appendChild($column);
		}
		// 일시/장소 칼럼
		const createScheduleColumn = () => {
			const $column = Dom.createElement("div", "item", "dateTimeRoom");

			const $dateTimeDiv = Dom.createElement("div");
			$column.appendChild($dateTimeDiv);
			const $dateSpan = Dom.createElement("span", "date");
			$dateSpan.innerHTML = moment(assign.holdingDate).format("YYYY.MM.DD");
			const $timeSpan = Dom.createElement("span", "time");
			$timeSpan.innerHTML = moment(assign.beginDateTime).format("HH:mm") + "~" + moment(assign.finishDateTime).format("HH:mm")
			$dateTimeDiv.appendChild($dateSpan);
			$dateTimeDiv.appendChild($timeSpan);

			const $roomDiv = Dom.createElement("div");
			$column.appendChild($roomDiv);
			$roomDiv.innerHTML = assign.room?.roomName;

			$row.appendChild($column);
		}
		// 보안설정 칼럼
		const createSecretColumn = async () => {
			const $securityColumn = Dom.createElement("div", "item", "security");
			const $span = Dom.createElement("span");
			$securityColumn.appendChild($span);
			if (assign.secretYN == 'Y') {
				Dom.addClass($row, "secu");
			}
			$row.appendChild($securityColumn);
		}
		// 제목 칼럼
		const createTitleColumn = () => {
			const $column = Dom.createElement("div", "item", "title", "text-truncate");
			const $a = Dom.createElement("a", "w-100", "d-inline-block", "text-truncate");
			$column.appendChild($a);
			$a.innerHTML = assign.title;
			$a.href = "/ewp/manager/approval/meeting/assign/" + assign.skdKey;
			$row.appendChild($column);
		}
		// 주관자 칼럼
		const createHostColumn = () => {
			const $column = Dom.createElement("div", "item", "host", "text-truncate");
			const $tipSpan = Dom.createElement("span", "headerTip");
			$tipSpan.innerHTML = "주관자: ";
			$column.appendChild($tipSpan);

			const $nameSpan = Dom.createElement("div", "w-100", "d-inline-block", "text-truncate");
			$nameSpan.innerHTML = assign.skdHost;
			$column.appendChild($nameSpan);

			$row.appendChild($column);
		}
		// 참석자 칼럼
		const createAttendeeColumn = () => {
			const $column = Dom.createElement("div", "item", "attendUser", "align-items-center", "overflow-hidden");
			$column.style.maxHeight = "1.6em";
			const $tipSpan = Dom.createElement("span", "headerTip");
			$tipSpan.innerHTML = "참석자: ";
			$column.appendChild($tipSpan);
			if (assign.elecYN == 'Y') {
				const attendeeList = assign.attendeeList;
				for (const attendee of attendeeList) {
					const $attendee = Dom.createElement("span");
					$attendee.innerHTML = attendee.userName;
					$column.appendChild($attendee);
				}
				$column.onpointerenter = () => {
					$column.title = attendeeList.map(attendee => attendee.userName);
				}
				$column.onpointerleave = () => {
					$column.title = "";
				}
			}

			$row.appendChild($column);
		}
		// 등록일 칼럼
		const createRegDateColumn = () => {
			const $column = Dom.createElement("div", "item", "regDate");
			const $tipSpan = Dom.createElement("span", "headerTip");
			$tipSpan.innerHTML = "등록일: ";
			$column.appendChild($tipSpan);

			const regDateM = moment(assign.regDateTime);
			const $dateSpan = Dom.createElement("span", "date");
			$dateSpan.innerHTML = regDateM.format("YYYY.MM.DD");
			const $timeSpan = Dom.createElement("span", "time");
			$timeSpan.innerHTML = regDateM.format("HH:mm");
			$column.appendChild($dateSpan);
			$column.appendChild($timeSpan);

			$row.appendChild($column);
		}
		//결재 메모 칼럼
		const createAppCommentColumn = () => {
			const $column = Dom.createElement("div", "item", "approvalComment", "text-truncate");
			const $comment = Dom.createElement("span", "w-100", "d-inline-block", "text-truncate")
			$comment.innerHTML = assign.appComment ? assign.appComment : "";
			$column.onpointerenter = () => {
				$column.title = $comment.innerHTML;
			}
			$column.onpointerleave = () => {
				$column.title = "";
			}
			$column.appendChild($comment);
			$row.appendChild($column);
		}
		createNoColumn();
		createApprovalStatusColumn();
		//createMeetingStatusColumn();
		createRoomTypeColumn();
		createElecColumn();
		createSecretColumn();
		createScheduleColumn();
		createTitleColumn();
		createHostColumn();
		createAttendeeColumn();
		createRegDateColumn();
		createAppCommentColumn();
		return $row;
	},
}

