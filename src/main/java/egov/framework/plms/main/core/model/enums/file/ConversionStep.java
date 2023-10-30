package egov.framework.plms.main.core.model.enums.file;

import org.apache.ibatis.type.MappedTypes;

import egov.framework.plms.main.core.model.enums.NumEnum;
import egov.framework.plms.main.core.mybatis.NumEnumTypeHandler;

/**
 * {@link FileConvertVO#cvtStep}의 값을 표현하기 위한 ENUM 클래스로 {@link FileInfoVO#conversionStatus}와 달리,
 * 요청된 변환 프로세스의 특정 단계에 초점을 맞춰 각 변환 단계를 추적하는데 사용된다. 요청이 보류중인지, 대기열 또는 진행중인지, 변환 결과가 완료 혹은 실패인지를 표시한다.
 * 
 * <br>{@link #NOT_QUEUED}
 * <br>{@link #QUEUED_OR_PROCESSING}
 * <br>{@link #SUCCEEDED}
 * <br>{@link #FAILED}
 * 
 * @author mckim
 * @version 1.0
 * @since 2023. 3. 28
 */
public enum ConversionStep implements NumEnum{
	/** 변환 요청이 등록되었지만 변환 대기중이 아님(0) */
	NOT_QUEUED(0),
	/** 요청이 변환 대기열에 추가되었거나 변환중임(1) */
	QUEUED_OR_PROCESSING(1),
	/** 변환 요청 처리 성공(2) */
	SUCCEEDED(2),
	/** 변환 요청 처리 실패(-1) */
	FAILED(-1)
	;
	private final Integer code;

	ConversionStep(Integer code) {
		this.code = code;
	}

	@Override
	public Integer getCode() {
		return code;
	}
	
	@MappedTypes(ConversionStep.class)
    public static class TypeHandler extends NumEnumTypeHandler<ConversionStep> {
        public TypeHandler() {
            super(ConversionStep.class);
        }
    }
	
	public static ConversionStep codeOf(Integer code) {
    	return NumEnum.codeOf(ConversionStep.class, code);
    }
}
