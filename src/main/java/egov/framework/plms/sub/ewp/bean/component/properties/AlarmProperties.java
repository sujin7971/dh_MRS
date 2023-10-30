package egov.framework.plms.sub.ewp.bean.component.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties("config.alarm")
@Profile("ewp")
public class AlarmProperties {
	private String host;
	private Mail mail;
	private Sms sms;
	private Messenger messenger;
	@Data
	public static class Mail{
		private boolean enabled;
	}
	@Data
	public static class Sms{
		private boolean enabled;
		private String callback;
		private String defaultReceiver;
	}
	@Data
	public static class Messenger{
		private boolean enabled;
		private String defaultReceiver;
	}
}
