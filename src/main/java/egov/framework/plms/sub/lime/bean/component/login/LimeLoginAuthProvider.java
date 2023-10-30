package egov.framework.plms.sub.lime.bean.component.login;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import egov.framework.plms.main.bean.component.properties.LoginProperties;
import egov.framework.plms.main.bean.component.properties.LoginProperties.TempAccount;
import egov.framework.plms.main.bean.component.session.SessionManager;
import egov.framework.plms.main.bean.mvc.entity.admin.AdminRosterVO;
import egov.framework.plms.main.bean.mvc.entity.organization.UserAccountVO;
import egov.framework.plms.main.bean.mvc.entity.organization.UserInfoVO;
import egov.framework.plms.main.bean.mvc.service.admin.AdminRosterService;
import egov.framework.plms.main.core.model.enums.user.DomainRole;
import egov.framework.plms.main.core.model.enums.user.LoginType;
import egov.framework.plms.main.core.model.login.CustomWebAuthenticationDetails;
import egov.framework.plms.main.core.util.secure.SHA256Util;
import egov.framework.plms.sub.lime.bean.mvc.service.organization.LimeUserAccountService;
import egov.framework.plms.sub.lime.bean.mvc.service.organization.LimeUserInfoService;
import egov.framework.plms.sub.lime.core.model.login.LimeAuthenticationDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("lime")
public class LimeLoginAuthProvider implements AuthenticationProvider{

	private final LoginProperties loginProperties;
	private final SessionManager sessionMng;
	private final LimeUserInfoService userServ;
	private final LimeUserAccountService accountServ;
	private final AdminRosterService admRosterServ;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		return processLogin(authentication);
	}

	private UsernamePasswordAuthenticationToken processLogin(Authentication authentication) {
		UsernamePasswordAuthenticationToken result = null;
		
		CustomWebAuthenticationDetails customDetail = (CustomWebAuthenticationDetails) authentication.getDetails();
		LoginType loginType = customDetail.getLoginType();
		if(loginType == LoginType.FORMAL) {
			result = processMemberLogin(authentication);
		}else if(loginType == LoginType.GUEST) {
			result = processGuestLogin(authentication);
		}else {
			throw new InternalAuthenticationServiceException(loginType.toString());
		}
		return result;
	}
	
	private UsernamePasswordAuthenticationToken processMemberLogin(Authentication authentication) throws AuthenticationException {
		String userId = authentication.getName();
		String userPw = authentication.getCredentials().toString();
		Map<String, TempAccount> tempAccountMap = loginProperties.getTempAccount().stream().collect(Collectors.toMap(TempAccount::getPrincipal, Function.identity()));
		if(tempAccountMap.containsKey(userId)) {
			TempAccount account = tempAccountMap.get(userId);
			if(!account.getCredential().equals(userPw)) {
				throw new BadCredentialsException(userPw);
			}
			return generateTempAuthenticationToken(account);
		}else {
			validateAuthentication(userId, userPw);
			UserInfoVO userModel = validateUserModel(userId);
			return generateAuthenticationToken(LoginType.FORMAL, userModel);
		}
	}
	
	private UsernamePasswordAuthenticationToken processGuestLogin(Authentication authentication) throws AuthenticationException {
		throw new AuthenticationServiceException("대기실에 입장할 수 없습니다. 다시 시도해 주세요.");
	}
	
	public UsernamePasswordAuthenticationToken generateTempAuthenticationToken(TempAccount account) {
		UserInfoVO userModel = UserInfoVO.builder().userId(account.getPrincipal()).userName(account.getName()).build();
		List<SimpleGrantedAuthority> auth = new ArrayList<>();
		if(loginProperties.getLoginPermission().isSystemAdminEnabled() && account.getDomainRole() == DomainRole.SYSTEM_ADMIN){
			auth.add(new SimpleGrantedAuthority(DomainRole.SYSTEM_ADMIN.getCode()));
		}
		if(loginProperties.getLoginPermission().isMasterAdminEnabled() && account.getDomainRole() == DomainRole.MASTER_ADMIN){
			auth.add(new SimpleGrantedAuthority(DomainRole.SYSTEM_ADMIN.getCode()));
		    auth.add(new SimpleGrantedAuthority(DomainRole.MASTER_ADMIN.getCode()));
		}
		if(!auth.isEmpty()) {
			auth.add(new SimpleGrantedAuthority(DomainRole.MEMBER.getCode()));
		}
		log.info("generateAuthenticationToken - 권한목록: {}",auth);
		if(auth.isEmpty()) {
			throw new InsufficientAuthenticationException("사용자에게 부여할 권한이 없습니다");
		}
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userModel.getUserId(), null, auth);
		UserDetails detail = LimeAuthenticationDetails.builder()
				.authorities(auth)
				.user(userModel)
				.loginDateTime(LocalDateTime.now())
				.loginType(LoginType.FORMAL)
				.build();
		token.setDetails(detail);
		return token;
	}
	
	/**
	 * 로그인 요청 유효성 검증. 로그인 방식별 검증 절차 진행.
	 * @param loginType 로그인 유형
	 * @param userId 요청한 로그인 ID
	 * @param userPw 요청한 로그인 PW
	 */
	private void validateAuthentication(String userId, String userPw) {
		Optional<UserAccountVO> accountOpt = accountServ.selectUserAccountOne(userId);
		if(accountOpt.isPresent()) {
			UserAccountVO accountVO = accountOpt.get();
			String salt = accountVO.getSalt();
			if(!SHA256Util.getEncrypt(userPw, salt).equals(accountVO.getUserPw())) {
				throw new BadCredentialsException(userPw);
			}
			if(!accountVO.isEnabled()) {
				throw new DisabledException(userId);
			}
			if(!accountVO.isAccountNonExpired()) {
				throw new AccountExpiredException(userId);
			}
			if(!accountVO.isAccountNonLocked()) {
				throw new LockedException(userId);
			}
		}else {
			List<TempAccount> tempAccountList = loginProperties.getTempAccount();
			log.info("tempAccountList: {}", tempAccountList);
			throw new InternalAuthenticationServiceException(userId);
		}
	}
	
	private UserInfoVO validateUserModel(String userId) {
		Optional<UserInfoVO> userOpt = userServ.selectUserInfoOne(userId);
		if(userOpt.isPresent()) {
			UserInfoVO userVO = userOpt.get();
			if(userVO.getDelYN() == 'N') {
				return userVO;
			}else {
				throw new DisabledException(userId);
			}
		}else {
			List<TempAccount> tempAccountList = loginProperties.getTempAccount();
			log.info("tempAccountList: {}", tempAccountList);
			throw new InternalAuthenticationServiceException(userId);
		}
	}

	public UsernamePasswordAuthenticationToken generateAuthenticationToken(LoginType loginType, UserInfoVO userVO) {
		Collection<? extends GrantedAuthority> authorities = getLevelAuthorities(userVO);
		log.info("generateAuthenticationToken - 권한목록: {}",authorities);
		if(authorities.isEmpty()) {
			throw new InsufficientAuthenticationException("사용자에게 부여할 권한이 없습니다");
		}
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userVO.getUserId(), null, authorities);
		UserDetails detail = LimeAuthenticationDetails.builder()
				.authorities(authorities)
				.user(userVO)
				.loginDateTime(LocalDateTime.now())
				.loginType(loginType)
				.build();
		token.setDetails(detail);
		return token;
	}
	/**
	 * 권한 발급 처리
	 * @param saupsoAdminList
	 * @param accountVO
	 * @return
	 */
	public Collection<? extends GrantedAuthority> getLevelAuthorities(UserInfoVO accountVO) {
		String userId = accountVO.getUserId();
		List<AdminRosterVO> domainAdminList = admRosterServ.getDomainAdminList(userId);
		List<SimpleGrantedAuthority> auth = new ArrayList<>();
		if(loginProperties.getLoginPermission().isGeneralUserEnabled()) {
			auth.add(new SimpleGrantedAuthority(DomainRole.GENERAL.getCode()));
		}
		Set<DomainRole> adminRoleSet = new HashSet<>();
		if( domainAdminList.size() > 0) {
			adminRoleSet = domainAdminList.stream().map(AdminRosterVO::getDomainRole).collect(Collectors.toSet());
		}
		if(loginProperties.getLoginPermission().isSystemAdminEnabled() && adminRoleSet.contains(DomainRole.SYSTEM_ADMIN)){
			auth.add(new SimpleGrantedAuthority(DomainRole.SYSTEM_ADMIN.getCode()));
		}
		if(loginProperties.getLoginPermission().isMasterAdminEnabled() && adminRoleSet.contains(DomainRole.MASTER_ADMIN)){
			auth.add(new SimpleGrantedAuthority(DomainRole.SYSTEM_ADMIN.getCode()));
		    auth.add(new SimpleGrantedAuthority(DomainRole.MASTER_ADMIN.getCode()));
		}
		if(adminRoleSet.contains(DomainRole.DEV)){
			auth.add(new SimpleGrantedAuthority(DomainRole.SYSTEM_ADMIN.getCode()));
		    auth.add(new SimpleGrantedAuthority(DomainRole.MASTER_ADMIN.getCode()));
		    auth.add(new SimpleGrantedAuthority(DomainRole.DEV.getCode()));
		}
		if(!auth.isEmpty()) {
			auth.add(new SimpleGrantedAuthority(DomainRole.MEMBER.getCode()));
		}
		return auth;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
	
}
