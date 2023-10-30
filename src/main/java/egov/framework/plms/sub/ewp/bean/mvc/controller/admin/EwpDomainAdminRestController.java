package egov.framework.plms.sub.ewp.bean.mvc.controller.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import egov.framework.plms.main.bean.component.session.SessionManager;
import egov.framework.plms.main.bean.mvc.entity.admin.AdminRosterVO;
import egov.framework.plms.main.core.exception.ApiNotFoundException;
import egov.framework.plms.main.core.model.enums.error.ErrorCode;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import egov.framework.plms.main.core.model.enums.user.DomainRole;
import egov.framework.plms.main.core.model.enums.user.LoginType;
import egov.framework.plms.main.core.model.enums.user.ManagerRole;
import egov.framework.plms.main.core.model.login.AuthenticationDetails;
import egov.framework.plms.main.core.model.response.ResponseMessage;
import egov.framework.plms.main.core.model.response.ResponseMessage.StatusCode;
import egov.framework.plms.sub.ewp.bean.component.login.EwpLoginAuthProvider;
import egov.framework.plms.sub.ewp.bean.mvc.entity.admin.EwpItemManagerRosterVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.organization.EwpUserInfoDTO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.organization.EwpUserInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.service.admin.EwpAdminRosterService;
import egov.framework.plms.sub.ewp.bean.mvc.service.organization.EwpUserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ewp")
@Profile("ewp")
public class EwpDomainAdminRestController {
	private final EwpAdminRosterService rosterServ;
	private final EwpUserInfoService userServ;
	private final SessionManager sessionManager;
	private final EwpLoginAuthProvider loginAuthProvider;
	/**
	 * 세션 접속자 조회
	 * @param authentication
	 * @param officeCode 사업소 코드
	 * @return userCnt: int, users: {loginDate: LocalDateTime, userVO: EwpUserInfoVO}
	 */
	//@GetMapping("/dev/session/userList")
	public Map<String, Object> getSessionUserList(Authentication authentication) {
		List<AuthenticationDetails> userList = sessionManager.getSessionOwners(); // 세션 접속 유저 리스트
		Map<String, Object> map = new HashMap<String, Object>(); // 리턴용 map 생성
		map.put("users", userList); // 로그인 유저 정보리스트
		map.put("userCnt", userList.size()); // 로그인 한 유저 인원 수 저장
		return map;
	}
	
	//@PostMapping("/dev/switch-authentication/user/{userId}/loginType/{loginType}")
	public ResponseMessage switchUserAuthenticationForDev(@PathVariable String userId, @PathVariable LoginType loginType) {
		log.info("사용자 인증 정보 변경 요청 - 사번: {}, 로그인 타입: {}", userId, loginType);
		Optional<EwpUserInfoVO> userOpt = userServ.selectUserInfoOne(userId);
		if(userOpt == null) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.NOT_FOUND).message("인증 정보 변경 실패").detail("인증 정보를 변경할 사용자를 찾을 수 없습니다").build();
		}
		try {
			EwpUserInfoVO userVO = userOpt.get();
			UsernamePasswordAuthenticationToken token = loginAuthProvider.generateAuthenticationToken(loginType, userVO);
			SecurityContextHolder.getContext().setAuthentication(token);
			return ResponseMessage.builder(ResponseMessage.StatusCode.OK).build();
		}catch(InsufficientAuthenticationException e) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.NOT_FOUND).message("인증 정보 변경 실패").detail(e.getMessage()).build();
		}
	}
	@GetMapping("/admin/system/user/{userKey}")
	public EwpUserInfoDTO getUserOne(Authentication authentication, @PathVariable String userKey) {
		Optional<EwpUserInfoVO> userOpt = userServ.selectUserInfoOne(userKey);
		return userOpt.map(EwpUserInfoVO::convert).orElse(null);
	}
	@GetMapping("/admin/system/roster/system-admin/list")
	public List<EwpUserInfoDTO> getSystemDomainAdminList(){
		List<AdminRosterVO> list = rosterServ.getDomainAdminList(DomainRole.SYSTEM_ADMIN);
		return list.stream()
				.map(roster -> userServ.selectUserInfoOne(roster.getUserId()))
				.filter(Optional::isPresent)
		        .map(Optional::get)
		        .map(EwpUserInfoVO::convert)
		        .collect(Collectors.toList());
	}
	@PostMapping("/admin/system/roster/system-admin/{userId}")
	public ResponseMessage postSystemDomainAdmin(@PathVariable String userId) {
		validationUser(userId);
		boolean result = rosterServ.postDomainAdmin(userId, DomainRole.SYSTEM_ADMIN);
		if(result) {
			return ResponseMessage.builder(StatusCode.OK).build();
		}else {
			return ResponseMessage.builder(StatusCode.UNPROCESSABLE_ENTITY).message(ErrorCode.DOMAIN_ROSTER.POST_FAILED.getMessage()).build();
		}
	}
	@DeleteMapping("/admin/system/roster/system-admin/{userId}")
	public ResponseMessage deleteSystemDomainAdmin(@PathVariable String userId) {
		validationUser(userId);
		boolean result = rosterServ.deleteDomainAdmin(userId, DomainRole.SYSTEM_ADMIN);
		if(result) {
			return ResponseMessage.builder(StatusCode.OK).build();
		}else {
			return ResponseMessage.builder(StatusCode.UNPROCESSABLE_ENTITY).message(ErrorCode.DOMAIN_ROSTER.DELETE_FAILED.getMessage()).build();
		}
	}
	@GetMapping("/admin/system/roster/master-admin/list")
	public List<EwpUserInfoDTO> getMasterAdminList(){
		List<AdminRosterVO> list = rosterServ.getDomainAdminList(DomainRole.MASTER_ADMIN);
		return list.stream()
				.map(roster -> userServ.selectUserInfoOne(roster.getUserId()))
				.filter(Optional::isPresent)
		        .map(Optional::get)
		        .map(EwpUserInfoVO::convert)
		        .collect(Collectors.toList());
	}
	

	@GetMapping("/admin/system/roster/request-manager/list")
	public List<EwpUserInfoDTO> getRequestManagerList(){
		List<AdminRosterVO> list = rosterServ.getManagerAdminList(ManagerRole.REQUEST_MANAGER);
		return list.stream()
				.map(roster -> userServ.selectUserInfoOne(roster.getUserId()))
				.filter(Optional::isPresent)
		        .map(Optional::get)
		        .map(EwpUserInfoVO::convert)
		        .collect(Collectors.toList());
	}
	@PostMapping("/admin/system/roster/request-manager/{userId}")
	public ResponseMessage postRequestManagerAdmin(@PathVariable String userId) {
		validationUser(userId);
		boolean result = rosterServ.postManagerAdmin(userId, ManagerRole.REQUEST_MANAGER);
		if(result) {
			return ResponseMessage.builder(StatusCode.OK).build();
		}else {
			return ResponseMessage.builder(StatusCode.UNPROCESSABLE_ENTITY).message(ErrorCode.MANAGER_ROSTER.POST_FAILED.getMessage()).build();
		}
	}
	@DeleteMapping("/admin/system/roster/request-manager/{userId}")
	public ResponseMessage deleteRequestManagerAdmin(@PathVariable String userId) {
		validationUser(userId);
		boolean result = rosterServ.deleteManagerAdmin(userId, ManagerRole.REQUEST_MANAGER);
		if(result) {
			return ResponseMessage.builder(StatusCode.OK).build();
		}else {
			return ResponseMessage.builder(StatusCode.UNPROCESSABLE_ENTITY).message(ErrorCode.MANAGER_ROSTER.DELETE_FAILED.getMessage()).build();
		}
	}
	@PostMapping("/admin/system/roster/room-manager/{userId}")
	public ResponseMessage postRequestManagerAdmin(@PathVariable String userId,
			@RequestParam String officeCode, 
			@RequestParam RoomType roomType) {
		validationUser(userId);
		boolean result = rosterServ.postManagerAdmin(userId, officeCode, ManagerRole.getRoomManagerRole(roomType));
		if(result) {
			return ResponseMessage.builder(StatusCode.OK).build();
		}else {
			return ResponseMessage.builder(StatusCode.UNPROCESSABLE_ENTITY).message(ErrorCode.MANAGER_ROSTER.POST_FAILED.getMessage()).build();
		}
	}
	@DeleteMapping("/admin/system/roster/room-manager/{userId}")
	public ResponseMessage deleteRequestManagerAdmin(@PathVariable String userId,
			@RequestParam String officeCode, 
			@RequestParam RoomType roomType) {
		validationUser(userId);
		boolean result = rosterServ.deleteManagerAdmin(userId, officeCode, ManagerRole.getRoomManagerRole(roomType));
		if(result) {
			return ResponseMessage.builder(StatusCode.OK).build();
		}else {
			return ResponseMessage.builder(StatusCode.UNPROCESSABLE_ENTITY).message(ErrorCode.MANAGER_ROSTER.DELETE_FAILED.getMessage()).build();
		}
	}
	@GetMapping("/admin/system/roster/room-manager/list")
	public List<EwpUserInfoDTO> getRoomManagerList(
			@RequestParam @Nullable String officeCode, 
			@RequestParam RoomType roomType
		){
		List<AdminRosterVO> list = rosterServ.getManagerAdminList(officeCode, ManagerRole.getRoomManagerRole(roomType));
		return list.stream()
				.map(roster -> userServ.selectUserInfoOne(roster.getUserId()))
				.filter(Optional::isPresent)
		        .map(Optional::get)
		        .map(EwpUserInfoVO::convert)
		        .collect(Collectors.toList());
	}
	
	private void validationUser(String userId) {
	    Optional<EwpUserInfoVO> userOpt = userServ.selectUserInfoOne(userId);
	    if (!userOpt.isPresent()) {
	        throw new ApiNotFoundException(ErrorCode.USER.NOT_FOUND);
	    }
	}
}
