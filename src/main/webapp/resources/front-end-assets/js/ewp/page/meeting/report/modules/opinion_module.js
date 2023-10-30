/**
 * 
 */
import {Util, Modal} from '/resources/core-assets/essential_index.js';
import {LimePad} from '/resources/core-assets/module_index.js';
import {ReportCall as $REPORT, AttendeeCall as $ATT} from '/resources/front-end-assets/js/ewp/comm/ajax_index.js';
/**
 * 작성된 의견 표시
 */
const opnGenerator = {
	generate(opinionList){
		const $listBox = Util.getElement("#opinionBox");
		for(const opinion of opinionList){
			const isEditable = opnInputHandler.editable;
			const isDeletable = (loginKey == opinion.writerKey && isEditable)?true:false;
			const $row = this.createRow(opinion, isDeletable);
			//$listBox.appendChild($row);
			$listBox.insertBefore($row, $listBox.firstChild);
		}
	},
	clear(){
		const $listBox = Util.getElement("#opinionBox");
		$listBox.innerHTML = "";
	}
	,createRow(opinion, isDeletable){
		const $row = Util.createElement("div", "row", "board", "my-2");
		//작성자 칼럼
		const createWriterColumn = () => {
			const $column = Util.createElement("div", "attender");
			
			const $namespan = Util.createElement("span");
			$namespan.innerHTML = opinion.writerName;
			$column.appendChild($namespan);
			
			const $telspan = Util.createElement("span", "interphone",);
			$telspan.innerHTML = opinion.writerTel;
			$column.appendChild($telspan);
			
			$row.appendChild($column);
		}
		//의견 칼럼
		const createCommentColumn = () => {
			const $column = Util.createElement("div", "comment");
			
			const $commentspan = Util.createElement("span");
			$commentspan.innerHTML = opinion.comment;
			$column.appendChild($commentspan);
			if(isDeletable){
				const $delIcon = Util.createElement("i", "fas", "fa-times");
				$delIcon.onclick = async () => {
					const res = await opnReqHandler.deleteOpinion(opinion.opnId);
					if(res){
						$row.remove();	
					}
				}
				$column.appendChild($delIcon);
			}
			$row.appendChild($column);
		}
		//작성일 칼럼
		const createRegDateTimeColumn = () => {
			const $column = Util.createElement("div", "regDate");
			
			$column.innerHTML = moment(opinion.regDateTime).format("YYYY.MM.DD HH:mm");
			
			$row.appendChild($column);
		}
		createWriterColumn();
		createCommentColumn();
		createRegDateTimeColumn();
		
		return $row;
	}
}

/**
 * 의견 작성 이벤트 제어
 */
const opnInputHandler = {
	init(data = {}){
		const {
			loginKey,
			attendKey,
		} = data;
		this.editable = true;
		const $opinionInput = Util.getElement("#opinionInput");
		if(!$opinionInput){
			this.editable = false;
			return;
		}
		$opinionInput.oninput = () => {
			Util.acceptWithinLength($opinionInput);
		}
		const $opinionWriteBtn = Util.getElement("#opinionWriteBtn");
		$opinionWriteBtn.onclick = async () => {
			const comment = $opinionInput.value;
			if(!comment || comment == ""){
				return;
			}
			const param = {
				loginKey: loginKey,
				attendKey: attendKey,
				comment: comment,
			}
			opnReqHandler.writeOpinion(param);
			
		}
		this.enableWrite = () => {
			$opinionInput.disabled = false;
			$opinionWriteBtn.disabled = false;
		}
		this.disableWrite = () => {
			$opinionInput.disabled = true;
			$opinionWriteBtn.disabled = true;
		}
		this.clearWrite = () => {
			$opinionInput.value = "";
		}
		this.getOpinionWrite = () => {
			return {
				contents: $opinionInput.value,
			}
		}
		this.setReloadBtn();
	},
	setReloadBtn(){
		const $btn = Util.getElement("#reloadBtn");
		if($btn){
			$btn.onclick = async () => {
				$btn.disabled = true;
				await opnReqHandler.reloadOpinion();
				$btn.disabled = false;
			}
		}
	}
}

const reportAgreeHandler = {
	init(data = {}){
		const {
			attendKey,
			signYN = 'N',
		} = data;
		const $reportAgreeYN = Util.getElement("#agreeYN");
		if(!$reportAgreeYN){
			return;
		}
		if(signYN == 'Y'){
			$reportAgreeYN.checked = true;
		}else{
			$reportAgreeYN.checked = false;
		}
		$reportAgreeYN.onchange = async () => {
			const param = {
				attendKey: attendKey,
			}
			if($reportAgreeYN.checked == true){
				const signSrc = await this.getUserSign();
				if(signSrc){
					param.signSrc = signSrc;
					opnReqHandler.agreeReport(param);
				}else{
					$reportAgreeYN.checked = false;
				}
			}else{
				const res = await Modal.get("cancelAgreeModal").show();
				if(res == "OK"){
					opnReqHandler.disagreeReport(param);
				}else{
					$reportAgreeYN.checked = true;
				}
			}
		}
	},
	async getUserSign(){
		if(!this.signPad){
			this.signPad = new LimePad({
				section : "signPad",
				width: 280,
				height: 220,
			});
			this.signPad.start();
			this.signPad.selectTool("pen");
		}else{
			this.signPad.clear();
		}
		const res = await Modal.get("attendSignModal").show();
		if(res == "OK" && !this.signPad.isEmpty()){
			const signSrc = await this.signPad.getPadImage();
			return signSrc;
		}else{
			return;
		}
	}
}

/**
 * 의견 작성/삭제 요청 처리
 */
const opnReqHandler = {
	init(data = {}){
		const {
			meetingKey,
			attendeeList,
		} = data;
		this.meetingKey = meetingKey;
		this.attendeeList = attendeeList;
		const confirmUserList = attendeeList.filter(attendee => attendee.signYN == 'Y').map(attendee => attendee.userKey);
		this.confirmUserList = confirmUserList;
	},
	async reloadOpinion(){
		opnGenerator.clear();
		const opinionList = await $REPORT.Get.opinionList(this.meetingKey);
		opnGenerator.generate(opinionList);
	},
	async writeOpinion(data = {}){
		const {
			loginKey: loginKey,
			comment: comment,
		} = data;
		opnInputHandler.clearWrite();
		try{
			const res = await $REPORT.Post.opinion({
				meetingKey: this.meetingKey,
				comment: comment,
			});
			if(res.status == 200){
				//const opinion = res.data;
				//opnGenerator.generate([opinion]);
				this.reloadOpinion();
			}else{
				
			}
		}catch(err){
			console.error(err);
			Modal.info({msg: "검토의견을 등록할 수 없습니다. 다시 시도해 주세요."});
		}
	},
	async deleteOpinion(opnId){
		const res = await Modal.get("commentDelModal").show();
		if(res == "OK"){
			const delres = await $REPORT.Delete.opinion({
				meetingKey: this.meetingKey,
				opnId: opnId,
			});
			return delres.status;
		}else{
			return;
		}
	},	
	async agreeReport(data = {}){
		const {
			attendKey: attendKey,
			signSrc: signSrc,
		} = data;
		try{
			const res = await $ATT.Put.sign({
				meetingKey: this.meetingKey, 
				attendKey: attendKey, 
				signYN: 'Y',
				signSrc: signSrc
			});
			if(res.status == 200){
				location.reload();
			}else{
				
			}
		}catch(err){
			console.error(err);
			Modal.info({msg: "희의록 내용 동의에 실패했습니다. 다시 시도해 주세요."});
		}
	},
	async disagreeReport(data = {}){
		const {
			attendKey: attendKey,
		} = data;
		try{
			const res = await $ATT.Put.sign({
				meetingKey: this.meetingKey, 
				attendKey: attendKey, 
				signYN: 'N',
			});
			if(res.status == 200){
				location.reload();
			}else{
				
			}
		}catch(err){
			console.error(err);
			Modal.info({msg: "희의록 내용 동의 취소에 실패했습니다. 다시 시도해 주세요."});
		}
	},
}

export default {
	async init(data = {}){
		const {
			meetingKey, 
			attendeeList, 
		} = data;
		this.meetingKey = meetingKey;
		this.attendeeList = attendeeList;
		opnReqHandler.init({
			meetingKey: meetingKey,
			attendeeList: this.attendeeList,
		});
		opnInputHandler.init({
			loginKey: loginKey,
		});
		const attendee = this.attendeeList.find(attendee => attendee.userKey == loginKey);
		reportAgreeHandler.init({
			attendKey: attendee.attendKey,
			signYN: attendee.signYN
		});
		try{
			const opinionList = await $REPORT.Get.opinionList(meetingKey);
			opnGenerator.generate(opinionList);
		}catch(err){
			throw err;
		}
	},
	async reload(){
		await opnReqHandler.reloadOpinion();
	},
	enable(){
		opnInputHandler.enableWrite?.();
	},
	disable(){
		opnInputHandler.disableWrite?.();
	}
}