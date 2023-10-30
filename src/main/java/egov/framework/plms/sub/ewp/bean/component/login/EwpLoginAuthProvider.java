package egov.framework.plms.sub.ewp.bean.component.login;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import egov.framework.plms.main.bean.component.properties.LoginProperties;
import egov.framework.plms.main.bean.component.session.SessionManager;
import egov.framework.plms.main.bean.mvc.entity.admin.AdminRosterVO;
import egov.framework.plms.main.core.model.enums.user.DomainRole;
import egov.framework.plms.main.core.model.enums.user.LoginType;
import egov.framework.plms.main.core.model.enums.user.ManagerRole;
import egov.framework.plms.main.core.model.login.CustomWebAuthenticationDetails;
import egov.framework.plms.sub.ewp.bean.component.properties.SsoProperties;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingAttendeeVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.organization.EwpUserInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.service.admin.EwpAdminRosterService;
import egov.framework.plms.sub.ewp.bean.mvc.service.common.EwpCodeService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingAttendeeService;
import egov.framework.plms.sub.ewp.bean.mvc.service.organization.EwpDeptInfoService;
import egov.framework.plms.sub.ewp.bean.mvc.service.organization.EwpUserInfoService;
import egov.framework.plms.sub.ewp.core.model.login.EwpAuthenticationDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("ewp")
public class EwpLoginAuthProvider implements AuthenticationProvider{

	private final LoginProperties loginProperties;
	private final SsoProperties ssoProperties;
	private final SessionManager sessionMng;
	private final EwpUserInfoService userInfoServ;
	private final EwpDeptInfoService deptServ;
	private final EwpAdminRosterService admRosterServ;
	private final EwpCodeService codeServ;
	private final EwpMeetingAttendeeService attserv;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		return processMemberLogin(authentication);
	}

	private UsernamePasswordAuthenticationToken processMemberLogin(Authentication authentication) {
		CustomWebAuthenticationDetails customDetail = (CustomWebAuthenticationDetails) authentication.getDetails();
		HttpServletRequest request = customDetail.getRequest();
		//동서환경에서 아래와 같은 방식으로 IP주소를 가져와야 SSO/PLTE구분 가능
		String connectIp = request.getRemoteAddr();
		
		String userId = authentication.getName();
		String userPw = authentication.getCredentials().toString();
		LoginType loginType = customDetail.getLoginType();
		if(loginType == LoginType.SSO) {
			userId = (String) request.getSession().getAttribute("loginId");
		}
		loginType = LoginType.SSO;
		log.info("로그인 인증 시작 - IP: {}, 요청 ID: {}, 인증방식: {}", connectIp, userId, loginType);
		
		//Oracle 인사DB의 정보
		Optional<EwpUserInfoVO> userOpt = userInfoServ.selectUserInfoOne(userId);
		//계정 존재여부
		if(!userOpt.isPresent()) {
			throw new InternalAuthenticationServiceException(userId);
		}
		EwpUserInfoVO userVO = userOpt.get();
		validateAuthentication(loginType, userId, userPw);			
		//validateLoginPermission();
		
		return generateAuthenticationToken(loginType, userVO);
	}
	/**
	 * 로그인 요청 유효성 검증. 로그인 방식별 검증 절차 진행.
	 * @param loginType 로그인 유형
	 * @param userId 요청한 로그인 ID
	 * @param userPw 요청한 로그인 PW
	 */
	private void validateAuthentication(LoginType loginType, String userId, String userPw) {
		switch(loginType) {
			case SSO:
				break;
			case PLTE:
				//MariaDB 회의 참석자 정보
				LocalDate currDate = LocalDate.now();
				List<EwpMeetingAttendeeVO> attendeeList = attserv.getMeetingAttendeeList( EwpMeetingAttendeeVO.builder().userKey(userId).build() )
						.stream()
						.filter(obj -> obj.getExpireDate() !=null && obj.getExpireDate().isAfter( currDate.minusDays(1) ) )
						.collect(Collectors.toList());
				if( attendeeList.size() == 0 ) {
					throw new AccountExpiredException("임시비밀번호를 발급한 전자회의를 조회할 수 없습니다. 초대받은 메시지 내역을 확인해 주세요.");
				}
				attendeeList = attendeeList.stream().filter(attendee -> attendee.getTempPW().equals(userPw)).collect(Collectors.toList());
				if( attendeeList.size() == 0 ) {
					throw new BadCredentialsException("임시비밀번호가 틀립니다. 초대받은 메시지 내역을 확인해 주세요.");
				}
				break;
			case GUEST:
			default:
				throw new InternalAuthenticationServiceException("로그인 요청방식이 올바르지 않습니다.");
		}
	}
	/**
	 * 로그인 허용 여부 판별. 현재 동시접속자 제한에 대해서만 판별함
	 */
	private void validateLoginPermission() {
		Integer authenticatedSessionCount = sessionMng.getAuthenticatedSessionCount();
		if(authenticatedSessionCount >= loginProperties.getMaximumConcurrentUser()) {
			throw new AuthenticationServiceException("동시 접속 인원 제한 초과");
		}
	}
	/**
	 * 사용자 모델 유효성 검증. 동서발전은 부서 식별키나 내선번호가 유효하지 않은 경우 사용신청 불가.
	 * 부서 식별키나 내선번호가 유효하지 않은 경우 ICT운영부의 부서 식별키와 내선번호를 사용하도록 설정
	 * @param userVO 검증할 사용자 모델
	 */
	private EwpUserInfoVO validateUserModel(EwpUserInfoVO userVO) {
		if(!codeServ.isEntriedOffice(userVO.getOfficeCode())) {
			//등록되지 않은 사업소. 본사 사업소를 사용하도록 변경
			userVO = userVO.toBuilder().officeCode("1000").build();
		}
		if(!deptServ.selectDeptInfoOne(userVO.getDeptId()).isPresent()) {
			log.info("userModelValidation - 유효하지 않은 부서({}) 소속. ICT운영부 부서키 50113484 설정", userVO.getDeptId());
			//테스트계정 등록된 부서가 실제로 없는 경우
			userVO = userVO.toBuilder().deptId("50133484").build();
		}
		if(userVO.getOfficeDeskPhone() == null) {
			log.info("userModelValidation - 내선번호 없음. ICT운영부 내선번호 8655 설정");
			userVO = userVO.toBuilder().officeDeskPhone("8655").build();
		}
		return userVO;
	}
	public UsernamePasswordAuthenticationToken generateAuthenticationToken(LoginType loginType, EwpUserInfoVO userVO) {
		userVO = validateUserModel(userVO);
		Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
		Collection<? extends GrantedAuthority> positions = new ArrayList<>();
		switch(loginType) {
			case SSO:
				positions = getPositionAuthorities(userVO);
				authorities = getLevelAuthoritiesForSSO(userVO, positions);
				break;
			case PLTE:
				authorities = getLevelAuthoritiesForPLTE(userVO);
				break;
			default:
				break;
		}
		log.info("generateAuthenticationToken - 권한목록: {}",authorities);
		if(authorities.isEmpty()) {
			throw new InsufficientAuthenticationException("사용자에게 부여할 권한이 없습니다");
		}
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userVO.getUserId(), null, authorities);
		UserDetails detail = EwpAuthenticationDetails.builder()
				.authorities(authorities)
				.positions(positions)
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
	public Collection<? extends GrantedAuthority> getLevelAuthoritiesForSSO(EwpUserInfoVO accountVO, Collection<? extends GrantedAuthority> positions) {
		String officeCode = accountVO.getOfficeCode();
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
		log.info("adminRoleSet: {}", adminRoleSet);
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
		if(positions.contains(new SimpleGrantedAuthority(ManagerRole.APPROVAL_MANAGER.getCode()))) {
			auth.add(new SimpleGrantedAuthority(DomainRole.APPROVAL_ADMIN.getCode()));
		}
		if(!auth.isEmpty()) {
			auth.add(new SimpleGrantedAuthority(DomainRole.MEMBER.getCode()));
		}
		return auth;
	}
	
	public Collection<? extends GrantedAuthority> getLevelAuthoritiesForPLTE(EwpUserInfoVO accountVO) {
		List<SimpleGrantedAuthority> auth = new ArrayList<>();
		if(loginProperties.getLoginPermission().isGeneralUserEnabled()) {
			auth.add(new SimpleGrantedAuthority(DomainRole.GENERAL.getCode()));
		}
		if(!auth.isEmpty()) {
			auth.add(new SimpleGrantedAuthority(DomainRole.MEMBER.getCode()));
		}
		return auth;
	}
	
	public Collection<? extends GrantedAuthority> getPositionAuthorities(EwpUserInfoVO accountVO) {
		List<SimpleGrantedAuthority> auth = new ArrayList<>();
		String officeCode = accountVO.getOfficeCode();
		String userId = accountVO.getUserId();
		List<AdminRosterVO> domainAdminList = admRosterServ.getDomainAdminList(userId);
		Set<DomainRole> adminRoleSet = domainAdminList.stream().map(AdminRosterVO::getDomainRole).collect(Collectors.toSet());
		if(adminRoleSet.contains(DomainRole.DEV)){
			auth.add(new SimpleGrantedAuthority(ManagerRole.APPROVAL_MANAGER.getCode()));
		    auth.add(new SimpleGrantedAuthority(ManagerRole.MEETING_ROOM_MANAGER.getCode()));
		    auth.add(new SimpleGrantedAuthority(ManagerRole.EDU_ROOM_MANAGER.getCode()));
		    auth.add(new SimpleGrantedAuthority(ManagerRole.HALL_MANAGER.getCode()));
		    auth.add(new SimpleGrantedAuthority(ManagerRole.REQUEST_MANAGER.getCode()));
		}else {
			List<AdminRosterVO> managerAdminList = admRosterServ.getManagerAdminList(userId);
			boolean hasRoomManagerRole = managerAdminList.stream().anyMatch(entry -> new HashSet<>(Arrays.asList(
					ManagerRole.MEETING_ROOM_MANAGER,
					ManagerRole.EDU_ROOM_MANAGER,
					ManagerRole.HALL_MANAGER
			)).contains(entry.getManagerRole()));
			if(loginProperties.getLoginPermission().isApprovalManagerEnabled() && hasRoomManagerRole) {
				auth.add(new SimpleGrantedAuthority(ManagerRole.APPROVAL_MANAGER.getCode()));
			}
			managerAdminList.forEach(manager -> {
				auth.add(new SimpleGrantedAuthority(manager.getManagerRole().getCode()));
			});
		}
		return auth;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
	
}
