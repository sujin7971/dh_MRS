package egov.framework.plms.sub.ewp.bean.mvc.entity.meeting;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingAssignModelVO;
import egov.framework.plms.main.core.model.able.Pageable;
import egov.framework.plms.main.core.model.enums.common.OrderColumn;
import egov.framework.plms.main.core.model.enums.common.OrderDirection;
import egov.framework.plms.main.core.model.enums.meeting.ApprovalStatus;
import egov.framework.plms.main.core.model.enums.meeting.MeetingStatus;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import egov.framework.plms.main.core.model.enums.meeting.ScheduleType;
import egov.framework.plms.sub.ewp.bean.mvc.entity.organization.EwpUserInfoDTO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.organization.EwpUserInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.room.EwpRoomInfoDTO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.room.EwpRoomInfoVO;
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
public class EwpMeetingAssignVO extends MeetingAssignModelVO implements Pageable{
	/********Schedule********/
	private Integer skdKey;
	private String writerKey;
	private EwpUserInfoVO writer;
	private Integer reqKey;
	private String officeCode;
	private String deptId;
	private RoomType roomType;
	private Integer roomKey;
	private EwpRoomInfoVO room;
	private ScheduleType skdType;
	private String skdHost;
	private Integer attendeeCnt;
	private ApprovalStatus appStatus;
	private String appComment;
	private LocalDate holdingDate;
	private LocalDateTime beginDateTime;
	private LocalDateTime finishDateTime;
	private LocalDateTime expDateTime;
	private LocalDateTime regDateTime;
	/********Meeting********/
	private Integer meetingKey;
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
	private String userKey;
	private String attendeeName;
	private List<EwpMeetingAttendeeVO> attendeeList;
	/********paging********/
	private Integer rowNum;
	private LocalDate startDate;
	private LocalDate endDate;
	private Integer pageNo;
	private Integer pageCnt;
	
	private OrderColumn orderColumn;
	private OrderDirection orderDirection;
	
	@Builder(builderClassName = "init", builderMethodName = "initVO")
	public EwpMeetingAssignVO(EwpMeetingAssignDTO dto){
		this.skdKey = dto.getSkdKey();
		this.writerKey = dto.getWriterKey();
		this.writer = Optional.ofNullable(dto.getWriter()).map(EwpUserInfoDTO::convert).orElseGet(()->null);
		this.reqKey = dto.getReqKey();
		this.officeCode = dto.getOfficeCode();
		this.deptId = dto.getDeptId();
		this.roomType = dto.getRoomType();
		this.roomKey = dto.getRoomKey();
		this.room = Optional.ofNullable(dto.getRoom()).map(EwpRoomInfoDTO::convert).orElseGet(()->null);
		this.skdType = dto.getSkdType();
		this.skdHost = dto.getSkdHost();
		this.attendeeCnt = dto.getAttendeeCnt();
		this.appStatus = dto.getAppStatus();
		this.appComment = dto.getAppComment();
		this.holdingDate = dto.getHoldingDate();
		this.beginDateTime = dto.getBeginDateTime();
		this.finishDateTime = dto.getFinishDateTime();
		this.expDateTime = dto.getExpDateTime();
		this.regDateTime = dto.getRegDateTime();
		
		this.meetingKey = dto.getMeetingKey();
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
		
		this.attendeeList = Optional.ofNullable(dto.getAttendeeList()).map(list -> list.stream().map(EwpMeetingAttendeeDTO::convert).collect(Collectors.toList())).orElse(null);
		
		this.delYN = dto.getDelYN();
		
		this.rowNum = dto.getRowNum();
		this.startDate = dto.getStartDate();
		this.endDate = dto.getEndDate();
		this.pageNo = dto.getPageNo();
		this.pageCnt = dto.getPageCnt();
	}

	@Override
	public EwpMeetingAssignDTO convert() {
		return EwpMeetingAssignDTO.initDTO().vo(this).build();
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
	
	public ScheduleType getSkdType() {
		if(this.skdType != null) {
			return this.skdType;
		}else if(this.elecYN != null) {
			return (this.elecYN == 'Y')?ScheduleType.FORMAL:ScheduleType.RENTAL;
		}
		return null;
	}
	
	public LocalDate getHoldingDate() {
		if(this.holdingDate != null) {
			return this.holdingDate;
		}else if(this.beginDateTime != null){
			return this.beginDateTime.toLocalDate();
		}else if(this.finishDateTime != null) {
			return this.finishDateTime.toLocalDate();
		}else {
			return null;
		}
	}
	
	@Override
	public Integer getScheduleId() {
		// TODO Auto-generated method stub
		return this.skdKey;
	}

	@Override
	public Integer getMeetingId() {
		// TODO Auto-generated method stub
		return this.meetingKey;
	}

	@Override
	public String getExternalReservationId() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.reqKey).map(key -> key.toString()).orElse(null);
	}

	@Override
	public String getWriterId() {
		// TODO Auto-generated method stub
		return this.writerKey;
	}

	@Override
	public Integer getRoomId() {
		// TODO Auto-generated method stub
		return this.roomKey;
	}

	@Override
	public String getUserDefinedLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getScheduleHost() {
		// TODO Auto-generated method stub
		return this.skdHost;
	}

	@Override
	public ScheduleType getScheduleType() {
		// TODO Auto-generated method stub
		return this.skdType;
	}

	@Override
	public ApprovalStatus getApprovalStatus() {
		// TODO Auto-generated method stub
		return this.appStatus;
	}

	@Override
	public EwpMeetingScheduleVO getMeetingSchedule() {
		// TODO Auto-generated method stub
		return EwpMeetingScheduleVO.builder()
				.skdKey(this.skdKey)
				.meetingKey(this.meetingKey)
				.writerKey(this.writerKey)
				.writer(this.writer)
				.officeCode(this.officeCode)
				.deptId(this.deptId)
				.reqKey(this.reqKey)
				.roomType(this.roomType)
				.roomKey(this.roomKey)
				.room(this.room)
				.attendeeCnt(this.attendeeCnt)
				.skdHost(this.skdHost)
				.skdType(this.getSkdType())
				.skdStatus(this.appStatus)
				.skdComment(this.appComment)
				.holdingDate(this.holdingDate)
				.beginDateTime(this.beginDateTime)
				.finishDateTime(this.finishDateTime)
				.expDateTime(this.expDateTime)
				.delYN(this.delYN)
				.build();
	}

	@Override
	public EwpMeetingInfoVO getMeetingInfo() {
		// TODO Auto-generated method stub
		return EwpMeetingInfoVO.builder()
				.meetingKey(this.meetingKey)
				.writerKey(this.writerKey)
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
