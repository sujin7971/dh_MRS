package egov.framework.plms.main.bean.mvc.mapper.meeting;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingInfoVO;
import egov.framework.plms.main.bean.mvc.mapper.meeting.abst.MeetingInfoAbstractMapper;
@Mapper
public interface MeetingInfoMapper extends MeetingInfoAbstractMapper<MeetingInfoVO>{

}
