package egov.framework.plms.main.core.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) 
/**
 * 웹소켓에서 사용할 메시지 객체. Builder를 통한 객체 생성만 가능하며 생성시 MessageType을 반드시 Builder 생성자에 넘겨야함.
 * @author mckim
 * @param <T> : Generic 지정 안함
 * @since 1.0
 * @version 2.0
 */
public class SocketMessage<T> {
	/** 메시지 전송자(유저키) */
	private String sender;
	private String sessionId;
	/** 전송 메시지 타입 */
	private MessageType messageType;
	/** 관련 데이터 타입 */
	private ResourceType resourceType;
	/** 관련 동작 타입 */
	private ActionType actionType;
	/** 데이터 */
	private T data;
	/** 메시지 타입 */
	public enum MessageType {
		/** 알림. 클라이언트가 보유한 데이터 변경 없음 */
		NOTICE,
		/** 수정. 클라이언트가 보유한 데이터 일부 수정 */
		UPDATE,
		/** 동기화. 클라이언트가 보유한 데이터 일괄 폐기후 전달받은 데이터로 교체 */	
		SYNC,
		/** 요청. 클라이언트에게 특정한 동작 요청 */
		REQUEST,
	}
	/** 수정/동기화 메시지가 다룰 데이터 타입 */
	public enum ResourceType {
		/** 회의 진행정보(참석자 정보, 공유자 등) */
		PROGRESS,
		/** 스케줄 정보 */
		SCHEDULE,
		/** 회의 개요 정보 */
		MEETING,
		/** 보조진행자 */
		ASSISTANT,
		/** 참석자 */
		ATTENDEE,
		/** 파일 */
		FILE,
	}
	/** 알림/요청 메시지가 다룰 동작 타입 */
	public enum ActionType {
		/** 회의 참석 */
		JOIN,
		/** 회의 나감 */
		LEAVE,
		/** 페이지 이동요청 */
		FORWARD,
		/** 화면 제어 */
		CONTROL,
		/** 커서 하이라이트 */
		HIGHLIGHT,
	}
	
	/**
	 * 기존 Builder 생성자 변경. MessageType을 넘겨야만 Builder 생성자를 통해 객체 생성 가능
	 * @param messageType
	 * @return
	 */
	public static SocketMessageBuilder builder(MessageType messageType){
        return new SocketMessageBuilder().messageType(messageType);
    }
	
	/**
	 * 전송자 지정
	 * @param sender
	 */
	public void setSender(String sender) {
		this.sender = sender;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	/**
	 * 전송 데이터 지정
	 * @param data
	 */
	public void setData(T data) {
		this.data = data;
	}
}
