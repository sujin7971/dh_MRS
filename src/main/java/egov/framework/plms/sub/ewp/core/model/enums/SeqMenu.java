package egov.framework.plms.sub.ewp.core.model.enums;

import org.apache.ibatis.type.MappedTypes;

import egov.framework.plms.main.core.model.enums.NumEnum;
import egov.framework.plms.main.core.mybatis.NumEnumTypeHandler;

/**
 * 동서발전 품목 코드
 * 
 * {@link #MEETING_ROOM_MANAGER} : 회의실 품목<br>
 * {@link #EDU_ROOM_MANAGER} : 강의실 품목<br>
 * {@link #HALL_MANAGER} : 감당 품목<br>
 * @author mckim
 * @version 1.0
 * @since 2023. 1. 12
 */
public enum SeqMenu implements NumEnum{
	/** 회의실 품목(30) */
	ITEM_MEETING_ROOM(30),
	/** 강의실 품목(31) */
	ITEM_EDU_ROOM(31),
	/** 감당 품목(32) */
	ITEM_HALL(32)
	;
	private final Integer code;

	SeqMenu(Integer code) {
		this.code = code;
	}

	@Override
	public Integer getCode() {
		return code;
	}
	
	@MappedTypes(SeqMenu.class)
    public static class TypeHandler extends NumEnumTypeHandler<SeqMenu> {
        public TypeHandler() {
            super(SeqMenu.class);
        }
    }
	
	public static SeqMenu valueOf(Integer num) {
		for(SeqMenu status: SeqMenu.values()) {
			if(status.getCode().equals(num)) {
				return status;
			}
		}
		return null;
	}
}
