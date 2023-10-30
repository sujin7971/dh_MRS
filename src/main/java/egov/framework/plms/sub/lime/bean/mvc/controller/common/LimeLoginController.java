package egov.framework.plms.sub.lime.bean.mvc.controller.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import egov.framework.plms.main.annotation.NoCache;
import egov.framework.plms.main.bean.component.properties.ConfigProperties;
import egov.framework.plms.main.core.model.enums.user.DomainRole;
import egov.framework.plms.sub.lime.core.model.login.LimeAuthenticationDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@Profile("lime")
public class LimeLoginController {
	private final Environment environment;
	
	private final ConfigProperties configProperties;

	/**
	 * 기본요청시 로그인 화면으로.
	 */
	@GetMapping("/")
	public String main(Authentication authentication, HttpServletRequest request, HttpServletResponse response, Model model) {
		return "redirect:/login";
	}

	/**
	 * 로그인화면.
	 */
	@NoCache
	@RequestMapping("/login")
	public String memberLogin(Authentication authentication, Model model, HttpServletResponse response, HttpSession session) {		
		if (authentication == null) {
			return "/core/login";
		} else {
			LimeAuthenticationDetails detail = (LimeAuthenticationDetails)authentication.getDetails();
			if(detail.hasRole(DomainRole.FIRST_LOGIN)) {
				return "redirect:/init";
			}else {
				return "redirect:/home";
			}
		}
	}

	/**
	 * 비밀번호 변경 화면
	 * 
	 * @return
	 */
	@GetMapping("/init")
	public String initAccount() {
		return "/main/emp/changePW";
	}
}
