package egov.framework.plms.sub.ewp.bean.mvc.controller.common;

import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import egov.framework.plms.main.bean.component.properties.LoginProperties;
import egov.framework.plms.main.core.util.CommUtil;
import egov.framework.plms.sub.ewp.bean.component.login.sso.EwpSSOLoginCheck;
import egov.framework.plms.sub.ewp.bean.component.properties.PlteProperties;
import egov.framework.plms.sub.ewp.bean.component.properties.SsoProperties;
import egov.framework.plms.sub.ewp.bean.mvc.service.organization.EwpUserInfoService;
import egov.framework.plms.sub.ewp.core.util.EwpSecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@Profile("ewp")
public class EwpLoginController {
	private final Environment environment;
	
	private final LoginProperties loginProperties;
	private final PlteProperties plteProperties;
	private final SsoProperties ssoProperties;
	private final EwpSSOLoginCheck ewpSSO;
	private final EwpUserInfoService userInfoServ;
	@RequestMapping("/")
	public String redirectDefaultURI() {
		return "redirect:/login";
	}
	
	@RequestMapping("/login")
	public String login(HttpServletRequest request, HttpServletResponse response, Model model) {
		log.info("로그인 페이지 요청");
		if(!EwpSecurityUtil.isAnonymous()) {
			log.info("인증된 사용자의 로그인 페이지 요청. 홈페이지로 리다이렉트");
			return "redirect:/home";
		}
		String connectIp = request.getRemoteAddr();
		if(ssoProperties.isEnabled() && !connectIp.contains( plteProperties.getRemoteIp() )) {
			return processSSOLogin(request, response, model);
		}else if(plteProperties.isEnabled()){
			return processPLTELogin(request, response, model);
		}else {
			return processMonitoring(request, response, model);
		}
	}
	
	private String processMonitoring(HttpServletRequest request, HttpServletResponse response, Model model) {
		return "redirect:/ewp/display";
	}
	
	private String processPLTELogin(HttpServletRequest request, HttpServletResponse response, Model model) {
		model.addAttribute("loginPermission", loginProperties.getLoginPermission());
		return "/core/login/login-plte";
	}
	
	private String processSSOLogin(HttpServletRequest request, HttpServletResponse response, Model model) {
		String connectIp = request.getRemoteAddr();
		String ssoPageUrl = ssoProperties.getPageUrl();
		log.info("Start SSO Login Process - IP: {}, SSO SERVER: {}", connectIp, ssoPageUrl);
		try {
			String encData = ewpSSO.createRequestData(request);
			String ssoResponse = ewpSSO.httpPost(URLEncoder.encode(encData));
			if(ssoResponse == null) {
				return "redirect:"+ssoPageUrl;
			}
			String responseToken = ewpSSO.parsingData(ssoResponse);
			log.info("SSO 서버망 인증 요청 - 응답: {}, 인증토큰: {}",ssoResponse, responseToken);
			if(ssoResponse.contains("connect")) {
				log.info("SSO 서버망 응답 없음. 로그인 절차 PLTE로 변경");
				return processPLTELogin(request, response, model);
			}else {
				String loginId = ewpSSO.getId(request, responseToken);
				log.info("SSO 서버망 인증 ID: {}", loginId);
				if(!CommUtil.isEmpty(loginId) && userInfoServ.selectUserInfoOne(loginId) != null) {
					request.getSession().setAttribute("loginId", loginId);
					return "/core/login/login-sso";
				}
				log.info("SSO 서버망 로그인 되어 있지 않음. SSO 로그인 페이지 연결");
				return "redirect:"+ssoPageUrl;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("SSO 서버망 인증 진행중 에러: {}", e.getMessage() );
			return "redirect:"+ssoPageUrl;
		}
	}
	
	@GetMapping("/login/serverAccessDenied")
    public String showServerAccessDeniedPage(HttpServletRequest request, Model model) {
        if (request.getSession().getAttribute("serverAccessDenied") == null) {
            return "redirect:/ewp/home"; // 속성이 없는 경우 로그인 페이지로 리다이렉트
        }

        request.getSession().removeAttribute("serverAccessDenied"); // 속성 제거
        return "/core/error/serverAccessDenied"; // JSP 페이지 이름
    }
}
