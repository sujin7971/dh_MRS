package egov.framework.plms.sub.ewp.bean.mvc.entity.meeting;

import java.time.LocalDateTime;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingInfoModelDTO;
import egov.framework.plms.main.bean.mvc.entity.organization.abst.UserInfoModelDTO;
import egov.framework.plms.main.bean.mvc.entity.organization.abst.UserInfoModelVO;
import egov.framework.plms.main.core.model.enums.meeting.MeetingStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class EwpMeetingInfoDTO extends MeetingInfoModelDTO {
	private Integer meetingKey;
	private String writerKey;
	private UserInfoModelDTO writer;
	private String title;
	private String contents;
	private MeetingStatus meetingStatus;
	private Integer stickyBit;
	private boolean isReady;
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
	
	private Integer skdKey;
	private String roomType;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS") 
	@JsonDeserialize(using = LocalDateTimeDeserializer.class) 
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime regDateTime;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS") 
	@JsonDeserialize(using = LocalDateTimeDeserializer.class) 
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime modDateTime;
	
	@Builder(builderClassName = "init", builderMethodName = "initDTO")
	public EwpMeetingInfoDTO(EwpMeetingInfoVO vo){
		if(vo == null) {
			return;
		}
		this.meetingKey = vo.getMeetingKey();
		this.writerKey = vo.getWriterKey();
		this.writer = Optional.ofNullable(vo.getWriter()).map(UserInfoModelVO::convert).orElseGet(()->null);
		this.title = vo.getTitle();
		this.contents = vo.getContents();
		this.meetingStatus = vo.getMeetingStatus();
		//this.stickyBit = vo.getStickyBit();
		this.isReady = Optional.ofNullable(vo.getStickyBit()).map(bit -> bit > 0).orElseGet(() -> false);
		this.secretYN = vo.getSecretYN();
		this.secuLevel = vo.getSecuLevel();
		this.hostSecuLvl = vo.getHostSecuLvl();
		this.attendeeSecuLvl = vo.getAttendeeSecuLvl();
		this.observerSecuLvl = vo.getObserverSecuLvl();
		this.elecYN = vo.getElecYN();
		this.messengerYN = vo.getMessengerYN();
		this.mailYN = vo.getMailYN();
		this.smsYN = vo.getSmsYN();
		this.delYN = vo.getDelYN();
		
		this.regDateTime = vo.getRegDateTime();
		this.modDateTime = vo.getModDateTime();
	}
	
	@Override
	public EwpMeetingInfoVO convert() {
		return EwpMeetingInfoVO.initVO().dto(this).build();
	}

	@Override
	public void setMeetingId(Integer value) {
		// TODO Auto-generated method stub
		this.meetingKey = value;
	}

	@Override
	public void setWriterId(String value) {
		// TODO Auto-generated method stub
		this.writerKey = value;
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
