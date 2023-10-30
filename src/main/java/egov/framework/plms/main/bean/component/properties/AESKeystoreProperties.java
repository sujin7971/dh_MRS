package egov.framework.plms.main.bean.component.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties("secure.aes")
@Profile("lime")
public class AESKeystoreProperties {
	private String iv;
	private Keystore keystore;
	
	@Data
	public static class Keystore{
		private String name;
		private String pwd;
		private String alias;
		private String keypwd;
	}
}
