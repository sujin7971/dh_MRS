package egov.framework.plms.main.bean.component.properties.abst;

import org.springframework.boot.autoconfigure.web.ServerProperties;

import egov.framework.plms.main.core.model.enums.common.ServerType;

public abstract class CustomServerProperties extends ServerProperties {
	public abstract ServerType getType();
}
