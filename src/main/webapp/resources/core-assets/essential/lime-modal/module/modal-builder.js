import * as Util from '/resources/core-assets/essential/util_module.js';
import * as Dom from '/resources/core-assets/essential/dom_module.js';
import {Modal, LoadingModal} from './modal.js';
import {ModalManager} from './modal-manager.js';

/**
 * Modal 클래스 기반의 모달 인스턴스를 쉽게 생성할 수 있도록 돕는 빌더 패턴 클래스입니다.
 * ModalService는 기본적으로 ModalBuilder의 빌더 패턴을 통해서만 사용자가 Modal 인스턴스를 생성하도록 제한합니다.
 */
export class ModalBuilder {
	static Model = Modal;
	//생성자를 통해 해당 모달이 기본적으로 사용할 Element 틀을 생성합니다.
	constructor(options = {}) {
		const {
			exclusive = false,
			autoHide = true,
			autoDestroy = true,
		} = options;
		this.builderContext = {};
		this.builderContext.exclusive = exclusive;
		this.builderContext.autoHide = autoHide;
		this.builderContext.autoDestroy = autoDestroy;
		this.builderContext.buttons = [];
		const $modal = Dom.createFromString(ModalBuilder.Model.TEMPLATE);
		this.builderContext.modalElement = $modal;
		$modal.dataset.modal = Util.getUUID(8);
		return this;
	}
	// 모달의 제목을 설정합니다
	setTitle(title) {
		if (!this.builderContext) {
			throw new Error("속성을 설정하기 전에 'builder' 메서드를 호출해야 합니다.");
		}
		this.builderContext.title = title;
		return this;
	}
	// 모달의 본문을 설정합니다
	setMessage(message) {
		if (!this.builderContext) {
			throw new Error("속성을 설정하기 전에 'builder' 메서드를 호출해야 합니다.");
		}
		this.builderContext.message = message;
		return this;
	}
	// 모달에 버튼을 추가합니다
	addButton(...buttonList) {
		if (!this.builderContext) {
			throw new Error("속성을 설정하기 전에 'builder' 메서드를 호출해야 합니다.");
		}
		this.builderContext.buttons.push(...buttonList.flat());
		return this;
	}
	// 빌더 패턴을 통해 설정한 값을 기반으로 새로운 Modal 클래스의 모달 객체를 생성하여 반환합니다. 해당 모달의 Element는 자동으로 body 태그에 추가됩니다.
	build() {
		if (!this.builderContext) {
			throw new Error("먼저 'builder' 메서드를 호출한 다음에 'build'를 호출해야 합니다.");
		}
		const {
			title = "",
			message = "",
			buttons = []
		} = this.builderContext;
		const modalInstance = new ModalBuilder.Model(this.builderContext);
		modalInstance.setTitle(title);
		modalInstance.setMessage(message);
		modalInstance.addButton(buttons);
		ModalManager.add(modalInstance);
		return modalInstance;
	}
}
export class LoadingModalBuilder extends ModalBuilder {
	static Model = LoadingModal;
	constructor(options = {}) {
		super({
			exclusive: false,
			autoHide: true,
			autoRemove: true,
		});
		const $modal = Dom.createFromString(LoadingModalBuilder.Model.TEMPLATE);
		this.builderContext.modalElement = $modal;
		$modal.dataset.modal = Util.getUUID(8);
		return this;
	}
	setTitle(){
		return this;
	}
	setIcon(icon){
		if (!this.builderContext) {
			throw new Error("속성을 설정하기 전에 'builder' 메서드를 호출해야 합니다.");
		}
		this.builderContext.icon = icon;
		return this;
	}
	addButton(){
		return this;
	}
	build() {
		if (!this.builderContext) {
			throw new Error("Must call 'builder' before 'build'");
		}
		const {
			message = "",
			icon = "spinner",
		} = this.builderContext;
		Dom.getElement("body").appendChild(this.builderContext.modalElement);
		const modalInstance = new LoadingModalBuilder.Model(this.builderContext);
		//modalInstance.setTitle("");
		modalInstance.setMessage(message);
		modalInstance.setIcon(icon);
		//modalInstance.addButton([]);
		ModalManager.add(modalInstance);
		return modalInstance;
	}
}