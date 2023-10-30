package egov.framework.plms.sub.ewp.bean.component.alarm;

import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("ewp")
public class EwpSmsManager {
	
	/**
	 * 임시 비밀번호 메일 발송
	 * 비밀번호 초기화 및 회의 개최 시 발생하는 임시 비밀번호 메일
	 * mailMap 묶음 모음 
	 * --------------------------------
	 * @param userName : 수신인 이름
	 * @param addr : 수신자 메일
	 * @param purpose : 메일 타입
	 * @param newPw : 임시비밀번호
	 * @param url : 로그인페이지 링크
	 * --------------------------------
	 */
	@SuppressWarnings("finally")
	public String sendResetPassSmsOne(Map<String, String> smsMap) {
		String alarmBody = "[비밀번호안내]"
						+ "\r\n";
		String name = smsMap.get("name");
		try {
			switch (smsMap.get("perpose")) {
				case "reset": // 비밀번호 초기화
					alarmBody += name + "님, 비밀번호 초기화가 완료되었습니다."
							+ "\r\n" 
							+ "새로운 비밀번호는 "
							+ smsMap.get("newPw")
							+ "입니다.";
					break;
				case "temp": // 임시 비밀번호 발급
					alarmBody += name + "님, 임시비밀번호가 발급되었습니다."
							+ "\r\n" 
							+ "임시 비밀번호는 "
							+ smsMap.get("newPw")
							+ "입니다.";
					break;
			}
			
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			return alarmBody;
		}
	}

	/**
	 * 회의 관련 안내 문자 발송
	 * smsMap 묶음 모음 
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
	public String createMeetingSmsBody(Map<String, String> smsMap) {
		String alarmBody = "";
		String name = smsMap.get("name");
		try {
			switch (smsMap.get("type")) {
				case "open":
					switch (smsMap.get("role")) {
						case "HOST":
							alarmBody = "회의 진행자로 초대";
							break;
						case "ATTENDEE":
							alarmBody = "회의 참석자로 초대";
							break;
						case "OBSERVER":
							alarmBody = "회의 참관자로 초대";
							break;
					}
					break;
				case "modi":
					switch (smsMap.get("role")) {
						case "HOST":
							alarmBody += "회의 진행자로 변경";
							break;
						case "ATTENDEE":
							alarmBody += "회의 참석자로 변경";
							break;
						case "OBSERVER":
							alarmBody += "회의 참관자로 변경";
							break;
					}
					break;
				case "delete":
					switch (smsMap.get("role")) {
						case "HOST":
							alarmBody = "회의 진행 취소";
							break;
						case "ATTENDEE":
							alarmBody = "회의 참가 취소.";
							break;
						case "OBSERVER":
							alarmBody = "회의 참관 취소";
							break;
					}
					break;
				case "cancel":
					alarmBody = name + "회의 취소";
					break;
				case "expected":
					alarmBody = name + "예정된 회의";
					break;
			}

			alarmBody = MeetingSmsDefaultTemplate(smsMap, alarmBody);
		
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			return alarmBody;
		}
	}
	
	/**
	 * 회의실 사용 신청 상태 변경 메일 발송
	 * smsMap 묶음 모음
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
	public String createStatusSmsBody(Map<String, String> smsMap) {
		String alarmBody = "";
		String name = smsMap.get("name");
		try {
			switch (smsMap.get("type")) {
				case "NEW_REQUEST":
					alarmBody = "회의실 사용 신청 요청";
					break;
				case "REQUEST":
					alarmBody = "회의실 사용 승인 신청";
					break;
				case "APPROVED":
					alarmBody = "회의실 사용 승인";
					break;
				case "REJECTED":
					alarmBody = "회의실 사용 승인 거부";
					break;
				case "CANCELED":
					alarmBody = "회의실 사용 승인 취소";
					break;
			}

			alarmBody = MeetingSmsDefaultTemplate(smsMap, alarmBody);
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			return alarmBody;
		}
	}

	/**
	 * 회의록 검토요청 메일
	 * smsMap 묶음 모음
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
	public String createReportExamineSmsBody(Map<String, String> smsMap) {
		String alarmBody = "";
		String name = smsMap.get("name");
		try {
			alarmBody = "회의록 검토 요청";
			alarmBody = MeetingSmsDefaultTemplate(smsMap, alarmBody);
		
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			return alarmBody;
		}
	}
	
	/**
	 * 회의 개최 예정 메일
	 * smsMap 묶음 모음
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
	public String sendMeetingExpectedSmsOne(Map<String, String> smsMap) {
		String alarmBody = "";
		String name = smsMap.get("name");
		try {
			alarmBody = "개최 예정 회의";

			alarmBody = MeetingSmsDefaultTemplate(smsMap, alarmBody);
		
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			return alarmBody;
		}
	}
	
	/**
	 * 회의 관련 메일 공통 기본 템플릿
	 * @param contents : 메일 본문
	 */
	private String MeetingSmsDefaultTemplate(Map<String, String> smsMap, String contents) {
		String smsContent = "";
		switch (smsMap.get("purpose")) {
			case "meeting": // 회의 진행, 역할 변경, 회의 취소 알람
				smsContent = "[회의안내]"
						+ "\r\n";
				break;
			case "status": // 회의실 사용 신청 상태 변경 알람
				smsContent = "[회의실안내]"
						+ "\r\n";
				break;
			case "note": // 회의록 검토 요청 알람
				smsContent = "[회의록안내]"
						+ "\r\n";
				break;
		}
		smsContent += smsMap.get("title");
		/*
		smsContent += contents
				+ "\r\n"  
				+ smsMap.get("nowDate") + " "
				+ smsMap.get("startTime")
				+ " ~ "
				+ smsMap.get("endTime")
				+ " "
				+ smsMap.get("roomName") 
				;
		*/
		switch (smsMap.get("type")) {
			case "open":
			case "modi":
			case "expected":
				smsContent += "\r\n" 
				+ "임시비밀번호: " 
				+ smsMap.get("tempPW")
				+ "\r\n" 
				+ "만료일: " 
				+ smsMap.get("expireDate") 
				;
				break;
			case "cancel":
				smsContent += "\r\n"
				+ "회의 개최 취소"
				;
			case "delete":
				smsContent += "\r\n"
				+ "회의 참석 취소"
				;
		}
		return smsContent;
	}
}
