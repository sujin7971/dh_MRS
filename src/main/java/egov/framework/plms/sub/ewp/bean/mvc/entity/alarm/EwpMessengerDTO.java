package egov.framework.plms.sub.ewp.bean.mvc.entity.alarm;

import org.apache.commons.text.StringEscapeUtils;

import egov.framework.plms.main.core.model.able.Convertable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
@Slf4j
public class EwpMessengerDTO implements Convertable<EwpMessengerVO>{

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

	@Builder(builderClassName = "init", builderMethodName = "initDTO")
	public EwpMessengerDTO(EwpMessengerVO vo) {
		this.msgKey = vo.getMsgKey();
		this.msgGubun = vo.getMsgGubun();
		this.actionCode = vo.getActionCode();
		this.systemName = vo.getSystemName();
		this.enSystemName = vo.getEnSystemName();
		this.sendId = vo.getSendId();
		this.sendName = vo.getSendName();
		this.enSendName = vo.getEnSendName();
		this.recvIds = vo.getRecvIds();
		this.empno = vo.getEmpno();
		this.subject = vo.getSubject();
		this.enSubject = vo.getEnSubject();
		this.contents = vo.getContents();
		this.enContents = vo.getEnContents();
		this.url = vo.getUrl();
		this.urlEncode = vo.getUrlEncode();
		this.option = vo.getOption();
		this.sendYn = vo.getSendYn();
		this.sendDate = vo.getSendDate();
	}
	
	@Override
	public EwpMessengerVO convert() {
		// TODO Auto-generated method stub
		return EwpMessengerVO.initVO().dto(this).build();
	}
	
	public String getSubject() {
		return StringEscapeUtils.unescapeHtml4(this.subject);
	}
	
	public String getContents() {
		return StringEscapeUtils.unescapeHtml4(this.contents);
	}
}
