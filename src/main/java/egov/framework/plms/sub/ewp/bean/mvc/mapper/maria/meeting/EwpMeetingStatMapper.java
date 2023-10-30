package egov.framework.plms.sub.ewp.bean.mvc.mapper.maria.meeting;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.main.bean.mvc.entity.common.StatVO;
import egov.framework.plms.main.bean.mvc.mapper.meeting.abst.MeetingStatAbstractMapper;

@Mapper
public interface EwpMeetingStatMapper extends MeetingStatAbstractMapper<StatVO>{
}
