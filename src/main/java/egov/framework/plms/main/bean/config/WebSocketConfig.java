package egov.framework.plms.main.bean.config;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.fasterxml.jackson.databind.ObjectMapper;

import egov.framework.plms.main.bean.component.monitoring.MeetingMonitoringManager;
import egov.framework.plms.main.core.xss.HTMLCharacterEscapes;
import egov.framework.plms.sub.ewp.bean.mvc.controller.common.EwpWebsocketController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * 웹소켓 설정 컴포넌트
 * 
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 * @see {@link EwpWebsocketController}
 */
@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	private final MeetingMonitoringManager monitoringMng;
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/subscribe");
        config.setApplicationDestinationPrefixes("/topic");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOrigins("*").addInterceptors(getInterceptot()).withSockJS();
    }
    
    /**
     * 세션 연결시 동작할 인터셉터 메서드
     * @return
     */
    private HandshakeInterceptor getInterceptot() {
        return new HandshakeInterceptor(){

            @Override
            public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
            	// 웹소켓 연결 세션 생성시 현재 사용자의 세션 ID값을 넣어준다
            	if (request instanceof ServletServerHttpRequest) {
                    ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
                    HttpSession session = servletRequest.getServletRequest().getSession();
                    attributes.put("sessionId", session.getId());
                }
                return true; //TODO
            }

            @Override
            public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
            }
            
        };
    }
    
    /**
     * WebSocket XSS 방어
     */
    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
    	// TODO Auto-generated method stub
    	messageConverters.add(escapingConverter());
    	return WebSocketMessageBrokerConfigurer.super.configureMessageConverters(messageConverters);
    }
	
	private MessageConverter escapingConverter() {
	    ObjectMapper objectMapper = new ObjectMapper();
	    objectMapper.getFactory().setCharacterEscapes(new HTMLCharacterEscapes());
	
	    MappingJackson2MessageConverter escapingConverter =
	            new MappingJackson2MessageConverter();
	    escapingConverter.setObjectMapper(objectMapper);
	
	    return escapingConverter;
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		// TODO Auto-generated method stub
		registration.interceptors(new TopicSubscriptionInterceptor());
	}
	
	public class TopicSubscriptionInterceptor implements ChannelInterceptor{

		@Override
		public Message<?> preSend(Message<?> message, MessageChannel channel) {
			StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
	        if(StompCommand.SUBSCRIBE.equals(accessor.getCommand())){

	            MessageHeaders headers = message.getHeaders();
	            LinkedMultiValueMap<String, String> map= headers.get(StompHeaderAccessor.NATIVE_HEADERS, LinkedMultiValueMap.class);
	            
	            String destination = (String) map.get("destination").get(0);
	            Integer meetingKey = extractMeetingKey(destination);
	            String sessionId = accessor.getSessionAttributes().get("sessionId").toString();
	            if(meetingKey != null && monitoringMng.isMonitoring(meetingKey) && sessionId != null) {
	            	monitoringMng.addEntry(meetingKey, sessionId);
	            }
	        }
			return ChannelInterceptor.super.preSend(message, channel);
		}
		
		private Integer extractMeetingKey(String destination) {
			String[] parts = destination.split("/");
			for(String part: parts) {
				if(part.matches("\\d+")) {
					return Integer.parseInt(part);
				}
			}
			return null;
		}
		
	}
	
	
}