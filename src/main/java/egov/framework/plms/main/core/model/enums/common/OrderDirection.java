package egov.framework.plms.main.core.model.enums.common;

import org.apache.ibatis.type.MappedTypes;

import egov.framework.plms.main.core.model.enums.CodeEnum;
import egov.framework.plms.main.core.mybatis.CodeEnumTypeHandler;

public enum OrderDirection implements CodeEnum{
	DESC("DESC"),
	ASC("ASC"),
	;
	private final String code;
	
	OrderDirection(String code){
		this.code = code;
	}
	
	@MappedTypes(OrderDirection.class)
    public static class TypeHandler extends CodeEnumTypeHandler<OrderDirection> {
        public TypeHandler() {
            super(OrderDirection.class);
        }
    }

	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return this.code;
	}

}
