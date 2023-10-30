package egov.framework.plms.sub.lime.bean.mvc.mapper.room;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.main.bean.mvc.entity.room.RoomInfoVO;
import egov.framework.plms.main.bean.mvc.mapper.room.abst.RoomInfoAbstractMapper;

@Mapper
public interface LimeRoomInfoMapper extends RoomInfoAbstractMapper<RoomInfoVO>{
}
