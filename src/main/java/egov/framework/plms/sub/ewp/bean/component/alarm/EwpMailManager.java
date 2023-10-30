package egov.framework.plms.sub.ewp.bean.component.alarm;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.mail.internet.MimeMessage;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import egov.framework.plms.sub.ewp.bean.component.properties.AlarmProperties;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingAssignVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingAttendeeVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingScheduleVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.room.EwpRoomInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("ewp")
public class EwpMailManager {
	
	private final AlarmProperties alarmProperties;
	
	@Autowired
	private JavaMailSender mailSender;
	
	// yml 안의 메일 유저 정보를 불러온다
	@Value("${spring.mail.username}")
	String from;
	
	/**
	 * 임시 비밀번호 메일 발송
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
		MimeMessage mess = mailSender.createMimeMessage(); // mimeMessage를 생성
		String subject = "";
		try {
			MimeMessageHelper messageHelper = new MimeMessageHelper(mess, true, "UTF-8"); // 메일의 multipart를 boolean 으로 설정해주고, UTF-8로 인코딩을 설정한다.
			
			switch (mailMap.get("perpose")) {
				case "reset": // 비밀번호 초기화
					subject = mailMap.get("userName") + "님, 비밀번호가 초기화되었습니다.";
					break;
				case "temp": // 임시 비밀번호 발급
					subject = mailMap.get("userName") + "님, 임시비밀번호가 발급되었습니다.";
					break;
			}
			
			if(alarmProperties.getMail().isEnabled()) {
				messageHelper.setSubject(subject); // 메일 제목
				messageHelper.setTo(mailMap.get("addr")); // 메일 수신인
				messageHelper.setFrom(from); // 메일 발송인
				String messageInfo = setEwpResetMailInfo(mailMap); // 메일의 html을 설정해준다.
				messageHelper.setText(messageInfo, true); // 메일의 내용을 넣어주고, html형태인지 아닌지를 boolean으로 설정해준다.
				mailSender.send(mess); // 메일을 발송한다.
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			return subject;
		}
	}
	
	/**
	 * 회의 개최 메일 발송
	 * mailMap 묶음 모음 
	 * --------------------------------
	 * @param url : 회의 주소
	 * @param type : 메일 타입
	 * @param role : 회의 역할
	 * @param addr : 메일 주소
	 * @param userName : 수신인 이름
	 * @param hostNm : 회의 진행자 부서, 이름
	 * @param writerNm : 작성자 부서, 이름
	 * --------------------------------
	 * @param assignVO : 회의 정보
	 * @param isHost : 회의 진행자
	 * @param isAttendee : 참석자
	 * @param isObserver : 참관자
	 */
	@SuppressWarnings("finally")
	public String sendMeetingMailOne(
			Map<String, String> infoMap
			, EwpMeetingAssignVO assignVO
			, EwpMeetingAttendeeVO isHost
			, List<EwpMeetingAttendeeVO> isAttendee
			, List<EwpMeetingAttendeeVO> isObserver
	) {
		MimeMessage mess = mailSender.createMimeMessage(); // mimeMessage를 생성
		String subject = "";
		try {
			MimeMessageHelper messageHelper = new MimeMessageHelper(mess, true, "UTF-8"); // 메일의 multipart를 boolean 으로 설정해주고, UTF-8로 인코딩을 설정한다.

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
							subject = infoMap.get("shortStartDate") + " 회의가 개설되었습니다";
							break;
						default: // 회의 참가·참관자
							subject = infoMap.get("shortStartDate") + " 회의에 초대되셨습니다";
							break;
					}
					break;
				case "modi": // 회의 역할 수정
					subject = infoMap.get("shortStartDate") + " 회의의 역할이 변경되었습니다";
					break;
				case "delete": // 회의 참석 취소
					subject = infoMap.get("shortStartDate") + " 회의의 " + userRole + "이 취소되었습니다";
					break;
				case "cancel": // 회의 개설 취소
					subject = infoMap.get("shortStartDate") + " 개최 예정 회의가 취소되었습니다";
					break;
				case "expected": // 회의 개최 예정
					subject = infoMap.get("shortStartDate") + " 개최 예정 회의가 있습니다.";
					break;
				default:
					break;
			}
			
			if(alarmProperties.getMail().isEnabled()) {
				messageHelper.setSubject(subject); // 메일 제목
				messageHelper.setTo(infoMap.get("addr")); // 메일 수신인
				messageHelper.setFrom(from); // 메일 발송인
				String messageInfo = setEwpMeetingMailInfo(infoMap, assignVO, isHost, isAttendee, isObserver); // 메일의 html을 설정해준다.
				messageHelper.setText(messageInfo, true); // 메일의 내용을 넣어주고, html형태인지 아닌지를 boolean으로 설정해준다.
				mailSender.send(mess); // 메일을 발송한다.
			}

		} catch(Exception e){
			e.printStackTrace();
		} finally {
			return subject;			
		}
	}
	
	/**
	 * 회의실 사용 신청 상태 변경 메일 발송
	 * mailMap 묶음 모음
	 * --------------------------------
	 * @param url : 회의 주소
	 * @param type : 승인 타입
	 * @param addr : 메일 주소
	 * @param userName : 수신인 이름
	 * @param writerNm : 작성자 부서, 이름
	 * --------------------------------
	 * @param assignVO : 회의 정보
	 */
	@SuppressWarnings("finally")
	public String sendStatusMailOne(
			Map<String, String> mailMap
			, EwpMeetingAssignVO assignVO
			, EwpRoomInfoVO room
	) {
		MimeMessage mess = mailSender.createMimeMessage(); // mimeMessage를 생성
		String subject = "";
		try {
			MimeMessageHelper messageHelper = new MimeMessageHelper(mess, true, "UTF-8"); // 메일의 multipart를 boolean 으로 설정해주고, UTF-8로 인코딩을 설정한다.
			switch (mailMap.get("type")) { // 메일 타입
				case "NEW_REQUEST": // 회의실 사용 신청이 발생 한 경우
					subject = room.getRoomName() + " 사용 신청 요청 발생";
					break;
				case "REQUEST": // 회의실 사용 신청이 완료 된 경우
					subject = room.getRoomName() + " 사용승인 요청 정상 처리 완료";
					break;
				case "APPROVED": // 회의실 사용이 승인이 된 경우
					subject = room.getRoomName() + " 사용 승인 처리 완료";
					break;
				case "REJECTED": // 회의실 사용이 승인 거부 된 경우
					subject = room.getRoomName() + " 사용 승인 거부";
					break;
				case "CANCELED": // 회의실 사용 승인이 취소 된 경우
					subject = room.getRoomName() + " 사용 취소 처리 완료";
					break;
				default:
					break;
			}

			if(alarmProperties.getMail().isEnabled()) {
				messageHelper.setSubject(subject); // 메일 제목
				messageHelper.setTo(mailMap.get("addr")); // 메일 수신인
				messageHelper.setFrom(from); // 메일 발송인
				String messageInfo = setEwpStatusMailInfo(mailMap, assignVO); // 메일의 html을 설정해준다.
				messageHelper.setText(messageInfo, true); // 메일의 내용을 넣어주고, html형태인지 아닌지를 boolean으로 설정해준다.
				mailSender.send(mess); // 메일을 발송한다.
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			return subject;
		}
	}

	/**
	 * 회의록 검토요청 메일
	 * mailMap 묶음 모음
	 * --------------------------------
	 * @param url : 회의 주소
	 * @param type : 승인 타입
	 * @param addr : 메일 주소
	 * @param userName : 수신인 이름
	 * --------------------------------
	 * @param assignVO : 회의 정보
	 */
	@SuppressWarnings("finally")
	public String sendMeetingNoteMailOne(
			Map<String, String> mailMap
			, EwpMeetingAttendeeVO host
			, EwpMeetingAssignVO assignVO
	) {
		MimeMessage mess = mailSender.createMimeMessage(); // mimeMessage를 생성
		String subject = "";
		try {
			DateTimeFormatter yearMonth = DateTimeFormatter.ofPattern("yy년 MM월 dd일");
			String nowDate = assignVO.getBeginDateTime().format(yearMonth);
			MimeMessageHelper messageHelper = new MimeMessageHelper(mess, true, "UTF-8"); // 메일의 multipart를 boolean 으로 설정해주고, UTF-8로 인코딩을 설정한다.

			subject = nowDate + " 회의의 회의록 검토 요청이 왔습니다";

			if(alarmProperties.getMail().isEnabled()) {
				messageHelper.setSubject(subject); // 메일 제목
				messageHelper.setTo(mailMap.get("addr")); // 메일 수신인
				messageHelper.setFrom(from); // 메일 발송인
				String messageInfo = setEwpMeetingNoteMailInfo(mailMap, host, assignVO); // 메일의 html을 설정해준다.
				messageHelper.setText(messageInfo, true); // 메일의 내용을 넣어주고, html형태인지 아닌지를 boolean으로 설정해준다.
				mailSender.send(mess); // 메일을 발송한다.
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			return subject;
		}
	}
	
	/**
	 * 개최 예정 회의 메일
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
	public String sendMeetingExpectedMailOne(
			Map<String, String> mailMap
			, EwpMeetingAssignVO assign
	) {
		MimeMessage mess = mailSender.createMimeMessage(); // mimeMessage를 생성
		String subject = "";
		try {
			DateTimeFormatter yearMonth = DateTimeFormatter.ofPattern("yy년 MM월 dd일");
			String nowDate = assign.getBeginDateTime().format(yearMonth);
			MimeMessageHelper messageHelper = new MimeMessageHelper(mess, true, "UTF-8"); // 메일의 multipart를 boolean 으로 설정해주고, UTF-8로 인코딩을 설정한다.

			subject = nowDate + " 개최 예정 회의가 있습니다";

			if(alarmProperties.getMail().isEnabled()) {
				messageHelper.setSubject(subject); // 메일 제목
				messageHelper.setTo(mailMap.get("addr")); // 메일 수신인
				messageHelper.setFrom(from); // 메일 발송인
				String messageInfo = setEwpMeetingExpectedMailInfo(mailMap, assign); // 메일의 html을 설정해준다.
				messageHelper.setText(messageInfo, true); // 메일의 내용을 넣어주고, html형태인지 아닌지를 boolean으로 설정해준다.
				mailSender.send(mess); // 메일을 발송한다.
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			return subject;
		}
	}
	
	/**
	 * 비밀번호 초기화 메일 탬플릿
	 * mailMap 묶음 모음
	 * --------------------------------
	 * @param userName : 수신인 이름
	 * @param addr : 수신자 메일
	 * @param purpose : 메일 타입
	 * @param newPw : 임시비밀번호
	 * @param url : 로그인페이지 링크
	 * --------------------------------
	 */
	private String setEwpResetMailInfo(Map<String, String> mailMap) {
		String mediaQuery = "<style> @media screen and (max-width:570px) {.plmsMail .plmsHeader .ciDiv {width:10%;}.plmsMail .plmsHeader .sysTit {font-size:3.5vw;}.plmsMail .plmsContainer {padding:20px 0 !important;}} </style>";
		String mailContent = "";
		mailContent += ""
				 +      "<div class='plmsContent' >"
				 +          "<div class='wrap' >"
				 +              "<div>"
				 +                  "<p>"
				 +                      "<span>" + mailMap.get("name") + "</span>님"
				 +                  "</p>"
				 +                  "<br/>"
				 +                  "<p style='margin-bottom: 5px;'>";
		switch (mailMap.get("purpose")) { // 메일 종류
			case "reset": // 비밀번호 초기화 메일
				mailContent += "비밀번호가 초기화 되었습니다.";
				break;
			case "temp": // 임시비밀번호 발급 메일
				mailContent += "임시비밀번호가 발급되었습니다.";
				break;
			default:
				mailContent += "임시비밀번호가 발급되었습니다.";
				break;
		}
		mailContent += ""
				 +                  "</p>"
				 +                  "<p>"
				 +                      "임시 비밀번호는 <span style='font-weight: bold; font-size: 30px;'>" + mailMap.get("newPw") + "</span>입니다."
				 +                  "</p>"
				 +                  "<br/><br/>"
				 +              "</div>"
				 +          "</div>"
				 +          "<div class='btnDiv'>"
				 +              "<a class='plmsBtn' href='" + mailMap.get("url") + "'>바로가기</a>"
				 +          "</div>"
				 +      "</div>";
		;
		String mailInfo = MeetingMailDefaultTemplate(mailContent);
		//return mailInfo;
		return automaticCssInliner(mailInfo, mediaQuery);
	}

	/**
	 * 회의 개최 메일 탬플릿
	 * mailMap 묶음 모음 
	 * --------------------------------
	 * @param url : 회의 주소
	 * @param type : 메일 타입
	 * @param role : 회의 역할
	 * @param addr : 메일 주소
	 * @param userName : 수신인 이름
	 * @param hostNm : 회의 진행자 부서, 이름
	 * @param writerNm : 작성자 부서, 이름
	 * @param userRole : 회의 내 발신자 역할
	 * --------------------------------
	 * @param assignVO : 회의 정보
	 * @param isHost : 회의 진행자
	 * @param isAttendee : 참석자
	 * @param isObserver : 참관자
	 */
	private String setEwpMeetingMailInfo(
			Map<String, String> mailMap
			, EwpMeetingAssignVO assignVO
			, EwpMeetingAttendeeVO isHost
			, List<EwpMeetingAttendeeVO> isAttendee
			, List<EwpMeetingAttendeeVO> isObserver
	) {
		// 날짜 형식 변환
		DateTimeFormatter yearMonth = DateTimeFormatter.ofPattern("yyyy.MM.dd");
		String nowDate = assignVO.getBeginDateTime().format(yearMonth);
		
		DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm");
		String startTime = assignVO.getBeginDateTime().format(time);
		String endTime = assignVO.getFinishDateTime().format(time);
		
		String mediaQuery = "<style> @media screen and (max-width:570px) {.plmsMail .plmsHeader .ciDiv {width:10%;}.plmsMail .plmsHeader .sysTit {font-size:3.5vw;}.plmsMail .plmsContainer {padding:20px 0 !important;}} </style>";
		String mailContent = "";
		mailContent += ""
				 +      "<div class='plmsContent' >"
				 +          "<div class='wrap' >"
				 +              "<p><span>" + mailMap.get("name") + "</span>님</p>"
				 +              "<p>";
					switch (mailMap.get("type")) { // 메일 종류
						case "open": case "modi": // 회의가 신규 등록 또는 수정된 경우
							mailContent += "아래 회의에 <span>" + mailMap.get("userRole") + "자</span>로 초대되었습니다.";
							break;
						case "delete": // 회의가 참석에서 삭제 경우
							mailContent += "아래 회의의 <span>" + mailMap.get("userRole") + "</span>이(가) 취소되었습니다.";
							break;
						case "cancel": // 회의가 취소된 경우
							mailContent += "아래 회의가 취소되었습니다.";
							break;
						case "expected": // 회의가 개최 예정인 경우
							mailContent += "아래 회의가 " + nowDate + " 에 개최 예정입니다.";
							break;
						default:
							break;
					}
		mailContent +=             "</p>"
				 +              "<pre class='content'>  </pre>"
				 +              "<table class='plmsTable'>"
				 +                  "<tbody>"
				 +                      "<tr>"
				 +                          "<th>일 시</th>"
				 +                          "<td>"
				 +                              "<span>"
				 +                                  nowDate
				 +                              "</span>"
				 +                              "<br/>"
				 +                              "<span>"
				 +                                  startTime
				 +                                  " ~ "
				 +                                  endTime
				 +                              "</span>"
				 +                          "</td>"
				 +                      "</tr>"
				 +                      "<tr>"
				 +                          "<th>장 소</th>"
				 +                          "<td>"
				 +                              "<span>"
				 +                                  assignVO.getRoom().getOfficeName()
				 +                              "</span>"
				 +                              "<span>"
				 +                                  assignVO.getRoom().getRoomName()
				 +                              "</span>"
				 +                          "</td>"
				 +                      "</tr>"
				 +                      "<tr>"
				 +                          "<th>회의제목</th>"
				 +                          "<td>"
				 +                              "<span>"
				 +                                  assignVO.getTitle()
				 +                              "</span>"
				 +                          "</td>"
				 +                      "</tr>"
				 +                      "<tr>"
				 +                          "<th>주관자</th>"
				 +                          "<td>"
				 +                              "<span>"
				 +                                  mailMap.get("hostNm")
				 +                              "</span>"
				 +                          "</td>"
				 +                      "</tr>"
				 +                      "<tr>"
				 +                          "<th>회의 진행자</th>"
				 +                          "<td>"
				 +                              "<span>"
				 +                                  isHost.getUser().getDeptName() + " " 
				 +                                  isHost.getUser().getUserName()
				 +                              "</span>"
				 +                          "</td>"
				 +                      "</tr>"
				 +                      "<tr>"
				 +                          "<th>참석자</th>"
				 +                          "<td>";
					// 참석자 리스트 추가
					for(int i = 0; i < isAttendee.size(); i++) {
						mailContent +=          "<span>" + isAttendee.get(i).getUser().getUserName() + "</span>";
					}
		mailContent +=                      "</td>"
				 +                      "</tr>"
				 +                      "<tr>"
				 +                          "<th>참관자</th>"
				 +                          "<td>";
					// 참관자 리스트 추가
					for(int i = 0; i < isObserver.size(); i++) {
						mailContent +=          "<span>" + isObserver.get(i).getUser().getUserName() + "</span>";
					}
		mailContent +=                      "</td>"
				 +                      "</tr>"
				 +                      "<tr>"
				 +                          "<th>신청자</th>"
				 +                          "<td>"
				 +                              "<span>"
				 +                                  mailMap.get("writerNm")
				 +                              "</span>"
				 +                          "</td>"
				 +                      "</tr>";

		switch (mailMap.get("type")) { // 회의 생성, 역할 수정, 개최 예정일 경우 임시비밀번호 발송
			case "open": // 회의 생성
			case "modi": // 회의 역할 수정
			case "expected": // 회의 개최 예정
				mailContent +=          "<tr>"
						 +                  "<th>임시비밀번호</th>"
						 +                  "<td>"
						 +                      "<span>"
						 +                          mailMap.get("tempPW")
						 +                      "</span>"
						 +                  "</td>"
						 +              "</tr>"
						 +              "<tr>"
						 +                  "<th>임시비밀번호 만료일</th>"
						 +                  "<td>"
						 +                      "<span>"
						 +                          mailMap.get("expireDate")
						 +                      "</span>"
						 +                  "</td>"
						 +              "</tr>";
				break;
		}
		mailContent +=              "</tbody>"
				 +              "</table>"
				 +          "</div>"
				 +          "<div class='btnDiv'>";
		if(!mailMap.get("type").equals("delete" )) {
			mailContent +=      "<a class='plmsBtn' href='" + mailMap.get("url") + "'>확인하기</a>";
		}
		mailContent +=      "</div>"
				 +      "</div>"
		;
		String mailInfo = MeetingMailDefaultTemplate(mailContent);
		return automaticCssInliner(mailInfo, mediaQuery);
	}
	
	/**
	 * 회의실 사용 신청 상태 변경 탬플릿
	 * mailMap 묶음 모음
	 * --------------------------------
	 * @param url : 회의 주소
	 * @param type : 승인 타입
	 * @param addr : 메일 주소
	 * @param userName : 수신인 이름
	 * @param writerNm : 작성자 부서, 이름
	 * --------------------------------
	 * @param assignVO : 회의 정보
	 */
	private String setEwpStatusMailInfo(
			Map<String, String> mailMap
			, EwpMeetingAssignVO assignVO
	) {
		// 날짜 형식 변환
		DateTimeFormatter yearMonth = DateTimeFormatter.ofPattern("yyyy.MM.dd");
		String nowDate = assignVO.getBeginDateTime().format(yearMonth);
		
		DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm");
		String startTime = assignVO.getBeginDateTime().format(time);
		String endTime = assignVO.getFinishDateTime().format(time);
		
		String mediaQuery = "<style> @media screen and (max-width:570px) {.plmsMail .plmsHeader .ciDiv {width:10%;}.plmsMail .plmsHeader .sysTit {font-size:3.5vw;}.plmsMail .plmsContainer {padding:20px 0 !important;}} </style>";
		String mailContent = "";
		mailContent += ""
				 +      "<div class='plmsContent' >"
				 +          "<div class='wrap' >"
				 +              "<p><span>" + mailMap.get("name") + "</span>님</p>"
				 +              "<p>";
					switch (mailMap.get("type")) { // 메일 타입
						case "NEW_REQUEST": // 회의실 사용 신청이 발생 한 경우
							mailContent += "아래 회의실 사용 신청 요청이 발생하였습니다.";
							break;
						case "REQUEST": // 회의실 사용이 승인이 된 경우
							mailContent += "아래 회의의 회의실 사용 승인신청이 완료되었습니다.";
							break;
						case "APPROVED": // 회의실 사용이 승인이 된 경우
							mailContent += "아래 회의의 회의실 사용이 승인되었습니다.";
							break;
						case "REJECTED": // 회의실 사용이 승인 거부 된 경우
							mailContent += "아래 회의의 회의실 사용이 승인 거부되었습니다.";
							break;
						case "CANCELED": // 회의실 사용 승인이 취소 된 경우
							mailContent += "아래 회의의 회의실 사용이 취소되었습니다.";
							break;
						default:
							break;
					}
		mailContent +=             "</p>"
				 +              "<pre class='content'>  </pre>"
				 +              "<table class='plmsTable'>"
				 +                  "<tbody>"
				 +                      "<tr>"
				 +                          "<th>일 시</th>"
				 +                          "<td>"
				 +                              "<span>"
				 +                                  nowDate
				 +                              "</span>"
				 +                              "<br/>"
				 +                              "<span>"
				 +                                  startTime
				 +                                  " ~ "
				 +                                  endTime
				 +                              "</span>"
				 +                          "</td>"
				 +                      "</tr>"
				 +                      "<tr>"
				 +                          "<th>장 소</th>"
				 +                          "<td>"
				 +                              "<span>"
				 +                                  assignVO.getRoom().getOfficeName()
				 +                              "</span>"
				 +                              "<span>"
				 +                                  assignVO.getRoom().getRoomName()
				 +                              "</span>"
				 +                          "</td>"
				 +                      "</tr>"
				 +                      "<tr>"
				 +                          "<th>신청자</th>"
				 +                          "<td>"
				 +                              "<span>"
				 +                                  mailMap.get("writerNm")
				 +                              "</span>"
				 +                          "</td>"
				 +                      "</tr>"
				 +                  "</tbody>"
				 +              "</table>"
				 +          "</div>"
				 +          "<div class='btnDiv'>"
				 +              "<a class='plmsBtn' href='" + mailMap.get("url") + "'>확인하기</a>"
				 +          "</div>"
				 +      "</div>";
		String mailInfo = MeetingMailDefaultTemplate(mailContent);
		return automaticCssInliner(mailInfo, mediaQuery);
	}
	
	/**
	 * 회의록 검토요청 안내 탬플릿
	 * mailMap 묶음 모음
	 * --------------------------------
	 * @param url : 회의 주소
	 * @param type : 승인 타입
	 * @param addr : 메일 주소
	 * @param userName : 수신인 이름
	 * --------------------------------
	 * @param assignVO : 회의 정보
	 */
	private String setEwpMeetingNoteMailInfo(
			Map<String, String> mailMap
			, EwpMeetingAttendeeVO host
			, EwpMeetingAssignVO assignVO
	) {
		// 날짜 형식 변환
		DateTimeFormatter yearMonth = DateTimeFormatter.ofPattern("yyyy.MM.dd");
		String nowDate = assignVO.getBeginDateTime().format(yearMonth);
		
		DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm");
		String startTime = assignVO.getBeginDateTime().format(time);
		String endTime = assignVO.getFinishDateTime().format(time);
		
		String mediaQuery = "<style> @media screen and (max-width:570px) {.plmsMail .plmsHeader .ciDiv {width:10%;}.plmsMail .plmsHeader .sysTit {font-size:3.5vw;}.plmsMail .plmsContainer {padding:20px 0 !important;}} </style>";
		String mailContent = "";
		mailContent += ""
				 +      "<div class='plmsContent' >"
				 +          "<div class='wrap' >"
				 +              "<p><span>" + mailMap.get("name") + "</span>님</p>"
				 +              "<p>"
				 +                  "아래 회의의 회의록을 검토해주세요."
				 +              "</p>"
				 +              "<pre class='content'>  </pre>"
				 +              "<table class='plmsTable'>"
				 +                  "<tbody>"
				 +                      "<tr>"
				 +                          "<th>일 시</th>"
				 +                          "<td>"
				 +                              "<span>"
				 +                                  nowDate
				 +                              "</span>"
				 +                              "<br/>"
				 +                              "<span>"
				 +                                  startTime
				 +                                  " ~ "
				 +                                  endTime
				 +                              "</span>"
				 +                          "</td>"
				 +                      "</tr>"
				 +                      "<tr>"
				 +                          "<th>장 소</th>"
				 +                          "<td>"
				 +                              "<span>"
				 +                                  assignVO.getRoom().getOfficeName()
				 +                              "</span>"
				 +                              "<span>"
				 +                                  assignVO.getRoom().getRoomName()
				 +                              "</span>"
				 +                          "</td>"
				 +                      "</tr>"
				 +                      "<tr>"
				 +                          "<th>회의제목</th>"
				 +                          "<td>"
				 +                              "<span>"
				 +                                  assignVO.getTitle()
				 +                              "</span>"
				 +                          "</td>"
				 +                      "</tr>"
				 +                      "<tr>"
				 +                          "<th>주관자</th>"
				 +                          "<td>"
				 +                              "<span>"
				 +                                  mailMap.get("hostNm")
				 +                              "</span>"
				 +                          "</td>"
				 +                      "</tr>"
				 +                      "<tr>"
				 +                          "<th>회의 진행자</th>"
				 +                          "<td>"
				 +                              "<span>"
				 +                                  host.getUser().getDeptName() + " " 
				 +                                  host.getUser().getUserName()
				 +                              "</span>"
				 +                          "</td>"
				 +                      "</tr>"
				 +                  "</tbody>"
				 +              "</table>"
				 +          "</div>"
				 +          "<div class='btnDiv'>"
				 +              "<a class='plmsBtn' href='" + mailMap.get("url") + "'>확인하기</a>"
				 +          "</div>"
				 +      "</div>";
		String mailInfo = MeetingMailDefaultTemplate(mailContent);
		return automaticCssInliner(mailInfo, mediaQuery);
	}
	
	/**
	 * 개최 예정 회의 탬플릿
	 * mailMap 묶음 모음
	 * --------------------------------
	 * @param url : 회의 주소
	 * @param type : 승인 타입
	 * @param addr : 메일 주소
	 * @param userName : 수신인 이름
	 * --------------------------------
	 * @param assign : 회의 정보
	 */
	private String setEwpMeetingExpectedMailInfo(
			Map<String, String> mailMap
			, EwpMeetingAssignVO assign
	) {
		// 날짜 형식 변환
		DateTimeFormatter yearMonth = DateTimeFormatter.ofPattern("yyyy.MM.dd");
		String nowDate = assign.getBeginDateTime().format(yearMonth);
		
		DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm");
		String startTime = assign.getBeginDateTime().format(time);
		String endTime = assign.getFinishDateTime().format(time);
		
		String mediaQuery = "<style> @media screen and (max-width:570px) {.plmsMail .plmsHeader .ciDiv {width:10%;}.plmsMail .plmsHeader .sysTit {font-size:3.5vw;}.plmsMail .plmsContainer {padding:20px 0 !important;}} </style>";
		String mailContent = "";
		mailContent += ""
				 +      "<div class='plmsContent' >"
				 +          "<div class='wrap' >"
				 +              "<p><span>" + mailMap.get("name") + "</span>님</p>"
				 +              "<p>"
				 +                  "아래 회의가 " + nowDate + " 에 개최 예정입니다."
				 +              "</p>"
				 +              "<pre class='content'>  </pre>"
				 +              "<table class='plmsTable'>"
				 +                  "<tbody>"
				 +                      "<tr>"
				 +                          "<th>일 시</th>"
				 +                          "<td>"
				 +                              "<span>"
				 +                                  nowDate
				 +                              "</span>"
				 +                              "<br/>"
				 +                              "<span>"
				 +                                  startTime
				 +                                  " ~ "
				 +                                  endTime
				 +                              "</span>"
				 +                          "</td>"
				 +                      "</tr>"
				 +                      "<tr>"
				 +                          "<th>장 소</th>"
				 +                          "<td>"
				 +                              "<span>"
				 +                                  assign.getRoom().getOfficeName()
				 +                              "</span>"
				 +                              "<span>"
				 +                                  assign.getRoom().getRoomName()
				 +                              "</span>"
				 +                          "</td>"
				 +                      "</tr>"
				 +                      "<tr>"
				 +                          "<th>회의제목</th>"
				 +                          "<td>"
				 +                              "<span>"
				 +                                  assign.getTitle()
				 +                              "</span>"
				 +                          "</td>"
				 +                      "</tr>"
				 +                      "<tr>"
				 +                          "<th>주관자</th>"
				 +                          "<td>"
				 +                              "<span>"
				 +                                  mailMap.get("hostNm")
				 +                              "</span>"
				 +                          "</td>"
				 +                      "</tr>"
				 +                      "<tr>"
				 +                          "<th>신청자</th>"
				 +                          "<td>"
				 +                              "<span>"
				 +                                  mailMap.get("writerNm")
				 +                              "</span>"
				 +                          "</td>"
				 +                      "</tr>"
				 +                      "<tr>"
				 +                          "<th>임시비밀번호</th>"
				 +                          "<td>"
				 +                              "<span>"
				 +                                  mailMap.get("tempPW")
				 +                              "</span>"
				 +                          "</td>"
				 +                      "</tr>"
				 +                      "<tr>"
				 +                          "<th>임시비밀번호 만료일</th>"
				 +                          "<td>"
				 +                              "<span>"
				 +                                  mailMap.get("expireDate")
				 +                              "</span>"
				 +                          "</td>"
				 +                      "</tr>"
				 +                  "</tbody>"
				 +              "</table>"
				 +          "</div>"
				 +          "<div class='btnDiv'>"
				 +              "<a class='plmsBtn' href='" + mailMap.get("url") + "'>확인하기</a>"
				 +          "</div>"
				 +      "</div>";
		String mailInfo = MeetingMailDefaultTemplate(mailContent);
		return automaticCssInliner(mailInfo, mediaQuery);
	}
	
	/**
	 * 회의 관련 메일 공통 기본 템플릿
	 * @param contents : 메일 본문
	 */
	private String MeetingMailDefaultTemplate(String contents) {
		File image1;
		String encodeString = "";
		try {
			// 메일 첨부 용 사업소 로고 base64 변환 작업
			image1 = ResourceUtils.getFile("classpath:img/bi.svg");
			byte[] fileContent = FileUtils.readFileToByteArray(image1);
			encodeString = Base64.getEncoder().encodeToString(fileContent);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		File image2;
		String encodeString2 = "";
		try {
			// 메일 첨부 용 사업소 로고 base64 변환 작업
			image2 = ResourceUtils.getFile("classpath:img/ci_ewp.png");
			byte[] fileContent2 = FileUtils.readFileToByteArray(image2);
			encodeString2 = Base64.getEncoder().encodeToString(fileContent2);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String mailInfo = "";
		mailInfo += ""
				 + "<html>"
				 + "<head>"
				 + "</head>"
				 + "<body class='plmsMail'>"
				 +     "<style data-premailer='ignore'>"
				 +         ".plmsMail {font-family:'Malgun Gothic', 'Noto Sans KR', arial}"
				 +         ".plmsMail .plmsContainer {width: 800px;max-width:90vw;padding:20px 0; font-family:'Malgun Gothic','Noto Sans KR', sans-serif; overflow:hidden;}"
				 +         ".plmsMail .plmsHeader {height:60px; display:flex; border-bottom:2px solid #555;}"
				 +         ".plmsMail .plmsHeader .ciDiv {width:57px; position:relative;}"
				 +         ".plmsMail .plmsHeader .ciDiv img {width:100%; position:absolute; top:50%; transform:translateY(-50%);}"
				 +         ".plmsMail .plmsHeader .sysTit {display:flex;place-self: center; padding-left:8px; color:#333; font-size:0.8rem; font-weight:bold; letter-spacing:-1px;}"
				 +         ".plmsMail .plmsHeader .plms_tit {display: flex; place-self: center; margin-left:auto;}"
				 +         ".plmsMail .plmsHeader .plms_tit > .ciSvg {width:32px; height: auto; margin-right: 8px;}"
				 +         ".plmsMail .plmsHeader .plms_tit > .ciSubTit {font-size:0.8rem; font-weight:bold; color: #034364; white-space: nowrap;}"
				 +         ".plmsMail .plmsHeader .plms_tit > .ciSvg .d {fill: #034364}"
				 +         ".plmsMail .plmsCi {padding:30px 10px 0; display:flex; align-items:center; justify-content:center; font-size:1.5rem;}"
				 +         ".plmsMail .plmsCi img { width:106px; height:auto;}"
				 +         ".plmsMail .plmsContent {padding:30px;}"
				 +         ".plmsMail .plmsContent > .btnDiv {padding:30px 0 40px; text-align:center;}"
				 +         ".plmsMail .plmsContent > .wrap {line-height:1.6; width:100%;}"
				 +         ".plmsMail .plmsContent p {color:#006b85; font-size:1rem; font-weight:900; line-height:1; letter-spacing:-0.5px;}"
				 +         ".plmsMail .plmsContent .content {font-family:'Malgun Gothic', 'Noto Sans KR'; font-size:14px; line-height:1.5; white-space: pre-line; margin: 0 ; word-break:keep-all;}"
				 +         ".plmsMail .plmsTable {font-size:0.8rem; border: 1px solid #b8b8b8; border-radius: 6px; background: #f9f9f9; margin:30px 0; width:100%;}"
				 +         ".plmsMail .plmsTable tr > * {border-bottom:1px solid #dedede;}"
				 +         ".plmsMail .plmsTable tr:last-child > * {border:none !important;}"
				 +         ".plmsMail .plmsTable th {width:80px; text-align:left; vertical-align:top; padding:6px 6px 6px 12px;}"
				 +         ".plmsMail .plmsTable td {padding:10px 6px;}"
				 +         ".plmsMail .plmsTable td span {margin-right:8px; vertical-align:top;}"
				 +         ".plmsMail .plmsBtn {height: 46px; padding: 10px 88px; min-width: 100px; background-color: #f3f3f3; color: #000 !important; border: 1px solid #a8a8a8; outline: none; border-radius: 3px; cursor: pointer; font-weight: bold; font-size: 15px;text-decoration: none;}"
				 +     "</style>"
				 +     "<div class='plmsContainer'>"
				 +         "<div class='plmsHeader'>"
				 +             "<div class='ciDiv'>"
				 +                 "<img src='data:image/png;base64,"+ encodeString2 +"' alt=''>"
				 +             "</div>"
				 +             "<div class='sysTit'>스마트 회의시스템</div>"
				 +             "<div class='plms_tit'>"
				 +                 "<div class='ciSvg'>"
				 +                     "<img src='data:image/svg+xml;base64,"+ encodeString +"'>"
				 +                 "</div>"
				 +                 "<div class='ciSubTit'>PAPERLESS MEETING</div>"
				 +             "</div>"
				 +         "</div>";
		mailInfo += contents;	
		mailInfo += ""
				 +     "</div>"
				 + "</body>"
				 + "</html>";
		return mailInfo;
	}
	
	/**
	 * 메일 내 style태그 내용 본문태그에 attr로 변환 작업
	 * @param htmlString : 메일 본문
	 * @param mediaQuery : style 내 media태그
	 */
	private String automaticCssInliner (String htmlString, String mediaQuery) {
		Document doc = Jsoup.parse(htmlString);
		Elements els = doc.select("style");
		for (Element e : els) {
            String styleRules = e.getAllElements().get(0).data().replaceAll(
                    "\n", "").trim(), delims = "{}";
            StringTokenizer st = new StringTokenizer(styleRules, delims);
            while (st.countTokens() > 1) {
                String selector = st.nextToken(), properties = st.nextToken();
                Elements selectedElements = doc.select(selector);
                for (Element selElem : selectedElements) {
                	String oldProperties = selElem.attr("style");
                	selElem.attr("style",
                			oldProperties.length() > 0 ? concatenateProperties(
                					oldProperties, properties) : properties);
                }
            }
            e.remove();
        }
		doc.head().append(mediaQuery);
		return doc.html();
	}

	/**
	 * 메일 내 style태그 내용 본문태그에 attr로 변환 작업 진행 중 css 별 세미콜론 처리
	 * @param oldProp : 기존 스타일
	 * @param newProp : 새로 삽입한 스타일
	 */
	private static String concatenateProperties(String oldProp, String newProp) {
		oldProp = oldProp.trim();
		if (!newProp.endsWith(";"))
			newProp += ";";
		return newProp + oldProp; // The existing (old) properties should take precedence.
	}

}
