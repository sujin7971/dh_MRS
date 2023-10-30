package egov.framework.plms.sub.lime.bean.mvc.controller.common;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import egov.framework.plms.sub.lime.bean.mvc.service.meeting.LimeMeetingFileInfoService;
import egov.framework.plms.sub.lime.core.util.LimeSecurityUtil;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lime/test")
@Profile("lime-server-embeded")
public class LimeTestRestController {
	private final LimeMeetingFileInfoService fileServ;
	
	@GetMapping("/meeting/{meetingId}/memo/create")
	public void generateMemoFile(@PathVariable Integer meetingId) {
		String userId = LimeSecurityUtil.getLoginId();
		fileServ.generateMemo(meetingId, userId);
	}
}
