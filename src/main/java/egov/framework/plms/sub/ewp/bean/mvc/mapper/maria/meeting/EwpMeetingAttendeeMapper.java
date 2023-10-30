package egov.framework.plms.sub.ewp.bean.mvc.mapper.maria.meeting;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.main.bean.mvc.mapper.meeting.abst.MeetingAttendeeAbstractMapper;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingAttendeeVO;

@Mapper
public interface EwpMeetingAttendeeMapper extends MeetingAttendeeAbstractMapper<EwpMeetingAttendeeVO> {
}
