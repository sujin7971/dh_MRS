package egov.framework.plms.sub.ewp.bean.mvc.controller.meeting;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

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
import egov.framework.plms.main.bean.component.monitoring.MeetingMonitoringManager;
import egov.framework.plms.main.bean.component.session.SessionManager;
import egov.framework.plms.main.core.exception.ApiDataAccessDeniedException;
import egov.framework.plms.main.core.model.auth.ResourceAuthorityCollection;
import egov.framework.plms.main.core.model.enums.auth.MeetingAuth;
import egov.framework.plms.main.core.model.enums.error.ErrorCode;
import egov.framework.plms.main.core.model.enums.meeting.ApprovalStatus;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import egov.framework.plms.main.core.model.enums.meeting.ScheduleType;
import egov.framework.plms.main.core.model.enums.user.LoginType;
import egov.framework.plms.main.core.model.enums.user.ManagerRole;
import egov.framework.plms.main.core.util.DateTimeUtil;
import egov.framework.plms.sub.ewp.bean.component.auth.EwpResourceAuthorityProvider;
import egov.framework.plms.sub.ewp.bean.component.properties.EwpPropertiesProvider;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingAssignVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingAttendeeVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingScheduleVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingSecurityAgreementVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.room.EwpRoomInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.service.common.EwpCodeService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingAssignService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingAttendeeService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingScheduleService;
import egov.framework.plms.sub.ewp.bean.mvc.service.room.EwpMariaRoomInfoService;
import egov.framework.plms.sub.ewp.core.util.EwpSecurityUtil;
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
@RequestMapping("/ewp")
@RequiredArgsConstructor
@Profile("ewp")
public class EwpMeetingAssignController {
	private final EwpResourceAuthorityProvider authorityProvider;
	private final PassTokenProvider tokenProvider;
	private final EwpPropertiesProvider propertiesProvider;
	
	private final EwpCodeService codeServ;
	private final EwpMeetingAssignService assignServ;
	private final EwpMeetingScheduleService scheServ;
	private final EwpMeetingAttendeeService attServ;
	private final EwpMariaRoomInfoService roomServ;
	
	private final SessionManager sessionMng;
	private final MeetingMonitoringManager monitoringMng;
	/**
	 * 배정현황 목록 페이지
	 * @param authentication
	 * @param model
	 * @param holdingDate 배정현황 조회 시작일자. 날짜형식의 값이 아니거나 오늘보다 이전 인 경우 오늘 날짜로 설정.
	 * @return
	 */
	@NoCache
	@GetMapping("/meeting/assign/status/list")
	public String scheduleAssignmentListForm(Model model,
			@RequestParam @Nullable RoomType roomType,
			@RequestParam @Nullable Integer roomKey,
			@RequestParam @Nullable String holdingDate) {
		model.addAttribute("officeBook", codeServ.getOfficeBook());
		model.addAttribute("roomType", roomType);
		model.addAttribute("roomKey", roomKey);
		LocalDate ld = DateTimeUtil.toFormattedDate(holdingDate);
		if(ld.isBefore(LocalDate.now())) {
			ld = LocalDate.now();
		}
		model.addAttribute("holdingDate", ld);
		model.addAttribute("startDate", ld);
		model.addAttribute("endDate", ld.plusDays(7));
		return "/meeting/assign/assign_status_list";
	}
	
	/**
	 * 배정현황 시간표 페이지 
	 * @param authentication
	 * @param model
	 * @param holdingDate 타임테이블 조회 일자. 날짜형식의 값이 아니거나 오늘보다 이전 인 경우 오늘 날짜로 설정.
	 * @return
	 */
	@GetMapping("/meeting/assign/status/timetable")
	public String scheduleAssignmentTimetableForm(Model model,
			@RequestParam @Nullable RoomType roomType,
			@RequestParam @Nullable Integer roomKey,
			@RequestParam @Nullable String holdingDate) {
		model.addAttribute("officeBook", codeServ.getOfficeBook());
		model.addAttribute("roomType", roomType);
		model.addAttribute("roomKey", roomKey);
		LocalDate ld = DateTimeUtil.toFormattedDate(holdingDate);
		if(ld.isBefore(LocalDate.now())) {
			ld = LocalDate.now();
		}
		model.addAttribute("holdingDate", ld);
		LocalTime openTime = propertiesProvider.getReserveProperties().getOpenTime();
		LocalTime closeTime = propertiesProvider.getReserveProperties().getCloseTime();
		Integer intervalMinute = propertiesProvider.getReserveProperties().getIntervalMinute();
		model.addAttribute("OPEN_TIME", openTime);
		model.addAttribute("CLOSE_TIME", closeTime);
		model.addAttribute("INTERVAL_MINUTE", intervalMinute);
		return "/meeting/assign/assign_status_timetable";
	}
	
	/**
	 * 사용신청 등록 페이지
	 * @param authentication
	 * @param model
	 * @param roomType
	 * @param roomKey
	 * @param holdingDate
	 * @return
	 */
	@NoCache
	@GetMapping("/meeting/assign/post")
	public String postRoomAssignForm(Authentication authentication, Model model, 
			@RequestParam RoomType roomType, 
			@RequestParam Integer roomKey, 
			@RequestParam String holdingDate,
			@RequestParam @Nullable String startTime,
			@RequestParam @Nullable String endTime) {
		if(!EwpSecurityUtil.hasPosition(ManagerRole.getRoomManagerRole(roomType))) {
			try {
				String deptId = EwpSecurityUtil.getDeptId();
				boolean isRoomPermitted = roomServ.isRoomPermitted(deptId, roomType, roomKey);
				if(!isRoomPermitted) {
					throw new ApiDataAccessDeniedException(ErrorCode.ROOM.NOT_PERMITTED);
				}
			}catch(Exception e) {
				throw e;
			}
		}
		model.addAttribute("mode", "post");
		model.addAttribute("officeBook", codeServ.getOfficeBook());
		model.addAttribute("roomType", roomType);
		model.addAttribute("roomKey", roomKey);
		model.addAttribute("holdingDate", DateTimeUtil.toFormattedDate(holdingDate));
		EwpRoomInfoVO roomVO = roomServ.selectRoomOne(roomType, roomKey).get();
		LocalTime openTime = roomVO.getOpenTime();
		LocalTime closeTime = roomVO.getCloseTime();
		Integer intervalMinute = propertiesProvider.getReserveProperties().getIntervalMinute();
		model.addAttribute("OPEN_TIME", openTime);
		model.addAttribute("CLOSE_TIME", closeTime);
		model.addAttribute("INTERVAL_MINUTE", intervalMinute);
		DateTimeUtil.setModelOfTimePeriod(model, startTime, endTime, intervalMinute, openTime, closeTime);
		return "/meeting/assign/assign_post";
	}
	
	/**
	 * 사용신청 수정 페이지
	 * @param authentication
	 * @param model
	 * @param skdKey
	 * @return
	 */
	@GetMapping("/meeting/assign/{skdKey}/post")
	public String putRoomAssignForm(Authentication authentication, Model model, 
			@PathVariable Integer skdKey) {
		Optional<EwpMeetingScheduleVO> opt = scheServ.getMeetingScheduleOne(skdKey);
		if(!opt.isPresent()) {
			throw new NotFoundException("사용신청 내역을 찾을 수 없습니다.");
		}
		EwpMeetingScheduleVO scheVO = opt.get();
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(scheVO.getMeetingKey());
		if(authorityCollection.hasAuthority(MeetingAuth.UPDATE)) {
			model.addAttribute("mode", "put");
			model.addAttribute("authorityCollection", authorityCollection.toJson());
			model.addAttribute("officeBook", codeServ.getOfficeBook());
			model.addAttribute("roomType", scheVO.getRoomType());
			model.addAttribute("roomKey", scheVO.getRoomKey());
			model.addAttribute("reqKey", scheVO.getReqKey());
			model.addAttribute("skdKey", skdKey);
			EwpRoomInfoVO roomVO = roomServ.selectRoomOne(scheVO.getRoomType(), scheVO.getRoomKey()).get();
			LocalTime openTime = roomVO.getOpenTime();
			LocalTime closeTime = roomVO.getCloseTime();
			Integer intervalMinute = propertiesProvider.getReserveProperties().getIntervalMinute();
			model.addAttribute("OPEN_TIME", openTime);
			model.addAttribute("CLOSE_TIME", closeTime);
			model.addAttribute("INTERVAL_MINUTE", intervalMinute);
			return "/meeting/assign/assign_post";
		}else {
			throw new AccessDeniedException("사용신청에 대한 수정 권한이 없습니다.");
		}
	}
	
	/**
	 * 사용신청 조회 페이지
	 * @param model
	 * @param skdKey
	 * @return
	 */
	@GetMapping("/meeting/assign/{skdKey}")
	public String viewRoomAssignForm(HttpSession session, Model model, @PathVariable Integer skdKey) {
		Optional<EwpMeetingAssignVO> opt = assignServ.getMeetingAssignOneByScheduleId(skdKey);
		if(!opt.isPresent()) {
			throw new NotFoundException("사용신청 내역을 찾을 수 없습니다.");
		}
		EwpMeetingAssignVO assignVO = opt.get();
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(assignVO.getMeetingKey());
		if(authorityCollection.hasAuthority(MeetingAuth.VIEW)) {
			model.addAttribute("authorityCollection", authorityCollection);
			model.addAttribute("skdKey", assignVO.getSkdKey());
			model.addAttribute("meetingKey", assignVO.getMeetingId());
			String loginId = EwpSecurityUtil.getLoginId();
			LoginType loginType = EwpSecurityUtil.getLoginType();
			if(assignVO.getSkdType() != ScheduleType.RENTAL && assignVO.getApprovalStatus() == ApprovalStatus.APPROVED && loginType == LoginType.PLTE && LocalDateTime.now().isBefore(assignVO.getBeginDateTime())) {
				String token = tokenProvider.createToken(loginId, assignVO.getMeetingKey().toString(), assignVO.getBeginDateTime(), new HashSet<>(Arrays.asList(
					MeetingAuth.ATTEND
				)));
				session.setAttribute("passToken", token);
				return forwardMeetingPage(session, model, assignVO);
			}
			if(authorityCollection.hasAuthority(MeetingAuth.ATTEND)) {
				if(assignVO.getSecretYN() == 'Y') {
					Integer attendKey = attServ.getAttendId(loginId, assignVO.getMeetingKey());
					Optional<EwpMeetingSecurityAgreementVO> agreement = attServ.selectMeetingSecurityAgreementOne(attendKey);
					if(!agreement.isPresent()) {
						return forwardSecurityAgreementPage();
					}
				}
				return forwardMeetingPage(session, model, assignVO);
			}else {
				return forwardInfoPage(model, assignVO);
			}
		}else {
			throw new AccessDeniedException("사용신청에 대한 조회 권한이 없습니다.");
		}
	}
	
	@GetMapping("/meeting/assign/{skdKey}/security-agreemenet")
	public String viewSecurityAgreemenetForm(HttpSession session, Model model, @PathVariable Integer skdKey) {
		return forwardSecurityAgreementPage();
	}
	
	private String forwardInfoPage(Model model, EwpMeetingAssignVO assignVO) {
		ResourceAuthorityCollection reportAuthorityCollection = authorityProvider.getMeetingReportAuthorityCollection(assignVO.getMeetingKey());
		model.addAttribute("reportAuthorityCollection", reportAuthorityCollection);
		model.addAttribute("officeList", codeServ.getOfficeBook());
		return "/meeting/assign/assign_view";
	}
	
	private String forwardMeetingPage(HttpSession session, Model model, EwpMeetingAssignVO assignVO) {
		Integer meetingKey = assignVO.getMeetingKey();
		EwpRoomInfoVO roomVO = roomServ.selectRoomOne(assignVO.getRoomType(), assignVO.getRoomKey()).get();
		LocalTime openTime = roomVO.getOpenTime();
		LocalTime closeTime = roomVO.getCloseTime();
		Integer intervalMinute = propertiesProvider.getReserveProperties().getIntervalMinute();
		model.addAttribute("OPEN_TIME", openTime);
		model.addAttribute("CLOSE_TIME", closeTime);
		model.addAttribute("INTERVAL_MINUTE", intervalMinute);
		
		LocalTime endTime = closeTime;
		LocalDateTime dayEndTime = LocalDateTime.of(LocalDate.now(), endTime);
		Integer newTimeout = (int) ChronoUnit.SECONDS.between(LocalDateTime.now(), dayEndTime) + session.getMaxInactiveInterval();
		session.setMaxInactiveInterval(newTimeout);
		model.addAttribute("sessionId", session.getId());
		
		monitoringMng.startMonitoring(meetingKey);
		sessionMng.setSessionOwner(session.getId(), EwpSecurityUtil.getAuthenticationDetails());
		attServ.putMeetingAttendee(EwpMeetingAttendeeVO.builder().userKey(EwpSecurityUtil.getLoginId()).meetingKey(meetingKey).attendYN('Y').build());
		return "/meeting/meeting_space";
	}
	
	private String forwardSecurityAgreementPage() {
		return "/meeting/security-agreement";
	}
}
