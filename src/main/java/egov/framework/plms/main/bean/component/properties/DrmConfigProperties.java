package egov.framework.plms.main.bean.component.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties("config.drm")
public class DrmConfigProperties {
	private boolean enabled;
	private String keyPath;
	private String key;
	private String trans_path;
}
