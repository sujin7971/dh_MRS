package egov.framework.plms.sub.ewp.bean.mvc.entity.meeting;

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
public class EwpReportOpinionDTO implements Convertable<EwpReportOpinionVO> {
	private Integer opnId;
	private Integer meetingKey;
	private String writerKey;
	private String writerName;
	private String writerTel;
	private String comment;
	private String regDateTime;
	private String delYN;
	
	@Builder(builderClassName = "init", builderMethodName = "initDTO")
	public EwpReportOpinionDTO(EwpReportOpinionVO vo) {
		this.opnId = vo.getOpnId();
		this.meetingKey = vo.getMeetingKey();
		this.writerKey = vo.getWriterKey();
		this.writerName = vo.getWriterName();
		this.writerTel = vo.getWriterTel();
		this.comment = vo.getComment();
		this.regDateTime = vo.getRegDateTime();
	}
	
	@Override
	public EwpReportOpinionVO convert() {
		return EwpReportOpinionVO.initVO().dto(this).build();
	}
	
}
