package egov.framework.plms.sub.ewp.bean.mvc.mapper.maria.meeting;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.main.bean.mvc.mapper.meeting.abst.MeetingApprovalAbstractMapper;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingApprovalVO;

@Mapper
public interface EwpMeetingApprovalMapper extends MeetingApprovalAbstractMapper<EwpMeetingApprovalVO>{
}
