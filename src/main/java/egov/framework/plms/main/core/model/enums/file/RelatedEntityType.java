package egov.framework.plms.main.core.model.enums.file;

import org.apache.ibatis.type.MappedTypes;

import egov.framework.plms.main.core.model.enums.CodeEnum;
import egov.framework.plms.main.core.model.enums.NumEnum;
import egov.framework.plms.main.core.mybatis.CodeEnumTypeHandler;
import egov.framework.plms.main.core.mybatis.NumEnumTypeHandler;


/**
 * 파일 유형별 발급될 MIME에 대한 ENUM
 * @author mckim
 * @version 1.0
 * @since 2023. 03. 28
 */
public enum RelatedEntityType implements NumEnum{
	/** {@code 회의} */
	MEETING(10),
	/** {@code 사용자 게시판} */
	USER_BOARD(20),
	/** {@code 공지사항 게시판} */
	NOTICE_BOARD(21)
	;
	RelatedEntityType(Integer code){
		this.code = code;
	}
	private final Integer code;
	
	@MappedTypes(RelatedEntityType.class)
    public static class TypeHandler extends NumEnumTypeHandler<RelatedEntityType> {
        public TypeHandler() {
            super(RelatedEntityType.class);
        }
    }
	
	@Override
	public Integer getCode() {
		return code;
	}
	
	public static RelatedEntityType codeOf(Integer code) {
    	return NumEnum.codeOf(RelatedEntityType.class, code);
    }
}
