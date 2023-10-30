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
public class EwpSmsVO implements Convertable<EwpSmsDTO> {
	
	private LocalDateTime trSendDate;       //발송시간 
	private String trSendStat = "0";        //발송상태값
	private String trMsgType = "0";         //메시지타입
	private String trPhone;            		//발송번호
	private String trCallback;              //콜백번호
	private String trMsg;          			//sms msg
	
	@Builder(builderClassName = "init", builderMethodName = "initVO")
	public EwpSmsVO(EwpSmsDTO dto) {
		this.trSendDate = dto.getTrSendDate();
		this.trSendStat = dto.getTrSendStat();
		this.trMsgType = dto.getTrMsgType();
		this.trPhone = dto.getTrPhone();
		this.trCallback = dto.getTrCallback();
		this.trMsg = dto.getTrMsg();
	}
	
	@Override
	public String toString() {
		return "ScTranVO [trSendDate=" + trSendDate + ", trSendStat=" + trSendStat + ", trMsgType=" + trMsgType
				+ ", trPhone=" + trPhone + ", trCallback=" + trCallback + ", trMsg=" + trMsg + "]";
	}	

	@Override
	public EwpSmsDTO convert() {
		return EwpSmsDTO.initDTO().vo(this).build();
	}
	
	public String getTrMsg() {
		return StringEscapeUtils.unescapeHtml4(this.trMsg);
	}
}
