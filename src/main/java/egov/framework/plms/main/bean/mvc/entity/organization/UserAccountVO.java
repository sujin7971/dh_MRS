package egov.framework.plms.main.bean.mvc.entity.organization;

import java.time.LocalDateTime;

import egov.framework.plms.main.core.model.able.Convertable;
import egov.framework.plms.main.core.model.enums.user.AccountStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserAccountVO implements Convertable<UserAccountDTO> {
	private String userId;
	private String userPw;
	private String salt;
	private AccountStatus status;
	private Integer failedAttempts;
	private LocalDateTime lockoutDateTime;
	
	@Builder(builderClassName = "init", builderMethodName = "initVO")
	public UserAccountVO(UserAccountDTO dto) {
		this.userId = dto.getUserId();
		this.status = dto.getStatus();
		this.failedAttempts = dto.getFailedAttempts();
		this.lockoutDateTime = dto.getLockoutDateTime();
	}
	
	@Override
	public UserAccountDTO convert() {
		return UserAccountDTO.initDTO().vo(this).build();
	}
	
	public boolean isAccountNonExpired() {
		// 계정 유효기간 만료
		return true;
	}

	public boolean isAccountNonLocked() {
		switch(this.status) {
			case NORMAL:
				return true;
			case LOCKED:
			case DEACTIVATED:
				return false;
			case TEMPORARILY_LOCKED:
				if(this.lockoutDateTime.isAfter(LocalDateTime.now())) {
					return false;
				}else {
					return true;
				}
			default:
				return false;
		}
	}

	public boolean isCredentialsNonExpired() {
		// 비밀번호 유효기간 만료
		return true;
	}

	public boolean isEnabled() {
		if(this.status == AccountStatus.DEACTIVATED) {
			return false;
		}else {
			return true;
		}
	}	
}
