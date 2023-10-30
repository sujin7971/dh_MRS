package egov.framework.plms.sub.ewp.bean.mvc.entity.meeting;

import java.time.LocalDateTime;
import java.util.Optional;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import egov.framework.plms.main.core.model.able.Convertable;
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
public class EwpMeetingSecurityAgreementVO implements Convertable<EwpMeetingSecurityAgreementDTO> {
	private Integer attendId;
	private Integer meetingId;
	private String signSrc;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime agreeDateTime;
	
	@Builder(builderClassName = "init", builderMethodName = "initVO")
	public EwpMeetingSecurityAgreementVO(EwpMeetingSecurityAgreementDTO dto) {
		this.attendId = dto.getAttendId();
		this.meetingId = dto.getMeetingId();
		this.signSrc = dto.getSignSrc();
		this.agreeDateTime = dto.getAgreeDateTime();
	}

	@Override
	public EwpMeetingSecurityAgreementDTO convert() {
		// TODO Auto-generated method stub
		return EwpMeetingSecurityAgreementDTO.initDTO().vo(this).build();
	}
}
