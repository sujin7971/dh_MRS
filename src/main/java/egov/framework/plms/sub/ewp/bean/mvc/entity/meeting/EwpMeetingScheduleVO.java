package egov.framework.plms.sub.ewp.bean.mvc.entity.meeting;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingScheduleModelVO;
import egov.framework.plms.main.core.model.able.Pageable;
import egov.framework.plms.main.core.model.enums.common.OrderColumn;
import egov.framework.plms.main.core.model.enums.common.OrderDirection;
import egov.framework.plms.main.core.model.enums.meeting.ApprovalStatus;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import egov.framework.plms.main.core.model.enums.meeting.ScheduleType;
import egov.framework.plms.main.core.model.response.ErrorMessage;
import egov.framework.plms.main.core.model.response.ResponseMessage;
import egov.framework.plms.sub.ewp.bean.mvc.entity.assign.EwpRoomReqVO;
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

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class EwpMeetingScheduleVO extends MeetingScheduleModelVO implements Pageable {
	private Integer skdKey;
	private String writerKey;
	private EwpUserInfoVO writer;
	private Integer meetingKey;
	private String officeCode;
	private String officeName;
	private String deptId;
	/** 사용신청 키 {@link EwpRoomReqVO#seqReq} */
	private Integer reqKey;
	private RoomType roomType;
	private Integer roomKey;
	private EwpRoomInfoVO room;
	private Integer attendeeCnt;
	private String skdHost;
	private ScheduleType skdType;
	private ApprovalStatus skdStatus;
	private String skdComment;
	private LocalDate holdingDate;
	private LocalDateTime beginDateTime;
	private LocalDateTime finishDateTime;
	private LocalDateTime regDateTime;
	private LocalDateTime modDateTime;
	private LocalDateTime expDateTime;

	private Character delYN;
	
	/********paging********/
	private Integer rowNum;
	private LocalDate startDate;
	private LocalDate endDate;
	private Integer pageNo;
	private Integer pageCnt;
	private OrderColumn orderColumn;
	private OrderDirection orderDirection;
	
	@Builder(builderClassName = "init", builderMethodName = "initVO")
	public EwpMeetingScheduleVO(EwpMeetingScheduleDTO dto){
		if(dto == null) {
			return;
		}
		this.skdKey = dto.getSkdKey();
		this.writerKey = dto.getWriterKey();
		this.writer = Optional.ofNullable(dto.getWriter()).map(EwpUserInfoDTO::convert).orElseGet(()->null);
		this.meetingKey = dto.getMeetingKey();
		this.officeCode = dto.getOfficeCode();
		this.officeName = dto.getOfficeName();
		this.deptId = dto.getDeptId();
		this.reqKey = dto.getReqKey();
		this.roomType = dto.getRoomType();
		this.roomKey = dto.getRoomKey();
		this.room = Optional.ofNullable(dto.getRoom()).map(EwpRoomInfoDTO::convert).orElseGet(()->null);
		this.attendeeCnt = dto.getAttendeeCnt();
		this.skdHost = dto.getSkdHost();
		this.skdType = dto.getSkdType();
		this.skdStatus = dto.getSkdStatus();
		this.skdComment = dto.getSkdComment();
		this.holdingDate = dto.getHoldingDate();
		this.beginDateTime = dto.getBeginDateTime();
		this.finishDateTime = dto.getFinishDateTime();
		this.regDateTime = dto.getRegDateTime();
		this.modDateTime = dto.getModDateTime();
		this.expDateTime = dto.getExpDateTime();
		this.delYN = dto.getDelYN();
		
		this.rowNum = dto.getRowNum();
		this.startDate = dto.getStartDate();
		this.endDate = dto.getEndDate();
		this.pageNo = dto.getPageNo();
		this.pageCnt = dto.getPageCnt();
	}
	
	@Override
	public EwpMeetingScheduleDTO convert() {
		return EwpMeetingScheduleDTO.initDTO().vo(this).build();
	}
	
	public static ResponseMessage validation(EwpMeetingScheduleDTO schedule) {
		ErrorMessage error = null;
		try {
			LocalDateTime beginDateTime = schedule.getBeginDateTime();
			LocalDateTime finishDateTime = schedule.getFinishDateTime();
			LocalDateTime nowDateTime = LocalDateTime.now();
			if(beginDateTime == null) {
				error = (ErrorMessage.builder(
						ErrorMessage.ErrorCode.EMPTY_VALUE)
						.message(ErrorMessage.MessageCode.SCHEDULE.EMPTY_BEGIN.value())
						.build());
			}else if(finishDateTime == null) {
				error = (ErrorMessage.builder(
						ErrorMessage.ErrorCode.EMPTY_VALUE)
						.message(ErrorMessage.MessageCode.SCHEDULE.EMPTY_FINISH.value())
						.build());
			}else if(beginDateTime.equals(finishDateTime)) {
				error = (ErrorMessage.builder(
						ErrorMessage.ErrorCode.EMPTY_VALUE)
						.message(ErrorMessage.MessageCode.SCHEDULE.INVALID_TIME.value())
						.build());
			}
			if(beginDateTime.isAfter(finishDateTime)) {
				error = (ErrorMessage.builder(
						ErrorMessage.ErrorCode.EMPTY_VALUE)
						.message(ErrorMessage.MessageCode.SCHEDULE.INVALID_FINISH.value())
						.build());
			}
			if(beginDateTime.isBefore(nowDateTime) || finishDateTime.isBefore(nowDateTime)) {
				error = (ErrorMessage.builder(
						ErrorMessage.ErrorCode.EMPTY_VALUE)
						.message(ErrorMessage.MessageCode.SCHEDULE.INVALID_BEGIN.value())
						.build());
			}
		}catch(DateTimeParseException e){
			error = (ErrorMessage.builder(
							ErrorMessage.ErrorCode.EMPTY_VALUE)
							.message(ErrorMessage.MessageCode.SCHEDULE.INVALID_FORMAT.value())
							.build());
		}catch(ClassCastException e) {
			error = (ErrorMessage.builder(
							ErrorMessage.ErrorCode.EMPTY_VALUE)
							.message(ErrorMessage.MessageCode.SCHEDULE.INVALID_FORMAT.value())
							.build());
		}
		if(error == null) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
					.build();
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
					.error(error)
					.build();
		}
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
}
