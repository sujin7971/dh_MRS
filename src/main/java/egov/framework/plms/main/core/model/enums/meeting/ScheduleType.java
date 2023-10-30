package egov.framework.plms.main.core.model.enums.meeting;

import org.apache.ibatis.type.MappedTypes;

import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingInfoVO;
import egov.framework.plms.main.core.model.enums.CodeEnum;
import egov.framework.plms.main.core.mybatis.CodeEnumTypeHandler;

/**
 * 사용신청 성격에 대한 ENUM 클래스<br><br>
 * {@link #FORMAL} : 정규 스케줄(전자회의). {@link MeetingInfoVO#elecYN}의 값이<code>'Y'<br>
 * {@link #INSTANT} : 즉석 스케줄(전자회의). {@link MeetingInfoVO#elecYN}의 값이<code>'Y'<br>
 * {@link #RENTAL} : 장소대여(일반회의). {@link MeetingInfoVO#elecYN}의 값이<code>'N'</code><br>
 * 
 * @author mckim
 *
 */
public enum ScheduleType implements CodeEnum{
	/** 정규 스케줄(FORMAL) */
	FORMAL("FORMAL"),
	/** 즉석 스케줄(INSTANT) */
	INSTANT("INSTANT"),
	/** 장소대여(RENTAL) */
	RENTAL("RENTAL"),
	;
	private final String code;
	
	ScheduleType(String code) {
		this.code = code;
	}
	
	@MappedTypes(ScheduleType.class)
    public static class TypeHandler extends CodeEnumTypeHandler<ScheduleType> {
        public TypeHandler() {
            super(ScheduleType.class);
        }
    }
	
	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return code;
	}
}
