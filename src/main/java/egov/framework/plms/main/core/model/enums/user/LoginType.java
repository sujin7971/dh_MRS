package egov.framework.plms.main.core.model.enums.user;

import org.apache.ibatis.type.MappedTypes;

import egov.framework.plms.main.core.model.enums.CodeEnum;
import egov.framework.plms.main.core.mybatis.CodeEnumTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
/**
 * 로그인 방식 구분<br>
 * 
 * {@link #SSO} : SSO를 통한 로그인<br>
 * {@link #PLTE} : PLTE를 통한 로그인<br>
 * {@link #FORMAL} : 일반 로그인<br>
 * {@link #GUEST} : 외부인 로그인<br>
 * 
 * @author mckim
 *
 */
@AllArgsConstructor
@Getter
public enum LoginType implements CodeEnum{
	/** SSO */
	SSO("SSO"),
	/** PLTE */
	PLTE("PLTE"),
	/** 일반 */
	FORMAL("FORMAL"),
	/** 게스트 */
	GUEST("GUEST");
    private final String code;

    @MappedTypes(LoginType.class)
    public static class TypeHandler extends CodeEnumTypeHandler<LoginType> {
        public TypeHandler() {
            super(LoginType.class);
        }
    }
    @Override
    public String getCode() {
        return code;
    }
}
