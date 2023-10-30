package egov.framework.plms.main.bean.component.auth;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;

import egov.framework.plms.main.bean.mvc.entity.auth.ResourceAuthorityVO;
import egov.framework.plms.main.core.model.enums.AuthCode;
import egov.framework.plms.main.core.model.enums.user.DomainRole;
import egov.framework.plms.main.core.model.login.AuthenticationDetails;
import lombok.extern.slf4j.Slf4j;

/**
 * 자원에 대한 사용자의 모든 권한을 수집하여 제공한다
 * 
 * @author mckim
 *
 */
@Slf4j
public abstract class AbstractAuthorityCollector implements BitMaskable {
	/**
	 * Authentication 객체에서 UserRole의 String 코드값으로 이루어진 권한 리스트를 가져와 UserRole객체의 집합을 생성하여 반환
	 * @param authentication
	 * @return
	 */
	protected Set<DomainRole> getAllDomainRole(Authentication authentication) {
		AuthenticationDetails details = (AuthenticationDetails )authentication.getDetails();
		log.info("인증 대상 사용자 권한 목록: {}", authentication.getAuthorities().toString());
		return details.getAuthorities().stream()
				.map(authority -> DomainRole.codeOf(authority.getAuthority()))
				.filter(role -> role != null)
				.collect(Collectors.toSet());
	}
	protected Set<DomainRole> getAllPositionRole(Authentication authentication) {
		AuthenticationDetails details = (AuthenticationDetails )authentication.getDetails();
		log.info("인증 대상 사용자 권한 목록: {}", authentication.getAuthorities().toString());
		return details.getAuthorities().stream()
				.map(authority -> DomainRole.codeOf(authority.getAuthority()))
				.filter(role -> role != null)
				.collect(Collectors.toSet());
	}
	
	public boolean hasBit(Integer target, Integer check) {
		Integer result = bitWiseAND(target, check);
		return (result == 0)?false:true;
	}
	
	public Integer bitWiseAND(Integer a, Integer b) {
		try {
			return a & b;
		}catch(Exception e){
			return 0;
		}
	}
	
	public Integer bitWiseAND(Collection<Integer> collection) {
		Integer p = collection.stream().reduce(0, (total, n) -> bitWiseAND(total, n));
		return p;
	}
	
	public Integer bitWiseOR(Integer a, Integer b) {
		try {
			return a | b;
		}catch(Exception e){
			return 0;
		}
	}
	
	public Integer bitWiseOR(Collection<Integer> collection) {
		Integer p = collection.stream().reduce(0, (total, n) -> bitWiseOR(total, n));
		return p;
	}
	
	public Integer bitWiseMINUS(Integer a, Integer b) {
		try {
			return a & ~b;
		}catch(Exception e){
			return 0;
		}
	}
	/**
	 * 해당 권한의 권한값 반환
	 * @param auth 권한
	 * @return
	 */
	abstract public Integer getAuthorityValue(AuthCode auth);
	/**
	 * 해당 권한 모음의 통합 권한값 반환
	 * @param authCollection 권한 모음
	 * @return
	 */
	abstract public Integer getAuthorityValue(Collection<AuthCode> authCollection);
	/**
	 * 해당 자원의 대한 사용자의 개인 권한 조회
	 * @param userId 사용자 로그인 ID
	 * @param srcId 자원 고유키
	 * @return 권한 집합을 소유한 ResourceAuthorityCollection 객체
	 */
	abstract public List<ResourceAuthorityVO> getPersonalAuthorityList(String userId, Integer srcId);
	/**
	 * 해당 자원의 대한 사용자의 일반 권한 조회
	 * @param authentication 사용자 인증 객체
	 * @param srcId 자원 고유키
	 * @return 권한 집합을 소유한 ResourceAuthorityCollection 객체
	 */
	abstract public List<ResourceAuthorityVO> getGeneralAuthorityList(Authentication authentication, Integer srcId);
	/**
	 * 해당 자원의 대한 사용자의 모든 권한 조회
	 * @param authentication 사용자 인증 객체
	 * @param srcId 자원 고유키
	 * @return 권한 집합을 소유한 ResourceAuthorityCollection 객체
	 */
	abstract public List<ResourceAuthorityVO> getPrivilegeAuthorityList(Authentication authentication, Integer srcId);
}
