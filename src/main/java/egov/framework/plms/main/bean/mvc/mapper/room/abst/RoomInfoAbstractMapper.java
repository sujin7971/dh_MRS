package egov.framework.plms.main.bean.mvc.mapper.room.abst;

import java.util.List;

import egov.framework.plms.main.bean.mvc.entity.room.abst.RoomInfoModelVO;

public interface RoomInfoAbstractMapper<T extends RoomInfoModelVO> {
	Integer insertRoomOne(T params);
	Integer updateRoomOne(T params);
	Integer updateRoomOneToDelete(Integer roomId);
	T selectRoomOne(Integer roomId);
	List<T> selectRoomList(T params);
}
