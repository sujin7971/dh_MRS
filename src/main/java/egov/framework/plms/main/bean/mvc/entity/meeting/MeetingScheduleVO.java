package egov.framework.plms.main.bean.mvc.entity.meeting;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingScheduleModelVO;
import egov.framework.plms.main.bean.mvc.entity.organization.abst.UserInfoModelDTO;
import egov.framework.plms.main.bean.mvc.entity.organization.abst.UserInfoModelVO;
import egov.framework.plms.main.bean.mvc.entity.room.abst.RoomInfoModelDTO;
import egov.framework.plms.main.bean.mvc.entity.room.abst.RoomInfoModelVO;
import egov.framework.plms.main.core.model.enums.meeting.ApprovalStatus;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import egov.framework.plms.main.core.model.enums.meeting.ScheduleType;
import egov.framework.plms.main.core.model.response.ErrorMessage;
import egov.framework.plms.main.core.model.response.ResponseMessage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class MeetingScheduleVO extends MeetingScheduleModelVO{
	private Integer scheduleId;
	private Integer meetingId;
	private String writerId;
	private String externalReservationId;
	private UserInfoModelVO writer;
	private String officeCode;
	private String deptId;
	private RoomType roomType;
	private Integer roomId;
	private RoomInfoModelVO room;
	private Integer attendeeCnt;
	private String userDefinedLocation;
	private String scheduleHost;
	private ScheduleType scheduleType;
	private ApprovalStatus approvalStatus;
	private String approvalComment;
	private LocalDate holdingDate;
	private LocalDateTime beginDateTime;
	private LocalDateTime finishDateTime;
	private LocalDateTime regDateTime;
	private LocalDateTime modDateTime;
	private LocalDateTime expDateTime;

	private Character delYN;
	
	@Builder(builderClassName = "init", builderMethodName = "initVO")
	public MeetingScheduleVO(MeetingScheduleDTO dto){
		if(dto == null) {
			return;
		}
		this.scheduleId = dto.getScheduleId();
		this.writerId = dto.getWriterId();
		this.writer = Optional.ofNullable(dto.getWriter()).map(UserInfoModelDTO::convert).orElseGet(()->null);
		this.meetingId = dto.getMeetingId();
		this.officeCode = dto.getOfficeCode();
		this.deptId = dto.getDeptId();
		this.externalReservationId = dto.getExternalReservationId();
		this.roomType = dto.getRoomType();
		this.roomId = dto.getRoomId();
		this.room = Optional.ofNullable(dto.getRoom()).map(RoomInfoModelDTO::convert).orElseGet(()->null);
		this.attendeeCnt = dto.getAttendeeCnt();
		this.userDefinedLocation = dto.getUserDefinedLocation();
		this.scheduleHost = dto.getScheduleHost();
		this.scheduleType = dto.getScheduleType();
		this.approvalStatus = dto.getApprovalStatus();
		this.approvalComment = dto.getApprovalComment();
		this.holdingDate = dto.getHoldingDate();
		this.beginDateTime = dto.getBeginDateTime();
		this.finishDateTime = dto.getFinishDateTime();
		this.regDateTime = dto.getRegDateTime();
		this.modDateTime = dto.getModDateTime();
		this.expDateTime = dto.getExpDateTime();
		this.delYN = dto.getDelYN();
	}
	
	@Override
	public MeetingScheduleDTO convert() {
		return MeetingScheduleDTO.initDTO().vo(this).build();
	}
}
