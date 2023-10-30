package egov.framework.plms.main.bean.component.session;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

import egov.framework.plms.main.bean.mvc.entity.organization.abst.UserInfoEntity;
import egov.framework.plms.main.core.model.login.AuthenticationDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 접속한 사용자의 세션 키와 세션 객체 및 사용자 정보 객체를 보관하고 관리할 컴포넌트 클래스
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 * 
 * @see {@link UserInfoEntity}
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SessionManager {
	private final SessionRegistry sessionRegistry;
	private Map<String, AuthenticationDetails> sessionOwner;
	
	@PostConstruct
	private void init() {
		sessionOwner = new HashMap<>();
	}
	
	/**
	 * 현재 등록된 세션 개수 조회
	 * @return 세션 개수
	 */
	public Integer getAuthenticatedSessionCount() {
		syncSessionStatus();
		return sessionOwner.size();
	}
	
	public void syncSessionStatus() {
		log.info("갱신전 세션 명단: {}", sessionOwner.keySet());
		synchronized(sessionOwner) {
	        Iterator<Map.Entry<String, AuthenticationDetails>> iterator = sessionOwner.entrySet().iterator();
	        while (iterator.hasNext()) {
	            Map.Entry<String, AuthenticationDetails> entry = iterator.next();
	            String sessionId = entry.getKey();
	            if (!isSessionAlive(sessionId)) {
	                iterator.remove();
	            }
	        }
	    }
		log.info("갱신후 세션 명단: {}", sessionOwner.keySet());
	}
	
	/**
	 * 해당 세션 ID의 SessionInfomation을 {@link SessionRegistry}에서 제거
	 * @param sessionId 세션 고유키
	 */
	public void removeSessionInformation(String sessionId) {
		sessionRegistry.removeSessionInformation(sessionId);
		removeSessionOwner(sessionId);
	}
	
	/**
	 * 세션 소유 정보 등록
	 * @param sessionId 세션 고유키
	 * @param details 사용자 인증 정보 객체
	 */
	public void setSessionOwner(String sessionId, AuthenticationDetails details) {
		String principal = details.getUserId();
		//killSessionByPrincipal(principal);
		sessionOwner.put(sessionId, details);
		log.info("setSessionOwner- sessionId: {}, userId: {}, time: {}", sessionId, details.getUserId(), details.getLoginDateTime());
	}
	
	/**
	 * 세션 소유 정보 삭제
	 * @param sessionId
	 */
	public void removeSessionOwner(String sessionId) {
		log.info("removeSessionOwner- sessionId: {}", sessionId);
		sessionOwner.remove(sessionId);
	}
	
	/**
	 * 해당 세션 ID의 사용자 정보 조회
	 * @param sessionId 세션 고유키
	 * @return
	 */
	public AuthenticationDetails getSessionOwner(String sessionId) {
		try {
			return sessionOwner.get(sessionId);
		}catch(NullPointerException e) {
			return null;
		}
	}
	
	/**
	 * 세션에 로그인 한 사용자 리스트 조회
	 * @param sessionId 세션 고유키
	 * @return
	 */
	public List<AuthenticationDetails> getSessionOwners() {
		try {
			return new ArrayList<AuthenticationDetails>(sessionOwner.values());
		}catch(NullPointerException e) {
			return null;
		}
	}
	
	/**
	 * 특정 사용자가 등록한 세션정보 {@link SessionRegistry}에서 제거
	 * @param principal 사용자 식별키
	 * @see {@link Authentication#getPrincipal()}
	 */
	public void killSessionByPrincipal(String principal) {
		log.info("killSessionByPrincipal- principal: {}", principal);
		List<Object> principalList = sessionRegistry.getAllPrincipals();
		log.info("killSessionByPrincipal- now principal list: {}", principalList);
		for(Object registerdPrincipal: principalList) {
			String userId = (String) registerdPrincipal;
			if(userId.equals(principal)) {
				List<SessionInformation> sessionInfo = sessionRegistry.getAllSessions(registerdPrincipal, true);
				for (SessionInformation session: sessionInfo) {
					session.expireNow();
					removeSessionOwner(session.getSessionId());
				}
			}
		}
	}
	
	/**
	 * 세션이 유효한지 확인
	 * @param sessionId 대상 세션 ID
	 * @return {@code sessionId}의 {@code SessionInformation}을 {@link SessionRegistry}로부터 조회하여 등록 또는 만료 여부를 확인하여 유효하면 <code>true<code>, 그렇지 않다면 <code>false<code> 
	 */
	public boolean isSessionAlive(String sessionId) {
		boolean result = false;
		if(sessionId != null) {
			SessionInformation sessionInfo = sessionRegistry.getSessionInformation(sessionId);
			if(sessionInfo != null && !sessionInfo.isExpired()) {
				result = true;
			}
		}
		//log.info("isSessionAlive - sessionId: {}, result: {}", sessionId, result);
		return result;
	}
	
	/**
	 * 해당 사용자 식별키로 등록된 세션의 ID 조회
	 * @param principal 사용자 식별키
	 * @see {@link Authentication#getPrincipal()}
	 * @return 조회된 세션 ID, 없다면 <code>NULL<code>
	 */
	@Deprecated
	public String getSessionId(String principal) {
		List<Object> principalList = sessionRegistry.getAllPrincipals();
		if(principalList.size() == 0) {
			return null;
		}
		List<SessionInformation> sessionList = principalList.stream().filter(p -> p.equals(principal)).map(p -> sessionRegistry.getAllSessions(p, false)).findFirst().orElseGet(null);
		return (sessionList != null)?sessionList.stream().map(s -> s.getSessionId()).findFirst().orElseGet(null):null;
	}
	
	/**
	 * 해당 세션 ID를 소유한 사용자 식별키 조회
	 * @param sessionID 세션 ID
	 * @return 조회된 사용자 식별키, 없다면 <code>NULL<code>
	 */
	public String getPrincipal(String sessionId) {
		SessionInformation sessionInfo = sessionRegistry.getSessionInformation(sessionId);
		return (String) sessionInfo.getPrincipal();
	}
	
	/**
	 * 현재 세션을 보유한 모든 사용자 식별키 조회하여 반환
	 * @return 사용자 식별키 리스트
	 */
	public List<Object> getAllPrincipals() {
		return sessionRegistry.getAllPrincipals();
	}
	
	/**
	 * 해당 사용자 식별키로 등록된 모든 {@code SessionInformation}를 조회하여 반환. {@code includeExpiredSessions}가 <code>true<code>인 경우, 이미 만료된 세션에 대한 {@code SessionInformation}도 포함한다.
	 * @param principal 사용자 식별키
	 * @param includeExpiredSessions 만료된 세션 포함 여부
	 * @return {@code SessionInformation} 리스트
	 */
	public List<SessionInformation> getAllSessions(Object principal, boolean includeExpiredSessions){
		return sessionRegistry.getAllSessions(principal, includeExpiredSessions);
	}
}
