import { default as eventMixin } from '/resources/core-assets/essential/mixin/eventMixin.js';
import * as Dom from '/resources/core-assets/essential/dom_module.js';
/**
 * 모달의 실제 구현을 담당하는 클래스입니다.
 * 모달을 생성하고, 콘텐츠를 업데이트하며, 이벤트를 처리하고, 모달을 표시하거나 숨기는 등의 기능을 가집니다.
 * 특정 모달이 더 많은 기능을 필요로 한다면, Modal 클래스를 상속하는 새로운 클래스르 정의하여 ModalService를 통해 ModalManager에 등록해야합니다.
 */
export class Modal {
	static TEMPLATE = `
	<div class="modal" data-modal="09bcbd7d">
		<div class="modalWrap">
			<div class="modal_content">
				<div class="modalTitle" data-modal-role="title"></div>
				<div class="modalBody" data-modal-role="message"></div>
				<div class="modalBtnDiv" data-modal-role="btn-container"></div>
			</div>
		</div>
	</div>
	`
	constructor(builder) {
		const {
			modalElement,
			exclusive = false,
			autoHide = true,
			autoDestroy = true,
		} = builder;
		this.update({
			modalElement: modalElement,
			exclusive: exclusive,
			autoHide: autoHide,
			autoDestroy: autoDestroy,
		});
		Dom.getElement("body").appendChild(this.modalElement);
	}
	update(builder){
		const {
			modalElement = this.modalElement,
			exclusive = this.exclusive,
			autoHide = this.autoHide,
			autoDestroy = this.autoDestroy,
		} = builder;
		this.exclusive = exclusive;
		this.autoHide = autoHide;
		this.autoDestroy = autoDestroy;
		this.modalElement = modalElement;
		this.id = modalElement.dataset.modal;
		this.setButton();
	}
	//해당 모달을 화면에 표시합니다. Promise객체를 반환하여 모달 hide시, 호출자가 비동기적인 응답을 받아 추가적인 처리를 가능하도록해줍니다.
	show() {
		if(this.resolve){
			return;
		}
		this.payload = undefined;
		this.modalElement.style.display = "block";
		this.trigger("show");
		return new Promise((resolve, reject) => {
			this.resolve = resolve;
			this.reject = reject;
		});
	}
	/*
	 * 해당 모달을 화면에서 숨깁니다. 자동삭제가 설정된경우, 해당 모달의 Dom Element를 삭제하고 ModalManager의 명단에서 인스턴스를 제거합니다.
	 * 숨김처리시 호출자에게 promise를 통한 응답을 반환하며, setPayload를 통해 따로 값이 설정되지 않은 경우 모달 hide를 유발한 버튼의 data-modal-action 속성값을 반환합니다.
	 */
	async hide(options = {}) {
		const {
			action = "AUTO",
			source = "USER",
			status = "SUCESS",
			payload
		} = options;
		this.modalElement.style.display = "none";
		this.trigger("hide");
		const response = {
			action: action,
			status: status,
			source: source,
			timestamp: Date.now(),
			payload: payload
		}
		if(payload){
			response.payload = payload;
		}
		this.resolve?.(response);
		this.resolve = null;
		this.reject = null;
	}
	// 해당 모달의 제목을 설정합니다.
	setTitle(title){
		const $title = this.modalElement.querySelector(".modalTitle");
		$title && ($title.innerHTML = title);
		return this;
	}
	setMessage(message) {
		const $message = this.modalElement.querySelector(".modalBody");
		$message && ($message.innerHTML = message);
		return this;
	}
	addButton(buttonList){
		const $btnContainer = this.modalElement.querySelector(".modalBtnDiv");
		if(!$btnContainer){
			return this;
		}
		for(const button of buttonList.flat()){
			const {
				label = "닫기",
				action = "close",
				color = "blue"
			} = button;
			const classList = ["btn", "btn-md"];
			switch (color) {
				case "red":
					classList.push("btn-red");
					break;
				case "yellow":
					classList.push("btn-yellow");
					break;
				case "blue":
					classList.push("btn-blue");
					break;
				case "silver":
				default:
					classList.push("btn-silver");
					break;
			}
			const $btn = Dom.createElement("button", ...classList);
			$btn.type = "button";
			$btn.innerText = label;
			$btn.dataset.modalAction = action;
			$btnContainer.appendChild($btn);
		}
		this.setButton();
		return this;
	}
	setButton(){
		const btnList = this.modalElement.querySelectorAll("[data-modal-action]");
		this.buttons = btnList;
		btnList.forEach($button => {
			const action = $button.dataset.modalAction?.toUpperCase() || "NONE";
			const modalClose = $button.dataset.modalClose || true;
			const clickEvent = () => {
				this.trigger("action", action, $button);
				if (this.autoHide === true && modalClose === true) {
					this.hide({
						soure: "AUTO",
						action: action,
					});
				}
			}
			$button.onclick = clickEvent;
		});
	}
	// 해당 모달이 독점 모달인지 확인합니다
	isExclulsive() {
		return this.exclusive;
	}
	// 해당 모달의 식별 키를 반환합니다
	getId() {
		return this.id;
	}
	// 해당 모달의 Element를 반환합니다.
	getElement() {
		return this.modalElement;
	}
	displayButton(...actions){
		this.buttons.forEach($button => {
			if(actions.length == 0 || actions.includes($button.name)){
				$button.style.display = "";				
			}
		});
	}
	concealButton(...actions){
		this.buttons.forEach($button => {
			if(actions.length == 0 || actions.includes($button.name)){
				$button.style.display = "none";				
			}
		});
	}
	enableButton(...actions){
		this.buttons.forEach($button => {
			if(actions.length == 0 || actions.includes($button.name)){
				$button.disabled = false;				
			}
		});
	}
	disableButton(...actions){
		this.buttons.forEach($button => {
			if(actions.length == 0 || actions.includes($button.name)){
				$button.disabled = true;			
			}
		});
	}
	destroy(){
		this.modalElement?.remove?.();
		this.off?.();
	}
	getType(){
		return "NORMAL";
	}
}
Object.assign(Modal.prototype, eventMixin);

export class LoadingModal extends Modal{
	static instance;
	static TEMPLATE = `
	<div class="modal" data-modal="09bcbd7d">
		<div class="modalWrap">
			<div class="modal_content flex-row align-items-center">
				<div class="modalIcon overflow-hidden p-2" data-modal-role="icon-container"></div>
				<div class="modalBody" data-modal-role="message"></div>
			</div>
		</div>
	</div>
	`
	constructor(builder) {
		if(LoadingModal.instance){
			LoadingModal.instance.update(builder);
			return LoadingModal.instance;
		}
		super(builder);
		LoadingModal.instance = this;
	}
	setIcon(icon = "spinner"){
		const ICON_TEMPLATE = ((icon) => {
			switch(icon){
				case "spinner": return '<div class="spinner-border text-primary"></div>'
				case "grow": return '<div class="spinner-grow text-primary"></div>'
				case "roller": return `<div class="lds-roller"><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div></div>`;
				case "ring": 
				default: return `<div class="lds-ring"><div></div><div></div><div></div><div></div></div>`;
			}
		})(icon);
		const $iconContainer = this.modalElement.querySelector("[data-modal-role=icon-container]");
		const $icon = Dom.createFromString(ICON_TEMPLATE);
		$iconContainer.appendChild($icon);
	}
	process(asyncFunction) {
    	this.asyncFunction = asyncFunction;
    	return this;
  	}
	getType(){
		return "LOADING";
	}
}
