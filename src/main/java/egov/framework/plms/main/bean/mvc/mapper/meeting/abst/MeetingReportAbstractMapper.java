package egov.framework.plms.main.bean.mvc.mapper.meeting.abst;

import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingReportModelVO;

public interface MeetingReportAbstractMapper<T extends MeetingReportModelVO> {
	Integer postMeetingReport(T report);
	Integer putMeetingReport(T report);
	Integer deleteMeetingReport(Integer meetingId);
	T getMeetingReportOne(Integer meetingId);
	String getMeetingReportContents(Integer meetingId);
}
