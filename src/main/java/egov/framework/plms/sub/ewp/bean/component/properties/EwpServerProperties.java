package egov.framework.plms.sub.ewp.bean.component.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import egov.framework.plms.main.bean.component.properties.abst.CustomServerProperties;
import egov.framework.plms.main.core.model.enums.common.ServerType;
import lombok.Data;

@Data
@Component
@ConfigurationProperties("server")
@Primary
@Profile("ewp")
public class EwpServerProperties extends CustomServerProperties{
	/** HTTP PORT */
	private Integer http;
	private ServerType type;
	private PortTable portTable;
	
	@Data
	public static class PortTable {
		private Integer serviceServer;
		private Integer adminServer;
		private Integer monitorServer;
		private Integer encodingServer;
	}
}
