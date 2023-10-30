package egov.framework.plms.sub.ewp.bean.mvc.controller.common;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import egov.framework.plms.main.bean.component.server.ProxyHelper;
import egov.framework.plms.main.core.model.response.SocketMessage;
import egov.framework.plms.main.core.model.response.SocketMessage.MessageType;
import egov.framework.plms.main.core.model.response.SocketMessage.ResourceType;
import egov.framework.plms.sub.ewp.bean.component.properties.EwpServerProperties;
import egov.framework.plms.sub.ewp.bean.component.server.EwpServerListener;
import egov.framework.plms.sub.ewp.bean.component.websocket.EwpMeetingMessageBroker;
import egov.framework.plms.sub.ewp.bean.mvc.entity.file.MeetingFileInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.service.file.EwpMeetingFileInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/ewp")
@RequiredArgsConstructor
@Profile("ewp")
public class EwpProxyRestController {
	private final EwpServerProperties serverProperties;
	private final EwpServerListener listener;
	private final ProxyHelper proxy;
	private final EwpMeetingMessageBroker msgBroker; //특정 Broker로 메세지를 전달;
	
	private final EwpMeetingFileInfoService fileServ;

	@ResponseBody
	@GetMapping("/proxy/test/response")
	public String responseTest() {
		return "Hello";
	}
	
	@ResponseBody
	@GetMapping("/proxy/test")
	public String communicateTest() {
		log.info("service port: {}", serverProperties.getPortTable().getServiceServer());
		String host = String.format("https://%s:%d", listener.getHost(), serverProperties.getPortTable().getServiceServer());
		return proxy.call(host, "/api/ewp/proxy/test/response", HttpMethod.GET, String.class);
	}
	
	@GetMapping("/proxy/file/{fileKey}/update")
	public boolean fileUpdated(@PathVariable Integer fileKey) {
		MeetingFileInfoVO fileVO = fileServ.getFileOne(fileKey);
		if(fileVO != null) {
			msgBroker.broadCastMsg(fileVO.getMeetingKey(), SocketMessage.builder(MessageType.UPDATE).resourceType(ResourceType.FILE).data(fileVO.getFileKey()).build());
			return true;
		}else {
			return false;
		}
	}
}
