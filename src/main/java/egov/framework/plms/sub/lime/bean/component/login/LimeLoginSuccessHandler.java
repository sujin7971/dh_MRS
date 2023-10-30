package egov.framework.plms.sub.lime.bean.component.login;

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

import egov.framework.plms.main.bean.component.properties.LoginProperties;
import egov.framework.plms.main.bean.component.properties.abst.CustomServerProperties;
import egov.framework.plms.main.bean.mvc.entity.organization.UserAccountVO;
import egov.framework.plms.main.bean.mvc.service.log.LoginHistoryService;
import egov.framework.plms.main.core.model.enums.common.LoginResult;
import egov.framework.plms.main.core.model.enums.common.ServerType;
import egov.framework.plms.main.core.model.enums.user.AccountStatus;
import egov.framework.plms.main.core.model.enums.user.DomainRole;
import egov.framework.plms.main.core.model.login.AuthenticationDetails;
import egov.framework.plms.main.core.util.CommUtil;
import egov.framework.plms.sub.lime.bean.component.properties.LimeServerProperties;
import egov.framework.plms.sub.lime.bean.mvc.service.organization.LimeUserAccountService;
import egov.framework.plms.sub.lime.core.model.login.LimeAuthenticationDetails;
import egov.framework.plms.sub.lime.core.util.LimeSecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("lime")
public class LimeLoginSuccessHandler implements AuthenticationSuccessHandler{
	private final LimeUserAccountService accountServ;
	private final LoginHistoryService loginHistoryService;
	private final CustomServerProperties serverProperties;
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		String uri = getSavedRequestURI(request, response);
		if(uri == null) {
			ServerType serverType = serverProperties.getType();
			switch(serverType) {
				case USER:
					uri = getRedirectUserServer();
					break;
				case ADMIN:
					uri = getRedirectAdminServer();
					break;
				case EMBEDED:
					uri = "/lime/dashboard";
					break;
			}
		}
		String principal = request.getParameter("username");
		loginHistoryService.insertLoginHistory(principal, CommUtil.getIp(request), LoginResult.SUCCESS);
		processMemberLoginSuccess(request, response, authentication);
		response.sendRedirect(uri);
	}
	
	private String getSavedRequestURI(HttpServletRequest request, HttpServletResponse response) {
		RequestCache requestCache = new HttpSessionRequestCache();
		SavedRequest savedRequest = requestCache.getRequest(request, response);
		String uri = null;
		// 있을 경우 URI 등 정보를 가져와서 사용
		if (savedRequest != null) {
			uri = savedRequest.getRedirectUrl();	
			// 세션에 저장된 객체를 다 사용한 뒤에는 지워줘서 메모리 누수 방지
			requestCache.removeRequest(request, response);
		}
		return uri;
	}
	
	private String getRedirectUserServer() {
		return "/lime/dashboard";
	}
	
	private String getRedirectAdminServer() {
		LimeAuthenticationDetails detail = LimeSecurityUtil.getAuthenticationDetails();
		String uri = "/lime/dashboard";
		if(detail.hasRole(DomainRole.MASTER_ADMIN)) {
			uri = "/lime/admin/system/manage/organization";
		}else if(detail.hasRole(DomainRole.SYSTEM_ADMIN)) {
			uri = "/lime/admin/system/manage/organization";
		}
		return uri;
	}
	
	private void processMemberLoginSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {
		clearAuthenticationAttributes(request);
		clearAccountFailureHistory();
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
	
	private void clearAccountFailureHistory() {
		String userId = LimeSecurityUtil.getLoginId();
		accountServ.updateUserAccount(UserAccountVO.builder().userId(userId).failedAttempts(0).lockoutDateTime(null).status(AccountStatus.NORMAL).build());
	}
}
