package egov.framework.plms.main.core.model.enums.meeting;

import org.apache.ibatis.type.MappedTypes;

import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingScheduleVO;
import egov.framework.plms.main.core.model.enums.CodeEnum;
import egov.framework.plms.main.core.mybatis.CodeEnumTypeHandler;
import lombok.AllArgsConstructor;

/**
 * 회의 상태값 ENUM 클래스<br>
 * 해당 사용신청(스케줄) 결재/상태 값 {@link MeetingScheduleVO#ApprovalStatus}이 {@link ApprovalStatus#CANCELED} 또는 {@link ApprovalStatus#REJECTED}인 경우 회의 상태값을 {@link #DROP}으로 변경<br><br>
 * 
 * {@link #NEW} : 등록 <br>
 * {@link #UNAPPROVAL} : 미승인<br>
 * {@link #APPROVED} : 승인됨<br>
 * {@link #DROP} : 드랍<br>
 * {@link #OPENING} : 개시<br>
 * {@link #START} : 시작 <br>
 * {@link #FINISH} : 종료<br>
 * {@link #CLOSING} : 마감<br>
 * {@link #END} : 끝남<br>
 * 
 * @author mckim
 *
 */
public enum MeetingStatus implements CodeEnum{
	/** 
	 * <code>등록</code><br>
	 * 회의가 새로 등록됨. 필요한 절차가 완료되면 미승인 단계로 넘어가 관리자 승인을 기다림.
	 */
	NEW("NEW"),
	/** 
	 * <code>미승인</code><br>
	 * 회의 예약 신청후 관리자 승인 전까지 회의 상태
	 */
	UNAPPROVAL("UNAPPROVAL"),
	/**
	 * <code>승인됨</code><br>
	 * 관리자로부터 회의 신청이 승인된 경우 회의 상태
	 */
	APPROVED("APPROVED"),
	/**
	 * <code>드랍</code><br>
	 * 회의가 취소/승인거절 등으로 인해 드랍
	 */
	DROP("DROP"),
	/**
	 * <code>취소</code><br>
	 * 회의가 취소된 경우
	 */
	CANCEL("CANCEL"),
	/**
	 * <code>개시</code><br>
	 * 회의 페이지로 참석 가능한 상태. 현재는 회의 시작 10분 전부터.
	 */
	OPENING("OPENING"),
	/**
	 * <code>시작</code><br>
	 * 회의가 시작됨. 예약 시작시간부터 종료시간전까지 유지.
	 */
	START("START"),
	/**
	 * <code>종료</code><br>
	 * 회의가 종료됨. 마감처리 제한시간 전까지 유지.
	 */
	FINISH("FINISH"),
	/**
	 * <code>마감</code><br>
	 * 마감 제한시간이 지난 경우 회의 상태
	 */
	CLOSING("CLOSING"),
	/**
	 * <code>끝남</code><br>
	 * 마감 상태에서 필요한 모든 조치들이 끝난 경우 회의 상태
	 */
	END("END")
	;
	private final String code;
	MeetingStatus(String code) {
		this.code = code;
	}
	@MappedTypes(MeetingStatus.class)
    public static class TypeHandler extends CodeEnumTypeHandler<MeetingStatus> {
        public TypeHandler() {
            super(MeetingStatus.class);
        }
    }
	
	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return code;
	}
}
