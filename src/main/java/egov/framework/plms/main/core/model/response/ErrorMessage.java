package egov.framework.plms.main.core.model.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;


/**
 * 사용자 응답요청에 에러에 대한 상세 정보를 담아 전달할 객체
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 * @see {@link ResponseMessage}
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class ErrorMessage {
	@NonNull
	private ErrorCode status;
	private String message;
	private Object data;
	
	public static ErrorMessageBuilder builder(ErrorCode status){
        return new ErrorMessageBuilder().status(status);
    }
	
	public Integer getStatus() {
		return this.status.value();
	}
	
	public enum ErrorCode implements StatusEnum {
		INVALID_VALUE(10),
		EMPTY_VALUE(20),
		CONFLICT_VALUE(30),
		NOT_UPDATED(40)
		;
		private final Integer value;

		ErrorCode(Integer value) {
	        this.value = value;
	    }
		
		public Integer value() {
	        return value;
	    }
	}
	
	public enum MessageCode implements MessageEnum{
		;
		public enum SCHEDULE implements MessageEnum{
			UNPROCESSABLE_ENTITY("요청을 처리할 수 없습니다. 다시 시도해 주세요."),
			EMPTY_BEGIN("시작 시간을 선택해주세요."),
			EMPTY_FINISH("종료 시간을 선택해주세요."),
			INVALID_TIME("시작시간과 종료시간이 동일합니다"),
			INVALID_BEGIN("현재 시간 이전에 예약할 수 없습니다"),
			INVALID_FINISH("종료시간이 시작시간보다 빠를 수 없습니다"),
			INVALID_FORMAT("올바르지 않은 날짜형식입니다."),
			CONFLICT_TIME("요청하신 예약시간과 겹치는 등록된 회의가 존재합니다.<br> 예약시간을 다시 선택해 주세요."),
			CONSECUTIVE_REQUEST("연속적으로 5영업일을 초과하여 배정신청을 할 수 없습니다.<br> 다른 배정신청일자를 선택해 주세요.")
			;
			private String value;
			SCHEDULE(String value) {
		        this.value = value;
		    }
			public String value() {
		        return value;
		    }
			public void text() {
				
			}
		}
		public enum MEETING implements MessageEnum{
			UNPROCESSABLE_ENTITY("요청을 처리할 수 없습니다. 다시 시도해 주세요."),
			EMPTY_TITLE("회의제목을 입력해 주세요."),
			EMPTY_HOST("회의 진행자를 선택해 주세요."),
			EMPTY_PUBLICLVL("보안등급을 선택해 주세요."),
			EMPTY_ADMINPASS("관리자 공개여부를 선택해 주세요."),
			INVALID_TITLE("회의제목 입력값 허용길이를 초과하였습니다."),
			INVALID_CONTENTS("회의내용 입력값 허용길이를 초과하였습니다."),
			;
			private final String value;
			MEETING(String value) {
		        this.value = value;
		    }
			public String value() {
		        return value;
		    }
		}
		public enum REPORT implements MessageEnum{
			UNPROCESSABLE_ENTITY("요청을 처리할 수 없습니다. 다시 시도해 주세요."),
			;
			private final String value;
			REPORT(String value) {
		        this.value = value;
		    }
			public String value() {
		        return value;
		    }
		}
		public enum ACCOUNT implements MessageEnum{
			UNPROCESSABLE_ENTITY("요청을 처리할 수 없습니다. 다시 시도해 주세요."),
			EMPTY_PASSWORD("비밀번호를 입력해 주세요."),
			INVALID_PASSWORD("비밀번호는 문자, 특수문자(?!@#$%), 숫자를 모두 포함한 8~16자 사이의 값이어야 합니다. 다시 입력해 주세요."),
			BAD_PASWORD("비밀번호가 일치하지 않습니다. 다시 입력해 주세요."),
			;
			private final String value;
			ACCOUNT(String value) {
		        this.value = value;
		    }
			public String value() {
		        return value;
		    }
		}
		public enum EMPLOYEE implements MessageEnum{
			UNPROCESSABLE_ENTITY("요청을 처리할 수 없습니다. 다시 시도해 주세요."),
			EMPTY_DEPT("사원 부서를 선택해 주세요."),
			EMPTY_TITLE("사원 직위를 선택해 주세요."),
			EMPTY_NAME("사원 이름을 입력해 주세요."),
			EMPTY_ID("사원 번호를 입력해 주세요."),
			EMPTY_MAIL("사원 이메일 주소를 입력해 주세요."),
			EMPTY_PHONE("사원 연락처를 입력해 주세요."),
			INVALID_NAME("사원 이름은 공백, 특수문자, 숫자를 제외한 2~10자리 사이의 문자 값이어야 합니다."),
			INVALID_ID("사원 번호는 4자리의 숫자 값이어야 합니다."),
			INVALID_MAIL("메일 주소 형식이 올바르지 않습니다."),
			INVALID_PHONE("연락처 형식이 올바르지 않습니다."),
			CONFLICT_ID("이미 사용중인 사원번호 입니다."),
			;
			private final String value;
			EMPLOYEE(String value) {
		        this.value = value;
		    }
			public String value() {
		        return value;
		    }
		}
		public enum ROOM implements MessageEnum{
			UNPROCESSABLE_ENTITY("요청을 처리할 수 없습니다. 다시 시도해 주세요."),
			EMPTY_NAME("장소 이름을 입력해 주세요."),
			EMPTY_LOCATION("장소의 위치를 입력해 주세요."),
			EMPTY_SIZE("장소의 크기를 입력해 주세요."),
			EMPTY_PUT_LIST("수정을 요청한 장소의 값이 없습니다. 다시 시도해 주세요."),
			INVALID_NAME("입력한 장소의 이름의 길이가 너무 큽니다. 25자 이내로 입력해 주세요."),
			INVALID_LOCATION("입력한 위치 안내의 길이가 너무 큽니다. 25자 이내로 입력해 주세요."),
			INVALID_MAXINUM_SIZE("입력한 장소의 규모가 너무 큽니다. 100 이내의 값을 입력해 주세요."),
			INVALID_MINIMUM_SIZE("장소의 규모는 1보다 큰 값이어야 합니다. 2 이상의 값을 입력해 주세요."),
			INVALID_TOTAL_SIZE("장소의 규모의 총합이 100보다 큽니다. 총합이 100을 넘지 않도록 장소 규모를 입력해 주세요."),
			CONFLICT_NAME("이미 사용중인 장소명 입니다."),
			;
			private final String value;
			ROOM(String value) {
		        this.value = value;
		    }
			public String value() {
		        return value;
		    }
		}
		public enum ATTENDEE implements MessageEnum{
			UNPROCESSABLE_ENTITY("요청을 처리할 수 없습니다. 다시 시도해 주세요."),
			INVALID_ATTENDTYPE("올바르지 않은 참석자유형입니다."),
			CONFLICT_ATTENDEE("은(는) 참석자로 등록된 회의가 있습니다."),
			CONFLICT_HOST("은(는) 회의 진행자로 등록된 회의가 있습니다."),
			;
			private final String value;
			ATTENDEE(String value) {
		        this.value = value;
		    }
			public String value() {
		        return value;
		    }
		}
		public enum FILE implements MessageEnum{
			UNPROCESSABLE_ENTITY("요청을 처리할 수 없습니다. 다시 시도해 주세요."),
			INVALID_FORMAT("지원하지 않는 파일 형식입니다."),
			;
			private final String value;
			FILE(String value) {
		        this.value = value;
		    }
			public String value() {
		        return value;
		    }
		}
		public enum APPROVAL implements MessageEnum{
			UNPROCESSABLE_ENTITY("요청을 처리할 수 없습니다. 다시 시도해 주세요."),
			CONFLICT_APPROVAL("이미 처리할 결재 요청이 있습니다."),
			;
			private final String value;
			APPROVAL(String value) {
		        this.value = value;
		    }
			public String value() {
		        return value;
		    }
		}
		private final String value;

		MessageCode(String value) {
	        this.value = value;
	    }
		
		public String value() {
	        return value;
	    }
	}
}
