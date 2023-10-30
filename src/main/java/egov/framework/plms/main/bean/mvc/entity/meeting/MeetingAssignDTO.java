package egov.framework.plms.main.bean.mvc.entity.meeting;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingAssignModelDTO;
import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingAttendeeModelDTO;
import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingAttendeeModelVO;
import egov.framework.plms.main.bean.mvc.entity.organization.abst.UserInfoModelDTO;
import egov.framework.plms.main.bean.mvc.entity.organization.abst.UserInfoModelVO;
import egov.framework.plms.main.bean.mvc.entity.room.abst.RoomInfoModelDTO;
import egov.framework.plms.main.bean.mvc.entity.room.abst.RoomInfoModelVO;
import egov.framework.plms.main.core.model.able.Pageable;
import egov.framework.plms.main.core.model.enums.common.OrderColumn;
import egov.framework.plms.main.core.model.enums.common.OrderDirection;
import egov.framework.plms.main.core.model.enums.meeting.ApprovalStatus;
import egov.framework.plms.main.core.model.enums.meeting.MeetingStatus;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import egov.framework.plms.main.core.model.enums.meeting.ScheduleType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * meeting_schedule 테이블과 meeting_info테이블의 조인 뷰 테이블 view_meeting_assign_info 데이터를 위한 VO 모델<br>
 * 사용신청 정보와 진행상황과 관련된 정보를 바탕으로 회의 진행 및 권한 부여를 위해 설계
 * 
 * @author mckim
 *
 */
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MeetingAssignDTO extends MeetingAssignModelDTO implements Pageable {
	/********Schedule********/
	private Integer scheduleId;
	private String writerId;
	private UserInfoModelDTO writer;
	private String externalReservationId;
	private String officeCode;
	private String deptId;
	private RoomType roomType;
	private Integer roomId;
	private RoomInfoModelDTO room;
	private String userDefinedLocation;
	private ScheduleType scheduleType;
	private String scheduleHost;
	private Integer attendeeCnt;
	private ApprovalStatus approvalStatus;
	private String approvalComment;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate holdingDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss") 
	@JsonDeserialize(using = LocalDateTimeDeserializer.class) 
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime beginDateTime;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss") 
	@JsonDeserialize(using = LocalDateTimeDeserializer.class) 
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
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
	private String attendee;
	private List<MeetingAttendeeModelDTO> attendeeList;
	/********paging********/
	private Integer rowNum;
	private LocalDate startDate;
	private LocalDate endDate;
	private Integer pageNo;
	private Integer pageCnt;
	private OrderColumn orderColumn;
	private OrderDirection orderDirection;
	
	@Builder(builderClassName = "init", builderMethodName = "initDTO")
	public MeetingAssignDTO(MeetingAssignVO vo){
		this.scheduleId = vo.getScheduleId();
		this.writerId = vo.getWriterId();
		this.writer = Optional.ofNullable(vo.getWriter()).map(UserInfoModelVO::convert).orElseGet(()->null);
		this.externalReservationId = vo.getExternalReservationId();
		this.officeCode = vo.getOfficeCode();
		this.deptId = vo.getDeptId();
		this.roomType = vo.getRoomType();
		this.roomId = vo.getRoomId();
		this.room = Optional.ofNullable(vo.getRoom()).map(RoomInfoModelVO::convert).orElseGet(()->null);
		this.userDefinedLocation = vo.getUserDefinedLocation();
		this.scheduleType = vo.getScheduleType();
		this.scheduleHost = vo.getScheduleHost();
		this.attendeeCnt = vo.getAttendeeCnt();
		this.approvalStatus = vo.getApprovalStatus();
		this.approvalComment = vo.getApprovalComment();
		this.holdingDate = vo.getHoldingDate();
		this.beginDateTime = vo.getBeginDateTime();
		this.finishDateTime = vo.getFinishDateTime();
		this.expDateTime = vo.getExpDateTime();
		this.regDateTime = vo.getRegDateTime();
		
		this.meetingId = vo.getMeetingId();
		this.title = vo.getTitle();
		this.contents = vo.getContents();
		this.meetingStatus = vo.getMeetingStatus();
		this.stickyBit = vo.getStickyBit();
		this.secretYN = vo.getSecretYN();
		this.hostSecuLvl = vo.getHostSecuLvl();
		this.attendeeSecuLvl = vo.getAttendeeSecuLvl();
		this.observerSecuLvl = vo.getObserverSecuLvl();
		this.elecYN = vo.getElecYN();
		this.messengerYN = vo.getMessengerYN();
		this.mailYN = vo.getMailYN();
		this.smsYN = vo.getSmsYN();
		
		this.attendeeList = Optional.ofNullable(vo.getAttendeeList()).map(list -> list.stream().map(MeetingAttendeeModelVO::convert).collect(Collectors.toList())).orElse(null);
		
		this.delYN = vo.getDelYN();
		
		this.rowNum = vo.getRowNum();
		this.startDate = vo.getStartDate();
		this.endDate = vo.getEndDate();
		this.pageNo = vo.getPageNo();
		this.pageCnt = vo.getPageCnt();
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

	@Override
	public MeetingAssignVO convert() {
		return MeetingAssignVO.initVO().dto(this).build();
	}

	@Override
	public MeetingAssignDTO filterForEssential() {
		// TODO Auto-generated method stub
		return (MeetingAssignDTO) super.filterForEssential();
	}

	@Override
	public MeetingAssignDTO filterForBasic() {
		// TODO Auto-generated method stub
		return (MeetingAssignDTO) super.filterForBasic();
	}

	@Override
	public MeetingAssignDTO filterForDetailed() {
		// TODO Auto-generated method stub
		return (MeetingAssignDTO) super.filterForDetailed();
	}

	@Override
	public MeetingAssignDTO filterForSensitive() {
		// TODO Auto-generated method stub
		return (MeetingAssignDTO) super.filterForSensitive();
	}

	@Override
	public MeetingAssignDTO filterForHighest() {
		// TODO Auto-generated method stub
		return (MeetingAssignDTO) super.filterForHighest();
	}
}
