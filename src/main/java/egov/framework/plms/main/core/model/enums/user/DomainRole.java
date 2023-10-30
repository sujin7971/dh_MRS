package egov.framework.plms.main.core.model.enums.user;

import org.apache.ibatis.type.MappedTypes;

import egov.framework.plms.main.core.model.enums.CodeEnum;
import egov.framework.plms.main.core.model.login.AuthenticationDetails;
import egov.framework.plms.main.core.mybatis.CodeEnumTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
/**
 * Spring Security를 통한 권한 설정에서 사용할 사용자 역할 구분<br>
 * Spring Security의 기본 Prefix가 "ROLE_"이므로 enum value에 모두 "ROLE_"을 붙임<br>
 * {@link AuthenticationDetails#getAuthorities}를 통해 사용자에게 알맞은 권한 부여 <br><br>
 * 
 * {@link #DEV} : 개발자 권한. 서비스 버전에서는 사용되지 않음<br>
 * {@link #MEMBER} : 인증된 사용자가 기본적으로 부여받을 권한<br>
 * {@link #MASTER} : 최고 관리자 로그인시 부여될 권한<br>
 * {@link #ADMIN} : 관리자 로그인시 부여될 권한<br>
 * {@link #MANAGER} : 사업소 관리자 로그인시 부여될 권한. 동서버전에서만 사용<br>
 * {@link #GENERAL} : 일반 사용자 로그인시 부여될 권한<br>
 * {@link #INIT} : 첫 접속, 비밀번호 초기화 등으로 비밀번호 재설정이 필요한 사용자에게 부여될 권한. 동서버전에서는 사용되지 않음.<br>
 * {@link #GUEST} : 외부 인원 로그인시 부여될 권한. 동서버전에서는 사용되지 않음.<br>
 * 
 * @author mckim
 *
 */
@AllArgsConstructor
@Getter
public enum DomainRole implements LevelRole{
	/** 사용자 */
	MEMBER("ROLE_MEMBER"),
	/** 개발자 */
	DEV("ROLE_DEV"),
	/** 최고 관리자 */
	MASTER_ADMIN("ROLE_MASTER_ADMIN"),
	/** 관리자 */
	SYSTEM_ADMIN("ROLE_SYSTEM_ADMIN"),
	/** 결재 관리자 */
	APPROVAL_ADMIN("ROLE_APPROVAL_ADMIN"),
    /** 일반 사용자 */
    GENERAL("ROLE_GENERAL"),
	/** 게스트 */
	GUEST("ROLE_GUEST"),
	/** 비밀번호 변경등 이후 첫 로그인 */
	FIRST_LOGIN("ROLE_FIRST_LOGIN");
    private final String code;

    @MappedTypes(DomainRole.class)
    public static class TypeHandler extends CodeEnumTypeHandler<DomainRole> {
        public TypeHandler() {
            super(DomainRole.class);
        }
    }

    @Override
    public String getCode() {
        return code;
    }
    
    public static DomainRole codeOf(String code) {
    	return CodeEnum.codeOf(DomainRole.class, code);
    }
}
