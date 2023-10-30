package egov.framework.plms.main.core.model.login;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

import egov.framework.plms.main.core.model.enums.user.LoginType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
/**
 * LoginAuthProvider에게 제공할 Authentication의 Custom Detail객체. 비회원 외부인 로그인시 필요한 파라미터 저장 및 제공.
 * @author mckim
 */
@Getter
@Slf4j
public class CustomWebAuthenticationDetails extends WebAuthenticationDetails {
	private static final long serialVersionUID = -3984468376168493070L;
	private final LoginType loginType;
	private final String guestname;
	private final String title;
	private final String compname;
	private final HttpServletRequest request;

	public CustomWebAuthenticationDetails(HttpServletRequest request) {
		super(request);
		// 로그인 폼에서 선언한 파라미터 명으로 request
		this.loginType = Optional.ofNullable(request.getParameter("loginType")).map(type -> LoginType.valueOf(type)).orElse(null);
		this.guestname = request.getParameter("guestname");
		this.title = request.getParameter("title");
		this.compname = request.getParameter("compname");
		this.request = request;
	}
}
