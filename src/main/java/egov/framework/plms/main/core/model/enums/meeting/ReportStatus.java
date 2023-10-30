package egov.framework.plms.main.core.model.enums.meeting;

import org.apache.ibatis.type.MappedTypes;

import egov.framework.plms.main.core.model.enums.NumEnum;
import egov.framework.plms.main.core.mybatis.NumEnumTypeHandler;

/**
 * 회의록의 현재 진행 상태에 대한 ENUM 클래스. 
 * 
 * {@link #NEW} : 신규<br>
 * {@link #OPEN} : 의견수립을 위해 공개<br>
 * {@link #FINISH} : 작성완료<br>
 * @author mckim
 *
 */
public enum ReportStatus implements NumEnum{
	/** 신규(0) */
	NEW(0),
	/** 의견수립을 위해 공개(1) */
	OPEN(1),
	/** 작성완료(2) */
	FINISH(2)
	;
	private final Integer code;

	ReportStatus(Integer code) {
		this.code = code;
	}

	@Override
	public Integer getCode() {
		return code;
	}
	
	@MappedTypes(ReportStatus.class)
    public static class TypeHandler extends NumEnumTypeHandler<ReportStatus> {
        public TypeHandler() {
            super(ReportStatus.class);
        }
    }
	
	public static ReportStatus codeOf(Integer code) {
    	return NumEnum.codeOf(ReportStatus.class, code);
    }
}
