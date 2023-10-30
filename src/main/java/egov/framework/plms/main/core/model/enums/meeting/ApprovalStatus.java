package egov.framework.plms.main.core.model.enums.meeting;

import org.apache.ibatis.type.MappedTypes;

import egov.framework.plms.main.core.model.enums.NumEnum;
import egov.framework.plms.main.core.model.enums.file.RelationType;
import egov.framework.plms.main.core.mybatis.NumEnumTypeHandler;

/**
 * 사용신청(스케줄)의 현재 결재 상태에 대한 ENUM 클래스. 
 * 동서발전은 {@link #REQUEST}, {@link #APPROVED}, {@link #CANCELED}, {@link #REJECTED}만 사용됨<br><br>
 * 
 * {@link #DELETE} : 삭제<br>
 * {@link #NEW} : 신규<br>
 * {@link #REQUEST} : 승인신청<br>
 * {@link #APPROVED} : 승인됨<br>
 * {@link #CANCELED} : 취소됨<br>
 * {@link #REJECTED} : 승인 거절됨<br>
 * @author mckim
 *
 */
public enum ApprovalStatus implements NumEnum{
	/** 삭제(-1) */
	DELETE(-1),
	/** 신규(0) */
	NEW(0),
	/** 승인신청(1) */
	REQUEST(1),
	/** 승인됨(2) */
	APPROVED(2),
	/** 취소됨(3) */
	CANCELED(3),
	/** 승인 거절됨(4) */
	REJECTED(4)
	;
	private final Integer code;

	ApprovalStatus(Integer code) {
		this.code = code;
	}

	@Override
	public Integer getCode() {
		return code;
	}
	
	@MappedTypes(ApprovalStatus.class)
    public static class TypeHandler extends NumEnumTypeHandler<ApprovalStatus> {
        public TypeHandler() {
            super(ApprovalStatus.class);
        }
    }
	
	public static ApprovalStatus codeOf(Integer code) {
    	return NumEnum.codeOf(ApprovalStatus.class, code);
    }
	/**
	 * 사용신청 결재 상태에 대한 명칭 조회
	 * @return
	 */
	public String getName() {
		String[] namearr = new String[] {"삭제", "신규", "승인신청", "승인됨", "취소됨", "승인 거절됨"};
		return namearr[this.code + 1];
	}
	
	public MeetingStatus getMeetingStatus() {
		switch(this) {
			case REQUEST:
				return MeetingStatus.UNAPPROVAL;
			case APPROVED:
				return MeetingStatus.APPROVED;
			case CANCELED:
				return MeetingStatus.CANCEL;
			case REJECTED:
				return MeetingStatus.DROP;
			default:
				return MeetingStatus.NEW;
		}
	}
}
