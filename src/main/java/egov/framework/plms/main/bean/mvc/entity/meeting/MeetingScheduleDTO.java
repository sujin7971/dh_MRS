package egov.framework.plms.main.bean.mvc.entity.meeting;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingScheduleModelDTO;
import egov.framework.plms.main.bean.mvc.entity.organization.abst.UserInfoModelDTO;
import egov.framework.plms.main.bean.mvc.entity.organization.abst.UserInfoModelVO;
import egov.framework.plms.main.bean.mvc.entity.room.abst.RoomInfoModelDTO;
import egov.framework.plms.main.bean.mvc.entity.room.abst.RoomInfoModelVO;
import egov.framework.plms.main.core.model.enums.meeting.ApprovalStatus;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import egov.framework.plms.main.core.model.enums.meeting.ScheduleType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MeetingScheduleDTO extends MeetingScheduleModelDTO{
	private Integer scheduleId;
	private Integer meetingId;
	private String externalReservationId;
	private String writerId;
	private UserInfoModelDTO writer;
	private String officeCode;
	private String deptId;
	private RoomType roomType;
	private Integer roomId;
	private RoomInfoModelDTO room;
	private Integer attendeeCnt;
	private String userDefinedLocation;
	private String scheduleHost;
	private ScheduleType scheduleType;
	private ApprovalStatus approvalStatus;
	private String approvalComment;
	
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
	private LocalDateTime expDateTime;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss") 
	@JsonDeserialize(using = LocalDateTimeDeserializer.class) 
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime regDateTime;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss") 
	@JsonDeserialize(using = LocalDateTimeDeserializer.class) 
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime modDateTime;

	private Character delYN;
	
	@Builder(builderClassName = "init", builderMethodName = "initDTO")
	public MeetingScheduleDTO(MeetingScheduleVO vo){
		if(vo == null) {
			return;
		}
		this.scheduleId = vo.getScheduleId();
		this.writerId = vo.getWriterId();
		this.writer = Optional.ofNullable(vo.getWriter()).map(UserInfoModelVO::convert).orElseGet(()->null);
		this.meetingId = vo.getMeetingId();
		this.officeCode = vo.getOfficeCode();
		this.deptId = vo.getDeptId();
		this.externalReservationId = vo.getExternalReservationId();
		this.roomType = vo.getRoomType();
		this.roomId = vo.getRoomId();
		this.room = Optional.ofNullable(vo.getRoom()).map(RoomInfoModelVO::convert).orElseGet(()->null);
		this.attendeeCnt = vo.getAttendeeCnt();
		this.userDefinedLocation = vo.getUserDefinedLocation();
		this.scheduleHost = vo.getScheduleHost();
		this.scheduleType = vo.getScheduleType();
		this.approvalStatus = vo.getApprovalStatus();
		this.approvalComment = vo.getApprovalComment();
		this.holdingDate = vo.getHoldingDate();
		this.beginDateTime = vo.getBeginDateTime();
		this.finishDateTime = vo.getFinishDateTime();
		this.regDateTime = vo.getRegDateTime();
		this.modDateTime = vo.getModDateTime();
		this.expDateTime = vo.getExpDateTime();
		this.delYN = vo.getDelYN();
	}
	
	@Override
	public MeetingScheduleVO convert() {
		return MeetingScheduleVO.initVO().dto(this).build();
	}
	
	public String stringify() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
	}
}
