package egov.framework.plms.sub.lime.bean.mvc.controller.common;

import org.springframework.context.annotation.Profile;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import egov.framework.plms.main.core.model.enums.common.ServerType;
import egov.framework.plms.main.core.model.enums.user.DomainRole;
import egov.framework.plms.main.core.util.DateTimeUtil;
import egov.framework.plms.sub.ewp.bean.mvc.service.common.EwpCodeService;
import egov.framework.plms.sub.ewp.core.model.login.EwpAuthenticationDetails;
import egov.framework.plms.sub.lime.bean.component.properties.LimeServerProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@Profile("lime")
public class LimeCommonController {
	private final LimeServerProperties serverProperties;
	@GetMapping("/home")
	public String home() {
		return "redirect:/lime/home";
	}
	
	@GetMapping("/lime/home")
	public String home(Authentication authentication, Model model,
			@RequestParam @Nullable String date) {
		ServerType serverType = serverProperties.getType();
		switch(serverType) {
			case USER:
			case EMBEDED:
				return "redirect:/lime/dashboard?date = "+date;
			case ADMIN:
				return "redirect:/lime/admin/dashboard?date = "+date;
		}
		return "redirect:/lime/dashboard?date = "+date;
	}
	
	@GetMapping("/lime/dashboard")
	public String dashboard(Authentication authentication, Model model,
			@RequestParam @Nullable String date) {
		
		model.addAttribute("date", DateTimeUtil.toFormattedDate(date));
		return "/home/user-home";
	}
	
	@GetMapping("/viewer")
	//viewer?file="/file/link/ajksdhkasjdhaksd"
	public String test(@RequestParam String file) {
		return "/common/viewer";
	}
}
