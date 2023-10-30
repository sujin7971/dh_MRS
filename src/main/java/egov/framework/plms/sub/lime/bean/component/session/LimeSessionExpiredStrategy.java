package egov.framework.plms.sub.lime.bean.component.session;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Profile;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


/**
 * 세션이 만료된 사용자에 대한 처리 클래스
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Profile("lime")
public class LimeSessionExpiredStrategy implements SessionInformationExpiredStrategy {
	@Setter
	private SessionRegistry sessionRegistry;
	
    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent e) throws IOException, ServletException {
    	String redirectUrl = e.getRequest().getContextPath() + "/login";
    	String message = "세션이 만료되어 자동 로그아웃되었습니다. 로그인 페이지로 이동합니다.";
	    String expiredID = (String) e.getSessionInformation().getPrincipal();
	    List<Object> principalList = sessionRegistry.getAllPrincipals();
		for(Object principal: principalList) {
			String empID = (String) principal;
			if(empID.equals(expiredID)) {
				//지금 만료된 사번과 동일한 사번을 가진 세션이 있는지 확인
				List<SessionInformation> sessionInfo = sessionRegistry.getAllSessions(principal, false);
				if(sessionInfo.size() == 0) {//수정으로 인한 자동 로그아웃으로 인해 세션 없음
					message = "계정정보가 수정되어 자동으로 로그아웃 처리 되었습니다.<br>자세한 사항은 관리자에게 문의하여 주시기 바랍니다.";
				}else {//중복 로그인으로 인한 자동 로그아웃 인 경우 세션 존재
					message = "동일한 아이디로 중복 로그인하여 자동 로그아웃 처리 되었습니다.<br>부정 로그인이 의심되는 경우, 관리자에게 문의하시기 바랍니다.";
				}
				break;
			}
		}
	    String ajaxHeader = e.getRequest().getHeader("X-Requested-With");
	    log.info("ajaxHeader : {}", ajaxHeader);
	    if ("XMLHttpRequest".equals(ajaxHeader)) {
        	// 401 Error를 반환
	    	e.getResponse().setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	    	e.getResponse().getWriter().write(message);
        }else {
        	RequestDispatcher dispatcher = e.getRequest().getRequestDispatcher(redirectUrl);
        	e.getRequest().setAttribute("expiredMessage", message);
        	dispatcher.forward(e.getRequest(), e.getResponse());
        }
    }

}
