package egov.framework.plms.main.bean.component.properties;

import java.time.LocalTime;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import egov.framework.plms.main.core.util.DateTimeUtil;
import lombok.Data;

@Data
@Component
@ConfigurationProperties("config.reserve")
public class ReserveConfigProperties {
	private String openTime;
	private String closeTime;
	private Integer intervalMinute;
	private Integer consecutiveLimit;
	private boolean autoApproval;
	public LocalTime getOpenTime() {
		return DateTimeUtil.toFormattedTime(openTime);
	}
	
	public LocalTime getCloseTime() {
		return DateTimeUtil.toFormattedTime(closeTime);
	}
}
