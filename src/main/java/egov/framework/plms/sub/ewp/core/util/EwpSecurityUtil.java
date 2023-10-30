package egov.framework.plms.sub.ewp.core.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import egov.framework.plms.main.core.model.enums.user.DomainRole;
import egov.framework.plms.main.core.model.enums.user.LoginType;
import egov.framework.plms.main.core.model.enums.user.ManagerRole;
import egov.framework.plms.sub.ewp.core.model.login.EwpAuthenticationDetails;
import lombok.extern.slf4j.Slf4j;


/**
 * EwpSecurityUtil 클래스는 스프링 시큐리티와 관련된 유틸리티 메소드를 제공합니다.<br>
 *
 * - {@link #getAuthentication()}: 인증 객체 조회<br>
 * - {@link #getAuthenticationDetails()}: 인증 상세 정보 조회<br>
 * - {@link #getLoginId()}: 로그인 ID 조회<br>
 * - {@link #isAnonymous()}: 사용자가 익명 사용자인지 확인<br>
 * 
 * @author mckim
 * @version 1.0
 * @since 2023. 5. 2
 */
@Slf4j
public class EwpSecurityUtil {
	/**
     * 현재 SecurityContext에서 Authentication 객체를 반환합니다.
     * 
     * @return 현재 인증 객체
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 현재 사용자가 익명 사용자인지 확인합니다.
     * 
     * @return 익명 사용자인 경우 true, 그렇지 않은 경우 false
     */
    public static boolean isAnonymous() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return true;
        }
        return authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ANONYMOUS"));
    }

    /**
     * 현재 인증된 사용자의 EwpAuthenticationDetails 객체를 반환합니다.
     * 인증되지 않은 사용자의 경우 null을 반환합니다.
     * 
     * @return EwpAuthenticationDetails 객체 또는 null
     */
    public static EwpAuthenticationDetails getAuthenticationDetails() {
        if (isAnonymous()) {
            return null;
        }

        Authentication authentication = getAuthentication();
        return (EwpAuthenticationDetails) authentication.getDetails();
    }
    
    public static boolean hasRole(DomainRole domainRole) {
    	EwpAuthenticationDetails details = getAuthenticationDetails();
    	if(details == null) {
    		return false;
    	}else {
    		return details.hasRole(domainRole);
    	}
    }
    
    public static boolean hasPosition(ManagerRole positionRole) {
    	EwpAuthenticationDetails details = getAuthenticationDetails();
    	if(details == null) {
    		return false;
    	}else {
    		return details.hasPosition(positionRole);
    	}
    }

    /**
     * 현재 인증된 사용자의 로그인 ID를 반환합니다.
     * 인증되지 않은 사용자의 경우 null을 반환합니다.
     * 
     * @return 로그인 ID 또는 null
     */
    public static String getLoginId() {
        if (isAnonymous()) {
            return null;
        }

        EwpAuthenticationDetails details = getAuthenticationDetails();
        if (details != null) {
            return details.getUserId();
        }
        return null;
    }
    
    /**
     * 현재 인증된 사용자의 소속 사업소 코드를 반환합니다.
     * 인증되지 않은 사용자의 경우 null을 반환합니다.
     * 
     * @return 소속 사업소 코드 또는 null
     */
    public static String getOfficeCode() {
        if (isAnonymous()) {
            return null;
        }

        EwpAuthenticationDetails details = getAuthenticationDetails();
        if (details != null) {
            return details.getOfficeCode();
        }
        return null;
    }

    /**
     * 현재 인증된 사용자의 소속 부서 ID를 반환합니다.
     * 인증되지 않은 사용자의 경우 null을 반환합니다.
     * 
     * @return 소속 부서 ID 또는 null
     */
	public static String getDeptId() {
		if (isAnonymous()) {
            return null;
        }

        EwpAuthenticationDetails details = getAuthenticationDetails();
        if (details != null) {
            return details.getDeptId();
        }
        return null;
	}
	
	/**
     * 현재 인증된 사용자의  로그인 방식을 반환합니다.
     * 인증되지 않은 사용자의 경우 null을 반환합니다.
     * 
     * @return 로그인 방식 또는 null
     */
	public static LoginType getLoginType() {
		if (isAnonymous()) {
            return null;
        }

        EwpAuthenticationDetails details = getAuthenticationDetails();
        if (details != null) {
            return details.getLoginType();
        }
        return null;
	}
	
	/**
     * 현재 요청에 대한 HttpSession을 반환합니다.
     * HTTP 요청이 없는 경우 null을 반환합니다.
     * 
     * @return HttpSession 객체 또는 null
     */
    public static HttpSession getHttpSession() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            return request.getSession(false);
        } catch (IllegalStateException | ClassCastException ex) {
            // HTTP 요청이 없거나 다른 문제가 발생한 경우
            return null;
        }
    }
}
