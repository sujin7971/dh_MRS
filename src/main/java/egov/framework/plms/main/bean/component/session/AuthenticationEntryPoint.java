package egov.framework.plms.main.bean.component.session;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import lombok.extern.slf4j.Slf4j;

/**
 * 세션 만료로 인한 인증되지 않은 사용자의 Ajax요청을 처리할 클래스. 다른 요인으로 인해 세션이 expired된 사용자의 Ajax 요청은 LoginExpiredStrategy에서 처리.
 * {@link org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter SecurityConfig}의 
 * {@link egov.framework.plms.main.config.SecurityConfig#configure(HttpSecurity) SecurityConfig.configure} 함수에서 authenticationEntyuPoint로 설정.
 * 
 * @author mckim
 */
@Slf4j
public class AuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    public AuthenticationEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
    	String header = request.getHeader("X-Requested-With");
    	String forwardUri = (String) request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
        log.info("AuthenticationEntryPoint commence- forwardUri: {}", forwardUri);
        // AJAX 요청인지 검사 (헤더 검사, 비동기인지 체크)
        if ("XMLHttpRequest".equals(header)) {
        	// 401 Error를 반환
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증되지 않은 사용자의 요청입니다. 로그인 페이지로 이동합니다.");
        } else {
            super.commence(request, response, authException);
        }
    }
}
