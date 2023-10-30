import {Util, Dom, ModalService} from '/resources/core-assets/essential_index.js';
import {AdminCall as $ADMIN} from '/resources/front-end-assets/js/ewp/comm/ajax_index.js';
const MODAL_TEMPLATE = `
<div class="modal" data-modal="approvalProcess">
    <div class="modalWrap">
        <div class="modal_content overflow-visible">
        	<div class="modalTitle">결재</div>
            <div class="modalBody">
                <div class="modalFormDiv">
                    <h2 class="mb-3 text-align-center" id="appTitle">승인</h2>
                    <span class="ml-auto" data-length="appComment">0/500</span>
                    <div class="row">
                        <div class="answer">
                            <textarea rows="20" name="appComment" placeholder="담당자 메모" maxlength="500"></textarea>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modalBtnDiv">
                <button type="button" data-modal-action="cancel" class="btn btn-md btn-silver" >닫 기</button>
                <button type="button" data-modal-action="process" class="btn btn-md btn-blue" >결 재</button>
            </div>
        </div>
    </div>
</div>
`
const Modal = ModalService.getModalModel();
class ApprovalProcessModal extends Modal {
	constructor(options = {}){
		super(options);
		const $textarea = this.modalElement.querySelector("textarea");
		this.$commentArea = $textarea;
		$textarea.oninput = () => {
			Util.acceptWithinLength($textarea);
			Util.displayInputLength($textarea);
		}
	}
	show(options = {}){
		const {
			id,
			request
		} = options;
		const label = ((sts) => {
			switch(sts){
				case "REQUEST": return "대기";
				case "APPROVED": return "승인";
				case "CANCELED": return "승인취소";
				case "REJECTED": return "승인불가";
				default: return null;
			}
		})(request);
		if(!label){
			throw new Error("요청한 결재단계가 올바르지 않습니다.");
		}else{
			this.processParams = {id: id, request: request};
			this.modalElement.querySelector("#appTitle").innerText = label;
			return super.show();
		}
	}
	hide(options){
		this.clearComment();
		return super.hide(options);
	}
	clearComment(){
		const $commentArea = this.$commentArea;
		$commentArea.value = "";
		Util.displayInputLength($commentArea);
	}
	getComment(){
		return this.$commentArea.value;
	}
	getProcessParams(){
		return {
			...this.processParams,
			comment: this.getComment()
		}
	}
}
export function setupApprovalProcessModal() {
	if(setupApprovalProcessModal.executed){
		return;
	}
	setupApprovalProcessModal.executed = true;
	const $modalElement = Dom.createFromString(MODAL_TEMPLATE);
	const modalInstance = new ApprovalProcessModal({
		modalElement: $modalElement,
		autoHide: false,
		autoDestroy: false,
	});
	ModalService.add(modalInstance);
	modalInstance.on({
		action: async (name) => {
			switch(name){
				case "CANCEL":{
					modalInstance.hide({
						action: name,
					});
				}
				break;
				case "PROCESS":{
					const {id, request, comment} = modalInstance.getProcessParams();
					const loading = ModalService.loadingBuilder().setMessage("결재 처리 요청중입니다...").build();
					try{
						loading.show();
						const response = await $ADMIN.Post.assignApproval({
							skdKey: id,
							status: request,
							comment: comment
						});
						if(response.status == 200){
							loading.setMessage("결재 처리중입니다...");
							setTimeout(() => {
								loading.setMessage("결재 완료. 잠시만 기다려주세요...");
								modalInstance.hide({
									action: "PROCESS",
									status: "SUCCESS"
								});
							}, 5000);
						}else{
							throw response;				
						}
					}catch(err){
						ModalService.errorBuilder(err).build().show();
					}
				}
				break;
			}
		}
	});
	return modalInstance;
}
