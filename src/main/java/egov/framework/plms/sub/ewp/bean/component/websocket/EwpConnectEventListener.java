package egov.framework.plms.sub.ewp.bean.component.websocket;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;

import egov.framework.plms.main.bean.component.monitoring.MeetingMonitoringManager;
import egov.framework.plms.main.bean.component.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("ewp")
/**
 * 웹소켓 연결 이벤트 감지 컴포넌트. HttpSession과 WebsocketSession은 서로 다른 객체(HttpSession내에 WebsocketSession 위치)이므로 
 * 연결된 WebsocketSession을 통해 HttpSession의 Id를 알기 위해 {@link WebSocketConfig WebSocketConfig}에서 addInterceptors를 통해 beforeHandshake메소드를 오버라이드하여 sessionId를 넘긴다.
 * @author mckim
 *
 */
public class EwpConnectEventListener implements ApplicationListener<SessionConnectEvent> {
	private final SessionManager sessionMng;
	private final EwpMeetingMessageBroker broker;
	private final MeetingMonitoringManager monitoringMng;

	@Override
	public void onApplicationEvent(SessionConnectEvent event) {
		StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
		String sessionId = sha.getSessionAttributes().get("sessionId").toString();
		log.info("SessionConnectEvent- HttpSession Id: {} / owner: {}", sessionId, sessionMng.getSessionOwner(sessionId));
		/*
		Integer meetingKey = monitoringMng.getEntriedMeeting(sessionId);
		if(meetingKey != null) {
			log.info("세션 {}의 참석중인 회의: {}", sessionId, meetingKey);
			broker.broadCastMsg(meetingKey, SocketMessage.builder(MessageType.NOTICE).actionType(ActionType.JOIN).build());
		}
		*/
	}
}