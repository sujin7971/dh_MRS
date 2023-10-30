package egov.framework.plms.main.core.model.enums.file;

import org.apache.ibatis.type.MappedTypes;

import egov.framework.plms.main.core.model.enums.NumEnum;
import egov.framework.plms.main.core.mybatis.NumEnumTypeHandler;


/**
 * 
 * 
 * @author mckim
 *
 */

/**
 * 자원에 속한 파일의 사용목적에 따른 분류 유형에 대한 ENUM 클래스<br>
 * <br>
 * {@link #MEETING_MATERIAL} : 회의 원본자료. 사용자가 업로드 가능한 파일 유형
 * {@link #MEETING_MEMO} : 메모. 회의 진행중 메모장을 통해 작성한 내용이 있는 경우 회의 종료후 PDF 파일로 생성<br>
 * {@link #MEETING_COPY} : 판서본. 회의 종료 후 판서본 생성이 가능한 경우 자동 변환하여 사용자에게 제공<br>
 * {@link #MEETING_REPORT} : 회의록. 회의록 작성 후 최종 등록을 처리한 경우 서버에 PDF 파일로 업로드<br>
 * {@link #MEETING_PHOTO} : 사진기록. 회의 중 업로드 가능<br>
 * {@link #MEETING_VOICE} : 음성기록. 회의 중 업로드 가능<br>
 * {@link #BOARD_ATTACHMENT} : 게시판 첨부파일<br>
 * @author mckim
 * @version 1.0
 * @since 2023. 3. 27
 */
public enum RelationType implements NumEnum{
	/** 원본자료 */
	MEETING_MATERIAL(100),
	/** 메모 */
	MEETING_MEMO(101),
	/** 판서본 */
	MEETING_COPY(102),
	/** 회의록 */
	MEETING_REPORT(103),
	/** 사진 기록 */
	MEETING_PHOTO(104),
	/** 녹음 기록 */
	MEETING_VOICE(105),
	/** 게시판 첨부파일 */
	BOARD_ATTACHMENT(200),
	;
	private final Integer code;
	
	RelationType(Integer code){
		this.code = code;
	}
	
	@MappedTypes(RelationType.class)
    public static class TypeHandler extends NumEnumTypeHandler<RelationType> {
        public TypeHandler() {
            super(RelationType.class);
        }
    }
	
	@Override
	public Integer getCode() {
		// TODO Auto-generated method stub
		return code;
	}
	
	public static RelationType codeOf(Integer code) {
    	return NumEnum.codeOf(RelationType.class, code);
    }
}
