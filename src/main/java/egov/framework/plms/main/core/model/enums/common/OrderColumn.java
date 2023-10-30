package egov.framework.plms.main.core.model.enums.common;

import org.apache.ibatis.type.MappedTypes;

import egov.framework.plms.main.core.model.enums.CodeEnum;
import egov.framework.plms.main.core.mybatis.CodeEnumTypeHandler;

public enum OrderColumn implements CodeEnum{
	등록일시("regDateTime"),
	시작일시("beginDateTime"),
	;
	private final String code;
	
	OrderColumn(String code){
		this.code = code;
	}
	
	@MappedTypes(OrderColumn.class)
    public static class TypeHandler extends CodeEnumTypeHandler<OrderColumn> {
        public TypeHandler() {
            super(OrderColumn.class);
        }
    }

	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return this.code;
	}

}
