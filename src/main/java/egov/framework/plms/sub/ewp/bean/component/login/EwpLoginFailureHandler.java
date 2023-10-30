package egov.framework.plms.sub.ewp.bean.component.login;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import egov.framework.plms.main.core.util.CommUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("ewp")
public class EwpLoginFailureHandler implements AuthenticationFailureHandler{

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		String clientMessage = "";
		String empID = request.getParameter("username");
        String logMessage = "";
		if( exception instanceof InternalAuthenticationServiceException) {
			clientMessage = "아이디 또는 비밀번호가 잘못 입력 되었습니다.<br> 아이디와 비밀번호를 정확히 입력해 주세요.";
			logMessage = "잘못된 사번(ID): "+exception.getMessage();
		} else if(exception instanceof AuthenticationServiceException) {
        	clientMessage = "동시접속 제한 인원을 초과했습니다. 잠시 후 다시 시도해 주세요.";
        	logMessage = "제한 인원 초과";
		} else if(exception instanceof InsufficientAuthenticationException) {
			request.getSession().setAttribute("serverAccessDenied", true); // 세션에 속성 추가
        	response.sendRedirect("/login/serverAccessDenied");
        	return;
		} else if(exception instanceof AccountExpiredException) {
        	clientMessage = exception.getMessage();
        	logMessage = "유효한 임시비밀번호 없음";
		} else if(exception instanceof BadCredentialsException) {
        	clientMessage = exception.getMessage();
        	logMessage = "올바르지 않은 임시비밀번호";
		}else {
			//exception.printStackTrace();
			log.error("로그인 실패 Exception: {}", exception.getMessage());
			clientMessage = exception.getMessage();
			logMessage = "예외-"+clientMessage;
		}
		log.info("로그인 실패 - IP: {}, ID: {}, 메시지: {}", CommUtil.getIp(request), empID, logMessage);
		request.setAttribute("loginMessage", clientMessage);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/login");
		dispatcher.forward(request, response);	
		
	}

}
