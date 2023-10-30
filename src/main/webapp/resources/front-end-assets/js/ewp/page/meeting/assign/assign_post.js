/**
 * 
 */

import {Util, Modal, AjaxBuilder} from '/resources/core-assets/essential_index.js';
import {paramHandler, inputHandler} from './module_index.js';
import {AssignCall as $AS, RoomCall as $RM, UserCall as $USER, AttendeeCall as $ATT, FileCall as $FILE} from '/resources/front-end-assets/js/ewp/comm/ajax_index.js';

$(async () => {
	paramHandler.init();
	domHandler.init();
	evtHandler.init();
});

const domHandler = {
	async init(){
		const $saveBtn = document.querySelector("#saveBtn");
		requestHandler.disable = () => {
			$saveBtn.disabled = true;
			$saveBtn.onclick = "";
		}
		if(isPost){
			requestHandler.enable = () => {
				$saveBtn.disabled = false;
				$saveBtn.onclick = async () => {
					$saveBtn.disabled = true;
					const result = paramHandler.validation();
					if(result.valid){
						await requestHandler.post();
					}else{
						Modal.info({
							msg: result.msg
						});
					}
					$saveBtn.disabled = false;
				};
			}
			$saveBtn.innerHTML = "등 록";
			await this.setPostMode();
			requestHandler.enable();
			//requestHandler.disable();
		}else{
			requestHandler.enable = () => {
				$saveBtn.disabled = false;
				$saveBtn.onclick = async () => {
					$saveBtn.disabled = true;
					const result = paramHandler.validation();
					if(result.valid){
						await requestHandler.put();
					}else{
						Modal.info({
							msg: result.msg
						});
					}
					$saveBtn.disabled = false;
				}
			}
			$saveBtn.innerHTML = "수 정";
			await this.setPutMode();
			requestHandler.enable();
			//requestHandler.enable();
		}
		this.setApprovalManager();
	},
	async setPostMode(){
		const room = await $RM.Get.roomOne({
			roomType: roomType,
			roomKey: roomKey,
		});
		paramHandler.initRoomParam(room);
		paramHandler.initAttendeeHandler({
			officeCode: room.officeCode,
			editable: true,
		});
		let momentDate = moment(holdingDate).format('YYYY-MM-DD');
		if(momentDate == "Invalid date"){
			momentDate = moment().format('YYYY-MM-DD');
		}
		paramHandler.initScheduleParam({
			holdingDate: momentDate,
			beginTime: startTime,
			finishTime: endTime,
			attendeeCnt: 0,
		});
	},
	async setPutMode(){
		const assign = await $AS.Get.assignOne(skdKey);
		if(assign){
			requestHandler.meetingKey = assign.meetingKey;
			paramHandler.initAttendeeHandler({
				officeCode: assign.officeCode,
				editable: true,
			});
			paramHandler.initHiddenParam(assign);
			paramHandler.initStatusParam(assign);
			paramHandler.initRoomParam(assign.room);
			paramHandler.initScheduleParam(assign);
			paramHandler.initMeetingParam(assign);
			const attendeeList = await $ATT.Get.attendeeSimpleListByMeeting({meetingKey: assign.meetingKey});
			paramHandler.initAttendeeParam(attendeeList);
			const fileList = await $FILE.Get.meetingMaterialFileList(assign.meetingKey);
			paramHandler.setFiles(fileList);
		}else{
			location.href = history.back();
		}
	},
	async setApprovalManager(){
		const {
			officeCode,
			roomType,
		} = paramHandler.getRoomParam();
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
}

const requestHandler = {
	async post(){
		try{
			let success = [];
			let error = [];
			const holdingDate = inputHandler.getHoldingDate();
			const endDate = (inputHandler.getEndDate?.())?inputHandler.getEndDate():holdingDate;
			const getDaysBetween = (startDate, endDate, excludeWeekends = false) => {
				  const start = moment(startDate);
				  const end = moment(endDate);
				  let days = 0;

				  for (let date = start; date.isSameOrBefore(end); date.add(1, "days")) {
				    if (excludeWeekends) {
				      const dayOfWeek = date.day();
				      if (dayOfWeek === 6 || dayOfWeek === 0) {
				        // 토요일(6) 또는 일요일(0) 인 경우 건너뜁니다.
				        continue;
				      }
				    }

				    days++;
				  }

				  return days;
				}
			const postWithinPeriod = async (startDate, endDate, excludeWeekends = false) => {
				const start = moment(startDate);
				const end = moment(endDate);

				for (let date = start; date.isSameOrBefore(end); date.add(1, "days")) {
					if (excludeWeekends) {
				    	const dayOfWeek = date.day();
				    	if (dayOfWeek === 6 || dayOfWeek === 0) {
				    		// 토요일(6) 또는 일요일(0) 인 경우 건너뜁니다.
				    		continue;
				    	}
				    }
					const requestDate = date.format("YYYY-MM-DD");
					paramHandler.setDateTimeInput({holdingDate: requestDate});
					const result = await postMeetingAssign();
					if(!result){
						Modal.endLoading();
						if(requestDate == endDate){
							const err = error[error.length - 1];
							await Modal.confirm({title:"신청 실패", msg: requestDate+"일 예약에 실패했습니다.", detail: [err.detail]});
						}else{
							const res = await Modal.confirm({title:"신청 실패", msg: requestDate+"일 예약에 실패했습니다. 연속신청을 계속 진행하시겠습니까?"});
							if(res != "OK"){
								return;
							}
							Modal.startLoading();
						}
					}
				}
			}
			
			const postMeetingAssign = async () => {
				const $form = paramHandler.getInputParam();
				const formData = $($form).serialize();
				let successRes = null;
				let errorRes = null;
				try{
					successRes = await this.postAssign(formData);
					success.push(successRes);
				}catch(err){
					errorRes = err;
					error.push(errorRes);
				}
				if(successRes){
					const meetingKey = successRes.data.meetingKey;
					const attRes = await this.postAttendee(meetingKey);
					await this.postFile(meetingKey);
					return true;
				}else{
					return false;
				}
				//await this.deleteFile(meetingKey);
			}
			const daysBetween = getDaysBetween(holdingDate, endDate);
			if(daysBetween > 1){
				const res = await Modal.confirm({title:"연속 신청", msg: holdingDate+"일부터 "+endDate+"일까지 "+daysBetween+"일 연속 예약 신청을 하시겠습니까?"});
				if(res != "OK"){
					return;
				}
			}
			Modal.startLoading();
			await postWithinPeriod(holdingDate, endDate);
			Modal.endLoading();
			if(!Util.isEmpty(success)){
				await Modal.info({
					msg: "회의가 등록되었습니다."
				});
				history.replaceState(null, null, '/ewp/dashboard');
				const skdKey = success.pop().data.skdKey;
				location.href  = `/ewp/meeting/assign/${skdKey}`;
			}
			//location.href = "/ewp/home";
		}catch(err){
			console.log("err", err);
			Modal.error({response: err});
		}finally{
			Modal.endLoading();
		}
		
	},
	async put(){
		try{
			Modal.startLoading();
			const $form = paramHandler.getChangedInputParam();
			if($form){
				const formData = $($form).serialize();
				const assignRes = await this.putAssign(formData);
			}
			const meetingKey = this.meetingKey;
			const attRes = await this.postAttendee(meetingKey);
			await this.postFile(meetingKey);
			await this.deleteFile(meetingKey);
			Modal.endLoading();
			await Modal.info({
				msg: "회의가 수정되었습니다."
			});
			history.replaceState(null, null, '/ewp/dashboard');
			location.href  = `/ewp/meeting/assign/${skdKey}`;
		}catch(err){
			console.log("err", err);
			Modal.error({response: err});
		}finally{
			Modal.endLoading();
		}
	},
	async postAssign(formData){
		try{
			const res = await $AS.Post.assignOne({
				roomType: roomType,
				formData: formData,
			});
			if(res.status != 200){
				throw res;
			}
			return res;
		}catch(err){
			throw err;
		}
	},
	async putAssign(formData){
		try{
			//const formData = $("#postF").serialize();
			const res = await $AS.Put.assignOne({
				skdKey: skdKey,
				formData: formData,
			});
			if(res.status != 200){
				throw res;
			}
			return res;
		}catch(err){
			throw err;
		}
	},
	async postAttendee(meetingKey){
		try{
			const attendeeParam = paramHandler.getAttendeeParam();
			if(!attendeeParam){
				return;
			}
			const list = [];
			attendeeParam?.insertList.forEach(data => {
				list.push({
					userKey: data.userKey,
					deptId: data.deptId,
					data: data.attendRole,
					operation: "CREATE",
				});
			});
			attendeeParam?.updateList.forEach(data => {
				list.push({
					userKey: data.userKey,
					data: data.attendRole,
					operation: "UPDATE",
				});
			});
			attendeeParam?.deleteList.forEach(data => {
				list.push({
					userKey: data.userKey,
					data: data.attendRole,
					operation: "DELETE",
				});
			});
			if(list.length == 0){
				return;
			}
			const res = await $ATT.Post.attendeeList({
				meetingKey: meetingKey,
				list: list,
			});
			if(res.status != 200){
				throw res;
			}
			return res;
		}catch(err){
			throw err;
		}
	},
	async postFile(meetingKey){
		const createForm = (fileList) => {
			const formData = new FormData;
			fileList.forEach(file => {
				formData.append("list", file);
			});
			return formData;
		}
		try{
			const fileList = paramHandler.getAddedFiles();
			console.log("fileList", fileList);
			if(!fileList || fileList.length == 0){
				return;
			}
			const res = await $FILE.Post.meetingFileList({
				meetingKey: meetingKey, 
				fileType: "MATERIAL",
				fileList: createForm(fileList)
			});
			if(res.status != 200){
				throw res;
			}
			return res;
		}catch(err){
			throw err;
		}
	},
	async deleteFile(meetingKey){
		try{
			const keyList = paramHandler.getDeletedFiles();
			if(!keyList || keyList.length == 0){
				return;
			}
			const res = await $FILE.Delete.meetingFileList({
				meetingKey: meetingKey, 
				fileList: keyList
			});
			if(res.status != 200){
				throw res;
			}
			return res;
		}catch(err){
			throw err;
		}
	}
}

const evtHandler = {
	init(){
		this.enableCancelBtn();
		this.enableTitleClearBtn();
		this.enableSkdHostClearBtn();
		this.enableContentsClearBtn();
		this.enableLoadInfoBtn();
	},
	enableCancelBtn(){
		$("#cancelBtn").click((evt) => {
			if( $("#skdKeyInput").val() != '' ){
				location.href = `/ewp/meeting/assign/${skdKey}`;
			} else{
				history.back();
			}
		});
	},
	enableTitleClearBtn(){
		const $btn = Util.getElement("#titleInputClear");
		if($btn){
			$btn.onclick = () => {
				console.log("inputHandler", inputHandler)
				inputHandler.clearTitle();
			}
		}
	},
	enableSkdHostClearBtn(){
		const $btn = Util.getElement("#skdHostInputClear");
		if($btn){
			$btn.onclick = () => {
				inputHandler.clearSkdHost();
			}
		}
	},
	enableContentsClearBtn(){
		const $btn = Util.getElement("#contentsInputClear");
		if($btn){
			$btn.onclick = () => {
				inputHandler.clearContents();
			}
		}
	},
	enableLoadInfoBtn(){
		const $btn = Util.getElement("#loadInfoBtn");
		if($btn){
			const $modal = Modal.get("callRegModal");
			if(!$modal){
				return;
			}
			// 신청내역 클릭시 불러오기 확인 모달 표시
			const showInfoModal = async (assign) => {
				const $infoModal = Modal.get("infoReservedMeeting");
				const $title = $infoModal.querySelector("#titleInput");
				$title.innerHTML = assign.title;
				const $schedule = $infoModal.querySelector("#scheduleInput");
				$schedule.innerHTML = moment(assign.beginDateTime).format("HH:mm")+" ~ "+moment(assign.finishDateTime).format("HH:mm");
				const $host = $infoModal.querySelector("#hostInput");
				$host.innerHTML = assign.skdHost;
				const $facilitatorRow = $infoModal.querySelector('[data-row=facilitator]');
				const $attendeeListRow = $infoModal.querySelector('[data-row=attendeeList]');
				if(assign.elecYN == 'Y'){
					$facilitatorRow.style.display = "";
					$attendeeListRow.style.display = "";
					const $facilitatorNameplate = $infoModal.querySelector("#facilitatorNameplateInput");
					$facilitatorNameplate.innerHTML = (assign.facilitatorName)?assign.facilitatorName:"";
					const $attendeeList = $infoModal.querySelector("#attendeeListInput");
					$attendeeList.innerHTML = "";
					for(const attendee of assign.attendeeList.filter(attendee => attendee.attendRole != "HOST")){
						const $attendee = Util.createElement("span", "mr-1");
						$attendee.innerHTML = attendee.userName;
						$attendeeList.appendChild($attendee);
					}
				}else{
					$facilitatorRow.style.display = "none";
					$attendeeListRow.style.display = "none";
				}
				const res = await $infoModal.show();
				return res;
			}
			// 신청내역 표시
			const createTableRow = (assign) => {
				const $tr = Util.createElement("tr");
				const createApprovalColumn = () => {
					const $td = Util.createElement("td", "align-middle");
					const $heading = Util.createElement("h3");
					const $badge = Util.createElement("span", "badge");
					let text, cls = ["", "d-none"];
					switch(assign.approvalStatus){
						case "REQUEST":
						    [text, cls] = ["승인대기", "badge-warning"];
						    break;
						case "APPROVED":
							[text, cls] = ["승인완료", "badge-primary"];
						    break;
						case "CANCELED":
							[text, cls] = ["사용취소", "badge-secondary"];
							break;
						case "REJECTED":
							[text, cls] = ["승인불가", "badge-danger"];
							break;
					}
					Util.addClass($badge, cls);
					$badge.innerText = text;
					$heading.appendChild($badge);
					$td.appendChild($heading);
					$tr.appendChild($td);
				}
				const createScheduleColumn = () => {
					const $td = Util.createElement("td", "align-middle");
					$td.innerHTML = assign.holdingDate
									+"<br>"
									+moment(assign.beginDateTime).format('HH:mm')
									+"~"
									+moment(assign.finishDateTime).format('HH:mm');
					$tr.appendChild($td);
				}
				const createTitleColumn = () => {
					const $td = Util.createElement("td", "align-middle");
					$td.style.maxWidth = "150px";
					const $span = Util.createElement("span", "text-truncate", "d-inline-block", "w-100");
					$span.innerHTML = assign.title;
					$td.appendChild($span);
					$tr.appendChild($td);
				}
				const createHostColumn = () => {
					const $td = Util.createElement("td", "align-middle");
					$td.innerHTML = assign.skdHost;
					$tr.appendChild($td);
				}
				const createFacilitatorColumn = () => {
					const $td = Util.createElement("td", "align-middle");
					const facilitatorName = assign.attendeeList.filter(attendee => attendee.attendRole == "HOST").pop()?.userName;
					assign.facilitatorName = facilitatorName;
					if(facilitatorName){
						$td.innerHTML = facilitatorName;
					}
					$tr.appendChild($td);
				}
				const createAttendeeListColumn = () => {
					const $td = Util.createElement("td", "align-middle");
					$td.style.maxWidth = "150px";
					const attendeeList = assign.attendeeList.filter(attendee => attendee.attendRole != "HOST");
					let namelist = "";
					for(const attendee of attendeeList){
						namelist += attendee.userName + " ";
					}
					const $span = Util.createElement("span", "text-truncate", "d-inline-block", "w-100");
					$span.innerHTML = namelist;
					$td.appendChild($span);
					$tr.appendChild($td);
				}
				createApprovalColumn();
				createScheduleColumn();
				createTitleColumn();
				createHostColumn();
				createFacilitatorColumn();
				createAttendeeListColumn();
				$tr.onclick = async () => {
					// 선택한 신청 내용 불러오기 확인 후 처리
					const res = await showInfoModal(assign);
					if(res != "OK"){
						return;
					}
					Modal.startLoading();
					paramHandler.setScheduleParam({
						beginDateTime: assign.beginDateTime,
						finishDateTime: assign.finishDateTime,
						skdHost: assign.skdHost,
						attendeeCnt: assign.attendeeCnt,
					});
					paramHandler.setMeetingParam(assign);
					let attendeeList = [];
					if(assign.elecYN == "Y"){
						attendeeList = await $ATT.Get.attendeeSimpleListByMeeting({meetingKey: assign.meetingKey});
					}
					paramHandler.clearAttendeeParam();
					paramHandler.setAttendeeParam(attendeeList);
					Modal.endLoading();
					$modal.close();
				}
				return $tr;
			}
			const $modalbody = $modal.querySelector(".modalBody");
			const $tbody = $modal.querySelector("tbody");
			$btn.onclick = async () => {
				const res = await $modal.show(async () => {
					if(!$modal.loaded){
						$tbody.innerHTML = "";
						Modal.startLoading({
							animation: "spinner", 
							target: $modalbody,
						});
						const assignList = await AjaxBuilder({
							request: $AS.Get.assignListForRegister,
							param: {
								pageNo: 1,
								pageCnt: 10,
							},
							loading: false,
						}).exe();
						Modal.endLoading();
						assignList.forEach(assign => {
							if(assign.secretYN == 'N'){
								const $row = createTableRow(assign);
								$tbody.appendChild($row);
							}
						});
						$modal.loaded = true;
					}
				});
			}
		}
	},
}

