package egov.framework.plms.sub.ewp.bean.mvc.entity.meeting;

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
import egov.framework.plms.main.bean.mvc.entity.room.abst.RoomInfoModelDTO;
import egov.framework.plms.main.core.model.able.Pageable;
import egov.framework.plms.main.core.model.enums.common.OrderColumn;
import egov.framework.plms.main.core.model.enums.common.OrderDirection;
import egov.framework.plms.main.core.model.enums.meeting.ApprovalStatus;
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

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EwpMeetingScheduleDTO extends MeetingScheduleModelDTO implements Pageable {
	private Integer skdKey;
	private String writerKey;
	private EwpUserInfoDTO writer;
	private Integer meetingKey;
	private String officeCode;
	private String officeName;
	private String deptId;
	private Integer reqKey;
	private RoomType roomType;
	private Integer roomKey;
	private EwpRoomInfoDTO room;
	private Integer attendeeCnt;
	private String customPlace;
	private String skdHost;
	private ScheduleType skdType;
	private ApprovalStatus skdStatus;
	private String skdComment;
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
	
	/********paging********/
	private Integer rowNum;
	private LocalDate startDate;
	private LocalDate endDate;
	private Integer pageNo;
	private Integer pageCnt;
	private OrderColumn orderColumn;
	private OrderDirection orderDirection;
	
	@Builder(builderClassName = "init", builderMethodName = "initDTO")
	public EwpMeetingScheduleDTO(EwpMeetingScheduleVO vo){
		if(vo == null) {
			return;
		}
		this.skdKey = vo.getSkdKey();
		this.writerKey = vo.getWriterKey();
		this.writer = Optional.ofNullable(vo.getWriter()).map(EwpUserInfoVO::convert).orElseGet(()->null);
		this.meetingKey = vo.getMeetingKey();
		this.officeCode = vo.getOfficeCode();
		this.officeName = vo.getOfficeName();
		this.deptId = vo.getDeptId();
		this.reqKey = vo.getReqKey();
		this.roomType = vo.getRoomType();
		this.roomKey = vo.getRoomKey();
		this.room = Optional.ofNullable(vo.getRoom()).map(EwpRoomInfoVO::convert).orElseGet(()->null);
		this.attendeeCnt = vo.getAttendeeCnt();
		this.skdHost = vo.getSkdHost();
		this.skdType = vo.getSkdType();
		this.skdStatus = vo.getSkdStatus();
		this.skdComment = vo.getSkdComment();
		this.holdingDate = vo.getHoldingDate();
		this.beginDateTime = vo.getBeginDateTime();
		this.finishDateTime = vo.getFinishDateTime();
		this.regDateTime = vo.getRegDateTime();
		this.modDateTime = vo.getModDateTime();
		this.expDateTime = vo.getExpDateTime();
		this.delYN = vo.getDelYN();
		
		this.rowNum = vo.getRowNum();
		this.startDate = vo.getStartDate();
		this.endDate = vo.getEndDate();
		this.pageNo = vo.getPageNo();
		this.pageCnt = vo.getPageCnt();
	}
	
	@Override
	public EwpMeetingScheduleVO convert() {
		return EwpMeetingScheduleVO.initVO().dto(this).build();
	}
	
	public String stringify() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
	}

	public String getPlaceName() {
		return Optional.ofNullable(this.room).map(RoomInfoModelDTO::getRoomName).orElse(customPlace);
	}

	@Override
	public Integer getRoomId() {
		return this.roomKey;
	}

	@Override
	public void setScheduleId(Integer skdId) {
		// TODO Auto-generated method stub
		this.skdKey = skdId;
	}

	@Override
	public void setRoomId(Integer roomId) {
		// TODO Auto-generated method stub
		this.roomKey = roomId;
	}

	@Override
	public ScheduleType getScheduleType() {
		// TODO Auto-generated method stub
		return this.skdType;
	}

	@Override
	public ApprovalStatus getApprovalStatus() {
		// TODO Auto-generated method stub
		return this.skdStatus;
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
			
		}
	}

	@Override
	public void setWriterId(String value) {
		// TODO Auto-generated method stub
		this.writerKey = value;
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
		this.skdStatus = value;
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
	public String getUserDefinedLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getScheduleHost() {
		// TODO Auto-generated method stub
		return this.skdHost;
	}
	
}
