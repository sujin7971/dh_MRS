package egov.framework.plms.sub.ewp.bean.component.server;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import egov.framework.plms.sub.ewp.bean.component.properties.EwpServerProperties;

/**
 * 서버 호스트와 포트번호를 제공하는 컴포넌트
 * 
 * @author mckim
 * @version 1.0
 * @since 2023. 2. 10
 */
@Component
@Profile("ewp")
public class EwpServerListener implements ApplicationListener<WebServerInitializedEvent> {
	@Autowired
	private EwpServerProperties serverProperties;
	private int serverPort;

	@Override
	public void onApplicationEvent(WebServerInitializedEvent event) {
		this.serverPort = event.getWebServer().getPort();
	}

	public int getPort() {
		return this.serverPort;
	}

	public String getHost() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			return null;
		}
	}

	public String getServiceDomain() {
		return "https://" + getHost() + ":" + serverProperties.getPortTable().getServiceServer();
	}
}
