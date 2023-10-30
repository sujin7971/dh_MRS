package egov.framework.plms.main.core.model.auth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import egov.framework.plms.main.bean.mvc.entity.auth.ResourceAuthorityVO;
import egov.framework.plms.main.core.model.enums.AuthCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;


/**
 * 사용자의 자원에 대한 권한을 보관하고 권한 판별 관련 기능을 제공할 클래스
 * 
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 * @see {@link ResourceAuthorityVO}
 */
@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ResourceAuthorityCollection {
	@NonNull
	@Singular("authorities") private List<ResourceAuthorityVO> authorities;
	
	/**
	 * 권한 소유 여부 확인
	 * @param auth 확인할 권한
	 * @return 보유 중이라면 <code>true<code>, 아니라면 <code>false<code>
	 */
	public boolean hasAuthority(AuthCode auth) {
		if(auth == null) {
			return false;
		}
		for(ResourceAuthorityVO authority : authorities) {
			if(authority.getAuthority().equals(auth.getCode())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 권한 소유 여부 확인
	 * @param code 확인할 권한의 코드값
	 * @return 보유 중이라면 <code>true<code>, 아니라면 <code>false<code>
	 */
	public boolean hasAuthority(String code) {
		if(code == null) {
			return false;
		}
		for(ResourceAuthorityVO authority : authorities) {
			if(authority.getAuthority().equals(code)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 보유한 권한이 없는지 확인
	 * @return 보유한 권한이 없다면 <code>true<code>, 아니라면 <code>false<code>
	 */
	public boolean isEmpty() {
		return authorities.isEmpty();
	}
	/**
	 * 가변인자 매개변수로 전달받은 권한 코드를 제외하고 보유한 권한이 없는지 확인한다
	 * @param funcCodeArgs 권한 코드 집합
	 * @return 보유한 권한이 없다면 <code>true<code>, 아니라면 <code>false<code>
	 */
	public boolean isEmpty(String ...funcCodeArgs) {
		Set<String> funcCodeSet = Arrays.stream(funcCodeArgs).collect(Collectors.toSet());
		List<ResourceAuthorityVO> filteredAuthorities = authorities.stream().filter(auth -> !funcCodeSet.contains(auth.getAuthCode())).collect(Collectors.toList());
		return filteredAuthorities.isEmpty();
	}
	
	
	
	/**
	 * 보유한 권한의 코드 리스트 반환
	 * @return 보유 권한 코드 리스트
	 */
	public Set<String> getAuthorities(){
		return authorities.stream().map(e -> e.getAuthority()).collect(Collectors.toSet());
	}
	
	public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()+" (\n");
        for(ResourceAuthorityVO authority : authorities) {
        	 sb.append(" ");
	         sb.append(authority.toString());
	         sb.append("\n");
        }
	    sb.append(")\n");
	    return sb.toString();
	}
	
	/**
	 * 보유 권한 Serialize
	 * @return 권한 리스트의 Serialize된 JSON String
	 */
	public String toJson() {
		Set<String> authorities = getAuthorities();
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(authorities);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * 해당 권한 모음을 보유한 {@link ResourceAuthorityCollection}객체 생성 및 반환
	 * @param authortiyCollection 보유할 권한 모음
	 * @return
	 */
	public static ResourceAuthorityCollection generateCollection(Collection<AuthCode> authortiyCollection) {
		List<ResourceAuthorityVO> authortiyList = authortiyCollection.stream().map(e -> ResourceAuthorityVO.builder().authCode(e.getCode()).build()).collect(Collectors.toList());
		return ResourceAuthorityCollection.builder().authorities(authortiyList).build();
	}
	
	public void addAuthorities(List<ResourceAuthorityVO> addAuthorities) {
		List<ResourceAuthorityVO> newAuthorities = new ArrayList<>();
		log.info("권한 추가- 기존 권한: {}, 추가할 권한: {}", this.authorities.toString(), addAuthorities.toString());
		this.authorities.forEach(authority -> newAuthorities.add(authority));
		addAuthorities.forEach(authority -> newAuthorities.add(authority));
		
		this.authorities = newAuthorities;
	}
}
