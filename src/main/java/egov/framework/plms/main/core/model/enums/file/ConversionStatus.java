package egov.framework.plms.main.core.model.enums.file;

import org.apache.ibatis.type.MappedTypes;

import egov.framework.plms.main.core.model.enums.CodeEnum;
import egov.framework.plms.main.core.model.enums.NumEnum;
import egov.framework.plms.main.core.model.enums.user.DomainRole;
import egov.framework.plms.main.core.mybatis.NumEnumTypeHandler;

/**
 * 파일 변환 상태 코드. {@link FileInfoVO#conversionStatus} 의 값을 표현하기 위한 ENUM 클래스로 {@link FileConvertVO#cvtStep}와 달리,
 * 변환 프로세스의 전체 상태를 나타내는데 사용됨. 변환이 보류 중인지, 진행 중인지, 완료 되었는지 또는 실패했는지에 대한 더 높은 수준의 보기를 파일 관점으로 제공.
 * 
 * <br>{@link #NOT_STARTED}
 * <br>{@link #DOC_TO_PDF}
 * <br>{@link #PDF_TO_IMAGES}
 * <br>{@link #IMAGE_TO_WEBP}
 * <br>{@link #IMAGES_TO_PDF}
 * <br>{@link #FAILED}
 * 
 * <br>1xx: Conversion not started.
 * <br>2xx: Conversion in progress (different types of conversions).
 * <br>3xx: Conversion success.
 * <br>4xx: Conversion failed.
 * @author mckim
 * @version 1.0
 * @since 2023. 3. 27
 */
public enum ConversionStatus implements NumEnum{
	/** 요청없음 */
	NO_REQUEST(0),
	/** 변환전(100) */
	NOT_STARTED(100),
	/** 문서를 PDF파일로 변환(200) */
	DOC_TO_PDF(200),
	/** PDF파일 페이지별 이미지 생성(210) */
	PDF_TO_IMAGES(210),
	/** 이미지 파일 WEBP로 변환(220) */
	IMAGE_TO_WEBP(220),
	/** 페이지별 이미지 PDF파일로 변환(230) */
	IMAGES_TO_PDF(230),
	/** 변환 완료(300) */
	COMPLETED(300),
	/** 변환 실패(400) */
	FAILED(400)
	;
	private final Integer code;

	ConversionStatus(Integer code) {
		this.code = code;
	}

	@Override
	public Integer getCode() {
		return code;
	}
	
	@MappedTypes(ConversionStatus.class)
    public static class TypeHandler extends NumEnumTypeHandler<ConversionStatus> {
        public TypeHandler() {
            super(ConversionStatus.class);
        }
    }
	
	public static ConversionStatus codeOf(Integer code) {
    	return NumEnum.codeOf(ConversionStatus.class, code);
    }
}
