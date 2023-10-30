package egov.framework.plms.sub.ewp.bean.mvc.entity.meeting;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingInfoModelVO;
import egov.framework.plms.main.bean.mvc.entity.organization.abst.UserInfoModelDTO;
import egov.framework.plms.main.bean.mvc.entity.organization.abst.UserInfoModelVO;
import egov.framework.plms.main.core.model.enums.meeting.MeetingStatus;
import egov.framework.plms.main.core.model.response.ErrorMessage;
import egov.framework.plms.main.core.model.response.ResponseMessage;
import egov.framework.plms.main.core.util.CommUtil;
import egov.framework.plms.sub.ewp.bean.mvc.entity.assign.EwpRoomReqVO;
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
public class EwpMeetingInfoVO extends MeetingInfoModelVO {
	private Integer meetingKey;
	private String writerKey;
	private UserInfoModelVO writer;
	private String title;
	private String contents;
	private MeetingStatus meetingStatus;
	private Integer stickyBit;
	private Character secretYN;
	private Integer secuLevel;
	private Integer hostSecuLvl;
	private Integer attendeeSecuLvl;
	private Integer observerSecuLvl;
	private Character elecYN;
	private Character messengerYN;
	private Character mailYN;
	private Character smsYN;
	private Character delYN;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS") 
	@JsonDeserialize(using = LocalDateTimeDeserializer.class) 
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime regDateTime;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS") 
	@JsonDeserialize(using = LocalDateTimeDeserializer.class) 
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime modDateTime;
	
	@Builder(builderClassName = "init", builderMethodName = "initVO")
	public EwpMeetingInfoVO(EwpMeetingInfoDTO dto){
		if(dto == null) {
			return;
		}
		this.meetingKey = dto.getMeetingKey();
		this.writerKey = dto.getWriterKey();
		this.writer = Optional.ofNullable(dto.getWriter()).map(UserInfoModelDTO::convert).orElseGet(()->null);
		this.title = dto.getTitle();
		this.contents = dto.getContents();
		this.meetingStatus = dto.getMeetingStatus();
		this.stickyBit = dto.getStickyBit();
		this.secretYN = dto.getSecretYN();
		this.secuLevel = dto.getSecuLevel();
		this.hostSecuLvl = dto.getHostSecuLvl();
		this.attendeeSecuLvl = dto.getAttendeeSecuLvl();
		this.observerSecuLvl = dto.getObserverSecuLvl();
		this.elecYN = dto.getElecYN();
		this.mailYN = dto.getMailYN();
		this.messengerYN = dto.getMessengerYN();
		this.smsYN = dto.getSmsYN();
		this.delYN = dto.getDelYN();
		
		this.regDateTime = dto.getRegDateTime();
		this.modDateTime = dto.getModDateTime();
	}
	
	@Override
	public EwpMeetingInfoDTO convert() {
		return EwpMeetingInfoDTO.initDTO().vo(this).build();
	}
	
	public boolean isRental() {
		return Optional.ofNullable(elecYN).map(yn -> (yn == 'Y')?false:true).orElse(false);
	}
	
	/**
	 * 회의 객체 입력값 검증
	 * @param meeting
	 * @return
	 */
	public static ResponseMessage validation(EwpMeetingInfoDTO meeting) {
		List<ErrorMessage> error = new ArrayList<>();
		if(CommUtil.isEmpty(meeting.getTitle())) {
			error.add(ErrorMessage.builder(
					ErrorMessage.ErrorCode.EMPTY_VALUE)
					.message(ErrorMessage.MessageCode.MEETING.EMPTY_TITLE.value())
					.build());
		}else if(!CommUtil.isValidLength(meeting.getTitle(), 50)) {
			error.add(ErrorMessage.builder(
					ErrorMessage.ErrorCode.INVALID_VALUE)
					.message(ErrorMessage.MessageCode.MEETING.INVALID_TITLE.value())
					.build());
		}
		/*
		if(!CommUtil.isEmpty(meeting.getContents()) && !CommUtil.isValidLength(meeting.getContents(), 100)) {
			error.add(ErrorMessage.builder(
					ErrorMessage.ErrorCode.INVALID_VALUE)
					.message(ErrorMessage.MessageCode.MEETING.INVALID_CONTENTS.value())
					.build());
		}
		*/
		/*
		if(CommUtil.isEmpty(meeting.getOpt_secretYN())) {
			return ResponseMessage.builder(
					ResponseMessage.StatusCode.BAD_REQUEST)
					.detail(ResponseMessage.DetailCode.MEETING.EMPTY_ADMINPASS)
					.build();
		}
		*/
		if(error.size() == 0) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.OK).build();
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
					.error(error)
					.build();
		}
	}
	@Override
	public Integer getMeetingId() {
		// TODO Auto-generated method stub
		return this.meetingKey;
	}
	@Override
	public String getWriterId() {
		// TODO Auto-generated method stub
		return this.writerKey;
	}
}
