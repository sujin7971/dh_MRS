package egov.framework.plms.main.core.model.enums.file;

import org.apache.ibatis.type.MappedTypes;

import egov.framework.plms.main.core.model.enums.CodeEnum;
import egov.framework.plms.main.core.mybatis.CodeEnumTypeHandler;


/**
 * 파일 유형별 발급될 MIME에 대한 ENUM
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 */
public enum FileCategory implements CodeEnum{
	/** {@code UNKNOWN 알 수 없는 파일} */
	UNKNOWN("UNKNOWN"),
	/** {@code PDF PDF 파일} */
	PDF("PDF"),
	/** {@code HWP 한글 문서 파일} */
	HWP("HWP"),
	/** {@code IMG 이미지 파일} */
	IMG("IMG"),
	/** {@code EXCEL 엑셀 파일} */
	EXCEL("EXCEL"),
	/** {@code PPT 파워포인트 파일} */
	PPT("PPT"),
	/** {@code WORD 워드 파일} */
	WORD("WORD"),
	/** {@code AUDIO 음성 파일} */
	AUDIO("AUDIO")
	;
	FileCategory(String code){
		this.code = code;
	}
	private final String code;
	
	@MappedTypes(FileCategory.class)
    public static class TypeHandler extends CodeEnumTypeHandler<FileCategory> {
        public TypeHandler() {
            super(FileCategory.class);
        }
    }
	
	@Override
	public String getCode() {
		return code;
	}
}
