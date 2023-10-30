package egov.framework.plms.main.core.model.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Singular;
import lombok.ToString;


/**
 * 사용자 요청에 대한 응답 결과를 담을 객체
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class ResponseMessage {
	@NonNull
	private StatusCode status;
	private String message;
	private String detail;
	private Object data;
	@Singular("error") private List<ErrorMessage> error;
	
	public static ResponseMessageBuilder builder(StatusCode status){
        return new ResponseMessageBuilder().status(status);
    }
	
	public ResponseMessage message(String message) {
		this.message = message;
		return this;
	}
	
	public ResponseMessage detail(String detail) {
		this.detail = detail;
		return this;
	}
	
	public Integer getStatus() {
		return this.status.value();
	}
	
	public boolean isSuccess() {
		if(status == ResponseMessage.StatusCode.OK) {
			return true;
		}else {
			return false;
		}
	}
	
	public List<ErrorMessage> getError(){
		if(error != null && error.size() == 0) {
			return null;
		}else {
			return error;
		}
	}
	
	public void setData(Object data) {
		this.data = data;
	}
	
	/**
	 * {@link StatusCode#OK}
	 * {@link StatusCode#BAD_REQUEST}
	 * {@link StatusCode#UNAUTHORIZED}
	 * {@link StatusCode#FORBIDDEN}
	 * {@link StatusCode#NOT_FOUND}
	 * {@link StatusCode#CONFLICT}
	 * {@link StatusCode#UNPROCESSABLE_ENTITY}
	 */
	public enum StatusCode implements StatusEnum{
		/** 성공(200)*/
		OK(200),
		/** 필수 파라미터가 전달이 안됐거나 처리 불가능한 포맷으로 요청(400)*/
		BAD_REQUEST(400),
		/** 인증되지 않은 사용자의 요청(401)*/
		UNAUTHORIZED(401),
		/** 서버가 요청 엔터티의 콘텐츠 유형을 이해하고 요청 엔터티의 구문이 정확하지만 접근이 허용되지 않은 경우(403)*/
		FORBIDDEN(403),
		/** URI가 올바르지 않거나 요청한 리소스가 존재하지 않는 경우(404)*/
		NOT_FOUND(404),
		/** 이미 점유된 리소스에 대해 사용이나 수정을 요청한 경우(409)*/
		CONFLICT(409),
		/** 서버가 요청 엔터티의 콘텐츠 유형을 이해하고 요청 엔터티의 구문이 정확하지만 포함된 명령을 처리 할 수 ​​없음(422)*/
		UNPROCESSABLE_ENTITY(422)
		;
		private final Integer value;

		StatusCode(Integer value) {
	        this.value = value;
	    }
		
		public Integer value() {
	        return value;
	    }
	}
	
	public enum MessageCode implements MessageEnum {
		;
		public enum ASSIGN implements MessageEnum{
			POST_SUCCESS("사용신청 등록 성공"),
			POST_FAIL("사용신청 등록 실패"),
			PUT_SUCCESS("사용신청 수정 성공"),
			PUT_FAIL("사용신청 수정 실패"),
			DELETE_SUCCESS("사용신청 삭제 성공"),
			DELETE_FAIL("사용신청 삭제 실패"),
			CANCEL_SUCCESS("사용신청 취소 성공"),
			CANCEL_FAIL("사용신청 취소 실패"),
			;
			private final String value;
			ASSIGN(String value) {
		        this.value = value;
		    }
			public String value() {
		        return value;
		    }
		}
		public enum SCHEDULE implements MessageEnum{
			POST_SUCCESS("회의 일정 등록 성공"),
			POST_FAIL("회의 일정 등록 실패"),
			PUT_SUCCESS("회의 일정 수정 성공"),
			PUT_FAIL("회의 일정 수정 실패"),
			DELETE_SUCCESS("파일 삭제 성공"),
			DELETE_FAIL("회의 일정 삭제 실패"),
			CANCEL_SUCCESS("회의 일정 취소 성공"),
			CANCEL_FAIL("회의 일정 취소 실패"),
			;
			private final String value;
			SCHEDULE(String value) {
		        this.value = value;
		    }
			public String value() {
		        return value;
		    }
		}
		public enum MEETING implements MessageEnum{
			PUT_SUCCESS("회의 수정 성공"),
			PUT_FAIL("회의 수정 실패"),
			DELETE_SUCCESS("회의 삭제 성공"),
			DELETE_FAIL("회의 삭제 실패"),
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
			PUT_SUCCESS("회의록 작성 성공"),
			PUT_FAIL("회의록 작성 실패"),
			DELETE_SUCCESS("회의록 삭제 성공"),
			DELETE_FAIL("회의록 삭제 실패")
			;
			private final String value;
			REPORT(String value) {
		        this.value = value;
		    }
			public String value() {
		        return value;
		    }
		}
		public enum REPORTOPN implements MessageEnum{
			POST_SUCCESS("회의록 의견 등록 성공"),
			POST_FAIL("회의록 의견 등록 실패"),
			PUT_SUCCESS("회의록 의견 수정 성공"),
			PUT_FAIL("회의록 의견 수정 실패"),
			DELETE_SUCCESS("회의록 의견 삭제 성공"),
			DELETE_FAIL("회의록 의견 삭제 실패")
			;
			private final String value;
			REPORTOPN(String value) {
		        this.value = value;
		    }
			public String value() {
		        return value;
		    }
		}
		public enum NOTICE implements MessageEnum{
			POST_SUCCESS("공지사항 등록 성공"),
			POST_FAIL("공지사항 등록 실패"),
			PUT_SUCCESS("공지사항 수정 성공"),
			PUT_FAIL("공지사항 수정 실패"),
			DELETE_SUCCESS("공지사항 삭제 성공"),
			DELETE_FAIL("공지사항 삭제 실패")
			;
			private final String value;
			NOTICE(String value) {
		        this.value = value;
		    }
			public String value() {
		        return value;
		    }
		}
		public enum FILE implements MessageEnum{
			POST_SUCCESS("파일 등록 성공"),
			POST_FAIL("파일 등록 실패"),
			PUT_SUCCESS("파일 수정 성공"),
			PUT_FAIL("파일 수정 실패"),
			DELETE_SUCCESS("파일 삭제 성공"),
			DELETE_FAIL("파일 삭제 실패")
			;
			private final String value;
			FILE(String value) {
		        this.value = value;
		    }
			public String value() {
		        return value;
		    }
		}
		public enum PASSWORD implements MessageEnum{
			INIT_SUCCESS("비밀번호 초기화 성공"),
			INIT_FAIL("비밀번호 초기화 실패"),
			PUT_SUCCESS("비밀번호 변경 성공"),
			PUT_FAIL("비밀번호 변경 실패"),
			;
			private final String value;
			PASSWORD(String value) {
		        this.value = value;
		    }
			public String value() {
		        return value;
		    }
		}
		public enum EMPLOYEE implements MessageEnum{
			POST_SUCCESS("사원 등록 성공"),
			POST_FAIL("사원 등록 실패"),
			PUT_SUCCESS("사원 수정 성공"),
			PUT_FAIL("사원 수정 실패"),
			DELETE_SUCCESS("사원 삭제 성공"),
			DELETE_FAIL("사원 삭제 실패")
			;
			private final String value;
			EMPLOYEE(String value) {
		        this.value = value;
		    }
			public String value() {
		        return value;
		    }
		}
		public enum GUEST implements MessageEnum{
			POST_SUCCESS("게스트 등록 성공"),
			POST_FAIL("게스트 등록 실패"),
			PUT_SUCCESS("게스트 수정 성공"),
			PUT_FAIL("게스트 수정 실패"),
			DELETE_SUCCESS("게스트 삭제 성공"),
			DELETE_FAIL("게스트 삭제 실패")
			;
			private final String value;
			GUEST(String value) {
		        this.value = value;
		    }
			public String value() {
		        return value;
		    }
		}
		public enum ROOM implements MessageEnum{
			POST_SUCCESS("장소 등록 성공"),
			POST_FAIL("장소 등록 실패"),
			PUT_SUCCESS("장소 수정 성공"),
			PUT_FAIL("장소 수정 실패"),
			DELETE_SUCCESS("장소 삭제 성공"),
			DELETE_FAIL("장소 삭제 실패")
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
			POST_SUCCESS("참석자 등록 성공"),
			POST_FAIL("참석자 등록 실패"),
			PUT_SUCCESS("참석자 수정 성공"),
			PUT_FAIL("참석자 수정 실패"),
			DELETE_SUCCESS("참석자 삭제 성공"),
			DELETE_FAIL("참석자 삭제 실패"),
			AGREEMENT_SUCCESS("보안서약서 동의 성공"),
			AGREEMENT_FAIL("보안서약서 동의 실패"),
			;
			private final String value;
			ATTENDEE(String value) {
		        this.value = value;
		    }
			public String value() {
		        return value;
		    }
		}
		public enum SERV_REPORT implements MessageEnum{
			POST_SUCCESS("신고 등록 성공"),
			POST_FAIL("신고 등록 실패"),
			PUT_SUCCESS("신고 수정 성공"),
			PUT_FAIL("신고 수정 실패"),
			DELETE_SUCCESS("신고 삭제 성공"),
			DELETE_FAIL("신고 삭제 실패"),
			;
			private final String value;
			SERV_REPORT(String value) {
		        this.value = value;
		    }
			public String value() {
		        return value;
		    }
		}
		public enum MAIL implements MessageEnum{
			POST_SUCCESS("메일 발송 완료"),
			POST_FAIL("메일 발송 실패")
			;
			private final String value;
			MAIL(String value) {
		        this.value = value;
		    }
			public String value() {
		        return value;
		    }
		}
		public enum GETEMP implements MessageEnum{
			POST_SUCCESS("사원 검색 성공"),
			POST_FAIL("사원 검색 실패")
			;
			private final String value;
			GETEMP(String value) {
		        this.value = value;
		    }
			public String value() {
		        return value;
		    }
		}
		public enum APPROVAL implements MessageEnum{
			POST_SUCCESS("배정신청 결재 성공"),
			POST_FAIL("배정신청 결재 실패")
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
	
	public enum DetailCode implements MessageEnum{
		;
		public enum ASSIGN implements MessageEnum{
			NOT_FOUND("사용신청 내역을 찾을 수 없습니다."),
			FORBIDDEN("해당 사용신청에 대한 권한이 없습니다."),
			UNPROCESSABLE_ENTITY("요청을 처리할 수 없습니다. 다시 시도해 주세요."),
			REQ_PARAM("요청을 처리할 데이터를 전송해 주세요."),
			BAD_ROOM_TYPE("정확한 장소 분류를 입력해 주세요"),
			BAD_ENDTIME("예약 종료시간이 현재시간보다 이전일 수 없습니다")
			;
			private String value;
			ASSIGN(String value) {
		        this.value = value;
		    }
			public String value() {
		        return value;
		    }
			public void text() {
				
			}
		}
		public enum SCHEDULE implements MessageEnum{
			NOT_FOUND("삭제되거나 존재하지 않는 회의입니다."),
			FORBIDDEN("해당 회의에 대한 권한이 없습니다."),
			UNPROCESSABLE_ENTITY("요청을 처리할 수 없습니다. 다시 시도해 주세요."),
			REQ_PARAM("요청을 처리할 데이터를 전송해 주세요.")
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
			NOT_FOUND("삭제되거나 존재하지 않는 회의입니다."),
			FORBIDDEN("해당 회의에 대한 권한이 없습니다."),
			UNPROCESSABLE_ENTITY("요청을 처리할 수 없습니다. 다시 시도해 주세요."),
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
			NOT_FOUND("해당 회의에 등록된 회의록이 존재하지 않습니다."),
			FORBIDDEN("회의록을 작성할 권한이 없습니다."),
			UNPROCESSABLE_ENTITY("요청을 처리할 수 없습니다. 다시 시도해 주세요."),
			REPORT_UPLOAD("회의록 파일 업로드에 실패했습니다. 다시 시도해 주세요.")
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
			NOT_FOUND("삭제되거나 존재하지 않는 계정입니다."),
			FORBIDDEN("해당 계정에대한 권한이 없습니다."),
			UNPROCESSABLE_ENTITY("요청을 처리할 수 없습니다. 다시 시도해 주세요."),
			;
			private final String value;
			ACCOUNT(String value) {
		        this.value = value;
		    }
			public String value() {
		        return value;
		    }
		}
		public enum PASSWORD implements MessageEnum{
			FORBIDDEN("비밀번호를 변경할 수 없는 계정입니다. 관리자에게 문의해 주세요."),
			;
			private final String value;
			PASSWORD(String value) {
		        this.value = value;
		    }
			public String value() {
		        return value;
		    }
		}
		public enum EMPLOYEE implements MessageEnum{
			NOT_FOUND("삭제되거나 존재하지 않는 사원입니다."),
			FORBIDDEN("해당 사원에대한 권한이 없습니다."),
			UNPROCESSABLE_ENTITY("요청을 처리할 수 없습니다. 다시 시도해 주세요."),
			PROTECTED_ENTITY("사원을 삭제할 수 없습니다. 참석이 예정되거나 진행중인 회의가 있습니다."),
			;
			private final String value;
			EMPLOYEE(String value) {
		        this.value = value;
		    }
			public String value() {
		        return value;
		    }
		}
		public enum GUEST implements MessageEnum{
			NOT_FOUND("삭제되거나 존재하지 않는 게스트입니다."),
			FORBIDDEN("해당 게스트에대한 권한이 없습니다."),
			UNPROCESSABLE_ENTITY("요청을 처리할 수 없습니다. 다시 시도해 주세요."),
			PROTECTED_ENTITY("게스트를 삭제할 수 없습니다. 참석이 예정되거나 진행중인 회의가 있습니다."),
			;
			private final String value;
			GUEST(String value) {
		        this.value = value;
		    }
			public String value() {
		        return value;
		    }
		}
		public enum ROOM implements MessageEnum{
			NOT_FOUND("삭제되거나 존재하지 않는 장소 입니다."),
			UNPROCESSABLE_ENTITY("요청을 처리할 수 없습니다. 다시 시도해 주세요."),
			REQ_PARAM("요청을 처리할 장소 데이터를 전송해 주세요."),
			PROTECTED_ENTITY("장소를 삭제할 수 없습니다. 예약되거나 진행중인 회의가 있습니다."),
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
			NOT_FOUND("참석자를 찾을 수 없습니다."),
			FORBIDDEN("해당 참석자에대한 권한이 없습니다."),
			UNPROCESSABLE_ENTITY("요청을 처리할 수 없습니다. 다시 시도해 주세요."),
			REQ_PARAM("요청을 처리할 참석자를 선택해 주세요.")
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
			NOT_FOUND("파일을 찾을 수 없습니다."),
			FORBIDDEN("해당 파일에대한 권한이 없습니다."),
			UNPROCESSABLE_ENTITY("요청을 처리할 수 없습니다. 다시 시도해 주세요."),
			REQ_PARAM("요청을 처리할 파일을 전송해 주세요.")
			;
			private final String value;
			FILE(String value) {
		        this.value = value;
		    }
			public String value() {
		        return value;
		    }
		}
		public enum SERV_REPORT implements MessageEnum{
			NOT_FOUND("리포트를 찾을 수 없습니다."),
			FORBIDDEN("해당 리포트대한 권한이 없습니다."),
			UNPROCESSABLE_ENTITY("요청을 처리할 수 없습니다. 다시 시도해 주세요."),
			REQ_PARAM("요청을 처리할 리포트를 지정해 주세요.")
			;
			private final String value;
			SERV_REPORT(String value) {
		        this.value = value;
		    }
			public String value() {
		        return value;
		    }
		}
		private final String value;

		DetailCode(String value) {
	        this.value = value;
	    }
		
		public String value() {
	        return value;
	    }
	}

	@Override
	public String toString() {
		return "ResponseMessage [status=" + status + ", message=" + message + ", detail=" + detail + ", data=" + data
				+ ", error=" + error + "]";
	}
	
	
}
