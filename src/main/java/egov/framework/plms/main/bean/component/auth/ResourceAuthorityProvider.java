package egov.framework.plms.main.bean.component.auth;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import egov.framework.plms.main.bean.component.common.AsyncProcessor;
import egov.framework.plms.main.bean.mvc.entity.auth.ResourceAuthorityVO;
import egov.framework.plms.main.core.model.auth.ResourceAuthorityCollection;
import egov.framework.plms.main.core.model.enums.user.DomainRole;
import egov.framework.plms.main.core.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 요청 자원에 대한 사용자의 권한 조회 후 제공
 * 
 * @author mckim
 *
 */
@Slf4j
@Component("ResourceAuthorityProvider")
public class ResourceAuthorityProvider {
	@Autowired
	private PassTokenProvider tokenProvider;
	@Autowired
	private MeetingAuthorityManager meetingAuthorityMng;
	@Autowired
	private AsyncProcessor asyncProcessor;
	
	protected boolean waitResourceAuthorityCertificate(Integer meetingId) {
		CompletableFuture<Boolean> futureResult = asyncProcessor.checkFunctionAsync(() -> {
			return meetingAuthorityMng.isResourceAuthorityCertificated(meetingId);
		}, true);
		try {
	        return futureResult.get(5, TimeUnit.SECONDS);
	    } catch (InterruptedException e) {
	        Thread.currentThread().interrupt();
	        return false;
	    } catch (ExecutionException e) {
	        // e.getCause()를 사용하여 원본 예외를 처리합니다.
	        // 로깅, 에러 메시지 등 추가 처리를 수행합니다.
	        return false;
	    } catch (TimeoutException e) {
	    	return false;
	    }
	}
	public ResourceAuthorityCollection getMeetingAuthorityCollection(Integer meetingId) {
		return getMeetingAuthorityCollection(meetingId, true);
	}
	public ResourceAuthorityCollection getMeetingAuthorityCollection(Integer meetingId, boolean enableToken) {
		Authentication authentication = SecurityUtil.getAuthentication();
		ResourceAuthorityCollection collection = getMeetingAuthorityCollection(authentication, meetingId);
		if(enableToken) {
			HttpSession session = SecurityUtil.getHttpSession();
			if(session != null) {
				Object token = session.getAttribute("passToken");
				if(token != null) {
					List<String> authList = tokenProvider.getTokenAuthorities((String) token, SecurityUtil.getLoginId(), meetingId.toString());
					List<ResourceAuthorityVO> authorities = authList.stream().map(code -> ResourceAuthorityVO.builder().authCode(code).build()).collect(Collectors.toList());
					collection.addAuthorities(authorities);
				}
			}
		}
		return collection;
	}
	public ResourceAuthorityCollection getMeetingPrivateAuthorityCollection(Integer meetingId) {
		return getMeetingPrivateAuthorityCollection(meetingId, true);
	}
	public ResourceAuthorityCollection getMeetingPrivateAuthorityCollection(String userId, Integer meetingId) {
		return getMeetingPrivateAuthorityCollection(userId, meetingId, true);
	}
	public ResourceAuthorityCollection getMeetingPrivateAuthorityCollection(Integer meetingId, boolean exceptToken) {
		return getMeetingPrivateAuthorityCollection(SecurityUtil.getLoginId(), meetingId, exceptToken);
	}
	public ResourceAuthorityCollection getMeetingPrivateAuthorityCollection(String userId, Integer meetingId, boolean exceptToken) {
		ResourceAuthorityCollection collection = getMeetingAuthorityCollection(userId, meetingId);
		if(!exceptToken) {
			HttpSession session = SecurityUtil.getHttpSession();
			if(session != null) {
				Object token = session.getAttribute("passToken");
				if(token != null) {
					List<String> authList = tokenProvider.getTokenAuthorities((String) token, SecurityUtil.getLoginId(), meetingId.toString());
					List<ResourceAuthorityVO> authorities = authList.stream().map(code -> ResourceAuthorityVO.builder().authCode(code).build()).collect(Collectors.toList());
					collection.addAuthorities(authorities);
				}
			}
		}
		return collection;
	}
	/**
	 * 회의에 대한 사용자의 모든 권한 제공. 개인/참석자로서의 권한 뿐만 아니라 {@link DomainRole} 및 소속 부서 권한도 함께 조회하여 제공한다. 
	 * @param authentication 사용자 로그인 인증객체. 사용자에게 부여된 {@link DomainRole} 조회
	 * @param meetingId 권한 제공할 회의 고유키
	 * @return 권한 {@link ResourceAuthorityVO}를 담은 권한모음객체
	 */
	private ResourceAuthorityCollection getMeetingAuthorityCollection(Authentication authentication, Integer meetingId) {
		boolean result = this.waitResourceAuthorityCertificate(meetingId);
		if(!result) {
			return ResourceAuthorityCollection.generateCollection(new HashSet<>());
		}
		ResourceAuthorityCollection mtCollection = meetingAuthorityMng.getResourceAuthorityCollection(authentication, meetingId);
		String authoritiesStr = Optional.of(mtCollection).map(ResourceAuthorityCollection::getAuthorities).map(value -> value.toString()).orElse("없음");
		String userName = authentication.getName();
		log.info("사용자({})의 회의({})에 대한 권한: {}", userName, meetingId, authoritiesStr);
		return mtCollection;
	}
	
	/**
	 * 회의에 대한 사용자의 권한 제공. 개인/참석자로서의 권한을 조회하여 제공한다. 
	 * @param userId 사용자 로그인 ID
	 * @param meetingId 권한 제공할 회의 고유키
	 * @return 권한 {@link ResourceAuthorityVO}를 담은 권한모음객체
	 */
	protected ResourceAuthorityCollection getMeetingAuthorityCollection(String userId, Integer meetingId) {
		boolean result = this.waitResourceAuthorityCertificate(meetingId);
		if(!result) {
			return ResourceAuthorityCollection.generateCollection(new HashSet<>());
		}
		ResourceAuthorityCollection mtCollection = meetingAuthorityMng.getResourceAuthorityCollection(userId, meetingId);
		String authoritiesStr = Optional.of(mtCollection).map(ResourceAuthorityCollection::getAuthorities).map(value -> value.toString()).orElse("없음");
		log.info("사용자({})의 회의({})에 대한 권한: {}", userId, meetingId, authoritiesStr);
		return mtCollection;
	}

}
