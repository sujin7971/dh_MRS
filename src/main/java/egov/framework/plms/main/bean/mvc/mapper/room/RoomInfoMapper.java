package egov.framework.plms.main.bean.mvc.mapper.room;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.main.bean.mvc.entity.room.RoomInfoVO;
import egov.framework.plms.main.bean.mvc.mapper.room.abst.RoomInfoAbstractMapper;

@Mapper
public interface RoomInfoMapper extends RoomInfoAbstractMapper<RoomInfoVO>{

}
