package egov.framework.plms.main.core.model.enums.file;

import org.apache.ibatis.type.MappedTypes;

import egov.framework.plms.main.core.model.enums.NumEnum;
import egov.framework.plms.main.core.mybatis.NumEnumTypeHandler;

/**
 * 파일 물리적 상태 코드
 * 
 * <br>{@link #EXISTS}
 * <br>{@link #MISSING}
 * <br>{@link #PASSWORD_PROTECTED}
 * <br>{@link #DRM_PROTECTED}
 * <br>{@link #DELETED_BY_LOGIC}
 * <br>{@link #DELETED_BY_EXPIRATION}
 * 
 * <br>1xx: File available and accessible.
 * <br>2xx: File missing or not uploaded.
 * <br>3xx: File encrypted or DRM protected.
 * <br>4xx: File deleted.
 * @author mckim
 * @version 1.0
 * @since 2023. 3. 27
 */
public enum FileStatus implements NumEnum{
	/** 디스크에 저장됨(100) */
	EXISTS(100),
	/** 디스크에서 누락됨(200) */
	FILE_MISSING(200),
	/** 디스크에 파일이 아직 할당되지 않음(210) */
	FILE_PENDING(210),
	/** 암호가 설정 되어 있음(300) */
	PASSWORD_PROTECTED(300),
	/** DRM으로 암호화 되어 있음(310) */
	DRM_PROTECTED(310),
	/** 내부 로직으로 인해 삭제됨(400) */
	DELETED_BY_LOGIC(400),
	/** 기간 만료로 삭제됨(410) */
	DELETED_BY_EXPIRATION(410)
	;
	private final Integer code;

	FileStatus(Integer code) {
		this.code = code;
	}

	@Override
	public Integer getCode() {
		return code;
	}
	
	@MappedTypes(FileStatus.class)
    public static class TypeHandler extends NumEnumTypeHandler<FileStatus> {
        public TypeHandler() {
            super(FileStatus.class);
        }
    }
	
	public static FileStatus codeOf(Integer code) {
    	return NumEnum.codeOf(FileStatus.class, code);
    }
}
