package egov.framework.plms.main.core.model.login;

import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import egov.framework.plms.main.bean.mvc.entity.organization.abst.UserInfoModelVO;
import egov.framework.plms.main.core.model.enums.common.ServerType;
import egov.framework.plms.main.core.model.enums.user.LevelRole;
import egov.framework.plms.main.core.model.enums.user.LoginType;
import egov.framework.plms.main.core.model.enums.user.ManagerRole;

public abstract class AuthenticationDetails<T extends UserInfoModelVO> implements UserDetails{
	public abstract boolean hasRole(LevelRole role);
	public abstract boolean hasRole(String role);
	public abstract Collection<? extends GrantedAuthority> getPositions();
	public abstract boolean hasPosition(ManagerRole role);
	public abstract boolean hasPosition(String role);
	public abstract T getUser();
	public abstract String getUserId();
	public abstract String getDeptId();
	public abstract LoginType getLoginType();
	public abstract LocalDateTime getLoginDateTime();
}
