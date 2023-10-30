package egov.framework.plms.sub.lime.bean.mvc.mapper.meeting;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingAttendeeVO;
import egov.framework.plms.main.bean.mvc.mapper.meeting.abst.MeetingAttendeeAbstractMapper;

@Mapper
public interface LimeMeetingAttendeeMapper extends MeetingAttendeeAbstractMapper<MeetingAttendeeVO> {
}
