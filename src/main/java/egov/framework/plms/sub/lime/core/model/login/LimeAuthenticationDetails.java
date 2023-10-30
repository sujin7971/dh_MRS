package egov.framework.plms.sub.lime.core.model.login;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import egov.framework.plms.main.bean.mvc.entity.organization.UserInfoVO;
import egov.framework.plms.main.core.model.enums.user.LevelRole;
import egov.framework.plms.main.core.model.enums.user.LoginType;
import egov.framework.plms.main.core.model.enums.user.ManagerRole;
import egov.framework.plms.main.core.model.login.AuthenticationDetails;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * Spring Seucirty의 인증객체 {@link Authentication}이 소유할 인증정보 객체
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 */
@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class LimeAuthenticationDetails extends AuthenticationDetails<UserInfoVO> {
	private UserInfoVO user;
	private LocalDateTime loginDateTime;
	private Collection<? extends GrantedAuthority> authorities;
	private Collection<? extends GrantedAuthority> positions;
	private LoginType loginType;
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
	}
	@Override
	public Collection<? extends GrantedAuthority> getPositions() {
        return positions;
	}
	@Override
	public boolean hasRole(LevelRole role) {
		Iterator<? extends GrantedAuthority> authlist_it= authorities.stream().filter(auth -> auth.getAuthority().contains("ROLE_")).iterator();
		while(authlist_it.hasNext()) {
			GrantedAuthority authority= authlist_it.next();
			if(authority.getAuthority().equals(role.getCode())) {
				return true;
			}
		}
		return false;
	}
	@Override
	public boolean hasRole(String role) {
		Iterator<? extends GrantedAuthority> authlist_it= authorities.stream().filter(auth -> auth.getAuthority().contains("ROLE_")).iterator();
		while(authlist_it.hasNext()) {
			GrantedAuthority authority= authlist_it.next();
			if(authority.getAuthority().equals(role)) {
				return true;
			}
		}
		return false;
	}
	@Override
	public boolean hasPosition(ManagerRole role) {
		Iterator<? extends GrantedAuthority> authlist_it= positions.stream().filter(auth -> auth.getAuthority().contains("MNG_")).iterator();
		while(authlist_it.hasNext()) {
			GrantedAuthority authority= authlist_it.next();
			if(authority.getAuthority().equals(role.getCode())) {
				return true;
			}
		}
		return false;
	}
	@Override
	public boolean hasPosition(String role) {
		if(positions == null) {
			return false;
		}
		Iterator<? extends GrantedAuthority> authlist_it= positions.stream().filter(auth -> auth.getAuthority().contains("MNG_")).iterator();
		while(authlist_it.hasNext()) {
			GrantedAuthority authority= authlist_it.next();
			if(authority.getAuthority().equals(role)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return user.getUserName();
	}
	
	@Override
	public String getDeptId() {
		return user.getDeptId();
	}

	@Override
	public boolean isAccountNonExpired() {
		// 계정 유효기간 만료
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// 비밀번호 유효기간 만료
		return true;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}
	@Override
	public String getUserId() {
		return user.getUserId();
	}
	
	public String getOfficeCode() {
		return user.getOfficeCode();
	}
}
