import Mutex from '/resources/library/async-mutex/es6/Mutex.js';
import {Util, Modal, AjaxBuilder} from '/resources/core-assets/essential_index.js';
import {TimerService, FileService, SocketService} from './service_index.js';
import {updateBtnHTML, fileHTML, extBtnHTML, reportBtnHTML, attendeeHTML} from './html-package.js';
import {BookletMng} from './booklet-module.js';
import {attendeeHandler} from '/resources/front-end-assets/js/lime/page/meeting/assign/module_index.js';
import {UserSearchModal} from '/resources/front-end-assets/js/lime/comm/module_index.js';
import {MeetingCall as $MEETING} from '/resources/front-end-assets/js/lime/ajax/ajax_index.js';
let _timerMng;
let _bookletMng;
const mutex = new Mutex();
const progress = {
	streamer: 0,
	streamerSessionId: null,
	entrySet: [],
}
let attendeeList = [];
//{attendId:{}, attendId:{},...}
let attendeeMap = {};
//회의 진행자
let hostEmp;
//보조 진행자
let assistantEmp;
//필수참석 직원목록
let reqList = [];
// 선택참석 직원목록
let optList = [];

/* 인원체크 */
let totalCount;
let attendCount;
let signCount;
const exitWithSave = async (URI) => {
	await _bookletMng.savePage();
	evtHandler.unsetExitListener();
	location.href = URI;
}
const exitWithoutSave = async (URI) => {
	evtHandler.unsetExitListener();
	location.href = URI;
}
const beforeUnloadListener = (event) => {
	event.returnValue="";
}
const instanceProvider = {
	init(data = {}){
		const {
			loginId,
			roomType,
			reqKey,
			scheduleId,
			meetingId,
			assign,
		} = data;
		this.loginId = loginId;
		this.assign = assign;
		this.roomType = roomType;
		this.reqKey = reqKey;
		this.scheduleId = scheduleId;
		this.meetingId = meetingId;
	},
	set(data = {}){
		const {
			attendRole = this.attendRole,
		} = data;
		this.attendRole = attendRole;
	},
	getFileService(){
		return FileService;
	},
	getSocketService(){
		return SocketService;
	},
}

const scheduleService = {
	setBeginTimer(){
		$(".btnFinish").click(function(){
			if(authorities.includes("FUNC_CANCEL")){
				showSelectedFinishModal("hostCancel_hostModal");
			}else{
				showSelectedFinishModal("guestFinish_guestModal");
			}
		});
		_timerMng = TimerMng({
			intervalEndTime: instanceProvider.assign.beginDateTime
			,intervalEndCall: async () => {
				authorities = await $MEETING.Get.userAuthorityForMeeting(meetingId);
				initManageBtn();
				this.setFinishTimer();
			}
			,timerFormat : 'ss'
		});
	},
	setFinishTimer() {
		//회의종료
		$(".btnFinish").removeClass("disabled");
		$(".btnFinish").click(function(){
			if(authorities.includes("FUNC_FINISH")){
				showSelectedFinishModal("hostFinish_hostModal");
			}else{
				showSelectedFinishModal("guestFinish_guestModal");
			}
		});
		_timerMng.update({
			fixedEndTime: instanceProvider.assign.finishDateTime,
			intervalEndTime: instanceProvider.assign.expDateTime,
			fixedCall: function(count){
				$(".finishCount").text(" -"+count+"분");
				if(isManager() && (count == 5 || count == 10)){
					const $modal = Modal.get("timeCountModal");
					const $count = $modal.querySelector("[data-timer=end]");
					if($count){
						$count.innerHTML = count;
					}
					$modal.show();
				}
			}
			,fixedEndCall: async () => {
				$(".finishCount").text("");
				await _bookletMng.savePage();
				if(isManager()){
					showSelectedFinishModal("timeout_hostModal");
				}else{
					showSelectedFinishModal("timeoutHost_guestModal");
				}
			}
			,intervalCall: function(millisecondCount, formattedCount){
				Modal.getPopupList().forEach($modal => {
					const $count = $modal.querySelector("[data-timer=exit]");
					if($count){
						$count.innerHTML = formattedCount;
					}
				});
			}
			,intervalEndCall: function(){
				evtHandler.unsetExitListener();
				exitWithoutSave("/lime/home");
			}
			,timerFormat : 'ss'
		});
	},
	extSchedule(){
		const $extModal = Modal.get("timeExtensionModal");
		AjaxBuilder({
			request: $MEETING.Get.nextScheduleOne,
			param: scheduleId,
			loading: false,
		}).success(async (nextSchedule) => {
			const $intervalInfo = $extModal.querySelector(".intervalInfo");
			$intervalInfo.innerHTML = "";
			const $extInfo = $extModal.querySelector(".extInfo");
			$extInfo.innerHTML = "";
			const buildScheduleInfoElem = (param = {}) => {
				const {
					label,
					time,
					host,
				} = param
				const $scheduleDiv = Util.createElement("div");
				const $labelSpan = Util.createElement("span", "label");
				$labelSpan.innerHTML = label;
				$scheduleDiv.appendChild($labelSpan);
				
				const $timeSpan = Util.createElement("span", "time");
				$timeSpan.innerHTML = time;
				$scheduleDiv.appendChild($timeSpan);
				
				const $hostSpan = Util.createElement("span", "name");
				$hostSpan.innerHTML = host;
				$scheduleDiv.appendChild($hostSpan);
				
				return $scheduleDiv;
			}
			const finishTimeM = moment(instanceProvider.assign.finishDateTime);
			const $nowScheduleDiv = buildScheduleInfoElem({
				label: "현재 회의 종료",
				time: finishTimeM.format('HH:mm'),
				host: instanceProvider.assign.scheduleHost,
			});
			Util.addClass($nowScheduleDiv, "prev");
			$intervalInfo.appendChild($nowScheduleDiv);
			let limitTimeM = moment(CLOSE_TIME, "HH:mm").clone();
			if(nextSchedule){//다음 예정된 스케줄이 있는 경우
				limitTimeM = moment(nextSchedule.beginDateTime);
				const $nextScheduleDiv = buildScheduleInfoElem({
					label: "다음 회의 시작",
					time: limitTimeM.format('HH:mm'),
					host: nextSchedule.scheduleHost,
				});
				Util.addClass($nextScheduleDiv, "next");
				$intervalInfo.appendChild($nextScheduleDiv);
			}
			let diffTimeM = limitTimeM.diff(finishTimeM);//연장할 회의의 종료시간과 다음 스케줄의 시작시간과의 차이
			let diffMinute = moment.duration(diffTimeM).asMinutes();
			if(diffMinute < INTERVAL_MINUTE){//더이상 연장을 할 수 없는경우
				const $span = Util.createElement("span");
				$span.innerHTML = "회의시간을 더 이상 연장할 수 없습니다.";
				$extInfo.appendChild($span);
				$extModal.disableBtn("OK");
				$extModal.show();
			}else{
				const $span = Util.createElement("span");
				$span.innerHTML = "희망 연장시간";
				$extInfo.appendChild($span);
				
				const $gap = Util.createElement("span", "mx-2");
				$gap.innerHTML = ":";
				$extInfo.appendChild($gap);
				
				const $timeSelect = Util.createElement("select");
				$extInfo.appendChild($timeSelect);
				for(let i = 1; i <= diffMinute / INTERVAL_MINUTE; i++){
					const $option = Util.createElement("option");
					$option.value = (i * INTERVAL_MINUTE);
					$option.innerText = (i * INTERVAL_MINUTE + "분");
					$timeSelect.appendChild($option);
				}
				$extModal.enableBtn("OK");
				const reply = await $extModal.show();
				if(reply == "OK"){
					const extRequstMinutes = $timeSelect.value;
					AjaxBuilder({
						request: $MEETING.Put.scheduleExtend,
						param: {
							scheduleId: instanceProvider.scheduleId,
							minutes: extRequstMinutes
						},
					}).success(res => {
						SocketService.sendUpdateMsg({
							resourceType: "SCHEDULE",
							data: instanceProvider.assign.scheduleId
						});
					}).exe();
				}
			}
			
		}).exe();
	},
}

/* 참석자 목록 초기화 */
function initAttendeeList(list) {
	if(list){
		attendeeList = list;
	}
	sortAttendeeByType(attendeeList);
	showAttendeeList();
	calAttNum();
}

/* 서버로부터 불러온 참가자 목록을 참가유형에 따라 분류 */
function sortAttendeeByType(list){
	totalCount = list.length;
	hostEmp = undefined;
	assistantEmp = null;
	reqList = [];
	optList = [];
	attendeeMap = {};
	attendeeList.forEach(attendee => attendeeMap[attendee.attendId] = attendee);
	for(let i = 0; i < list.length; i++) {
		let row = list[i];
		if(row.attendRole == 'FACILITATOR') {
			hostEmp = row;
		}else if(row.assistantYN == 'Y') {
			assistantEmp = row;
		}else if(row.attendRole == 'ATTENDEE') {
			reqList.push(row);
		}else if(row.attendRole == 'OBSERVER') {
			optList.push(row);
		}
		if(row.userId == loginId){
			const attendRole = (row.assistantYN == 'Y')?"ASSISTANT":row.attendRole;
			instanceProvider.set({
				attendRole: attendRole
			});
		}
	}
}

/* 참석자 리스트 표시 */
function showAttendeeList() {
	let $con = $("#userList");
	$con.empty();
	$con.append(generateAttendeeDOM(hostEmp, "FACILITATOR"));
	if(assistantEmp){
		$con.append(generateAttendeeDOM(assistantEmp, "ASSISTANT"));
	}else if(authorities.includes("FUNC_UPDATE")){
		$con.append(generateEmptyAssistantDOM());
	}
	for (let i = 0; i < reqList.length; i++) {
		let attendee = reqList[i];
		$con.append(generateAttendeeDOM(attendee, "ATTENDEE"));
	}
	for (let i = 0; i < optList.length; i++) {
		let attendee = optList[i];
		$con.append(generateAttendeeDOM(attendee, "OBSERVER"));
	}
}

/* 진행자, 보조 진행자가 사용 가능한 버튼 활성화 */
async function initManageBtn(){
	const $btnCon = $("#manageBtn");
	const $inviteBtn = Util.getElement("#inviteBtn");
	if($inviteBtn){
		$inviteBtn.onclick = null;
		$inviteBtn.style.display = "none";
	}
	
	const $extBtn = Util.getElement("#extBtn");
	if($extBtn){
		$extBtn.onclick = null;
		$extBtn.style.display = "none";
	}
	
	const $reportBtn = Util.getElement("#reportBtn");
	if($reportBtn){
		$reportBtn.onclick = null;
		$reportBtn.style.display = "none";
	}
	
	const $cameraBtn = Util.getElement("#cameraBtn");
	if($cameraBtn){
		$cameraBtn.onclick = null;
		$cameraBtn.style.display = "none";
	}
	
	const $voiceBtn = Util.getElement("#voiceBtn");
	if($voiceBtn){
		$voiceBtn.onclick = null;
		$voiceBtn.style.display = "none";
	}
	
	//참석자 추가
	if(authorities.includes("FUNC_INVITE")){
		$inviteBtn.style.display = "inline-flex";
		$inviteBtn.onclick = showAddAttendeeModal;
	}
	//회의시간연장
	if(authorities.includes("FUNC_EXTEND")){
		$extBtn.style.display = "";
		$extBtn.onclick = () => {
			scheduleService.extSchedule();
		};
	}
	FileService.disableUpload();
	//첨부파일 추가
	if(authorities.includes("FUNC_UPLOAD")){
		FileService.enableUpload();
	}
	//개요 수정
	if(authorities.includes("FUNC_UPDATE")){
		/*let $update = $(updateBtnHTML);
		$update.on("click", function(){
			updateMeeting();
		});
		$btnCon.append($update);*/
	}
	if(authorities.includes("FUNC_PHOTO")){
		$cameraBtn.style.display = "inline-flex";
		const event = new MouseEvent("click", {
			  bubbles: false,
			  cancelable: false,
		});
		$cameraBtn.onclick = () => {
			const $photoInput = Util.createElement("input");
			$photoInput.setAttribute("type", "file");
			$photoInput.setAttribute("accept", "image/*");
			$photoInput.capture = "camera";
			$photoInput.style.display = "none";
			$photoInput.onchange = async (evt) => {
				const fileList = Object.values($photoInput.files);
				const formData = new FormData();
				formData.append('list', fileList[0]);
				Modal.startLoading({
					text: "사진을 업로드 중입니다...",
					animation: "foldingCube",
				});
				const uploadRes = await $MEETING.Post.meetingFileList({
					meetingId: meetingId, 
					fileType: "PHOTO",
					fileList: formData
				});
				Modal.endLoading();
				if(uploadRes.status == 200){
					toastr.info("사진을 업로드했습니다.");
				}else{
					toastr.error("사진을 업로드할 수 없습니다. 다시 시도해 주세요.");
				}
			}
			$photoInput.dispatchEvent(event);
		}
	}
	if(authorities.includes("FUNC_VOICE")){
		 // 녹음중 상태 변수
	    let isRecording = false;
	    // MediaRecorder 변수 생성
	    let mediaRecorder = null;
	    // 녹음 데이터 저장 배열
	    const audioArray = [];
		
	    $voiceBtn.style.display = "inline-flex";
		$voiceBtn.onclick = async () => {
			if(!isRecording){
				const confirm = await Modal.confirm({msg: "녹음을 시작합니다."});
				if(confirm != "OK"){
					return;
				}
				 // 마이크 mediaStream 생성: Promise를 반환하므로 async/await 사용
	            const mediaStream = await navigator.mediaDevices.getUserMedia({audio: true});
	            // MediaRecorder 생성
	            mediaRecorder = new MediaRecorder(mediaStream);
	            // 이벤트핸들러: 녹음 데이터 취득 처리
	            mediaRecorder.ondataavailable = (event)=>{
	                audioArray.push(event.data); // 오디오 데이터가 취득될 때마다 배열에 담아둔다.
	            }
	            // 이벤트핸들러: 녹음 종료 처리 & 재생하기
	            mediaRecorder.onstop = async (event)=>{
	            	Modal.startLoading({
	    				text: "녹음을 업로드 중입니다...",
	    				animation: "foldingCube",
	    			});
	                // 녹음이 종료되면, 배열에 담긴 오디오 데이터(Blob)들을 합친다: 코덱도 설정해준다.
	                const blob = new Blob(audioArray, {"type": "audio/ogg codecs=opus"});
	                audioArray.splice(0); // 기존 오디오 데이터들은 모두 비워 초기화한다.
	                
	                const formData = new FormData();
	    	        formData.append('list', blob);
	    	        let success = false;
	    	        try{
	    	        	const uploadRes = await $MEETING.Post.meetingFileList({
	    	        		meetingId: meetingId, 
	    	        		fileType: "VOICE",
	    	        		fileList: formData
	    	        	});
	    	        	if(uploadRes.status == 200){
	    	        		success = true;
		    	        }
	    	        }catch(err){
	    	        }
	    	        Modal.endLoading();
	    	        if(success == true){
	    	        	toastr.info("녹음을 업로드했습니다.");
	    	        }else{
	    	        	toastr.error("녹음을 업로드할 수 없습니다. 다시 시도해 주세요.");
	    	        }
	            }
	            // 녹음 시작
	            mediaRecorder.start();
	            isRecording = true;
	            Util.addClass($voiceBtn, "recording");
			}else{
				const res = await Modal.confirm({msg: "녹음을 중단합니다."});
				if(res != "OK"){
					return;
				}
				mediaRecorder.stop();
				isRecording = false;
				Util.removeClass($voiceBtn, "recording");
			}
			
		};
	}
	//회의록작성
	if(authorities.includes("FUNC_REPORT")){
		$reportBtn.style.display = "";
		$reportBtn.onclick = () => {
			exitWithSave('/lime/meeting/'+meetingId+'/report');
		};
	}
}

/* 참석자 리스트 DOM 생성 및 이벤트 설정 */
function generateAttendeeDOM(attendee, attendRole){
	let html = attendeeHTML;
	
	let $infoCon = $(html.infoCon);
	if(attendee){
		switch(attendRole){
			case "FACILITATOR":
				$infoCon.addClass("u1");
				break;
			case "ASSISTANT":
				$infoCon.addClass("u2");
				break;
			case "ATTENDEE":
				$infoCon.addClass("u3");
				break;
			case "OBSERVER":
				$infoCon.addClass("u4");
				break;
			case "GUEST":
				$infoCon.addClass("u5");
				break;
		}
		
		let $infoBtn = $(html.infoBody.infoBtn);
		$infoCon.append($infoBtn);
		$infoBtn.on("click", function(){
			const $modal = Modal.get("userInfoModal");
			const $userPic = $modal.querySelector("#userPic");
			$userPic.innerHTML = "";
			const $img = Util.createElement("img", "img-profile", "my-4");
			$img.src = "/api/lime/user/"+attendee.user.userId+"/img";
			$img.onload = () => {
				Util.removeClass($userPic, "userPic");
				$userPic.appendChild($img);
			}
			$MEETING.Get.attendeeOne(attendee).then((result) => {
				const user = result.user;
				$modal.querySelector(".uDivision").innerHTML = (user.deptName);
				$modal.querySelector(".uNameGrade").innerHTML = (user.nameplate);
				$modal.querySelector(".uComNum").innerHTML = (user.userId);
				$modal.querySelector(".uPhonNum").innerHTML = (user.personalCellPhone);
				$modal.querySelector(".uEmail").innerHTML = (user.email);
				$modal.show();
			}).catch((err) => {
				showErrorMessage(err);
			});
		});
		let $name = $(html.infoBody.name);
		if(attendRole == "GUEST"){
			$name.html(attendee.user.nameplate);
		}else{
			$name.html(attendee.user.nameplate);
		}
		if(progress.entrySet.includes(attendee.user.userId)){
			$name.addClass("blue");
		}
		if(attendRole == "ASSISTANT" && authorities.includes("FUNC_UPDATE")){
			$name.on("click", function(){
				let list = [];
				list = list.concat(reqList);
				list = list.concat(optList);
				if(list.length == 0 && !assistantEmp){
					Modal.info({msg: "보조 진행자로 지정 가능한 참석자가 없습니다."});
				}else{
					showSelectAssistantModal(list, function(attendee){
						selectNewAssistant(attendee)					
					});
				}
			});
			$name.css("cursor", "pointer");
		}
		$infoCon.append($name);
		let $attendBtn = $(html.infoBody.attendBtn);
		if(attendee.attendYN == "Y"){
			$infoCon.addClass("a1");
			$attendBtn.addClass("switchOn");
		}
		if(authorities.includes("FUNC_CHECK")){
			$attendBtn.on("click", function(){
				let attendYN = ($(this).hasClass("switchOn"))?"N":"Y";
				$MEETING.Put.check({
					meetingId: meetingId, 
					attendId: attendee.attendId, 
					attendYN: attendYN}
				).then(function(res){
					let status = res.status;
					if(status == 200){
						SocketService.sendUpdateMsg({
							resourceType: "ATTENDEE",
							data: attendee.attendId
						});
					}else{
						throw res;
					}
				}).catch(function(err){
					showErrorMessage(err);
				});
			});
			$infoCon.append($attendBtn);
		}
	}
	return $infoCon;
}

function generateEmptyAssistantDOM(){
	let html = attendeeHTML;
	
	let $infoCon = $(html.infoCon);
	let $name = $(html.infoBody.name);
	let $nameInput = $(html.assistantBody.nameInput);
	$infoCon.addClass("u2");
	if(authorities.includes("FUNC_UPDATE")){
		//회의 진행자, 보조 진행자는 보조 진행자 지정 이벤트 활성화
		//let $assignBtn = $(html.assistantBody.assignBtn); 
		//$infoCon.append($assignBtn);
		$nameInput.on("click", function(){
			let list = [];
			list = list.concat(reqList);
			list = list.concat(optList);
			if(list.length == 0){
				Modal.info({msg: "보조 진행자로 지정 가능한 참석자가 없습니다."});
			}else{
				showSelectAssistantModal(list, function(attendee){
					selectNewAssistant(attendee)					
				});
			}
		});
	}
	$name.html($nameInput)
	$infoCon.append($name);
	
	return $infoCon;
}


/* 보조 진행자 지정 */
function selectNewAssistant(attendee) {
	console.log("selectNewAssistant", attendee);
	let data;
	let attendId;
	let assistantYN;
	if(attendee == null){
		attendId = assistantEmp.attendId;
		assistantYN = 'N';
	}else{
		attendId = attendee.attendId;
		assistantYN = 'Y';
	}
	$MEETING.Put.assistant({
		meetingId: meetingId, 
		attendId: attendId, 
		assistantYN: assistantYN
	}).then(function(res){
		let status = res.status;
		if(status == 200){
			SocketService.sendUpdateMsg({
				resourceType: "ATTENDEE",
			});
		}else{
			throw res;
		}
	}).catch(function(err){
		showErrorMessage(err);
	});
	
}

//참석자 추가
async function showAddAttendeeModal(){
	const postAttendeeList = async (insertList) => {
		if(insertList.length == 0){
			return;
		}
		try{
			const res = await $MEETING.Post.attendeeList({
				meetingId: meetingId,
				list: insertList,
			});
			if(res.status == 200){
				SocketService.sendUpdateMsg({
					resourceType: "ATTENDEE",
				});
			}else{
				throw res;
			}
		}catch(err){
			console.error(err);
			Modal.error({response: err});
		}
		
	}
	const showUserModal = () => {
		const disabled = attendeeList.map(attendee => attendee.userId);
		UserSearchModal.init({
			officeCode: officeCode,
		});

		UserSearchModal.update({
			checked: [],
			disabled: disabled,
		});
		UserSearchModal.unbind("change");
		UserSearchModal.on("change", async (selectedNodes) => {
			const selectedAttendeeList = [];
			selectedNodes.forEach(node => {
				const attendee = {
					userId: node.userId,
					attendRole: "ATTENDEE",
				}
				selectedAttendeeList.push(attendee);
			});
			await postAttendeeList(selectedAttendeeList);
		});
		UserSearchModal.show();
	}
	showUserModal();
}

/* 출석 현황 체크 */
function calAttNum(){
	let $div = $(".userListDiv")
	attendCount = $div.find(".a1").length;
	//signCount = $div.find("img").length;
	
	$("#totalCount").text(totalCount);
	$("#attendCount").text(attendCount);
	//$("#signCount").text(signCount);
}

/* 판서할 파일 세팅 및 이벤트 설정 */
function initBookletMng(instanceProvider){
	_bookletMng = BookletMng(instanceProvider);
	_bookletMng.on("selectFile", function(fileKey, pageno){
		let $controlBtn = $("div.btnControl");
		if(nowControlStatus == "control"){
			SocketService.sendNoticeMsg({
				actionType: "CONTROL",
				data: {
					fileKey: fileKey,
					pageno: pageno
				}
			});
		}
	});
	_bookletMng.on("laserStart", function(pos){
		if(nowControlStatus == "control"){
			SocketService.sendNoticeMsg({
				actionType: "HIGHLIGHT",
				data: {
					type: "laserStart",
					pos: pos,
				}
			});
		}
	});
	_bookletMng.on("laserMove", function(pos){
		if(nowControlStatus == "control"){
			SocketService.sendNoticeMsg({
				actionType: "HIGHLIGHT",
				data: {
					type: "laserMove",
					pos: pos,
				}
			});
		}
	});
	_bookletMng.on("laserEnd", function(){
		if(nowControlStatus == "control"){
			SocketService.sendNoticeMsg({
				actionType: "HIGHLIGHT",
				data: {
					type: "laserEnd",
				}
			});
		}
	});
	FileService.on("select", async (data = {}) => {
		try{
			await _bookletMng.selectFile(data);
		}catch(err){
			showErrorMessage(err);
		}
	});
}

const connectWebsocket = async () => {
	return new Promise((resolve, reject) => {
		SocketService.init({
			debug: true,
			instanceProvider: instanceProvider,
			destinationURL: "/update/"+meetingId,
		});
		SocketService.connected(() => {
			SocketService.subscribe({
				url: '/room/'+meetingId,
				receiver: async (e) => {
					const response = JSON.parse(e.body);
					console.log("response", response)
			    	switch(response.messageType){
				    	case "UPDATE":
				    		mutex.runExclusive(async () => {
				    			await updateMsgHandler(response);
				    		});
				    		break;
				    	case "REQUEST":
				    		mutex.runExclusive(async () => {
				    			await requestMsgHandler(response);
				    		});
				    		break;
				    	case "NOTICE":
				    		await noticeMsgHandler(response);
				    		break;
				    	case "SYNC":
				    		mutex.runExclusive(async () => {
				    			await syncMsgHandler(response);
				    		});
				    		break;
			    	}
				}
			});
			SocketService.subscribe({
				url: '/room/'+meetingId+'/userId/'+loginId,
				receiver: async (e) => {
					const response = JSON.parse(e.body);
					privateMsgHandler(response);
				}
			});
			SocketService.sendJoinMsg();
			window.onoffline = () => {
				SocketService.disconnect();
			}
			resolve();
		}).disconnected(async () => {
			const isServerOnline = await Util.isServerOnline();
			if(!isServerOnline){
				await Modal.info({
					msg: "서버와의 연결이 끊어졌습니다. 재로그인 해 주십시오.",
					singleton: true,
				}).then(() => {
					exitWithoutSave("/login");
				});
			}
			SocketService.connect();
			/*
			let connectionTimeout;
			let connectionInterval;
			const cleantimer = () => {
				if(connectionTimeout){
					clearTimeout(connectionTimeout);
					connectionTimeout = null;
				}
				if(connectionInterval){
					clearInterval(connectionInterval);
					connectionInterval = null;
				}
				Modal.endLoading();
			}
			const reconnect = () => {
				cleantimer();
				location.reload();
			}
			const exitconnect = () => {
				cleantimer();
				Modal.info({
					msg: "서버와의 연결이 끊어졌습니다. 재로그인 해 주십시오.",
					singleton: true,
				}).then(() => {
					evtHandler.unsetExitListener();
					location.href = "/login";
				});
			}
			window.ononline = ()=>{ 
				reconnect();
			}; 
			Modal.closeAll();
			Modal.startLoading({
				text: '네트워크 연결을 기다리는 중입니다',
				singleton: true,
				animation: 'wave'
			});
			connectionTimeout = setTimeout(() => {
				exitconnect();
			}, 30000);
			connectionInterval = setInterval(() => {
				const connect = Util.isConnected();
				const online = Util.isOnline();
				//console.log("connect", connect, "online", online);
				if(connect && online){
					reconnect();
				}else if(!connect && online){
					exitconnect();
				}
			}, 3000);
			reject();
			*/
		}).connect();
	});
}

/* websocket 수신받은 전체 메시지 처리 */
function requestMsgHandler(response) {
	let sender = response.sender;
	let data = response.data;
	switch(response.actionType) {
	
	}
}

function updateMsgHandler(response) {
	return new Promise(async (resolve, reject) => {
		let sender = response.sender;
		let data = response.data;
		switch(response.resourceType) {
			case 'ASSISTANT' :
				if(data == loginId){
					authorities = await $MEETING.Get.userAuthorityForMeeting(meetingId);
					initManageBtn();
				}
				initAttendeeList();
				break;
			case 'ATTENDEE' :
				const updateAttendee = async (param)=> {
					console.log("updateAttendee", param);
					let attendee = attendeeMap[param.attendId];
					if(attendee){
						const {
							attendRole= attendee.attendRole,
							assistantYN= attendee.assistantYN,
							attendYN= attendee.attendYN,
							exitYN= attendee.exitYN,
							signYN= attendee.signYN,
							signSrc= attendee.signSrc,
						} = param;
						attendee.attendRole = attendRole;
						attendee.assistantYN = assistantYN;
						attendee.attendYN = attendYN;
						attendee.exitYN = exitYN;
						attendee.signYN = signYN;
						attendee.signSrc = signSrc;
					}else{
						attendee = param;
					}
					attendeeMap[attendee.attendId] = attendee;
					if(param.assistantYN && attendee.userId == loginId){
						authorities = await $MEETING.Get.userAuthorityForMeeting(meetingId);
						initManageBtn();
					}
				}
				const updateList = [].concat(data);
				for(let attendee of updateList){
					//updateAttendee 처리중 권한 갱신이 필요한경우 갱신될때까지 대기
					await updateAttendee(attendee);
				}
				initAttendeeList(Object.values(attendeeMap));
				break;
			case 'SCHEDULE':
				if(data.skdStatus == 3 || data.skdStatus == 4){
					showSelectedFinishModal("hostCancel_guestModal");
				} else if(instanceProvider.assign.finishDateTime != data.finishDateTime){
					let beforeFinishM = moment(instanceProvider.assign.finishDateTime);
					let nowFinishM = moment(data.finishDateTime);
					let diff = (beforeFinishM.isValid())?moment.duration(beforeFinishM.diff(nowFinishM)).asMilliseconds():0;

					instanceProvider.assign.beginDateTime = data.beginDateTime;
					instanceProvider.assign.finishDateTime = data.finishDateTime;
					instanceProvider.assign.expDateTime = data.expDateTime;
					scheduleService.setFinishTimer();
					$(".finishCount").text("");
					$(".mTime").text(moment(instanceProvider.assign.beginDateTime).format("HH:mm") + " ~ " + moment(instanceProvider.assign.finishDateTime).format("HH:mm"));
				}
				break;
			case 'FILE':
				FileService.updateFile(data);
				break;
		}
		resolve();
	});
}
let nowControlStatus = "end";
let prevControlStatus = "end";
function noticeMsgHandler(response) {
	return new Promise(async (resolve, reject) => {
		const sender = response.sender;
		const data = response.data;
		const streamer = data.streamer;
		switch(response.actionType) {
			case 'JOIN' :
			case 'LEAVE' :
				if(progress.entrySet.length != data.entrySet.length){
					progress.entrySet = data.entrySet;
					initAttendeeList();
				}
				if(!progress.entrySet.includes(loginId)){
					SocketService.sendJoinMsg();
				}
				if(streamer){
					await setScreenControlData(data);
				}
				break;
			case 'HIGHLIGHT':
				if(nowControlStatus == "connect"){
					_bookletMng.laser(data);
				}
				break;
			case 'CONTROL' :
				await setScreenControlData(data);
				break;
		}
		resolve();
	});
}
/* 화면 공유 버튼 설정 */
function setScreenControlBtn(){
	const {
		streamer = null,
		fileKey = null,
		pageno = null,
	} = progress;
	const pageData = _bookletMng.getPageData();
	if(nowControlStatus == "end"){
		Modal.confirm({
			title:" 화면 공유",
			msg: "화면공유를 시작합니다.",
		}).then(function(btn){
			if(btn == "OK"){
				const pageData = _bookletMng.getPageData();
				if( pageData["file"] == "MEMO"){
					Modal.info({
						title:" 화면 공유",
						msg: "메모장은 화면공유를 지원하지 않습니다.",
					});
					return;
				}else if(!pageData["file"]){
					Modal.info({
						title:" 화면 공유",
						msg: "화면공유를 위한 첨부파일을 선택해 주세요.",
					});
					return;
				}
				SocketService.sendNoticeMsg({
					actionType: "CONTROL",
					data: {
						fileKey: pageData["fileKey"],
						pageno: pageData["pageno"]
					}
				});
			}
		});
	}else if(nowControlStatus == "control"){
		Modal.confirm({
			msg: "내 화면공유를 종료합니다."
		}).then((btn) => {
			if(btn == "OK"){
				SocketService.sendNoticeMsg({
					actionType: "CONTROL",
				});
			}
		});
	}else{
		let select = [];
		if(nowControlStatus == "connect"){
			select.push({text: "화면공유 잠시중단", isPrimary: false, key: "disconnect"});
		}else if(nowControlStatus == "disconnect"){
			select.push({text: "화면공유 계속", isPrimary: false, key: "connect"});
		}
		select.push({text: "내 화면공유를 시작합니다.", isPrimary: true, key: "control"});
		Modal.select({title: "화면 공유", select: select}).then((btn) => {
			switch(btn){
				case "connect":
					setScreenControlMode("connect");
					syncScreenControl();
					break;
				case "disconnect":
					setScreenControlMode("disconnect");
					break;
				case "control":
					SocketService.sendNoticeMsg({
						actionType: "CONTROL",
						data: {
							fileKey: pageData["fileKey"],
							pageno: pageData["pageno"]
						}
					});
					break;
			}
		});
	}
}
let pageData = null;
/* 화면 공유를 위해 필요한 데이터 설정 */
async function setScreenControlData(data = {}){
	const {
		streamer,
		streamerSessionId,
		fileKey,
		pageno,
	} = data;
	const nowStreamer = progress.streamer;
	if(!streamer){
		await setScreenControlMode("end");
		pageData = null;
		return;
	}else{
		progress.streamer = streamer;
		progress.streamerSessionId = streamerSessionId;
		progress.fileKey = fileKey;
		progress.pageno = pageno;
		if(streamer != nowStreamer){
			if( streamerSessionId == sessionId ){
				setScreenControlMode("control");
			}else{
				if(nowControlStatus != "disconnect"){
					setScreenControlMode("connect");
				}
			}
		}
		syncScreenControl();
	}
	
}
/* 화면 공유모드 설정 */
async function setScreenControlMode(mode){
	const {
		streamer = null,
		fileKey = null,
		pageno = null,
	} = progress;
	Modal.close("confirmRestorePage");
	const ctrlBtnElem = document.querySelector("div.btnControl");
	const ctrlTextElem = ctrlBtnElem.querySelector("span");
	switch(mode){
		case "control":{ // 화면 공유
			const streamerData = attendeeList.find(attendee => attendee.user.userId == streamer);
			nowControlStatus = "control";
			ctrlBtnElem.classList.remove("c0", "c1", "c2", "c3");
			ctrlBtnElem.classList.add("c1");
			ctrlTextElem.innerHTML = streamerData.user.userName;
			
			_bookletMng.deleteWaterMark();
			_bookletMng.setControlerMode();
		}
			break;
		case "end": // 화면 공유 종료
			nowControlStatus = "end";
			delete progress?.streamer;
			delete progress?.fileKey;
			delete progress?.pageno;
			ctrlTextElem.innerHTML = "";
			ctrlBtnElem.classList.remove("c0", "c1", "c2", "c3");
			ctrlBtnElem.classList.add("c0");
			
			_bookletMng.deleteWaterMark();
			_bookletMng.setNormalMode();
			if(pageData){
				await restorePageBeforeControled();
			}
			break;
		case "connect":{ // 화면 공유 수신 계속
			const streamerData = attendeeList.find(attendee => attendee.user.userId == streamer);
			if(streamerData){
				const msg = (nowControlStatus == 'disconnect')?"화면 공유를 복귀합니다":"화면 공유를 시작합니다";
				nowControlStatus = "connect";
				ctrlBtnElem.classList.remove("c0", "c1", "c2", "c3");
				ctrlBtnElem.classList.add("c2");
				ctrlTextElem.innerHTML = streamerData.user.userName;
				_bookletMng.showWaterMark(streamerData.user.userId);
				_bookletMng.setPassiveMode();
				toastr.error(msg, streamerData.user.userName);
			}
			if(prevControlStatus == "end"){
				const {file, pageno} = _bookletMng.getPageData();
				if(file && paeno){
					pageData = {
						file: file,
						pageno: pageno
					}
				}
				pageData = _bookletMng.getPageData();
			}
		}
			break;
		case "disconnect": // 화면 공유 수신 중단
			nowControlStatus = "disconnect";
			ctrlBtnElem.classList.remove("c0", "c1", "c2", "c3");
			ctrlBtnElem.classList.add("c3");
			ctrlTextElem.innerHTML = "잠시중단";
			
			_bookletMng.deleteWaterMark();
			_bookletMng.setHoldMode();
			break;
	}
	prevControlStatus = nowControlStatus;
}

async function restorePageBeforeControled(){
	const {file, pageno} = _bookletMng.getPageData();
	if(file == pageData.file && pageno == pageData.pageno){
		return;
	}
	Modal.close("confirmRestorePage");
	const res = await Modal.confirm({
		title: "화면 공유",
		msg: "화면 공유 이전의 본인 화면으로 이동하시겠습니까?",
		id: "confirmRestorePage",
	});
	if(res == "OK"){
		await _bookletMng.selectFile({
			file: pageData.file,
			pageno: pageData.pageno
		})
	}
	pageData = null;
}

async function syncScreenControl(){
	const {
		streamer = null,
		fileKey = null,
		pageno = null,
	} = progress;
	if(nowControlStatus == "disconnect"){
		return;
	}
	const file = FileService.getFile(fileKey);
	await _bookletMng.selectFile({
		file: file,
		pageno: pageno
	})
}

async function syncMsgHandler(response) {
	return new Promise(async (resolve, reject) => {
		let sender = response.sender;
		let data = response.data;
		switch(response.resourceType) {
			case 'PROGRESS':
				break;
			case 'FILE' :
				const newFileList = data;
				const newFileKeyList = newFileList.map(file => file.fileKey);
				const deletedFileList = FileService.getFileList().filter(row => {
					return !newFileKeyList.includes(row.fileKey);
				});
				const deletedFileKeyList = deletedFileList.map(file => file.fileKey);
				const pageData = _bookletMng.getPageData();
				if(deletedFileKeyList.includes(pageData.fileKey)){
					_bookletMng.clear();
				}
				FileService.setFileList(data);
				break;
			case 'ATTENDEE' :
				for(let attendee of data){
					attendeeMap[attendee.attendId] = attendee;
					if(attendee.assistantYN == 'Y' && attendee.userId == loginId){
						authorities = await $MEETING.Get.userAuthorityForMeeting(meetingId);
						initManageBtn();
					}else if(assistantEmp){
						if(attendee.userId == loginId && attendee.assistantYN == 'N'){
							authorities = await $MEETING.Get.userAuthorityForMeeting(meetingId);
							initManageBtn();
						}
					}
				}
				initAttendeeList(Object.values(attendeeMap));
				break;
		}
		resolve();
	});
}

/* 
 * websocket 수신받은 개인 메시지 처리 
 * 일반적으로 사용자가 요청한 결과가 처리되지 않은 경우 수신
 */
function privateMsgHandler(response) {
	let msgType = response.msgType;
	let data = response.data;
	if(response.resultCode == "FORBIDDEN"){
		Modal.info({msg: "해당 기능을 요청할 권한이 없습니다."});
		return;
	}else if(response.resultCode == "ERROR"){
		Modal.info({msg: "해당 기능을 처리할 수 없습니다.<br>잠시 후 다시 시도해 주세요."});
		return;
	}else if(response.resultCode == "CONFLICT"){
		Modal.info({msg: response.msg});
		return;
	}
}

async function showSelectedFinishModal(id){
	Modal.closeAll();
	Modal.endLoading();
	try{
		const res = await Modal.show(id);
		switch(res){
			case "CLOSE":
				break;
			case "EXIT":
				try{
					exitWithSave("/lime/home");
				}catch(err){
					showErrorMessage(err);
				}
				break;
			case "CANCEL":
				const reply = await Modal.confirm({
					msg: "회의를 취소하시겠습니까?"
				});
				if(reply == "OK"){
					AjaxBuilder({
						request: $MEETING.Post.approval,
						param: {
							scheduleId: scheduleId,
							status: 3,
							comment: '신청자 취소',
						},
						exception: 'success-only',
					}).success(res => {
						Modal.startLoading();
						setTimeout(() => {
							SocketService.sendUpdateMsg({
								resourceType: "SCHEDULE",
								data: instanceProvider.assign.scheduleId
							});
							exitWithoutSave("/lime/home");
						}, 3000);
					}).error(err => {
						Modal.error({response: err});
					}).finally(() => {
					}).exe();
				}
				break;
			case "FINISH":
				try{
					const res = await $MEETING.Put.scheduleFinish({
						scheduleId: instanceProvider.assign.scheduleId
					});
					const status = res.status;
					if(status == 200){
						SocketService.sendUpdateMsg({
							resourceType: "SCHEDULE",
							data: instanceProvider.assign.scheduleId
						});
						exitWithSave("/lime/home");
					}else{
						throw res;
					}
				}catch(err){
					showErrorMessage(err);
				}
				break;
		}
	}catch(err){
		console.log("err", err);
	}
}

/* 회의 진행자/보조 진행자 여부 */
function isManager(){
	if(loginId == hostEmp?.userId || loginId == assistantEmp?.userId){
		return true;
	}
	return false;
}
/**
 * 이벤트 설정
 */
const evtHandler = {
	init(){
		this.beforeUnloadListener = (event) => {
			event.returnValue="";
		}
		this.setExitListener();
		this.setExitBtn();
		this.setScreenResizeBtn();
		this.setControlBtn();
		this.setMemoBtn();
		this.securityInfoBtn();
	},
	setExitListener(){
		addEventListener("beforeunload", this.beforeUnloadListener, {capture: true});
	},
	unsetExitListener(){
		removeEventListener("beforeunload", this.beforeUnloadListener, {capture: true});
	},
	setExitBtn(){
		const $logoBtn = Util.getElement("#logoBtn");
		if($logoBtn){
			$logoBtn.onclick = () => {
				const msg = "회의스케줄 화면으로 이동합니다. <br>회의스케줄 화면에서 해당 회의를 클릭하시면 재입장이 가능합니다.";
				Modal.confirm({msg: msg}).then(async (btn) => {
					if(btn == "OK"){
						exitWithSave("/lime/home");
					}
				});
			}
		}
		const $archiveBtn = Util.getElement("#archiveBtn");
		if($archiveBtn){
			$archiveBtn.onclick = () => {
				const msg = "내 파일함으로 이동합니다. <br>회의실 재입장은 내파일함 화면 타이틀 옆의 뒤로가기 버튼 또는 회의 스케줄 화면에서 가능합니다.";
				Modal.confirm({msg: msg}).then(async (btn) => {
					if(btn == "OK"){
						exitWithSave("/lime/meeting/archive/manage/user");
					}
				});
			}
		}
	},
	setScreenResizeBtn(){
		const $expandBtn = Util.getElement(".btnExpand");
		const $contractBtn = Util.getElement(".btnContract");
		if($expandBtn && $contractBtn){
			[$expandBtn, $contractBtn].forEach($btn => {
				$btn.show = () => {
					$btn.style.display = "";
				}
				$btn.hide = () => {
					$btn.style.display = "none";
				}
			})
			const screenContract = () => {
				if (!document.fullscreenElement && !document.webkitIsFullScreen && !document.mozFullScreen && !document.msFullscreenElement) {
					$contractBtn.hide();
			    	$expandBtn.show();
			    }
			}
			document.addEventListener('fullscreenchange', screenContract);
			document.addEventListener('webkitfullscreenchange', screenContract);
			document.addEventListener('mozfullscreenchange', screenContract);
			document.addEventListener('MSFullscreenChange', screenContract);
			const docV = document.documentElement;
			$expandBtn.onclick = () =>{
				if (docV.requestFullscreen)
			        docV.requestFullscreen();
			    else if (docV.webkitRequestFullscreen) // Chrome, Safari (webkit)
			        docV.webkitRequestFullscreen();
			    else if (docV.mozRequestFullScreen) // Firefox
			        docV.mozRequestFullScreen();
			    else if (docV.msRequestFullscreen) // IE or Edge
			        docV.msRequestFullscreen();
				$expandBtn.hide();
				$contractBtn.show();
			}
			$contractBtn.onclick = () =>{
				if (document.exitFullscreen)
				    document.exitFullscreen();
			    else if (document.webkitExitFullscreen) // Chrome, Safari (webkit)
			    	document.webkitExitFullscreen();
			    else if (document.mozCancelFullScreen) // Firefox
			    	document.mozCancelFullScreen();
			    else if (document.msExitFullscreen) // IE or Edge
			    	document.msExitFullscreen();
				$contractBtn.hide();
				$expandBtn.show();
			}
		}
	},
	setControlBtn(){
		const $btn = Util.getElement(".btnControl");
		if($btn){
			$btn.onclick = () => {
				setScreenControlBtn();
			}
		}
	},
	setMemoBtn(){
		const $btn = Util.getElement("#memoBtn");
		if($btn){
			$btn.onclick = async () => {
				await _bookletMng.selectFile({
					file: "MEMO",
					pageno: 1
				});
			}
		}
	},
	securityInfoBtn(){
		const $btn = Util.getElement("#securityInfoBtn");
		if($btn){
			$btn.onclick = () => {
				Modal.show("securityInfoModal");
			}
		}
	}
}

function showErrorMessage(err){
	console.log("err",err);
	Modal.error({response:err});
}

const domHandler = {
	setDom(){
		this.setSecretLevel();
		this.setRoomInfo();
		this.setSchedule();
		this.setTitle();
	},
	setSecretLevel(){
		const assign = instanceProvider.assign;
		const secretYN = assign.secretYN;
		const $badge = Util.getElement("#levelBadge");
		Util.addClass($badge, (secretYN == 'Y')?"secuY":"secuN");
		
		const $infoBtn = Util.getElement("#securityInfoBtn");
		if(secretYN == 'N'){
			$infoBtn.remove();
		}
	},
	setRoomInfo(){
		const assign = instanceProvider.assign;
		const room = assign.room;
		
		const $name = Util.getElement("#roomName");
		$name.innerHTML = room.roomName;
	},
	setSchedule(){
		const assign = instanceProvider.assign;
		const $scheduleTime = Util.getElement("#scheduleTime");
		$scheduleTime.innerHTML = moment(assign.beginDateTime).format("HH:mm")+" ~ "+moment(assign.finishDateTime).format("HH:mm");
	},
	setTitle(){
		const assign = instanceProvider.assign;
		const $title = Util.getElement("#title");
		$title.innerHTML = assign.title;
	}
}

/* 초기화 */
;(async () => {
	Modal.startLoading({
		text: "전자회의를 준비중입니다..."
	});
	const assign = await $MEETING.Get.assignOne(scheduleId);
	instanceProvider.init({
		loginId: loginId,
		roomType: assign.roomType,
		scheduleId: scheduleId,
		meetingId: meetingId,
		assign: assign,
	});
	domHandler.setDom();
	try{
		scheduleService.setBeginTimer();
		const attendeeList = await $MEETING.Get.attendeeListByMeeting({
			meetingId: meetingId
		});
		initAttendeeList(attendeeList);
		initManageBtn();
		await FileService.init({
			instanceProvider: instanceProvider,
		});
		initBookletMng(instanceProvider);
		evtHandler.init();
		await connectWebsocket();
	}catch(err){
		showErrorMessage(err);
	}finally{
		Modal.endLoading();
	}
})();
