package egov.framework.plms.sub.lime.bean.mvc.mapper.meeting;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingScheduleVO;
import egov.framework.plms.main.bean.mvc.mapper.meeting.abst.MeetingScheduleAbstractMapper;

@Mapper
public interface LimeMeetingScheduleMapper extends MeetingScheduleAbstractMapper<MeetingScheduleVO>{

}
