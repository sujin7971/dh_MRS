package egov.framework.plms.sub.ewp.bean.mvc.controller.meeting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.apache.commons.text.StringEscapeUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import egov.framework.plms.main.bean.mvc.entity.QueryEntity;
import egov.framework.plms.main.core.exception.ApiBadRequestException;
import egov.framework.plms.main.core.exception.ApiDataAccessDeniedException;
import egov.framework.plms.main.core.model.auth.ResourceAuthorityCollection;
import egov.framework.plms.main.core.model.enums.auth.MeetingAuth;
import egov.framework.plms.main.core.model.enums.auth.ReportAuth;
import egov.framework.plms.main.core.model.enums.error.ErrorCode;
import egov.framework.plms.main.core.model.enums.meeting.ApprovalStatus;
import egov.framework.plms.main.core.model.enums.meeting.AttendRole;
import egov.framework.plms.main.core.model.response.ResponseMessage;
import egov.framework.plms.sub.ewp.bean.component.auth.EwpResourceAuthorityProvider;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingAssignVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingAttendeeDTO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingAttendeeVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingSecurityAgreementDTO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingSecurityAgreementVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.organization.EwpUserInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.service.alarm.EwpAlarmWriteService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingAssignService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingAttendeeService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingInfoService;
import egov.framework.plms.sub.ewp.bean.mvc.service.organization.EwpUserInfoService;
import egov.framework.plms.sub.ewp.core.util.EwpSecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 참석자에 대한 AJAX 요청 처리 컨트롤러
 * @author mckim
 * @version 2.0
 * @since 2022. 11. 22
 */
@Slf4j
@RestController
@RequestMapping("/api/ewp")
@RequiredArgsConstructor
@Profile("ewp")
public class EwpMeetingAttendeeRestController {
	private final EwpResourceAuthorityProvider authorityProvider;
	
	private final EwpMeetingAssignService assignServ;
	private final EwpMeetingInfoService meetingServ;
	private final EwpMeetingAttendeeService attServ;
	private final EwpUserInfoService userServ;
	private final EwpAlarmWriteService alarmServ;
	
	/**
	 * 참석자 일괄 등록/수정/삭제 처리.
	 * @param meetingKey 참석자 정보를 갱신할 회의 고유키
	 * @param list 갱신 목록
	 * @return
	 */
	@PostMapping("/meeting/{meetingKey}/attendee")
	public ResponseMessage postAttendeeList(Authentication authentication, HttpSession session, @PathVariable Integer meetingKey, @RequestBody List<QueryEntity<AttendRole>> queryList) {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingKey);
		if(authorityCollection.isEmpty()) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.NOT_FOUND)
					.message(ResponseMessage.MessageCode.ATTENDEE.POST_FAIL.value())
					.detail(ResponseMessage.DetailCode.MEETING.NOT_FOUND.value())
					.build();
		}
		if(!authorityCollection.hasAuthority(MeetingAuth.INVITE)) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.FORBIDDEN)
					.message(ResponseMessage.MessageCode.ATTENDEE.POST_FAIL.value())
					.detail(ResponseMessage.DetailCode.MEETING.FORBIDDEN.value())
					.build();
		}
		List<EwpMeetingAttendeeVO> insertList = new ArrayList<>();
		List<EwpMeetingAttendeeVO> updateList = new ArrayList<>();
		List<EwpMeetingAttendeeVO> deleteList = new ArrayList<>();
		queryList.forEach(query -> {
			switch(query.getOperation()) {
				case CREATE: {
						EwpMeetingAttendeeVO params = EwpMeetingAttendeeVO.builder()
								.meetingKey(meetingKey)
								.userKey(query.getUserKey())
								.userName(userServ.selectUserInfoOne(query.getUserKey()).map(EwpUserInfoVO::getUserName).orElse(null))
								.deptId(query.getDeptId())
								.attendRole(query.getData()).build();
						if(attServ.postMeetingAttendee(params)) {
							insertList.add(params);
						}
					}
					break;
				case UPDATE:{
						EwpMeetingAttendeeVO params = EwpMeetingAttendeeVO.builder()
							.meetingKey(meetingKey)
							.userKey(query.getUserKey())
							.attendRole(query.getData()).build();
						if(attServ.putMeetingAttendee(params)) {
							updateList.add(params);
						}
					}
					break;
				case DELETE:{
						EwpMeetingAttendeeVO params = EwpMeetingAttendeeVO.builder()
							.meetingKey(meetingKey)
							.userKey(query.getUserKey())
							.attendRole(query.getData())
							.build();
						Integer attendKey = attServ.getAttendId(params.getUserId(), params.getMeetingId());
						if(attServ.deleteMeetingAttendeeOne(attendKey)) {
							deleteList.add(params);
						}
					}
					break;
				default:
					break;
			}
		});
		EwpMeetingAssignVO assignVO = assignServ.getMeetingAssignOneByMeetingId(meetingKey).orElse(null);

		Character mailYn = assignVO.getMailYN();
		Character smsYn = assignVO.getSmsYN();
		Character messengerYn = assignVO.getMessengerYN();
		if(assignVO.getApprovalStatus() == ApprovalStatus.APPROVED) {
			if(mailYn.equals('Y')) {
				alarmServ.sendAttendeeMail(insertList, "open", meetingKey).isSuccess();
				alarmServ.sendAttendeeMail(updateList, "modi", meetingKey).isSuccess();
				alarmServ.sendAttendeeMail(deleteList, "delete", meetingKey).isSuccess();
			}
			
			if(smsYn.equals('Y')) {
				alarmServ.sendAttendeeSMS(insertList, "open", meetingKey).isSuccess();
				alarmServ.sendAttendeeSMS(updateList, "modi", meetingKey).isSuccess();
				alarmServ.sendAttendeeSMS(deleteList, "delete", meetingKey).isSuccess();
			}
			
			if(messengerYn.equals('Y')) {
				alarmServ.sendAttendeeMessenger(insertList, "open", meetingKey).isSuccess();
				alarmServ.sendAttendeeMessenger(updateList, "modi", meetingKey).isSuccess();
				alarmServ.sendAttendeeMessenger(deleteList, "delete", meetingKey).isSuccess();
			}
			
			// 오늘의 스케줄 페이지 알림은 무조건 insert
			alarmServ.sendAttendeeAlarm(insertList, "open", meetingKey).isSuccess();
			alarmServ.sendAttendeeAlarm(updateList, "modi", meetingKey).isSuccess();
			alarmServ.sendAttendeeAlarm(deleteList, "delete", meetingKey).isSuccess();
			
		}

		return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
				.message(ResponseMessage.MessageCode.ATTENDEE.POST_SUCCESS.value())
				.build();
	}
	
	/**
	 * 출석 체크
	 * @param authentication
	 * @param meetingKey 회의 고유키
	 * @param attendKey 참석자 고유키
	 * @param attendYN 출석 'Y', 미출석 'N'
	 * @return
	 */
	@PutMapping("/meeting/{meetingKey}/attendee/{attendKey}/attendance/{attendYN}")
	public ResponseMessage putAttendeeAttendance(Authentication authentication, @PathVariable Integer meetingKey, @PathVariable Integer attendKey, @PathVariable Character attendYN) {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingKey);
		if(authorityCollection.isEmpty()) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.NOT_FOUND)
					.message(ResponseMessage.MessageCode.ATTENDEE.PUT_FAIL.value())
					.detail(ResponseMessage.DetailCode.MEETING.NOT_FOUND.value())
					.build();
		}
		if(authorityCollection.hasAuthority(MeetingAuth.CHECK)) {
			boolean isSucess = attServ.putMeetingAttendee(EwpMeetingAttendeeVO.builder().attendKey(attendKey).meetingKey(meetingKey).attendYN(attendYN).build());
			if(isSucess) {
				return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
						.message(ResponseMessage.MessageCode.ATTENDEE.PUT_SUCCESS.value())
						.build();
			}else {
				return ResponseMessage.builder(ResponseMessage.StatusCode.UNPROCESSABLE_ENTITY)
						.message(ResponseMessage.MessageCode.ATTENDEE.PUT_FAIL.value())
						.build();
			}
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.FORBIDDEN)
					.message(ResponseMessage.MessageCode.ATTENDEE.PUT_FAIL.value())
					.detail(ErrorCode.MEETING_ATTENDEE.ATTENDANCE_NO_AUTHORITY.getMessage())
					.build();
		}
	}
	
	/**
	 * 사인 입력
	 * @param meetingKey 회의 고유키
	 * @param attendKey 참석자 고유키
	 * @param signYN 사인 'Y', 사인 암함 'N'
	 * @param signSrc base64 문자열 데이터
	 * @return
	 */
	@PutMapping("/meeting/{meetingKey}/attendee/{attendKey}/sign/{signYN}")
	public ResponseMessage putAttendeeSign(@PathVariable Integer meetingKey, @PathVariable Integer attendKey, @PathVariable Character signYN, @RequestBody @Nullable Object signSrc) {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingReportAuthorityCollection(meetingKey);
		if(authorityCollection.isEmpty()) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.NOT_FOUND)
					.message(ResponseMessage.MessageCode.ATTENDEE.PUT_FAIL.value())
					.detail(ResponseMessage.DetailCode.MEETING.NOT_FOUND.value())
					.build();
		}
		Optional<EwpMeetingAttendeeVO> voOpt = attServ.getMeetingAttendeeOne(attendKey);
		if(authorityCollection.hasAuthority(ReportAuth.OPINION) && voOpt.isPresent()) {
			if(!EwpSecurityUtil.getLoginId().equals(voOpt.get().getUserId())) {
				return ResponseMessage.builder(ResponseMessage.StatusCode.FORBIDDEN)
						.message(ResponseMessage.MessageCode.ATTENDEE.PUT_FAIL.value())
						.detail(ErrorCode.MEETING_ATTENDEE.SIGN_NO_AUTHORITY.getMessage())
						.build();
			}
			String signSrcStr = (signSrc == null)?null:(String) signSrc;
			boolean isSucess = attServ.putMeetingAttendee(EwpMeetingAttendeeVO.builder().attendKey(attendKey).meetingKey(meetingKey).signYN(signYN).signSrc(signSrcStr).build());
			if(isSucess) {
				return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
						.message(ResponseMessage.MessageCode.ATTENDEE.PUT_SUCCESS.value())
						.build();
			}else {
				return ResponseMessage.builder(ResponseMessage.StatusCode.UNPROCESSABLE_ENTITY)
						.message(ResponseMessage.MessageCode.ATTENDEE.PUT_FAIL.value())
						.build();
			}
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.FORBIDDEN)
					.message(ResponseMessage.MessageCode.ATTENDEE.PUT_FAIL.value())
					.detail(ResponseMessage.DetailCode.MEETING.FORBIDDEN.value())
					.build();
		}
	}
	
	/**
	 * 보조 진행자 등록/제거. 보조 진행자를 임명하는 경우 기존 보조 진행자는 임명 취소 처리
	 * @param authentication
	 * @param meetingKey : 보조 진행자를 등록할 회의키
	 * @param assistantYN : 보조 진행자 임명 'Y', 보조 진행자 임명 취소 'N'
	 * @return
	 */
	@PutMapping("/meeting/{meetingKey}/attendee/{attendKey}/assistant/{assistantYN}")
	public ResponseMessage putMeetingAssistant(Authentication authentication, @PathVariable Integer meetingKey, @PathVariable Integer attendKey, @PathVariable Character assistantYN) {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingKey);
		if(authorityCollection.isEmpty()) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.NOT_FOUND)
					.message(ResponseMessage.MessageCode.MEETING.PUT_FAIL.value())
					.detail(ResponseMessage.DetailCode.MEETING.NOT_FOUND.value())
					.build();
		}
		if(authorityCollection.hasAuthority(MeetingAuth.UPDATE)) {
			boolean isSucess = attServ.putMeetingAttendee(EwpMeetingAttendeeVO.builder().meetingKey(meetingKey).assistantYN('N').build());
			if(isSucess) {
				attServ.putMeetingAttendee(EwpMeetingAttendeeVO.builder().attendKey(attendKey).meetingKey(meetingKey).assistantYN(assistantYN).build());
				return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
						.message(ResponseMessage.MessageCode.ATTENDEE.PUT_SUCCESS.value())
						.build();
			}else {
				return ResponseMessage.builder(ResponseMessage.StatusCode.UNPROCESSABLE_ENTITY)
						.message(ResponseMessage.MessageCode.ATTENDEE.PUT_FAIL.value())
						.build();
			}
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.FORBIDDEN)
					.message(ResponseMessage.MessageCode.MEETING.PUT_FAIL.value())
					.detail(ResponseMessage.DetailCode.MEETING.FORBIDDEN.value())
					.build();
		}
	}
	
	/**
	 * 참석자 정보 조회
	 * @param attendKey 참석자 고유키
	 * @return
	 */
	@GetMapping("/meeting/{meetingKey}/attendee/{attendKey}")
	public EwpMeetingAttendeeDTO getAttendeeOne(@PathVariable Integer meetingKey, @PathVariable Integer attendKey) {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingKey);
		if(authorityCollection.hasAuthority(MeetingAuth.VIEW)) {
			Optional<EwpMeetingAttendeeVO> attOpt = attServ.getMeetingAttendeeOne(attendKey);
			return attOpt.map(vo -> joinUser(vo)).map(EwpMeetingAttendeeVO::convert).orElse(null);
		}else {
			throw new AccessDeniedException(ErrorCode.MEETING_ATTENDEE.READ_NO_AUTHORITY.getMessage());
		}
	}
	
	/**
	 * 해당 회의에 등록된 모든 참석자 리스트 조회
	 * @param meetingKey 회의 고유키
	 * @return
	 */
	@GetMapping("/meeting/{meetingKey}/attendee/list")
	public List<EwpMeetingAttendeeDTO> getMeetingAttendeeListByMeeting(
			@PathVariable Integer meetingKey, 
			@RequestParam @Nullable AttendRole attendRole, 
			@RequestParam @Nullable Character assistantYN, 
			@RequestParam @Nullable Character attendYN, 
			@RequestParam @Nullable Character exitYN, 
			@RequestParam @Nullable Character signYN
		) {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingKey);
		if(authorityCollection.hasAuthority(MeetingAuth.VIEW)) {
			List<EwpMeetingAttendeeVO> voList = attServ.getMeetingAttendeeList(
					EwpMeetingAttendeeVO.builder()
					.meetingKey(meetingKey)
					.attendRole(attendRole)
					.assistantYN(assistantYN)
					.attendYN(attendYN)
					.exitYN(exitYN)
					.signYN(signYN).build());
			return voList.stream().map(e -> getFilteredAttendeeDTO(authorityCollection, joinUser(e))).collect(Collectors.toList());
		}else {
			throw new AccessDeniedException(ErrorCode.MEETING_ATTENDEE.READ_NO_AUTHORITY.getMessage());
		}
	}
	
	/**
	 * 해당 회의에 등록된 모든 참석자 리스트 단순 조회
	 * @param meetingKey 회의 고유키
	 * @return
	 */
	@GetMapping("/meeting/{meetingKey}/attendee/list/simple")
	public List<EwpMeetingAttendeeDTO> getMeetingAttendeeSimpleListByMeeting(
			@PathVariable Integer meetingKey
		) {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingKey);
		List<EwpMeetingAttendeeVO> voList = attServ.getMeetingAttendeeListByMeeting(meetingKey);
		return voList.stream().map(e -> joinUser(e).convert().filterForBasic()).collect(Collectors.toList());
	}
	
	public EwpMeetingAttendeeVO joinUser(EwpMeetingAttendeeVO attVO) {
		return attVO.toBuilder().user(userServ.selectUserInfoOne(attVO.getUserId()).orElse(null)).build();
	}
	
	public EwpMeetingAttendeeDTO getFilteredAttendeeDTO(EwpMeetingAttendeeVO attVO) {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(attVO.getMeetingKey());
		return getFilteredAttendeeDTO(authorityCollection, attVO);
	}
	
	public EwpMeetingAttendeeDTO getFilteredAttendeeDTO(ResourceAuthorityCollection authorityCollection, EwpMeetingAttendeeVO attVO) {
		EwpMeetingAttendeeDTO dto = attVO.convert();
		if(authorityCollection.hasAuthority(MeetingAuth.UPDATE)) {
			dto = dto.filterForHighest();
		}else if(authorityCollection.hasAuthority(MeetingAuth.VIEW)) {
			dto = dto.filterForSensitive();
		}else if(authorityCollection.hasAuthority(MeetingAuth.READ)) {
			dto = dto.filterForEssential();
		}else {
			dto = null;
		}
		return dto;
	}
	
	@PostMapping("/meeting/{meetingKey}/attendee/security-agreement")
	public ResponseMessage insertSecurityAgreement(@PathVariable Integer meetingKey, @RequestBody Object signSrc) {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingKey);
		if(authorityCollection.hasAuthority(MeetingAuth.ATTEND)) {
			EwpMeetingInfoVO meeting = meetingServ.getMeetingInfoOne(meetingKey).get();
			if(meeting.getSecretYN() == 'N') {
				throw new ApiBadRequestException(ErrorCode.MEETING_ATTENDEE.AGREEMENT_UNAVAILABLE);
			}
			String loginId = EwpSecurityUtil.getLoginId();
			Integer attendKey = attServ.getAttendId(loginId, meetingKey);
			boolean result = attServ.insertMeetingSecurityAgreementOne(attendKey, meetingKey, (String) signSrc);
			if(result) {
				return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
						.message(ResponseMessage.MessageCode.ATTENDEE.AGREEMENT_SUCCESS.value())
						.build();
			}else {
				return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
						.message(ResponseMessage.MessageCode.ATTENDEE.AGREEMENT_SUCCESS.value())
						.build();
			}
		}else {
			throw new ApiDataAccessDeniedException(ErrorCode.MEETING_ATTENDEE.AGREEMENT_NO_AUTHORITY);
		}
	}
	
	@GetMapping("/meeting/{meetingKey}/attendee/{attendKey}/security-agreement")
	public EwpMeetingSecurityAgreementDTO selectSecurityAgreement(@PathVariable Integer meetingKey, @PathVariable Integer attendKey) {
		return attServ.selectMeetingSecurityAgreementOne(attendKey).map(EwpMeetingSecurityAgreementVO::convert).orElse(null);
	}
	
	@GetMapping("/meeting/{meetingKey}/attendee/security-agreement")
	public List<EwpMeetingSecurityAgreementDTO> selectSecurityAgreement(@PathVariable Integer meetingKey) {
		return attServ.selectMeetingSecurityAgreementAll(meetingKey).stream().map(EwpMeetingSecurityAgreementVO::convert).collect(Collectors.toList());
	}
}
