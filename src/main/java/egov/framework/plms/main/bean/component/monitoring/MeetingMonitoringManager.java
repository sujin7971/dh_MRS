package egov.framework.plms.main.bean.component.monitoring;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import egov.framework.plms.main.bean.component.session.SessionManager;
import egov.framework.plms.main.bean.mvc.entity.organization.abst.UserInfoEntity;
import egov.framework.plms.main.core.model.login.AuthenticationDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 회의 진행상황을 관리할 객체. 
 * 
 * @see {@link MeetingProgressVO}
 * @see {@link LimeMeetingMonitoringScheduler}
 * @author mckim
 * @since 2.0.0
 * @version 3.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MeetingMonitoringManager {
	private final SessionManager sessionMng;
	private Map<Integer, MeetingProgressVO> watchdog;
	
	@PostConstruct
	private void init() {
		watchdog = new HashMap<>();
	}
	
	/**
	 * 모니터링 시작
	 * @param meetingKey 모니터링을 시작할 회의 키
	 */
	public MeetingProgressVO startMonitoring(Integer meetingKey) {
		if(isMonitoring(meetingKey)) {
			return watchdog.get(meetingKey);
		}else {
			MeetingProgressVO progress = MeetingProgressVO.builder().meetingKey(meetingKey).entryMap(new HashMap<String, String>()).build();
			return watchdog.put(meetingKey, progress);
		}
	}
	
	/**
	 * 해당 회의의 모니터링 종료
	 * @param meetingKey 모니터링을 종료할 회의 키
	 * @return 모니터링 중이던 회의인 경우 지금까지의 진행상황을 기록한 <tt>{@link MeetingProgressVO MeetingProgressVO}</tt> 객체, 모니터링 중이 아닌 경우 <tt>null</tt>
	 */
	public MeetingProgressVO endMonitoring(Integer meetingKey) {
		return watchdog.remove(meetingKey);
	}
	
	/**
	 * 해당 회의의 진행상황 요청
	 * @param meetingKey 요청할 회의 키
	 * @return 진행상황을 담은 <tt>{@link MeetingProgressVO MeetingProgressVO}</tt> 객체, 감시중인 회의 키가 아닌 경우 <tt>null</tt>
	 */
	public MeetingProgressVO getProgress(Integer meetingKey) {
		MeetingProgressVO progressVO = watchdog.get(meetingKey);
		if(progressVO == null) {
			progressVO = startMonitoring(meetingKey);
		}
		return progressVO;
	}
	
	/**
	 * 해당 회의가 현재 모니터링 중인지 확인
	 * @param meetingKey 모니터링 여부를 확인할 회의 키
	 * @return 모니터링 중인 경우 <tt>true</tt>, 아닌 경우 <tt>false</tt>
	 */
	public boolean isMonitoring(Integer meetingKey) {
		return watchdog.containsKey(meetingKey);
	}
	
	/**
	 * 현재 모니터링중인 회의 키 집합
	 * @return 모니터링중인 회의 키를 담은 <tt>set</tt>
	 */
	public Set<Integer> getMonitoringKeySet(){
		return watchdog.keySet();
	}
	
	/**
	 * 해당 회의의 참석중인 세션 추가
	 * @param meetingKey 세션을 추가할 회의 키
	 * @param sessionId 회의에 참석한 세션 ID
	 */
	public boolean addEntry(Integer meetingKey, String sessionId) {
		log.info("addEntry- meetingKey: {}, sessionId: {}", meetingKey, sessionId);
		MeetingProgressVO progress = watchdog.get(meetingKey);
		if(progress == null) {
			log.error("회의{}에 세션({}) addEntry 실패- 해당 회의에 대한 모니터링이 진행중이 아닙니다.", meetingKey, sessionId);
			return false;
		}
		AuthenticationDetails userDetails = sessionMng.getSessionOwner(sessionId);
		if(userDetails != null) {
			progress.addEntry(sessionId, userDetails.getUserId());
			log.info("회의{}에 세션({}) addEntry 성공 : {}", meetingKey, sessionId, progress.getEntryMap());
			return true;
		}else {
			log.error("회의{}에 세션({}) addEntry 실패- 세션의 인증된 사용자 정보를 찾을 수 없습니다.", meetingKey, sessionId);
			return false;
		}
	}
	
	public boolean addEntry(Authentication authentication, Integer meetingKey, String sessionId) {
		log.info("addEntry- meetingKey: {}, sessionId: {}", meetingKey, sessionId);
		MeetingProgressVO progress = watchdog.get(meetingKey);
		if(progress == null) {
			log.error("회의{}에 세션({}) addEntry 실패- 해당 회의에 대한 모니터링이 진행중이 아닙니다.", meetingKey, sessionId);
			return false;
		}
		AuthenticationDetails userDetails = sessionMng.getSessionOwner(sessionId);
		if(userDetails != null) {
			progress.addEntry(sessionId, userDetails.getUserId());
			log.info("회의{}에 세션({}) addEntry 성공", meetingKey, sessionId);
			return true;
		}else {
			log.info("회의{}에 세션({}) addEntry 실패- 세션의 인증된 사용자 정보를 찾을 수 없습니다. 세션 소유자 정보를 갱신합니다.", meetingKey, sessionId);
			userDetails = (AuthenticationDetails) authentication.getDetails();
			sessionMng.setSessionOwner(sessionId, userDetails);
			progress.addEntry(sessionId, userDetails.getUserId());
			return true;
		}
	}
	
	/**
	 * 해당 회의의 참석중인 세션에서 해당 요청한 세션 제거
	 * @param meetingKey 세션을 제거할 회의 키
	 * @param sessionId 회의에서 나간 세션 ID
	 * @return 모니터링 중인 아닌 회의 인 경우 <tt>null</tt>, 세션을 제거 한 경우 해당 회의 키
	 */
	public Integer removeEntry(Integer meetingKey, String sessionId) {
		log.info("removeEntry- meetingKey: {}, sessionId: {}", meetingKey, sessionId);
		MeetingProgressVO progress = watchdog.get(meetingKey);
		if(progress == null) {
			return null;
		}
		progress.removeEntry(sessionId);
		log.info("{} entry set: {}", progress.getMeetingKey(), progress.getEntryUserIdSet());
		return meetingKey;
	}
	
	/**
	 * 세션 ID가 참석중인 회의 키를 찾아 참석 목록에서 제거
	 * @param sessionId 회의에서 나간 세션 ID
	 * @return 해당 세션이 참석중인 모니터링 대상 회의 가 없는 경우 <tt>null</tt>, 세션을 제거 한 경우 해당 회의 키
	 */
	public Integer removeEntry(String sessionId) {
		log.info("removeEntry- sessionId: {}", sessionId);
		try {
			MeetingProgressVO progress = getEntriedMeetingProgress(sessionId);
			if(progress == null) {
				return null;
			}
			progress.removeEntry(sessionId);
			log.info("{} entry set: {}", progress.getMeetingKey(), progress.getEntryUserIdSet());
			return progress.getMeetingKey();
		}catch(NullPointerException e) {
			return null;
		}
	}
	
	public MeetingProgressVO getEntriedMeetingProgress(String sessionId) {
		MeetingProgressVO progress = watchdog.values().stream().filter(p -> p.getEntrySize()!= 0).filter(p -> p.hasEntry(sessionId)).findFirst().orElse(null);
		return progress;
	}
	
	public Integer getEntriedMeetingId(String sessionId) {
		MeetingProgressVO progress = getEntriedMeetingProgress(sessionId);
		return Optional.ofNullable(progress).map(vo -> vo.getMeetingKey()).orElse(null);
	}
	
	/**
	 * 회의에 참석중인 세션 ID의 집합 조회
	 * @param meetingKey 조회할 회의 키
	 * @return 세션 ID 집합, 모니터링 중인 아닌 경우 <tt>null</tt>
	 */
	public Set<String> getEntryUserIdSet(Integer meetingKey) {
		MeetingProgressVO progress = watchdog.get(meetingKey);
		if(progress == null) {
			return null;
		}
		return progress.getEntryUserIdSet();
	}
	
	/**
	 * 화면 제어 시작
	 * @param meetingKey 대상 회의 고유키
	 * @param userKey 제어 담당자 고유키
	 * @param fileKey 현재 파일 고유키
	 * @param pageno 현재 페이지 번호
	 */
	public void startStream(Integer meetingKey, String userKey, String sesionId, Integer fileKey, Integer pageno) {
		MeetingProgressVO progress = watchdog.get(meetingKey);
		if(progress == null) {
			return;
		}
		progress.setStreamer(userKey);
		progress.setStreamerSessionId(sesionId);
		progress.setFileKey(fileKey);
		progress.setPageno(pageno);
	}
	
	/**
	 * 화면 제어 종료
	 * @param meetingKey 대상 회의 고유키
	 * @param userKey 제어 종료 요청자
	 */
	public boolean endStream(Integer meetingKey, String sessionId) {
		MeetingProgressVO progress = watchdog.get(meetingKey);
		if(progress == null) {
			return false;
		}
		if(progress.getStreamer() == null || !progress.getStreamerSessionId().equals(sessionId)) {
			return false;
		}
		progress.setStreamer(null);
		progress.setFileKey(null);
		progress.setPageno(null);
		return true;
	}
	
	/**
	 * 화면 제어 담당자 고유키 조회
	 * @param meetingKey 대상 회의 고유키
	 * @return 제어 담당자 사용자 고유키
	 */
	public String getStreamer(Integer meetingKey) {
		MeetingProgressVO progress = watchdog.get(meetingKey);
		if(progress == null) {
			return null;
		}
		return progress.getStreamer();
	}
	
	/**
	 * 모니터링중인 회의에 등록된 참석 세션 ID중 만료된 세션을 찾아 실제 참석자와 맞게 동기화 진행
	 * @param meetingKey 진행 상황을 갱신할 회의 키
	 * @return 동기화 완료된 참석 세션 ID 집합
	 */
	public Set<String> updateEntrySet(Integer meetingKey) {
		Set<String> sessionSet = getEntryUserIdSet(meetingKey);
		if(sessionSet == null) {
			return null;
		}
		Set<String> aliveSessionSet = sessionSet.stream().filter(id -> sessionMng.isSessionAlive(id)).collect(Collectors.toSet());
		//log.info("updateEntrySet - sessionSet: {}, aliveSessionSet: {}", sessionSet, aliveSessionSet);
		if(aliveSessionSet.size() != 0) {
			Set<String> expiredSessionSet = sessionSet.stream().filter(id -> !aliveSessionSet.contains(id)).collect(Collectors.toSet());
			for(String sessionId: expiredSessionSet) {
				removeEntry(meetingKey, sessionId);
				log.info("updateEntrySet removeEntry - meetingKey: {}, sessionId: {}", meetingKey, sessionId);
			}
		}else {
			sessionSet.clear();
		}
		return getEntryUserIdSet(meetingKey);
	}
	
	/**
	 * 해당 회의에 실제 참석자가 있는지 확인
	 * @param meetingKey 참석자 수를 확인할 회의 키
	 * @return 참석자가 아무도 없는 경우 <tt>true</tt>, 아닌 경우 <tt>false</tt>
	 */
	public boolean isMeetingBlackout(Integer meetingKey) {
		Set<String> sessionSet = getEntryUserIdSet(meetingKey);
		if(sessionSet == null || sessionSet.size() == 0) {
			return true;
		}else {
			return false;
		}
	}
}
