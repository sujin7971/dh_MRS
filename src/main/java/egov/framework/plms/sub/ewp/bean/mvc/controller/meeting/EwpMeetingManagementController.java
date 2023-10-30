package egov.framework.plms.sub.ewp.bean.mvc.controller.meeting;

import org.springframework.context.annotation.Profile;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import egov.framework.plms.main.core.model.enums.meeting.ApprovalStatus;
import egov.framework.plms.main.core.model.enums.meeting.MeetingStatus;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import egov.framework.plms.main.core.util.DateTimeUtil;
import egov.framework.plms.sub.ewp.core.model.enums.FileRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/ewp")
@RequiredArgsConstructor
@Profile("ewp")
public class EwpMeetingManagementController {
	/**
	 * 개인 사용 목록 페이지 요청
	 * @param authentication
	 * @param model
	 * @param roomType {@link RoomType#ALL_ROOM}, {@link RoomType#MEETING_ROOM}, {@link RoomType#EDU_ROOM}
	 * @param approvalStatus {@link ApprovalStatus#REQUEST}, {@link ApprovalStatus#APPROVED}, {@link ApprovalStatus#CANCEL}, {@link ApprovalStatus#REJECT}
	 * @param meetingStatus 회의 진행상태 {@link MeetingStatus#UNAPPROVAL}, {@link MeetingStatus#APPROVED}, {@link MeetingStatus#START}, {@link MeetingStatus#END}, {@link MeetingStatus#DROP}, {@link MeetingStatus#CANCEL}
	 * @param startDate 기간 검색을 위한 시작일자(yyyy-MM-dd)
	 * @param endDate 기간 검색을 위한 종료일자(yyyy-MM-dd)
	 * @param title 회의 제목. 조회시 동일 검색이 아닌 포함 검색 적용
	 * @param host 회의 주관자. 조회시 동일 검색이 아닌 포함 검색 적용
	 * @param attendeeName 참석자명. 조회시 동일 검색이 아닌 포함 검색 적용
	 * @return
	 */
	@GetMapping("/meeting/assign/manage/user")
	public String userMeetingAssignForm(Model model, 
			@RequestParam @Nullable RoomType roomType,
			@RequestParam @Nullable ApprovalStatus approvalStatus,
			@RequestParam @Nullable MeetingStatus meetingStatus,
			@RequestParam @Nullable Character elecYN,
			@RequestParam @Nullable Character secretYN,
			@RequestParam @Nullable String title,
			@RequestParam @Nullable String host,
			@RequestParam @Nullable String attendeeName,
			@RequestParam @Nullable String startDate, 
			@RequestParam @Nullable String endDate) {
		model.addAttribute("approvalStatus", approvalStatus);
		model.addAttribute("meetingStatus", meetingStatus);
		DateTimeUtil.setModelOfDatePeriod(model, startDate, endDate);
		model.addAttribute("roomType", roomType);
		model.addAttribute("elecYN", elecYN);
		model.addAttribute("secretYN", secretYN);
		model.addAttribute("title", title);
		model.addAttribute("host", host);
		model.addAttribute("attendeeName", attendeeName);
		return "/manage/assign/history_user";
	}
	
	/**
	 * 부서 사용 목록 페이지 요청
	 * @param authentication
	 * @param model
	 * @param roomType {@link RoomType#ALL_ROOM}, {@link RoomType#MEETING_ROOM}, {@link RoomType#EDU_ROOM}
	 * @param approvalStatus {@link ApprovalStatus#REQUEST}, {@link ApprovalStatus#APPROVED}, {@link ApprovalStatus#CANCEL}, {@link ApprovalStatus#REJECT}
	 * @param meetingStatus 회의 진행상태 {@link MeetingStatus#UNAPPROVAL}, {@link MeetingStatus#APPROVED}, {@link MeetingStatus#START}, {@link MeetingStatus#END}, {@link MeetingStatus#DROP}, {@link MeetingStatus#CANCEL}
	 * @param startDate 기간 검색을 위한 시작일자(yyyy-MM-dd)
	 * @param endDate 기간 검색을 위한 종료일자(yyyy-MM-dd)
	 * @param title 회의 제목. 조회시 동일 검색이 아닌 포함 검색 적용
	 * @param host 회의 주관자. 조회시 동일 검색이 아닌 포함 검색 적용
	 * @param attendeeName 참석자명. 조회시 동일 검색이 아닌 포함 검색 적용
	 * @return
	 */
	@GetMapping("/meeting/assign/manage/dept")
	public String deptMeetingAssignForm(Model model, 
			@RequestParam @Nullable RoomType roomType,
			@RequestParam @Nullable ApprovalStatus approvalStatus,
			@RequestParam @Nullable MeetingStatus meetingStatus,
			@RequestParam @Nullable Character elecYN,
			@RequestParam @Nullable Character secretYN,
			@RequestParam @Nullable String title,
			@RequestParam @Nullable String host,
			@RequestParam @Nullable String attendeeName,
			@RequestParam @Nullable String startDate, 
			@RequestParam @Nullable String endDate) {
		model.addAttribute("approvalStatus", approvalStatus);
		model.addAttribute("meetingStatus", meetingStatus);
		DateTimeUtil.setModelOfDatePeriod(model, startDate, endDate);
		model.addAttribute("roomType", roomType);
		model.addAttribute("elecYN", elecYN);
		model.addAttribute("secretYN", secretYN);
		model.addAttribute("title", title);
		model.addAttribute("host", host);
		model.addAttribute("attendeeName", attendeeName);
		return "/manage/assign/history_dept";
	}
	
	/**
	 * 개인 파일함 페이지 요청
	 * @param authentication
	 * @param model
	 * @param searchTarget 검색 대상 {@link MeetingArchiveVO#title}, {@link MeetingArchiveVO#skdHost}, {@link MeetingArchiveVO#originalName} 
	 * @param searchWord 검색어
	 * @param roleType {@link FileRole#COPY} 유형 파일 포함 여부
	 * @param startDate 기간 검색을 위한 시작일자(yyyy-MM-dd)
	 * @param endDate 기간 검색을 위한 종료일자(yyyy-MM-dd)
	 * @return
	 */
	@GetMapping("/meeting/archive/manage/user")
	public String userMeetingArchiveForm(Model model, 
			@RequestParam @Nullable String searchTarget, 
			@RequestParam @Nullable String searchWord, 
			@PathVariable @Nullable FileRole roleType, 
			@RequestParam @Nullable String startDate, 
			@RequestParam @Nullable String endDate) {
		model.addAttribute("searchTarget", searchTarget);
		model.addAttribute("searchWord", searchWord);
		model.addAttribute("roleType", roleType);
		model.addAttribute("nav", "archiveNav");
		DateTimeUtil.setModelOfDatePeriod(model, startDate, endDate);
		return "/manage/assign/archive_user";
	}
	
	/**
	 * 부서 파일함 페이지 요청
	 * @param authentication
	 * @param model
	 * @param searchTarget 검색 대상 {@link MeetingArchiveVO#title}, {@link MeetingArchiveVO#skdHost}, {@link MeetingArchiveVO#originalName} 
	 * @param searchWord 검색어
	 * @param startDate 기간 검색을 위한 시작일자(yyyy-MM-dd)
	 * @param endDate 기간 검색을 위한 종료일자(yyyy-MM-dd)
	 * @return
	 */
	@GetMapping("/meeting/archive/manage/dept")
	public String deptMeetingArchiveForm(Model model, 
			@RequestParam @Nullable String searchTarget, 
			@RequestParam @Nullable String searchWord, 
			@RequestParam @Nullable String startDate, 
			@RequestParam @Nullable String endDate) {
		model.addAttribute("searchTarget", searchTarget);
		model.addAttribute("searchWord", searchWord);
		model.addAttribute("nav", "archiveNav");
		DateTimeUtil.setModelOfDatePeriod(model, startDate, endDate);
		return "/manage/assign/archive_dept";
	}
}
