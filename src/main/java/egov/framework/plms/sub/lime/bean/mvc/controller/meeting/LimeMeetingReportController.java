package egov.framework.plms.sub.lime.bean.mvc.controller.meeting;

import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingScheduleVO;
import egov.framework.plms.main.core.exception.ApiNotFoundException;
import egov.framework.plms.main.core.model.auth.ResourceAuthorityCollection;
import egov.framework.plms.main.core.model.enums.auth.ReportAuth;
import egov.framework.plms.main.core.model.enums.error.ErrorCode;
import egov.framework.plms.sub.lime.bean.component.auth.LimeResourceAuthorityProvider;
import egov.framework.plms.sub.lime.bean.mvc.service.meeting.LimeMeetingScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/lime")
@RequiredArgsConstructor
@Profile("lime")
public class LimeMeetingReportController {
	private final LimeMeetingScheduleService scheServ;
	private final LimeResourceAuthorityProvider authorityProvider;
	/**
	 * 회의록 조회 페이지
	 * @param authentication
	 * @param meetingId
	 * @param model
	 * @return
	 */
	@GetMapping("/meeting/{meetingId}/report")
	public String viewReportForm(Authentication authentication, @PathVariable Integer meetingId, Model model) {
		Optional<MeetingScheduleVO> scheOpt = scheServ.getAssignedMeetingScheduleOne(meetingId);
		if(!scheOpt.isPresent()) {
			throw new ApiNotFoundException(ErrorCode.MEETING_ASSIGN.NOT_FOUND);
		}
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingReportAuthorityCollection(meetingId);
		if(authorityCollection.hasAuthority(ReportAuth.UPDATE)) {
			return "redirect:/lime/meeting/"+meetingId+"/report/post";
		}else if(authorityCollection.hasAuthority(ReportAuth.VIEW)) {
			model.addAttribute("authorityCollection", authorityCollection);
			model.addAttribute("scheduleId", scheOpt.get().getScheduleId());
			model.addAttribute("meetingId", meetingId);
			return "/meeting/report/report_view";
		}else {
			throw new AccessDeniedException("");
		}
	}
	
	/**
	 * 회의록 작성 페이지
	 * @param authentication
	 * @param meetingId
	 * @param model
	 * @return
	 */
	@GetMapping("/meeting/{meetingId}/report/post")
	public String postReportForm(Authentication authentication, @PathVariable Integer meetingId, Model model) {
		Optional<MeetingScheduleVO> scheOpt = scheServ.getAssignedMeetingScheduleOne(meetingId);
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingReportAuthorityCollection(meetingId);
		if(authorityCollection.hasAuthority(ReportAuth.UPDATE)) {
			model.addAttribute("authorityCollection", authorityCollection);
			model.addAttribute("scheduleId", scheOpt.get().getScheduleId());
			model.addAttribute("meetingId", meetingId);
			return "/meeting/report/report_write";
		}else if(authorityCollection.hasAuthority(ReportAuth.VIEW)) {
			return "redirect:/lime/meeting/"+meetingId+"/report";
		}else {
			throw new AccessDeniedException("");
		}
	}

}
