package egov.framework.plms.main.bean.mvc.entity.room.abst;

import egov.framework.plms.main.core.model.able.Convertable;
import egov.framework.plms.main.core.model.able.Filterable;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;

public abstract class RoomInfoModelDTO extends RoomInfoEntity implements Convertable<RoomInfoModelVO>, Filterable<RoomInfoModelDTO> {
	public abstract void setRoomId(Integer value);
	public abstract void setRoomType(RoomType value);
	public abstract void setRoomName(String value);
	public abstract void setRoomLabel(String value);
	public abstract void setRoomNote(String value);
	public abstract void setRoomSize(Integer value);
	public abstract void setRoomFloor(Integer value);
	public abstract void setDisableYN(Character value);
	public abstract void setDisableComment(String value);
	public abstract void setEnable(boolean value);
	
	@Override
	public RoomInfoModelDTO filterForEssential() {
		RoomInfoModelDTO filteredDTO = filterForBasic();
		return filteredDTO;
	}
	
	/**
	 * 다음 값들을 필터링합니다<br>
	 * {@link #disableYN}, {@link #disableComment}, 
	 * <br>{@link #roomNote}
	 */
	@Override
	public RoomInfoModelDTO filterForBasic() {
		RoomInfoModelDTO filteredDTO = filterForDetailed();
		filteredDTO.setDisableYN(null);
		filteredDTO.setDisableComment(null);
		return filteredDTO;
	}

	/**
	 * 다음 값들을 필터링합니다<br>
	 * {@link #roomNote}
	 */
	@Override
	public RoomInfoModelDTO filterForDetailed() {
		RoomInfoModelDTO filteredDTO = filterForSensitive();
		filteredDTO.setRoomNote(null);
		return filteredDTO;
	}

	@Override
	public RoomInfoModelDTO filterForSensitive() {
		RoomInfoModelDTO filteredDTO = filterForHighest();
		return filteredDTO;
	}

	@Override
	public RoomInfoModelDTO filterForHighest() {
		RoomInfoModelDTO filteredDTO = this;
		return filteredDTO;
	}
}
