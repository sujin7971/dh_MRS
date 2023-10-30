package egov.framework.plms.sub.lime.core.model.session;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionEvent;

import egov.framework.plms.main.core.model.enums.user.LoginType;
import egov.framework.plms.sub.lime.core.model.login.LimeAuthenticationDetails;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LimeSessionAuthenticationStrategy implements SessionAuthenticationStrategy {
	private CompositeSessionAuthenticationStrategy compositeStrategy;
	private ChangeSessionIdAuthenticationStrategy fixationProtectionStrategy;
	private NullAuthenticatedSessionStrategy nullAuthenticatedSessionStrategy;
	private ApplicationEventPublisher eventPublisher;
	public LimeSessionAuthenticationStrategy(CompositeSessionAuthenticationStrategy compositeStrategy, ApplicationEventPublisher eventPublisher) {
		this.compositeStrategy = compositeStrategy;
		this.eventPublisher = eventPublisher;
		this.fixationProtectionStrategy = new ChangeSessionIdAuthenticationStrategy();
		this.nullAuthenticatedSessionStrategy = new NullAuthenticatedSessionStrategy();
	}

	@Override
	public void onAuthentication(Authentication authentication, HttpServletRequest request,
			HttpServletResponse response) throws SessionAuthenticationException {
		if (authentication != null && authentication.getDetails() instanceof LimeAuthenticationDetails) {
            LimeAuthenticationDetails details = (LimeAuthenticationDetails) authentication.getDetails();
            if (details.getLoginType() == LoginType.FORMAL) {
                compositeStrategy.onAuthentication(authentication, request, response);
            } else {
                String oldSessionId = request.getRequestedSessionId();
                log.info("이전 세션 ID: {}", oldSessionId);
                fixationProtectionStrategy.onAuthentication(authentication, request, response);
                String newSessionId = request.getRequestedSessionId();
                log.info("현재 세션 ID: {}", newSessionId);
                if (!Objects.equals(oldSessionId, newSessionId)) {
                    SessionFixationProtectionEvent event = new SessionFixationProtectionEvent(authentication, oldSessionId, newSessionId);
                    eventPublisher.publishEvent(event);
                }
            }
        } else {
            nullAuthenticatedSessionStrategy.onAuthentication(authentication, request, response);
        }
	}
}
