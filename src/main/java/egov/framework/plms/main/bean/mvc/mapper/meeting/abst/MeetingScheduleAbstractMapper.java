package egov.framework.plms.main.bean.mvc.mapper.meeting.abst;

import java.util.List;
import java.util.Optional;

import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingScheduleModelVO;

public interface MeetingScheduleAbstractMapper<T extends MeetingScheduleModelVO> {
	Integer postMeetingSchedule(T params);
	Integer putMeetingSchedule(T params);
	Integer deleteMeetingScheduleOne(Integer scheduleId);
	T getMeetingScheduleOne(Integer scheduleId);
	T getMeetingScheduleNextOne(Integer scheduleId);
	T getAssignedMeetingScheduleOne(Integer meetingKey);
	List<T> getMeetingScheduleList(T params);
}
