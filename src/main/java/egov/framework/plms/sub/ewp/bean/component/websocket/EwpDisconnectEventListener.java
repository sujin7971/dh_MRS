package egov.framework.plms.sub.ewp.bean.component.websocket;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import egov.framework.plms.main.bean.component.monitoring.MeetingMonitoringManager;
import egov.framework.plms.main.bean.component.session.SessionManager;
import egov.framework.plms.main.core.model.response.SocketMessage;
import egov.framework.plms.main.core.model.response.SocketMessage.ActionType;
import egov.framework.plms.main.core.model.response.SocketMessage.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("ewp")
/**
 * 웹소켓 연결 해제 이벤트 감지 컴포넌트. HttpSession과 WebsocketSession은 서로 다른 객체(HttpSession내에 WebsocketSession 위치)이므로 
 * 연결 해제된 WebsocketSession을 통해 HttpSession의 Id를 알기 위해 {@link WebSocketConfig WebSocketConfig}에서 addInterceptors를 통해 beforeHandshake메소드를 오버라이드하여 sessionId를 넘긴다.
 * @author mckim
 *
 */
public class EwpDisconnectEventListener implements ApplicationListener<SessionDisconnectEvent> {
	private final SessionManager sessionMng;
	private final EwpMeetingMessageBroker broker;
	private final MeetingMonitoringManager monitoringMng;
	
	@Override
	public void onApplicationEvent(SessionDisconnectEvent event) {
		StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
		String sessionId = sha.getSessionAttributes().get("sessionId").toString();
		log.info("SessionDisconnectEvent- WebSocketSession Id:{} / HttpSession Id: {} / owner: {}",event.getSessionId(), sessionId, sessionMng.getSessionOwner(sessionId));
		log.info("event: {}", event.toString());
		log.info("sha: {}", sha.toString());
		Integer meetingKey = monitoringMng.removeEntry(sessionId);
		if(meetingKey != null) {
    		broker.broadCastMsg(meetingKey, SocketMessage.builder(MessageType.NOTICE).actionType(ActionType.LEAVE).build());
    	}
	}
}