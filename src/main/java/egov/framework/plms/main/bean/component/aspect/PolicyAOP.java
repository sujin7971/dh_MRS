package egov.framework.plms.main.bean.component.aspect;

import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PolicyAOP {
	@Pointcut("@annotation(egov.framework.plms.main.annotation.NoCache)")
	private void noCache() {}
	
	@Before("noCache()")
	public void disableCache(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();
        for(Object arg : args) {
            if(arg instanceof HttpServletResponse) {
            	HttpServletResponse response = (HttpServletResponse) arg;
            	response.setHeader("pragma",  "no-cache");  
        		response.setHeader("Cache-control", "no-cache, no-store, must-revalidate");             
        		response.setHeader("Expires", "0"); 
            }
        }
	}
}
