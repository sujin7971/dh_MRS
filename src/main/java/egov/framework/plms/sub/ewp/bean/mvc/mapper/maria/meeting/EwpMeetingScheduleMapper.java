package egov.framework.plms.sub.ewp.bean.mvc.mapper.maria.meeting;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.main.bean.mvc.mapper.meeting.abst.MeetingScheduleAbstractMapper;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingScheduleVO;

@Mapper
public interface EwpMeetingScheduleMapper extends MeetingScheduleAbstractMapper<EwpMeetingScheduleVO>{

}
