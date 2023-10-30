package egov.framework.plms.sub.ewp.bean.mvc.entity.meeting;

import java.time.LocalDateTime;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import egov.framework.plms.main.core.model.able.Convertable;
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
public class EwpMeetingSecurityAgreementDTO implements Convertable<EwpMeetingSecurityAgreementVO> {
	private Integer attendId;
	private Integer meetingId;
	private String signSrc;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime agreeDateTime;
	
	@Builder(builderClassName = "init", builderMethodName = "initDTO")
	public EwpMeetingSecurityAgreementDTO(EwpMeetingSecurityAgreementVO vo) {
		this.attendId = vo.getAttendId();
		this.meetingId = vo.getMeetingId();
		this.signSrc = vo.getSignSrc();
		this.agreeDateTime = vo.getAgreeDateTime();
	}

	@Override
	public EwpMeetingSecurityAgreementVO convert() {
		// TODO Auto-generated method stub
		return EwpMeetingSecurityAgreementVO.initVO().dto(this).build();
	}
}
