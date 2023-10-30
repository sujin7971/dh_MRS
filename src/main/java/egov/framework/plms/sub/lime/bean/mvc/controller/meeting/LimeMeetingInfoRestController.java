package egov.framework.plms.sub.lime.bean.mvc.controller.meeting;

import java.util.Set;

import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import egov.framework.plms.main.core.model.auth.ResourceAuthorityCollection;
import egov.framework.plms.sub.lime.bean.component.auth.LimeResourceAuthorityProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/lime")
@RequiredArgsConstructor
@Profile("lime")
public class LimeMeetingInfoRestController {
	private final LimeResourceAuthorityProvider authorityProvider;
	/**
	 * 사용자의 해당 회의에 대한 권한 조회
	 * @param authentication
	 * @param meetingId
	 * @return
	 */
	@GetMapping("/meeting/{meetingId}/authority/private")
	public Set<String> getUserAuthorityForMeeting(Authentication authentication, @PathVariable Integer meetingId) {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingId, false);
		return authorityCollection.getAuthorities();
	}
	
	/**
	 * 관리자의 해당 회의에 대한 권한 조회
	 * @param authentication
	 * @param meetingId
	 * @return
	 */
	@GetMapping("/admin/system/meeting/{meetingId}/authority/private")
	public Set<String> getAdminAuthorityForMeeting(Authentication authentication, @PathVariable Integer meetingId) {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingId, false);
		return authorityCollection.getAuthorities();
	}
}
