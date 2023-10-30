package egov.framework.plms.main.core.model.enums.resource;

import org.apache.ibatis.type.MappedTypes;

import egov.framework.plms.main.core.model.enums.CodeEnum;
import egov.framework.plms.main.core.mybatis.CodeEnumTypeHandler;
import lombok.AllArgsConstructor;


/**
 * 사용자 리포트의 처리에 따른 분류 ENUM. 현재 사용하지 않음
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 */
@AllArgsConstructor
@Deprecated
public enum ReportStep implements CodeEnum {
	/** 요청 */
	REQ("REQ"),
	/** 접수 */
	RCVD("RCVD"),
	/** 종료 */
	FIN("FIN"),
	/** 보류 */
	HOLD("HOLD")
	;
	private final String code;
	
	@MappedTypes(ReportStep.class)
    public static class TypeHandler extends CodeEnumTypeHandler<ReportStep> {
        public TypeHandler() {
            super(ReportStep.class);
        }
    }
	
	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return code;
	}
}
