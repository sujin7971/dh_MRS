package egov.framework.plms.main.core.model.enums.auth;

import egov.framework.plms.main.core.model.enums.AuthCode;

/**
 * 사용신청에 대한 결재 권한 ENUM
 * 
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 */
public enum AssignApprovalAuth implements AuthCode{
	/** {@code FUNC_READ 기본 정보 조회} */
	READ("FUNC_READ"),
	/** {@code FUNC_VIEW 상세 보기} */
	VIEW("FUNC_VIEW"),
	/** {@code FUNC_UPDATE 수정 } */
	UPDATE("FUNC_UPDATE"),
	/** {@code FUNC_DELETE 삭제} */
	DELETE("FUNC_DELETE"),
	/** {@code FUNC_CANCEL 취소} */
	CANCEL("FUNC_CANCEL"),
	/** {@code FUNC_APPROVAL 결재} */
	APPROVAL("FUNC_APPROVAL"),
	/** {@code FUNC_REJECT 거절} */
	REJECT("FUNC_REJECT"),
	/** {@code FUNC_NONE 없음} */
	NONE("FUNC_NONE")
	;
	private final String code;
	AssignApprovalAuth(String code){
		this.code = code;
	}
	@Override
	public String getCode() {
		return code;
	}

}
