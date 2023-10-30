package egov.framework.plms.sub.lime.bean.mvc.controller.meeting;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.ws.rs.NotFoundException;

import org.springframework.context.annotation.Profile;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import egov.framework.plms.main.annotation.NoCache;
import egov.framework.plms.main.bean.component.auth.PassTokenProvider;
import egov.framework.plms.main.bean.component.auth.ResourceAuthorityProvider;
import egov.framework.plms.main.bean.component.monitoring.MeetingMonitoringManager;
import egov.framework.plms.main.bean.component.properties.ReserveConfigProperties;
import egov.framework.plms.main.bean.component.session.SessionManager;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingAttendeeVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingScheduleVO;
import egov.framework.plms.main.core.model.auth.ResourceAuthorityCollection;
import egov.framework.plms.main.core.model.enums.auth.MeetingAuth;
import egov.framework.plms.main.core.model.enums.meeting.ApprovalStatus;
import egov.framework.plms.main.core.model.enums.meeting.MeetingStatus;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import egov.framework.plms.main.core.model.enums.meeting.ScheduleType;
import egov.framework.plms.main.core.model.enums.user.LoginType;
import egov.framework.plms.main.core.util.DateTimeUtil;
import egov.framework.plms.sub.ewp.core.model.enums.FileRole;
import egov.framework.plms.sub.lime.bean.mvc.service.meeting.LimeMeetingAttendeeService;
import egov.framework.plms.sub.lime.bean.mvc.service.meeting.LimeMeetingScheduleService;
import egov.framework.plms.sub.lime.core.util.LimeSecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * 회의배정정보(스케줄+설정)에 대한 AJAX 요청 처리 컨트롤러
 * @author mckim
 * @version 1.0
 * @since 2023. 5. 4
 */
@Slf4j
@Controller
@RequestMapping("/lime")
@RequiredArgsConstructor
@Profile("lime")
public class LimeMeetingAssignController {
	private final ResourceAuthorityProvider authorityProvider;
	private final PassTokenProvider tokenProvider;
	private final ReserveConfigProperties reserveProperties;
	
	private final LimeMeetingScheduleService scheServ;
	private final LimeMeetingAttendeeService attServ;
	
	private final SessionManager sessionMng;
	private final MeetingMonitoringManager monitoringMng;
	
	/**
	 * 배정현황 시간표 페이지 
	 * @param authentication
	 * @param model
	 * @param holdingDate 타임테이블 조회 일자. 날짜형식의 값이 아니거나 오늘보다 이전 인 경우 오늘 날짜로 설정.
	 * @return
	 */
	@GetMapping("/meeting/assign/status/timetable")
	public String scheduleAssignmentTimetableForm(Model model,
			@RequestParam @Nullable Integer roomId,
			@RequestParam @Nullable String holdingDate) {
		model.addAttribute("roomId", roomId);
		LocalDate ld = DateTimeUtil.toFormattedDate(holdingDate);
		if(ld.isBefore(LocalDate.now())) {
			ld = LocalDate.now();
		}
		model.addAttribute("holdingDate", ld);
		LocalTime openTime = reserveProperties.getOpenTime();
		LocalTime closeTime = reserveProperties.getCloseTime();
		Integer intervalMinute = reserveProperties.getIntervalMinute();
		model.addAttribute("OPEN_TIME", openTime);
		model.addAttribute("CLOSE_TIME", closeTime);
		model.addAttribute("INTERVAL_MINUTE", intervalMinute);
		return "/meeting/assign/assign_status_timetable";
	}
	
	/**
	 * 사용신청 등록 페이지
	 * @param authentication
	 * @param model
	 * @param roomId
	 * @param holdingDate
	 * @return
	 */
	@NoCache
	@GetMapping("/meeting/assign/post")
	public String postRoomAssignForm(Authentication authentication, Model model, 
			@RequestParam Integer roomId, 
			@RequestParam String holdingDate,
			@RequestParam @Nullable String startTime,
			@RequestParam @Nullable String endTime) {
		model.addAttribute("roomId", roomId);
		model.addAttribute("holdingDate", DateTimeUtil.toFormattedDate(holdingDate));
		LocalTime openTime = reserveProperties.getOpenTime();
		LocalTime closeTime = reserveProperties.getCloseTime();
		Integer intervalMinute = reserveProperties.getIntervalMinute();
		model.addAttribute("OPEN_TIME", openTime);
		model.addAttribute("CLOSE_TIME", closeTime);
		model.addAttribute("INTERVAL_MINUTE", intervalMinute);
		DateTimeUtil.setModelOfTimePeriod(model, startTime, endTime, intervalMinute, openTime, closeTime);
		return "/meeting/assign/assign_write";
	}
	
	/**
	 * 사용신청 수정 페이지
	 * @param authentication
	 * @param model
	 * @param scheduleId
	 * @return
	 */
	@GetMapping("/meeting/assign/{scheduleId}/post")
	public String putRoomAssignForm(Authentication authentication, Model model, 
			@PathVariable Integer scheduleId) {
		Optional<MeetingScheduleVO> opt = scheServ.getMeetingScheduleOne(scheduleId);
		if(!opt.isPresent()) {
			throw new NotFoundException("사용신청 내역을 찾을 수 없습니다.");
		}
		MeetingScheduleVO scheVO = opt.get();
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(scheVO.getMeetingId());
		if(authorityCollection.hasAuthority(MeetingAuth.UPDATE)) {
			model.addAttribute("authorityCollection", authorityCollection.toJson());
			model.addAttribute("roomId", scheVO.getRoomId());
			model.addAttribute("scheduleId", scheduleId);
			LocalTime openTime = reserveProperties.getOpenTime();
			LocalTime closeTime = reserveProperties.getCloseTime();
			Integer intervalMinute = reserveProperties.getIntervalMinute();
			model.addAttribute("OPEN_TIME", openTime);
			model.addAttribute("CLOSE_TIME", closeTime);
			model.addAttribute("INTERVAL_MINUTE", intervalMinute);
			return "/meeting/assign/assign_write";
		}else {
			throw new AccessDeniedException("사용신청에 대한 수정 권한이 없습니다.");
		}
	}
	
	/**
	 * 사용신청 조회 페이지
	 * @param model
	 * @param scheduleId
	 * @return
	 */
	@GetMapping("/meeting/assign/{scheduleId}")
	public String viewRoomAssignForm(HttpSession session, Model model, @PathVariable Integer scheduleId) {
		Optional<MeetingScheduleVO> opt = scheServ.getMeetingScheduleOne(scheduleId);
		if(!opt.isPresent()) {
			throw new NotFoundException("사용신청 내역을 찾을 수 없습니다.");
		}
		MeetingScheduleVO scheVO = opt.get();
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(scheVO.getMeetingId());
		if(authorityCollection.hasAuthority(MeetingAuth.VIEW)) {
			model.addAttribute("authorityCollection", authorityCollection);
			LoginType loginType = LimeSecurityUtil.getLoginType();
			if(scheVO.getScheduleType() != ScheduleType.RENTAL && scheVO.getApprovalStatus() == ApprovalStatus.APPROVED && loginType == LoginType.PLTE && LocalDateTime.now().isBefore(scheVO.getBeginDateTime())) {
				String token = tokenProvider.createToken(LimeSecurityUtil.getLoginId(), scheVO.getMeetingId().toString(), scheVO.getBeginDateTime(), new HashSet<>(Arrays.asList(
					MeetingAuth.ATTEND
				)));
				session.setAttribute("passToken", token);
				return viewMeetingPage(session, model, scheVO);
			}
			if(authorityCollection.hasAuthority(MeetingAuth.ATTEND)) {
				return viewMeetingPage(session, model, scheVO);
			}else {
				return viewInfoPage(model, scheVO);
			}
		}else {
			throw new AccessDeniedException("사용신청에 대한 조회 권한이 없습니다.");
		}
	}
	
	private String viewInfoPage(Model model, MeetingScheduleVO scheVO) {
		ResourceAuthorityCollection reportAuthorityCollection = authorityProvider.getMeetingAuthorityCollection(scheVO.getMeetingId());
		model.addAttribute("reportAuthorityCollection", reportAuthorityCollection);
		model.addAttribute("scheduleId", scheVO.getScheduleId());
		model.addAttribute("meetingId", scheVO.getMeetingId());
		return "/meeting/assign/assign_view";
	}
	
	private String viewMeetingPage(HttpSession session, Model model, MeetingScheduleVO scheVO) {
		Integer meetingId = scheVO.getMeetingId();
		model.addAttribute("scheduleId", scheVO.getScheduleId());
		model.addAttribute("meetingId", scheVO.getMeetingId());
		LocalTime openTime = reserveProperties.getOpenTime();
		LocalTime closeTime = reserveProperties.getCloseTime();
		Integer intervalMinute = reserveProperties.getIntervalMinute();
		model.addAttribute("OPEN_TIME", openTime);
		model.addAttribute("CLOSE_TIME", closeTime);
		model.addAttribute("INTERVAL_MINUTE", intervalMinute);
		
		LocalTime endTime = closeTime;
		LocalDateTime dayEndTime = LocalDateTime.of(LocalDate.now(), endTime);
		Integer newTimeout = (int) ChronoUnit.SECONDS.between(LocalDateTime.now(), dayEndTime) + session.getMaxInactiveInterval();
		session.setMaxInactiveInterval(newTimeout);
		model.addAttribute("sessionId", session.getId());
		
		monitoringMng.startMonitoring(meetingId);
		sessionMng.setSessionOwner(session.getId(), LimeSecurityUtil.getAuthenticationDetails());
		attServ.putMeetingAttendee(MeetingAttendeeVO.builder().userId(LimeSecurityUtil.getLoginId()).meetingId(meetingId).attendYN('Y').build());
		return "/meeting/room";
	}
	
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
	 * @param scheduleHost 회의 주관자. 조회시 동일 검색이 아닌 포함 검색 적용
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
			@RequestParam @Nullable String scheduleHost,
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
		model.addAttribute("scheduleHost", scheduleHost);
		model.addAttribute("attendeeName", attendeeName);
		return "/manage/assign/assign_user";
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
	 * @param scheduleHost 회의 주관자. 조회시 동일 검색이 아닌 포함 검색 적용
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
			@RequestParam @Nullable String scheduleHost,
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
		model.addAttribute("scheduleHost", scheduleHost);
		model.addAttribute("attendeeName", attendeeName);
		return "/manage/assign/assign_dept";
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
		return "/manage/archive/archive_user";
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
		return "/manage/archive/archive_dept";
	}
}
