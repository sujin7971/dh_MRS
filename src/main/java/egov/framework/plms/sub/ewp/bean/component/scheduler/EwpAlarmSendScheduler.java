package egov.framework.plms.sub.ewp.bean.component.scheduler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import egov.framework.plms.main.bean.mvc.entity.alarm.AlarmSendVO;
import egov.framework.plms.sub.ewp.bean.component.properties.AlarmProperties;
import egov.framework.plms.sub.ewp.bean.mvc.entity.alarm.EwpMessengerDTO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.alarm.EwpSmsVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingScheduleVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.organization.EwpUserInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.service.alarm.EwpAlarmSendService;
import egov.framework.plms.sub.ewp.bean.mvc.service.alarm.EwpAlarmWriteService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingScheduleService;
import egov.framework.plms.sub.ewp.bean.mvc.service.organization.EwpUserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @FileName : AlarmSendProcessScheduler.java
 * @Project : EWP PLMS SYSTEM
 * @Date : 2022. 11. 21. 
 * @작성자 : hb
 * @변경이력 :
 * @프로그램 설명 : 10초 간격 alarm_send 테이블을 조회하여 email 및 sms 전송처리, 회의시작 정보 SMS 및 EMAIL 전송 처리 스케쥴
 */

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "config.scheduledTasks.alarmSendScheduler", name="enabled", havingValue = "true", matchIfMissing = false)
@Profile("ewp")
public class EwpAlarmSendScheduler {
	
	private static final String ALARM_DIV_MAIL_E = "E";
	private static final String ALARM_DIV_SMS_S = "S";
	private static final String ALARM_DIV_MESSENGER_M = "M";
	private static final String ALARM_DIV_BOARD_B = "B";
	
	private final AlarmProperties alarmProperties;
	
	private final EwpAlarmSendService alarmSendService;
	private final EwpAlarmWriteService alarmServ;
	private final EwpUserInfoService ewpEmpServ;
	private final EwpMeetingScheduleService ewpScheduleServ;

	
	
	/**
	 * alarm_send 테이블을 조회하여 email 및 sms 전송처리
	 */
	@Scheduled(cron="${config.scheduledTasks.alarmSendScheduler.cron}")
	private void scheduler() {
		try {
			try {
				//E-MAIL 전송건 조회 후 전송 처리
				List<AlarmSendVO> alarmSendList = alarmSendService.getAlarmSendListForPending(AlarmSendVO.builder().alarmDiv(ALARM_DIV_MAIL_E).build());
				if(alarmSendList != null) {
					for(AlarmSendVO alarmSendVO : alarmSendList){
						try {
							log.info("E-MAIL, alarmSendVO: " + alarmSendVO.toString());

							if(alarmProperties.getMail().isEnabled()) {
								// 수신자 정보 조회
								EwpUserInfoVO writerVO = ewpEmpServ.selectUserInfoOne(alarmSendVO.getUserKey()).orElse(null);
								if(writerVO != null) {
									
									String subject = "";
									
									switch (alarmSendVO.getMailPurpose()) {
									case "meeting": // 회의 초대, 역할 변경, 초대 취소, 회의 취소
									case "status": // 회의실 사용 신청 상태 변경
									case "note": // 회의록 검토 요청
										subject = alarmServ.sendMeetingMailOne(
												alarmSendVO.getMailPurpose()
												, alarmSendVO.getMailType()
												, alarmSendVO.getMeetingKey()
												, alarmSendVO.getMailLinkUrl()
												, alarmSendVO.getAlarmEmail()
												, alarmSendVO.getUserKey()
												, writerVO.getUserName()
												, alarmSendVO.getMailRole()
												);
										break;
									case "reset": // 비밀번호 초기화
									case "temp": // 임시비밀번호 발급
										subject = alarmServ.sendResetPassMailOne(
												alarmSendVO.getMailPurpose()
												, alarmSendVO.getAlarmEmail()
												, writerVO.getUserName()
												, alarmSendVO.getAlarmBody()
												, alarmSendVO.getMailLinkUrl()
												);
										break;
									}
									alarmSendVO.setAlarmSubject(subject);
									alarmSendVO.setAlarmRlt("S"); // 메일 발송 처리 성공
								}else {
									alarmSendVO.setAlarmRlt("N"); // sms insert 안함
								}
							}else {
								alarmSendVO.setAlarmRlt("N"); // sms insert 안함
							}
						} catch (Exception e) {
							log.error(e.toString(), e);
							alarmSendVO.setAlarmRlt("F"); // 메일 발송 처리 실패
						} finally {
							alarmSendService.updateAlarmSend(alarmSendVO);
						}
					}
				}
			} catch(Exception e) {
				log.error(e.toString(), e);
			}

			//SMS 전송건 조회 후 전송 처리
			try {
				//SMS 전송건 조회 후 전송 처리
				List<AlarmSendVO> alarmSendList = alarmSendService.getAlarmSendListForPending(AlarmSendVO.builder().alarmDiv(ALARM_DIV_SMS_S).build());
				if(alarmSendList != null) {
					for(AlarmSendVO alarmSendVO : alarmSendList){
						try {
							log.info("SMS, alarmSendVO: " + alarmSendVO.toString());
							if(alarmProperties.getSms().isEnabled()) {
								String smsBody = "";
	
								// 수신자 정보 조회
								EwpUserInfoVO writerVO = ewpEmpServ.selectUserInfoOne(alarmSendVO.getUserKey()).orElse(null);
								if(writerVO != null) {
									switch (alarmSendVO.getMailPurpose()) {
										case "meeting": // 회의 초대, 역할 변경, 초대 취소, 회의 취소
										case "status": // 회의실 사용 신청 상태 변경
										case "note": // 회의록 검토 요청
											smsBody = alarmServ.sendMeetingSmsOne(
														alarmSendVO.getMailPurpose()
														, alarmSendVO.getMailType()
														, alarmSendVO.getUserKey()
														, alarmSendVO.getMeetingKey()
														, alarmSendVO.getMailLinkUrl()
														, writerVO.getUserName()
														, alarmSendVO.getMailRole()
													);
											break;
										case "reset": // 비밀번호 초기화
										case "temp": // 임시비밀번호 발급
											smsBody = alarmServ.sendResetPassSmsOne(
														alarmSendVO.getMailPurpose()
														, alarmSendVO.getAlarmEmail()
														, writerVO.getUserName()
														, alarmSendVO.getAlarmBody()
														, alarmSendVO.getMailLinkUrl()
													);
											break;
									}
									alarmSendVO.setAlarmBody(smsBody);
									String receiver = (alarmProperties.getSms().getDefaultReceiver() != null)?alarmProperties.getSms().getDefaultReceiver():alarmSendVO.getAlarmTel();
									EwpSmsVO scTranVO = EwpSmsVO.builder().trSendStat("0").trMsgType("0").trPhone(receiver).trCallback(alarmProperties.getSms().getCallback()).trMsg(alarmSendVO.getAlarmBody()).build();
									log.info("설정된 SMS 알람: {}", alarmSendVO.toString());
									log.info("전송할 SMS 양식: {}", scTranVO.toString());
									int result = alarmSendService.doSndSms(scTranVO);
									if(result != 1) {
										String message = String.format("문자 발송 실패 - 알람 번호: %s, 알람 대상 사번: %d, 알람 대상 연락처: %s, 알람 대상 회의: %d", alarmSendVO.getAlarmNo(), alarmSendVO.getUserKey(), alarmSendVO.getAlarmTel(), alarmSendVO.getMeetingKey());
										throw new Exception(message);
									}
									alarmSendVO.setAlarmRlt("S"); // sms insert 성공
								}else {
									alarmSendVO.setAlarmRlt("N"); // sms insert 안함
								}
							}else {
								alarmSendVO.setAlarmRlt("N"); // sms insert 안함
							}
						}catch (Exception e) {
							log.error(e.toString(), e);
							alarmSendVO.setAlarmRlt("F"); // sms insert 실패
						} finally {
							alarmSendService.updateAlarmSend(alarmSendVO);
						}
					}
				}
			} catch(Exception e) {
				log.error(e.toString(), e);
			}
			
			// 메신저 전송건 조회 후 전송 처리
			try {
				// 메신저 전송건 조회 후 전송 처리
				List<AlarmSendVO> alarmSendList = alarmSendService.getAlarmSendListForPending(AlarmSendVO.builder().alarmDiv(ALARM_DIV_MESSENGER_M).build());
				if(alarmSendList != null) {
					for(AlarmSendVO alarmSendVO : alarmSendList){
						try {
							log.info("MESSENGER, alarmSendVO: " + alarmSendVO.toString());
							if(alarmProperties.getMessenger().isEnabled()) {
								// 수신자 정보 조회
								EwpUserInfoVO writerVO = ewpEmpServ.selectUserInfoOne(alarmSendVO.getUserKey()).orElse(null);
								if(writerVO != null) {
									Map<String, String> messengerMap = new HashMap<String, String>();
									switch (alarmSendVO.getMailPurpose()) {
										case "meeting": // 회의 초대, 역할 변경, 초대 취소, 회의 취소
										case "status": // 회의실 사용 신청 상태 변경
										case "note": // 회의록 검토 요청
											messengerMap = alarmServ.sendMeetingMessengerOne(
															alarmSendVO.getMailPurpose()
															, alarmSendVO.getMailType()
															, alarmSendVO.getUserKey()
															, alarmSendVO.getMeetingKey()
															, alarmSendVO.getMailLinkUrl()
															, writerVO.getUserName()
															, alarmSendVO.getMailRole()
														);
											alarmSendVO.setAlarmSubject(messengerMap.get("subject"));
											alarmSendVO.setAlarmBody(messengerMap.get("alarmBody"));
											break;
									}
									
									String strToday = "";
									Date today = new Date(); 
									SimpleDateFormat format;
									format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									strToday = format.format(today);
									
									// 회의 등록자의 정보를 들고온다
									UUID uuid = UUID.randomUUID();
									String uuidStr = uuid.toString().substring(0,31);
									String receiver = (alarmProperties.getMessenger().getDefaultReceiver() != null)?alarmProperties.getMessenger().getDefaultReceiver():writerVO.getUserId();
									EwpMessengerDTO messengerAlarmSendDTO = EwpMessengerDTO.builder()
											.msgKey(uuidStr)
											.msgGubun("ALERT")
											.actionCode("ALERT")
											.systemName("[회의관리시스템]")
											.enSystemName("UTF-9")
											.sendId(writerVO.getUserId())
											.sendName(writerVO.getUserName())
											.enSendName("UTF-9")
											.recvIds(receiver)
											.empno(writerVO.getUserId())
											.subject(alarmSendVO.getAlarmSubject())
											.enSubject("UTF-9")
											.contents(alarmSendVO.getAlarmBody())
											.enContents("UTF-9")
											.url(alarmSendVO.getMailLinkUrl())
											.urlEncode("UTF-9")
											.sendYn("")
											.sendDate(strToday)
											.build();
									log.info("설정된 메신저 알람: {}", alarmSendVO.toString());
									log.info("전송할 메신저 양식: {}", messengerAlarmSendDTO.toString());
									int result = alarmSendService.doSndMessenger(messengerAlarmSendDTO);
									if(result != 1) {
										String message = String.format("메신저 발송 실패 - 알람 번호: %s, 알람 대상 사번: %d, 알람 대상 회의: %d", alarmSendVO.getAlarmNo(), alarmSendVO.getUserKey(), alarmSendVO.getMeetingKey());
										throw new Exception(message);
									}
									alarmSendVO.setAlarmRlt("S"); // messenger insert 성공
								}else {
									alarmSendVO.setAlarmRlt("N"); // messenger insert 안함
								}
							}else {
								alarmSendVO.setAlarmRlt("N"); // messenger insert 안함
							}
						} catch (Exception e) {
							log.error(e.toString(), e);
							alarmSendVO.setAlarmRlt("F"); // messenger insert 실패
						} finally {
							alarmSendService.updateAlarmSend(alarmSendVO);
						}
					}
				}
			} catch(Exception e) {
				log.error(e.toString(), e);
			}
			
			try {
				// 알림 추가 건 조회 후 가공 처리
				List<AlarmSendVO> alarmSendList = alarmSendService.getAlarmSendListForPending(AlarmSendVO.builder().alarmDiv(ALARM_DIV_BOARD_B).build());
				if(alarmSendList != null) {
					for(AlarmSendVO alarmSendVO : alarmSendList){
						try {
							log.info("ALARM, alarmSendVO: " + alarmSendVO.toString());
							// 수신자 정보 조회
							EwpUserInfoVO writerVO = ewpEmpServ.selectUserInfoOne(alarmSendVO.getUserKey()).orElse(null);
							if(writerVO != null) {
								String subject = "";
								switch (alarmSendVO.getMailPurpose()) {
									case "meeting": // 회의 초대, 역할 변경, 초대 취소, 회의 취소
									case "status": // 회의실 사용 신청 상태 변경
									case "note": // 회의록 검토 요청
										subject = alarmServ.makeMeetingAlarmOne(
													alarmSendVO.getMailPurpose()
													, alarmSendVO.getMailType()
													, alarmSendVO.getMeetingKey()
													, alarmSendVO.getMailLinkUrl()
													, alarmSendVO.getAlarmEmail()
													, alarmSendVO.getUserKey()
													, writerVO.getUserName()
													, alarmSendVO.getMailRole()
												);
										break;
								}
								alarmSendVO.setAlarmBody(subject);
								alarmSendVO.setAlarmRlt("S"); // 알림 발송 처리 성공
							}else {
								alarmSendVO.setAlarmRlt("N");
							}
							
						} catch (Exception e) {
							log.error(e.toString(), e);
							alarmSendVO.setAlarmRlt("F"); // 알림 발송 처리 실패
						} finally {
							alarmSendService.updateAlarmSend(alarmSendVO);
						}
					}
				}
			} catch(Exception e) {
				log.error(e.toString(), e);
			}
			
		} catch(Exception e) {
			log.error(e.toString(), e);
		}

	}
}


