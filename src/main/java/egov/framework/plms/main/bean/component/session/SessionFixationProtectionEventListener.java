package egov.framework.plms.main.bean.component.session;

import org.springframework.context.ApplicationListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.session.SessionFixationProtectionEvent;
import org.springframework.stereotype.Component;

import egov.framework.plms.main.core.model.login.AuthenticationDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
/**
 * Session Fixation 방지를 위한 세션 교체시 트리거될 SessionFixationProtectionEvent의 리스너.
 * SessionManager에 새로 발급된 세션의 아이디와 그 소유주 등록.
 * @author mckim
 *
 */
public class SessionFixationProtectionEventListener implements ApplicationListener<SessionFixationProtectionEvent> {
	private final SessionManager sessionMng;
	
	@Override
	public void onApplicationEvent(SessionFixationProtectionEvent event) {
		String newId = event.getNewSessionId();
		
		Authentication authentication = event.getAuthentication();
    	AuthenticationDetails detail = (AuthenticationDetails) authentication.getDetails();
    	sessionMng.setSessionOwner(newId, detail);
    	log.info("SessionFixationProtection- newId: {}", newId);
	}

}
