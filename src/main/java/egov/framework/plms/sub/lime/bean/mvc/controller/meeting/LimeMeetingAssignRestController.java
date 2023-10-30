package egov.framework.plms.sub.lime.bean.mvc.controller.meeting;

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
import egov.framework.plms.main.bean.component.auth.ResourceAuthorityProvider;
import egov.framework.plms.main.bean.mvc.entity.common.StatDTO;
import egov.framework.plms.main.bean.mvc.entity.common.StatVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingArchiveDTO;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingArchiveVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingAssignDTO;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingAssignVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingScheduleVO;
import egov.framework.plms.main.bean.mvc.entity.organization.UserInfoVO;
import egov.framework.plms.main.bean.mvc.service.meeting.MeetingApprovalService;
import egov.framework.plms.main.core.exception.ApiDataOperationException;
import egov.framework.plms.main.core.exception.abst.ApiException;
import egov.framework.plms.main.core.model.auth.ResourceAuthorityCollection;
import egov.framework.plms.main.core.model.enums.auth.MeetingAuth;
import egov.framework.plms.main.core.model.enums.common.OrderColumn;
import egov.framework.plms.main.core.model.enums.common.OrderDirection;
import egov.framework.plms.main.core.model.enums.error.ErrorCode;
import egov.framework.plms.main.core.model.enums.meeting.ApprovalStatus;
import egov.framework.plms.main.core.model.enums.meeting.MeetingStatus;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import egov.framework.plms.main.core.model.response.ResponseMessage;
import egov.framework.plms.main.core.util.DateTimeUtil;
import egov.framework.plms.sub.ewp.core.model.enums.FileRole;
import egov.framework.plms.sub.lime.bean.mvc.service.meeting.LimeMeetingAssignService;
import egov.framework.plms.sub.lime.bean.mvc.service.meeting.LimeMeetingAttendeeService;
import egov.framework.plms.sub.lime.bean.mvc.service.organization.LimeUserInfoService;
import egov.framework.plms.sub.lime.bean.mvc.service.room.LimeRoomInfoService;
import egov.framework.plms.sub.lime.core.util.LimeSecurityUtil;
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
@RequestMapping("/api/lime")
@RequiredArgsConstructor
@Profile("lime")
public class LimeMeetingAssignRestController {
	private final ResourceAuthorityProvider authorityProvider;
	private final PassTokenProvider tokenProvider;
	
	private final LimeMeetingAssignService assignServ;
	private final MeetingApprovalService approvalServ;
	private final LimeMeetingAttendeeService attServ;
	private final LimeRoomInfoService rmServ;
	private final LimeUserInfoService userServ;
	
	@PostMapping("/meeting/assign/post")
	public ResponseMessage postMeetingAssign(Authentication authentication, HttpSession session, MeetingAssignDTO assignDTO) {
		assignDTO.setWriterId(LimeSecurityUtil.getLoginId());
		assignDTO.setWriter(LimeSecurityUtil.getAuthenticationDetails().getUser().convert());
		assignDTO.setDeptId(LimeSecurityUtil.getDeptId());
		
		MeetingAssignVO params = assignDTO.convert();
		try {
			if(!DateTimeUtil.isAfterNow(params.getFinishDateTime())) {
				return ResponseMessage.builder(ResponseMessage.StatusCode.FORBIDDEN)
						.message(ResponseMessage.MessageCode.ASSIGN.POST_FAIL.value())
						.detail(ResponseMessage.DetailCode.ASSIGN.BAD_ENDTIME.value())
						.build();
			}
			params = assignServ.postMeetingAssign(params);
			try {
				String token = tokenProvider.createToken(LimeSecurityUtil.getLoginId(), params.getMeetingId().toString(), new HashSet<>(Arrays.asList(
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
			return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
					.message(ResponseMessage.MessageCode.ASSIGN.POST_FAIL.value())
					.detail(e.getErrorCode().getMessage())
					.build();
		}catch(Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
					.message(ResponseMessage.MessageCode.ASSIGN.POST_FAIL.value())
					.detail(ErrorCode.MEETING_ASSIGN.POST_FAILED.getMessage())
					.build();
		}
	}
	
	@PutMapping("/meeting/assign/{scheduleId}")
	public ResponseMessage putMeetingAssign(Authentication authentication, HttpSession session, @PathVariable Integer scheduleId, MeetingAssignDTO assignDTO) {
		session.removeAttribute("passToken");
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(assignDTO.getMeetingId());
		if(authorityCollection.hasAuthority(MeetingAuth.UPDATE)) {
			MeetingAssignVO newAssignVO = assignDTO.convert();
			try {
				if(!DateTimeUtil.isAfterNow(newAssignVO.getFinishDateTime())) {
					throw new ApiDataOperationException(ErrorCode.MEETING_SCHEDULE.INVALID_PAST_START_TIME);
				}
				boolean isSucess = assignServ.putMeetingAssign(newAssignVO);
				if(isSucess) {
					Integer meetingKey = newAssignVO.getMeetingId();
					newAssignVO = assignServ.getMeetingAssignOneByMeetingId(meetingKey).get();
					if(newAssignVO.getElecYN() != null && newAssignVO.getElecYN() == 'N') {
						attServ.deleteMeetingAttendeeAll(meetingKey);
						//fileServ.deleteMeetingFolder(meetingKey);
					}else {
						String token = tokenProvider.createToken((String) authentication.getPrincipal(), meetingKey.toString(), new HashSet<>(Arrays.asList(
							MeetingAuth.VIEW,
							MeetingAuth.UPDATE,
							MeetingAuth.UPLOAD,
							MeetingAuth.INVITE
						)));
						session.setAttribute("passToken", token);
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
	 * @param scheduleId {@link MeetingScheduleVO#scheduleId}
	 * @param status {@link ApprovalStatus#APPROVED}, {@link ApprovalStatus#CANCEL}, {@link ApprovalStatus#REJECT}, {@link ApprovalStatus#DELETE}
	 * @param comment 결재 요청 코멘트
	 * 
	 * @see {@link ApprovalStatus}
	 * @return
	 */
	@PostMapping("/meeting/assign/{scheduleId}/approval/{approvalStatus}")
	public ResponseMessage postAssignApprovalRequest(@PathVariable Integer scheduleId,
			@PathVariable ApprovalStatus approvalStatus, @RequestParam @Nullable String comment) {
		return ResponseMessage.builder(ResponseMessage.StatusCode.FORBIDDEN).build();
		/*
		ResourceAuthorityCollection authorityCollection = authorityProvider.getAssignApprovalAuthorityCollection(scheduleId);
		if(authorityCollection.isEmpty()) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.FORBIDDEN)
					.message(ResponseMessage.MessageCode.APPROVAL.POST_FAIL.value())
					.detail(ResponseMessage.DetailCode.ASSIGN.FORBIDDEN.value())
					.build();
		}
		AssignApprovalAuth reqAuth = null;
		switch(approvalStatus) {
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
			boolean result = approvalServ.insertApprovalOne(LimeSecurityUtil.getLoginId(), scheduleId, approvalStatus, comment);
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
		*/
	}
	
	/**
	 * 사용신청에 대한 정보 조회. 권한별로 다른 필터링을 적용하여 전달
	 * @param authentication
	 * @param scheduleId 스케줄키
	 * @return 사용신청 정보 반환
	 */
	@GetMapping("/meeting/assign/{scheduleId}")
	public MeetingAssignDTO getMeetingAssignOne(Authentication authentication, Model model, @PathVariable Integer scheduleId) {
		MeetingAssignDTO dto = null;
		Optional<MeetingAssignVO> opt = assignServ.getMeetingAssignOneByScheduleId(scheduleId);
		if(opt.isPresent()) {
			dto = opt.get().convert();
			Optional<UserInfoVO> userOpt = userServ.selectUserInfoOne(dto.getWriterId());
			if(userOpt.isPresent()) {
				dto.setWriter(userOpt.get().convert());
			}
			ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(dto.getMeetingId());
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
	public List<MeetingAssignDTO> getMeetingAssignListForPlanned(
			@RequestParam String startDate, 
			@RequestParam String endDate) {
		String loginId = LimeSecurityUtil.getLoginId();
		
		LocalDate validSdate = DateTimeUtil.toFormattedDate(startDate);
		LocalDate validEdate = DateTimeUtil.toFormattedDate(endDate);
		
		List<MeetingAssignVO> voList = assignServ.getMeetingAssignListForPlanned(MeetingAssignVO.builder().userId(loginId).startDate(validSdate).endDate(validEdate).build());
		return voList.stream().map(MeetingAssignVO::convert).map(MeetingAssignDTO::filterForDetailed).collect(Collectors.toList());
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
		String loginId = LimeSecurityUtil.getLoginId();
		
		LocalDate validSdate = DateTimeUtil.toFormattedDate(startDate);
		LocalDate validEdate = DateTimeUtil.toFormattedDate(endDate);
		
		List<StatVO> voList = assignServ.getMeetingAssignStatForPlanned(MeetingAssignVO.builder().userId(loginId).startDate(validSdate).endDate(validEdate).build());
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
	public List<MeetingAssignDTO> getMeetingAssignListForRegister(
			@RequestParam @Nullable Integer pageNo,
			@RequestParam @Nullable Integer pageCnt
		) {
		String loginId = LimeSecurityUtil.getLoginId();
		if(pageNo == null) {
			pageNo = 1;
		}
		if(pageCnt == null) {
			pageCnt = 5;
		}
		List<MeetingAssignVO> voList = assignServ.getMeetingAssignList(MeetingAssignVO.builder().writerId(loginId).pageNo(pageNo).pageCnt(pageCnt).orderColumn(OrderColumn.등록일시).orderDirection(OrderDirection.DESC).build());
		return voList.stream().map(MeetingAssignVO::convert).map(dto -> {
			dto.filterForSensitive();
			dto.getAttendeeList().forEach(attendee -> attendee.filterForBasic());
			return dto;
		}).collect(Collectors.toList());
	}
	
	/**
	 * 해당 장소에 등록된 사용신청 목록 조회. 전달된 기간(<code>startDate</code>, <code>endDate</code>)이 올바르지 않은 경우 범위를 요청 당일로 적용.
	 * @param authentication
	 * @param roomType {@link RoomRole#ALL_ROOM}, {@link RoomRole#MEETING_ROOM}, {@link RoomRole#EDU_ROOM}
	 * @param roomId 특정 장소에 등록된 사용신청을 조회할 경우 필요, 특정 장소가 아닌 해당 장소 구분에 해당하는 모든 장소 대상으로 조회할 경우 <code>null</code>
	 * @param approvalStatus {@link ApprovalStatus#REQUEST}, {@link ApprovalStatus#APPROVED}, {@link ApprovalStatus#CANCEL}, {@link ApprovalStatus#REJECT}
	 * @param startDate 기간 검색을 위한 시작일자(yyyy-MM-dd)
	 * @param endDate 기간 검색을 위한 종료일자(yyyy-MM-dd)
	 * @param scheduleHost 회의 주관자. 조회시 동일 검색이 아닌 포함 검색 적용
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
	public List<MeetingAssignDTO> getMeetingAssignList(Authentication authentication, 
			@RequestParam @Nullable String officeCode, 
			@RequestParam RoomType roomType, 
			@RequestParam @Nullable Integer roomId, 
			@RequestParam @Nullable ApprovalStatus approvalStatus,
			@RequestParam String startDate, 
			@RequestParam String endDate,
			@RequestParam @Nullable String scheduleHost,
			@RequestParam @Nullable Integer pageNo,
			@RequestParam @Nullable Integer pageCnt,
			@RequestParam @Nullable OrderColumn orderColumn,
			@RequestParam @Nullable OrderDirection orderDirection) {
		LocalDate validSdate = DateTimeUtil.toFormattedDate(startDate);
		LocalDate validEdate = DateTimeUtil.toFormattedDate(endDate);
		List<MeetingAssignVO> voList = assignServ.getMeetingAssignList(MeetingAssignVO.builder().roomId(roomId).approvalStatus(approvalStatus).startDate(validSdate).endDate(validEdate).scheduleHost(scheduleHost).pageNo(pageNo).pageCnt(pageCnt).orderColumn(orderColumn).orderDirection(orderDirection).build());
		return joinWriter(voList).stream().map(MeetingAssignVO::convert).map(MeetingAssignDTO::filterForEssential).collect(Collectors.toList());
	}
	
	/**
	 * 사용신청 목록 조회 결과의 총 개수 조회(TIBERO). 검색 조건과 처리는 {@link LimeMeetingAssignRestController#getRoomReqList}참고
	 *
	 * @return 검색 조건으로 조회된 데이터의 총 개수
	 */
	@GetMapping("/meeting/assign/list/cnt")
	public Integer getMeetingAssignListCnt(Authentication authentication, 
			@RequestParam @Nullable String officeCode, 
			@RequestParam RoomType roomType, 
			@RequestParam @Nullable Integer roomId, 
			@RequestParam @Nullable ApprovalStatus approvalStatus,
			@RequestParam String startDate, 
			@RequestParam String endDate,
			@RequestParam @Nullable String scheduleHost) {
		LocalDate validSdate = DateTimeUtil.toFormattedDate(startDate);
		LocalDate validEdate = DateTimeUtil.toFormattedDate(endDate);
		return assignServ.getMeetingAssignListCnt(MeetingAssignVO.builder().roomId(roomId).approvalStatus(approvalStatus).startDate(validSdate).endDate(validEdate).scheduleHost(scheduleHost).build());
	}
	
	/**
	 * 예약 내역 확인을 위한 해당 장소에 등록된 사용신청 목록 조회. 전달된 기간(<code>startDate</code>, <code>endDate</code>)이 올바르지 않은 경우 범위를 요청 당일로 적용.
	 * @param authentication
	 * @param roomType {@link RoomType#ALL_ROOM}, {@link RoomType#MEETING_ROOM}, {@link RoomType#EDU_ROOM}
	 * @param roomId 특정 장소에 등록된 사용신청을 조회할 경우 필요, 특정 장소가 아닌 해당 장소 구분에 해당하는 모든 장소 대상으로 조회할 경우 <code>null</code>
	 * @param approvalStatus {@link ApprovalStatus#REQUEST}, {@link ApprovalStatus#APPROVED}, {@link ApprovalStatus#CANCEL}, {@link ApprovalStatus#REJECT}
	 * @param startDate 기간 검색을 위한 시작일자(yyyy-MM-dd)
	 * @param endDate 기간 검색을 위한 종료일자(yyyy-MM-dd)
	 * 
	 * @see {@link RoomType}
	 * @see {@link ApprovalStatus}
	 * @return
	 */
	@GetMapping("/meeting/assign/display/list")
	public List<MeetingAssignDTO> getMeetingAssignListForDisplay(Authentication authentication, 
			@RequestParam @Nullable String officeCode, 
			@RequestParam @Nullable RoomType roomType, 
			@RequestParam @Nullable Integer roomId, 
			@RequestParam String startDate, 
			@RequestParam String endDate
		) {
		LocalDate validSdate = DateTimeUtil.toFormattedDate(startDate);
		LocalDate validEdate = DateTimeUtil.toFormattedDate(endDate);
		List<MeetingAssignVO> voList = assignServ.getMeetingAssignList(
				MeetingAssignVO.builder().roomId(roomId)
				.startDate(validSdate).endDate(validEdate).orderColumn(OrderColumn.시작일시).orderDirection(OrderDirection.ASC).build());
		voList = voList.stream().filter(vo -> vo.getApprovalStatus() == ApprovalStatus.APPROVED || vo.getApprovalStatus() == ApprovalStatus.REQUEST).collect(Collectors.toList());
		return joinWriter(voList).stream().map(MeetingAssignVO::convert).map(MeetingAssignDTO::filterForEssential).collect(Collectors.toList());
	}
	
	/**
	 * 미인증 사용자가 예약 내역 확인을 위한 해당 장소에 등록된 사용신청 목록 조회. 전달된 기간(<code>startDate</code>, <code>endDate</code>)이 올바르지 않은 경우 범위를 요청 당일로 적용.
	 * @param authentication
	 * @param roomType {@link RoomType#ALL_ROOM}, {@link RoomType#MEETING_ROOM}, {@link RoomType#EDU_ROOM}
	 * @param roomId 특정 장소에 등록된 사용신청을 조회할 경우 필요, 특정 장소가 아닌 해당 장소 구분에 해당하는 모든 장소 대상으로 조회할 경우 <code>null</code>
	 * @param approvalStatus {@link ApprovalStatus#REQUEST}, {@link ApprovalStatus#APPROVED}, {@link ApprovalStatus#CANCEL}, {@link ApprovalStatus#REJECT}
	 * @param startDate 기간 검색을 위한 시작일자(yyyy-MM-dd)
	 * @param endDate 기간 검색을 위한 종료일자(yyyy-MM-dd)
	 * 
	 * @see {@link RoomType}
	 * @see {@link ApprovalStatus}
	 * @return
	 */
	@GetMapping("/public/meeting/assign/display/list")
	public List<MeetingAssignDTO> getMeetingAssignPublicListForDisplay(Authentication authentication, 
			@RequestParam @Nullable String officeCode, 
			@RequestParam @Nullable RoomType roomType, 
			@RequestParam @Nullable Integer roomId, 
			@RequestParam @Nullable String startDate, 
			@RequestParam @Nullable String endDate
		) {
		LocalDate validSdate = DateTimeUtil.toFormattedDate(startDate);
		LocalDate validEdate = DateTimeUtil.toFormattedDate(endDate);
		List<MeetingAssignVO> voList = assignServ.getMeetingAssignList(
				MeetingAssignVO.builder().roomId(roomId)
				.startDate(validSdate).endDate(validEdate).orderColumn(OrderColumn.시작일시).orderDirection(OrderDirection.ASC).build());
		voList = voList.stream().filter(vo -> vo.getApprovalStatus() == ApprovalStatus.APPROVED || vo.getApprovalStatus() == ApprovalStatus.REQUEST).collect(Collectors.toList());
		return joinWriter(voList).stream().map(MeetingAssignVO::convert).map(MeetingAssignDTO::filterForEssential).collect(Collectors.toList());
	}
	
	private List<MeetingAssignVO> joinWriter(List<MeetingAssignVO> voList){
		return voList.stream().map(vo -> vo.toBuilder().writer(userServ.selectUserInfoOne(vo.getWriterId()).orElse(null)).build()).collect(Collectors.toList());
	}
	
	/**
	 * 개인 사용신청목록 페이지 검색 요청(Maria DB)
	 * @param authentication
	 * @param roomType {@link RoomType#ALL_ROOM}, {@link RoomType#MEETING_ROOM}, {@link RoomType#EDU_ROOM}
	 * @param approvalStatus {@link ApprovalStatus#REQUEST}, {@link ApprovalStatus#APPROVED}, {@link ApprovalStatus#CANCEL}, {@link ApprovalStatus#REJECT}
	 * @param meetingStatus 회의 진행상태 {@link MeetingStatus#UNAPPROVAL}, {@link MeetingStatus#RESERVE}, {@link MeetingStatus#START}, {@link MeetingStatus#END}, {@link MeetingStatus#DROP}, {@link MeetingStatus#CANCEL}
	 * @param elecYN 전자회의 여부(Y/N)
	 * @param secretYN 기밀회의 여부(Y/N)
	 * @param title 회의 제목. 조회시 동일 검색이 아닌 포함 검색 적용
	 * @param scheduleHost 회의 주관자. 조회시 동일 검색이 아닌 포함 검색 적용
	 * @param attendeeName 회의 주관자. 조회시 동일 검색이 아닌 포함 검색 적용
	 * @param startDate 기간 검색을 위한 시작일자(yyyy-MM-dd)
	 * @param endDate 기간 검색을 위한 종료일자(yyyy-MM-dd)
	 * @param pageNo 페이지 번호
	 * @param pageCnt 페이지당 표시할 개수. pageNo는 유효하고 pageCnt가 NULL인 경우 10을 기본값으로 사용
	 * @param orderColumn 정렬 대상
	 * @param orderDirection 정렬 순서
	 * 
	 * @see {@link RoomType}
	 * @see {@link ApprovalStatus}
	 * @return
	 */
	@GetMapping("/meeting/assign/manage/user")
	public List<MeetingAssignDTO> getUserAssignList(
			@RequestParam @Nullable RoomType roomType,
			@RequestParam @Nullable ApprovalStatus approvalStatus,
			@RequestParam @Nullable MeetingStatus meetingStatus,
			@RequestParam @Nullable Character elecYN,
			@RequestParam @Nullable Character secretYN,
			@RequestParam @Nullable String title,
			@RequestParam @Nullable String scheduleHost,
			@RequestParam @Nullable String writerId,
			@RequestParam @Nullable String attendeeName,
			@RequestParam @Nullable String startDate, 
			@RequestParam @Nullable String endDate,
			@RequestParam @Nullable Integer pageNo,
			@RequestParam @Nullable Integer pageCnt,
			@RequestParam @Nullable String orderColumn,
			@RequestParam @Nullable OrderDirection orderDirection) {
		if(roomType == RoomType.ALL_ROOM) {
			roomType = null;
		}
		String loginId = LimeSecurityUtil.getLoginId();
		LocalDate validSdate = DateTimeUtil.toFormattedDate(startDate);
		LocalDate validEdate = DateTimeUtil.toFormattedDate(endDate);
		List<MeetingAssignVO> voList = assignServ.getMeetingAssignList(MeetingAssignVO.builder()
				.writerId(loginId).roomType(roomType).approvalStatus(approvalStatus).meetingStatus(meetingStatus).elecYN(elecYN).secretYN(secretYN)
				.title(title).scheduleHost(scheduleHost).writerId(writerId).attendeeName(attendeeName).startDate(validSdate).endDate(validEdate).pageNo(pageNo).pageCnt(pageCnt).orderDirection(orderDirection).build());
		return voList.stream().map(MeetingAssignVO::convert).collect(Collectors.toList());
	}
	
	/**
	 * 부서 사용신청목록 페이지 검색 요청(Maria DB)
	 * @param authentication
	 * @param roomType {@link RoomType#ALL_ROOM}, {@link RoomType#MEETING_ROOM}, {@link RoomType#EDU_ROOM}
	 * @param approvalStatus {@link ApprovalStatus#REQUEST}, {@link ApprovalStatus#APPROVED}, {@link ApprovalStatus#CANCEL}, {@link ApprovalStatus#REJECT}
	 * @param meetingStatus 회의 진행상태 {@link MeetingStatus#UNAPPROVAL}, {@link MeetingStatus#APPROVED}, {@link MeetingStatus#START}, {@link MeetingStatus#END}, {@link MeetingStatus#DROP}, {@link MeetingStatus#CANCEL}
	 * @param elecYN 전자회의 여부(Y/N)
	 * @param secretYN 기밀회의 여부(Y/N)
	 * @param title 회의 제목. 조회시 동일 검색이 아닌 포함 검색 적용
	 * @param scheduleHost 회의 주관자. 조회시 동일 검색이 아닌 포함 검색 적용
	 * @param attendeeName 회의 주관자. 조회시 동일 검색이 아닌 포함 검색 적용
	 * @param startDate 기간 검색을 위한 시작일자(yyyy-MM-dd)
	 * @param endDate 기간 검색을 위한 종료일자(yyyy-MM-dd)
	 * @param pageNo 페이지 번호
	 * @param pageCnt 페이지당 표시할 개수. pageNo는 유효하고 pageCnt가 NULL인 경우 10을 기본값으로 사용
	 * @param orderColumn 정렬 대상
	 * @param orderDirection 정렬 순서
	 * 
	 * @see {@link RoomType}
	 * @see {@link ApprovalStatus}
	 * @return
	 */
	@GetMapping("/meeting/assign/manage/dept")
	public List<MeetingAssignDTO> getDeptAssignList(
			@RequestParam @Nullable RoomType roomType,
			@RequestParam @Nullable ApprovalStatus approvalStatus,
			@RequestParam @Nullable MeetingStatus meetingStatus,
			@RequestParam @Nullable Character elecYN,
			@RequestParam @Nullable Character secretYN,
			@RequestParam @Nullable String title,
			@RequestParam @Nullable String scheduleHost,
			@RequestParam @Nullable String attendeeName,
			@RequestParam @Nullable String startDate, 
			@RequestParam @Nullable String endDate,
			@RequestParam @Nullable Integer pageNo,
			@RequestParam @Nullable Integer pageCnt,
			@RequestParam @Nullable String orderColumn,
			@RequestParam @Nullable OrderDirection orderDirection) {
		if(roomType == RoomType.ALL_ROOM) {
			roomType = null;
		}
		String deptId = LimeSecurityUtil.getDeptId();
		LocalDate validSdate = DateTimeUtil.toFormattedDate(startDate);
		LocalDate validEdate = DateTimeUtil.toFormattedDate(endDate);
		List<MeetingAssignVO> voList = assignServ.getMeetingAssignList(MeetingAssignVO.builder()
				.deptId(deptId).roomType(roomType).approvalStatus(approvalStatus).meetingStatus(meetingStatus).elecYN(elecYN).secretYN(secretYN)
				.title(title).scheduleHost(scheduleHost).attendeeName(attendeeName).startDate(validSdate).endDate(validEdate).pageNo(pageNo).pageCnt(pageCnt).orderDirection(orderDirection).build());
		return voList.stream().map(MeetingAssignVO::convert).collect(Collectors.toList());
	}
}
