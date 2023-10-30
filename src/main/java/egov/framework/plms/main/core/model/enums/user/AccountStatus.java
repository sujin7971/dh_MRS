package egov.framework.plms.main.core.model.enums.user;

import org.apache.ibatis.type.MappedTypes;

import egov.framework.plms.main.core.model.enums.CodeEnum;
import egov.framework.plms.main.core.mybatis.CodeEnumTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AccountStatus implements CodeEnum{
	/** 정상적으로 로그인 가능한 계정 */
	NORMAL("NORMAL"),
	/** 영구적으로 잠긴 계정 */
	LOCKED("LOCKED"),
	/** 일시적으로 잠긴 계정, 로그인 실패 횟수 초과 등 */
	TEMPORARILY_LOCKED("TEMPORARILY_LOCKED"),
	/** 비활성화된 계정 */
	DEACTIVATED("DEACTIVATED");
    private final String code;

    @MappedTypes(AccountStatus.class)
    public static class TypeHandler extends CodeEnumTypeHandler<AccountStatus> {
        public TypeHandler() {
            super(AccountStatus.class);
        }
    }
    @Override
    public String getCode() {
        return code;
    }
}
