package egov.framework.plms.main.bean.mvc.entity.meeting;

import java.time.LocalDateTime;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MeetingInfoDTO extends MeetingInfoModelDTO {
	private Integer meetingId;
	private String writerId;
	private UserInfoModelDTO writer;
	private String title;
	private String contents;
	private MeetingStatus meetingStatus;
	private Integer stickyBit;
	private boolean isReady;
	private Character secretYN;
	private Integer hostSecuLvl;
	private Integer attendeeSecuLvl;
	private Integer observerSecuLvl;
	private Character elecYN;
	private Character messengerYN;
	private Character mailYN;
	private Character smsYN;
	private Character delYN;
	
	private Integer skdId;
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
	public MeetingInfoDTO(MeetingInfoVO vo){
		if(vo == null) {
			return;
		}
		this.meetingId = vo.getMeetingId();
		this.writerId = vo.getWriterId();
		this.writer = Optional.ofNullable(vo.getWriter()).map(UserInfoModelVO::convert).orElseGet(()->null);
		this.title = vo.getTitle();
		this.contents = vo.getContents();
		this.meetingStatus = vo.getMeetingStatus();
		this.isReady = Optional.ofNullable(vo.getStickyBit()).map(bit -> bit > 0).orElseGet(() -> false);
		this.secretYN = vo.getSecretYN();
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
	public MeetingInfoVO convert() {
		return MeetingInfoVO.initVO().dto(this).build();
	}
}
