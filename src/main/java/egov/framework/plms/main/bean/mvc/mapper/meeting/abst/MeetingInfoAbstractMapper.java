package egov.framework.plms.main.bean.mvc.mapper.meeting.abst;

import java.util.List;

import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingInfoModelVO;

public interface MeetingInfoAbstractMapper<T extends MeetingInfoModelVO> {
	Integer postMeetingInfo(T meeting);
	Integer putMeetingInfo(T meeting);
	Integer putMeetingStickyBit(T meeting);
	Integer deleteMeetingInfo(Integer meetingId);
	Integer getMeetingStickyBit(Integer meetingId);
	T getMeetingInfoOne(Integer meetingId);
	List<T> getMeetingInfoList(T meeting);
}
