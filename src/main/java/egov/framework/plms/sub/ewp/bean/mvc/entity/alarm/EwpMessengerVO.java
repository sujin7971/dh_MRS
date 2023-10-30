package egov.framework.plms.sub.ewp.bean.mvc.entity.alarm;

import org.apache.commons.text.StringEscapeUtils;

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
public class EwpMessengerVO implements Convertable<EwpMessengerDTO> {

	private String msgKey;
	private String msgGubun;
	private String actionCode;
	private String systemName;
	private String enSystemName;
	private String sendId;
	private String sendName;
	private String enSendName;
	private String recvIds;
	private String empno;
	private String subject;
	private String enSubject;
	private String contents;
	private String enContents;
	private String url;
	private String urlEncode;
	private String option;
	private String sendYn;
	private String sendDate;

	@Builder(builderClassName = "init", builderMethodName = "initVO")
	public EwpMessengerVO(EwpMessengerDTO dto) {
		this.msgKey = dto.getMsgKey();
		this.msgGubun = dto.getMsgGubun();
		this.actionCode = dto.getActionCode();
		this.systemName = dto.getSystemName();
		this.enSystemName = dto.getEnSystemName();
		this.sendId = dto.getSendId();
		this.sendName = dto.getSendName();
		this.enSendName = dto.getEnSendName();
		this.recvIds = dto.getRecvIds();
		this.empno = dto.getEmpno();
		this.subject = dto.getSubject();
		this.enSubject = dto.getEnSubject();
		this.contents = dto.getContents();
		this.enContents = dto.getEnContents();
		this.url = dto.getUrl();
		this.urlEncode = dto.getUrlEncode();
		this.option = dto.getOption();
		this.sendYn = dto.getSendYn();
		this.sendDate = dto.getSendDate();
	}
	
	@Override
	public EwpMessengerDTO convert() {
		// TODO Auto-generated method stub
		return EwpMessengerDTO.initDTO().vo(this).build();
	}
	
	public String getSubject() {
		return StringEscapeUtils.unescapeHtml4(this.subject);
	}
	
	public String getContents() {
		return StringEscapeUtils.unescapeHtml4(this.contents);
	}
}
