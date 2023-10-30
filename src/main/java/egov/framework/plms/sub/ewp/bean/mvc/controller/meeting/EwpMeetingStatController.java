package egov.framework.plms.sub.ewp.bean.mvc.controller.meeting;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/ewp")
@RequiredArgsConstructor
@Profile("ewp")
public class EwpMeetingStatController {
	
	@GetMapping("/meeting/stat")
	public String viewMeetingStatPage() {
		return "/meeting/stat";
	}
}
