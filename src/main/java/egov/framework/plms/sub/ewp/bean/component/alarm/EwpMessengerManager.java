package egov.framework.plms.sub.ewp.bean.component.alarm;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import egov.framework.plms.main.core.util.DateTimeUtil;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingAssignVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.organization.EwpUserInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("ewp")
public class EwpMessengerManager {

	/**
	 * 회의 관련 안내 문자 발송
	 * messengerMap 묶음 모음 
	 * --------------------------------
	 * @param purpose : 수신인 이름
	 * @param type : 수신자 메일
	 * @param userKey : 메일 타입
	 * @param url : 로그인페이지 링크
	 * @param name : 수신자 이름
	 * @param role : 회의 내의 수신자 역할
	 * @param title : 회의 제목
	 * @param roomName : 장소 이름
	 * @param nowDate : 회의 시작 날짜
	 * @param startTime : 회의 시작 시간
	 * @param endTime : 회의 종료 시간
	 * --------------------------------
	 */
	@SuppressWarnings("finally")
	public Map<String, String> sendMeetingMessengerOne(Map<String, String> messengerMap) {
		Map<String, String> resultMap = new HashMap<String, String>();
		String subject = "";
		String alarmBody = "";
		String name = messengerMap.get("name"); // 수신자 이름
		String startTime = messengerMap.get("startTime"); // 회의 시작 날짜
		String nowDate = messengerMap.get("nowDate"); // 회의 시작 날짜
		String startDate = messengerMap.get("startDate"); // 회의 시작 날짜 포맷버전
		String shortStartDate = messengerMap.get("shortStartDate"); // 회의 시작 날짜 짧은년도 포맷버전
		try {
			switch (messengerMap.get("type")) {
				case "open":
					switch (messengerMap.get("role")) {
						case "HOST":
							subject = shortStartDate + " 회의가 개설되었습니다";
							alarmBody = nowDate + " " + startTime + "에 개최하는 회의에 진행자로 초대되었습니다.";
							break;
						case "ATTENDEE":
							subject = shortStartDate + " 회의에 초대되었습니다";
							alarmBody = nowDate + " " + startTime + "에 개최하는 회의에 참석자로 초대되었습니다.";
							break;
						case "OBSERVER":
							subject = shortStartDate + " 회의에 초대되었습니다";
							alarmBody = nowDate + " " + startTime + "에 개최하는 회의에 참관자로 초대되었습니다.";
							break;
					}
					break;
				case "modi":
					subject = shortStartDate + " 회의의 역할이 변경되었습니다";
					switch (messengerMap.get("role")) {
						case "HOST":
							alarmBody = nowDate + " " + startTime + "에 개최하는 회의에 진행자로 초대되었습니다.";
							break;
						case "ATTENDEE":
							alarmBody = nowDate + " " + startTime + "에 개최하는 회의에 참석자로 초대되었습니다.";
							break;
						case "OBSERVER":
							alarmBody = nowDate + " " + startTime + "에 개최하는 회의에 참관자로 초대되었습니다.";
							break;
					}
					break;
				case "delete":
					switch (messengerMap.get("role")) {
						case "HOST":
							subject = shortStartDate + " 회의의 진행이 취소되었습니다";
							alarmBody = nowDate + " " + startTime + "에 개최하는 회의의 회의 진행이 취소되었습니다.";
							break;
						case "ATTENDEE":
							subject = shortStartDate + " 회의의 참석이 취소되었습니다";
							alarmBody = nowDate + " " + startTime + "에 개최하는 회의의 참석이 취소되었습니다.";
							break;
						case "OBSERVER":
							subject = shortStartDate + " 회의의 참관이 취소되었습니다";
							alarmBody = nowDate + " " + startTime + "에 개최하는 회의의 참관이 취소되었습니다.";
							break;
					}
					break;
				case "cancel":
					subject = shortStartDate + " 개최 예정 회의가 취소되었습니다";
					alarmBody = nowDate + " " + startTime + "에 개최 예정이였던 회의가 취소되었습니다.";
					break;
				case "expected":
					subject = shortStartDate + " 개최 예정 회의가 있습니다";
					alarmBody = nowDate + " " + startTime + "에 개최 예정인 회의가 있습니다.";
					break;
			}
			alarmBody = MeetingMessengerDefaultTemplate(messengerMap, alarmBody);
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			resultMap.put("subject", subject);
			resultMap.put("alarmBody", alarmBody);
			return resultMap;
		}
	}
	
	/**
	 * 회의실 사용 신청 상태 변경 메일 발송
	 * messengerMap 묶음 모음
	 * --------------------------------
	 * @param purpose : 수신인 이름
	 * @param type : 수신자 메일
	 * @param userKey : 메일 타입
	 * @param url : 로그인페이지 링크
	 * @param name : 수신자 이름
	 * @param role : 회의 내의 수신자 역할
	 * @param title : 회의 제목
	 * @param roomName : 장소 이름
	 * @param nowDate : 회의 시작 날짜
	 * @param startTime : 회의 시작 시간
	 * @param endTime : 회의 종료 시간
	 * --------------------------------
	 */
	@SuppressWarnings("finally")
	public Map<String, String> sendStatusMessengerOne(Map<String, String> messengerMap) {
		Map<String, String> resultMap = new HashMap<String, String>();
		
		String subject = "";
		String alarmBody = "";
		
		String name = messengerMap.get("name");
		String startTime = messengerMap.get("startTime");
		String nowDate = messengerMap.get("nowDate");
		String roomName = messengerMap.get("roomName");

		try {
			switch (messengerMap.get("type")) {
				case "NEW_REQUEST":
					subject = roomName + " 사용 신청 요청 발생";
					alarmBody = nowDate + " " + startTime + "에 " + roomName + "의 사용 신청 요청이 발생하였습니다.";
					break;
				case "REQUEST":
					subject = roomName + " 사용승인 요청 정상 처리 완료";
					alarmBody = nowDate + " " + startTime + "에 " + roomName + "의 사용 승인신청이 완료되었습니다.";
					break;
				case "APPROVED":
					subject = roomName + " 사용 승인 처리 완료";
					alarmBody = nowDate + " " + startTime + "에 " + roomName + "의 사용이 승인되었습니다.";
					break;
				case "REJECTED":
					subject = roomName + " 사용 승인 거부";
					alarmBody = nowDate + " " + startTime + "에 " + roomName + "의 사용이 승인 거부 되었습니다.";
					break;
				case "CANCELED":
					subject = roomName + " 사용 취소 처리 완료";
					alarmBody = nowDate + " " + startTime + "에 " + roomName + "의 사용이 취소되었습니다.";
					break;
			}
			alarmBody = MeetingMessengerDefaultTemplate(messengerMap, alarmBody);
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			resultMap.put("subject", subject);
			resultMap.put("alarmBody", alarmBody);
			return resultMap;
		}
	}

	/**
	 * 회의록 검토요청 메일
	 * messengerMap 묶음 모음
	 * --------------------------------
	 * @param purpose : 수신인 이름
	 * @param type : 수신자 메일
	 * @param userKey : 메일 타입
	 * @param url : 로그인페이지 링크
	 * @param name : 수신자 이름
	 * @param role : 회의 내의 수신자 역할
	 * @param title : 회의 제목
	 * @param roomName : 장소 이름
	 * @param nowDate : 회의 시작 날짜
	 * @param startTime : 회의 시작 시간
	 * @param endTime : 회의 종료 시간
	 * --------------------------------
	 */
	@SuppressWarnings("finally")
	public Map<String, String> sendMeetingNoteMessengerOne(Map<String, String> messengerMap) {
		Map<String, String> resultMap = new HashMap<String, String>();
		
		String subject = "";
		String alarmBody = "";

		String name = messengerMap.get("name");
		String startTime = messengerMap.get("startTime");
		String nowDate = messengerMap.get("nowDate");
		String startDate = messengerMap.get("startDate");
		String shortStartDate = messengerMap.get("shortStartDate");

		try {
			subject = shortStartDate + " 회의의 회의록 검토 요청이 왔습니다";
			alarmBody = nowDate + " " + startTime + "에 개최된 회의의 회의록 검토 요청이 발생하였습니다.";
			alarmBody = MeetingMessengerDefaultTemplate(messengerMap, alarmBody);
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			resultMap.put("subject", subject);
			resultMap.put("alarmBody", alarmBody);
			return resultMap;
		}
	}
	
	/**
	 * 회의 개최 예정 메일
	 * messengerMap 묶음 모음
	 * --------------------------------
	 * @param purpose : 수신인 이름
	 * @param type : 수신자 메일
	 * @param userKey : 메일 타입
	 * @param url : 로그인페이지 링크
	 * @param name : 수신자 이름
	 * @param role : 회의 내의 수신자 역할
	 * @param title : 회의 제목
	 * @param roomName : 장소 이름
	 * @param nowDate : 회의 시작 날짜
	 * @param startTime : 회의 시작 시간
	 * @param endTime : 회의 종료 시간
	 * --------------------------------
	 */
	@SuppressWarnings("finally")
	public Map<String, String> sendMeetingExpectedMessengerOne(Map<String, String> messengerMap) {
		Map<String, String> resultMap = new HashMap<String, String>();
		
		String subject = "";
		String alarmBody = "";

		String name = messengerMap.get("name");
		String startTime = messengerMap.get("startTime");
		String nowDate = messengerMap.get("nowDate");
		String roomName = messengerMap.get("roomName");
		String startDate = messengerMap.get("startDate");
		String shortStartDate = messengerMap.get("shortStartDate");
		try {
			subject = shortStartDate + " 개최 예정 회의가 있습니다";
			alarmBody = nowDate + " " + startTime + "에 " + roomName + "에서 회의가 개최될 예정입니다.";
			alarmBody = MeetingMessengerDefaultTemplate(messengerMap, alarmBody);
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			resultMap.put("subject", subject);
			resultMap.put("alarmBody", alarmBody);
			return resultMap;
		}
	}
	
	/**
	 * 회의 관련 메신저 공통 기본 템플릿
	 * @param contents : 메일 본문
	 */
	private String MeetingMessengerDefaultTemplate(Map<String, String> messengerMap, String contents) {
		String messengerContent = "";
		switch (messengerMap.get("purpose")) {
			case "meeting": // 회의 주최, 역할 변경, 회의 취소 알람
				messengerContent = "[회의안내]"
						+ "\r\n";
				break;
			case "status": // 회의실 사용 신청 상태 변경 알람
				messengerContent = "[회의실안내]"
						+ "\r\n";
				break;
			case "note": // 회의록 검토 요청 알람
				messengerContent = "[회의록안내]"
						+ "\r\n";
				break;
		}

		messengerContent += contents
				+ "\r\n" 
				+ "회의제목 : " 
				+ messengerMap.get("title")
				+ "\r\n" 
				+ "회의장소 : " 
				+ messengerMap.get("roomName") 
				+ "\r\n" 
				+ "주최자 : " 
				+ messengerMap.get("host") 
				+ "\r\n" 
				+ "회의시간 : " 
				+ messengerMap.get("nowDate") + " "
				+ messengerMap.get("startTime")
				+ " ~ "
				+ messengerMap.get("endTime")
				;
		switch (messengerMap.get("purpose")) {
			case "meeting": // 회의 주최, 역할 변경, 회의 취소 알람
				switch (messengerMap.get("type")) {
				case "open":
				case "modi":
				case "expected":
					messengerContent += "\r\n" 
						+ "링크 : " 
						+ messengerMap.get("url")
						;
					break;
				}
				break;
			case "status": // 회의실 사용 신청 상태 변경 알람
				messengerContent += "\r\n" 
					+ "링크 : " 
					+ messengerMap.get("url")
					;
				break;
			case "note": // 회의록 검토 요청 알람
				messengerContent += "\r\n" 
					+ "링크 : " 
					+ messengerMap.get("url")
					;
				break;
		}
		return messengerContent;
	}
	public String createMeetingUpdateNoticeMessengerAlarmSubject(EwpMeetingAssignVO oldAssignVO) {
		return oldAssignVO.getBeginDateTime().toLocalDate().format(DateTimeUtil.format("yyyy년 MM월 dd일"))+"개최 예정 회의가 변경되었습니다";
	}
	
	public String createMeetingUpdateNoticeMessengerAlarmBody(String url, EwpUserInfoVO writerVO, EwpMeetingAssignVO oldAssignVO, EwpMeetingAssignVO newAssignVO) {
		Set<String> changedSet = new HashSet<>();
		if(!oldAssignVO.getTitle().equals(newAssignVO.getTitle())) {
			changedSet.add("제목");
		}
		if(!oldAssignVO.getSkdHost().equals(newAssignVO.getSkdHost())) {
			changedSet.add("주최자");
		}
		if(!oldAssignVO.getBeginDateTime().isEqual(newAssignVO.getBeginDateTime()) || !oldAssignVO.getFinishDateTime().isEqual(newAssignVO.getFinishDateTime())) {
			changedSet.add("회의시간");
		}
		String body = "[회의변경안내]";
		body += "\r\n";
		body += LocalDateTime.now().format(DateTimeUtil.format("yyyy-MM-dd HH:mm"))+"에 "+writerVO.getNameplate()+"님이 회의의 "+changedSet+"을 변경하였습니다";
		body += "\r\n";
		body += "회의제목 : " + oldAssignVO.getTitle();
		if(changedSet.contains("제목")) {
			body += " → " + newAssignVO.getTitle();
		}
		body += "\r\n";
		body += "회의장소 : " + newAssignVO.getRoom().getRoomName();
		body += "\r\n";
		body += "주최자 : " + oldAssignVO.getSkdHost();
		if(changedSet.contains("주최자")) {
			body += " → " + newAssignVO.getSkdHost();
		}
		body += "\r\n";
		body += "회의시간 : " + oldAssignVO.getHoldingDate() + " "
		+ oldAssignVO.getBeginDateTime().toLocalTime().format(DateTimeUtil.format("HH:mm"))
		+ " ~ "
		+ oldAssignVO.getFinishDateTime().toLocalTime().format(DateTimeUtil.format("HH:mm"))
		;
		if(changedSet.contains("회의시간")) {
			body += " → " + newAssignVO.getHoldingDate() + " "
					+ newAssignVO.getBeginDateTime().toLocalTime().format(DateTimeUtil.format("HH:mm"))
					+ " ~ "
					+ newAssignVO.getFinishDateTime().toLocalTime().format(DateTimeUtil.format("HH:mm"))
					;
		}
		body += "\r\n";
		body += "링크 : " + url;
		return body;
	}
	private String createMeetingInfoTemplate(EwpMeetingAssignVO assignVO) {
		String messengerContent = "\r\n" 
				+ "회의제목 : " 
				+ assignVO.getTitle()
				+ "\r\n" 
				+ "회의장소 : " 
				+ assignVO.getRoom().getRoomName()
				+ "\r\n" 
				+ "주최자 : " 
				+ assignVO.getSkdHost()
				+ "\r\n" 
				+ "회의시간 : " 
				+ assignVO.getHoldingDate()
				+ assignVO.getBeginDateTime().toLocalTime().format(DateTimeUtil.format("HH:mm"))
				+ " ~ "
				+ assignVO.getFinishDateTime().toLocalTime().format(DateTimeUtil.format("HH:mm"))
				;
		return messengerContent;
	}
}
