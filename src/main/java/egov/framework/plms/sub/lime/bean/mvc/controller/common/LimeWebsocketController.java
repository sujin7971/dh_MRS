package egov.framework.plms.sub.lime.bean.mvc.controller.common;

import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import egov.framework.plms.main.bean.component.monitoring.MeetingMonitoringManager;
import egov.framework.plms.main.core.model.login.AuthenticationDetails;
import egov.framework.plms.main.core.model.response.SocketMessage;
import egov.framework.plms.main.core.model.response.SocketMessage.ActionType;
import egov.framework.plms.main.core.model.response.SocketMessage.MessageType;
import egov.framework.plms.sub.lime.bean.component.websocket.LimeMeetingMessageBroker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * 웹소켓을 통한 클라이언트 와의 통신을 위한 컨트롤러. 한번 요청에 전송 가능한 메시지는 약 70KB 이내. 초과하는 경우 자동으로 연결 끊김
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@Profile("lime")
public class LimeWebsocketController {
	private final LimeMeetingMessageBroker broker;
	private final MeetingMonitoringManager monitoringMng;
	@MessageMapping("/join/{meetingId}")
	@SendTo("/subscribe/room/{meetingId}")
	public void joinRoom(Authentication authentication, @DestinationVariable Integer meetingId, SimpMessageHeaderAccessor headerAccessor) {
		AuthenticationDetails userDetails = (AuthenticationDetails) authentication.getDetails();
		String loginKey = userDetails.getUserId();
		String sessionId = headerAccessor.getSessionAttributes().get("sessionId").toString();
		monitoringMng.addEntry(meetingId, sessionId);
		log.info("참석한 사용자의 세션 id: {}", sessionId);
		broker.broadCastMsg(meetingId, SocketMessage.builder(MessageType.NOTICE).sender(loginKey).sessionId(sessionId).actionType(ActionType.JOIN).build());
	}
	
	@Deprecated
	@MessageMapping("/leave/{meetingId}")
	@SendTo("/subscribe/room/{meetingId}")
	public void leaveRoom(Authentication authentication, @DestinationVariable Integer meetingId, @Payload SocketMessage msg) {
		AuthenticationDetails userDetails = (AuthenticationDetails) authentication.getDetails();
		String loginKey = userDetails.getUserId();
		msg.setSender(loginKey);
		broker.broadCastMsg(meetingId, msg);
	}
	
	@MessageMapping("/update/{meetingId}")
	@SendTo("/subscribe/room/{meetingId}")
	public void updateRoom(Authentication authentication, @DestinationVariable Integer meetingId, @Payload SocketMessage msg, SimpMessageHeaderAccessor headerAccessor) {
		AuthenticationDetails userDetails = (AuthenticationDetails) authentication.getDetails();
		String loginKey = userDetails.getUserId();
		String sessionId = headerAccessor.getSessionAttributes().get("sessionId").toString();
		msg.setSender(loginKey);
		msg.setSessionId(sessionId);
		broker.broadCastMsg(meetingId, msg);
	}
	
	@Deprecated
	@MessageMapping("/stream/{meetingId}")
	@SendTo("/subscribe/room/{meetingId}/stream/{streamer}")
	public void stream(Authentication authentication, @DestinationVariable Integer meetingId, @Payload SocketMessage msg) {
		AuthenticationDetails userDetails = (AuthenticationDetails) authentication.getDetails();
		String loginKey = userDetails.getUserId();
		msg.setSender(loginKey);
		broker.streamMsg(meetingId, msg);
	}
	
	@MessageMapping("/guest/join")
	@SendTo("/subscribe/guest/{guestKey}")
	public void guestJoinWaitingRoom(Authentication authentication, @Payload SocketMessage msg) {
		AuthenticationDetails userDetails = (AuthenticationDetails) authentication.getDetails();
		String loginKey = userDetails.getUserId();
	}
	
	@MessageMapping("/guest/leave")
	@SendTo("/subscribe/guest/{guestKey}")
	public void guestLeaveWaitingRoom(Authentication authentication, @Payload SocketMessage msg) {
		AuthenticationDetails userDetails = (AuthenticationDetails) authentication.getDetails();
		String loginKey = userDetails.getUserId();
	}
}
