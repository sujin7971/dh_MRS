package egov.framework.plms.main.bean.mvc.entity.meeting;

import java.time.LocalDateTime;
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
public class MeetingInfoVO extends MeetingInfoModelVO{
	private Integer meetingId;
	private String writerId;
	private UserInfoModelVO writer;
	private String title;
	private String contents;
	private MeetingStatus meetingStatus;
	private Integer stickyBit;
	private Character secretYN;
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
	public MeetingInfoVO(MeetingInfoDTO dto){
		if(dto == null) {
			return;
		}
		this.meetingId = dto.getMeetingId();
		this.writerId = dto.getWriterId();
		this.writer = Optional.ofNullable(dto.getWriter()).map(UserInfoModelDTO::convert).orElseGet(()->null);
		this.title = dto.getTitle();
		this.contents = dto.getContents();
		this.meetingStatus = dto.getMeetingStatus();
		this.stickyBit = dto.getStickyBit();
		this.secretYN = dto.getSecretYN();
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
	public MeetingInfoDTO convert() {
		return MeetingInfoDTO.initDTO().vo(this).build();
	}
}
