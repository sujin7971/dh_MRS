package egov.framework.plms.sub.lime.bean.mvc.mapper.meeting;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingArchiveVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingAssignVO;
import egov.framework.plms.main.bean.mvc.mapper.meeting.abst.MeetingAssignAbstractMapper;

@Mapper
public interface LimeMeetingAssignMapper extends MeetingAssignAbstractMapper<MeetingAssignVO>{
	
}
