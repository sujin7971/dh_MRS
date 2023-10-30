package egov.framework.plms.main.bean.mvc.entity.room.abst;

import egov.framework.plms.main.core.model.enums.meeting.RoomType;

/**
 * 장소 객체
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 */
public abstract class RoomInfoEntity {
	public abstract RoomType getRoomType();
	public abstract Integer getRoomId();
	public abstract String getRoomName();
	public abstract String getRoomLabel();
	public abstract String getRoomNote();
	public abstract Integer getRoomSize();
	public abstract Integer getRoomFloor();
	public abstract Character getDisableYN();
	public abstract String getDisableComment();
	public abstract boolean isEnable();
}
