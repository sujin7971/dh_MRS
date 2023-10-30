package egov.framework.plms.main.core.model.enums.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode implements ErrorCodeEnum {
	NOT_FOUND(100, "자원을 찾을 수 없습니다"),
	DISABLED(101, "비활성화된 자원입니다"),
	UNPROCESSABLE_ENTITY(102, "요청을 처리할 수 없습니다. 다시 시도해 주세요."),
	SYSTEM_ERROR(500, "시스템 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.")
	;
	private final int code;
    private final String message;
    @Getter
    @AllArgsConstructor
    public enum MEETING_SCHEDULE implements ErrorCodeEnum {
    	NOT_FOUND(100, "회의 일정 정보를 찾을 수 없습니다."),
    	
    	EMPTY_BEGIN(110, "시작 시간을 선택해주세요."),
    	EMPTY_FINISH(111, "종료 시간을 선택해주세요."),
		
    	INVALID_TIME_FORMAT(120, "올바르지 않은 날짜형식입니다."),
		INVALID_SAME_START_END_TIME(121, "시작시간과 종료시간이 동일합니다"),
		INVALID_PAST_START_TIME(122, "현재 시간 이전에 예약할 수 없습니다"),
		INVALID_EARLIER_END_TIME(123, "종료시간이 시작시간보다 빠를 수 없습니다"),
		
		CONFLICT_EXISTING_MEETING(130, "요청하신 예약시간과 겹치는 등록된 회의가 존재합니다."),
		CONFLICT_CONSECUTIVE_DAYS(131, "연속적으로 5영업일을 초과하여 배정신청을 할 수 없습니다."),
		
		POST_FAILED(140, "회의 일정 생성에 실패했습니다."),
        DELETE_FAILED(141, "회의 일정 삭제에 실패했습니다."),
        UPDATE_FAILED(142, "회의 일정 업데이트에 실패했습니다."),
        
    	READ_NO_AUTHORITY(250, "회의 일정을 조회할 권한이 없습니다."),
    	VIEW_NO_AUTHORITY(251, "회의 일정을 조회할 권한이 없습니다."),
    	
    	;
    	private final int code;
        private final String message;
    }
    
    @Getter
    @AllArgsConstructor
    public enum MEETING_INFO implements ErrorCodeEnum {
    	NOT_FOUND(200, "회의 설정 정보를 찾을 수 없습니다."),
    	
    	EMPTY_TITLE(210, "회의제목을 입력해 주세요."),
		EMPTY_HOST(211, "회의 진행자를 선택해 주세요."),
		
		INVALID_TITLE(220, "회의제목 입력값 허용길이를 초과하였습니다."),
		INVALID_CONTENTS(221, "회의내용 입력값 허용길이를 초과하였습니다."),
        
        POST_FAILED(240, "회의 설정 생성에 실패했습니다."),
        DELETE_FAILED(241, "회의 설정 삭제에 실패했습니다."),
        UPDATE_FAILED(242, "회의 설정 업데이트에 실패했습니다."),
    	
    	READ_NO_AUTHORITY(250, "회의 설정을 조회할 권한이 없습니다."),
    	VIEW_NO_AUTHORITY(251, "회의 설정을 조회할 권한이 없습니다."),
    	
    	;
    	private final int code;
        private final String message;
    }
    
    @Getter
    @AllArgsConstructor
    public enum MEETING_ASSIGN implements ErrorCodeEnum {
    	NOT_FOUND(300, "회의 정보를 찾을 수 없습니다."),
    	
        POST_FAILED(340, "회의 생성에 실패했습니다."),
        DELETE_FAILED(341, "회의 삭제에 실패했습니다."),
        UPDATE_FAILED(342, "회의 업데이트에 실패했습니다."),
    	
    	READ_NO_AUTHORITY(350, "회의를 조회할 권한이 없습니다."),
    	VIEW_NO_AUTHORITY(351, "회의를 조회할 권한이 없습니다."),
    	
    	;
    	private final int code;
        private final String message;
    }
    
    @Getter
    @AllArgsConstructor
    public enum MEETING_APPROVAL implements ErrorCodeEnum {
    	POST_FAILED(440, "회의 결재에 실패했습니다."),
    	
    	APPROVAL_NO_AUTHORITY(450, "회의를 결재할 권한이 없습니다."),
    	CHANGE_POLICY_NO_AUTHORITY(451, "사업소 결재 정책을 변경할 권한이 없습니다."),
    	CHANGE_DETAILS_NO_AUTHORITY(451, "회의를 수정할 권한이 없습니다."),
    	;
    	private final int code;
        private final String message;
    }
    
    @Getter
    @AllArgsConstructor
    public enum MEETING_ATTENDEE implements ErrorCodeEnum {
    	NOT_FOUND(500, "참석자 정보를 찾을 수 없습니다."),
		
		POST_FAILED(540, "참석자 등록에 실패했습니다."),
        DELETE_FAILED(541, "참석자 삭제에 실패했습니다."),
        UPDATE_FAILED(542, "참석자 업데이트에 실패했습니다."),
        
        READ_NO_AUTHORITY(550, "참석자 정보를 조회할 권한이 없습니다."),
        SIGN_NO_AUTHORITY(551, "참석자 사인을 갱신할 권한이 없습니다."),
        ATTENDANCE_NO_AUTHORITY(552, "참석자 참석 여부를 갱신할 권한이 없습니다."),
        AGREEMENT_NO_AUTHORITY(553, "이 회의의 참석자가 아니므로 보안서약서에 동의할 수 없습니다."),
        
        AGREEMENT_UNAVAILABLE(560, "이 회의는 보안서약서 동의가 필요하지 않습니다.")
    	;
    	private final int code;
        private final String message;
    }
    
    @Getter
    @AllArgsConstructor
    public enum FILE implements ErrorCodeEnum {
    	NOT_FOUND(400, "파일 정보를 찾을 수 없습니다."),
		
		POST_FAILED(440, "파일 등록에 실패했습니다."),
        DELETE_FAILED(441, "파일 삭제에 실패했습니다."),
        UPDATE_FAILED(442, "파일 업데이트에 실패했습니다."),
        
        READ_NO_AUTHORITY(450, "파일 정보를 조회할 권한이 없습니다."),
        PREVIEW_NO_AUTHORITY(451, "파일을 미리보기할 권한이 없습니다."),
        DOWLOAD_NO_AUTHORITY(452, "파일을 다운로드할 권한이 없습니다."),
        UPDATE_NO_AUTHORITY(453, "파일을 수정할 권한이 없습니다."),
        DELETE_NO_AUTHORITY(454, "파일을 삭제할 권한이 없습니다."),
        
        READ_UNAVAILABLE(460, "조회할 수 없는 파일입니다."),
        PREVIEW_UNAVAILABLE(461, "미리보기할 수 없는 파일입니다"),
        DOWLOAD_UNAVAILABLE(462, "다운로드할 수 없는 파일입니다."),
    	;
    	private final int code;
        private final String message;
    }
    
    @Getter
    @AllArgsConstructor
    public enum ROOM implements ErrorCodeEnum {
    	NOT_FOUND(500, "삭제되거나 등록되지 않은 장소입니다."),
    	DISABLED(501, "대여불가능한 장소입니다."),
    	NOT_PERMITTED(502, "대여가능부서가 아닙니다."),
    	
    	EMPTY_TYPE(510, "장소 유형이 필요합니다. 장소 유형을 선택해주세요."),
    	EMPTY_NAME(511, "장소 이름이 필요합니다. 장소 이름을 입력해주세요."),
    	EMPTY_DISABLE(512, "대여 가능 여부가 명시되지 않았습니다. 대여 가능 여부를 선택해주세요."),
		EMPTY_OFFICE(513, "장소를 등록할 사업소가 선택되지 않았습니다. 소속 사업소를 선택해주세요."),
		
    	INVALID_TYPE(520, "장소 유형 선택이 올바르지 않습니다. 올바른 장소 유형을 선택해주세요."),
		INVALID_NAME(521, "장소 이름이 너무 깁니다. 장소 이름은 최대 길이 제한 내에서 입력해주세요."),
    	INVALID_DISABLE(522, "대여 가능 여부 선택이 올바르지 않습니다. 올바른 대여 가능 여부를 선택해주세요."),
    	INVALID_DISABLE_COMMENT(523, "대여 불가 사유가 너무 깁니다. 대여 불가 사유는 최대 길이 제한 내에서 입력해주세요."),
    	INVALID_NOTE(524, "기타 정보 입력이 너무 깁니다. 기타 정보는 최대 길이 제한 내에서 입력해주세요."),
    	INVALID_OFFICE(525, "선택된 사업소는 등록 가능한 사업소가 아닙니다. 유효한 사업소를 선택해주세요."),
    	
    	CONFLICT_NAME(530, "이미 사용중인 장소명 입니다."),
    	
    	INFO_POST_FAILED(540, "장소 등록에 실패했습니다."),
    	INFO_DELETE_FAILED(541, "장소 삭제에 실패했습니다."),
    	INFO_UPDATE_FAILED(542, "장소 업데이트에 실패했습니다."),
    	
    	PERMISSION_POST_FAILED(540, "장소 대여가능부서 등록에 실패했습니다."),
    	PERMISSION_DELETE_FAILED(541, "장소 대여가능부서 삭제에 실패했습니다.")
    	;
    	private final int code;
        private final String message;
    }
    
    @Getter
    @AllArgsConstructor
    public enum USER implements ErrorCodeEnum {
    	NOT_FOUND(600, "사용자 정보를 찾을 수 없습니다."),
    	;
    	private final int code;
        private final String message;
    }
    
    @Getter
    @AllArgsConstructor
    public enum DOMAIN_ROSTER implements ErrorCodeEnum {
    	NOT_FOUND(700, "사용자 정보를 찾을 수 없습니다."),
    	
    	POST_FAILED(740, "관리자 명단 등록에 실패했습니다."),
    	DELETE_FAILED(741, "관리자 명단 삭제에 실패했습니다."),
    	;
    	private final int code;
        private final String message;
    }
    
    @Getter
    @AllArgsConstructor
    public enum MANAGER_ROSTER implements ErrorCodeEnum {
    	NOT_FOUND(800, "사용자 정보를 찾을 수 없습니다."),
    	
    	POST_FAILED(840, "관리자 명단 등록에 실패했습니다."),
    	DELETE_FAILED(841, "관리자 명단 삭제에 실패했습니다."),
    	;
    	private final int code;
        private final String message;
    }
}
