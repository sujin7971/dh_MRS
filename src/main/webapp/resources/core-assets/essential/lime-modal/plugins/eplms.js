import * as Dom from '/resources/core-assets/essential/dom_module.js';
import {Modal} from '../module/modal.js';
import {ModalBuilder, LoadingModalBuilder} from '../module/modal-builder.js';
/**
 * 
 */
class EplmsModal extends Modal {
	setMessage(title){
		const $message = this.modalElement.querySelector("[data-modal-role=message]");
		$message.innerHTML = "";
		if(title){
			const $ment = Dom.createElement("div", "commonMent");
			$ment.innerHTML = title;
			$message.appendChild($ment);
		}
		return this;
	}
}
export default {
	ModalBuilder: {
		Model: EplmsModal
	},
	ModalService: {
		deleteBuilder(options = {}) {
			const builderInstance = new ModalBuilder(options);
			const buttons = [{ label: "아니오", action: "no", color: "silver" }, { label: "삭제", action: "yes", color: "red" }];
			return builderInstance.setTitle("확인").setMessage("삭제하시겠습니까?").addButton(buttons);
		},
		errorBuilder(options){
			const {
				status,
				responseText,
				message,
				detail,
				error,
			} = options;
			const modaltitle = (message) || ((status) => {
				switch (status) {
					case 400: return "400 Bad Request";
					case 401: return "401 Unauthorized";
					case 403: return "403 Forbidden";
					case 404: return "404 Not Found";
					case 409: return "409 Conflict";
					case 422: return "422 Unprocessable Entity";
					default: return "잠시 후 다시 시도해주세요";
				}
			})(status);
			const modalmessage = detail || ((status) => {
				switch (status) {
					case 400: return "요청문에 오류가 있거나 서버가 요청을 이해하지 못했습니다. 지속적으로 발생할 경우 관리자에게 문의해주세요.";
					case 401: return "인증되지 않은 사용자의 요청입니다. 로그인 페이지로 이동합니다.";
					case 403: return "요청에 대한 권한이 없습니다.";
					case 404: return "요청한 페이지를 찾을 수 없습니다. 지속적으로 발생할 경우 관리자에게 문의해주세요.";
					case 409: return "요청한 서비스가 현재 사용 가능하지 않습니다. 다시 시도해 주세요.";
					case 422: return "요청에 대해 응답할 수 없습니다. 지속적으로 발생할 경우 관리자에게 문의해주세요.";
					default: return "서버로부터 올바른 응답을 받지 못했습니다. 지속적으로 발생할 경우 관리자에게 문의해주세요.";
				}
			})(status);
			const isExclusive = ((status) => {
				switch(status){
					case 401: return true;
					default: return  false;
				}
			})(status);
			const builderInstance = new ModalBuilder({
				...options,
				exclusive: isExclusive
			});
			const buttons = [{ label: "확인", action: "close", color: "silver" }];
			return builderInstance.setTitle(modaltitle).setMessage(modalmessage).addButton(buttons);
		}
	}
}
