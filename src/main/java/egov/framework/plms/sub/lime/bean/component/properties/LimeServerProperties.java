package egov.framework.plms.sub.lime.bean.component.properties;

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
@Profile("lime")
public class LimeServerProperties extends CustomServerProperties{
	/** HTTP PORT */
	private Integer http;
	private ServerType type;
}
