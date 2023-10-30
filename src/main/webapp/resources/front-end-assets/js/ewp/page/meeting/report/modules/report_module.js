/**
 * 
 */
import {Util, Modal, AjaxBuilder} from '/resources/core-assets/essential_index.js';
import {AssignCall as $AS, ReportCall as $REPORT, AttendeeCall as $ATT} from '/resources/front-end-assets/js/ewp/comm/ajax_index.js';

const domHandler = {
}
/**
 * 회의록 내용 화면에 표시
 */
const inputHandler = {
	//일정 표시
	setSchedule(data = {}){
		const {
			beginDateTime,
			finishDateTime,
		} = data;
		const $input = Util.getElement("#scheduleInput");
		$input.innerHTML = moment(beginDateTime).format("YYYY.MM.DD") + " " + moment(beginDateTime).format("HH:mm") + " ~ " + moment(finishDateTime).format("HH:mm")
	},
	//장소 이름 표시
	setRoomName(roomName){
		const $input = Util.getElement("#roomNameInput");
		$input.innerHTML = roomName; 
	},
	//회의 제목 표시
	setTitle(title){
		const $input = Util.getElement("#titleInput");
		$input.innerHTML = title; 
	},
	//회의 내용 표시
	async setContents(reportContents){
		editorHandler.init("smartEditor");
		await editorHandler.load(reportContents);
	},
	//회의록 작성일자 표시
	setModDateTime(modDateTime){
		const $input = Util.getElement("#modDateTimeInput");
		$input.innerHTML = moment(modDateTime).format("YYYY.MM.DD") + " " + moment(modDateTime).format("HH:mm")
	},
	//회의록 작성자 표시
	setReporter(data = {}){
		const {
			nameplate,
			tel = "",
		} = data;
		const $nameinput = Util.getElement("#nameplateInput");
		$nameinput.innerHTML = nameplate;
		const $telinput = Util.getElement("#telInput");
		$telinput.innerHTML = tel;
	},
	//회의록 검토 확인 정보 표시
	setConfirmCount(attendeeList){
		const totalCnt = attendeeList.length;
		const confirmCnt = attendeeList.filter(attendee => attendee.signYN == 'Y').length;
		const $input = Util.getElement("#reportConfirmCountInput");
		$input.innerHTML = confirmCnt+"/"+totalCnt;
	},
	//회의 참석자 표시
	setAttendee(attendeeList){
		const $attendeeBox = Util.getElement("#attendeeBox");
		const createAttendeeRow = (attendee) => {
			const $row = Util.createElement("li");
			const $nameDiv = Util.createElement("div", "attendUser");
			$nameDiv.innerHTML = attendee.user.nameplate;
			if(attendee.attendYN == 'Y'){
				Util.addClass($nameDiv, "a1");
			}
			$row.appendChild($nameDiv);
			if(attendee.signYN == 'Y'){
				const $signDiv = Util.createElement("div", "signImg");
				const $sign = Util.createElement("img");
				$sign.src = attendee.signSrc;
				$signDiv.appendChild($sign);
				$row.appendChild($signDiv);
			}
			return $row;
		}
		const rowList = [];
		for(const attendee of attendeeList){
			const $row = createAttendeeRow(attendee);
			$attendeeBox.appendChild($row);
		}
	}
}
/**
 * 스마트 에디터 관련 기능 제어
 * 
 * #init(id) 스마트에디터 설정. 에디터가 적용될 textarea의 id값이 반드시 필요.
 * #load(@nullable contents) 스마트에디터 초기화. contents값이 유효한 경우 해당 내용을 적용. 에디터가 적용될 textarea가 reaonly인 경우 편집 기능 비활성화
 * #get() 스마트에디터로 작성된 내용 반환
 */
const editorHandler = {
	init(id){
		const $smartEditor = Util.getElement("#"+id); 
		this.id = id;
		this.editor = $smartEditor;
		this.oEditors = [];
		$smartEditor.onpaste = () => {
			const se = this.oEditors.getById[id];
			se.exec("UPDATE_CONTENTS_FIELD", []);

			//se2에 입력된 값
			const resContent = $smartEditor.value;
			//string to DOM
			const htmlObject = document.createElement('div');
			htmlObject.innerHTML = resContent;
			//table 노드 모두 가져옴
			const allTable = htmlObject.querySelectorAll('table');
			for (let i = 0; i < allTable.length; i++) {
				//table의 class속성 설정
				allTable[i].setAttribute('class', "__se_tbl");
			}

			//se2 값 초기화
			se.exec("SET_IR", [ "" ]);
			//수정한 내용으로 se2 값 설정
			se.exec("PASTE_HTML", [ htmlObject.innerHTML ]);
		}
	},
	load(contents){
		return new Promise((resolve, reject) => {
			this.oEditors = [];
			nhn.husky.EZCreator.createInIFrame({
				oAppRef : this.oEditors,
				//textarea의 id
				elPlaceHolder : this.id,
				//SmartEditor2Skin.html의 경로
				sSkinURI : "/resources/library/smarteditor2/SmartEditor2Skin.html",
				fCreator : "createSEditor2",
				htParams : {
					// 툴바 사용 여부 (true:사용/ false:사용하지 않음) 
					bUseToolbar : (this.editor.readOnly)?false:true,
							// 입력창 크기 조절바 사용 여부 (true:사용/ false:사용하지 않음) 
							bUseVerticalResizer : true,
							// 모드 탭(Editor | HTML | TEXT) 사용 여부 (true:사용/ false:사용하지 않음) 
							bUseModeChanger : false
				},
				fOnAppLoad : () => {
					this.oEditors.getById[this.id].exec("SET_IR", [""]);
					if(contents){
						this.oEditors.getById[this.id].exec("PASTE_HTML", [ Util.unescape(contents) ]);
					}
					if(this.editor.readOnly == true){
						this.oEditors.getById[this.id].exec("DISABLE_WYSIWYG");
						this.oEditors.getById[this.id].exec("DISABLE_ALL_UI");
					}
					resolve();
				}
			});
		});
	},
	get(){
		const removeEndBR = (data) => {
			let endWith = data.substring(data.length-4, data.length);
			if(endWith == "<br>" || endWith == "<BR>"){
				return data.substring(0, data.length-4);
			}
			return data;
		}
		this.oEditors.getById[this.id].exec("UPDATE_CONTENTS_FIELD", []);
		const contents = removeEndBR(this.editor.value);
		return contents;
	},
	async saveAsPDF(){
		const contentsToCanvas = async (contents) => {
			const $con = Util.createElement("div");
			$con.id = "report_container";
			const $wrapper = Util.createElement("div", "report_content_wrapper");
			const $content = Util.createElement("div", "report_content");
			$content.innerHTML = contents;
			
			$wrapper.appendChild($content);
			$con.appendChild($wrapper);
			const $contentsBox = Util.getElement("#contentsBox");
			$contentsBox.innerHTML = "";
			$contentsBox.appendChild($con);
			
			const $reportBox = Util.getElement("#reportBox");
			$reportBox.style.width = "210mm";
			return await html2canvas($reportBox);
		}
		const contents = this.get(); 
		const canvas = await contentsToCanvas(contents);
		const imgData = canvas.toDataURL('image/png');
	 
		const imgWidth = 210; // 이미지 가로 길이(mm) / A4 기준 210mm
		const pageHeight = imgWidth * 1.414;  // 출력 페이지 세로 길이 계산 A4 기준
		const imgHeight = canvas.height * imgWidth / canvas.width;
		let heightLeft = imgHeight;
		const margin = 0; // 출력 페이지 여백설정
		let position = 0;
		const doc = new jspdf.jsPDF({
			orientation: 'p',
			unit: 'mm',
			format: 'a4',
		});
	       
	    // 첫 페이지 출력
	    doc.addImage(imgData, 'PNG', margin, position, imgWidth, imgHeight);
	    heightLeft -= pageHeight;
	         
	    // 한 페이지 이상일 경우 루프 돌면서 출력
	    while (heightLeft >= 20) {
	        position = heightLeft - imgHeight;
	        doc.addPage();
	        doc.addImage(imgData, 'PNG', margin, position, imgWidth, imgHeight);
	        heightLeft -= pageHeight;
	    }
	    const pdf = doc.output('blob');

	    return pdf;
	},
	/*async saveAsPDF(){
		const contentsToCanvas = async (contents) => {
			const $con = Util.createElement("div");
			$con.id = "report_container";
			const $wrapper = Util.createElement("div", "report_content_wrapper");
			const $content = Util.createElement("div", "report_content");
			$content.innerHTML = contents;
			
			$wrapper.appendChild($content);
			$con.appendChild($wrapper);
			const $contentsBox = Util.getElement("#contentsBox");
			$contentsBox.innerHTML = "";
			$contentsBox.appendChild($con);
			
			const $reportBox = Util.getElement("#reportBox");
			$reportBox.style.width = "210mm";
			return await html2canvas($reportBox);
		}
		const contents = this.get(); 
		const canvas = await contentsToCanvas(contents);
		const doc = new jspdf.jsPDF({
			orientation: 'p',
			unit: 'mm',
			format: 'a4',
		});
		console.log("canvas width", canvas.width, "height", canvas.height);
		const A4Width = 210;
		const A4Height = A4Width * 1.414;
		
		const totalWidth = canvas.width;
		const totalHeight = canvas.height;
		const pdfWidth = totalWidth; // Image width in mm (A4 size: 210mm)
		const pdfHeight = pdfWidth * 1.414; // Page height calculated based on A4 ratio
		const marginTop = 10; // Top margin size in mm
		const marginBottom = 10; // Bottom margin size in mm
		const pageHeight = pdfHeight - marginTop - marginBottom; // Image height calculated based on available space
		
		let currentImageHeight = 0;
		
		while (true) {
		  const remainingImageHeight = totalHeight - currentImageHeight;
		  const nowHeight = Math.min(pageHeight, remainingImageHeight);
		  console.log("remainingImageHeight", remainingImageHeight, "nowHeight", nowHeight);
		  
		  const tempCanvas = document.createElement("canvas");
		  tempCanvas.width = pdfWidth;
		  tempCanvas.height = pdfHeight;
		  console.log("tempCanvas.width", tempCanvas.width, "tempCanvas.height", tempCanvas.height);
		  
		  const ctx = tempCanvas.getContext("2d");
		  ctx.drawImage(canvas, 0, currentImageHeight, pdfWidth, nowHeight, 0, 0, pdfWidth, nowHeight);
		  
		  const imgData = tempCanvas.toDataURL("image/png");
		  doc.addImage(imgData, "PNG", 0, marginTop, A4Width, A4Height);
		  
		  currentImageHeight += nowHeight;
		  if(currentImageHeight < totalHeight){
			  doc.addPage();
		  }else{
			  break;
		  }
		}
		
		const pdf = doc.output('blob');
	    return pdf;
	},*/
}

const putReport = async (data) => {
	Modal.startLoading();
	const res = await $REPORT.Put.report(data);
	Modal.endLoading();
	return res;
}

const evtHandler = {
	init(data){
		const {
			skdKey,
			meetingKey,
		} = data;
		this.setModBtn(meetingKey);
		this.setCloseBtn(skdKey);
		this.setCancelBtn(meetingKey);
		this.setTempSaveBtn(meetingKey);
		this.setOpenOpnBtn(meetingKey);
		this.setFinishBtn(meetingKey);
	},
	setModBtn(meetingKey){
		const $btn = Util.getElement("#modBtn");
		if($btn){
			$btn.onclick = () => {
				location.href = "/ewp/meeting/"+meetingKey+"/report/post";
			}
		}
	},
	setCloseBtn(skdKey){
		const $btn = Util.getElement("#closeBtn");
		if($btn){
			$btn.onclick = () => {
				location.href = "/ewp/meeting/assign/"+skdKey;
			}
		}
	},
	setCancelBtn(meetingKey){
		const $btn = Util.getElement("#cancelBtn");
		if($btn){
			$btn.onclick = () => {
				location.href = "/ewp/meeting/"+meetingKey+"/report";
			}
		}
	},
	setTempSaveBtn(meetingKey){
		const $btn = Util.getElement("#tempSaveBtn");
		if($btn){
			$btn.onclick = async () => {
				$btn.disabled = true;
				const reportContents = editorHandler.get();
				AjaxBuilder({
					request: $REPORT.Put.report,
					param: {
						meetingKey: meetingKey,
						reportContents: reportContents,
					},
					exception: 'success-only'
				}).success(async () => {
					await Modal.get("tempSaveModal").show();
					//location.href = "/ewp/meeting/"+meetingKey+"/report";
				}).finally(() => {
					$btn.disabled = false;
				}).exe();
			}
		}
	},
	setOpenOpnBtn(meetingKey){
		const $btn = Util.getElement("#openOpnBtn");
		if($btn){
			$btn.onclick = async () => {
				const modalres = await Modal.get("tempSaveRqModal").show();
				if(modalres != "OK"){
					return;
				}
				$btn.disabled = true;
				const reportContents = editorHandler.get();
				AjaxBuilder({
					request: $REPORT.Put.report,
					param: {
						meetingKey: meetingKey,
						reportContents: reportContents,
						reportStatus: "OPEN"
					},
					exception: 'success-only'
				}).success(() => {
					//location.href = "/ewp/meeting/"+meetingKey+"/report";
					location.reload();
				}).finally(() => {
					$btn.disabled = false;
				}).exe();
			}
		}
	},
	setFinishBtn(meetingKey){
		const $btn = Util.getElement("#finishBtn");
		if($btn){
			$btn.onclick = async () => {
				const modalres = await Modal.get("savePdfModal").show();
				if(modalres != "OK"){
					return;
				}
				$btn.disabled = true;
				const reportContents = editorHandler.get();
				const pdf = await editorHandler.saveAsPDF();
				AjaxBuilder({
					request: $REPORT.Put.report,
					param: {
						meetingKey: meetingKey,
						reportContents: reportContents,
						reportStatus: "FINISH",
						pdf: pdf,
					},
					exception: 'success-only'
				}).success(() => {
					location.href = "/ewp/meeting/"+meetingKey+"/report";
				}).finally(() => {
					$btn.disabled = false;
				}).exe();
			}
		}
	}
}



export default {
	init(data = {}){
		const {
			skdKey,
			meetingKey,
		} = data;
		evtHandler.init(data);
		this.skdKey = skdKey;
		this.meetingKey = meetingKey;
	},
	async setReport(){
		try{
			const assign = await $AS.Get.assignOne(this.skdKey);
			if(assign){
				inputHandler.setSchedule(assign);
				inputHandler.setRoomName(assign.room.roomName);
				inputHandler.setTitle(assign.title);
			}else{
				throw {
					status: 404
				}
			}
			const report = await $REPORT.Get.reportOne(this.meetingKey);
			if(report){
				await inputHandler.setContents(report.reportContents);
				inputHandler.setModDateTime(report.modDateTime);
				inputHandler.setReporter(report.reporter);
			}else{
				throw {
					status: 404
				}
			}
		}catch(err){
			console.error(err);
			await Modal.info({msg: "회의록 정보를 불러올 수 없습니다."});
			history.back();
		}
	},
	async setAttendee(){
		try{
			const attendeeList = await $ATT.Get.attendeeListByMeeting({meetingKey: this.meetingKey});
			inputHandler.setConfirmCount(attendeeList);
			inputHandler.setAttendee(attendeeList);
			
			this.getAttendeeList = () => {
				return attendeeList;
			}
		}catch(err){
			console.error(err);
			await Modal.info({msg: "참석자 정보를 불러올 수 없습니다."});
		}
	},
}