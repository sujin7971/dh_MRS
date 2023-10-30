package egov.framework.plms.sub.ewp.bean.mvc.mapper.maria.meeting;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.main.bean.mvc.mapper.meeting.abst.MeetingReportAbstractMapper;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingReportVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpReportOpinionVO;
@Mapper
public interface EwpMeetingReportMapper extends MeetingReportAbstractMapper<EwpMeetingReportVO>{
	Integer postMeetingReportOpn(EwpReportOpinionVO params);
	Integer deleteMeetingReportOpn(EwpReportOpinionVO params);
	Integer deleteMeetingReportOpnAll(Integer meetingId);
	EwpReportOpinionVO getMeetingReportOpnOne(Integer opnId);
	List<EwpReportOpinionVO> getMeetingReportOpnList(Integer meetingId);
}
