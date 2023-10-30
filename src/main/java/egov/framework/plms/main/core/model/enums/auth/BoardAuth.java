package egov.framework.plms.main.core.model.enums.auth;

import egov.framework.plms.main.core.model.enums.AuthCode;

/**
 * 사용자 리포트에 대한 권한 ENUM. 현재 사용자 리포트 기능을 사용하지 않아 불필요.
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 */
@Deprecated
public enum BoardAuth implements AuthCode{
	/** 기본 정보 조회 */
	READ("FUNC_READ"),
	/** 상세 보기 */
	VIEW("FUNC_VIEW"),
	/** 수정 */
	UPDATE("FUNC_UPDATE"),
	/** 처리 */
	PROCESS("FUNC_PROCESS"),
	/** 삭제 */
	DELETE("FUNC_DELETE"),
	;
	private final String code;
	
	BoardAuth(String code){
		this.code = code;
	}
	
	@Override
	public String getCode() {
		return code;
	}

}
