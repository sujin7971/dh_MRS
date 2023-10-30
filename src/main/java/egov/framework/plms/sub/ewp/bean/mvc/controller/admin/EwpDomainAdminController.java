package egov.framework.plms.sub.ewp.bean.mvc.controller.admin;

import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import egov.framework.plms.main.annotation.NoCache;
import egov.framework.plms.main.core.exception.ApiDataAccessDeniedException;
import egov.framework.plms.main.core.exception.ApiNotFoundException;
import egov.framework.plms.main.core.model.auth.ResourceAuthorityCollection;
import egov.framework.plms.main.core.model.enums.auth.MeetingAuth;
import egov.framework.plms.main.core.model.enums.error.ErrorCode;
import egov.framework.plms.main.core.model.enums.user.DomainRole;
import egov.framework.plms.main.core.util.DateTimeUtil;
import egov.framework.plms.sub.ewp.bean.component.auth.EwpResourceAuthorityProvider;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingScheduleVO;
import egov.framework.plms.sub.ewp.bean.mvc.service.admin.EwpAdminRosterService;
import egov.framework.plms.sub.ewp.bean.mvc.service.common.EwpCodeService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingScheduleService;
import egov.framework.plms.sub.ewp.bean.mvc.service.organization.EwpUserInfoService;
import egov.framework.plms.sub.ewp.core.model.login.EwpAuthenticationDetails;
import egov.framework.plms.sub.ewp.core.util.EwpSecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * 관리자 페이지 요청 처리 컨트롤러
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 */
@Slf4j
@Controller
@RequestMapping("/ewp")
@RequiredArgsConstructor
@Profile("ewp")
public class EwpDomainAdminController {
	private final EwpResourceAuthorityProvider authorityProvider;
	private final EwpCodeService codeServ;
	private final EwpUserInfoService userServ;
	private final EwpAdminRosterService rosterServ;
	private final EwpMeetingScheduleService scheServ;
	
	/**
	 * 관리자 지정 페이지
	 * @param model
	 * @return
	 */
	@NoCache
	@GetMapping("/admin/system/manage/authority")
	public String adminEmployeeManageForm(Model model) {
		model.addAttribute("officeBook", codeServ.getOfficeBook());
		model.addAttribute("officeCode", EwpSecurityUtil.getOfficeCode());
		return "/admin/manage/authority_manage";
	}
	
	/**
	 * 장소 관리 페이지
	 * @param model
	 * @return
	 */
	@NoCache
	@GetMapping("/admin/system/manage/room")
	public String roomManageForm(Model model) {
		model.addAttribute("officeBook", codeServ.getOfficeBook());
		EwpAuthenticationDetails detail = EwpSecurityUtil.getAuthenticationDetails();
		if(detail.hasRole(DomainRole.MASTER_ADMIN)) {
			model.addAttribute("officeCode", "all");
		}else if(detail.hasRole(DomainRole.SYSTEM_ADMIN)){
			model.addAttribute("officeCode", EwpSecurityUtil.getOfficeCode());
		}
		model.addAttribute("detail", detail);
		return "/admin/manage/room_manage";
	}
	
	/**
	 * 관리자 파일함 페이지 요청
	 * @param authentication
	 * @param model
	 * @param searchTarget 검색 대상 {@link MeetingArchiveVO#title}, {@link MeetingArchiveVO#skdHost}, {@link MeetingArchiveVO#originalName} 
	 * @param searchWord 검색어
	 * @param startDate 기간 검색을 위한 시작일자(yyyy-MM-dd)
	 * @param endDate 기간 검색을 위한 종료일자(yyyy-MM-dd)
	 * @return
	 */
	@GetMapping("/admin/master/manage/meeting/archive")
	public String deptMeetingArchiveForm(Authentication authentication, Model model, 
			@RequestParam @Nullable String searchTarget, 
			@RequestParam @Nullable String searchWord, 
			@RequestParam @Nullable String startDate, 
			@RequestParam @Nullable String endDate) {
		model.addAttribute("searchTarget", searchTarget);
		model.addAttribute("searchWord", searchWord);
		model.addAttribute("nav", "adminNav");
		DateTimeUtil.setModelOfDatePeriod(model, startDate, endDate);
		return "/manage/assign/archive_admin";
	}
	
	@GetMapping("/admin/system/meeting/assign/{skdKey}")
	public String viewRoomAssignForm(Model model, @PathVariable Integer skdKey) {
		Optional<EwpMeetingScheduleVO> opt = scheServ.getMeetingScheduleOne(skdKey);
		if(!opt.isPresent()) {
			throw new ApiNotFoundException(ErrorCode.MEETING_ASSIGN.NOT_FOUND);
		}
		EwpMeetingScheduleVO scheVO = opt.get();
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(scheVO.getMeetingKey());
		if(authorityCollection.hasAuthority(MeetingAuth.VIEW)) {
			model.addAttribute("authorityCollection", authorityCollection);
			model.addAttribute("officeList", codeServ.getOfficeBook());
			model.addAttribute("skdKey", skdKey);
			return "/meeting/assign/assign_view";
		}else {
			throw new ApiDataAccessDeniedException(ErrorCode.MEETING_ASSIGN.READ_NO_AUTHORITY);
		}
	}
	
	/**
	 * 공지사항 관리 페이지
	 * @param authentication
	 * @param model
	 * @return
	 */
	@GetMapping("/admin/system/manage/notice")
	public String noticeManageForm(Authentication authentication, Model model) {
		model.addAttribute("officeBook", codeServ.getOfficeBook());
		return "/admin/manage/notice_manage";
	}
	
	/** 배정 담당자 관리 페이지
	 * @param authentication
	 * @param model
	 * @return
	 */
	@RequestMapping("/admin/system/manage/manager")
	public String managerManageForm(Authentication authentication, Model model) {
		model.addAttribute("officeList", codeServ.getOfficeBook());
		return "/admin/manage/manager_manage";
	}
	
	/** 세션리스트 관리 페이지
	 * @param authentication
	 * @param model
	 * @return
	 */
	//@GetMapping("/dev/manage/session")
	public String sessionForm(Authentication authentication, Model model) {
		model.addAttribute("officeBook", codeServ.getOfficeBook());
		return "/admin/manage/session_manage";
	}
	
	@NoCache
	//@GetMapping("/dev/switch-authentication")
	public String switchUserAuthenticationForDevForm(Authentication authentication, Model model) {
		return "/admin/switch_authentication";
	}
	
}
