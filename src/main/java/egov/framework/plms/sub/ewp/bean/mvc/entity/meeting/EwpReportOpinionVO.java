package egov.framework.plms.sub.ewp.bean.mvc.entity.meeting;

import egov.framework.plms.main.core.model.able.Convertable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class EwpReportOpinionVO implements Convertable<EwpReportOpinionDTO> {
	private Integer opnId;
	private Integer meetingKey;
	private String writerKey;
	private String writerName;
	private String writerTel;
	private String comment;
	private String regDateTime;
	private String delYN;
	
	@Builder(builderClassName = "init", builderMethodName = "initVO")
	public EwpReportOpinionVO(EwpReportOpinionDTO dto) {
		this.opnId = dto.getOpnId();
		this.meetingKey = dto.getMeetingKey();
		this.writerKey = dto.getWriterKey();
		this.writerName = dto.getWriterName();
		this.writerTel = dto.getWriterTel();
		this.comment = dto.getComment();
		this.regDateTime = dto.getRegDateTime();
	}
	
	@Override
	public EwpReportOpinionDTO convert() {
		return EwpReportOpinionDTO.initDTO().vo(this).build();
	}
	
}
