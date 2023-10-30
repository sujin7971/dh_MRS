package egov.framework.plms.main.bean.mvc.entity.meeting;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingAssignModelVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingAttendeeModelDTO;
import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingAttendeeModelVO;
import egov.framework.plms.main.bean.mvc.entity.organization.abst.UserInfoModelDTO;
import egov.framework.plms.main.bean.mvc.entity.organization.abst.UserInfoModelVO;
import egov.framework.plms.main.bean.mvc.entity.room.abst.RoomInfoModelDTO;
import egov.framework.plms.main.bean.mvc.entity.room.abst.RoomInfoModelVO;
import egov.framework.plms.main.core.model.able.Pageable;
import egov.framework.plms.main.core.model.able.Sortable;
import egov.framework.plms.main.core.model.enums.common.OrderColumn;
import egov.framework.plms.main.core.model.enums.common.OrderDirection;
import egov.framework.plms.main.core.model.enums.meeting.ApprovalStatus;
import egov.framework.plms.main.core.model.enums.meeting.MeetingStatus;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import egov.framework.plms.main.core.model.enums.meeting.ScheduleType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * meeting_schedule 테이블과 meeting_info테이블의 조인 뷰 테이블 view_meeting_assign_info 데이터를 위한 VO 모델<br>
 * 사용신청 정보와 진행상황과 관련된 정보를 바탕으로 회의 진행 및 권한 부여를 위해 설계
 * 
 * @author mckim
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class MeetingAssignVO extends MeetingAssignModelVO implements Pageable {
	/********Schedule********/
	private Integer scheduleId;
	private String writerId;
	private UserInfoModelVO writer;
	private String externalReservationId;
	private String officeCode;
	private String deptId;
	private RoomType roomType;
	private Integer roomId;
	private RoomInfoModelVO room;
	private String userDefinedLocation;
	private ScheduleType scheduleType;
	private String scheduleHost;
	private Integer attendeeCnt;
	private ApprovalStatus approvalStatus;
	private String approvalComment;
	private LocalDate holdingDate;
	private LocalDateTime beginDateTime;
	private LocalDateTime finishDateTime;
	private LocalDateTime expDateTime;
	private LocalDateTime regDateTime;
	/********Meeting********/
	private Integer meetingId;
	private String title;
	private String contents;
	private MeetingStatus meetingStatus;
	private Character elecYN;
	private Character messengerYN;
	private Character mailYN;
	private Character smsYN;
	private Character secretYN;
	private Integer hostSecuLvl;
	private Integer attendeeSecuLvl;
	private Integer observerSecuLvl;
	private Integer stickyBit;
	private Character delYN;
	/********Attendee********/
	private String userId;
	private String attendeeName;
	private List<MeetingAttendeeModelVO> attendeeList;
	/********paging********/
	private Integer rowNum;
	private LocalDate startDate;
	private LocalDate endDate;
	private Integer pageNo;
	private Integer pageCnt;
	private OrderColumn orderColumn;
	private OrderDirection orderDirection;
	
	@Builder(builderClassName = "init", builderMethodName = "initVO")
	public MeetingAssignVO(MeetingAssignDTO dto){
		this.scheduleId = dto.getScheduleId();
		this.writerId = dto.getWriterId();
		this.writer = Optional.ofNullable(dto.getWriter()).map(UserInfoModelDTO::convert).orElseGet(()->null);
		this.externalReservationId = dto.getExternalReservationId();
		this.officeCode = dto.getOfficeCode();
		this.deptId = dto.getDeptId();
		this.roomType = dto.getRoomType();
		this.roomId = dto.getRoomId();
		this.room = Optional.ofNullable(dto.getRoom()).map(RoomInfoModelDTO::convert).orElseGet(()->null);
		this.userDefinedLocation = dto.getUserDefinedLocation();
		this.scheduleType = dto.getScheduleType();
		this.scheduleHost = dto.getScheduleHost();
		this.attendeeCnt = dto.getAttendeeCnt();
		this.approvalStatus = dto.getApprovalStatus();
		this.approvalComment = dto.getApprovalComment();
		this.holdingDate = dto.getHoldingDate();
		this.beginDateTime = dto.getBeginDateTime();
		this.finishDateTime = dto.getFinishDateTime();
		this.expDateTime = dto.getExpDateTime();
		this.regDateTime = dto.getRegDateTime();
		
		this.meetingId = dto.getMeetingId();
		this.title = dto.getTitle();
		this.contents = dto.getContents();
		this.meetingStatus = dto.getMeetingStatus();
		this.stickyBit = dto.getStickyBit();
		this.secretYN = dto.getSecretYN();
		this.hostSecuLvl = dto.getHostSecuLvl();
		this.attendeeSecuLvl = dto.getAttendeeSecuLvl();
		this.observerSecuLvl = dto.getObserverSecuLvl();
		this.messengerYN = dto.getMessengerYN();
		this.elecYN = dto.getElecYN();
		this.mailYN = dto.getMailYN();
		this.smsYN = dto.getSmsYN();
		
		this.attendeeList = Optional.ofNullable(dto.getAttendeeList()).map(list -> list.stream().map(MeetingAttendeeModelDTO::convert).collect(Collectors.toList())).orElse(null);
		
		this.delYN = dto.getDelYN();
		
		this.rowNum = dto.getRowNum();
		this.startDate = dto.getStartDate();
		this.endDate = dto.getEndDate();
		this.pageNo = dto.getPageNo();
		this.pageCnt = dto.getPageCnt();
	}

	@Override
	public MeetingAssignDTO convert() {
		return MeetingAssignDTO.initDTO().vo(this).build();
	}
	
	@Override
	public LocalTime getBeginTime() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.beginDateTime).map(LocalDateTime::toLocalTime).orElse(null);
	}

	@Override
	public LocalTime getFinishTime() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.finishDateTime).map(LocalDateTime::toLocalTime).orElse(null);
	}
	
	public ScheduleType getScheduleType() {
		if(this.scheduleType != null) {
			return this.scheduleType;
		}else if(this.elecYN != null) {
			return (this.elecYN == 'Y')?ScheduleType.FORMAL:ScheduleType.RENTAL;
		}
		return null;
	}

	public Integer getOffset() {
		if(this.pageNo != null && this.pageCnt != null) {
			return (this.pageNo - 1) * this.pageCnt;
		}else {
			return null;
		}
	}
	
	public Integer getLimit() {
		if(this.pageCnt != null) {
			return this.pageCnt;
		}else {
			return 10;
		}
	}
	
	@Override
	public MeetingScheduleVO getMeetingSchedule() {
		return MeetingScheduleVO.builder()
				.scheduleId(this.scheduleId)
				.meetingId(this.meetingId)
				.writerId(this.writerId)
				.writer(this.writer)
				.officeCode(this.officeCode)
				.deptId(this.deptId)
				.externalReservationId(this.externalReservationId)
				.roomType(this.roomType)
				.roomId(this.roomId)
				.room(this.room)
				.attendeeCnt(this.attendeeCnt)
				.userDefinedLocation(this.userDefinedLocation)
				.scheduleHost(this.scheduleHost)
				.scheduleType(this.scheduleType)
				.approvalStatus(this.approvalStatus)
				.approvalComment(this.approvalComment)
				.holdingDate(this.holdingDate)
				.beginDateTime(this.beginDateTime)
				.finishDateTime(this.finishDateTime)
				.expDateTime(this.expDateTime)
				.delYN(this.delYN)
				.build();
	}
	@Override
	public MeetingInfoVO getMeetingInfo() {
		return MeetingInfoVO.builder()
				.meetingId(this.meetingId)
				.writerId(this.writerId)
				.writer(this.writer)
				.title(this.title)
				.contents(this.contents)
				.meetingStatus(this.meetingStatus)
				.stickyBit(this.stickyBit)
				.secretYN(this.secretYN)
				.hostSecuLvl(this.hostSecuLvl)
				.attendeeSecuLvl(this.attendeeSecuLvl)
				.observerSecuLvl(this.observerSecuLvl)
				.elecYN(this.elecYN)
				.messengerYN(this.messengerYN)
				.mailYN(this.mailYN)
				.smsYN(this.smsYN)
				.delYN(this.delYN)
				.build();
	}

}
