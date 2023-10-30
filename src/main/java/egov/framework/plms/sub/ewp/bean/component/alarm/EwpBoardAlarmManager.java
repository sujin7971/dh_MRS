package egov.framework.plms.sub.ewp.bean.component.alarm;

import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingAssignVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.room.EwpRoomInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("ewp")
public class EwpBoardAlarmManager {
	
	/**
	 * 임시 비밀번호 알림 생성
	 * 비밀번호 초기화 및 회의 개최 시 발생하는 임시 비밀번호 메일
	 * mailMap 묶음 모음 
	 * --------------------------------
	 * @param purpose : 메일 타입
	 * @param addr : 수신자 메일
	 * @param name : 수신인 이름
	 * @param newPw : 임시비밀번호
	 * @param url : 로그인페이지 링크
	 * --------------------------------
	 */
	@SuppressWarnings("finally")
	public String sendResetPassMailOne(Map<String, String> mailMap) {
		String subject = "";
		try {
			
			switch (mailMap.get("perpose")) {
				case "reset": // 비밀번호 초기화
					subject = mailMap.get("userName") + "님, 비밀번호가 초기화되었습니다.";
					break;
				case "temp": // 임시 비밀번호 발급
					subject = mailMap.get("userName") + "님, 임시비밀번호가 발급되었습니다.";
					break;
			}
			
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			return subject;
		}
	}
	
	/**
	 * 회의 개최 알림 생성
	 * mailMap 묶음 모음 
	 * --------------------------------
	 * @param url : 회의 주소
	 * @param type : 메일 타입
	 * @param role : 회의 역할
	 * @param addr : 메일 주소
	 * @param userName : 수신인 이름
	 * @param hostNm : 주최자 부서, 이름
	 * @param writerNm : 작성자 부서, 이름
	 * --------------------------------
	 * @param assign : 회의 정보
	 * @param isHost : 회의 진행자
	 * @param isAttendee : 참석자
	 * @param isObserver : 참관자
	 */
	@SuppressWarnings("finally")
	public String makeMeetingAlarmOne(
			Map<String, String> infoMap
	) {
		String subject = "";
		try {
			String userRole = "";
			switch (infoMap.get("role")) { // 메일 발신자의 역할
				case "HOST": // 회의 진행자
					userRole = "진행";
					break;
				case "ATTENDEE": // 회의 참석자
					userRole = "참석";
					break;
				case "OBSERVER": // 회의 참관자
					userRole = "참관";
					break;
				default:
					userRole = "참관";
					break;
			}

			infoMap.put("userRole", userRole);

			switch (infoMap.get("type")) { // 메일 제목 지정
				case "open": // 회의 생성
					switch (infoMap.get("role")) {
						case "HOST": // 회의 진행자
							subject = infoMap.get("startDate") + " 회의가 개설되었습니다.";
							break;
						default: // 회의 참가·참관자
							subject = infoMap.get("startDate") + " 회의에 초대되셨습니다.";
							break;
					}
					break;
				case "modi": // 회의 역할 수정
					subject = infoMap.get("startDate") + " 회의의 역할이 변경되셨습니다.";
					break;
				case "delete": // 회의 참석 취소
					subject = infoMap.get("startDate") + " 회의의 " + userRole + "이 취소되었습니다.";
					break;
				case "cancel": // 회의 개설 취소
					subject = infoMap.get("startDate") + " 개최 예정이였던 회의가 취소되었습니다.";
					break;
				case "expected": // 회의 개최 예정
					subject = infoMap.get("startDate") + " 개최 예정인 회의가 있습니다.";
					break;
				default:
					break;
			}
			
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			return subject;			
		}
	}
	
	/**
	 * 회의실 사용 신청 상태 변경 알림 생성
	 * mailMap 묶음 모음
	 * --------------------------------
	 * @param url : 회의 주소
	 * @param type : 승인 타입
	 * @param addr : 메일 주소
	 * @param userName : 수신인 이름
	 * @param writerNm : 작성자 부서, 이름
	 * --------------------------------
	 * @param assign : 회의 정보
	 */
	@SuppressWarnings("finally")
	public String makeStatusAlarmOne(
			Map<String, String> alarmMap
			, EwpRoomInfoVO room
	) {
		String subject = "";
		try {
			switch (alarmMap.get("type")) { // 메일 타입
				case "NEW_REQUEST": // 회의실 사용 신청이 발생 한 경우
					subject = room.getRoomName() 
								+ "의 사용 신청 요청이 발생하였습니다 <br/>" 
								+ alarmMap.get("startDate") + " ";
					break;
				case "REQUEST": // 회의실 사용 신청이 완료 된 경우
					subject = room.getRoomName() 
								+ "의 사용승인 요청이 정상 처리되었습니다 <br/>"
								+ alarmMap.get("startDate") + " ";
					break;
				case "APPROVED": // 회의실 사용이 승인이 된 경우
					subject = room.getRoomName() 
								+ "의 사용이 승인되었습니다 <br/>"
								+ alarmMap.get("startDate") + " ";
					break;
				case "REJECTED": // 회의실 사용이 승인 거부 된 경우
					subject = room.getRoomName() 
								+ "의 사용이 승인 거부되었습니다 <br/>"
								+ alarmMap.get("startDate") + " ";
					break;
				case "CANCELED": // 회의실 사용 승인이 취소 된 경우
					subject = room.getRoomName() + "의 사용 승인이 취소되었습니다 <br/>"
							+ alarmMap.get("startDate") + " ";
					break;
				default:
					break;
			}

		} catch(Exception e){
			e.printStackTrace();
		} finally {
			return subject;
		}
	}

	/**
	 * 회의록 검토요청 알림
	 * mailMap 묶음 모음
	 * --------------------------------
	 * @param url : 회의 주소
	 * @param type : 승인 타입
	 * @param addr : 메일 주소
	 * @param userName : 수신인 이름
	 * --------------------------------
	 * @param assign : 회의 정보
	 */
	@SuppressWarnings("finally")
	public String makeMeetingNoteAlarmOne(
			Map<String, String> alarmMap
			, EwpMeetingAssignVO assign
	) {
		String subject = "";
		try {
			subject = alarmMap.get("startDate") + " 회의의 회의록 검토 요청이 왔습니다.";

		} catch(Exception e){
			e.printStackTrace();
		} finally {
			return subject;
		}
	}
	
	/**
	 * 개최 예정 회의 알림
	 * mailMap 묶음 모음
	 * --------------------------------
	 * @param url : 회의 주소
	 * @param type : 승인 타입
	 * @param addr : 메일 주소
	 * @param userName : 수신인 이름
	 * --------------------------------
	 * @param assign : 회의 정보
	 */
	@SuppressWarnings("finally")
	public String makeMeetingExpectedAlarmOne(
			Map<String, String> alarmMap
			, EwpMeetingAssignVO assign
	) {
		String subject = "";
		try {
			subject = alarmMap.get("startDate") + " 개최 예정인 회의가 있습니다.";

		} catch(Exception e){
			e.printStackTrace();
		} finally {
			return subject;
		}
	}

}