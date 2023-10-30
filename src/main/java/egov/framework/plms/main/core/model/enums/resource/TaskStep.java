package egov.framework.plms.main.core.model.enums.resource;

import org.apache.ibatis.type.MappedTypes;

import egov.framework.plms.main.core.model.enums.CodeEnum;
import egov.framework.plms.main.core.mybatis.CodeEnumTypeHandler;
import lombok.AllArgsConstructor;

/**
 * 사용자 리포트의 담당에 따른 분류 ENUM. 현재 사용하지 않음
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 */
@AllArgsConstructor
@Deprecated
public enum TaskStep implements CodeEnum {
	/** 조사 */
	EXAMINE("EXAMINE"),
	/** 개발 */
	DEV("DEV"),
	/** 검토 */
	REVIEW("REVIEW"),
	/** 철회 */
	RJT("RJT"),
	/** 완료 */
	CPL("CPL"),
	;
	private final String code;
	
	@MappedTypes(TaskStep.class)
    public static class TypeHandler extends CodeEnumTypeHandler<TaskStep> {
        public TypeHandler() {
            super(TaskStep.class);
        }
    }
	
	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return code;
	}
}
