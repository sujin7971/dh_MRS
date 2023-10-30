package egov.framework.plms.main.bean.mvc.mapper.meeting;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingApprovalVO;
import egov.framework.plms.main.bean.mvc.mapper.meeting.abst.MeetingApprovalAbstractMapper;

@Mapper
public interface MeetingApprovalMapper extends MeetingApprovalAbstractMapper<MeetingApprovalVO>{
}
