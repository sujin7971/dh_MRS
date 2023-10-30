package egov.framework.plms.sub.ewp.core.model.enums;

import org.apache.ibatis.type.MappedTypes;

import egov.framework.plms.main.core.model.enums.CodeEnum;
import egov.framework.plms.main.core.mybatis.CodeEnumTypeHandler;


/**
 * 회의 파일의 사용목적에 따른 분류 유형에 대한 ENUM 클래스<br><br>
 * 
 * {@link #MATERIAL} : 회의 원본자료. 사용자가 업로드 가능한 파일 유형<br>
 * {@link #MEMO} : 메모. 회의 진행중 메모장을 통해 작성한 내용이 있는 경우 회의 종료후 PDF 파일로 생성<br>
 * {@link #COPY} : 판서본. 회의 종료 후 판서본 생성이 가능한 경우 자동 변환하여 사용자에게 제공<br>
 * {@link #REPORT} : 회의록. 회의록 작성 후 최종 등록을 처리한 경우 서버에 PDF 파일로 업로드<br>
 * {@link #PHOTO} : 사진기록. 회의 중 업로드 가능<br>
 * {@link #VOICE} : 음성기록. 회의 중 업로드 가능<br>
 * 
 * @author mckim
 *
 */
public enum FileRole implements CodeEnum{
	/** 원본자료 */
	MATERIAL("MATERIAL"),
	/** 메모 */
	MEMO("MEMO"),
	/** 판서본 */
	COPY("COPY"),
	/** 회의록 */
	REPORT("REPORT"),
	/** 사진 기록 */
	PHOTO("PHOTO"),
	/** 녹음 기록 */
	VOICE("VOICE"),
	;
	private final String code;
	
	FileRole(String code){
		this.code = code;
	}
	
	@MappedTypes(FileRole.class)
    public static class TypeHandler extends CodeEnumTypeHandler<FileRole> {
        public TypeHandler() {
            super(FileRole.class);
        }
    }
	
	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return code;
	}
}
