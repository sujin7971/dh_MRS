package egov.framework.plms.main.core.model.enums.user;

import org.apache.ibatis.type.MappedTypes;

import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import egov.framework.plms.main.core.mybatis.CodeEnumTypeHandler;
import egov.framework.plms.sub.ewp.core.model.enums.SeqMenu;
import lombok.AllArgsConstructor;
import lombok.Getter;
/**
 * 동서발전 경영품목별 담당자에 대한 역할 ENUM<br>
 * 
 * {@link #MEETING_ROOM_MANAGER} : 회의실 품목 담당자<br>
 * {@link #EDU_ROOM_MANAGER} : 강의실 품목 담당자<br>
 * {@link #HALL_MANAGER} : 강당 품목 담당자<br>
 * 
 * @author mckim
 *
 */
@AllArgsConstructor
@Getter
public enum ManagerRole implements PositionRole{
	/** 결재 담당자 */
	APPROVAL_MANAGER("MNG_APPROVAL"), 
	/** 회의실 담당자 */
	MEETING_ROOM_MANAGER("MNG_ITEM_MR"),
	/** 강의실 담당자 */
	EDU_ROOM_MANAGER("MNG_ITEM_ER"),
	/** 강당 담당자 */
	HALL_MANAGER("MNG_ITEM_HALL"),
	/** 사용신청 담당자 */
	REQUEST_MANAGER("MNG_REQUEST_ROOM");
    private final String code;

    @MappedTypes(ManagerRole.class)
    public static class TypeHandler extends CodeEnumTypeHandler<ManagerRole> {
        public TypeHandler() {
            super(ManagerRole.class);
        }
    }

    @Override
    public String getCode() {
        return code;
    }
    
    public static ManagerRole getRoomManagerRole(RoomType roomType) {
		switch(roomType) {
			case MEETING_ROOM: return ManagerRole.MEETING_ROOM_MANAGER;
			case EDU_ROOM: return ManagerRole.EDU_ROOM_MANAGER;
			case HALL: return ManagerRole.HALL_MANAGER;
			default: return null;
		}
    }
}
