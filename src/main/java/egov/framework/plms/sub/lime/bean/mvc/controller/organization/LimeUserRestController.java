package egov.framework.plms.sub.lime.bean.mvc.controller.organization;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import egov.framework.plms.main.bean.component.auth.ResourceAuthorityProvider;
import egov.framework.plms.main.bean.mvc.entity.organization.UserInfoDTO;
import egov.framework.plms.main.bean.mvc.entity.organization.UserInfoVO;
import egov.framework.plms.main.bean.mvc.service.admin.AdminRosterService;
import egov.framework.plms.main.core.model.auth.ResourceAuthorityCollection;
import egov.framework.plms.main.core.model.enums.user.DomainRole;
import egov.framework.plms.main.core.model.response.ResponseMessage;
import egov.framework.plms.main.core.model.response.ResponseMessage.StatusCode;
import egov.framework.plms.sub.lime.bean.mvc.service.organization.LimeUserAccountService;
import egov.framework.plms.sub.lime.bean.mvc.service.organization.LimeUserInfoService;
import egov.framework.plms.sub.lime.core.util.LimeSecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/lime")
@RequiredArgsConstructor
@Profile("lime")
public class LimeUserRestController {
	private final ResourceAuthorityProvider authorityProvider;
	private final LimeUserInfoService userServ;
	private final LimeUserAccountService accountServ;
	private final AdminRosterService admRosterServ;
	
	@PostMapping("/admin/system/user")
	public ResponseMessage insertUserOne(UserInfoDTO userDTO) {
		log.info("userDTO: {}", userDTO);
		UserInfoVO infoParams = userDTO.convert();
		boolean infoResult = userServ.insertUserInfoOne(infoParams);
		if(!infoResult) {
			return ResponseMessage.builder(StatusCode.BAD_REQUEST).build();
		}
		boolean accountResult = accountServ.insertUserAccount(infoParams.getUserId());
		if(!accountResult) {
			userServ.deleteUserInfoOne(infoParams.getUserId());
			return ResponseMessage.builder(StatusCode.BAD_REQUEST).build();
		}
		return ResponseMessage.builder(StatusCode.OK).build();
	}
	
	@PutMapping("/admin/system/user")
	public ResponseMessage updateUserOne(UserInfoDTO userDTO) {
		log.info("userDTO: {}", userDTO);
		UserInfoVO infoParams = userDTO.convert();
		boolean infoResult = userServ.updateUserInfoOne(infoParams);
		if(!infoResult) {
			return ResponseMessage.builder(StatusCode.BAD_REQUEST).build();
		}
		return ResponseMessage.builder(StatusCode.OK).build();
	}
	
	@DeleteMapping("/admin/system/user/{userId}")
	public ResponseMessage deleteUserOne(@PathVariable String userId) {
		boolean infoResult = userServ.updateUserInfoOneToDelete(userId);
		if(!infoResult) {
			return ResponseMessage.builder(StatusCode.BAD_REQUEST).build();
		}
		return ResponseMessage.builder(StatusCode.OK).build();
	}
	/**
	 * 유저 전체 검색. 사번을 전달하는 경우 LIKE검색 처리(동일 사번 검색은 유저 한명 조회하는 쿼리 사용하도록)
	 * @param authentication
	 * @param userDTO
	 * @return
	 */
	@GetMapping("/user/list")
	public List<UserInfoDTO> getUserList(UserInfoDTO userDTO) {
		List<UserInfoVO> userList = userServ.selectUserInfoList(userDTO.convert());
		return userList.stream().map(UserInfoVO::convert).map(dto -> {
			if(!LimeSecurityUtil.hasRole(DomainRole.SYSTEM_ADMIN)) {
				return dto.filterForBasic();
			}
			return dto;
		}).collect(Collectors.toList());
	}
}
