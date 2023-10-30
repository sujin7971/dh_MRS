package egov.framework.plms.sub.ewp.bean.mvc.controller.organization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.codehaus.commons.nullanalysis.Nullable;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import egov.framework.plms.main.bean.mvc.entity.admin.AdminRosterVO;
import egov.framework.plms.main.core.model.auth.ResourceAuthorityCollection;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import egov.framework.plms.main.core.model.enums.user.LoginType;
import egov.framework.plms.main.core.model.enums.user.ManagerRole;
import egov.framework.plms.main.core.util.CommUtil;
import egov.framework.plms.sub.ewp.bean.component.auth.EwpResourceAuthorityProvider;
import egov.framework.plms.sub.ewp.bean.component.properties.PlteProperties;
import egov.framework.plms.sub.ewp.bean.component.properties.SsoProperties;
import egov.framework.plms.sub.ewp.bean.mvc.entity.admin.EwpItemManagerRosterVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.organization.EwpUserInfoDTO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.organization.EwpUserInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.service.admin.EwpAdminRosterService;
import egov.framework.plms.sub.ewp.bean.mvc.service.organization.EwpUserInfoService;
import egov.framework.plms.sub.ewp.core.model.login.EwpAuthenticationDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/ewp")
@RequiredArgsConstructor
@Profile("ewp")
public class EwpUserRestController {
	private final EwpResourceAuthorityProvider authorityProvider;
	private final EwpUserInfoService userServ;
	private final EwpAdminRosterService rosterServ;
	private final SsoProperties ssoProperties;
	private final PlteProperties plteProperties;
	/**
	 * 유저 전체 검색. 사번을 전달하는 경우 LIKE검색 처리(동일 사번 검색은 유저 한명 조회하는 쿼리 사용하도록)
	 * @param authentication
	 * @param userDTO
	 * @return
	 */
	@GetMapping("/user/list")
	public List<EwpUserInfoDTO> getUserList(EwpUserInfoDTO userDTO) {
		log.info("사용자 검색 파라미터: {}", userDTO.toString());
		List<EwpUserInfoVO> userList = userServ.selectUserInfoList(userDTO.convert());
		return userList.stream().map(EwpUserInfoVO::convert).map(EwpUserInfoDTO::filterForBasic).collect(Collectors.toList());
	}
	
	/**
	 * 미인증 사용자의 품목 담당자 목록 조회
	 * @param officeCode
	 * @param roomType
	 * @return
	 */
	@GetMapping("/public/roster/approval-manager/list")
	public List<EwpUserInfoDTO> getApprovalManagerPublicList(
			@RequestParam @Nullable String officeCode, 
			@RequestParam @Nullable RoomType roomType){
		List<AdminRosterVO> list = rosterServ.getManagerAdminList(officeCode, ManagerRole.getRoomManagerRole(roomType));
		List<EwpUserInfoVO> userList = list.stream().map(roster -> {
			Optional<EwpUserInfoVO> opt = userServ.selectUserInfoOne(roster.getUserId());
			return opt.get();
		}).filter(user -> user != null).collect(Collectors.toList());
		return userList.stream().map(EwpUserInfoVO::convert).map(EwpUserInfoDTO::filterForBasic).collect(Collectors.toList());
	}
	
	/**
	 * 사용자 이미지 조회
	 * @param response
	 * @param userKey
	 * @throws Exception
	 */
	@GetMapping("/user/{userKey}/img")
	public void getImage(Authentication authentication, HttpServletResponse response, @PathVariable String userKey) 
            throws Exception {
		EwpAuthenticationDetails userDetails = (EwpAuthenticationDetails) authentication.getDetails();
		LoginType loginType = userDetails.getLoginType();
		String domain = plteProperties.getPictureDomain();
		if(loginType == LoginType.SSO) {
			domain = ssoProperties.getPictureDomain();
		}
		if(CommUtil.isEmpty(domain)) {
			log.info("사용자({})의 이미지 요청 실패- 이미지를 요청할 도메인 주소가 필요합니다.");
			returnDefaultUserImg(response);
			return;
		}
		log.info("사용자({})의 이미지 요청- 로그인방식: {}", userKey, loginType);
		// TODO Auto-generated method stub
		String url = domain + "/" + userKey + ".jpg";
		URL imgUrl = new URL(url);
		URLConnection conn = imgUrl.openConnection();
		conn.setConnectTimeout(2000);
		conn.setReadTimeout(2000);
		response.setContentType("application/jpeg");
		try {
			IOUtils.copy( conn.getInputStream(), response.getOutputStream());
		}catch (FileNotFoundException | ConnectException | SocketTimeoutException e) {
			returnDefaultUserImg(response);
		}
	}
	
	private void returnDefaultUserImg(HttpServletResponse response) throws IOException {
		File defaultImg = new ClassPathResource("/img/user.png").getFile();
	    try (InputStream is = new FileInputStream(defaultImg)) {
	        IOUtils.copy(is, response.getOutputStream());
	    } catch (IOException ex) {
	        log.error("기본 이미지 파일을 읽어올 수 없습니다.");
	    }
	}
	
	/**
	 * 사용자의 해당 회의에 대한 권한 조회
	 * @param authentication
	 * @param meetingKey
	 * @return
	 */
	@GetMapping("/user/authority/meeting/{meetingKey}")
	public Set<String> getUserAuthorityForMeeting(Authentication authentication, @PathVariable Integer meetingKey) {
		EwpAuthenticationDetails userDetails = (EwpAuthenticationDetails) authentication.getDetails();
		String loginKey = userDetails.getUser().getUserKey();
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingKey, false);
		return authorityCollection.getAuthorities();
	}
	
	/**
	 * 관리자의 해당 회의에 대한 권한 조회
	 * @param authentication
	 * @param meetingKey
	 * @return
	 */
	@GetMapping("/admin/authority/meeting/{meetingKey}")
	public Set<String> getAdminAuthorityForMeeting(Authentication authentication, @PathVariable Integer meetingKey) {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingKey, false);
		return authorityCollection.getAuthorities();
	}
}
