package egov.framework.plms.main.bean.mvc.mapper.meeting.abst;

import java.util.List;

import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingAttendeeModelVO;


public interface MeetingAttendeeAbstractMapper<T extends MeetingAttendeeModelVO> {
	Integer postMeetingAttendee(T attendee);
	Integer putMeetingAttendee(T attendee);
	Integer deleteMeetingAttendeeOne(Integer attendId);
	Integer deleteMeetingAttendeeList(T attendee);
	
	T getMeetingAttendeeOne(Integer attendId);
	Integer getAttendId(T params);
	List<T> getMeetingAttendeeList(T params);
}
