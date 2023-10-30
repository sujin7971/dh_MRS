package egov.framework.plms.main.bean.component.login;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import egov.framework.plms.main.core.model.login.CustomWebAuthenticationDetails;
/**
 * Authentication의 getDetails() 함수가 제공할 Detail객체를 Custom Detail객체로 교체.
 * @author mckim
 */
@Component
public class CustomWebAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> {
	@Override
	public WebAuthenticationDetails buildDetails(HttpServletRequest context) {
		return new CustomWebAuthenticationDetails(context);
	}
}
