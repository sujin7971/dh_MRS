import * as Util from '/resources/core-assets/essential/util_module.js';
import * as Dom from '/resources/core-assets/essential/dom_module.js';
import {ModalBuilder, LoadingModalBuilder} from './module/modal-builder.js';
import {ModalManager} from './module/modal-manager.js';
import {Modal} from './module/modal.js';
import {default as Eplms} from './plugins/eplms.js';

/**
 * 모달 팝업 관리자 모듈
 * 이 모듈은 웹 페이지에서 모달 팝업을 관리하기 위한 기능을 제공합니다.
 * 
 * 이 모듈은 다음의 기능을 제공합니다:
 * - 모달 팝업 생성, 표시, 숨김, 제거
 * - 모달 팝업에 대한 사용자 이벤트 처리
 * - 모달 팝업의 콘텐츠 업데이트
 * - 모달 팝업 컬렉션 관리
 * 
 * 이 모듈에는 ModalManager, Modal, ModalBuilder, ModalService 네 가지 주요 클래스가 있으며, 
 * 사용을 위해 직접적으로 접근 가능한 클래스는 Modal과 ModalService 두가지입니다.
 * @author mckim
 * @version 3.0
 * @since 2023. 7. 24.
 * @see 이 모듈 또는 클래스의 코드는 함부로 수정하거나 변경해서는 안 됩니다. 만약 이 모듈이나 클래스에 기능을 추가하거나 수정이 필요하다면, 개발1팀에 문의하시기 바랍니다.
 */
/**
 * Modal 모듈이 제공하는 여러 기능을 사용자가 사용가능하도록 통합하여 관리하는 서비스 Object입니다.
 * ModalService를 통해 모달 생성/등록/조회/삭제둥 모달 관련 작업을 수행할 수 있습니다.
 */
const ModalService = {
	// 기본 모달 빌더 메소드, 사용자 지정 옵션을 가진 모달을 생성합니다.
	builder(options = {}) {
		const builderInstance = new ModalBuilder(options);
		return builderInstance;
	},
	// 알림 메시지용 모달 빌더 메소드, '확인' 버튼이 있는 알림 메시지 모달을 생성합니다.
	infoBuilder(options = {}) {
		const builderInstance = new ModalBuilder(options);
		const buttons = [{ label: "확인", action: "CLOSE", color: "silver" }];
		return builderInstance.setTitle("알림").addButton(buttons);
	},
	// '예'와 '아니오' 버튼이 있는 확인 메시지 모달 빌더 메소드
	confirmBuilder(options = {}) {
		const builderInstance = new ModalBuilder(options);
		const buttons = [{ label: "아니오", action: "NO", color: "silver" }, { label: "예", action: "YES", color: "blue" }];
		return builderInstance.setTitle("확인").addButton(buttons);
	},
	// '아니오'와 '삭제' 버튼이 있는 삭제 확인 메시지 모달 빌더 메소드
	deleteBuilder(options = {}) {
		const builderInstance = new ModalBuilder(options);
		const buttons = [{ label: "아니오", action: "NO", color: "silver" }, { label: "삭제", action: "YES", color: "yellow" }];
		return builderInstance.setTitle("확인").setMessage("삭제하시겠습니까?").addButton(buttons);
	},
	// 서버의 REST API 요청 응답객체 ApiPayload에 대응하여 오류시 사용할 에러 모달 빌더 메소드
	errorBuilder(options = {}){
		const {
			status,
			payload = {},
		} = options;
		const title = (payload.message) || ((status) => {
			switch(status){
				case 400: return "400 Bad Request";
				case 401: return "401 Unauthorized";
				case 403: return "403 Forbidden";
				case 404: return "404 Not Found";
				case 409: return "409 Conflict";
				case 422: return "422 Unprocessable Entity";
				default: return "잠시 후 다시 시도해주세요";
			}
		})(status);
		const message = payload.errors?.pop().message || ((status) => {
			switch(status){
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
		const buttons = [{ label: "확인", action: "CLOSE", color: "silver" }];
		return builderInstance.setTitle(title).setMessage(message).addButton(buttons);
	},
	// 로딩중임을 표시하는 모달 빌더 메소드
	loadingBuilder(options = {}){
		const builderInstance = new LoadingModalBuilder(options);
		return builderInstance.setMessage("잠시만 기다려주세요...");
	},
	// 특정 ID를 가진 모달을 반환하는 메소드
	get(id) {
		return ModalManager.get(id);
	},
	getLoadingModal(){
		return ModalManager.getLoadingModal();
	},
	// 특정 모달 인스턴스를 제거하는 메소드
	delete(modalInstance) {
		if (modalInstance instanceof Modal) {
			ModalManager.delete(modalInstance);
		}
	},
	// 특정 모달 인스턴스를 추가하는 메소드
	add(modalInstance) {
		if (modalInstance instanceof Modal) {
			ModalManager.add(modalInstance);
		}
	},
	getModalModel(){
		return Modal;
	}
}
export default ModalService;
document.addEventListener('DOMContentLoaded', () => {
	//Dom.loadCssDynamically("/resources/core-assets/essential/lime-modal/lime-modal.css");
	if(Eplms){
		Eplms.ModalService && Util.replaceProperties(ModalService, Eplms.ModalService);
		Eplms.ModalBuilder && Util.replaceProperties(ModalBuilder, Eplms.ModalBuilder);
		Eplms.Modal && Util.replaceProperties(Modal, Eplms.Modal);
	}
});