package egov.framework.plms.main.core.model.enums.auth;

import egov.framework.plms.main.core.model.enums.AuthCode;

/**
 * 회의에 대한 권한 ENUM
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 */
public enum ReportAuth implements AuthCode{
	/** 없음 */
	NONE("FUNC_NONE"),
	/** 기본 정보 조회 */
	READ("FUNC_READ"),
	/** 상세 보기 */
	VIEW("FUNC_VIEW"),
	/** 수정 */
	UPDATE("FUNC_UPDATE"),
	/** 의견 수렴 */
	OPEN("FUNC_OPEN"),
	/** 최종저장 */
	FINISH("FUNC_FINISH"),
	/** 삭제 */
	DELETE("FUNC_DELETE"),
	/** 의견 작성 */
	OPINION("FUNC_OPINION")
	;
	private final String code;
	ReportAuth(String code){
		this.code = code;
	}
	@Override
	public String getCode() {
		return code;
	}

}
