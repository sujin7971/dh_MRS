package egov.framework.plms.sub.ewp.bean.component.login;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import egov.framework.plms.main.core.model.enums.user.DomainRole;
import egov.framework.plms.main.core.model.enums.user.ManagerRole;
import egov.framework.plms.main.core.model.login.AuthenticationDetails;
import egov.framework.plms.main.core.util.CommUtil;
import egov.framework.plms.sub.ewp.core.model.login.EwpAuthenticationDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("ewp")
public class EwpLoginSuccessHandler implements AuthenticationSuccessHandler{

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		clearAuthenticationAttributes(request);
		RequestCache requestCache = new HttpSessionRequestCache();
		SavedRequest savedRequest = requestCache.getRequest(request, response);
		String uri = "/ewp/home";
		// 있을 경우 URI 등 정보를 가져와서 사용
		if (savedRequest != null) {
			uri = savedRequest.getRedirectUrl();	
			// 세션에 저장된 객체를 다 사용한 뒤에는 지워줘서 메모리 누수 방지
			requestCache.removeRequest(request, response);
		}
		EwpAuthenticationDetails detail = (EwpAuthenticationDetails)authentication.getDetails();
		log.info("인증객체: {}, {}", detail.getAuthorities(), detail.getPositions());
		if(detail.hasPosition(ManagerRole.APPROVAL_MANAGER)) {
			uri = "/ewp/manager/approval/manage/meeting";
		}else if(detail.hasRole(DomainRole.MASTER_ADMIN)) {
			uri = "/ewp/admin/system/manage/authority";
		}else if(detail.hasRole(DomainRole.SYSTEM_ADMIN)) {
			uri = "/ewp/admin/system/manage/room";
		}
		processMemberLoginSuccess(request, response, authentication);
		response.sendRedirect(uri);
	}
	
	private void processMemberLoginSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {
		AuthenticationDetails detail = (AuthenticationDetails)authentication.getDetails();
		LocalDateTime nowDT = LocalDateTime.now();
		LocalDateTime loginDT = detail.getLoginDateTime();
		Duration duration = Duration.between(loginDT, nowDT);
		log.info("로그인 성공({}ms) - IP: {}, ID: {}", duration.toMillis(), CommUtil.getIp(request), detail.getUserId());
	}

	/**
	 * 세션에 기록된 에러 제거
	 * @param request
	 */
	protected void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if(session==null) 
        	return;
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }
	
}
