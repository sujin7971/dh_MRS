package egov.framework.plms.sub.ewp.bean.mvc.entity.alarm;

import java.time.LocalDateTime;
import java.util.Date;

import egov.framework.plms.sub.ewp.bean.mvc.entity.alarm.abst.AlarmModelDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class EwpAlarmDTO extends AlarmModelDTO {
	private Date trSendDate;					//발송시간
	private String trSendStat = "0";			//발송상태값
	private String trMsgType = "0";				//메시지타입
	private String trPhone;						//발송번호
	private String trCallBack;					//콜백번호
	private String trMsg;						//msg

	private String userKey; // 사번
	private String alarmDiv; // 알람 구분(E:e-mail, S:sms, A:알림톡, P:push)
	private LocalDateTime alarmNo; // 알람발송번호
	private Integer meetingkey; // 회의 고유키
	private String alarmEmail; // 이메일주소
	private String alarmTel; // 수신 전화번호
	private String alarmSubject; // 알람 제목
	private String alarmBody; // 알람 BODY
	private String templateCode; // 알림톡 템플릿 코드
	private LocalDateTime alarmDate; // 알림발송일자
	private String alarmRlt; // 발송처리결과
	private String cancelYn; // 발송 취소여부
	private LocalDateTime regDateTime; // 등록일시
	private LocalDateTime modDateTime; // 수정일시
	
	@Builder(builderClassName = "init", builderMethodName = "initDTO")
	public EwpAlarmDTO(EwpAlarmVO vo) {
		if(vo == null) {
			return;
		}
		this.trSendDate = vo.getTrSendDate();
		this.trSendStat = vo.getTrSendStat();
		this.trMsgType = vo.getTrMsgType();
		this.trPhone = vo.getTrPhone();
		this.trCallBack = vo.getTrCallBack();
		this.trMsg = vo.getTrMsg();
		
		this.userKey = vo.getUserKey();
		this.alarmDiv = vo.getAlarmDiv();
		this.alarmNo = vo.getAlarmNo();
		this.meetingkey = vo.getMeetingkey();
		this.alarmEmail = vo.getAlarmEmail();
		this.alarmTel = vo.getAlarmTel();
		this.alarmSubject = vo.getAlarmSubject();
		this.alarmBody = vo.getAlarmBody();
		this.templateCode = vo.getTemplateCode();
		this.alarmDate = vo.getAlarmDate();
		this.alarmRlt = vo.getAlarmRlt();
		this.cancelYn = vo.getCancelYn();
		this.regDateTime = vo.getRegDateTime();
		this.modDateTime = vo.getModDateTime();
	}
	
	
	@Override
	public EwpAlarmVO convert() {
		return EwpAlarmVO.initVO().dto(this).build();
	}

}
