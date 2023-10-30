import { Dom, ModalService } from '/resources/core-assets/essential_index.js';
import { LimePad } from '/resources/core-assets/module_index.js';
const MODAL_TEMPLATE = `
<div class="modal" data-modal="sign">
    <div class="modalWrap">
        <div class="modal_content">
            <div class="modalTitle">참석자 사인</div>
            <div class="modalBody">
            	<div id="signPad" class="border overflow-visible">
            	
            	</div>
            </div>
            <div class="modalBtnDiv">
                <div class="btn btn-md btn-silver" data-modal-action="CANCEL">취 소</div>
                <div class="btn btn-md btn-blue" data-modal-action="OK">저 장</div>
            </div>
        </div>
    </div>
</div>
`
const Modal = ModalService.getModalModel();
class SignModal extends Modal {
	constructor(options = {}) {
		super(options);
		this.signPad = new LimePad({
			section: "signPad",
			width: 280,
			height: 220,
		});
		this.signPad.start();
		this.signPad.selectTool("pen");
	}
	show() {
		this.signPad.clear();
		return super.show();
	}
	async getSign() {
		if (this.signPad.isEmpty()) {
			return null;
		} else {
			const signSrc = await this.signPad.getPadImage();
			return signSrc;
		}
	}
}
export function setupSignModal() {
	if(setupSignModal.executed){
		return;
	}
	setupSignModal.executed = true;
	Dom.loadCssDynamically("/resources/core-assets/modules/limePad/limePad.css");
	const $modalElement = Dom.createFromString(MODAL_TEMPLATE);
	const modalInstance = new SignModal({
		modalElement: $modalElement,
		autoHide: false,
		autoDestroy: false,
	});
	ModalService.add(modalInstance);
	modalInstance.on({
		action: async (name) => {
			switch (name) {
				case "CANCEL": {
					modalInstance.hide({
						action: name,
					});
				}
					break;
				case "OK": {
					const signSrc = await modalInstance.getSign();
					modalInstance.hide({
						action: name,
						status: "SUCCESS",
						payload: signSrc,
					});
				}
					break;
			}
		}
	});
	return modalInstance;
}
