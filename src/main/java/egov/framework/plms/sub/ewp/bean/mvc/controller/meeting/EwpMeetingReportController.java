package egov.framework.plms.sub.ewp.bean.mvc.controller.meeting;

import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import egov.framework.plms.main.core.exception.ApiNotFoundException;
import egov.framework.plms.main.core.model.auth.ResourceAuthorityCollection;
import egov.framework.plms.main.core.model.enums.auth.ReportAuth;
import egov.framework.plms.main.core.model.enums.error.ErrorCode;
import egov.framework.plms.sub.ewp.bean.component.auth.EwpResourceAuthorityProvider;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingScheduleVO;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/ewp")
@RequiredArgsConstructor
@Profile("ewp")
public class EwpMeetingReportController {
	private final EwpMeetingScheduleService scheServ;
	private final EwpResourceAuthorityProvider authorityProvider;
	/**
	 * 회의록 조회 페이지
	 * @param authentication
	 * @param meetingKey
	 * @param model
	 * @return
	 */
	@GetMapping("/meeting/{meetingKey}/report")
	public String viewReportForm(Authentication authentication, @PathVariable Integer meetingKey, Model model) {
		Optional<EwpMeetingScheduleVO> scheOpt = scheServ.getAssignedMeetingScheduleOne(meetingKey);
		if(!scheOpt.isPresent()) {
			throw new ApiNotFoundException(ErrorCode.MEETING_ASSIGN.NOT_FOUND);
		}
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingReportAuthorityCollection(meetingKey);
		if(authorityCollection.hasAuthority(ReportAuth.UPDATE)) {
			return "redirect:/ewp/meeting/"+meetingKey+"/report/post";
		}else if(authorityCollection.hasAuthority(ReportAuth.VIEW)) {
			model.addAttribute("authorityCollection", authorityCollection);
			model.addAttribute("skdKey", scheOpt.get().getSkdKey());
			model.addAttribute("meetingKey", meetingKey);
			return "/meeting/report/report_view";
		}else {
			throw new AccessDeniedException("");
		}
	}
	
	/**
	 * 회의록 작성 페이지
	 * @param authentication
	 * @param meetingKey
	 * @param model
	 * @return
	 */
	@GetMapping("/meeting/{meetingKey}/report/post")
	public String postReportForm(Authentication authentication, @PathVariable Integer meetingKey, Model model) {
		Optional<EwpMeetingScheduleVO> scheOpt = scheServ.getAssignedMeetingScheduleOne(meetingKey);
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingReportAuthorityCollection(meetingKey);
		if(authorityCollection.hasAuthority(ReportAuth.UPDATE)) {
			model.addAttribute("authorityCollection", authorityCollection);
			model.addAttribute("skdKey", scheOpt.get().getSkdKey());
			model.addAttribute("meetingKey", meetingKey);
			return "/meeting/report/report_write";
		}else if(authorityCollection.hasAuthority(ReportAuth.VIEW)) {
			return "redirect:/ewp/meeting/"+meetingKey+"/report";
		}else {
			throw new AccessDeniedException("");
		}
	}

}
