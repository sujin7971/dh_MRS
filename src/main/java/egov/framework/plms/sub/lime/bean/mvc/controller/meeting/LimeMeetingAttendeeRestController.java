package egov.framework.plms.sub.lime.bean.mvc.controller.meeting;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Profile;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingAttendeeDTO;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingAttendeeVO;
import egov.framework.plms.main.bean.mvc.entity.organization.UserInfoVO;
import egov.framework.plms.main.core.model.auth.ResourceAuthorityCollection;
import egov.framework.plms.main.core.model.enums.auth.MeetingAuth;
import egov.framework.plms.main.core.model.enums.error.ErrorCode;
import egov.framework.plms.main.core.model.enums.meeting.AttendRole;
import egov.framework.plms.main.core.model.response.ResponseMessage;
import egov.framework.plms.sub.ewp.core.util.EwpSecurityUtil;
import egov.framework.plms.sub.lime.bean.component.auth.LimeResourceAuthorityProvider;
import egov.framework.plms.sub.lime.bean.mvc.service.meeting.LimeMeetingAttendeeService;
import egov.framework.plms.sub.lime.bean.mvc.service.organization.LimeUserInfoService;
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
@RequestMapping("/api/lime")
@RequiredArgsConstructor
@Profile("lime")
public class LimeMeetingAttendeeRestController {
	private final LimeResourceAuthorityProvider authorityProvider;
	
	private final LimeMeetingAttendeeService attServ;
	private final LimeUserInfoService userServ;
	
	/**
	 * 참석자 일괄 등록/수정/삭제 처리.
	 * @param meetingId 참석자 정보를 갱신할 회의 고유키
	 * @param list 갱신 목록
	 * @return
	 */
	@PostMapping("/meeting/{meetingId}/attendee")
	public ResponseMessage postAttendeeList(Authentication authentication, HttpSession session, @PathVariable Integer meetingId, @RequestBody List<MeetingAttendeeVO> insertList) {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingId);
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
		insertList.forEach(params -> {
			Optional<UserInfoVO> userOpt = userServ.selectUserInfoOne(params.getUserId());
			String userName = userOpt.map(UserInfoVO::getUserName).orElse(null);
			String deptId = userOpt.map(UserInfoVO::getDeptId).orElse(null);
			attServ.postMeetingAttendee(params.toBuilder().meetingId(meetingId).userName(userName).deptId(deptId).build());
		});
		return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
				.message(ResponseMessage.MessageCode.ATTENDEE.POST_SUCCESS.value())
				.build();
	}
	
	@PutMapping("/meeting/{meetingId}/attendee")
	public ResponseMessage updateAttendeeList(Authentication authentication, HttpSession session, @PathVariable Integer meetingId, @RequestBody List<MeetingAttendeeVO> updateList) {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingId);
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
		updateList.forEach(params -> {
			attServ.putMeetingAttendee(params.toBuilder().meetingId(meetingId).build());
		});
		return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
				.message(ResponseMessage.MessageCode.ATTENDEE.POST_SUCCESS.value())
				.build();
	}
	
	@DeleteMapping("/meeting/{meetingId}/attendee")
	public ResponseMessage deleteAttendeeList(Authentication authentication, HttpSession session, @PathVariable Integer meetingId, @RequestBody List<Integer> deleteList) {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingId);
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
		deleteList.forEach(params -> {
			attServ.deleteMeetingAttendeeOne(params);
		});
		return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
				.message(ResponseMessage.MessageCode.ATTENDEE.POST_SUCCESS.value())
				.build();
	}
	
	/**
	 * 출석 체크
	 * @param authentication
	 * @param meetingId 회의 고유키
	 * @param attendId 참석자 고유키
	 * @param attendYN 출석 'Y', 미출석 'N'
	 * @return
	 */
	@PutMapping("/meeting/{meetingId}/attendee/{attendId}/attendance/{attendYN}")
	public ResponseMessage putAttendeeAttendance(Authentication authentication, @PathVariable Integer meetingId, @PathVariable Integer attendId, @PathVariable Character attendYN) {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingId);
		if(authorityCollection.isEmpty()) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.NOT_FOUND)
					.message(ResponseMessage.MessageCode.ATTENDEE.PUT_FAIL.value())
					.detail(ResponseMessage.DetailCode.MEETING.NOT_FOUND.value())
					.build();
		}
		if(authorityCollection.hasAuthority(MeetingAuth.CHECK)) {
			boolean isSucess = attServ.putMeetingAttendee(MeetingAttendeeVO.builder().attendId(attendId).meetingId(meetingId).attendYN(attendYN).build());
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
	 * @param meetingId 회의 고유키
	 * @param attendId 참석자 고유키
	 * @param signYN 사인 'Y', 사인 암함 'N'
	 * @param signSrc base64 문자열 데이터
	 * @return
	 */
	@PutMapping("/meeting/{meetingId}/attendee/{attendId}/sign/{signYN}")
	public ResponseMessage putAttendeeSign(@PathVariable Integer meetingId, @PathVariable Integer attendId, @PathVariable Character signYN, @RequestBody @Nullable Object signSrc) {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingId);
		if(authorityCollection.isEmpty()) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.NOT_FOUND)
					.message(ResponseMessage.MessageCode.ATTENDEE.PUT_FAIL.value())
					.detail(ResponseMessage.DetailCode.MEETING.NOT_FOUND.value())
					.build();
		}
		Optional<MeetingAttendeeVO> voOpt = attServ.getMeetingAttendeeOne(attendId);
		if(authorityCollection.hasAuthority(MeetingAuth.ATTEND)) {
			if(!EwpSecurityUtil.getLoginId().equals(voOpt.get().getUserId())) {
				return ResponseMessage.builder(ResponseMessage.StatusCode.FORBIDDEN)
						.message(ResponseMessage.MessageCode.ATTENDEE.PUT_FAIL.value())
						.detail(ErrorCode.MEETING_ATTENDEE.SIGN_NO_AUTHORITY.getMessage())
						.build();
			}
			String signSrcStr = (signSrc == null)?null:(String) signSrc;
			boolean isSucess = attServ.putMeetingAttendee(MeetingAttendeeVO.builder().attendId(attendId).meetingId(meetingId).signYN(signYN).signSrc(signSrcStr).build());
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
	 * @param meetingId : 보조 진행자를 등록할 회의키
	 * @param assistantYN : 보조 진행자 임명 'Y', 보조 진행자 임명 취소 'N'
	 * @return
	 */
	@PutMapping("/meeting/{meetingId}/attendee/{attendId}/assistant/{assistantYN}")
	public ResponseMessage putMeetingAssistant(Authentication authentication, @PathVariable Integer meetingId, @PathVariable Integer attendId, @PathVariable Character assistantYN) {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingId);
		if(authorityCollection.isEmpty()) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.NOT_FOUND)
					.message(ResponseMessage.MessageCode.MEETING.PUT_FAIL.value())
					.detail(ResponseMessage.DetailCode.MEETING.NOT_FOUND.value())
					.build();
		}
		if(authorityCollection.hasAuthority(MeetingAuth.UPDATE)) {
			boolean isSucess = attServ.putMeetingAttendee(MeetingAttendeeVO.builder().meetingId(meetingId).assistantYN('N').build());
			if(isSucess) {
				attServ.putMeetingAttendee(MeetingAttendeeVO.builder().attendId(attendId).meetingId(meetingId).assistantYN(assistantYN).build());
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
	 * @param attendId 참석자 고유키
	 * @return
	 */
	@GetMapping("/meeting/{meetingId}/attendee/{attendId}")
	public MeetingAttendeeDTO getAttendeeOne(@PathVariable Integer meetingId, @PathVariable Integer attendId) {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingId);
		if(authorityCollection.hasAuthority(MeetingAuth.VIEW)) {
			Optional<MeetingAttendeeVO> attOpt = attServ.getMeetingAttendeeOne(attendId);
			return attOpt.map(vo -> joinUser(vo)).map(MeetingAttendeeVO::convert).orElse(null);
		}else {
			throw new AccessDeniedException(ErrorCode.MEETING_ATTENDEE.READ_NO_AUTHORITY.getMessage());
		}
	}
	
	/**
	 * 해당 회의에 등록된 모든 참석자 리스트 조회
	 * @param meetingId 회의 고유키
	 * @return
	 */
	@GetMapping("/meeting/{meetingId}/attendee/list")
	public List<MeetingAttendeeDTO> getMeetingAttendeeListByMeeting(
			@PathVariable Integer meetingId, 
			@RequestParam @Nullable AttendRole attendRole, 
			@RequestParam @Nullable Character assistantYN, 
			@RequestParam @Nullable Character attendYN, 
			@RequestParam @Nullable Character exitYN, 
			@RequestParam @Nullable Character signYN
		) {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingId);
		if(authorityCollection.hasAuthority(MeetingAuth.VIEW)) {
			List<MeetingAttendeeVO> voList = attServ.getMeetingAttendeeList(
					MeetingAttendeeVO.builder()
					.meetingId(meetingId)
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
	 * @param meetingId 회의 고유키
	 * @return
	 */
	@GetMapping("/meeting/{meetingId}/attendee/list/simple")
	public List<MeetingAttendeeDTO> getMeetingAttendeeSimpleListByMeeting(
			@PathVariable Integer meetingId
		) {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingId);
		if(authorityCollection.hasAuthority(MeetingAuth.VIEW)) {
			List<MeetingAttendeeVO> voList = attServ.getMeetingAttendeeListByMeeting(meetingId);
			return voList.stream().map(e -> joinUser(e).convert().filterForBasic()).collect(Collectors.toList());
		}else {
			throw new AccessDeniedException(ErrorCode.MEETING_ATTENDEE.READ_NO_AUTHORITY.getMessage());
		}
	}
	
	public MeetingAttendeeVO joinUser(MeetingAttendeeVO attVO) {
		return attVO.toBuilder().user(userServ.selectUserInfoOne(attVO.getUserId()).orElse(null)).build();
	}
	
	public MeetingAttendeeDTO getFilteredAttendeeDTO(MeetingAttendeeVO attVO) {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(attVO.getMeetingId());
		return getFilteredAttendeeDTO(authorityCollection, attVO);
	}
	
	public MeetingAttendeeDTO getFilteredAttendeeDTO(ResourceAuthorityCollection authorityCollection, MeetingAttendeeVO attVO) {
		MeetingAttendeeDTO dto = attVO.convert();
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
}
