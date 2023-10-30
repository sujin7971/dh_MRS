package egov.framework.plms.sub.lime.bean.mvc.controller.meeting;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/lime")
@RequiredArgsConstructor
@Profile("lime")
public class LimeMeetingStatController {
	
	@GetMapping("/meeting/stat")
	public String viewMeetingStatPage() {
		return "/meeting/stat";
	}
}
