package egov.framework.plms.main.bean.mvc.mapper.meeting;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.main.bean.mvc.entity.common.StatVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingAssignVO;
import egov.framework.plms.main.bean.mvc.mapper.meeting.abst.MeetingAssignAbstractMapper;
import egov.framework.plms.main.bean.mvc.mapper.meeting.abst.MeetingStatAbstractMapper;

@Mapper
public interface MeetingStatMapper extends MeetingStatAbstractMapper<StatVO>{
}
