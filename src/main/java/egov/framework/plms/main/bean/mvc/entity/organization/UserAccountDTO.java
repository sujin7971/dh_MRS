package egov.framework.plms.main.bean.mvc.entity.organization;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import egov.framework.plms.main.core.model.able.Convertable;
import egov.framework.plms.main.core.model.enums.user.AccountStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserAccountDTO implements Convertable<UserAccountVO> {
	private String userId;
	private AccountStatus status;
	private Integer failedAttempts;
	private LocalDateTime lockoutDateTime;
	
	@Builder(builderClassName = "init", builderMethodName = "initDTO")
	public UserAccountDTO(UserAccountVO vo) {
		this.userId = vo.getUserId();
		this.status = vo.getStatus();
		this.failedAttempts = vo.getFailedAttempts();
		this.lockoutDateTime = vo.getLockoutDateTime();
	}

	@Override
	public UserAccountVO convert() {
		return UserAccountVO.initVO().dto(this).build();
	}
	
}
