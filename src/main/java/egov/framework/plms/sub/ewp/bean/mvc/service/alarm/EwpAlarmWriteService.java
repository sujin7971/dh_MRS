package egov.framework.plms.sub.ewp.bean.mvc.service.alarm;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.mvc.entity.alarm.AlarmSendVO;
import egov.framework.plms.main.core.model.auth.ResourceAuthorityCollection;
import egov.framework.plms.main.core.model.enums.auth.ReportAuth;
import egov.framework.plms.main.core.model.enums.meeting.ApprovalStatus;
import egov.framework.plms.main.core.model.enums.meeting.AttendRole;
import egov.framework.plms.main.core.model.response.ResponseMessage;
import egov.framework.plms.sub.ewp.bean.component.alarm.EwpBoardAlarmManager;
import egov.framework.plms.sub.ewp.bean.component.alarm.EwpMailManager;
import egov.framework.plms.sub.ewp.bean.component.alarm.EwpMessengerManager;
import egov.framework.plms.sub.ewp.bean.component.alarm.EwpSmsManager;
import egov.framework.plms.sub.ewp.bean.component.auth.EwpResourceAuthorityProvider;
import egov.framework.plms.sub.ewp.bean.component.properties.AlarmProperties;
import egov.framework.plms.sub.ewp.bean.component.properties.EwpServerProperties;
import egov.framework.plms.sub.ewp.bean.component.server.EwpServerListener;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingApprovalVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingAssignVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingAttendeeVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingAssignVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.organization.EwpUserInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.room.EwpRoomInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.service.admin.EwpAdminRosterService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingAssignService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingAttendeeService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingInfoService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingScheduleService;
import egov.framework.plms.sub.ewp.bean.mvc.service.organization.EwpUserInfoService;
import egov.framework.plms.sub.ewp.bean.mvc.service.room.EwpMariaRoomInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("EwpAlarmService")
@RequiredArgsConstructor
@Profile("ewp")
public class EwpAlarmWriteService {
	private final EwpServerListener serverListener;
	private final EwpMailManager mailMng;
	private final EwpSmsManager smsMng;
	private final EwpMessengerManager messengerMng;
	private final EwpBoardAlarmManager boardAlarmMng;
	
	private final EwpMeetingAssignService assignServ;
	private final EwpMeetingScheduleService scheServ;
	private final EwpUserInfoService userServ;
	private final EwpMeetingAttendeeService attServ;
	private final EwpMariaRoomInfoService rmServ;
	private final EwpMeetingInfoService mtServ;
	private final EwpAlarmSendService alarmServ;
	private final EwpAdminRosterService admRosterServ;
	
	private final AlarmProperties alarmProperties;
	private final EwpServerProperties serverProperties;
	
	private final EwpResourceAuthorityProvider authorityProvider;

	// (이미 컨트롤러에서 메일 수신 여부 확인)
	public ResponseMessage sendAttendeeSMS(List<EwpMeetingAttendeeVO> attendeeList, String type, int meetingKey) {
		if(alarmProperties.getSms().isEnabled()) {
			if(attendeeList != null && attendeeList.size() > 0) {
				EwpMeetingAssignVO assignVO = assignServ.getMeetingAssignOneByMeetingId(meetingKey).orElse(null);
				String url = alarmProperties.getHost() + "/ewp/meeting/assign/" + assignVO.getSkdKey();
				for(EwpMeetingAttendeeVO attendee : attendeeList) {
					EwpUserInfoVO emp = userServ.selectUserInfoOne(attendee.getUserKey()).orElse(null);
					try {
						AlarmSendVO emailAlarmSendVO;
						emailAlarmSendVO = AlarmSendVO.builder()
								.userKey(attendee.getUserKey())
								.alarmDiv("S")
								.alarmDtlDiv(null)
								.meetingKey(attendee.getMeetingKey())
								.alarmTel(emp.getPersonalCellPhone())
								.mailPurpose("meeting")
								.mailType(type)
								.mailLinkUrl(url)
								.mailRole(attendee.getAttendRole().toString())
								.build();
						
						if(alarmServ.doSndMsg(emailAlarmSendVO) <= 0) {
							return ResponseMessage.builder(ResponseMessage.StatusCode.UNPROCESSABLE_ENTITY)
									.message("문자 등록 실패")
									.build();
						}
					} catch (Exception e) {
						log.error(e.getMessage());
					}
				}
			}
		}
		return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
				.message("문자 등록 완료")
				.build();
	}
	
	@SuppressWarnings("finally")
	public String sendMeetingSmsOne(
			String purpose
			, String type
			, String userKey
			, int meetingKey
			, String url
			, String name
			, String role
		) {
		String alarmBody = "";
		try {
			EwpMeetingAssignVO assignVO = assignServ.getMeetingAssignOneByMeetingId(meetingKey).orElse(null); // 회의 스케쥴을 VO형태로 들고온다.
			EwpMeetingInfoVO infoVO = mtServ.getMeetingInfoOne(meetingKey).orElse(null);

			DateTimeFormatter yearMonth = DateTimeFormatter.ofPattern("yyyy.MM.dd");
			String nowDate = assignVO.getBeginDateTime().format(yearMonth);
			
			DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm");
			String startTime = assignVO.getBeginDateTime().format(time);
			String endTime = assignVO.getFinishDateTime().format(time);

			Map<String, String> smsMap = new HashMap<String, String>();

			smsMap.put("purpose", purpose);
			smsMap.put("type", type);
			smsMap.put("userKey", userKey);
			smsMap.put("url", url);
			smsMap.put("name", name);
			smsMap.put("role", role);
			smsMap.put("title", infoVO.getTitle());
			smsMap.put("roomName", assignVO.getRoom().getRoomName());
			smsMap.put("nowDate", nowDate);
			smsMap.put("startTime", startTime);
			smsMap.put("endTime", endTime);
			
			log.info("SMS 양식 생성 - 전송목적: {}, 유형: {}", purpose, type);
			switch (purpose) {
				case "meeting": // 회의 주최, 역할 변경, 회의 취소 , 회의 개최 예정 알람
					EwpMeetingAttendeeVO myAttendee = attServ.getMeetingAttendeeOne(userKey, meetingKey).orElse(null);
					log.info("SMS 양식 생성 - 전송대상 참석자: {}", myAttendee);
					if(myAttendee != null) {
						if(myAttendee.getTempPW() != null) {
							smsMap.put("tempPW", myAttendee.getTempPW());
						} else {
							smsMap.put("tempPW", "");
						}

						if(myAttendee.getExpireDate() != null) {
							String expireDate = myAttendee.getExpireDate().format(yearMonth);
							smsMap.put("expireDate", expireDate);
						} else {
							smsMap.put("expireDate", "");
						}
					}
					alarmBody = smsMng.createMeetingSmsBody(smsMap);
					break;
				case "status": // 회의실 사용 신청 상태 변경 알람
					alarmBody = smsMng.createStatusSmsBody(smsMap);
					break;
				case "note": // 회의록 검토 요청 알람
					alarmBody = smsMng.createReportExamineSmsBody(smsMap);
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} finally {
			return alarmBody;
		}
	}
	
	/**
	 * 임시비밀번호 메일 발송
	 * @param addr : 수신자 메일
	 * @param type : 메일 타입
	 * @param newPw : 임시비밀번호
	 * @param url : 주소
	 * @return
	 */
	@SuppressWarnings("finally")
	public String sendResetPassSmsOne(
			String purpose
			, String addr
			, String name
			, String newPw
			, String url
	) {
		Map<String, String> mailMap = new HashMap<String, String>();

		mailMap.put("purpose", purpose);
		mailMap.put("addr", addr);
		mailMap.put("name", name);
		mailMap.put("newPw", newPw);
		mailMap.put("url", url);
		String alarmBody = "";
		try {
			alarmBody = smsMng.sendResetPassSmsOne(mailMap); // 메일 매니저로 메일을 보내준다.
		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			return alarmBody;
		}
	}
	
	/**
	 * 회의 참석 메일 등록 (이미 컨트롤러에서 메일 수신 여부 확인)
	 * @param attendeeList : 수신자 리스트
	 * @param type : 메일 타입
	 * @param meetingKey : 회의 키
	 * @return
	 */
	public ResponseMessage sendAttendeeMail(List<EwpMeetingAttendeeVO> attendeeList, String type, int meetingKey) {
		if(alarmProperties.getMail().isEnabled()) {
			if(attendeeList != null && attendeeList.size() > 0) {
				EwpMeetingAssignVO meetingVO = assignServ.getMeetingAssignOneByMeetingId(meetingKey).orElse(null);
				String url = alarmProperties.getHost() + "/ewp/meeting/assign/" + meetingVO.getSkdKey();
				for(EwpMeetingAttendeeVO attendee : attendeeList) {
					EwpUserInfoVO emp = userServ.selectUserInfoOne(attendee.getUserKey()).orElse(null);
					AlarmSendVO emailAlarmSendVO;
					try {
						emailAlarmSendVO = AlarmSendVO.builder()
								.userKey(attendee.getUserKey())
								.alarmDiv("E")
								.alarmDtlDiv(null)
								.meetingKey(attendee.getMeetingKey())
								.alarmEmail(emp.getEmail())
								.alarmSubject("메일제목")
								.alarmBody("메일내용")
								.mailPurpose("meeting")
								.mailType(type)
								.mailLinkUrl(url)
								.mailRole(attendee.getAttendRole().toString())
								.build();
	
						if(alarmServ.doSndMsg(emailAlarmSendVO) <= 0) {
							return ResponseMessage.builder(ResponseMessage.StatusCode.UNPROCESSABLE_ENTITY)
									.message("메일 등록 실패")
									.build();
						}
					} catch (Exception e) {
						log.error(e.getMessage());
					}
				}
			}
		}
		return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
				.message("메일 등록 완료")
				.build();
	}
	
	public void postMeetingUpdateNoticeAlarm(Integer meetingKey, EwpUserInfoVO writerVO, EwpMeetingAssignVO oldAssignVO, EwpMeetingAssignVO newAssignVO) {
		String url = alarmProperties.getHost() + "/ewp/schedule/" + newAssignVO.getSkdKey();
		if(alarmProperties.getMessenger().isEnabled()) {
			if(newAssignVO.getMessengerYN().equals('Y') && newAssignVO.getElecYN().equals('Y')) {
				log.info("메신저 전송 대상 스케줄: {}", newAssignVO.toString());
				String subject = messengerMng.createMeetingUpdateNoticeMessengerAlarmSubject(oldAssignVO);
				String body = messengerMng.createMeetingUpdateNoticeMessengerAlarmBody(url, writerVO, oldAssignVO, newAssignVO);
				List<EwpMeetingAttendeeVO> voList = attServ.getMeetingAttendeeListByMeeting(meetingKey);
				if(voList != null && voList.size() > 0) {
					for(EwpMeetingAttendeeVO attendee : voList) {
						Optional<EwpUserInfoVO> userOpt = userServ.selectUserInfoOne(attendee.getUserKey());
						if(!userOpt.isPresent()) {
							continue;
						}
						AlarmSendVO smsAlarmVO = AlarmSendVO.builder()
								.userKey(userOpt.get().getUserKey())
								.alarmDiv("M")
								.alarmDtlDiv(null)
								.meetingKey(meetingKey)
								.mailPurpose("notice")
								.mailType("meetingUpdate")
								.alarmSubject(subject)
								.alarmBody(body)
								.mailLinkUrl(url)
								.mailRole(attendee.getAttendRole().toString())
								.build();
						alarmServ.doSndMsg(smsAlarmVO);
					}
				}
			}
		}
	}
	
	/**
	 * 회의실 상태 변경 메일 등록 (신청자는 무조건 알람 발송, 알람 설정 여부에 따라 참석자 알람 발송)
	 * @param vo : 회의실 상태 관련 VO
	 * @return
	 */
	public ResponseMessage changeStatus(EwpMeetingApprovalVO vo) {
		// 회의 신청자에게 상태변경 메일 발송
		EwpMeetingAssignVO assignVO = assignServ.getMeetingAssignOneByScheduleId(vo.getSkdKey()).orElse(null);
		if(assignVO != null) {
			try {
				Integer meetingKey = assignVO.getMeetingKey();
				String url = alarmProperties.getHost() + "/ewp/meeting/assign/" + assignVO.getSkdKey();
				String approvalUrl = alarmProperties.getHost() + "/ewp/room/" + assignVO.getRoomType().toString() + "/assign/" + assignVO.getReqKey() + "/approval";
				
				EwpUserInfoVO writer = userServ.selectUserInfoOne(assignVO.getWriterId()).orElse(null);
				
				// 사용 승인 신청일 경우 해당 사업소 담당자에게 신규 신청 알림
				/*
				if(meeting.getSkdStatus() == ApprovalStatus.REQUEST) {
					
					// 사용신청 승인 담당자 리스트
					List<ItemManagerVO> managerList = adminServ.getItemManagerList(meeting.getOfficeCode());
					for(ItemManagerVO manager : managerList) { // 사용신청 승인 담당자 알림 발송
						EwpUserInfoVO officeManager = ewpEmpServ.selectUserInfoOne(manager.getUserId());
						if(alarmProperties.getMail().isEnabled()) {
							AlarmSendVO emailAlarmSendVO = AlarmSendVO.builder()
									.userKey(officeManager.getUserKey())
									.alarmDiv("E")
									.alarmDtlDiv(null)
									.meetingKey(meetingKey)
									.alarmEmail(officeManager.getEmail())
									.alarmSubject("메일제목")
									.alarmBody("메일내용")
									.mailPurpose("status")
									.mailType("NEW_REQUEST")
									.mailLinkUrl(approvalUrl)
									.mailRole(null)
									.build();
							alarmServ.doSndMsg(emailAlarmSendVO);
						}
						
						if(alarmProperties.getSms().isEnabled()) {
							AlarmSendVO smsAlarmSendVO = AlarmSendVO.builder()
									.userKey(officeManager.getUserKey())
									.alarmDiv("S")
									.alarmDtlDiv(null)
									.meetingKey(meetingKey)
									.alarmTel(officeManager.getPersonalCellPhone())
									.mailPurpose("status")
									.mailType("NEW_REQUEST")
									.mailLinkUrl(approvalUrl)
									.mailRole(null)
									.build();
							alarmServ.doSndMsg(smsAlarmSendVO);
						}
						
						if(alarmProperties.getMessenger().isEnabled()) {
							AlarmSendVO messengerAlarmSendVO = AlarmSendVO.builder()
									.userKey(officeManager.getUserKey())
									.alarmDiv("M")
									.alarmDtlDiv(null)
									.meetingKey(meetingKey)
									.mailPurpose("status")
									.mailType("NEW_REQUEST")
									.mailLinkUrl(approvalUrl)
									.mailRole(null)
									.build();
							alarmServ.doSndMsg(messengerAlarmSendVO);
						}
						
						AlarmSendVO messengerAlarmSendVO = AlarmSendVO.builder()
								.userKey(officeManager.getUserKey())
								.alarmDiv("B")
								.alarmDtlDiv(null)
								.meetingKey(meetingKey)
								.mailPurpose("status")
								.mailType("NEW_REQUEST")
								.mailLinkUrl(approvalUrl)
								.mailRole(null)
								.build();
						alarmServ.doSndMsg(messengerAlarmSendVO);
					}
					
				}
				*/
				// 메일 전송
				if(alarmProperties.getMail().isEnabled()) {
					if(assignVO.getMailYN().equals('Y')) { // 작성자 메일 발송
						AlarmSendVO emailAlarmSendVO = AlarmSendVO.builder()
								.userKey(assignVO.getWriterId())
								.alarmDiv("E")
								.alarmDtlDiv(null)
								.meetingKey(meetingKey)
								.alarmEmail(writer.getEmail())
								.alarmSubject("메일제목")
								.alarmBody("메일내용")
								.mailPurpose("status")
								.mailType(assignVO.getApprovalStatus().toString())
								.mailLinkUrl(url)
								.mailRole(null)
								.build();
						
						if(alarmServ.doSndMsg(emailAlarmSendVO) > 0) {
							if(assignVO.getElecYN().equals('Y')) {
								log.info("알람 전송 대상 스케줄: {}", assignVO.toString());
								if(assignVO.getApprovalStatus() == ApprovalStatus.APPROVED) {
									// 회의실 사용 승인 시 회의 참석·참관자 에게 메일 발송
									List<EwpMeetingAttendeeVO> voList = attServ.getMeetingAttendeeListByMeeting(meetingKey);
									if(voList != null && voList.size() > 0) {
										for(EwpMeetingAttendeeVO attendee : voList) {
											EwpUserInfoVO emp = userServ.selectUserInfoOne(attendee.getUserKey()).orElse(null);
											AlarmSendVO alarmVO = AlarmSendVO.builder()
													.userKey(emp.getUserKey())
													.alarmDiv("E")
													.alarmDtlDiv(null)
													.meetingKey(meetingKey)
													.alarmEmail(emp.getEmail())
													.alarmSubject("메일제목")
													.alarmBody("메일내용")
													.mailPurpose("meeting")
													.mailType("open")
													.mailLinkUrl(url)
													.mailRole(attendee.getAttendRole().toString())
													.build();
											if(alarmServ.doSndMsg(alarmVO) <= 0) {
												return ResponseMessage.builder(ResponseMessage.StatusCode.UNPROCESSABLE_ENTITY)
														.message("메일 등록 실패")
														.build();
											}
										}
									}
								} else if(assignVO.getApprovalStatus() == ApprovalStatus.CANCELED || assignVO.getApprovalStatus() == ApprovalStatus.REJECTED || assignVO.getApprovalStatus() == ApprovalStatus.DELETE) {
									// 회의실 사용 승인 취소 시 회의 참석·참관자 에게 메일 발송
									List<EwpMeetingAttendeeVO> voList = attServ.getMeetingAttendeeListByMeeting(meetingKey);
									if(voList != null && voList.size() > 0) {
										for(EwpMeetingAttendeeVO attendee : voList) {
											EwpUserInfoVO emp = userServ.selectUserInfoOne(attendee.getUserKey()).orElse(null);
											AlarmSendVO alarmVO = AlarmSendVO.builder()
													.userKey(emp.getUserKey())
													.alarmDiv("E")
													.alarmDtlDiv(null)
													.meetingKey(meetingKey)
													.alarmEmail(emp.getEmail())
													.alarmSubject("메일제목")
													.alarmBody("메일내용")
													.mailPurpose("meeting")
													.mailType("cancel")
													.mailLinkUrl(url)
													.mailRole(attendee.getAttendRole().toString())
													.build();
											if(alarmServ.doSndMsg(alarmVO) <= 0) {
												return ResponseMessage.builder(ResponseMessage.StatusCode.UNPROCESSABLE_ENTITY)
														.message("메일 등록 실패")
														.build();
											}
										}
									}
								}
							}
						} else {
							return ResponseMessage.builder(ResponseMessage.StatusCode.UNPROCESSABLE_ENTITY)
									.message("메일 등록 실패")
									.build();
						}
					}
				}

				// 문자 발송
				if(alarmProperties.getSms().isEnabled()) {
					if(assignVO.getSmsYN().equals('Y')) {
						AlarmSendVO smsAlarmSendVO = AlarmSendVO.builder()
								.userKey(assignVO.getWriterId())
								.alarmDiv("S")
								.alarmDtlDiv(null)
								.meetingKey(meetingKey)
								.alarmTel(writer.getPersonalCellPhone())
								.mailPurpose("status")
								.mailType(assignVO.getApprovalStatus().toString())
								.mailLinkUrl(url)
								.mailRole(null)
								.build();
						
						//if(alarmServ.doSndMsg(smsAlarmSendVO) > 0) {
							if(assignVO.getElecYN().equals('Y')) {
								if(assignVO.getApprovalStatus() == ApprovalStatus.APPROVED) {
									// 회의실 사용 승인 시 회의 참석·참관자 에게 메일 발송
									List<EwpMeetingAttendeeVO> voList = attServ.getMeetingAttendeeListByMeeting(meetingKey);
									if(voList != null && voList.size() > 0) {
										for(EwpMeetingAttendeeVO attendee : voList) {
											EwpUserInfoVO emp = userServ.selectUserInfoOne(attendee.getUserKey()).orElse(null);
											AlarmSendVO smsAlarmVO = AlarmSendVO.builder()
													.userKey(emp.getUserKey())
													.alarmDiv("S")
													.alarmDtlDiv(null)
													.meetingKey(meetingKey)
													.alarmTel(emp.getPersonalCellPhone())
													.mailPurpose("meeting")
													.mailType("open")
													.mailLinkUrl(url)
													.mailRole(attendee.getAttendRole().toString())
													.build();
											if(alarmServ.doSndMsg(smsAlarmVO) <= 0) {
												return ResponseMessage.builder(ResponseMessage.StatusCode.UNPROCESSABLE_ENTITY)
														.message("문자 등록 실패")
														.build();
											}
										}
									}
								} else if(assignVO.getApprovalStatus() == ApprovalStatus.CANCELED || assignVO.getApprovalStatus() == ApprovalStatus.REJECTED || assignVO.getApprovalStatus() == ApprovalStatus.DELETE) {
									// 회의실 사용 승인 시 회의 참석·참관자 에게 메일 발송
									List<EwpMeetingAttendeeVO> voList = attServ.getMeetingAttendeeListByMeeting(meetingKey);
									if(voList != null && voList.size() > 0) {
										for(EwpMeetingAttendeeVO attendee : voList) {
											EwpUserInfoVO emp = userServ.selectUserInfoOne(attendee.getUserKey()).orElse(null);
											AlarmSendVO alarmVO = AlarmSendVO.builder()
													.userKey(emp.getUserKey())
													.alarmDiv("S")
													.alarmDtlDiv(null)
													.meetingKey(meetingKey)
													.alarmTel(emp.getPersonalCellPhone())
													.mailPurpose("meeting")
													.mailType("cancel")
													.mailLinkUrl(url)
													.mailRole(attendee.getAttendRole().toString())
													.build();
											if(alarmServ.doSndMsg(alarmVO) <= 0) {
												return ResponseMessage.builder(ResponseMessage.StatusCode.UNPROCESSABLE_ENTITY)
														.message("문자 등록 실패")
														.build();
											}
										}
									}
								}
							}
						/*
						} else {
							return ResponseMessage.builder(ResponseMessage.StatusCode.UNPROCESSABLE_ENTITY)
									.message("문자 등록 실패")
									.build();
						}*/
					}
				}

				// 메신저 발송
				if(alarmProperties.getMessenger().isEnabled()) {
					if(assignVO.getMessengerYN().equals('Y')) {
						AlarmSendVO messengerAlarmSendVO = AlarmSendVO.builder()
								.userKey(assignVO.getWriterId())
								.alarmDiv("M")
								.alarmDtlDiv(null)
								.meetingKey(meetingKey)
								.mailPurpose("status")
								.mailType(assignVO.getApprovalStatus().toString())
								.mailLinkUrl(url)
								.mailRole(null)
								.build();
						
						//if(alarmServ.doSndMsg(messengerAlarmSendVO) > 0) {
							if(assignVO.getElecYN().equals('Y')) {
								if(assignVO.getApprovalStatus() == ApprovalStatus.APPROVED) {
									// 회의실 사용 승인 시 회의 참석·참관자 에게 메신저 발송
									List<EwpMeetingAttendeeVO> voList = attServ.getMeetingAttendeeListByMeeting(meetingKey);
									if(voList != null && voList.size() > 0) {
										for(EwpMeetingAttendeeVO attendee : voList) {
											EwpUserInfoVO emp = userServ.selectUserInfoOne(attendee.getUserKey()).orElse(null);
											AlarmSendVO smsAlarmVO = AlarmSendVO.builder()
													.userKey(emp.getUserKey())
													.alarmDiv("M")
													.alarmDtlDiv(null)
													.meetingKey(meetingKey)
													.mailPurpose("meeting")
													.mailType("open")
													.mailLinkUrl(url)
													.mailRole(attendee.getAttendRole().toString())
													.build();
											if(alarmServ.doSndMsg(smsAlarmVO) <= 0) {
												return ResponseMessage.builder(ResponseMessage.StatusCode.UNPROCESSABLE_ENTITY)
														.message("메신저 등록 실패")
														.build();
											}
										}
									}
								} else if(assignVO.getApprovalStatus() == ApprovalStatus.REJECTED || assignVO.getApprovalStatus() == ApprovalStatus.DELETE) {
									// 회의실 사용 승인 시 회의 참석·참관자 에게 메신저 발송
									List<EwpMeetingAttendeeVO> voList = attServ.getMeetingAttendeeListByMeeting(meetingKey);
									if(voList != null && voList.size() > 0) {
										for(EwpMeetingAttendeeVO attendee : voList) {
											EwpUserInfoVO emp = userServ.selectUserInfoOne(attendee.getUserKey()).orElse(null);
											AlarmSendVO alarmVO = AlarmSendVO.builder()
													.userKey(emp.getUserKey())
													.alarmDiv("M")
													.alarmDtlDiv(null)
													.meetingKey(meetingKey)
													.mailPurpose("meeting")
													.mailType("cancel")
													.mailLinkUrl(url)
													.mailRole(attendee.getAttendRole().toString())
													.build();
											if(alarmServ.doSndMsg(alarmVO) <= 0) {
												return ResponseMessage.builder(ResponseMessage.StatusCode.UNPROCESSABLE_ENTITY)
														.message("메신저 등록 실패")
														.build();
											}
										}
									}
								}
							}
						/*
						} else {
							return ResponseMessage.builder(ResponseMessage.StatusCode.UNPROCESSABLE_ENTITY)
									.message("메신저 등록 실패")
									.build();
						}*/
					}
				}
				
				// 알림 추가
				AlarmSendVO messengerAlarmSendVO = AlarmSendVO.builder()
						.userKey(assignVO.getWriterId())
						.alarmDiv("B")
						.alarmDtlDiv(null)
						.meetingKey(meetingKey)
						.mailPurpose("status")
						.mailType(assignVO.getApprovalStatus().toString())
						.mailLinkUrl(url)
						.mailRole(null)
						.build();
					
				if(alarmServ.doSndMsg(messengerAlarmSendVO) > 0) {
					if(assignVO.getElecYN().equals('Y')) {
						if(assignVO.getApprovalStatus() == ApprovalStatus.APPROVED) {
							// 회의실 사용 승인 시 회의 참석·참관자 에게 메신저 발송
							List<EwpMeetingAttendeeVO> voList = attServ.getMeetingAttendeeListByMeeting(meetingKey);
							if(voList != null && voList.size() > 0) {
								for(EwpMeetingAttendeeVO attendee : voList) {
									EwpUserInfoVO emp = userServ.selectUserInfoOne(attendee.getUserKey()).orElse(null);
									AlarmSendVO smsAlarmVO = AlarmSendVO.builder()
											.userKey(emp.getUserKey())
											.alarmDiv("B")
											.alarmDtlDiv(null)
											.meetingKey(meetingKey)
											.mailPurpose("meeting")
											.mailType("open")
											.mailLinkUrl(url)
											.mailRole(attendee.getAttendRole().toString())
											.build();
									if(alarmServ.doSndMsg(smsAlarmVO) <= 0) {
										return ResponseMessage.builder(ResponseMessage.StatusCode.UNPROCESSABLE_ENTITY)
												.message("알림 등록 실패")
												.build();
									}
								}
							}
						} else if(assignVO.getApprovalStatus() == ApprovalStatus.REJECTED || assignVO.getApprovalStatus() == ApprovalStatus.DELETE) {
							// 회의실 사용 승인 시 회의 참석·참관자 에게 메신저 발송
							List<EwpMeetingAttendeeVO> voList = attServ.getMeetingAttendeeListByMeeting(meetingKey);
							if(voList != null && voList.size() > 0) {
								for(EwpMeetingAttendeeVO attendee : voList) {
									EwpUserInfoVO emp = userServ.selectUserInfoOne(attendee.getUserKey()).orElse(null);
									AlarmSendVO alarmVO = AlarmSendVO.builder()
											.userKey(emp.getUserKey())
											.alarmDiv("B")
											.alarmDtlDiv(null)
											.meetingKey(meetingKey)
											.mailPurpose("meeting")
											.mailType("cancel")
											.mailLinkUrl(url)
											.mailRole(attendee.getAttendRole().toString())
											.build();
									if(alarmServ.doSndMsg(alarmVO) <= 0) {
										return ResponseMessage.builder(ResponseMessage.StatusCode.UNPROCESSABLE_ENTITY)
												.message("알림 등록 실패")
												.build();
									}
								}
							}
						}
					}
				} else {
					return ResponseMessage.builder(ResponseMessage.StatusCode.UNPROCESSABLE_ENTITY)
							.message("알림 등록 실패")
							.build();
				}

			} catch (Exception e) {
				log.error(e.getMessage());
			}
		return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
				.message("알람 등록 완료")
				.build();
		} else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.UNPROCESSABLE_ENTITY)
					.message("알람 등록 실패")
					.build();
		}
	}
	
	/**
	 * 회의록 관련 메일 발송
	 * @param meetingKey : 회의 키
	 * @return
	 */
	public ResponseMessage sendReportAlarm(int meetingKey) {
		EwpMeetingInfoVO infoVO = mtServ.getMeetingInfoOne(meetingKey).orElse(null);
		if(infoVO != null) {
			String url = alarmProperties.getHost() + "/ewp/meeting/" + meetingKey + "/report";
			List<EwpMeetingAttendeeVO> attList = attServ.getMeetingAttendeeListByMeeting(meetingKey);

			// 메일 발송
			if(alarmProperties.getMail().isEnabled()) {
				for(EwpMeetingAttendeeVO attendee : attList) {
					// 해당 회의 참가자가 회의 진행자 또는 보조진행자인 경우 건너뛴다.
					if(attendee.getAttendRole() == AttendRole.HOST || attendee.getAssistantYN().equals('Y')) continue;
					
					ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingReportAuthorityCollection(attendee.getUserKey(), meetingKey);
					if(authorityCollection.hasAuthority(ReportAuth.OPINION)) {
						EwpUserInfoVO emp = userServ.selectUserInfoOne(attendee.getUserKey()).orElse(null);
						AlarmSendVO emailAlarmSendVO;
						try {
							emailAlarmSendVO = AlarmSendVO.builder()
									.userKey(attendee.getUserKey())
									.alarmDiv("E")
									.alarmDtlDiv(null)
									.meetingKey(meetingKey)
									.alarmEmail(emp.getEmail())
									.alarmSubject("메일제목")
									.alarmBody("메일내용")
									.mailPurpose("note")
									.mailLinkUrl(url)
									.mailRole(null)
									.build();
							
							if(alarmServ.doSndMsg(emailAlarmSendVO) <= 0) {
								return ResponseMessage.builder(ResponseMessage.StatusCode.UNPROCESSABLE_ENTITY)
										.message("메일 등록 실패")
										.build();
							}
						} catch (Exception e) {
							log.error(e.getMessage());
						}
					}
				}
			}

			// 문자 발송
			if(alarmProperties.getSms().isEnabled()) {
				for(EwpMeetingAttendeeVO attendee : attList) {
					// 해당 회의 참가자가 회의 진행자 또는 보조진행자인 경우 건너뛴다.
					if(attendee.getAttendRole() == AttendRole.HOST || attendee.getAssistantYN().equals('Y')) continue;
				
					ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingReportAuthorityCollection(attendee.getUserKey(), meetingKey);
					if(authorityCollection.hasAuthority(ReportAuth.OPINION)) {
						EwpUserInfoVO emp = userServ.selectUserInfoOne(attendee.getUserKey()).orElse(null);
						AlarmSendVO smsAlarmSendVO;
						try {
							smsAlarmSendVO = AlarmSendVO.builder()
									.userKey(attendee.getUserKey())
									.alarmDiv("S")
									.alarmDtlDiv(null)
									.meetingKey(meetingKey)
									.alarmTel(emp.getPersonalCellPhone())
									.mailPurpose("note")
									.mailLinkUrl(url)
									.mailRole(null)
									.build();
							
							if(alarmServ.doSndMsg(smsAlarmSendVO) <= 0) {
								return ResponseMessage.builder(ResponseMessage.StatusCode.UNPROCESSABLE_ENTITY)
										.message("문자 등록 실패")
										.build();
							}
						} catch (Exception e) {
							log.error(e.getMessage());
						}
					}
				}
			}
			
			// 메신저 발송
			if(alarmProperties.getMessenger().isEnabled()) {
				for(EwpMeetingAttendeeVO attendee : attList) {
					// 해당 회의 참가자가 회의 진행자 또는 보조진행자인 경우 건너뛴다.
					if(attendee.getAttendRole() == AttendRole.HOST || attendee.getAssistantYN().equals('Y')) continue;
					
					ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingReportAuthorityCollection(attendee.getUserKey(), meetingKey);
					if(authorityCollection.hasAuthority(ReportAuth.OPINION)) {
						AlarmSendVO messengerAlarmSendVO;
						try {
								messengerAlarmSendVO = AlarmSendVO.builder()
										.userKey(attendee.getUserKey())
										.alarmDiv("M")
										.alarmDtlDiv(null)
										.meetingKey(meetingKey)
										.mailPurpose("note")
										.mailLinkUrl(url)
										.mailRole(null)
										.build();
								
								if(alarmServ.doSndMsg(messengerAlarmSendVO) <= 0) {
									return ResponseMessage.builder(ResponseMessage.StatusCode.UNPROCESSABLE_ENTITY)
											.message("메신저 등록 실패")
											.build();
								}
						} catch (Exception e) {
							log.error(e.getMessage());
						}
					}
				}
			}
			
			// 알림 발송
			for(EwpMeetingAttendeeVO attendee : attList) {
				// 해당 회의 참가자가 회의 진행자 또는 보조진행자인 경우 건너뛴다.
				if(attendee.getAttendRole() == AttendRole.HOST || attendee.getAssistantYN().equals('Y')) continue;
				
				ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingReportAuthorityCollection(attendee.getUserKey(), meetingKey);
				if(authorityCollection.hasAuthority(ReportAuth.OPINION)) {
					AlarmSendVO messengerAlarmSendVO;
					try {
							messengerAlarmSendVO = AlarmSendVO.builder()
									.userKey(attendee.getUserKey())
									.alarmDiv("B")
									.alarmDtlDiv(null)
									.meetingKey(meetingKey)
									.mailPurpose("note")
									.mailLinkUrl(url)
									.mailRole(null)
									.build();
							
							if(alarmServ.doSndMsg(messengerAlarmSendVO) <= 0) {
								return ResponseMessage.builder(ResponseMessage.StatusCode.UNPROCESSABLE_ENTITY)
										.message("알림 등록 실패")
										.build();
							}
					} catch (Exception e) {
						log.error(e.getMessage());
					}
				}
			}
		}
		return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
				.message("알림 등록 완료")
				.build();
	}
	
	/**
	 * 회의 관련 메일 개인 발송
	 * @param purpose : 메일 용도
	 * @param type : 메일 종류
	 * @param meetingKey : 회의 번호
	 * @param url : 회의 링크
	 * @param addr : 발신자 메일
	 * @param name : 발신자 이름
	 * @param role : 발신자 역할
	 * @return
	 */
	@SuppressWarnings("finally")
	public String sendMeetingMailOne(String purpose, String type, int meetingKey, String url, String addr, String userKey, String name, String role) {
		String alarmBody = "";
		EwpMeetingAssignVO assignVO = assignServ.getMeetingAssignOneByMeetingId(meetingKey).orElse(null); // 회의 스케쥴을 VO형태로 들고온다.
		String hostNm = assignVO.getSkdHost();
		EwpUserInfoVO writer = userServ.selectUserInfoOne(assignVO.getWriterKey()).orElse(null);
		String writerNm = writer.getDeptName() + " " + writer.getUserName();

		Map<String, String> mailMap = new HashMap<String, String>();

		mailMap.put("url", url);
		mailMap.put("type", type);
		mailMap.put("role", role);
		mailMap.put("addr", addr);
		mailMap.put("name", name);
		mailMap.put("hostNm", hostNm);
		mailMap.put("writerNm", writerNm);


		try {
			switch (purpose) { // 메일의 용도 (없을 시 메일 발송 불가)
				case "meeting": // 회의 개최 관련 메일
					EwpMeetingAttendeeVO isHost = null; // 회의 진행자
					List<EwpMeetingAttendeeVO> isAttendee = new ArrayList<>(); // 회의 참가자 리스트
					List<EwpMeetingAttendeeVO> isObserver = new ArrayList<>(); // 회의 참관자 리스트
					EwpMeetingAttendeeVO myAttendee = attServ.getMeetingAttendeeOne(userKey, meetingKey).orElse(null);
					if(myAttendee != null) {
						if(myAttendee.getTempPW() != null) {
							mailMap.put("tempPW", myAttendee.getTempPW());
						} else {
							mailMap.put("tempPW", "");
						}

						if(myAttendee.getExpireDate() != null) {
							DateTimeFormatter yearMonth = DateTimeFormatter.ofPattern("yyyy.MM.dd");
							String expireDate = myAttendee.getExpireDate().format(yearMonth);
							mailMap.put("expireDate", expireDate);
						} else {
							mailMap.put("expireDate", "");
						}
					}

					String startDate = "";
					String shortStartDate = "";
					if(assignVO.getBeginDateTime() != null) {
						DateTimeFormatter yearMonth = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
						startDate = assignVO.getBeginDateTime().format(yearMonth);
						
						DateTimeFormatter shortYearMonth = DateTimeFormatter.ofPattern("yy년 MM월 dd일");
						shortStartDate = assignVO.getBeginDateTime().format(shortYearMonth);
					}
					mailMap.put("startDate", startDate);
					mailMap.put("shortStartDate", shortStartDate);

					List<EwpMeetingAttendeeVO> voList = attServ.getMeetingAttendeeListByMeeting(meetingKey);
					if(voList != null) {
						// 회의 주최자 및 참관자 리스트 추가
						for(int i = 0; i < voList.size(); i++){
							EwpMeetingAttendeeVO attendee = voList.get(i);
							if (attendee.getAttendRole() == AttendRole.HOST){ // 회의 주최자인 경우
								isHost = attendee;
							} else if(attendee.getAttendRole() == AttendRole.ATTENDEE){ // 회의 참가자인 경우
								isAttendee.add(attendee);
							} else if(attendee.getAttendRole() == AttendRole.OBSERVER){ // 회의 참관자인 경우
								isObserver.add(attendee);
							}
						}
					}

					// 회의 내용 미작성 시 공백처리
					if(assignVO.getContents() == null) {
						assignVO = assignVO.toBuilder().contents("").build();
					}
					alarmBody = mailMng.sendMeetingMailOne(mailMap, assignVO, isHost, isAttendee, isObserver); // 메일 매니저로 메일을 보내준다.

					break;
				case "status": // 회의실 사용 신청 상태 변경 메일
					EwpRoomInfoVO roomVO = rmServ.selectRoomOne(assignVO.getRoomType(), assignVO.getRoomKey()).orElse(null);
					alarmBody = mailMng.sendStatusMailOne(mailMap, assignVO, roomVO); // 메일 매니저로 메일을 보내준다.
					break;
				case "note": // 회의록 검토 요청 메일
					List<EwpMeetingAttendeeVO> host = attServ.getMeetingAttendeeListByMeeting(meetingKey, AttendRole.HOST); // 회의 진행자
					alarmBody = mailMng.sendMeetingNoteMailOne(mailMap, host.get(0), assignVO); // 메일 매니저로 메일을 보내준다.
					break;
				default:
					break;
			}
			
		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			return alarmBody;
		}
	}
	
	/**
	 * 임시비밀번호 메일 발송
	 * @param purpose : 메일 타입
	 * @param addr : 수신자 메일
	 * @param type : 메일 타입
	 * @param newPw : 임시비밀번호
	 * @param url : 주소
	 * @return
	 */
	@SuppressWarnings("finally")
	public String sendResetPassMailOne(String purpose, String addr, String name, String newPw, String url) {
		Map<String, String> mailMap = new HashMap<String, String>();

		mailMap.put("purpose", purpose);
		mailMap.put("addr", addr);
		mailMap.put("name", name);
		mailMap.put("newPw", newPw);
		mailMap.put("url", url);

		String alarmBody = "";
		try {
			alarmBody = mailMng.sendResetPassMailOne(mailMap); // 메일 매니저로 메일을 보내준다.
		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			return alarmBody;
		}
	}
	
	/**
	 * 회의 참석 메신저 등록 (이미 컨트롤러에서 메일 수신 여부 확인)
	 * @param attendeeList : 수신자 리스트
	 * @param type : 메일 타입
	 * @param meetingKey : 회의 키
	 * @return
	 */
	public ResponseMessage sendAttendeeMessenger(List<EwpMeetingAttendeeVO> attendeeList, String type, int meetingKey) {
		if(alarmProperties.getMessenger().isEnabled()) {
			if(attendeeList != null && attendeeList.size() > 0) {
				EwpMeetingAssignVO meetingVO = assignServ.getMeetingAssignOneByMeetingId(meetingKey).orElse(null);
				String url = alarmProperties.getHost() + "/ewp/meeting/assign/" + meetingVO.getSkdKey();
				for(EwpMeetingAttendeeVO attendee : attendeeList) {
					AlarmSendVO emailAlarmSendVO;
					try {
						emailAlarmSendVO = AlarmSendVO.builder()
								.userKey(attendee.getUserKey())
								.alarmDiv("M")
								.alarmDtlDiv(null)
								.meetingKey(attendee.getMeetingKey())
								.alarmSubject("메일제목")
								.alarmBody("메일내용")
								.mailPurpose("meeting")
								.mailType(type)
								.mailLinkUrl(url)
								.mailRole(attendee.getAttendRole().toString())
								.build();
	
						if(alarmServ.doSndMsg(emailAlarmSendVO) <= 0) {
							return ResponseMessage.builder(ResponseMessage.StatusCode.UNPROCESSABLE_ENTITY)
									.message("메신저 등록 실패")
									.build();
						}
					} catch (Exception e) {
						log.error(e.getMessage());
					}
				}
			}
		}
		return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
				.message("메신저 등록 완료")
				.build();
	}
	
	/**
	 *  메신저 등록 (이미 컨트롤러에서 메일 수신 여부 확인)
	 * @param attendeeList : 수신자 리스트
	 * @param type : 메일 타입
	 * @param meetingKey : 회의 키
	 * @return
	 */
	@SuppressWarnings("finally")
	public Map<String, String> sendMeetingMessengerOne(String purpose
			, String type
			, String userKey
			, int meetingKey
			, String url
			, String name
			, String role) {
		
		Map<String, String> resultMap = new HashMap<String, String>();
		try {
			EwpMeetingAssignVO assignVO = assignServ.getMeetingAssignOneByMeetingId(meetingKey).orElse(null); // 회의 스케쥴을 VO형태로 들고온다.
			EwpMeetingInfoVO infoVO = mtServ.getMeetingInfoOne(meetingKey).orElse(null);

			DateTimeFormatter yearMonth = DateTimeFormatter.ofPattern("yyyy.MM.dd");
			String nowDate = assignVO.getBeginDateTime().format(yearMonth);
			
			DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm");
			String startTime = assignVO.getBeginDateTime().format(time);
			String endTime = assignVO.getFinishDateTime().format(time);

			String startDate = "";
			String shortStartDate = "";
			if(assignVO.getBeginDateTime() != null) {
				DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
				startDate = assignVO.getBeginDateTime().format(dateFormat);
				
				DateTimeFormatter shortYearMonth = DateTimeFormatter.ofPattern("yy년 MM월 dd일");
				shortStartDate = assignVO.getBeginDateTime().format(shortYearMonth);
			}

			Map<String, String> messengerMap = new HashMap<String, String>();

			messengerMap.put("purpose", purpose);
			messengerMap.put("type", type);
			messengerMap.put("userKey", userKey);
			messengerMap.put("url", url);
			messengerMap.put("name", name);
			messengerMap.put("role", role);
			messengerMap.put("title", infoVO.getTitle());
			messengerMap.put("roomName", assignVO.getRoom().getRoomName());
			messengerMap.put("nowDate", nowDate);
			messengerMap.put("startTime", startTime);
			messengerMap.put("shortStartDate", shortStartDate);
			messengerMap.put("endTime", endTime);
			messengerMap.put("startDate", startDate);
			messengerMap.put("host", assignVO.getSkdHost());
			switch (purpose) {
				case "meeting": // 회의 주최, 역할 변경, 회의 취소 , 회의 개최 예정 알람
					EwpMeetingAttendeeVO myAttendee = attServ.getMeetingAttendeeOne(userKey, meetingKey).orElse(null);
					if(myAttendee != null) {
						if(myAttendee.getTempPW() != null) {
							messengerMap.put("tempPW", myAttendee.getTempPW());
						} else {
							messengerMap.put("tempPW", "");
						}

						if(myAttendee.getExpireDate() != null) {
							String expireDate = myAttendee.getExpireDate().format(yearMonth);
							messengerMap.put("expireDate", expireDate);
						} else {
							messengerMap.put("expireDate", "");
						}
					}
					resultMap = messengerMng.sendMeetingMessengerOne(messengerMap);
					break;
				case "status": // 회의실 사용 신청 상태 변경 알람
					resultMap = messengerMng.sendStatusMessengerOne(messengerMap);
					break;
				case "note": // 회의록 검토 요청 알람
					resultMap = messengerMng.sendMeetingNoteMessengerOne(messengerMap);
					break;
			}
			
		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			return resultMap;
		}
		
	}
	
	// 오늘의 스케줄 페이지 알림 추가
	public ResponseMessage sendAttendeeAlarm(List<EwpMeetingAttendeeVO> attendeeList, String type, int meetingKey) {
		if(attendeeList != null && attendeeList.size() > 0) {
			EwpMeetingAssignVO assignVO = assignServ.getMeetingAssignOneByMeetingId(meetingKey).orElse(null);
			String url = alarmProperties.getHost() + "/ewp/meeting/assign/" + assignVO.getSkdKey();
			for(EwpMeetingAttendeeVO attendee : attendeeList) {
				try {
					AlarmSendVO emailAlarmSendVO;
					emailAlarmSendVO = AlarmSendVO.builder()
							.userKey(attendee.getUserKey())
							.alarmDiv("B")
							.alarmDtlDiv(null)
							.meetingKey(attendee.getMeetingKey())
							.mailPurpose("meeting")
							.mailType(type)
							.mailLinkUrl(url)
							.mailRole(attendee.getAttendRole().toString())
							.build();
					
					if(alarmServ.doSndMsg(emailAlarmSendVO) <= 0) {
						return ResponseMessage.builder(ResponseMessage.StatusCode.UNPROCESSABLE_ENTITY)
								.message("알림 등록 실패")
								.build();
					}
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}
		}
		return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
				.message("알림 등록 완료")
				.build();
	}
	
	/**
	 * 회의 관련 알림 개인 발송
	 * @param purpose : 메일 용도
	 * @param type : 메일 종류
	 * @param meetingKey : 회의 번호
	 * @param url : 회의 링크
	 * @param addr : 발신자 메일
	 * @param name : 발신자 이름
	 * @param role : 발신자 역할
	 * @return
	 */
	@SuppressWarnings("finally")
	public String makeMeetingAlarmOne(String purpose, String type, int meetingKey, String url, String addr, String userKey, String name, String role) {
		String alarmBody = "";
		Optional<EwpMeetingAssignVO> assignOpt = assignServ.getMeetingAssignOneByMeetingId(meetingKey); // 회의 스케쥴을 VO형태로 들고온다.
		if(!assignOpt.isPresent()) {
			return alarmBody;
		}
		EwpMeetingAssignVO assignVO = assignOpt.get();
		Map<String, String> alarmMap = new HashMap<String, String>();

		String startDate = "";
		if(assignVO.getBeginDateTime() != null) {
			DateTimeFormatter yearMonth = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
			startDate = assignVO.getBeginDateTime().format(yearMonth);
		}
		alarmMap.put("startDate", startDate);
		alarmMap.put("type", type);
		alarmMap.put("role", role);
		alarmMap.put("name", name);

		try {
			switch (purpose) { // 메일의 용도 (없을 시 메일 발송 불가)
				case "meeting": // 회의 개최 관련 메일
					alarmBody = boardAlarmMng.makeMeetingAlarmOne(alarmMap); // 알림을 추가해준다.
					break;
				case "status": // 회의실 사용 신청 상태 변경 메일
					EwpRoomInfoVO roomVO = rmServ.selectRoomOne(assignVO.getRoomType(), assignVO.getRoomKey()).orElse(null);
					alarmBody = boardAlarmMng.makeStatusAlarmOne(alarmMap, roomVO);
					break;
				case "note": // 회의록 검토 요청 메일
					alarmBody = boardAlarmMng.makeMeetingNoteAlarmOne(alarmMap, assignVO);
					break;
				default:
					break;
			}
			
		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			return alarmBody;
		}
	}
}
