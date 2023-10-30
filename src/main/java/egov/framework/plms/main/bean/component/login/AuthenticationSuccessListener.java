package egov.framework.plms.main.bean.component.login;

import javax.servlet.http.HttpSession;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import egov.framework.plms.main.bean.component.session.SessionManager;
import egov.framework.plms.main.core.model.login.AuthenticationDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Authentication 할당 이벤트를 감지
 * @author mckim
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Deprecated
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {
	private final SessionManager sessionMng;
	private final HttpSession httpSession;


	@Override
	public void onApplicationEvent(AuthenticationSuccessEvent event) {
		// TODO Auto-generated method stub
		Authentication authentication = event.getAuthentication();
    	AuthenticationDetails detail = (AuthenticationDetails) authentication.getDetails();
    	
	}
}
