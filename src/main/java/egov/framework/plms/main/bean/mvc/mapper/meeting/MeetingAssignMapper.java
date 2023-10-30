package egov.framework.plms.main.bean.mvc.mapper.meeting;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingAssignVO;
import egov.framework.plms.main.bean.mvc.mapper.meeting.abst.MeetingAssignAbstractMapper;

@Mapper
public interface MeetingAssignMapper extends MeetingAssignAbstractMapper<MeetingAssignVO>{

}
