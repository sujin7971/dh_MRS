package egov.framework.plms.main.bean.component.aspect;

import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class LogAOP {

	
	@Pointcut("@annotation(egov.framework.plms.main.annotation.ControllerLogging)")
	public void ctrlLogging() {}
	@Pointcut("@annotation(egov.framework.plms.main.annotation.ServiceLogging)")
	public void servLogging() {}
		
	@Around("ctrlLogging()")
    public Object logging(ProceedingJoinPoint pjp) throws Throwable {

        String params = getRequestParams();

        long startAt = System.currentTimeMillis();

        log.info("----------> REQUEST : {}({}) = {}", pjp.getSignature().getDeclaringTypeName(),
                pjp.getSignature().getName(), params);

        Object result = pjp.proceed();

        long endAt = System.currentTimeMillis();

        log.info("----------> RESPONSE : {}({}) = {} ({}ms)", pjp.getSignature().getDeclaringTypeName(),
                pjp.getSignature().getName(), result, endAt-startAt);

        return result;

    }

    // get requset value
    private String getRequestParams() {

        String params = "";

        RequestAttributes requestAttribute = RequestContextHolder.getRequestAttributes();

        if(requestAttribute != null){
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes()).getRequest();

            Map<String, String[]> paramMap = request.getParameterMap();

            if(!paramMap.isEmpty()) {
                params = " [" + paramMapToString(paramMap) + "]";
            }
        }
        return params;
    }

    private String paramMapToString(Map<String, String[]> paramMap) {
        return paramMap.entrySet().stream()
                .map(entry -> String.format("%s -> (%s)",
                        entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(", "));
    }
}
