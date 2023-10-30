package egov.framework.plms.sub.ewp.bean.mvc.controller.admin;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import egov.framework.plms.main.bean.mvc.entity.admin.AdminRosterVO;
import egov.framework.plms.main.bean.mvc.entity.common.CodeVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingScheduleVO;
import egov.framework.plms.main.core.exception.ApiDataAccessDeniedException;
import egov.framework.plms.main.core.exception.ApiNotFoundException;
import egov.framework.plms.main.core.model.auth.ResourceAuthorityCollection;
import egov.framework.plms.main.core.model.enums.auth.AssignApprovalAuth;
import egov.framework.plms.main.core.model.enums.auth.MeetingAuth;
import egov.framework.plms.main.core.model.enums.error.ErrorCode;
import egov.framework.plms.main.core.model.enums.meeting.ApprovalStatus;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import egov.framework.plms.main.core.model.response.ResponseMessage;
import egov.framework.plms.main.core.util.DateTimeUtil;
import egov.framework.plms.sub.ewp.bean.component.auth.EwpResourceAuthorityProvider;
import egov.framework.plms.sub.ewp.bean.mvc.entity.assign.EwpRoomReqVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingAssignDTO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingAssignVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingAttendeeDTO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingAttendeeVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.organization.EwpUserInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.service.admin.EwpAdminRosterService;
import egov.framework.plms.sub.ewp.bean.mvc.service.common.EwpCodeService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingApprovalService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingAssignService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingAttendeeService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpTiberoRoomReqService;
import egov.framework.plms.sub.ewp.bean.mvc.service.organization.EwpUserInfoService;
import egov.framework.plms.sub.ewp.bean.mvc.service.room.EwpMariaRoomInfoService;
import egov.framework.plms.sub.ewp.core.util.EwpSecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ewp")
@Profile("ewp")
public class EwpManagerAdminRestController {
	private final EwpResourceAuthorityProvider authorityProvider;
	private final EwpMeetingApprovalService approvalServ;
	private final EwpAdminRosterService rosterServ;
	private final EwpUserInfoService userServ;
	private final EwpCodeService codeServ;
	private final EwpMeetingAssignService assignServ;
	private final EwpMeetingAttendeeService attServ;
	private final EwpMariaRoomInfoService rmServ;
	private final EwpTiberoRoomReqService reqServ;
	/**
	 * 해당 장소에 등록된 사용신청에 대한 결재 요청
	 * @param authentication
	 * @param skdKey {@link MeetingScheduleVO#skdKey}
	 * @param status {@link ApprovalStatus#APPROVED}, {@link ApprovalStatus#CANCEL}, {@link ApprovalStatus#REJECT}, {@link ApprovalStatus#DELETE}
	 * @param comment 결재 요청 코멘트
	 * 
	 * @see {@link ApprovalStatus}
	 * @return
	 */
	@PostMapping("/manager/approval/meeting/assign/{skdKey}/approval/{appStatus}")
	public ResponseMessage postAssignApprovalRequest(@PathVariable Integer skdKey,
			@PathVariable ApprovalStatus appStatus, @RequestParam @Nullable String comment) {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getAssignApprovalAuthorityCollection(skdKey);
		if(authorityCollection.isEmpty()) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.FORBIDDEN)
					.message(ResponseMessage.MessageCode.APPROVAL.POST_FAIL.value())
					.detail(ResponseMessage.DetailCode.ASSIGN.FORBIDDEN.value())
					.build();
		}
		AssignApprovalAuth reqAuth = null;
		switch(appStatus) {
			case DELETE:
				reqAuth = AssignApprovalAuth.DELETE;
				break;
			case APPROVED:
				reqAuth = AssignApprovalAuth.APPROVAL;
				break;
			case CANCELED:
				reqAuth = AssignApprovalAuth.CANCEL;
				break;
			case REJECTED:
				reqAuth = AssignApprovalAuth.REJECT;
				break;
			default:
				reqAuth = AssignApprovalAuth.NONE;
				break;
		}
		if(authorityCollection.hasAuthority(reqAuth)) {
			boolean result = approvalServ.insertApprovalOne(EwpSecurityUtil.getLoginId(), skdKey, appStatus, comment);
			if(result) {
				return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
						.message(ResponseMessage.MessageCode.APPROVAL.POST_SUCCESS.value())
						.build();
			}else {
				return ResponseMessage.builder(ResponseMessage.StatusCode.FORBIDDEN)
				.message(ResponseMessage.MessageCode.APPROVAL.POST_FAIL.value())
				.detail(ResponseMessage.DetailCode.ASSIGN.UNPROCESSABLE_ENTITY.value())
				.build();
			}
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.FORBIDDEN)
					.message(ResponseMessage.MessageCode.APPROVAL.POST_FAIL.value())
					.detail(ResponseMessage.DetailCode.ASSIGN.FORBIDDEN.value())
					.build();
		}
	}
	
	/**
	 * 사업소 결재 승인방식 변경
	 * @param authentication
	 * @param officeCode 사업소 코드
	 * @param autoYN 자동결재 여부
	 * @return
	 */
	@PutMapping("/manager/approval/office/{officeCode}/policy/{autoYN}")
	public ResponseMessage putOfficeAutoApproval(@PathVariable String officeCode, @PathVariable Character autoYN) {
		String loginId = EwpSecurityUtil.getLoginId();
		List<AdminRosterVO> roomManagerList = rosterServ.getAllRoomManagerList(loginId, officeCode);
		if(roomManagerList.isEmpty()) {
			throw new ApiDataAccessDeniedException(ErrorCode.MEETING_APPROVAL.CHANGE_POLICY_NO_AUTHORITY);
		}
		boolean result = codeServ.putOfficeApprovalPolicy(officeCode, autoYN, loginId);
		if(result) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.OK).build();
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST).build();
		}
	}
	
	@PutMapping("/manager/approval/assign/{skdKey}")
	public ResponseMessage putAssignTitleAndHost(
			@PathVariable Integer skdKey, 
			@RequestParam String title,
			@RequestParam String skdHost) {
		String loginId = EwpSecurityUtil.getLoginId();
		Optional<EwpMeetingAssignVO> opt = assignServ.getMeetingAssignOneByScheduleId(skdKey);
		if(!opt.isPresent()) {
			throw new ApiNotFoundException(ErrorCode.MEETING_ASSIGN.NOT_FOUND);
		}
		EwpMeetingAssignVO assignVO = opt.get();
		List<AdminRosterVO> roomManagerList = rosterServ.getAllRoomManagerList(loginId, assignVO.getOfficeCode());
		if(roomManagerList.isEmpty()) {
			throw new ApiDataAccessDeniedException(ErrorCode.MEETING_APPROVAL.CHANGE_DETAILS_NO_AUTHORITY);
		}
		assignVO = assignVO.toBuilder().skdKey(skdKey).meetingKey(assignVO.getMeetingKey()).title(title).skdHost(skdHost).build();
		if(assignVO.getReqKey() != null) {
			EwpRoomReqVO newReqVO = EwpRoomReqVO.generate(assignVO);
			try {
				reqServ.putRoomAssignWithoutValidation(newReqVO.getRoomType(), newReqVO);
			} catch (Exception e) {
				throw new ApiDataAccessDeniedException(ErrorCode.MEETING_ASSIGN.UPDATE_FAILED);
			}
		}
		boolean result = assignServ.putMeetingAssign(assignVO);
		if(result) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.OK).build();
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST).build();
		}
	}
	
	/**
	 * 사업소 결재 승인정책 조회
	 * @param authentication
	 * @param officeCode
	 * @return
	 */
	@GetMapping("/manager/approval/office/{officeCode}/policy")
	public String getOfficeApprovalPolicy(@PathVariable String officeCode) {
		CodeVO codeVO = codeServ.getOfficeComCodeOne(officeCode);
		log.info("사업소 {}의 코드표: {}", officeCode, codeVO.toString());
		return Optional.ofNullable(codeVO).map(CodeVO::getEtcCol1).orElse("AUTH_AUTO");
	}
	
	/**
	 * 사용신청에 대한 정보 조회. 권한별로 다른 필터링을 적용하여 전달
	 * @param authentication
	 * @param skdKey 스케줄키
	 * @return 사용신청 정보 반환
	 */
	@GetMapping("/manager/approval/meeting/assign/{skdKey}")
	public EwpMeetingAssignDTO getMeetingAssignOneForApproval(Model model, @PathVariable Integer skdKey) {
		Optional<EwpMeetingAssignVO> opt = assignServ.getMeetingAssignOneByScheduleId(skdKey);
		if(!opt.isPresent()) {
			throw new ApiNotFoundException(ErrorCode.MEETING_ASSIGN.NOT_FOUND);
		}
		EwpMeetingAssignDTO dto = opt.get().convert();
		ResourceAuthorityCollection authorityCollection = authorityProvider.getAssignApprovalAuthorityCollection(skdKey);
		if(authorityCollection.hasAuthority(MeetingAuth.VIEW)) {
			Optional<EwpUserInfoVO> userOpt = userServ.selectUserInfoOne(dto.getWriterKey());
			if(userOpt.isPresent()) {
				dto.setWriter(userOpt.get().convert());
			}
			List<EwpMeetingAttendeeDTO> attendeeList = attServ.getMeetingAttendeeListByMeeting(dto.getMeetingKey()).stream()
			.map(e -> e.toBuilder().user(userServ.selectUserInfoOne(e.getUserId()).orElse(null)).build())
			.map(EwpMeetingAttendeeVO::convert)
			.map(EwpMeetingAttendeeDTO::filterForBasic)
			.collect(Collectors.toList());
			dto.setAttendeeList(attendeeList);
			return dto;
		}else {
			throw new ApiDataAccessDeniedException(ErrorCode.MEETING_APPROVAL.APPROVAL_NO_AUTHORITY);
		}
	}
	
	public void validationUser(String userId) {
	    Optional<EwpUserInfoVO> userOpt = userServ.selectUserInfoOne(userId);
	    if (!userOpt.isPresent()) {
	        throw new ApiNotFoundException(ErrorCode.USER.NOT_FOUND);
	    }
	}
	
	@GetMapping("/manager/approval/meeting/assign/list")
	public List<EwpMeetingAssignDTO> getMeetingAssignList(Authentication authentication, 
			@RequestParam @Nullable String officeCode, 
			@RequestParam RoomType roomType, 
			@RequestParam @Nullable Integer roomKey, 
			@RequestParam @Nullable ApprovalStatus approvalStatus,
			@RequestParam @Nullable String title,
			@RequestParam @Nullable String host,
			@RequestParam @Nullable String attendeeName,
			@RequestParam @Nullable Character elecYN,
			@RequestParam @Nullable Character secretYN,
			@RequestParam String startDate, 
			@RequestParam String endDate,
			@RequestParam @Nullable Integer pageNo,
			@RequestParam @Nullable Integer pageCnt) {
		LocalDate validSdate = DateTimeUtil.toFormattedDate(startDate);
		LocalDate validEdate = DateTimeUtil.toFormattedDate(endDate);
		List<EwpMeetingAssignVO> voList = assignServ.getMeetingAssignListForApproval(EwpMeetingAssignVO.builder().officeCode(officeCode).roomType(roomType).roomKey(roomKey).appStatus(approvalStatus)
				.title(title).skdHost(host).attendeeName(attendeeName).elecYN(elecYN).secretYN(secretYN)
				.startDate(validSdate).endDate(validEdate)
				.pageNo(pageNo).pageCnt(pageCnt).build());
		return voList.stream().map(EwpMeetingAssignVO::convert).collect(Collectors.toList());
	}
}
