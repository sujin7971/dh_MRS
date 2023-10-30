package egov.framework.plms.main.bean.mvc.entity.log;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import egov.framework.plms.main.core.model.able.Convertable;
import egov.framework.plms.main.core.model.enums.common.LoginResult;
import egov.framework.plms.main.core.model.enums.common.ServerType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginHistoryDTO implements Convertable<LoginHistoryVO>{
	private Integer loginId;
	private String userId;
	private String ipAddress;
	private ServerType serverType;
	private String userAgent;
	private LocalDateTime loginDateTime;
	private LoginResult loginResult;
	
	@Builder(builderClassName = "init", builderMethodName = "initDTO")
	public LoginHistoryDTO(LoginHistoryVO vo) {
		this.loginId = vo.getLoginId();
		this.userId = vo.getUserId();
		this.ipAddress = vo.getIpAddress();
		this.serverType = vo.getServerType();
		this.userAgent = vo.getUserAgent();
		this.loginDateTime = vo.getLoginDateTime();
		this.loginResult = vo.getLoginResult();
	}
	
	@Override
	public LoginHistoryVO convert() {
		// TODO Auto-generated method stub
		return LoginHistoryVO.initVO().dto(this).build();
	}
}
