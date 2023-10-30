package egov.framework.plms.sub.ewp.bean.mvc.entity.alarm;

import java.time.LocalDateTime;

import org.apache.commons.text.StringEscapeUtils;

import egov.framework.plms.main.core.model.able.Convertable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class EwpSmsDTO implements Convertable<EwpSmsVO> {
	
	private LocalDateTime trSendDate;       //발송시간 
	private String trSendStat;              //발송상태값
	private String trMsgType;           	//메시지타입
	private String trPhone;            		//발송번호
	private String trCallback;              //콜백번호
	private String trMsg;          			//sms msg

	@Builder(builderClassName = "init", builderMethodName = "initDTO")
	public EwpSmsDTO(EwpSmsVO vo) {
		this.trSendDate = vo.getTrSendDate();
		this.trSendStat = vo.getTrSendStat();
		this.trMsgType = vo.getTrMsgType();
		this.trPhone = vo.getTrPhone();
		this.trCallback = vo.getTrCallback();
		this.trMsg = vo.getTrMsg();
	}

	@Override
	public String toString() {
		return "ScTranDTO [trSendDate=" + trSendDate + ", trSendStat=" + trSendStat + ", trMsgType=" + trMsgType
				+ ", trPhone=" + trPhone + ", trCallback=" + trCallback + ", trMsg=" + trMsg + "]";
	}	
	
	@Override
	public EwpSmsVO convert() {
		return EwpSmsVO.initVO().dto(this).build();
	}
	
	public String getTrMsg() {
		return StringEscapeUtils.unescapeHtml4(this.trMsg);
	}
}
