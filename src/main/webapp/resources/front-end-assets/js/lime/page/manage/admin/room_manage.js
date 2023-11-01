import {Dom, Util, Modal, AjaxBuilder, eventMixin, FormHelper} from '/resources/core-assets/essential_index.js';
import {SearchBar} from '/resources/front-end-assets/js/lime/comm/module_index.js';
import {RoomCall as $RM} from '/resources/front-end-assets/js/lime/ajax/ajax_index.js';

window.onload = () => {
	searchHandler.init({
		roomType: roomType,
		disableYN: disableYN,
		pageNo: pageNo
	});
	evtHandler.init();
};

const searchHandler = {
	init(data = {}){
		const {
			roomType,
			disableYN,
			pageNo = 1,
			pageCnt = 10,
		} = data;
		this.pageNo = 1;
		this.pageCnt = 10;
		const searchHelper = this.searchHelper = new FormHelper();
		searchHelper.addFormElements("#searchForm");
		this.search(searchHelper.getFormValues());
		
		searchHelper.addFormElements("#searchForm").on({
			click: (event, instance) => {
				const {
					name
				} = instance;
				switch(name){
					case "mobileReset":
					case "reset":
						this.reset();
						break;
					case "search":
						this.search(searchHelper.getFormValues());
						break;
				}
			}
		});
	},
	async search(data = {}){
		const {
			roomType: roomType,
			disableYN: disableYN,
		} = data;
		this.roomType = (roomType == "ALL_ROOM")?null:roomType;
		this.disableYN = (disableYN == "ALL")?null:disableYN;
		await this.pageMove();
	},
	reset(data){
		this.pageNo = 1;
		this.searchHelper.reset();
		this.search(data);
	},
	async pageMove(pageNo){
		this.pageNo = pageNo;
		await this.showList();
	},
	async showList(){
		await AjaxBuilder({
			request: await $RM.Get.roomList,
			param: {
				roomType: this.roomType,
				disableYN: this.disableYN,
			}
		}).success(list => {
			roomRowRenderer.generate(list);
		}).exe();
	}
}

const roomRowRenderer = {
	clear(){
		const $listBox = Util.getElement("#listBox");
		$listBox.innerHTML = "";
	},
	generate(roomList){
		this.clear();
		const $listBox = Util.getElement("#listBox");
		for(const roomData of roomList){
			const $row = roomRowBuilder();
			$row.setData(roomData);
			$listBox.appendChild($row);
			$row.on('showEditModal', async () => {
				const result = await roomModal.showEditModal($row.getData());
				if(result){
					$row.setData(result);
				}
			});
			$row.on('showDeleteModal', async () => {
				const reply = await Modal.confirm({msg: roomData.roomName + '을(를) 삭제하시겠습니가?', delMode: true});
				if(reply != "DELETE"){
					return;
				}
				AjaxBuilder({
					request: $RM.Delete.roomOne,
					param: roomData,
					exception: 'success-only',
				}).success((response) => {
					$row.remove();
				}).error((response) => {
					Modal.error({response: response});
				}).exe();
			});
		}
	},
}

const roomRowBuilder = () => {
	const $row = Util.createElement("div", "row");
	const createNoColumn = () => {
		const $column = Util.createElement("div", "item", "no");
		$row.appendChild($column);
		$row.setNo = (value) => {
			$column.innerHTML = value;
		}
	}
	// 장소구분 칼럼
	const createRoomTypeColumn = () => {
		const $column = Util.createElement("div", "item", "category");
		const $span = Util.createElement("span");
		$column.appendChild($span);
		$row.appendChild($column);
		$row.setRoomType = (value) => {
			console.log("setRoomType", value);
			if(value == "MEETING_ROOM"){
				$span.innerText = "회의실";
			}else if(value == "EDU_ROOM"){
				$span.innerText = "강의실";
			}else if(value == "HALL"){
				$span.innerText = "강당";
			}
		}
	}
	// 제목 칼럼
	const createRoomNameColumn = () => {
		const $column = Util.createElement("div", "item", "room");
		const $span = Util.createElement("span");
		$column.appendChild($span);
		$row.appendChild($column);
		$row.setRoomName = (value) => {
			$span.innerHTML = value;
		}
		$span.onclick = () => {
			$row.trigger?.('showEditModal');
		}
	}
	const createDisableYNColumn = () => {
		const $column = Util.createElement("div", "item", "rent");
		const $span = Util.createElement("span");
		$column.appendChild($span);
		$row.appendChild($column);
		$row.setDisableYN = (value) => {
			Util.removeClass($span, "rentY", "rentN");
			if(value == 'Y'){
				Util.addClass($span, "rentN");
			}else{
				Util.addClass($span, "rentY");
			}
		}
	}
	const createDisableCommentColumn = () => {
		const $column = Util.createElement("div", "item", "reason");
		const $span = Util.createElement("span");
		$column.appendChild($span);
		$row.appendChild($column);
		$row.setDisableComment = (value) => {
			$span.innerHTML = value;
		}
	}
	const createRoomNoteColumn = () => {
		const $column = Util.createElement("div", "item", "memo");
		const $span = Util.createElement("span");
		$column.appendChild($span);
		$row.appendChild($column);
		$row.setRoomNote = (value) => {
			$span.innerHTML = value;
		}
	}
	const createDeleteButtonColumn = () => {
		const $column = Util.createElement("div", "item", "delete");
		const $span = Util.createElement("span", "btn-del");
		$column.appendChild($span);
		$row.appendChild($column);
		$span.onclick = () => {
			$row.trigger?.('showDeleteModal');
		}
	}
	createNoColumn();
	createRoomTypeColumn();
	createRoomNameColumn();
	createDisableYNColumn();
	createDisableCommentColumn();
	createRoomNoteColumn();
	createDeleteButtonColumn();
	$row.setData = (data = {}) => {
		const {
			roomId = "",
			roomType,
			roomName = "",
			disableYN,
			disableComment = "",
			roomNote = "",
		} = data;
		$row.setNo?.(roomId);
		$row.setRoomType?.(roomType);
		$row.setRoomName?.(roomName);
		$row.setDisableYN?.(disableYN);
		$row.setDisableComment?.(disableComment);
		$row.setRoomNote?.(roomNote);
		
		$row.getData = () => {
			return data;
		}
	}
	Object.assign($row, eventMixin);
	return $row;
}

const evtHandler = {
	init(){
		this.setAddBtn();
	},
	setAddBtn(){
		const $btn = Util.getElement("#addRoomBtn");
		$btn.onclick = () => {
			roomModal.showAddModal();
		}
	},
}

const roomModal = {
	getModal(){
		if(!this.modal){
			this.modal = Modal.get("addRoomModal");
		}
		return this.modal;
	},
	setFormData(data = {}){
		const {
			roomId = null,
			roomType = "MEETING_ROOM",
			roomName = null,
			roomLabel = null,
			roomFloor = null,
			roomSize = null,
			disableYN = 'N',
			disableComment = null,
			roomNote = null,
		} = data;
		const $modal = this.getModal();
		const $roomIdInput = $modal.querySelector("[name=roomId]");
		$roomIdInput.value = roomId;
		const $roomTypeRadioList = $modal.querySelectorAll("[name=roomType]");
		$roomTypeRadioList.forEach($radio => {
			if($radio.value == roomType){
				$radio.checked = true;
			}else{
				$radio.checked = false;
			}
		});
		const $roomNameInput = $modal.querySelector("[name=roomName]");
		$roomNameInput.value = roomName;
		const $roomLabelInput = $modal.querySelector("[name=roomLabel]");
		$roomLabelInput.value = roomLabel;
		const $roomFloorInput = $modal.querySelector("[name=roomFloor]");
		$roomFloorInput.value = roomFloor;
		const $roomSizeInput = $modal.querySelector("[name=roomSize]");
		$roomSizeInput.value = roomSize;
		const $disableYNRadioList = $modal.querySelectorAll("[name=disableYN]");
		$disableYNRadioList.forEach($radio => {
			if($radio.value == disableYN){
				$radio.checked = true;
			}else{
				$radio.checked = false;
			}
		});
		const $disableCommentInput = $modal.querySelector("[name=disableComment]");
		$disableCommentInput.value = disableComment;
		const $roomNoteInput = $modal.querySelector("[name=roomNote]");
		$roomNoteInput.value = roomNote;
	},
	getFormData(){
		const form = this.modal.querySelector("form");
		const formData = new FormData(form);
		const roomTypeRadio = form.querySelector('input[name="roomType"]:checked');
		formData.append(roomTypeRadio.name, roomTypeRadio.value);
		const disableYNRadio = form.querySelector('input[name="disableYN"]:checked');
		formData.append(disableYNRadio.name, disableYNRadio.value);
		return formData;
	},
	getFormValues(){
		const formData = this.getFormData();
		const values = {};

		for (let [key, value] of formData) {
		    values[key] = value;
		}
		console.log("formvalues", values);
		return values;
	},
	async showAddModal(){
		const $modal = this.getModal();
		const $okBtn = $modal.getBtn("OK");
		$okBtn.setText("등록");
		this.setFormData();
		const reply = await $modal.show();
		this.getFormValues();
		if(reply != "OK"){
			return;
		}
		const result = await $RM.Post.roomOne(this.getFormData());
		if(result.status != 200){
			Modal.error({response: result});
			return;
		}
		searchHandler.search();
	},
	async showEditModal(roomData){
		const $modal = this.getModal();
		const $okBtn = $modal.getBtn("OK");
		$okBtn.setText("수정");
		this.setFormData(roomData);
		const reply = await $modal.show();
		if(reply != "OK"){
			return null;
		}
		const result = await $RM.Put.roomOne(this.getFormData());
		if(result.status != 200){
			Modal.error({response: result});
			return null;
		}
		return this.getFormValues();;
	},
}