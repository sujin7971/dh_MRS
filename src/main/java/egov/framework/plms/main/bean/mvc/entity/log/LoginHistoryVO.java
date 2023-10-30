package egov.framework.plms.main.bean.mvc.entity.log;

import java.time.LocalDateTime;

import egov.framework.plms.main.core.model.able.Convertable;
import egov.framework.plms.main.core.model.enums.common.LoginResult;
import egov.framework.plms.main.core.model.enums.common.ServerType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class LoginHistoryVO implements Convertable<LoginHistoryDTO> {
	private Integer loginId;
	private String userId;
	private String ipAddress;
	private ServerType serverType;
	private String userAgent;
	private LocalDateTime loginDateTime;
	private LoginResult loginResult;
	
	@Builder(builderClassName = "init", builderMethodName = "initVO")
	public LoginHistoryVO(LoginHistoryDTO dto) {
		this.loginId = dto.getLoginId();
		this.userId = dto.getUserId();
		this.ipAddress = dto.getIpAddress();
		this.serverType = dto.getServerType();
		this.userAgent = dto.getUserAgent();
		this.loginDateTime = dto.getLoginDateTime();
		this.loginResult = dto.getLoginResult();
	}
	
	@Override
	public LoginHistoryDTO convert() {
		// TODO Auto-generated method stub
		return LoginHistoryDTO.initDTO().vo(this).build();
	}
}
