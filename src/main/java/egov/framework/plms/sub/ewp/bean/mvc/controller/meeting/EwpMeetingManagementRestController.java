package egov.framework.plms.sub.ewp.bean.mvc.controller.meeting;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import egov.framework.plms.main.core.model.enums.common.OrderDirection;
import egov.framework.plms.main.core.model.enums.meeting.ApprovalStatus;
import egov.framework.plms.main.core.model.enums.meeting.MeetingStatus;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import egov.framework.plms.main.core.util.DateTimeUtil;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingArchiveDTO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingArchiveVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingAssignDTO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingAssignVO;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingAssignService;
import egov.framework.plms.sub.ewp.core.model.enums.FileRole;
import egov.framework.plms.sub.ewp.core.util.EwpSecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * 사용신청목록 / 파일함 페이지 관련 AJAX 요청 처리 
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 25
 */
@Slf4j
@RestController
@RequestMapping("/api/ewp")
@RequiredArgsConstructor
@Profile("ewp")
public class EwpMeetingManagementRestController {
	private final EwpMeetingAssignService meetServ;
	
	/**
	 * 개인 사용신청목록 페이지 검색 요청(Maria DB)
	 * @param authentication
	 * @param roomType {@link RoomType#ALL_ROOM}, {@link RoomType#MEETING_ROOM}, {@link RoomType#EDU_ROOM}
	 * @param approvalStatus {@link ApprovalStatus#REQUEST}, {@link ApprovalStatus#APPROVED}, {@link ApprovalStatus#CANCEL}, {@link ApprovalStatus#REJECT}
	 * @param meetingStatus 회의 진행상태 {@link MeetingStatus#UNAPPROVAL}, {@link MeetingStatus#RESERVE}, {@link MeetingStatus#START}, {@link MeetingStatus#END}, {@link MeetingStatus#DROP}, {@link MeetingStatus#CANCEL}
	 * @param elecYN 전자회의 여부(Y/N)
	 * @param secretYN 기밀회의 여부(Y/N)
	 * @param title 회의 제목. 조회시 동일 검색이 아닌 포함 검색 적용
	 * @param host 회의 주관자. 조회시 동일 검색이 아닌 포함 검색 적용
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
	public List<EwpMeetingAssignDTO> getUserAssignList(
			@RequestParam @Nullable RoomType roomType,
			@RequestParam @Nullable ApprovalStatus approvalStatus,
			@RequestParam @Nullable MeetingStatus meetingStatus,
			@RequestParam @Nullable Character elecYN,
			@RequestParam @Nullable Character secretYN,
			@RequestParam @Nullable String title,
			@RequestParam @Nullable String host,
			@RequestParam @Nullable String writerKey,
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
		String loginId = EwpSecurityUtil.getLoginId();
		LocalDate validSdate = DateTimeUtil.toFormattedDate(startDate);
		LocalDate validEdate = DateTimeUtil.toFormattedDate(endDate);
		List<EwpMeetingAssignVO> voList = meetServ.getMeetingAssignList(EwpMeetingAssignVO.builder()
				.userKey(loginId).roomType(roomType).appStatus(approvalStatus).meetingStatus(meetingStatus).elecYN(elecYN).secretYN(secretYN)
				.title(title).skdHost(host).writerKey(writerKey).attendeeName(attendeeName).startDate(validSdate).endDate(validEdate).pageNo(pageNo).pageCnt(pageCnt).orderDirection(orderDirection).build());
		return voList.stream().map(EwpMeetingAssignVO::convert).collect(Collectors.toList());
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
	 * @param host 회의 주관자. 조회시 동일 검색이 아닌 포함 검색 적용
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
	public List<EwpMeetingAssignDTO> getDeptAssignList(
			@RequestParam @Nullable RoomType roomType,
			@RequestParam @Nullable ApprovalStatus approvalStatus,
			@RequestParam @Nullable MeetingStatus meetingStatus,
			@RequestParam @Nullable Character elecYN,
			@RequestParam @Nullable Character secretYN,
			@RequestParam @Nullable String title,
			@RequestParam @Nullable String host,
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
		String deptId = EwpSecurityUtil.getDeptId();
		LocalDate validSdate = DateTimeUtil.toFormattedDate(startDate);
		LocalDate validEdate = DateTimeUtil.toFormattedDate(endDate);
		List<EwpMeetingAssignVO> voList = meetServ.getMeetingAssignList(EwpMeetingAssignVO.builder()
				.deptId(deptId).roomType(roomType).appStatus(approvalStatus).meetingStatus(meetingStatus).elecYN(elecYN).secretYN(secretYN)
				.title(title).skdHost(host).attendeeName(attendeeName).startDate(validSdate).endDate(validEdate).pageNo(pageNo).pageCnt(pageCnt).orderDirection(orderDirection).build());
		return voList.stream().map(EwpMeetingAssignVO::convert).collect(Collectors.toList());
	}
	
	/**
	 * 개인 파일함 페이지 검색 요청. 
	 * @param authentication 요청한 사용자의 고유키를 조회하기 위한 인증 객체
	 * @param skdHost 주관자. LIKE검색
	 * @param title 제목(사용목적). LIKE검색
	 * @param originalName 파일명. LIKE검색
	 * @param roleType {@link FileRole#COPY}을 요청 한 경우 판서본을 가진 회의에 대한 검색 결과를 제공.
	 * @param startDate 기간 검색을 위한 시작일자(yyyy-MM-dd)
	 * @param endDate 기간 검색을 위한 종료일자(yyyy-MM-dd)
	 * @param pageNo 페이지 번호
	 * @param pageCnt 페이지당 표시할 개수. pageNo는 유효하고 pageCnt가 NULL인 경우 10을 기본값으로 사용
	 * @return
	 */
	@GetMapping("/meeting/archive/manage/user")
	public List<EwpMeetingArchiveDTO> getUserArchiveList(
			@RequestParam @Nullable String skdHost, 
			@RequestParam @Nullable String title, 
			@RequestParam @Nullable String originalName, 
			@RequestParam @Nullable FileRole roleType, 
			@RequestParam String startDate, 
			@RequestParam String endDate,
			@RequestParam @Nullable Integer pageNo,
			@RequestParam @Nullable Integer pageCnt) {
		String loginId = EwpSecurityUtil.getLoginId();
		LocalDate validSdate = DateTimeUtil.toFormattedDate(startDate);
		LocalDate validEdate = DateTimeUtil.toFormattedDate(endDate);
		List<EwpMeetingArchiveVO> voList = meetServ.getMeetingArchiveList(EwpMeetingArchiveVO.builder()
				.userKey(loginId).skdHost(skdHost).title(title).originalName(originalName)
				.roleType(roleType).startDate(validSdate).endDate(validEdate).pageNo(pageNo).pageCnt(pageCnt).build());
		return voList.stream().map(EwpMeetingArchiveVO::convert).collect(Collectors.toList());
	}
	
	/**
	 * 부서 파일함 페이지 검색 요청. 
	 * @param authentication 요청한 사용자의 소속 부서키를 조회하기 위한 인증 객체
	 * @param skdHost 주관자. LIKE검색
	 * @param title 제목(사용목적). LIKE검색
	 * @param originalName 파일명. LIKE검색
	 * @param startDate 기간 검색을 위한 시작일자(yyyy-MM-dd)
	 * @param endDate 기간 검색을 위한 종료일자(yyyy-MM-dd)
	 * @param pageNo 페이지 번호
	 * @param pageCnt 페이지당 표시할 개수. pageNo는 유효하고 pageCnt가 NULL인 경우 10을 기본값으로 사용
	 * @return
	 */
	@GetMapping("/meeting/archive/manage/dept")
	public List<EwpMeetingArchiveDTO> getDeptArchiveList(
			@RequestParam @Nullable String skdHost, 
			@RequestParam @Nullable String title, 
			@RequestParam @Nullable String originalName, 
			@RequestParam String startDate, 
			@RequestParam String endDate,
			@RequestParam @Nullable Integer pageNo,
			@RequestParam @Nullable Integer pageCnt) {
		String deptId = EwpSecurityUtil.getDeptId();
		LocalDate validSdate = DateTimeUtil.toFormattedDate(startDate);
		LocalDate validEdate = DateTimeUtil.toFormattedDate(endDate);
		List<EwpMeetingArchiveVO> voList = meetServ.getMeetingArchiveList(EwpMeetingArchiveVO.builder()
				.deptId(deptId).skdHost(skdHost).title(title).originalName(originalName)
				.startDate(validSdate).endDate(validEdate).pageNo(pageNo).pageCnt(pageCnt).build());
		return voList.stream().map(EwpMeetingArchiveVO::convert).collect(Collectors.toList());
	}
	
	/**
	 * 관리자 부서 파일함 페이지 검색 요청. 
	 * @param authentication 요청한 사용자의 소속 부서키를 조회하기 위한 인증 객체
	 * @param skdHost 주관자. LIKE검색
	 * @param title 제목(사용목적). LIKE검색
	 * @param originalName 파일명. LIKE검색
	 * @param startDate 기간 검색을 위한 시작일자(yyyy-MM-dd)
	 * @param endDate 기간 검색을 위한 종료일자(yyyy-MM-dd)
	 * @param pageNo 페이지 번호
	 * @param pageCnt 페이지당 표시할 개수. pageNo는 유효하고 pageCnt가 NULL인 경우 10을 기본값으로 사용
	 * @return
	 */
	@GetMapping("/admin/master/meeting/archive/manage")
	public List<EwpMeetingArchiveDTO> getAdminArchiveList(Authentication authentication, 
			@RequestParam @Nullable String skdHost, 
			@RequestParam @Nullable String title, 
			@RequestParam @Nullable String originalName, 
			@RequestParam String startDate, 
			@RequestParam String endDate,
			@RequestParam @Nullable Integer pageNo,
			@RequestParam @Nullable Integer pageCnt) {
		LocalDate validSdate = DateTimeUtil.toFormattedDate(startDate);
		LocalDate validEdate = DateTimeUtil.toFormattedDate(endDate);
		List<EwpMeetingArchiveVO> voList = meetServ.getMeetingArchiveList(EwpMeetingArchiveVO.builder()
				.skdHost(skdHost).title(title).originalName(originalName)
				.startDate(validSdate).endDate(validEdate).pageNo(pageNo).pageCnt(pageCnt).build());
		return voList.stream().map(EwpMeetingArchiveVO::convert).collect(Collectors.toList());
	}
}
