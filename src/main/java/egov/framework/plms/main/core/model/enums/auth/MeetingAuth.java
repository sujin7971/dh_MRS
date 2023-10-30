package egov.framework.plms.main.core.model.enums.auth;

import egov.framework.plms.main.core.model.enums.AuthCode;

/**
 * 회의에 대한 권한 ENUM
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 */
public enum MeetingAuth implements AuthCode{
	/** 없음 */
	NONE("FUNC_NONE"),
	/** 기본 정보 조회 */
	READ("FUNC_READ"),
	/** 상세 보기 */
	VIEW("FUNC_VIEW"),
	/** 수정 */
	UPDATE("FUNC_UPDATE"),
	/** 삭제 */
	DELETE("FUNC_DELETE"),
	/** 취소 */
	CANCEL("FUNC_CANCEL"),
	/** 자료 업로드 */
	UPLOAD("FUNC_UPLOAD"),
	/** 참석자 초대 */
	INVITE("FUNC_INVITE"),
	/** 회의 참석 */
	ATTEND("FUNC_ATTEND"),
	/** 판서본 저장 */
	COPY("FUNC_COPY"),
	/** 회의록 작성 */
	REPORT("FUNC_REPORT"),
	/** 회의 연장 */
	EXTEND("FUNC_EXTEND"),
	/** 출석 체크 */
	CHECK("FUNC_CHECK"),
	/** 사인 입력 */
	SIGN("FUNC_SIGN"),
	/** 사진 기록 */
	PHOTO("FUNC_PHOTO"),
	/** 음성 기록 */
	VOICE("FUNC_VOICE"),
	/** 회의 종료 */
	FINISH("FUNC_FINISH"),
	/** 승인 */
	APPROVAL("FUNC_APPROVAL")
	;
	private final String code;
	MeetingAuth(String code){
		this.code = code;
	}
	@Override
	public String getCode() {
		return code;
	}

}
