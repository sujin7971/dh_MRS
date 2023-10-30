package egov.framework.plms.sub.lime.bean.mvc.controller.admin;

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
import egov.framework.plms.main.bean.component.auth.ResourceAuthorityProvider;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingArchiveVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingScheduleVO;
import egov.framework.plms.main.bean.mvc.service.admin.AdminRosterService;
import egov.framework.plms.main.bean.mvc.service.common.CodeService;
import egov.framework.plms.main.bean.mvc.service.meeting.MeetingScheduleService;
import egov.framework.plms.main.bean.mvc.service.organization.UserInfoService;
import egov.framework.plms.main.core.exception.ApiDataAccessDeniedException;
import egov.framework.plms.main.core.exception.ApiNotFoundException;
import egov.framework.plms.main.core.model.auth.ResourceAuthorityCollection;
import egov.framework.plms.main.core.model.enums.auth.MeetingAuth;
import egov.framework.plms.main.core.model.enums.error.ErrorCode;
import egov.framework.plms.main.core.util.DateTimeUtil;
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
@RequestMapping("/lime")
@RequiredArgsConstructor
@Profile("lime")
public class LimeDomainAdminController {
	private final ResourceAuthorityProvider authorityProvider;
	private final CodeService codeServ;
	private final UserInfoService userServ;
	private final AdminRosterService rosterServ;
	private final MeetingScheduleService scheServ;
	
	/**
	 * 관리자 지정 페이지
	 * @param model
	 * @return
	 */
	@NoCache
	@GetMapping("/admin/system/manage/authority")
	public String adminEmployeeManageForm() {
		return "/admin/manage/authority_manage";
	}
	
	/**
	 * 조직관리
	 * @return
	 */
	@NoCache
	@GetMapping("/admin/system/manage/organization")
	public String organizationManageForm() {
		return "/admin/manage/organ_manage";
	}
	
	/**
	 * 장소 관리 페이지
	 * @param model
	 * @return
	 */
	@NoCache
	@GetMapping("/admin/system/manage/room")
	public String roomManageForm(Model model) {
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
		return "/manage/archive/archive_admin";
	}
	
	@GetMapping("/admin/system/meeting/assign/{skdKey}")
	public String viewRoomAssignForm(Model model, @PathVariable Integer skdKey) {
		Optional<MeetingScheduleVO> opt = scheServ.getMeetingScheduleOne(skdKey);
		if(!opt.isPresent()) {
			throw new ApiNotFoundException(ErrorCode.MEETING_ASSIGN.NOT_FOUND);
		}
		MeetingScheduleVO scheVO = opt.get();
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(scheVO.getMeetingId());
		if(authorityCollection.hasAuthority(MeetingAuth.VIEW)) {
			model.addAttribute("authorityCollection", authorityCollection);
			model.addAttribute("skdKey", skdKey);
			return "/room/assign/assign_view";
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
		return "/admin/manage/notice_manage";
	}
}
