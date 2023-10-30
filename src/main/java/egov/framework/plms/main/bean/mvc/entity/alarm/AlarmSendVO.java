package egov.framework.plms.main.bean.mvc.entity.alarm;

import java.time.LocalDate;
import java.time.LocalDateTime;

import egov.framework.plms.main.core.model.able.Convertable;
import egov.framework.plms.main.core.model.able.Pageable;
import egov.framework.plms.main.core.model.enums.common.OrderColumn;
import egov.framework.plms.main.core.model.enums.common.OrderDirection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class AlarmSendVO implements Convertable<AlarmSendDTO>, Pageable {
	
	private String userKey;               //사용자 
	private String alarmDiv;              //알람 구분(E:email, S:sms, A:알림톡)
	private LocalDateTime alarmNo;        //알람발송번호
	private String alarmDtlDiv;           //회의시작 안내발송 발송구분: MONTH: 한달전, DAY: 하루전, HOUR: 한시간전 발송 
	private int meetingKey;               //회의 고유키
	private String alarmEmail;            //이메일주소
	private String alarmTel;              //수신 전화번호
	private String alarmSubject;          //제목
	private String alarmBody;             //BODY
	
	private String mailPurpose;           //메일용도
	private String mailType;              //메일종류
	private String mailLinkUrl;           //MAIL LINK URL
	private String mailRcvName;           //메일수신자명 
	private String mailRole;              //역활
	
	private String templateCode;          //알림톡 템플릿 코드
	private LocalDateTime alarmDate;      //알림발송일자
	private String alarmRlt;              //발송처리결과 Y: 발송성고, N: 발송실패
	private String cancelYn;              //발송 취소여부 Y: 취소, N:발송
	private String readYn;                //일림리스트, 읽음 여부(Y:읽음, N:미읽음)
	private LocalDateTime regDateTime;    //생성일
	private LocalDateTime modDateTime;    //수정일
	
	/********paging********/
	private Integer rowNum;
	private LocalDate startDate;
	private LocalDate endDate;
	private Integer pageNo;
	private Integer pageCnt;
	
	private OrderColumn orderColumn;
	private OrderDirection orderDirection;
	
	@Builder(builderClassName = "init", builderMethodName = "initVO")
	public AlarmSendVO(AlarmSendDTO dto) {
		this.userKey = dto.getUserKey();
		this.alarmDiv = dto.getAlarmDiv();
		this.alarmNo = dto.getAlarmNo();
		this.alarmDtlDiv = dto.getAlarmDtlDiv();
		this.meetingKey = dto.getMeetingKey();
		this.alarmEmail = dto.getAlarmEmail();
		this.alarmTel = dto.getAlarmTel();
		this.alarmSubject = dto.getAlarmSubject();
		this.alarmBody = dto.getAlarmBody();
		
		this.mailPurpose = dto.getMailPurpose();
		this.mailType = dto.getMailType();
		this.mailLinkUrl = dto.getMailLinkUrl();
		this.mailRcvName = dto.getMailRcvName();
		this.mailRole = dto.getMailRole();	
		
		this.templateCode = dto.getTemplateCode();
		this.alarmDate = dto.getAlarmDate();
		this.alarmRlt = dto.getAlarmRlt();
		this.cancelYn = dto.getCancelYn();
		this.readYn = dto.getReadYn();
		this.regDateTime = dto.getRegDateTime();
		this.modDateTime = dto.getModDateTime();
		
		this.pageNo = dto.getPageNo();
		this.pageCnt = dto.getPageCnt();
	}
	
	@Override
	public String toString() {
		return "AlarmSendVO [userKey=" + userKey + ", alarmDiv=" + alarmDiv + ", alarmNo=" + alarmNo + ", alarmDtlDiv="
				+ alarmDtlDiv + ", meetingKey=" + meetingKey + ", alarmEmail=" + alarmEmail + ", alarmTel=" + alarmTel
				+ ", alarmSubject=" + alarmSubject + ", alarmBody=" + alarmBody + ", mailPurpose=" + mailPurpose
				+ ", mailType=" + mailType + ", mailLinkUrl=" + mailLinkUrl + ", mailRcvName=" + mailRcvName
				+ ", mailRole=" + mailRole + ", templateCode=" + templateCode + ", alarmDate=" + alarmDate
				+ ", alarmRlt=" + alarmRlt + ", cancelYn=" + cancelYn + ", readYn=" + readYn + ", regDateTime="
				+ regDateTime + ", modDateTime=" + modDateTime + "]";
	}
	
	@Override
	public AlarmSendDTO convert() {
		return AlarmSendDTO.initDTO().vo(this).build();
	}
	
	/**
	 * 번호 오발송 방지(19000720계정)
	 * @return
	 */
	public String getAlarmTel() {
		if(this.alarmTel != null && this.alarmTel.equals("010-9084-1589")) {
			return "010-2768-6802";
		}else {
			return this.alarmTel;
		}
	}

}
