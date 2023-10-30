package egov.framework.plms.sub.ewp.bean.mvc.controller.meeting;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

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

import egov.framework.plms.main.bean.component.auth.PassTokenProvider;
import egov.framework.plms.main.bean.mvc.entity.common.StatDTO;
import egov.framework.plms.main.bean.mvc.entity.common.StatVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingScheduleVO;
import egov.framework.plms.main.core.exception.ApiDataOperationException;
import egov.framework.plms.main.core.exception.abst.ApiException;
import egov.framework.plms.main.core.model.auth.ResourceAuthorityCollection;
import egov.framework.plms.main.core.model.enums.auth.AssignApprovalAuth;
import egov.framework.plms.main.core.model.enums.auth.MeetingAuth;
import egov.framework.plms.main.core.model.enums.common.OrderColumn;
import egov.framework.plms.main.core.model.enums.common.OrderDirection;
import egov.framework.plms.main.core.model.enums.error.ErrorCode;
import egov.framework.plms.main.core.model.enums.meeting.ApprovalStatus;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import egov.framework.plms.main.core.model.enums.user.ManagerRole;
import egov.framework.plms.main.core.model.response.ResponseMessage;
import egov.framework.plms.main.core.util.DateTimeUtil;
import egov.framework.plms.sub.ewp.bean.component.auth.EwpResourceAuthorityProvider;
import egov.framework.plms.sub.ewp.bean.mvc.entity.assign.EwpRoomReqVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingAssignDTO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingAssignVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingAttendeeVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.organization.EwpUserInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.room.EwpRoomInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.service.alarm.EwpAlarmWriteService;
import egov.framework.plms.sub.ewp.bean.mvc.service.file.EwpMeetingFileInfoService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingApprovalService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingAssignService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingAttendeeService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpTiberoRoomReqService;
import egov.framework.plms.sub.ewp.bean.mvc.service.organization.EwpUserInfoService;
import egov.framework.plms.sub.ewp.bean.mvc.service.room.EwpMariaRoomInfoService;
import egov.framework.plms.sub.ewp.core.util.EwpSecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 사용신청에 대한 요청 처리. 티베로와 내장 DB 모두 사용.
 * @author mckim
 * @version 2.0
 * @since 2022. 11. 25
 */
@Slf4j
@RestController
@RequestMapping("/api/ewp")
@RequiredArgsConstructor
@Profile("ewp")
public class EwpMeetingAssignRestController {
	private final EwpResourceAuthorityProvider authorityProvider;
	private final PassTokenProvider tokenProvider;
	
	private final EwpMeetingAssignService assignServ;
	private final EwpMeetingApprovalService approvalServ;
	private final EwpMeetingAttendeeService attServ;
	private final EwpTiberoRoomReqService reqServ;
	private final EwpMariaRoomInfoService rmServ;
	private final EwpUserInfoService userServ;
	private final EwpAlarmWriteService alarmServ;
	private final EwpMeetingFileInfoService fileServ;
	
	@PostMapping("/meeting/assign/post")
	public ResponseMessage postMeetingAssign(Authentication authentication, HttpSession session, EwpMeetingAssignDTO assignDTO) {
		assignDTO.setWriterId(EwpSecurityUtil.getLoginId());
		assignDTO.setWriter(EwpSecurityUtil.getAuthenticationDetails().getUser().convert());
		assignDTO.setDeptId(EwpSecurityUtil.getDeptId());
		
		EwpMeetingAssignVO params = assignDTO.convert();
		if(!DateTimeUtil.isAfterNow(params.getFinishDateTime())) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.FORBIDDEN)
					.message(ResponseMessage.MessageCode.ASSIGN.POST_FAIL.value())
					.detail(ResponseMessage.DetailCode.ASSIGN.BAD_ENDTIME.value())
					.build();
		}
		try {
			boolean isRoomPermitted = EwpSecurityUtil.hasPosition(ManagerRole.getRoomManagerRole(params.getRoomType())) || rmServ.isRoomPermitted(EwpSecurityUtil.getDeptId(), params.getRoomType(), params.getRoomId());
			if(!isRoomPermitted) {
				return ResponseMessage.builder(ResponseMessage.StatusCode.FORBIDDEN)
						.message(ResponseMessage.MessageCode.ASSIGN.POST_FAIL.value())
						.detail(ErrorCode.ROOM.NOT_PERMITTED.getMessage())
						.build();
			}
			EwpRoomInfoVO roomVO = rmServ.selectRoomOne(params.getRoomType(), params.getRoomId()).get();
			if(roomVO.getSyncYN() == 'Y') {
				EwpRoomReqVO reqVO = EwpRoomReqVO.generate(params);
				boolean isSucess = reqServ.postRoomAssign(params.getRoomType(), reqVO);
				if(!isSucess) {
					throw new ApiDataOperationException(ErrorCode.MEETING_ASSIGN.POST_FAILED);
				}
				params = params.toBuilder().reqKey(reqVO.getSeqReq()).build();
			}
			params = assignServ.postMeetingAssign(params);
			try {
				String token = tokenProvider.createToken(EwpSecurityUtil.getLoginId(), params.getMeetingKey().toString(), new HashSet<>(Arrays.asList(
						MeetingAuth.VIEW,
						MeetingAuth.UPDATE,
						MeetingAuth.UPLOAD,
						MeetingAuth.INVITE
						)));
				session.setAttribute("passToken", token);
			}catch(Exception e) {
				e.printStackTrace();
			}
			return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
					.message(ResponseMessage.MessageCode.ASSIGN.POST_SUCCESS.value())
					.data(params)
					.build();
		}catch(ApiException e) {
			log.error(e.getMessage());
			if(params.getReqKey() != null) {
				reqServ.deleteFailedRoomReq(params.getRoomType(), params.getReqKey());
			}
			return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
					.message(ResponseMessage.MessageCode.ASSIGN.POST_FAIL.value())
					.detail(e.getErrorCode().getMessage())
					.build();
		}catch(Exception e) {
			log.error(e.getMessage());
			if(params.getReqKey() != null) {
				reqServ.deleteFailedRoomReq(params.getRoomType(), params.getReqKey());
			}
			return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
					.message(ResponseMessage.MessageCode.ASSIGN.POST_FAIL.value())
					.detail(ErrorCode.MEETING_ASSIGN.POST_FAILED.getMessage())
					.build();
		}
	}
	
	@PutMapping("/meeting/assign/{skdKey}")
	public ResponseMessage putMeetingAssign(Authentication authentication, HttpSession session, @PathVariable Integer skdKey, EwpMeetingAssignDTO assignDTO) {
		session.removeAttribute("passToken");
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(assignDTO.getMeetingKey());
		if(authorityCollection.hasAuthority(MeetingAuth.UPDATE)) {
			EwpMeetingAssignVO oldAssignVO = assignServ.getMeetingAssignOneByScheduleId(skdKey).get();
			EwpMeetingAssignVO newAssignVO = assignDTO.convert();
			try {
				if(!DateTimeUtil.isAfterNow(newAssignVO.getFinishDateTime())) {
					throw new ApiDataOperationException(ErrorCode.MEETING_SCHEDULE.INVALID_PAST_START_TIME);
				}
				if(oldAssignVO.getReqKey() != null) {
					EwpRoomReqVO newReqVO = EwpRoomReqVO.generate(newAssignVO.toBuilder().reqKey(oldAssignVO.getReqKey()).build());
					LocalDate oldHoldingDate = oldAssignVO.getHoldingDate();
					LocalDate newHoldingDate = newReqVO.getHoldingDate();
					boolean isSucess = false;
					if(oldHoldingDate.isEqual(newHoldingDate)) {
						log.info("사용일자 동일");
						isSucess = reqServ.putRoomAssignWithoutValidation(newReqVO.getRoomType(), newReqVO);
					}else {
						log.info("사용일자 변경");
						isSucess = reqServ.putRoomAssign(newReqVO.getRoomType(), newReqVO);
					}
					if(!isSucess) {
						throw new ApiDataOperationException(ErrorCode.MEETING_ASSIGN.UPDATE_FAILED);
					}
				}
				boolean isSucess = assignServ.putMeetingAssign(newAssignVO);
				if(isSucess) {
					Integer meetingKey = newAssignVO.getMeetingKey();
					newAssignVO = assignServ.getMeetingAssignOneByMeetingId(meetingKey).get();
					if(newAssignVO.getElecYN() != null && newAssignVO.getElecYN() == 'N') {
						List<EwpMeetingAttendeeVO> attendeeList = attServ.getMeetingAttendeeListByMeeting(meetingKey);
						alarmServ.sendAttendeeAlarm(attendeeList, "delete", newAssignVO.getMeetingKey());
						attServ.deleteMeetingAttendeeAll(meetingKey);
						fileServ.deleteMeetingFolder(meetingKey);
					}else {
						String token = tokenProvider.createToken((String) authentication.getPrincipal(), meetingKey.toString(), new HashSet<>(Arrays.asList(
							MeetingAuth.VIEW,
							MeetingAuth.UPDATE,
							MeetingAuth.UPLOAD,
							MeetingAuth.INVITE
						)));
						session.setAttribute("passToken", token);
						EwpUserInfoVO userVO = EwpSecurityUtil.getAuthenticationDetails().getUser();
						if(	!(	oldAssignVO.getTitle().equals(newAssignVO.getTitle()) &&
								oldAssignVO.getSkdHost().equals(newAssignVO.getSkdHost()) && 
								(oldAssignVO.getBeginDateTime().isEqual(newAssignVO.getBeginDateTime()) && oldAssignVO.getFinishDateTime().isEqual(newAssignVO.getFinishDateTime())))) {
							alarmServ.postMeetingUpdateNoticeAlarm(meetingKey, userVO, oldAssignVO, newAssignVO);
						}
					}
					return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
							.message(ResponseMessage.MessageCode.ASSIGN.PUT_SUCCESS.value())
							.build();
				}else {
					throw new ApiDataOperationException(ErrorCode.MEETING_ASSIGN.UPDATE_FAILED);
				}
			}catch(ApiException e) {
				log.error(e.getMessage());
				return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
						.message(ResponseMessage.MessageCode.ASSIGN.PUT_FAIL.value())
						.detail(e.getErrorCode().getMessage())
						.build();
			}catch(Exception e) {
				log.error(e.getMessage());
				return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
						.message(ResponseMessage.MessageCode.ASSIGN.PUT_FAIL.value())
						.detail(ErrorCode.MEETING_ASSIGN.UPDATE_FAILED.getMessage())
						.build();
			}
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.FORBIDDEN)
					.message(ResponseMessage.MessageCode.ASSIGN.PUT_FAIL.value())
					.detail(ResponseMessage.DetailCode.ASSIGN.FORBIDDEN.value())
					.build();
		}
	}
	
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
	@PostMapping("/meeting/assign/{skdKey}/approval/{appStatus}")
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
	 * 사용신청에 대한 정보 조회. 권한별로 다른 필터링을 적용하여 전달
	 * @param authentication
	 * @param skdKey 스케줄키
	 * @return 사용신청 정보 반환
	 */
	@GetMapping("/meeting/assign/{skdKey}")
	public EwpMeetingAssignDTO getMeetingAssignOne(Authentication authentication, Model model, @PathVariable Integer skdKey) {
		EwpMeetingAssignDTO dto = null;
		Optional<EwpMeetingAssignVO> opt = assignServ.getMeetingAssignOneByScheduleId(skdKey);
		if(opt.isPresent()) {
			dto = opt.get().convert();
			Optional<EwpUserInfoVO> userOpt = userServ.selectUserInfoOne(dto.getWriterKey());
			if(userOpt.isPresent()) {
				dto.setWriter(userOpt.get().convert());
			}
			ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(dto.getMeetingKey());
			if(authorityCollection.hasAuthority(MeetingAuth.UPDATE)) {
				dto = dto.filterForHighest();
			}else if(authorityCollection.hasAuthority(MeetingAuth.VIEW)) {
				dto = dto.filterForSensitive();
			}else if(authorityCollection.hasAuthority(MeetingAuth.READ)) {
				dto = dto.filterForEssential();
			}else {
				dto = null;
			}
		}
		return dto;
	}
	
	/**
	 * 일정 기간동안 예정된 스케줄 조회. 전달된 기간(<code>startDate</code>, <code>endDate</code>)이 올바르지 않은 경우 범위를 요청 당일로 적용.
	 *
	 * @param authentication
	 * @param startDate 기간 검색을 위한 시작일자(yyyy-MM-dd)
	 * @param endDate 기간 검색을 위한 종료일자(yyyy-MM-dd)
	 * @return
	 */
	@GetMapping("/meeting/assign/planned/list")
	public List<EwpMeetingAssignDTO> getMeetingAssignListForPlanned(
			@RequestParam String startDate, 
			@RequestParam String endDate) {
		String loginKey = EwpSecurityUtil.getLoginId();
		
		LocalDate validSdate = DateTimeUtil.toFormattedDate(startDate);
		LocalDate validEdate = DateTimeUtil.toFormattedDate(endDate);
		
		List<EwpMeetingAssignVO> voList = assignServ.getMeetingAssignListForPlanned(EwpMeetingAssignVO.builder().userKey(loginKey).startDate(validSdate).endDate(validEdate).build());
		return voList.stream().map(EwpMeetingAssignVO::convert).map(EwpMeetingAssignDTO::filterForDetailed).collect(Collectors.toList());
	}
	
	/**
	 * 일정 기간동안 예정된 스케줄의 통계 조회. 전달된 기간(<code>startDate</code>, <code>endDate</code>)이 올바르지 않은 경우 범위를 요청 당일로 적용.
	 *
	 * @param authentication
	 * @param startDate 기간 검색을 위한 시작일자(yyyy-MM-dd)
	 * @param endDate 기간 검색을 위한 종료일자(yyyy-MM-dd)
	 * @return
	 */
	@GetMapping("/meeting/assign/planned/stat")
	public List<StatDTO> getMeetingAssignStatForPlanned(
			@RequestParam String startDate, 
			@RequestParam String endDate) {
		String loginKey = EwpSecurityUtil.getLoginId();
		
		LocalDate validSdate = DateTimeUtil.toFormattedDate(startDate);
		LocalDate validEdate = DateTimeUtil.toFormattedDate(endDate);
		
		List<StatVO> voList = assignServ.getMeetingAssignStatForPlanned(EwpMeetingAssignVO.builder().userKey(loginKey).startDate(validSdate).endDate(validEdate).build());
		return voList.stream().map(StatVO::convert).collect(Collectors.toList());
	}
	
	/**
	 * 일정 기간동안 자신이 등록한 스케줄 조회. 전달된 기간(<code>startDate</code>, <code>endDate</code>)이 올바르지 않은 경우 범위를 요청 당일로 적용.
	 *
	 * @param authentication
	 * @param startDate 기간 검색을 위한 시작일자(yyyy-MM-dd)
	 * @param endDate 기간 검색을 위한 종료일자(yyyy-MM-dd)
	 * @return
	 */
	@GetMapping("/meeting/assign/register/list")
	public List<EwpMeetingAssignDTO> getMeetingAssignListForRegister(
			@RequestParam @Nullable Integer pageNo,
			@RequestParam @Nullable Integer pageCnt
		) {
		String loginKey = EwpSecurityUtil.getLoginId();
		if(pageNo == null) {
			pageNo = 1;
		}
		if(pageCnt == null) {
			pageCnt = 5;
		}
		List<EwpMeetingAssignVO> voList = assignServ.getMeetingAssignList(EwpMeetingAssignVO.builder().writerKey(loginKey).pageNo(pageNo).pageCnt(pageCnt).orderColumn(OrderColumn.등록일시).orderDirection(OrderDirection.DESC).build());
		return voList.stream().map(EwpMeetingAssignVO::convert).map(dto -> {
			dto.filterForSensitive();
			dto.getAttendeeList().forEach(attendee -> attendee.filterForBasic());
			return dto;
		}).collect(Collectors.toList());
	}
	
	/**
	 * 해당 장소에 등록된 사용신청 목록 조회. 전달된 기간(<code>startDate</code>, <code>endDate</code>)이 올바르지 않은 경우 범위를 요청 당일로 적용.
	 * @param authentication
	 * @param roomType {@link RoomRole#ALL_ROOM}, {@link RoomRole#MEETING_ROOM}, {@link RoomRole#EDU_ROOM}
	 * @param roomKey 특정 장소에 등록된 사용신청을 조회할 경우 필요, 특정 장소가 아닌 해당 장소 구분에 해당하는 모든 장소 대상으로 조회할 경우 <code>null</code>
	 * @param approvalStatus {@link ApprovalStatus#REQUEST}, {@link ApprovalStatus#APPROVED}, {@link ApprovalStatus#CANCEL}, {@link ApprovalStatus#REJECT}
	 * @param startDate 기간 검색을 위한 시작일자(yyyy-MM-dd)
	 * @param endDate 기간 검색을 위한 종료일자(yyyy-MM-dd)
	 * @param host 회의 주관자. 조회시 동일 검색이 아닌 포함 검색 적용
	 * @param pageNo 페이지 번호
	 * @param pageCnt 페이지당 표시할 개수. pageNo는 유효하고 pageCnt가 NULL인 경우 10을 기본값으로 사용
	 * @param orderColumn 정렬 대상
	 * @param orderDirection 정렬 순서
	 * 
	 * @see {@link RoomRole}
	 * @see {@link ApprovalStatus}
	 * @return
	 */
	@GetMapping("/meeting/assign/list")
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
			@RequestParam @Nullable Integer pageCnt,
			@RequestParam @Nullable OrderColumn orderColumn,
			@RequestParam @Nullable OrderDirection orderDirection) {
		LocalDate validSdate = DateTimeUtil.toFormattedDate(startDate);
		LocalDate validEdate = DateTimeUtil.toFormattedDate(endDate);
		List<EwpMeetingAssignVO> voList = assignServ.getMeetingAssignList(EwpMeetingAssignVO.builder().officeCode(officeCode).roomType(roomType).roomKey(roomKey).appStatus(approvalStatus)
				.title(title).skdHost(host).attendeeName(attendeeName).elecYN(elecYN).secretYN(secretYN)
				.startDate(validSdate).endDate(validEdate)
				.pageNo(pageNo).pageCnt(pageCnt).orderColumn(orderColumn).orderDirection(orderDirection).build());
		return joinWriter(voList).stream().map(EwpMeetingAssignVO::convert).collect(Collectors.toList());
	}
	
	/**
	 * 사용신청 목록 조회 결과의 총 개수 조회(TIBERO). 검색 조건과 처리는 {@link EwpMeetingAssignRestController#getRoomReqList}참고
	 *
	 * @return 검색 조건으로 조회된 데이터의 총 개수
	 */
	@GetMapping("/meeting/assign/list/cnt")
	public Integer getMeetingAssignListCnt(Authentication authentication, 
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
			@RequestParam String endDate
			) {
		LocalDate validSdate = DateTimeUtil.toFormattedDate(startDate);
		LocalDate validEdate = DateTimeUtil.toFormattedDate(endDate);
		return assignServ.getMeetingAssignListCnt(EwpMeetingAssignVO.builder().officeCode(officeCode).roomType(roomType).roomKey(roomKey).appStatus(approvalStatus)
				.title(title).skdHost(host).attendeeName(attendeeName).elecYN(elecYN).secretYN(secretYN)
				.startDate(validSdate).endDate(validEdate).build());
	}
	
	/**
	 * 예약 내역 확인을 위한 해당 장소에 등록된 사용신청 목록 조회. 전달된 기간(<code>startDate</code>, <code>endDate</code>)이 올바르지 않은 경우 범위를 요청 당일로 적용.
	 * @param authentication
	 * @param roomType {@link RoomType#ALL_ROOM}, {@link RoomType#MEETING_ROOM}, {@link RoomType#EDU_ROOM}
	 * @param roomKey 특정 장소에 등록된 사용신청을 조회할 경우 필요, 특정 장소가 아닌 해당 장소 구분에 해당하는 모든 장소 대상으로 조회할 경우 <code>null</code>
	 * @param approvalStatus {@link ApprovalStatus#REQUEST}, {@link ApprovalStatus#APPROVED}, {@link ApprovalStatus#CANCEL}, {@link ApprovalStatus#REJECT}
	 * @param startDate 기간 검색을 위한 시작일자(yyyy-MM-dd)
	 * @param endDate 기간 검색을 위한 종료일자(yyyy-MM-dd)
	 * 
	 * @see {@link RoomType}
	 * @see {@link ApprovalStatus}
	 * @return
	 */
	@GetMapping("/meeting/assign/display/list")
	public List<EwpMeetingAssignDTO> getMeetingAssignListForDisplay(Authentication authentication, 
			@RequestParam @Nullable String officeCode, 
			@RequestParam RoomType roomType, 
			@RequestParam @Nullable Integer roomKey, 
			@RequestParam String startDate, 
			@RequestParam String endDate
		) {
		LocalDate validSdate = DateTimeUtil.toFormattedDate(startDate);
		LocalDate validEdate = DateTimeUtil.toFormattedDate(endDate);
		List<EwpMeetingAssignVO> voList = assignServ.getMeetingAssignList(
				EwpMeetingAssignVO.builder().officeCode(officeCode).roomType(roomType).roomKey(roomKey)
				.startDate(validSdate).endDate(validEdate).orderColumn(OrderColumn.시작일시).orderDirection(OrderDirection.ASC).build());
		voList = voList.stream().filter(vo -> vo.getApprovalStatus() == ApprovalStatus.APPROVED || vo.getApprovalStatus() == ApprovalStatus.REQUEST).collect(Collectors.toList());
		return joinWriter(voList).stream().map(EwpMeetingAssignVO::convert).map(EwpMeetingAssignDTO::filterForEssential).collect(Collectors.toList());
	}
	
	/**
	 * 미인증 사용자가 예약 내역 확인을 위한 해당 장소에 등록된 사용신청 목록 조회. 전달된 기간(<code>startDate</code>, <code>endDate</code>)이 올바르지 않은 경우 범위를 요청 당일로 적용.
	 * @param authentication
	 * @param roomType {@link RoomType#ALL_ROOM}, {@link RoomType#MEETING_ROOM}, {@link RoomType#EDU_ROOM}
	 * @param roomKey 특정 장소에 등록된 사용신청을 조회할 경우 필요, 특정 장소가 아닌 해당 장소 구분에 해당하는 모든 장소 대상으로 조회할 경우 <code>null</code>
	 * @param approvalStatus {@link ApprovalStatus#REQUEST}, {@link ApprovalStatus#APPROVED}, {@link ApprovalStatus#CANCEL}, {@link ApprovalStatus#REJECT}
	 * @param startDate 기간 검색을 위한 시작일자(yyyy-MM-dd)
	 * @param endDate 기간 검색을 위한 종료일자(yyyy-MM-dd)
	 * 
	 * @see {@link RoomType}
	 * @see {@link ApprovalStatus}
	 * @return
	 */
	@GetMapping("/public/meeting/assign/display/list")
	public List<EwpMeetingAssignDTO> getMeetingAssignPublicListForDisplay(Authentication authentication, 
			@RequestParam @Nullable String officeCode, 
			@RequestParam @Nullable RoomType roomType, 
			@RequestParam @Nullable Integer roomKey, 
			@RequestParam @Nullable String startDate, 
			@RequestParam @Nullable String endDate
		) {
		LocalDate validSdate = DateTimeUtil.toFormattedDate(startDate);
		LocalDate validEdate = DateTimeUtil.toFormattedDate(endDate);
		List<EwpMeetingAssignVO> voList = assignServ.getMeetingAssignList(
				EwpMeetingAssignVO.builder().officeCode(officeCode).roomType(roomType).roomKey(roomKey)
				.startDate(validSdate).endDate(validEdate).orderColumn(OrderColumn.시작일시).orderDirection(OrderDirection.ASC).build());
		voList = voList.stream().filter(vo -> vo.getApprovalStatus() == ApprovalStatus.APPROVED || vo.getApprovalStatus() == ApprovalStatus.REQUEST).collect(Collectors.toList());
		return joinWriter(voList).stream().map(EwpMeetingAssignVO::convert).map(EwpMeetingAssignDTO::filterForEssential).collect(Collectors.toList());
	}
	
	private List<EwpMeetingAssignVO> joinWriter(List<EwpMeetingAssignVO> voList){
		return voList.stream().map(vo -> vo.toBuilder().writer(userServ.selectUserInfoOne(vo.getWriterId()).orElse(null)).build()).collect(Collectors.toList());
	}
}
