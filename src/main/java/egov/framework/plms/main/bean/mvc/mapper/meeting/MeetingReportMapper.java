package egov.framework.plms.main.bean.mvc.mapper.meeting;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingReportVO;
import egov.framework.plms.main.bean.mvc.mapper.meeting.abst.MeetingReportAbstractMapper;
@Mapper
public interface MeetingReportMapper extends MeetingReportAbstractMapper<MeetingReportVO>{
}
