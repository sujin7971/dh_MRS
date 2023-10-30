package egov.framework.plms.main.core.model.enums.meeting;

import org.apache.ibatis.type.MappedTypes;

import egov.framework.plms.main.core.model.enums.CodeEnum;
import egov.framework.plms.main.core.mybatis.CodeEnumTypeHandler;
import egov.framework.plms.main.core.util.CommUtil;



/**
 * 사용신청 장소에 대한 ENUM 클래스<br><br>
 * {@link #ALL_ROOM} : 전체. URI매핑시 RoomType이 반드시 경로상에 포함되어야 하나 전체 대상을 조건으로 삼고 싶은 경우에 사용.<br>
 * {@link #HALL} : 강당<br>
 * {@link #EDU_ROOM} : 강의실<br>
 * {@link #MEETING_ROOM} : 회의실<br>
 * 
 * @author mckim
 *
 */
public enum RoomType implements CodeEnum{
	/** 
	 * 전체 
	 * @see {@link EwpRoomAssignService#getRoomReqListSearch(egov.framework.plms.sub.ewp.bean.mvc.entity.assign.EwpRoomReqVO)}
	 */
	ALL_ROOM("ALL_ROOM"),
	/** 강당 */
	HALL("HALL"),
	/** 강의실 */
	EDU_ROOM("EDU_ROOM"),
	/** 회의실 */
	MEETING_ROOM("MEETING_ROOM"),
	;
	private final String code;
	
	RoomType(String code) {
		this.code = code;
	}
	@MappedTypes(RoomType.class)
    public static class TypeHandler extends CodeEnumTypeHandler<RoomType> {
        public TypeHandler() {
            super(RoomType.class);
        }
    }
	
	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return code;
	}
	
	public String getRoomName() {
		String name = "";
		switch(this.code) {
			case "EDU_ROOM":
				name = "강의실";
				break;
			case "MEETING_ROOM":
				name = "회의실";
				break;
			case "LECUTRE_HALL":
				name = "강당";
				break;
		}
		return name;
	}
}
