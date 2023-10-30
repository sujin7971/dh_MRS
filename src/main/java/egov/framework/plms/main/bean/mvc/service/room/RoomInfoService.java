package egov.framework.plms.main.bean.mvc.service.room;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.mvc.entity.room.RoomInfoVO;
import egov.framework.plms.main.bean.mvc.mapper.room.RoomInfoMapper;
import egov.framework.plms.main.bean.mvc.service.room.abst.RoomInfoAbstractService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("RoomInfoService")
public class RoomInfoService extends RoomInfoAbstractService<RoomInfoVO>{
	public RoomInfoService(@Autowired RoomInfoMapper mapper) {
		super(mapper);
	}
}
