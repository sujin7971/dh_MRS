package egov.framework.plms.sub.lime.bean.component.login;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import egov.framework.plms.main.bean.mvc.service.log.LoginHistoryService;
import egov.framework.plms.main.core.model.enums.common.LoginResult;
import egov.framework.plms.main.core.util.CommUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("lime")
public class LimeLoginFailureHandler implements AuthenticationFailureHandler{
	private final LoginHistoryService loginHistoryService;
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		String clientMessage = "";
		String principal = request.getParameter("username");
        LoginResult loginResult = LoginResult.UNKNOWN_ERROR;
		if( exception instanceof InternalAuthenticationServiceException) {
			clientMessage = "아이디 또는 비밀번호가 잘못 입력 되었습니다.<br> 아이디와 비밀번호를 정확히 입력해 주세요.";
			loginResult = LoginResult.BAD_PRINCIPAL;
		} else if(exception instanceof AuthenticationServiceException) {
        	clientMessage = "동시 접속자 인원을 초과했습니다. 잠시 후 다시 시도해주세요.";
        	loginResult = LoginResult.ACCESS_DENIED ;
		} else if(exception instanceof InsufficientAuthenticationException) {
			request.getSession().setAttribute("serverAccessDenied", true); // 세션에 속성 추가
        	response.sendRedirect("/login/serverAccessDenied");
        	return;
		} else if(exception instanceof AccountExpiredException) {
        	clientMessage = "만료된 계정입니다. 관리자에게 문의하여 계정을 재활성화해주세요.";
        	loginResult = LoginResult.EXPIRED_ACCOUNT;
		} else if(exception instanceof LockedException) {
        	clientMessage = "계정이 잠겨 있습니다. 잠시 후 다시 시도해주세요.";
        	loginResult = LoginResult.ACCOUNT_LOCKED;
		} else if(exception instanceof BadCredentialsException) {
        	clientMessage = "아이디 또는 비밀번호가 잘못 입력되었습니다.<br>아이디와 비밀번호를 정확히 입력해주세요.";
        	loginResult = LoginResult.BAD_CREDENTIALS;
		} else if(exception instanceof DisabledException) {
        	clientMessage = "비활성화된 계정입니다. 관리자에게 문의하여 계정을 활성화해주세요.";
        	loginResult = LoginResult.DEACTIVATED;
		}else {
			//exception.printStackTrace();
			log.error("로그인 실패 Exception: {}", exception.getMessage());
			clientMessage = "알 수 없는 에러가 발생했습니다. 다시 시도해주세요.";
		}
		request.setAttribute("loginMessage", clientMessage);
		loginHistoryService.insertLoginHistory(principal, CommUtil.getIp(request), loginResult);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/login");
		dispatcher.forward(request, response);	
		
	}
}
