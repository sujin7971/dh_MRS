/**
 * 참석자 input을 관리할 모듈
 * 
 * @author mckim
 */
import {Util, eventMixin} from '/resources/core-assets/essential_index.js';
import {UserSearchModal} from '/resources/front-end-assets/js/lime/comm/module_index.js';

const getAteendeeClassList = () => {
	const $facilitatorBtnList = document.querySelectorAll("button[data-btn=facilitatorBtn]");
	const $facilitatorDiv = document.querySelector("#facilitatorDiv");
	const $attendeeBtnList = document.querySelectorAll("button[data-btn=attendeeBtn]");
	const $attendeeDiv = document.querySelector("#attendeeDiv");
	const $observerBtnList = document.querySelectorAll("button[data-btn=observerBtn]");
	const $observerDiv = document.querySelector("#observerDiv");
	return [
		{role: "FACILITATOR", selectbox: "radio", btnList: $facilitatorBtnList, nodeDiv: $facilitatorDiv}, 
		{role: "ATTENDEE", selectbox: "checkbox", btnList: $attendeeBtnList, nodeDiv: $attendeeDiv}, 
		{role: "OBSERVER", selectbox: "checkbox", btnList: $observerBtnList, nodeDiv: $observerDiv}
	];
}

export default {
	__proto__: eventMixin,
	async init(data = {}){
		const {
			officeCode = "1000",
			maxSize = 100,
			editable = true,
		} = data;
		this.editable = editable;
		this.setBtn();
		this.newList = [];
		this.oldList = [];
		if(editable){
			UserSearchModal.init({
				officeCode: officeCode,
			});
		}
	},
	// 회의 진행자 선택 여부 확인
	validation(){
		const host = this.newList.filter(user => user.attendRole == "FACILITATOR");
		return (host.length > 0)?true:false;
	},
	// 설정 업데이트
	update(data = {}){
		const {
			editable = this.editable
		} = data;
		this.editable = editable;
	},
	// 버튼 이벤트 등록
	setBtn(){
		// 선택됨/선택불가 표시를 위한 필터
		const getFilteredList = (list, filter) => {
			const selected = [];
			const disabled = [];
			list.forEach(user => {
				if(user.attendRole == filter){
					selected.push(user.userId);
				}else{
					disabled.push(user.userId);
				}
			});
			return {
				selected: selected,
				disabled: disabled,
			}
		}
		const attendeeClassList = getAteendeeClassList();
		attendeeClassList.forEach(cls => {
			cls.btnList.forEach(btn => {
				btn.onclick = () => {
					const {
						selected,
						disabled,
					} = getFilteredList(this.newList, cls.role);
					this.show({
						selectType: cls.selectbox,
						checked: selected,
						disabled: disabled,
						role: cls.role,
					});
				}
			});
		});
	},
	// 참석자 리스트 설정 및 표시
	initAttendeeList(attendeeList){
		const attendeeClassList = getAteendeeClassList();
		this.oldList = attendeeList;
		this.newList = [...this.oldList];
		this.displayAttendeeList();
	},
	setAttendeeList(attendeeList){
		attendeeList.forEach(node => {
			if(node.attendRole == "FACILITATOR"){
				this.deleteRole("FACILITATOR");
			}
			this.newList.push(node);
		});
		this.displayAttendeeList();
	},
	clearAttendeeList(){
		this.newList = [];
		this.displayAttendeeList();
	},
	// 참석자 리스트 뷰 생성
	displayAttendeeList(){
		const attendeeList = this.newList;
		const classMap = {};
		getAteendeeClassList().forEach(cls => {
			if(cls.nodeDiv){
				classMap[cls.role] = {
						nodeDiv: cls.nodeDiv,
						isDeletable: (cls.role == "FACILITATOR")?false:true
				}
				cls.nodeDiv.innerHTML = "";
			}
		});
		// 참석자 DOM 생성
		const createUserElem = (attendee, isDeletable) => {
			const $node = Util.createElement("div", "attend");
			
			const $name = Util.createElement("span");
			$name.innerHTML = attendee.nameplate;
			$node.appendChild($name);
			if(this.editable && isDeletable){
				const $delBtn = Util.createElement("div", "btn-del");
				$delBtn.setAttribute("title", "삭제");
				$node.appendChild($delBtn);
				$delBtn.onclick = () => {
					const size = this.deleteUser(attendee.userId);
					this.trigger("change", this.getSelectedSize());
					$node.remove();
				}
			}
			return $node;
		}
		// 전달받은 참석자 리스트의 모든 DOM 생성하여 참석자 컨테이너 노드에 추가
		attendeeList.forEach(attendee => {
			const cls = classMap[attendee.attendRole];
			const $node = createUserElem(attendee, cls.isDeletable);
			if(attendee.attendRole == "FACILITATOR"){
				cls.nodeDiv.innerHTML = "";
			}
			cls.nodeDiv.appendChild($node);
			this.trigger("change", this.getSelectedSize());
		});
	},
	// 참석자 선택 모달창 팝업
	async show(options = {}){
		const {
			selectType = "checkbox",
			checked,
			disabled,
			role,
		} = options;
		UserSearchModal.update({
			selectType: selectType,
			checked: checked,
			disabled: disabled,
		});
		UserSearchModal.unbind("change");
		UserSearchModal.on("change", (selectedNodes) => {
			selectedNodes.forEach(node => {
				node.attendRole = role;
			});
			this.setAttendeeList(selectedNodes);
		});
		UserSearchModal.show();
	},
	// 현재 선택된 모든 참석자의 개수 반환
	getSelectedSize(){
		return this.newList.length;
	},
	// 선택된 유저 참석자 명단에서 제거
	deleteUser(userId){
		this.newList = this.newList.filter(user => {
			return user.userId != userId;
		});
		return this.newList.length;
	},
	// 선택된 유저 참석자 명단에서 제거
	deleteRole(role){
		this.newList = this.newList.filter(user => {
			return user.attendRole != role;
		});
		return this.newList.length;
	},
	// 추가/수정/삭제된 참석자 명단 요청
	getChangeResult(){
		const insertList = [];
		const updateList = [];
		this.newList.forEach(newData => {
			let list = insertList;
			for(const [index, oldData] of this.oldList.entries()){
				if(oldData.userId == newData.userId){
					this.oldList.splice(index, 1);
					if(oldData.attendRole != newData.attendRole){
						//역할 변경됨
						list = updateList;
						break;
					}else{
						list = null;
						break;
					}
				}
			}
			list?.push(newData);
		});
		const deleteList = this.oldList;
		return {
			insertList: insertList,
			updateList: updateList,
			deleteList: deleteList
		}
	}
}