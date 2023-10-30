package egov.framework.plms.sub.ewp.bean.mvc.mapper.maria.meeting;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.main.bean.mvc.mapper.meeting.abst.MeetingAssignAbstractMapper;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingArchiveVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingAssignVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingAttendeeVO;

@Mapper
public interface EwpMeetingAssignMapper extends MeetingAssignAbstractMapper<EwpMeetingAssignVO>{
	List<EwpMeetingArchiveVO> getMeetingArchiveList(EwpMeetingArchiveVO param);
	List<EwpMeetingAssignVO> getMeetingAssignListForApproval(EwpMeetingAssignVO params);
}
