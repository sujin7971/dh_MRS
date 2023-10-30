package egov.framework.plms.sub.ewp.bean.component.auth;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import egov.framework.plms.main.bean.component.auth.MeetingAuthorityManager;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingInfoVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingScheduleVO;
import egov.framework.plms.main.core.model.enums.AuthCode;
import egov.framework.plms.main.core.model.enums.auth.MeetingAuth;
import egov.framework.plms.main.core.model.enums.meeting.AttendRole;
import egov.framework.plms.main.core.model.enums.meeting.MeetingStatus;
import egov.framework.plms.main.core.model.enums.meeting.ScheduleType;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingAssignVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingAttendeeVO;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingAssignService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingAttendeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
/**
 * 회의 권한 제한 및 참석자들에게 허용할 권한을 부여해줄 컴포넌트
 * 
 * @author mckim
 * @see {@link MeetingAuthorityManager}
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Profile("ewp")
public class EwpMeetingAuthorityCertificater {
	private final MeetingAuthorityManager meetingAuthorityMng;
	
	private final EwpMeetingAssignService assignServ;
	
	private final EwpMeetingAttendeeService attServ;
	
	public boolean certificate(Integer meetingKey) {
		Optional<EwpMeetingAssignVO> assignOpt = assignServ.getMeetingAssignOneByMeetingId(meetingKey);
		if(assignOpt.isPresent()) {
			return certificate(assignOpt.get());
		}else {
			return false;
		}
	}

	/**
	 * 회의가 사용자에게 허용할 권한 설정 및 참석유형/부서/사용자별 허용할 권한을 회의 진행상황에 맞춰 부여. 정상적으로 진행 된 경우 해당 사용신청정보와 관계된 {@link MeetingScheduleVO}및 {@link MeetingInfoVO}의 캐시를 초기화
	 * 
	 * 
	 * @param assignVO 권한을 처리해야할 사용신청정보. 
	 * @return 파라미터 또는 파라미터의 멤버변수 {@link EwpMeetingAssignVO#skdKey} 나 {@link EwpMeetingAssignVO#meetingKey}가 <code>NULL<code>인 경우 <code>false<code>,<br>
	 * 처리가 정상적으로 완료 된 경우 <code>true<code>
	 */
	@Caching(evict = {
		@CacheEvict(value = "meeting", key = "#assignVO.meetingKey", condition="#assignVO.meetingKey!=null"), 
		@CacheEvict(value = "schedule", key = "#assignVO.skdKey", condition="#assignVO.skdKey!=null")
	})
	public boolean certificate(EwpMeetingAssignVO assignVO) {
		log.info("회의(번호: {}, 진행: {}) 권한 갱신", assignVO.getMeetingKey(), assignVO.getMeetingStatus());
		Set<AuthCode> bannedCollection = new HashSet<>();
		if(assignVO.getSkdType() == ScheduleType.INSTANT) {
			bannedCollection.add(MeetingAuth.EXTEND);
		}
		if(assignVO.getElecYN() == 'N') {
			bannedCollection.add(MeetingAuth.UPLOAD);
			bannedCollection.add(MeetingAuth.INVITE);
			bannedCollection.add(MeetingAuth.ATTEND);
			bannedCollection.add(MeetingAuth.CHECK);
			bannedCollection.add(MeetingAuth.SIGN);
			bannedCollection.add(MeetingAuth.EXTEND);
			bannedCollection.add(MeetingAuth.COPY);
			bannedCollection.add(MeetingAuth.REPORT);
			bannedCollection.add(MeetingAuth.PHOTO);
			bannedCollection.add(MeetingAuth.VOICE);
		}else {
			//bannedCollection.add(MeetingAuth.EXTEND);
			bannedCollection.add(MeetingAuth.SIGN);
			bannedCollection.add(MeetingAuth.PHOTO);
			bannedCollection.add(MeetingAuth.VOICE);
		}
		setAttendeeAuthority(assignVO);
		setMeetingAuthority(assignVO, bannedCollection);
		return true;
	}
	
	/**
	 * 일반/전자회의 구분에 맞춰 회의 자체 허용 권한 설정
	 * @param assignVO
	 * @param bannedCollection
	 */
	private void setMeetingAuthority(EwpMeetingAssignVO assignVO, Set<AuthCode> bannedCollection) {
		Set<AuthCode> functionCollection;
		Integer meetingKey = assignVO.getMeetingKey();
		if(assignVO.getElecYN() == 'N') {
			functionCollection = setMeetingAuthorityForRental(assignVO);
		}else {
			functionCollection = setMeetingAuthorityForElec(assignVO);
		}
		functionCollection.removeAll(bannedCollection);
		meetingAuthorityMng.changeResourceStickyBit(meetingKey, functionCollection);
	}
	
	/**
	 * 일반 회의의 승인단계에 따른 권한 설정
	 * @param assignVO
	 * @param bannedCollection
	 * @return
	 */
	private Set<AuthCode> setMeetingAuthorityForRental(EwpMeetingAssignVO assignVO) {
		MeetingStatus meetingStatus = assignVO.getMeetingStatus();
		Set<AuthCode> functionCollection = new HashSet<>();
		functionCollection.add(MeetingAuth.READ);
		switch(meetingStatus) {
			case NEW:
			case UNAPPROVAL:
			case APPROVED:
			case OPENING:
				functionCollection.addAll(new HashSet<>(Arrays.asList(
						MeetingAuth.VIEW,
						MeetingAuth.UPDATE,
						MeetingAuth.CANCEL
					)));
				break;
			case START:
				functionCollection.addAll(new HashSet<>(Arrays.asList(
						MeetingAuth.VIEW,
						MeetingAuth.UPDATE,
						MeetingAuth.CANCEL,
						MeetingAuth.FINISH
					)));
				break;
			case FINISH:
			case CLOSING:
			case END:
				functionCollection.addAll(new HashSet<>(Arrays.asList(
						MeetingAuth.VIEW
					)));
				break;
			case CANCEL:
			case DROP:
				functionCollection.addAll(new HashSet<>(Arrays.asList(
					MeetingAuth.VIEW
				)));
				break;
			default:
				break;
		}
		return functionCollection;
	}
	
	/**
	 * 전자 회의의 보안설정에 따른 권한 설정
	 * @param assignVO
	 * @param bannedCollection
	 * @return
	 */
	private Set<AuthCode> setMeetingAuthorityForElec(EwpMeetingAssignVO assignVO) {
		Character secretYN = assignVO.getSecretYN();
		MeetingStatus meetingStatus = assignVO.getMeetingStatus();
		Set<AuthCode> functionCollection = new HashSet<>();
		functionCollection.add(MeetingAuth.READ);
		if(secretYN == 'Y') {
			switch(meetingStatus) {
				case NEW:
				case UNAPPROVAL:
					functionCollection.addAll(new HashSet<>(Arrays.asList(
						MeetingAuth.VIEW,
						MeetingAuth.UPDATE,
						MeetingAuth.CANCEL,
						MeetingAuth.UPLOAD,
						MeetingAuth.INVITE
					)));
					break;
				case APPROVED:
					functionCollection.addAll(new HashSet<>(Arrays.asList(
						MeetingAuth.VIEW,
						MeetingAuth.UPDATE,
						MeetingAuth.CANCEL,
						MeetingAuth.UPLOAD,
						MeetingAuth.INVITE
					)));
					break;
				case OPENING:
					functionCollection.addAll(new HashSet<>(Arrays.asList(
						MeetingAuth.VIEW,
						MeetingAuth.UPDATE,
						MeetingAuth.CANCEL,
						MeetingAuth.UPLOAD,
						MeetingAuth.INVITE,
						MeetingAuth.ATTEND
					)));
					break;
				case START:
					functionCollection.addAll(new HashSet<>(Arrays.asList(
						MeetingAuth.VIEW,
						MeetingAuth.UPDATE,
						MeetingAuth.UPLOAD,
						MeetingAuth.INVITE,
						MeetingAuth.ATTEND,
						MeetingAuth.EXTEND,
						MeetingAuth.FINISH
					)));
					break;
				case FINISH:
					functionCollection.addAll(new HashSet<>(Arrays.asList(
						MeetingAuth.EXTEND
					)));
					break;
				case CLOSING:
				case END:
					break;
				case CANCEL:
				case DROP:
					functionCollection.addAll(new HashSet<>(Arrays.asList(
						MeetingAuth.VIEW
					)));
					break;
				default:
					break;
			}
		}else {
			switch(meetingStatus) {
				case NEW:
				case UNAPPROVAL:
					functionCollection.addAll(new HashSet<>(Arrays.asList(
						MeetingAuth.VIEW,
						MeetingAuth.UPDATE,
						MeetingAuth.CANCEL,
						MeetingAuth.UPLOAD,
						MeetingAuth.INVITE
					)));
					break;
				case APPROVED:
					functionCollection.addAll(new HashSet<>(Arrays.asList(
						MeetingAuth.VIEW,
						MeetingAuth.UPDATE,
						MeetingAuth.CANCEL,
						MeetingAuth.UPLOAD,
						MeetingAuth.INVITE
					)));
					break;
				case OPENING:
					functionCollection.addAll(new HashSet<>(Arrays.asList(
						MeetingAuth.VIEW,
						MeetingAuth.UPDATE,
						MeetingAuth.CANCEL,
						MeetingAuth.UPLOAD,
						MeetingAuth.INVITE,
						MeetingAuth.ATTEND,
						MeetingAuth.CHECK,
						MeetingAuth.SIGN
					)));
					break;
				case START:
					functionCollection.addAll(new HashSet<>(Arrays.asList(
						MeetingAuth.VIEW,
						MeetingAuth.UPDATE,
						MeetingAuth.UPLOAD,
						MeetingAuth.INVITE,
						MeetingAuth.ATTEND,
						MeetingAuth.CHECK,
						MeetingAuth.SIGN,
						MeetingAuth.EXTEND,
						MeetingAuth.COPY,
						MeetingAuth.REPORT,
						MeetingAuth.PHOTO,
						MeetingAuth.VOICE,
						MeetingAuth.FINISH
					)));
					break;
				case FINISH:
					functionCollection.addAll(new HashSet<>(Arrays.asList(
						MeetingAuth.VIEW,
						MeetingAuth.EXTEND,
						MeetingAuth.COPY,
						MeetingAuth.REPORT
					)));
					break;
				case CLOSING:
					functionCollection.addAll(new HashSet<>(Arrays.asList(
						MeetingAuth.VIEW,
						MeetingAuth.REPORT
					)));
					break;
				case END:
					functionCollection.addAll(new HashSet<>(Arrays.asList(
						MeetingAuth.VIEW,
						MeetingAuth.REPORT
					)));
					break;
				case CANCEL:
				case DROP:
					functionCollection.addAll(new HashSet<>(Arrays.asList(
						MeetingAuth.VIEW
					)));
					break;
				default:
					break;
			}
		}
		return functionCollection;
	}
	
	/**
	 * 회의 진행상황별로 회의 참석자가 가질 권한 설정. 동일 참석자 유형에 속한다면 동일한 권한을 가지며 회의 예약 후 종료 전까지는 각 참석 유형별 권한 고정.
	 * @param meeting: 권한을 설정할 회의.
	 */
	private void setAttendeeAuthority(EwpMeetingAssignVO assignVO) {
		Integer meetingKey = assignVO.getMeetingKey();
		Character elecYN = assignVO.getElecYN();
		if(elecYN == 'N') {
			setAttendeeAuthorityForRental(assignVO);
		}else {
			setAttendeeAuthorityForElec(assignVO);
		}
	}
	
	private void setAttendeeAuthorityForRental(EwpMeetingAssignVO assignVO) {
		Integer meetingKey = assignVO.getMeetingKey();
		MeetingStatus meetingStatus = assignVO.getMeetingStatus();
		Set<AuthCode> functionCollection = new HashSet<>();
		functionCollection.add(MeetingAuth.READ);
		switch(meetingStatus) {
			case APPROVED:
				break;
			case OPENING:
				meetingAuthorityMng.changePermissionForUser(assignVO.getWriterKey(), meetingKey, new HashSet<>(Arrays.asList(
						MeetingAuth.READ,
						MeetingAuth.VIEW,
						MeetingAuth.CANCEL,
						MeetingAuth.UPDATE
					)));
				break;
			case START:
				meetingAuthorityMng.changePermissionForUser(assignVO.getWriterKey(), meetingKey, new HashSet<>(Arrays.asList(
						MeetingAuth.READ,
						MeetingAuth.VIEW,
						MeetingAuth.CANCEL,
						MeetingAuth.UPDATE,
						MeetingAuth.FINISH
					)));
				break;
			case FINISH:
				meetingAuthorityMng.changePermissionForUser(assignVO.getWriterKey(), meetingKey, new HashSet<>(Arrays.asList(
					MeetingAuth.READ,
					MeetingAuth.VIEW
				)));
				break;
			case CLOSING:
				break;
			case END:
				break;
			case CANCEL:
			case DROP:
				meetingAuthorityMng.changePermissionForAttendee(AttendRole.HOST, meetingKey, new HashSet<>(Arrays.asList(
						MeetingAuth.READ,
						MeetingAuth.VIEW
				)));
				meetingAuthorityMng.changePermissionForAttendee(AttendRole.ASSISTANT, meetingKey, new HashSet<>(Arrays.asList(
						MeetingAuth.READ,
						MeetingAuth.VIEW
				)));
				meetingAuthorityMng.changePermissionForAttendee(AttendRole.ATTENDEE, meetingKey, new HashSet<>(Arrays.asList(
						MeetingAuth.READ,
						MeetingAuth.VIEW
				)));
				meetingAuthorityMng.changePermissionForAttendee(AttendRole.OBSERVER, meetingKey, new HashSet<>(Arrays.asList(
						MeetingAuth.READ,
						MeetingAuth.VIEW
				)));
			default:
				break;
		}
	}
	
	private void setAttendeeAuthorityForElec(EwpMeetingAssignVO assignVO) {
		Integer meetingKey = assignVO.getMeetingKey();
		Character secretYN = assignVO.getSecretYN();
		MeetingStatus meetingStatus = assignVO.getMeetingStatus();
		Set<AuthCode> functionCollection = new HashSet<>();
		functionCollection.add(MeetingAuth.READ);
		switch(meetingStatus) {
			case APPROVED:
				meetingAuthorityMng.changePermissionForAttendee(AttendRole.HOST, meetingKey, new HashSet<>(Arrays.asList(
					MeetingAuth.READ,
					MeetingAuth.VIEW,
					MeetingAuth.UPDATE,
					MeetingAuth.CANCEL,
					MeetingAuth.UPLOAD,
					MeetingAuth.INVITE,
					MeetingAuth.ATTEND,
					MeetingAuth.CHECK,
					MeetingAuth.SIGN,
					MeetingAuth.EXTEND,
					MeetingAuth.COPY,
					MeetingAuth.REPORT,
					MeetingAuth.PHOTO,
					MeetingAuth.VOICE,
					MeetingAuth.FINISH
				)));
				meetingAuthorityMng.changePermissionForAttendee(AttendRole.ASSISTANT, meetingKey, new HashSet<>(Arrays.asList(
					MeetingAuth.READ,
					MeetingAuth.VIEW,
					MeetingAuth.UPDATE,
					MeetingAuth.CANCEL,
					MeetingAuth.UPLOAD,
					MeetingAuth.INVITE,
					MeetingAuth.ATTEND,
					MeetingAuth.CHECK,
					MeetingAuth.SIGN,
					MeetingAuth.EXTEND,
					MeetingAuth.COPY,
					MeetingAuth.REPORT,
					MeetingAuth.PHOTO,
					MeetingAuth.VOICE,
					MeetingAuth.FINISH
				)));
				meetingAuthorityMng.changePermissionForAttendee(AttendRole.ATTENDEE, meetingKey, new HashSet<>(Arrays.asList(
					MeetingAuth.READ,
					MeetingAuth.VIEW,
					MeetingAuth.UPLOAD,
					MeetingAuth.ATTEND,
					MeetingAuth.SIGN,
					MeetingAuth.COPY
				)));
				meetingAuthorityMng.changePermissionForAttendee(AttendRole.OBSERVER, meetingKey, new HashSet<>(Arrays.asList(
					MeetingAuth.READ,
					MeetingAuth.VIEW,
					MeetingAuth.UPLOAD,
					MeetingAuth.ATTEND,
					MeetingAuth.SIGN,
					MeetingAuth.COPY
				)));
				break;
			case OPENING:
				meetingAuthorityMng.changePermissionForAttendee(AttendRole.HOST, meetingKey, new HashSet<>(Arrays.asList(
					MeetingAuth.READ,
					MeetingAuth.VIEW,
					MeetingAuth.UPDATE,
					MeetingAuth.CANCEL,
					MeetingAuth.UPLOAD,
					MeetingAuth.INVITE,
					MeetingAuth.ATTEND,
					MeetingAuth.CHECK,
					MeetingAuth.SIGN,
					MeetingAuth.EXTEND,
					MeetingAuth.COPY,
					MeetingAuth.REPORT,
					MeetingAuth.PHOTO,
					MeetingAuth.VOICE,
					MeetingAuth.FINISH
				)));
				meetingAuthorityMng.changePermissionForAttendee(AttendRole.ASSISTANT, meetingKey, new HashSet<>(Arrays.asList(
					MeetingAuth.READ,
					MeetingAuth.VIEW,
					MeetingAuth.UPDATE,
					MeetingAuth.CANCEL,
					MeetingAuth.UPLOAD,
					MeetingAuth.INVITE,
					MeetingAuth.ATTEND,
					MeetingAuth.CHECK,
					MeetingAuth.SIGN,
					MeetingAuth.EXTEND,
					MeetingAuth.COPY,
					MeetingAuth.REPORT,
					MeetingAuth.PHOTO,
					MeetingAuth.VOICE,
					MeetingAuth.FINISH
				)));
				meetingAuthorityMng.changePermissionForAttendee(AttendRole.ATTENDEE, meetingKey, new HashSet<>(Arrays.asList(
					MeetingAuth.READ,
					MeetingAuth.VIEW,
					MeetingAuth.UPLOAD,
					MeetingAuth.ATTEND,
					MeetingAuth.SIGN,
					MeetingAuth.COPY
				)));
				meetingAuthorityMng.changePermissionForAttendee(AttendRole.OBSERVER, meetingKey, new HashSet<>(Arrays.asList(
					MeetingAuth.READ,
					MeetingAuth.VIEW,
					MeetingAuth.UPLOAD,
					MeetingAuth.ATTEND,
					MeetingAuth.SIGN,
					MeetingAuth.COPY
				)));
				meetingAuthorityMng.changePermissionForUser(assignVO.getWriterKey(), meetingKey, MeetingAuth.READ);
				break;
			case START:
				meetingAuthorityMng.changePermissionForAttendee(AttendRole.HOST, meetingKey, new HashSet<>(Arrays.asList(
					MeetingAuth.READ,
					MeetingAuth.VIEW,
					MeetingAuth.UPDATE,
					MeetingAuth.CANCEL,
					MeetingAuth.UPLOAD,
					MeetingAuth.INVITE,
					MeetingAuth.ATTEND,
					MeetingAuth.CHECK,
					MeetingAuth.SIGN,
					MeetingAuth.EXTEND,
					MeetingAuth.COPY,
					MeetingAuth.REPORT,
					MeetingAuth.PHOTO,
					MeetingAuth.VOICE,
					MeetingAuth.FINISH
				)));
				meetingAuthorityMng.changePermissionForAttendee(AttendRole.ASSISTANT, meetingKey, new HashSet<>(Arrays.asList(
					MeetingAuth.READ,
					MeetingAuth.VIEW,
					MeetingAuth.UPDATE,
					MeetingAuth.CANCEL,
					MeetingAuth.UPLOAD,
					MeetingAuth.INVITE,
					MeetingAuth.ATTEND,
					MeetingAuth.CHECK,
					MeetingAuth.SIGN,
					MeetingAuth.EXTEND,
					MeetingAuth.COPY,
					MeetingAuth.REPORT,
					MeetingAuth.PHOTO,
					MeetingAuth.VOICE,
					MeetingAuth.FINISH
				)));
				meetingAuthorityMng.changePermissionForAttendee(AttendRole.ATTENDEE, meetingKey, new HashSet<>(Arrays.asList(
					MeetingAuth.READ,
					MeetingAuth.VIEW,
					MeetingAuth.UPLOAD,
					MeetingAuth.ATTEND,
					MeetingAuth.SIGN,
					MeetingAuth.COPY
				)));
				meetingAuthorityMng.changePermissionForAttendee(AttendRole.OBSERVER, meetingKey, new HashSet<>(Arrays.asList(
					MeetingAuth.READ,
					MeetingAuth.VIEW,
					MeetingAuth.UPLOAD,
					MeetingAuth.ATTEND,
					MeetingAuth.SIGN,
					MeetingAuth.COPY
				)));
				meetingAuthorityMng.changePermissionForUser(assignVO.getWriterKey(), meetingKey, MeetingAuth.READ);
				break;
			case FINISH:
				break;
			case CLOSING:
				meetingAuthorityMng.removePermissionForAttendee(AttendRole.GUEST, meetingKey);
				break;
			case END:
				if(secretYN == 'Y') {
					meetingAuthorityMng.changePermissionForAttendee(AttendRole.HOST, meetingKey, MeetingAuth.READ);
					meetingAuthorityMng.changePermissionForAttendee(AttendRole.ASSISTANT, meetingKey, MeetingAuth.READ);
					meetingAuthorityMng.changePermissionForAttendee(AttendRole.ATTENDEE, meetingKey, MeetingAuth.READ);
					meetingAuthorityMng.changePermissionForAttendee(AttendRole.OBSERVER, meetingKey, MeetingAuth.READ);
				}else {
					setAttendeeAuthorityBySecu(assignVO);
				}
				break;
			case CANCEL:
			case DROP:
				meetingAuthorityMng.changePermissionForAttendee(AttendRole.HOST, meetingKey, new HashSet<>(Arrays.asList(
						MeetingAuth.READ,
						MeetingAuth.VIEW
				)));
				meetingAuthorityMng.changePermissionForAttendee(AttendRole.ASSISTANT, meetingKey, new HashSet<>(Arrays.asList(
						MeetingAuth.READ,
						MeetingAuth.VIEW
				)));
				meetingAuthorityMng.changePermissionForAttendee(AttendRole.ATTENDEE, meetingKey, new HashSet<>(Arrays.asList(
						MeetingAuth.READ,
						MeetingAuth.VIEW
				)));
				meetingAuthorityMng.changePermissionForAttendee(AttendRole.OBSERVER, meetingKey, new HashSet<>(Arrays.asList(
						MeetingAuth.READ,
						MeetingAuth.VIEW
				)));
			default:
				break;
		}
	}
	
	/**
	 * 회의 종료 후 보안설정에 맞춰 참석자별 권한 설정.
	 * @param meeting: 권한을 설정할 회의.
	 */
	private void setAttendeeAuthorityBySecu(EwpMeetingAssignVO assignVO) {
		Integer meetingKey = assignVO.getMeetingKey();
		List<EwpMeetingAttendeeVO> attendeeList = attServ.getMeetingAttendeeListByMeeting(meetingKey);
		Set<String> deptSet = new HashSet<>();
		
		Integer hostSecuLvl = assignVO.getHostSecuLvl();
		Integer attendeeSecuLvl = assignVO.getAttendeeSecuLvl();
		Integer observerSecuLvl = assignVO.getObserverSecuLvl();
		if(hostSecuLvl == 1) {
			meetingAuthorityMng.changePermissionForAttendee(AttendRole.HOST, meetingKey, new HashSet<>(Arrays.asList(
				MeetingAuth.READ
			)));
			meetingAuthorityMng.changePermissionForAttendee(AttendRole.ASSISTANT, meetingKey, new HashSet<>(Arrays.asList(
				MeetingAuth.READ
			)));
		}else if(hostSecuLvl == 3) {
			EwpMeetingAttendeeVO host = attendeeList.stream().filter(a -> a.getAttendRole() == AttendRole.HOST).findFirst().orElseGet(null);
			deptSet.add(host.getUser().getDeptId());
			log.info("회의: {}, 회의 진행자 : {}, 부서: {}", meetingKey, host.toString(), host.getUser().getDeptId());
			List<EwpMeetingAttendeeVO> assistantList = attendeeList.stream().filter(a -> a.getAssistantYN() == 'Y').collect(Collectors.toList());
			assistantList.forEach(assistant -> {
				deptSet.add(assistant.getUser().getDeptId());
				log.info("회의: {}, 보조 진행자 : {}, 부서: {}", meetingKey,  assistant.toString(), assistant.getUser().getDeptId());
			});
		}
		if(attendeeSecuLvl == 1) {
			meetingAuthorityMng.changePermissionForAttendee(AttendRole.ATTENDEE, meetingKey, new HashSet<>(Arrays.asList(
				MeetingAuth.READ
			)));
		}else if(attendeeSecuLvl == 3) {
			List<EwpMeetingAttendeeVO> reqList = attendeeList.stream().filter(a -> a.getAttendRole() == AttendRole.ATTENDEE).collect(Collectors.toList());
			log.info("회의: {}, 참석자 목록 : {}", meetingKey,  reqList.toString());
			deptSet.addAll(reqList.stream().map(a -> a.getUser().getDeptId()).collect(Collectors.toSet()));
		}
		if(observerSecuLvl == 1) {
			meetingAuthorityMng.changePermissionForAttendee(AttendRole.OBSERVER, meetingKey, new HashSet<>(Arrays.asList(
				MeetingAuth.READ
			)));
		}else if(observerSecuLvl == 3) {
			List<EwpMeetingAttendeeVO> reqList = attendeeList.stream().filter(a -> a.getAttendRole() == AttendRole.OBSERVER).collect(Collectors.toList());
			log.info("회의: {}, 참석자 목록 : {}", meetingKey,  reqList.toString());
			deptSet.addAll(reqList.stream().map(a -> a.getUser().getDeptId()).collect(Collectors.toSet()));
		}
		deptSet.stream().forEach(d -> meetingAuthorityMng.providePermissionForDept(d, meetingKey, new HashSet<>(Arrays.asList(
				MeetingAuth.READ,
				MeetingAuth.VIEW
			))));
	}
}
