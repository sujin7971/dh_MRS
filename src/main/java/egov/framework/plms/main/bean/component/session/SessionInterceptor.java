package egov.framework.plms.main.bean.component.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


/**
 * URI 응답 요청 전 해당 요청에 맞춰 Session Timeout을 재조정해줄 컴포넌트 클래스
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 */
@Slf4j
@Component
@ConfigurationProperties("server.servlet.session")
public class SessionInterceptor implements HandlerInterceptor{
	@Setter
	private Integer timeout;
	
	
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
    	String uri = request.getRequestURI();
    	if(uri.contains("/resources")) {
    		return true;
    	}
    	if(uri.contains("/error")) {
    		return true;
    	}
        HttpSession session = request.getSession();
        int maxInactiveInterval = session.getMaxInactiveInterval();
        if(maxInactiveInterval < timeout) {
        	//log.info("[{}] - 세션 타임아웃 설정[남은시간:{}ms, 설정:{}ms]", uri, session.getMaxInactiveInterval(), timeout);
        	session.setMaxInactiveInterval(timeout);
        }else {
        	//log.info("[{}] - 세션 타임아웃 설정[남은시간:{}ms, 설정:{}ms]", uri, session.getMaxInactiveInterval(), timeout);
        	session.setMaxInactiveInterval(maxInactiveInterval);
        }
        return true;
    }
 
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        // TODO Auto-generated method stub
        
    }
 
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // TODO Auto-generated method stub
        
    }
 
}
