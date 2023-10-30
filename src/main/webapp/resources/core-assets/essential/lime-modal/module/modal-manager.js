import * as Dom from '/resources/core-assets/essential/dom_module.js';
import { Modal, LoadingModal } from './modal.js';
/**
 * 페이지 내 모든 모달 객체를 관리하는 클래스입니다. 
 * ModalBuilder를 통해 생성된 Modal 클래스의 모달 객체는 ModalManager에 등록되어 사용자가 필요시 바로 해당 모달이 제공하는 기능을 사용 가능하도록 해줍니다.
 * 
 * ModalBuilder를 통해 사용자가 동적으로 생성한 모달이 아닌, 템플릿 페이지에 미리 정의된 모달은 해당 모달 최상위 노드에 data-modal속성을 추가하여, 
 * ModalManager가 자동으로 해당 모달을 관리대상에 포함하도록 할 수 있습니다. 만약 해당 모달이 Modal클래스가 기본적으로 제공하는 기능보다 더 많은 기능이 필요하다면,
 * 해당 모달을 사용할 페이지에서 Modal 클래스를 상속하는 새로운 클래스를 정의하여 ModalService를 통해 ModalManager에 등록해야합나다.
 */
export const ModalManager = ((e) => {
	e.modalCollection = new Set();
	document.addEventListener('DOMContentLoaded', () => {
		const modalList = Dom.getElementAll('[data-modal]');
		modalList.forEach($modal => {
			const id = $modal.dataset.modal;
			if (!e.get(id)) {
				const modalInstance = new Modal({
					modalElement: $modal,
					autoDestroy: false,
				});
				e.add(modalInstance);
			}
		});
	});
	return e;
})({
	add(modal) {
		if (!(modal instanceof Modal)) {
			throw new Error("Modal 인스턴스만 추가할 수 있습니다.");
		}
		this.delete(modal.getId());
		this.modalCollection.add(modal);
		modal.on("hide", () => {
			if (modal.autoDestroy === true) {
				modal.destroy();
				ModalManager.delete(modal);
			}
		});
		modal.on("show", () => {
			if (modal.getType() === "NORMAL") {
				this.getAll().forEach(entry => {
					if (entry.getType() === "LOADING") {
						entry.hide({
							action: "SYSTEM",
							source: "MODAL"
						});
					}
				});
			}
			if (modal.isExclulsive()) {
				this.getAll().forEach(entry => {
					entry.hide({
						action: "SYSTEM",
						source: "MODAL"
					});
				});
			}
		});
	},
	delete(selector) {
		if (selector instanceof Modal) {
			this.modalCollection.delete(selector);
		} else {
			const modal = this.get(selector);
			this.modalCollection.delete(modal);
		}
	},
	get(id) {
		return Array.from(this.modalCollection).find(modal => modal.id === id);
	},
	getAll() {
		return Array.from(this.modalCollection);
	},
	getLoadingModal() {
		return Array.from(this.modalCollection).find(modal => modal instanceof LoadingModal);
	},
	hasExclusive() {
		return Array.from(this.modalCollection).some(modal => modal.isExclulsive() === true);
	},
});