package egov.framework.plms.main.bean.mvc.mapper.meeting.abst;

import java.util.List;

import egov.framework.plms.main.bean.mvc.entity.common.abst.StatModelVO;

public interface MeetingStatAbstractMapper<T extends StatModelVO> {
	Integer getPaperlessStatForCompany(T params);
	T getMeetingCountAndAverageDurationStatForCompany(T params);
	List<T> getTop5DepartmentWithMeetingForCompany(T params);
	List<T> getMeetingMonthlyTrendForCompany(T params);
	
	Integer getPaperlessStatForPersonal(T params);
	T getMeetingCountAndTotalDurationStatForHosting(T params);
	T getMeetingCountAndTotalDurationStatForAttendance(T params);
	List<T> getTop5DepartmentWithMeetingForOffice(T params);
	List<T> getMeetingMonthlyTrendForPersonal(T params);
}
