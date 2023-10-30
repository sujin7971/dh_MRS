package egov.framework.plms.sub.lime.bean.mvc.mapper.meeting;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingReportVO;
import egov.framework.plms.main.bean.mvc.mapper.meeting.abst.MeetingReportAbstractMapper;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpReportOpinionVO;
@Mapper
public interface LimeMeetingReportMapper extends MeetingReportAbstractMapper<MeetingReportVO>{
}
