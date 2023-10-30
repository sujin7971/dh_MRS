package egov.framework.plms.sub.ewp.bean.component.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.Data;



/**
 * 동서발전 SSO 설정값
 * @author mckim
 * @version 1.0
 * @since 2023. 1. 11
 */
@Data
@Component
@ConfigurationProperties("config.login.sso")
@Profile("ewp")
public class SsoProperties {
	private boolean enabled;
	private String validationUrl;
	private String pageUrl;
	private String clientId;
	private String clientSecret;
	private String pictureDomain;
}
