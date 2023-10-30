package egov.framework.plms.sub.lime.bean.component.scheduler;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import egov.framework.plms.main.bean.component.monitoring.MeetingMonitoringManager;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingAssignVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingAttendeeVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingInfoVO;
import egov.framework.plms.main.bean.mvc.service.meeting.MeetingApprovalService;
import egov.framework.plms.main.core.model.auth.ResourceAuthorityCollection;
import egov.framework.plms.main.core.model.enums.auth.MeetingAuth;
import egov.framework.plms.main.core.model.enums.meeting.ApprovalStatus;
import egov.framework.plms.main.core.model.enums.meeting.MeetingStatus;
import egov.framework.plms.sub.lime.bean.component.auth.LimeMeetingAuthorityCertificater;
import egov.framework.plms.sub.lime.bean.component.auth.LimeResourceAuthorityProvider;
import egov.framework.plms.sub.lime.bean.mvc.service.meeting.LimeMeetingAssignService;
import egov.framework.plms.sub.lime.bean.mvc.service.meeting.LimeMeetingAttendeeService;
import egov.framework.plms.sub.lime.bean.mvc.service.meeting.LimeMeetingFileInfoService;
import egov.framework.plms.sub.lime.bean.mvc.service.meeting.LimeMeetingInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 주기적으로 회의 진행상황에 따라 권한 갱신을 요청 및 전자회의의 회의진행상황 정보를 갱신
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 * 
 * @see {@link MeetingMonitoringManager}
 * @see {@link LimeMeetingAuthorityCertificater}
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "config.scheduledTasks.meetingMonitoringScheduler", name="enabled", havingValue = "true", matchIfMissing = false)
@Profile("lime")
public class LimeMeetingMonitoringScheduler {
	private final LimeResourceAuthorityProvider authorityProvider;
	private final LimeMeetingAuthorityCertificater authCertMng;
	private final MeetingMonitoringManager monitoringMng;
	
	private final LimeMeetingAssignService assignServ;
	private final LimeMeetingInfoService mtServ;
	private final LimeMeetingFileInfoService fileServ;
	private final MeetingApprovalService appServ;
	private final LimeMeetingAttendeeService attServ;
	private LinkedHashMap<Integer, MeetingAssignVO> certQueue;
	
	@PostConstruct
	private void init() {
		certQueue = new LinkedHashMap<>();
	}
	/**
	 * DB로부터 진행상황 갱신이 필요한 회의 목록을 조회하여 대기줄에 추가하고 해당 대기열에 추가된 회의에 대한 인증 및 정보 갱신을 요청한다
	 * @throws Exception
	 */
	@Scheduled(cron="${config.scheduledTasks.meetingMonitoringScheduler.cron}")
	private void scheduler() throws Exception {
		periodicProgressMonitoring();
		periodicEntryMonitoring();
	}
	
	private void periodicProgressMonitoring() {
		if(!certQueue.isEmpty()) {
			return;
		}
		List<MeetingAssignVO> meetingList = assignServ.getMeetingAssignListForMonitoring();
		for(MeetingAssignVO assignVO: meetingList) {
			if(!certQueue.containsKey(assignVO.getMeetingId())) {
				certQueue.put(assignVO.getMeetingId(), assignVO);
			}
		}
		startMonitoring();
	}
	
	/**
	 * 회의 모니터링 진행<br>
	 * -회의의 stickyBit가 0인 경우 @{link {@link LimeMeetingAuthorityCertificater#certificate(MeetingAssignVO)}를 통해 권한 발급 처리.<br>
	 * -회의 종료 시간 전인 경우 현재 시간에 따라 회의 진행상황 변경 처리 및 해당 진행상황별로 처리해야할 로직 수행
	 */
	private void startMonitoring() {
		for(MeetingAssignVO assignVO: certQueue.values()) {
			Integer stickyBit = assignVO.getStickyBit();
			if(stickyBit == 0) {
				authCertMng.certificate(assignVO);
			}
			processMeetingProgress(assignVO);
		}
		certQueue.clear();
	}
	
	/**
	 * 시간에 맞춰 회의 진행상황 갱신
	 * @param assignVO 회의사용신청 객체
	 */
	private void processMeetingProgress(MeetingAssignVO assignVO) {
		Integer meetingId = assignVO.getMeetingId();
		MeetingStatus meetingStatus = assignVO.getMeetingStatus();
		
		LocalDateTime nowDT = LocalDateTime.now();
		LocalDateTime beginDT = assignVO.getBeginDateTime();
		LocalDateTime finishDT = assignVO.getFinishDateTime();
		LocalDateTime expDT = assignVO.getExpDateTime();
		switch(meetingStatus) {
			case NEW:
				log.info("회의(번호: {}, 진행: {}) 진행상황 갱신", assignVO.getMeetingId(), assignVO.getMeetingStatus());
				appServ.insertApprovalOne("SYSTEM", assignVO.getScheduleId(), ApprovalStatus.REQUEST);
				break;
			case UNAPPROVAL:
				if(nowDT.isAfter(finishDT)) {
					log.info("회의(번호: {}, 진행: {}) 진행상황 갱신", assignVO.getMeetingId(), assignVO.getMeetingStatus());
					appServ.insertApprovalOne("SYSTEM", assignVO.getScheduleId(), ApprovalStatus.CANCELED, "자동 취소");
				}
				break;
			case APPROVED:
				if(ChronoUnit.MINUTES.between(nowDT, beginDT) <= 60) {
					log.info("회의(번호: {}, 진행: {}) 진행상황 갱신", assignVO.getMeetingId(), assignVO.getMeetingStatus());
					mtServ.putMeetingInfo(MeetingInfoVO.builder().meetingId(meetingId).meetingStatus(MeetingStatus.OPENING).build());
				}
				break;
			case OPENING:
				if(!monitoringMng.isMonitoring(meetingId)) {
					monitoringMng.startMonitoring(meetingId);
				}
				if(nowDT.isAfter(beginDT)) {
					log.info("회의(번호: {}, 진행: {}) 진행상황 갱신", assignVO.getMeetingId(), assignVO.getMeetingStatus());
					mtServ.putMeetingInfo(MeetingInfoVO.builder().meetingId(meetingId).meetingStatus(MeetingStatus.START).build());
				}
				break;
			case START:
				if(!monitoringMng.isMonitoring(meetingId)) {
					monitoringMng.startMonitoring(meetingId);
				}
				if(finishDT != null && nowDT.isAfter(finishDT)) {
					log.info("회의(번호: {}, 진행: {}) 진행상황 갱신", assignVO.getMeetingId(), assignVO.getMeetingStatus());
					mtServ.putMeetingInfo(MeetingInfoVO.builder().meetingId(meetingId).meetingStatus(MeetingStatus.FINISH).build());
				}
				break;
			case FINISH:
				if(nowDT.isBefore(finishDT)) {
					log.info("회의(번호: {}, 진행: {}) 진행상황 갱신", assignVO.getMeetingId(), assignVO.getMeetingStatus());
					mtServ.putMeetingInfo(MeetingInfoVO.builder().meetingId(meetingId).meetingStatus(MeetingStatus.START).build());
				}else if(expDT == null || nowDT.isAfter(expDT) || monitoringMng.isMeetingBlackout(meetingId)) {
					log.info("회의(번호: {}, 진행: {}) 진행상황 갱신", assignVO.getMeetingId(), assignVO.getMeetingStatus());
					createMaterialCopyEdition(meetingId);//판서본 생성 권한을 CLOSING 단계부터 회수하기 때문에 FINISH단계에서 CLOSING단계 변경 전 요청
					mtServ.putMeetingInfo(MeetingInfoVO.builder().meetingId(meetingId).meetingStatus(MeetingStatus.CLOSING).build());
				}
				break;
			case CLOSING:
				log.info("회의(번호: {}, 진행: {}) 진행상황 갱신", assignVO.getMeetingId(), assignVO.getMeetingStatus());
				mtServ.putMeetingInfo(MeetingInfoVO.builder().meetingId(meetingId).meetingStatus(MeetingStatus.END).build());
				monitoringMng.endMonitoring(meetingId);
				break;
			default:
				break;
		}
	}
	
	/**
	 * 회의종료 후 판서본 생성 권한을 가진 모든 참석자의 판서본 생성을 {@link LimeMeetingFileInfoService}에 요청
	 * @param meetingId
	 */
	
	private void createMaterialCopyEdition(Integer meetingId) {
		List<MeetingAttendeeVO> attendeeList = attServ.getMeetingAttendeeListByMeeting(meetingId);
		for(MeetingAttendeeVO attendee: attendeeList) {
			String userId = attendee.getUserId();
			ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingPrivateAuthorityCollection(userId, meetingId);
			if(authorityCollection.hasAuthority(MeetingAuth.COPY)) {
				fileServ.generateCopyEditionFromNote(meetingId, userId);
				fileServ.generateMemo(meetingId, userId);
			}
		}
	}
	
	/**
	 * 진행중인 회의에 대한 참석자 현황 및 화면 제어등 회의 관련 기능들의 정보 갱신 처리
	 */
	private void periodicEntryMonitoring() {
		Set<Integer> keySet = new HashSet<>();
		keySet.addAll(monitoringMng.getMonitoringKeySet());
		for(Integer meetingId : keySet) {
			try {
				monitoringMng.updateEntrySet(meetingId);
			}catch (Exception e) {
				log.error(e.toString(), e);
			}
		}
	}
}
