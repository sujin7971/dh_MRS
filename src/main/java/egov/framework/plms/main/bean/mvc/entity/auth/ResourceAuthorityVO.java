package egov.framework.plms.main.bean.mvc.entity.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 리소스가 제공할 권한에 대한 정보를 담을 모델.<br>
 * 
 * 
 * @author mckim
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ResourceAuthorityVO implements GrantedAuthority {
	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
	
	/** 권한 키 */
	private Integer authId;
	/** 권한 코드 */
	private String authCode;
	/**
	 * 권한 분류<br>
	 * {@code COM} 공통<br>
	 * {@code MT} 공통<br>
	 * {@code FILE} 공통<br>
	 */
	private String authDiv;
	/** 권한명 */
	private String authName;
	/** 권한 설명 */
	private String authDesc;
	/** 권한 값 */
	private Integer authVal;
	
	@Override
	public String toString() {
		return "AuthorityCodeVO [authId=" + authId + ", authCode=" + authCode + ", authDiv=" + authDiv + ", authName="
				+ authName + ", authDesc=" + authDesc + ", authVal=" + authVal + "]";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof ResourceAuthorityVO) {
			return this.authCode.equals(((ResourceAuthorityVO) obj).getAuthCode());
		}

		return false;
	}

	@Override
	public String getAuthority() {
		return this.authCode;
	}
}
