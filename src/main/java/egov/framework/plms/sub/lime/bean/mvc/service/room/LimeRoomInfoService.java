package egov.framework.plms.sub.lime.bean.mvc.service.room;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.mvc.entity.room.RoomInfoVO;
import egov.framework.plms.main.bean.mvc.service.room.abst.RoomInfoAbstractService;
import egov.framework.plms.sub.lime.bean.mvc.mapper.room.LimeRoomInfoMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile("lime")
@Primary
public class LimeRoomInfoService extends RoomInfoAbstractService<RoomInfoVO>{
	public LimeRoomInfoService(@Autowired LimeRoomInfoMapper mapper) {
		super(mapper);
	}
}
