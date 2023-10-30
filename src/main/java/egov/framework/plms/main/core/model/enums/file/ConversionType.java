package egov.framework.plms.main.core.model.enums.file;

import org.apache.ibatis.type.MappedTypes;

import egov.framework.plms.main.core.model.enums.NumEnum;
import egov.framework.plms.main.core.mybatis.CodeEnumTypeHandler;


/**
 * 변환 요청 유형<br>
 * <br>
 * {@link #DOC_TO_PDF}<br>
 * {@link #PDF_TO_IMAGES}<br>
 * {@link #IMAGES_TO_PDF}<br>
 * {@link #IMAGE_TO_WEBP}<br>
 * 
 * @author mckim
 * @version 1.0
 * @since 2023. 03. 27
 */
public enum ConversionType implements NumEnum{
	/** {@code DOC_TO_PDF 문서를 PDF로 변환} */
	DOC_TO_PDF(200),
	/** {@code PDF_TO_IMAGES PDF 페이지별 이미지 변환} */
	PDF_TO_IMAGES(210),
	/** {@code IMAGES_TO_PDF 이미지 파일을 PDF로 변환} */
	IMAGES_TO_PDF(220),
	/** {@code IMAGE_TO_WEBP 이미지 파일을 WEBP로 변환} */
	IMAGE_TO_WEBP(230)
	;
	ConversionType(Integer code){
		this.code = code;
	}
	private final Integer code;
	
	@MappedTypes(ConversionType.class)
    public static class TypeHandler extends CodeEnumTypeHandler<ConversionType> {
        public TypeHandler() {
            super(ConversionType.class);
        }
    }
	
	@Override
	public Integer getCode() {
		return code;
	}
	
	public static ConversionType codeOf(Integer code) {
    	return NumEnum.codeOf(ConversionType.class, code);
    }
}
