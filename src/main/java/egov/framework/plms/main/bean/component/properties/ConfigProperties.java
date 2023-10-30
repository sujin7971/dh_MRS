package egov.framework.plms.main.bean.component.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties("config")
public class ConfigProperties {
}
