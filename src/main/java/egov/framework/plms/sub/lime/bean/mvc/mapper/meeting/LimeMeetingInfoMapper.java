package egov.framework.plms.sub.lime.bean.mvc.mapper.meeting;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingInfoVO;
import egov.framework.plms.main.bean.mvc.mapper.meeting.abst.MeetingInfoAbstractMapper;
@Mapper
public interface LimeMeetingInfoMapper extends MeetingInfoAbstractMapper<MeetingInfoVO>{

}
