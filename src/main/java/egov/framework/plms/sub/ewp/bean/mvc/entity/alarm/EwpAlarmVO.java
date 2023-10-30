package egov.framework.plms.sub.ewp.bean.mvc.entity.alarm;

import java.time.LocalDateTime;
import java.util.Date;

import egov.framework.plms.sub.ewp.bean.mvc.entity.alarm.abst.AlarmModelVO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class EwpAlarmVO extends AlarmModelVO {
	
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
	
	@Builder(builderClassName = "init", builderMethodName = "initVO")
	public EwpAlarmVO(EwpAlarmDTO dto) {
		if(dto == null) {
			return;
		}
		this.trSendDate = dto.getTrSendDate();
		this.trSendStat = dto.getTrSendStat();
		this.trMsgType = dto.getTrMsgType();
		this.trPhone = dto.getTrPhone();
		this.trCallBack = dto.getTrCallBack();
		this.trMsg = dto.getTrMsg();
		
		this.userKey = dto.getUserKey();
		this.alarmDiv = dto.getAlarmDiv();
		this.alarmNo = dto.getAlarmNo();
		this.meetingkey = dto.getMeetingkey();
		this.alarmEmail = dto.getAlarmEmail();
		this.alarmTel = dto.getAlarmTel();
		this.alarmSubject = dto.getAlarmSubject();
		this.alarmBody = dto.getAlarmBody();
		this.templateCode = dto.getTemplateCode();
		this.alarmDate = dto.getAlarmDate();
		this.alarmRlt = dto.getAlarmRlt();
		this.cancelYn = dto.getCancelYn();
		this.regDateTime = dto.getRegDateTime();
		this.modDateTime = dto.getModDateTime();
	}
	
	@Override
	public EwpAlarmDTO convert() {
		return EwpAlarmDTO.initDTO().vo(this).build();
	}
}
