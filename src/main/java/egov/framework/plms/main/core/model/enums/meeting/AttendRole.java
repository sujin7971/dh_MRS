package egov.framework.plms.main.core.model.enums.meeting;

import org.apache.ibatis.type.MappedTypes;

import egov.framework.plms.main.core.model.enums.user.UserRole;
import egov.framework.plms.main.core.mybatis.CodeEnumTypeHandler;

/**
 * 참석자 유형 ENUM 클래스<br><br>
 * 
 * {@link #HOST} : 회의 진행자<br>
 * {@link #WRITER} : 회의 작성자<br>
 * {@link #ASSISTANT} : 회의 보조진행자<br>
 * {@link #ATTENDEE} : 회의 참석자<br>
 * {@link #OBSERVER} : 회의 참관자<br>
 * {@link #GUEST} : 외부 참석자<br>
 * 
 * @author mckim
 *
 */
public enum AttendRole implements UserRole{
	HOST("HOST"),
	FACILITATOR("FACILITATOR"),
	WRITER("WRITER"),
	ASSISTANT("ASSISTANT"),
	ATTENDEE("ATTENDEE"),
	OBSERVER("OBSERVER"),
	GUEST("GUEST"),
	;
	private final String code;
	
	AttendRole(String code){
		this.code = code;
	}
	
	@MappedTypes(AttendRole.class)
    public static class TypeHandler extends CodeEnumTypeHandler<AttendRole> {
        public TypeHandler() {
            super(AttendRole.class);
        }
    }
	
	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return code;
	}

}
