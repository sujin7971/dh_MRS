package egov.framework.plms.sub.ewp.bean.mvc.controller.common;

import org.springframework.context.annotation.Profile;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import egov.framework.plms.main.core.model.enums.user.DomainRole;
import egov.framework.plms.main.core.model.enums.user.ManagerRole;
import egov.framework.plms.main.core.util.DateTimeUtil;
import egov.framework.plms.sub.ewp.bean.mvc.service.common.EwpCodeService;
import egov.framework.plms.sub.ewp.core.model.login.EwpAuthenticationDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@Profile("ewp")
public class EwpCommonController {
	private final EwpCodeService codeServ;
	
	@GetMapping("/home")
	public String home() {
		return "redirect:/ewp/home";
	}
	
	@GetMapping("/ewp/home")
	public String home(Authentication authentication, Model model,
			@RequestParam @Nullable String date) {
		EwpAuthenticationDetails detail = (EwpAuthenticationDetails) authentication.getDetails();
		if(detail.hasPosition(ManagerRole.APPROVAL_MANAGER)) {
			return "redirect:/ewp/manager/approval/manage/meeting";
		}else if(detail.hasRole(DomainRole.MASTER_ADMIN)) {
			return "redirect:/ewp/admin/system/manage/authority";
		}else if(detail.hasRole(DomainRole.SYSTEM_ADMIN)) {
			return "redirect:/ewp/admin/system/manage/room";
		}
		return "redirect:/ewp/dashboard?date = "+date;
	}
	
	@GetMapping("/ewp/dashboard")
	public String dashboard(Authentication authentication, Model model,
			@RequestParam @Nullable String date) {
		
		model.addAttribute("date", DateTimeUtil.toFormattedDate(date));
		return "/common/home";
	}

	@GetMapping("/ewp/home/test")
	public String test(Authentication authentication, Model model) {
		model.addAttribute("officeBook", codeServ.getOfficeBook());
		return "/common/test";
	}
	
	@GetMapping("/viewer")
	//viewer?file="/file/link/ajksdhkasjdhaksd"
	public String test(@RequestParam String file) {
		return "/common/viewer";
	}
}
