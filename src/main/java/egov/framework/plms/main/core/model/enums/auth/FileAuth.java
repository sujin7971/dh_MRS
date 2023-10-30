package egov.framework.plms.main.core.model.enums.auth;

import egov.framework.plms.main.core.model.enums.AuthCode;

/**
 * 파일에 대한 권한 ENUM
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 */
public enum FileAuth implements AuthCode{
	/** 기본 정보 조회 */
	READ("FUNC_READ"),
	/** 상세 보기 */
	VIEW("FUNC_VIEW"),
	/** 수정 */
	UPDATE("FUNC_UPDATE"),
	/** 다운로드 */
	DOWN("FUNC_DOWN"),
	/** 삭제 */
	DELETE("FUNC_DELETE"),
	;
	private final String code;

	FileAuth(String code){
		this.code = code;
	}
	
	@Override
	public String getCode() {
		return code;
	}

}
