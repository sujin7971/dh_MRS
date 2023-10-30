package egov.framework.plms.sub.ewp.bean.component.websocket;

import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import egov.framework.plms.main.core.model.response.SocketMessage;
import egov.framework.plms.main.core.model.response.SocketMessage.MessageType;
import egov.framework.plms.main.core.model.response.SocketMessage.ResourceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("ewp")
public class EwpSocketMessageHelper {
	private final SimpMessagingTemplate template; //특정 subscriber에게 메세지를 전달
	
	public void sendMsgToMeeting(Integer meetingKey, MessageType messageType, ResourceType resourceType, Object data) {
		template.convertAndSend("/subscribe/room/"+meetingKey, SocketMessage.builder(messageType).resourceType(resourceType).data(data).build());
	}
}
