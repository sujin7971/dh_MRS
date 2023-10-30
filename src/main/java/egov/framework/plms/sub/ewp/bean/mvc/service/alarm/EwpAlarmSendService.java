package egov.framework.plms.sub.ewp.bean.mvc.service.alarm;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.mvc.entity.alarm.AlarmSendDTO;
import egov.framework.plms.main.bean.mvc.entity.alarm.AlarmSendVO;
import egov.framework.plms.main.bean.mvc.mapper.alarm.AlarmSendMapper;
import egov.framework.plms.sub.ewp.bean.component.properties.AlarmProperties;
import egov.framework.plms.sub.ewp.bean.component.server.EwpServerListener;
import egov.framework.plms.sub.ewp.bean.mvc.entity.alarm.EwpMessengerDTO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.alarm.EwpSmsVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingAttendeeVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingScheduleVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.organization.EwpUserInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.mapper.mssql.EwpSmsMapper;
import egov.framework.plms.sub.ewp.bean.mvc.mapper.oracle.alarm.EwpMessengerMapper;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingAttendeeService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingScheduleService;
import egov.framework.plms.sub.ewp.bean.mvc.service.organization.EwpUserInfoService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("AlarmSendService")
@Profile("ewp")
public class EwpAlarmSendService {
	@Autowired
	private EwpServerListener serverListener;
	private static final String ALARM_DIV_SMS_S = "S";  //문자발송
	private static final String ALARM_DIV_EMAIL_E = "E";  //E메일
	private static final String ALARM_DIV_MESSENGER_M = "M";  //메신저
	private static final String ALARM_DIV_BOARD_B = "B";  //board 알림
	
	private static final String ALARM_DTL_DIV_G1_MONTH = "G1_MONTH";    //안내발송 한달전
	private static final String ALARM_DTL_DIV_G2_WEEK = "G2_WEEK";      //안내발송 일주일전
	private static final String ALARM_DTL_DIV_G3_DAY = "G3_DAY";        //안내발송 하루전
	private static final String ALARM_DTL_DIV_G4_HOUR = "G4_HOUR";      //안내발송 한시간전
	
	private static final String ALARM_MAIL_PURPOSE_MEETING = "meeting"; //메일 용도
	private static final String ALARM_MAIL_TYPE_EXPECTED = "expected";  //메일 종류, 알림발송
	
	@Autowired
	private AlarmSendMapper alarmSendMapper;
	@Autowired
	private EwpUserInfoService userServ;
	@Lazy
	@Autowired
	private EwpMessengerMapper ewpMessengerMapper;
	@Lazy
	@Autowired
	private EwpSmsMapper smsMapper;
	@Autowired
	private AlarmProperties alarmProperties;
	@Autowired
	private EwpMeetingScheduleService ewpScheServ;
	@Autowired
	private EwpMeetingAttendeeService ewpAttServ;
	
	public int doSndMsg(AlarmSendVO alarmSend) {
		return alarmSendMapper.doSndMsg(alarmSend);
	}
	
	public int doSndMessenger(EwpMessengerDTO messengerSend) {
		return ewpMessengerMapper.doSndMsg(messengerSend);
	}
	
	public int doSndSms(EwpSmsVO sctran) {
		return smsMapper.doSndSms(sctran);
	}
	
	public int updateAlarmSend(AlarmSendVO alarmSend) {
		return alarmSendMapper.updateAlarmSend(alarmSend);
	}
	
	/**
	 * 전송해야할 알람 리스트 조회
	 * @param alarmSend
	 * @return
	 */
	public List<AlarmSendVO> getAlarmSendListForPending(AlarmSendVO alarmSend) {
		return alarmSendMapper.getAlarmSendListForPending(alarmSend);
	}
	
	/**
	 * 특정 사용자에게 보내진 팝업 알람 리스트 조회
	 * @param params
	 * @return
	 */
	public List<AlarmSendVO> getAlarmSendListForUserPopup(AlarmSendVO params) {
		List<AlarmSendVO> rlist = alarmSendMapper.getAlarmSendListForUserPopup(params);
		return rlist;
	}	

	public void getMeetingInfoSendMonthList() {
		
		List<AlarmSendVO> alarmSendMonthList = alarmSendMapper.getMeetingInfoSendMonthList(AlarmSendVO.builder().alarmDiv(ALARM_DIV_SMS_S).build());
		if(alarmSendMonthList != null) {
			for(AlarmSendVO alarmSendVO : alarmSendMonthList){
				EwpUserInfoVO user = userServ.selectUserInfoOne(alarmSendVO.getUserKey()).orElse(null); // 보낼 사원의 정보를 들고온다
				
				EwpMeetingScheduleVO meetingVO = ewpScheServ.getAssignedMeetingScheduleOne(alarmSendVO.getMeetingKey()).orElse(null);
				String url = alarmProperties.getHost() + "/ewp/schedule/" + meetingVO.getSkdKey();
				EwpMeetingAttendeeVO attendee = ewpAttServ.getMeetingAttendeeOne(alarmSendVO.getUserKey(), alarmSendVO.getMeetingKey()).orElse(null);
				
				//MeetingInfoVO meetingInfoVO = meetingMapper.getMeetingInfoOne(Integer.valueOf(alarmSendVO.getUserKey()));
//				EwpMeetingScheduleVO meetingScheduleVO = ewpScheduleMapper.getScheduleOne(alarmSendVO.getMeetingKey());
//				
//				EwpRoomVO ewpRoomVO = null;
//				if(meetingScheduleVO.getRoomType().equals(RoomRole.MEETING_ROOM)) { //회의실
//					ewpRoomVO = ewpRoomMapper.selectMeetingRoomOne(meetingScheduleVO.getRoomKey());
//				}else if(meetingScheduleVO.getRoomType().equals(RoomRole.EDU_ROOM)) { //강의실
//					ewpRoomVO = ewpRoomMapper.selectEduRoomOne(meetingScheduleVO.getRoomKey());
//				}else {
//					// 강당
//				}
				
				if(alarmProperties.getMail().isEnabled()) {
					try {
						//ALARM_DTL_DIV_G1_MONTH
						AlarmSendVO emailAlarmSendVO = AlarmSendVO.builder().userKey(alarmSendVO.getUserKey())
								.alarmDiv(ALARM_DIV_EMAIL_E).alarmDtlDiv(ALARM_DTL_DIV_G1_MONTH)
								.meetingKey(alarmSendVO.getMeetingKey())
								.mailPurpose(ALARM_MAIL_PURPOSE_MEETING)
								.mailType(ALARM_MAIL_TYPE_EXPECTED)
								.alarmEmail(user.getEmail())
								.mailLinkUrl(url)
								.mailRole(attendee.getAttendRole().toString()).build();
						alarmSendMapper.doSndMsg(emailAlarmSendVO);
					} catch (Exception e) {
						log.error(e.getMessage());
					}
				}
				
				if(alarmProperties.getSms().isEnabled()) {
					try {
						AlarmSendVO smsAlarmSendVO = AlarmSendVO.builder().userKey(alarmSendVO.getUserKey())
								.alarmDiv(ALARM_DIV_SMS_S).alarmDtlDiv(ALARM_DTL_DIV_G1_MONTH)
								.meetingKey(alarmSendVO.getMeetingKey())
								.mailPurpose(ALARM_MAIL_PURPOSE_MEETING)
								.mailType(ALARM_MAIL_TYPE_EXPECTED)
								.alarmTel(user.getPersonalCellPhone())
								.mailLinkUrl(url)
								.mailRole(attendee.getAttendRole().toString()).build();
						alarmSendMapper.doSndMsg(smsAlarmSendVO);
					} catch (Exception e) {
						log.error(e.getMessage());
					}
				}
				
				if(alarmProperties.getMessenger().isEnabled()) {
					try {
						AlarmSendVO smsAlarmSendVO = AlarmSendVO.builder().userKey(alarmSendVO.getUserKey())
								.alarmDiv(ALARM_DIV_MESSENGER_M).alarmDtlDiv(ALARM_DTL_DIV_G1_MONTH)
								.meetingKey(alarmSendVO.getMeetingKey())
								.mailPurpose(ALARM_MAIL_PURPOSE_MEETING)
								.mailType(ALARM_MAIL_TYPE_EXPECTED)
								.mailLinkUrl(url)
								.mailRole(attendee.getAttendRole().toString()).build();
						alarmSendMapper.doSndMsg(smsAlarmSendVO);
					} catch (Exception e) {
						log.error(e.getMessage());
					}
				}
				
				try {
					AlarmSendVO smsAlarmSendVO = AlarmSendVO.builder().userKey(alarmSendVO.getUserKey())
							.alarmDiv(ALARM_DIV_BOARD_B).alarmDtlDiv(ALARM_DTL_DIV_G1_MONTH)
							.meetingKey(alarmSendVO.getMeetingKey())
							.mailPurpose(ALARM_MAIL_PURPOSE_MEETING)
							.mailType(ALARM_MAIL_TYPE_EXPECTED)
							.mailLinkUrl(url)
							.mailRole(attendee.getAttendRole().toString()).build();
					alarmSendMapper.doSndMsg(smsAlarmSendVO);
				} catch (Exception e) {
					log.error(e.getMessage());
				}
				
			}
		}
	}
	
	public void getMeetingInfoSendDayList() {
		List<AlarmSendVO> alarmSendDayList = alarmSendMapper.getMeetingInfoSendDayList(AlarmSendVO.builder().alarmDiv(ALARM_DIV_SMS_S).build());
		if(alarmSendDayList != null) {
			for(AlarmSendVO alarmSendVO : alarmSendDayList){
				EwpUserInfoVO user = userServ.selectUserInfoOne(alarmSendVO.getUserKey()).orElse(null); // 보낼 사원의 정보를 들고온다
				
				EwpMeetingScheduleVO meetingVO = ewpScheServ.getAssignedMeetingScheduleOne(alarmSendVO.getMeetingKey()).orElse(null);
				String url = alarmProperties.getHost() + "/ewp/schedule/" + meetingVO.getSkdKey();
				EwpMeetingAttendeeVO attendee = ewpAttServ.getMeetingAttendeeOne(alarmSendVO.getUserKey(), alarmSendVO.getMeetingKey()).orElse(null);
				
				//MeetingInfoVO meetingInfoVO = meetingMapper.getMeetingInfoOne(Integer.valueOf(alarmSendVO.getUserKey()));
//				EwpMeetingScheduleVO meetingScheduleVO = ewpScheduleMapper.getScheduleOne(alarmSendVO.getMeetingKey());
//				
//				EwpRoomVO ewpRoomVO = null;
//				if(meetingScheduleVO.getRoomType().equals(RoomRole.MEETING_ROOM)) { //회의실
//					ewpRoomVO = ewpRoomMapper.selectMeetingRoomOne(meetingScheduleVO.getRoomKey());
//				}else if(meetingScheduleVO.getRoomType().equals(RoomRole.EDU_ROOM)) { //강의실
//					ewpRoomVO = ewpRoomMapper.selectEduRoomOne(meetingScheduleVO.getRoomKey());
//				}else {
//					// 강당
//				}
				
				if(alarmProperties.getMail().isEnabled()) {
					try {
						//ALARM_DTL_DIV_G3_DAY
						AlarmSendVO emailAlarmSendVO = AlarmSendVO.builder().userKey(alarmSendVO.getUserKey())
								.alarmDiv(ALARM_DIV_EMAIL_E).alarmDtlDiv(ALARM_DTL_DIV_G3_DAY)
								.meetingKey(alarmSendVO.getMeetingKey())
								.mailPurpose(ALARM_MAIL_PURPOSE_MEETING)
								.mailType(ALARM_MAIL_TYPE_EXPECTED)
								.alarmEmail(user.getEmail())
								.mailLinkUrl(url)
								.mailRole(attendee.getAttendRole().toString()).build();
						alarmSendMapper.doSndMsg(emailAlarmSendVO);
					} catch (Exception e) {
						log.error(e.getMessage());
					}
				}
				
				if(alarmProperties.getSms().isEnabled()) {
					try {
						AlarmSendVO smsAlarmSendVO = AlarmSendVO.builder().userKey(alarmSendVO.getUserKey())
								.alarmDiv(ALARM_DIV_SMS_S).alarmDtlDiv(ALARM_DTL_DIV_G3_DAY)
								.meetingKey(alarmSendVO.getMeetingKey())
								.mailPurpose(ALARM_MAIL_PURPOSE_MEETING)
								.mailType(ALARM_MAIL_TYPE_EXPECTED)
								.alarmTel(user.getPersonalCellPhone())
								.mailLinkUrl(url)
								.mailRole(attendee.getAttendRole().toString()).build();
						alarmSendMapper.doSndMsg(smsAlarmSendVO);
					} catch (Exception e) {
						log.error(e.getMessage());
					}
				}
				
				if(alarmProperties.getMessenger().isEnabled()) {
					try {
						AlarmSendVO smsAlarmSendVO = AlarmSendVO.builder().userKey(alarmSendVO.getUserKey())
								.alarmDiv(ALARM_DIV_MESSENGER_M).alarmDtlDiv(ALARM_DTL_DIV_G3_DAY)
								.meetingKey(alarmSendVO.getMeetingKey())
								.mailPurpose(ALARM_MAIL_PURPOSE_MEETING)
								.mailType(ALARM_MAIL_TYPE_EXPECTED)
								.mailLinkUrl(url)
								.mailRole(attendee.getAttendRole().toString()).build();
						alarmSendMapper.doSndMsg(smsAlarmSendVO);
					} catch (Exception e) {
						log.error(e.getMessage());
					}
				}
				
				try {
					//ALARM_DTL_DIV_G3_DAY
					AlarmSendVO emailAlarmSendVO = AlarmSendVO.builder().userKey(alarmSendVO.getUserKey())
							.alarmDiv(ALARM_DIV_BOARD_B).alarmDtlDiv(ALARM_DTL_DIV_G3_DAY)
							.meetingKey(alarmSendVO.getMeetingKey())
							.mailPurpose(ALARM_MAIL_PURPOSE_MEETING)
							.mailType(ALARM_MAIL_TYPE_EXPECTED)
							.mailLinkUrl(url)
							.mailRole(attendee.getAttendRole().toString()).build();
					alarmSendMapper.doSndMsg(emailAlarmSendVO);
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}
		}
	}
	
	public void getMeetingInfoSendHourList() {
		List<AlarmSendVO> alarmSendHourList = alarmSendMapper.getMeetingInfoSendHourList(AlarmSendVO.builder().alarmDiv(ALARM_DIV_SMS_S).build());
		if(alarmSendHourList != null) {
			for(AlarmSendVO alarmSendVO : alarmSendHourList){
				EwpUserInfoVO user = userServ.selectUserInfoOne(alarmSendVO.getUserKey()).orElse(null); // 보낼 사원의 정보를 들고온다
				
				EwpMeetingScheduleVO meetingVO = ewpScheServ.getAssignedMeetingScheduleOne(alarmSendVO.getMeetingKey()).orElse(null);
				String url = alarmProperties.getHost() + "/ewp/schedule/" + meetingVO.getSkdKey();
				EwpMeetingAttendeeVO attendee = ewpAttServ.getMeetingAttendeeOne(alarmSendVO.getUserKey(), alarmSendVO.getMeetingKey()).orElse(null);
				
				//MeetingInfoVO meetingInfoVO = meetingMapper.getMeetingInfoOne(Integer.valueOf(alarmSendVO.getUserKey()));
//				EwpMeetingScheduleVO meetingScheduleVO = ewpScheduleMapper.getScheduleOne(alarmSendVO.getMeetingKey());
//				
//				EwpRoomVO ewpRoomVO = null;
//				if(meetingScheduleVO.getRoomType().equals(RoomRole.MEETING_ROOM)) { //회의실
//					ewpRoomVO = ewpRoomMapper.selectMeetingRoomOne(meetingScheduleVO.getRoomKey());
//				}else if(meetingScheduleVO.getRoomType().equals(RoomRole.EDU_ROOM)) { //강의실
//					ewpRoomVO = ewpRoomMapper.selectEduRoomOne(meetingScheduleVO.getRoomKey());
//				}else {
//					// 강당
//				}
				
				if(alarmProperties.getMail().isEnabled()) {
					try {
						//ALARM_DTL_DIV_G4_HOUR
						AlarmSendVO emailAlarmSendVO = AlarmSendVO.builder().userKey(alarmSendVO.getUserKey())
								.alarmDiv(ALARM_DIV_EMAIL_E).alarmDtlDiv(ALARM_DTL_DIV_G4_HOUR)
								.meetingKey(alarmSendVO.getMeetingKey())
								.mailPurpose(ALARM_MAIL_PURPOSE_MEETING)
								.mailType(ALARM_MAIL_TYPE_EXPECTED)
								.alarmEmail(user.getEmail())
								.mailLinkUrl(url)
								.mailRole(attendee.getAttendRole().toString()).build();
						alarmSendMapper.doSndMsg(emailAlarmSendVO);
					} catch (Exception e) {
						log.error(e.getMessage());
					}
				}
				
				if(alarmProperties.getSms().isEnabled()) {
					try {
						AlarmSendVO smsAlarmSendVO = AlarmSendVO.builder().userKey(alarmSendVO.getUserKey())
								.alarmDiv(ALARM_DIV_SMS_S).alarmDtlDiv(ALARM_DTL_DIV_G4_HOUR)
								.meetingKey(alarmSendVO.getMeetingKey())
								.mailPurpose(ALARM_MAIL_PURPOSE_MEETING)
								.mailType(ALARM_MAIL_TYPE_EXPECTED)
								.alarmTel(user.getPersonalCellPhone())
								.mailLinkUrl(url)
								.mailRole(attendee.getAttendRole().toString()).build();
						alarmSendMapper.doSndMsg(smsAlarmSendVO);
					} catch (Exception e) {
						log.error(e.getMessage());
					}
				}
				
				if(alarmProperties.getMessenger().isEnabled()) {
					try {
						AlarmSendVO smsAlarmSendVO = AlarmSendVO.builder().userKey(alarmSendVO.getUserKey())
								.alarmDiv(ALARM_DIV_MESSENGER_M).alarmDtlDiv(ALARM_DTL_DIV_G4_HOUR)
								.meetingKey(alarmSendVO.getMeetingKey())
								.mailPurpose(ALARM_MAIL_PURPOSE_MEETING)
								.mailType(ALARM_MAIL_TYPE_EXPECTED)
								.mailLinkUrl(url)
								.mailRole(attendee.getAttendRole().toString()).build();
						alarmSendMapper.doSndMsg(smsAlarmSendVO);
					} catch (Exception e) {
						log.error(e.getMessage());
					}
				}
				
				try {
					AlarmSendVO smsAlarmSendVO = AlarmSendVO.builder().userKey(alarmSendVO.getUserKey())
							.alarmDiv(ALARM_DIV_BOARD_B).alarmDtlDiv(ALARM_DTL_DIV_G4_HOUR)
							.meetingKey(alarmSendVO.getMeetingKey())
							.mailPurpose(ALARM_MAIL_PURPOSE_MEETING)
							.mailType(ALARM_MAIL_TYPE_EXPECTED)
							.mailLinkUrl(url)
							.mailRole(attendee.getAttendRole().toString()).build();
					alarmSendMapper.doSndMsg(smsAlarmSendVO);
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}
		}
	}
	
	/**
	 * 알람 읽음 처리 기능
	 * @param alarmNo
	 */
	public boolean putAlarmReadYN(LocalDateTime alarmNo, String userKey) {
		try {
			Integer result = alarmSendMapper.putAlarmReadYN(AlarmSendVO.builder().alarmNo(alarmNo).userKey(userKey).build());
			return (result == 0)?false:true;
		}catch(Exception e) {
			return false;
		}
	}
	
	/**
	 * 알람 전체 읽음 처리
	 * @param userKey
	 */
	public boolean putAllAlarmReadYN(String userKey) {
		try {
			Integer result = alarmSendMapper.putAllAlarmReadYN(userKey);
			return (result == 0)?false:true;
		}catch(Exception e) {
			return false;
		}
	}

	public Optional<AlarmSendVO> getAlarmOne(LocalDateTime alarmNo) {
		AlarmSendVO alarmVO = alarmSendMapper.getAlarmOne(alarmNo);
		return Optional.ofNullable(alarmVO);
	}
}
