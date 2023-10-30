package egov.framework.plms.sub.lime.bean.mvc.mapper.meeting;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.main.bean.mvc.entity.common.StatVO;
import egov.framework.plms.main.bean.mvc.mapper.meeting.abst.MeetingStatAbstractMapper;

@Mapper
public interface LimeMeetingStatMapper extends MeetingStatAbstractMapper<StatVO>{
}
