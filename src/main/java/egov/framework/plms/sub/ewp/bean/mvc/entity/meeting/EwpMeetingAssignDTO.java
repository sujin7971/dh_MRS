package egov.framework.plms.sub.ewp.bean.mvc.entity.meeting;

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
public class EwpMeetingAssignDTO extends MeetingAssignModelDTO implements Pageable {
	/********Schedule********/
	private Integer skdKey;
	private String writerKey;
	private EwpUserInfoDTO writer;
	private Integer reqKey;
	private String officeCode;
	private String deptId;
	private RoomType roomType;
	private Integer roomKey;
	private EwpRoomInfoDTO room;
	private ScheduleType skdType;
	private String skdHost;
	private Integer attendeeCnt;
	private ApprovalStatus appStatus;
	private String appComment;
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
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss") 
	@JsonDeserialize(using = LocalDateTimeDeserializer.class) 
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
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
	private String attendee;
	private List<EwpMeetingAttendeeDTO> attendeeList;
	/********paging********/
	private Integer rowNum;
	private LocalDate startDate;
	private LocalDate endDate;
	private Integer pageNo;
	private Integer pageCnt;
	private OrderColumn orderColumn;
	private OrderDirection orderDirection;
	
	@Builder(builderClassName = "init", builderMethodName = "initDTO")
	public EwpMeetingAssignDTO(EwpMeetingAssignVO vo){
		this.skdKey = vo.getSkdKey();
		this.writerKey = vo.getWriterKey();
		this.writer = Optional.ofNullable(vo.getWriter()).map(EwpUserInfoVO::convert).orElseGet(()->null);
		this.reqKey = vo.getReqKey();
		this.officeCode = vo.getOfficeCode();
		this.deptId = vo.getDeptId();
		this.roomType = vo.getRoomType();
		this.roomKey = vo.getRoomKey();
		this.room = Optional.ofNullable(vo.getRoom()).map(EwpRoomInfoVO::convert).orElseGet(()->null);
		this.skdType = vo.getSkdType();
		this.skdHost = vo.getSkdHost();
		this.attendeeCnt = vo.getAttendeeCnt();
		this.appStatus = vo.getAppStatus();
		this.appComment = vo.getAppComment();
		this.holdingDate = vo.getHoldingDate();
		this.beginDateTime = vo.getBeginDateTime();
		this.finishDateTime = vo.getFinishDateTime();
		this.expDateTime = vo.getExpDateTime();
		this.regDateTime = vo.getRegDateTime();
		
		this.meetingKey = vo.getMeetingKey();
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
		
		this.attendeeList = Optional.ofNullable(vo.getAttendeeList()).map(list -> list.stream().map(EwpMeetingAttendeeVO::convert).collect(Collectors.toList())).orElse(null);
		
		this.delYN = vo.getDelYN();
		
		this.rowNum = vo.getRowNum();
		this.startDate = vo.getStartDate();
		this.endDate = vo.getEndDate();
		this.pageNo = vo.getPageNo();
		this.pageCnt = vo.getPageCnt();
	}

	@Override
	public EwpMeetingAssignVO convert() {
		return EwpMeetingAssignVO.initVO().dto(this).build();
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
	public void setScheduleId(Integer value) {
		// TODO Auto-generated method stub
		this.skdKey = value;
	}

	@Override
	public void setMeetingId(Integer value) {
		// TODO Auto-generated method stub
		this.meetingKey = value;
	}

	@Override
	public void setExternalReservationId(String value) {
		// TODO Auto-generated method stub
		try {
			this.reqKey = Integer.parseInt(value);
		}catch(Exception e) {
			this.reqKey = null;
		}
	}

	@Override
	public void setWriterId(String value) {
		// TODO Auto-generated method stub
		this.writerKey = value;
	}

	@Override
	public void setRoomId(Integer value) {
		// TODO Auto-generated method stub
		this.roomKey = value;
	}

	@Override
	public void setUserDefinedLocation(String value) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setScheduleHost(String value) {
		// TODO Auto-generated method stub
		this.skdHost = value;
	}

	@Override
	public void setScheduleType(ScheduleType value) {
		// TODO Auto-generated method stub
		this.skdType = value;
	}

	@Override
	public void setApprovalStatus(ApprovalStatus value) {
		// TODO Auto-generated method stub
		this.appStatus = value;
	}

	@Override
	public void setHoldingDate(LocalDate value) {
		// TODO Auto-generated method stub
		this.holdingDate = value;
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

	/**
	 * 다음 값들을 필터링합니다<br>
	 * {@link #meetingKey}, {@link #reqKey}, {@link #writerKey}, 
	 * {@link #officeCode}, {@link #deptId}, {@link #roomType}, {@link #roomKey},
	 * {@link #attendeeList}, 
	 * <br>{@link #attendeeCnt}, {@link #elecYN}, {@link #secretYN}
	 * <br>{@link #contents}, {@link #appComment}, {@link #expDateTime},
	 * {@link #hostSecuLvl}, {@link #attendeeSecuLvl}, {@link #observerSecuLvl}, 
	 * {@link #mailYN}, {@link #smsYN}, {@link #messengerYN}, {@link #delYN},
	 * <br>{@link #stickyBit}
	 */
	@Override
	public EwpMeetingAssignDTO filterForEssential() {
		EwpMeetingAssignDTO filteredDTO = (EwpMeetingAssignDTO) super.filterForEssential();
		if(filteredDTO.getWriter() != null) {
			filteredDTO.setWriter(filteredDTO.getWriter().filterForEssential());
		}
		if(filteredDTO.getRoom() != null) {
			filteredDTO.setRoom(filteredDTO.getRoom().filterForEssential());
		}
		filteredDTO.setAttendeeList(null);
		return filteredDTO;
	}

	/**
	 * 다음 값들을 필터링합니다<br>
	 * {@link #attendeeCnt}, {@link #elecYN}, {@link #secretYN}
	 * <br>{@link #contents}, {@link #appComment}, {@link #expDateTime},
	 * {@link #hostSecuLvl}, {@link #attendeeSecuLvl}, {@link #observerSecuLvl}, 
	 * {@link #mailYN}, {@link #smsYN}, {@link #messengerYN}, {@link #delYN},
	 * <br>{@link #stickyBit}
	 */
	@Override
	public EwpMeetingAssignDTO filterForBasic() {
		EwpMeetingAssignDTO filteredDTO = (EwpMeetingAssignDTO) super.filterForBasic();
		if(filteredDTO.getWriter() != null) {
			filteredDTO.setWriter(filteredDTO.getWriter().filterForBasic());
		}
		if(filteredDTO.getRoom() != null) {
			filteredDTO.setRoom(filteredDTO.getRoom().filterForBasic());
		}
		if(filteredDTO.getAttendeeList() != null) {
			filteredDTO.setAttendeeList(filteredDTO.getAttendeeList().stream().map(EwpMeetingAttendeeDTO::filterForBasic).collect(Collectors.toList()));
		}
		
		return filteredDTO;
	}

	/**
	 * 다음 값들을 필터링합니다<br>
	 * {@link #contents}, {@link #appComment}, {@link #expDateTime},
	 * {@link #hostSecuLvl}, {@link #attendeeSecuLvl}, {@link #observerSecuLvl}, 
	 * {@link #mailYN}, {@link #smsYN}, {@link #messengerYN}, {@link #delYN},
	 * <br>{@link #stickyBit}
	 */
	@Override
	public EwpMeetingAssignDTO filterForDetailed() {
		EwpMeetingAssignDTO filteredDTO = (EwpMeetingAssignDTO) super.filterForDetailed();
		if(filteredDTO.getWriter() != null) {
			filteredDTO.setWriter(filteredDTO.getWriter().filterForDetailed());
		}
		if(filteredDTO.getRoom() != null) {
			filteredDTO.setRoom(filteredDTO.getRoom().filterForDetailed());
		}
		if(filteredDTO.getAttendeeList() != null) {
			filteredDTO.setAttendeeList(filteredDTO.getAttendeeList().stream().map(EwpMeetingAttendeeDTO::filterForDetailed).collect(Collectors.toList()));
		}
		filteredDTO.setAppComment(null);
		return filteredDTO;
	}
	/**
	 * 다음 값들을 필터링합니다<br>
	 * {@link #stickyBit}
	 */
	@Override
	public EwpMeetingAssignDTO filterForSensitive() {
		EwpMeetingAssignDTO filteredDTO = (EwpMeetingAssignDTO) super.filterForSensitive();
		if(filteredDTO.getWriter() != null) {
			filteredDTO.setWriter(filteredDTO.getWriter().filterForSensitive());
		}
		if(filteredDTO.getRoom() != null) {
			filteredDTO.setRoom(filteredDTO.getRoom().filterForSensitive());
		}
		if(filteredDTO.getAttendeeList() != null) {
			filteredDTO.setAttendeeList(filteredDTO.getAttendeeList().stream().map(EwpMeetingAttendeeDTO::filterForSensitive).collect(Collectors.toList()));
		}
		return filteredDTO;
	}
	/**
	 * 다음 값들을 필터링합니다<br>
	 * {@link #stickyBit}
	 */
	@Override
	public EwpMeetingAssignDTO filterForHighest() {
		EwpMeetingAssignDTO filteredDTO = (EwpMeetingAssignDTO) super.filterForHighest();
		if(filteredDTO.getWriter() != null) {
			filteredDTO.setWriter(filteredDTO.getWriter().filterForHighest());
		}
		if(filteredDTO.getRoom() != null) {
			filteredDTO.setRoom(filteredDTO.getRoom().filterForHighest());
		}
		if(filteredDTO.getAttendeeList() != null) {
			filteredDTO.setAttendeeList(filteredDTO.getAttendeeList().stream().map(EwpMeetingAttendeeDTO::filterForHighest).collect(Collectors.toList()));
		}
		filteredDTO.setStickyBit(null);
		return filteredDTO;
	}
	
	public EwpMeetingAssignDTO filterForApproval() {
		EwpMeetingAssignDTO filteredDTO = (EwpMeetingAssignDTO) super.filterForHighest();
		if(filteredDTO.getWriter() != null) {
			filteredDTO.setWriter(filteredDTO.getWriter().filterForDetailed());
		}
		if(filteredDTO.getRoom() != null) {
			filteredDTO.setRoom(filteredDTO.getRoom().filterForHighest());
		}
		filteredDTO.setAttendeeList(null);
		filteredDTO.setStickyBit(null);
		filteredDTO.setRegDateTime(null);
		filteredDTO.setExpDateTime(null);
		filteredDTO.setHostSecuLvl(null);
		filteredDTO.setAttendeeSecuLvl(null);
		filteredDTO.setObserverSecuLvl(null);
		filteredDTO.setMessengerYN(null);
		filteredDTO.setMailYN(null);
		filteredDTO.setSmsYN(null);
		filteredDTO.setDelYN(null);
		filteredDTO.setElecYN(null);
		filteredDTO.setSecretYN(null);
		return filteredDTO;
	}
}
