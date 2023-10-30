package egov.framework.plms.main.bean.mvc.mapper.meeting.abst;

import java.util.List;

import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingApprovalModelVO;

public interface MeetingApprovalAbstractMapper<T extends MeetingApprovalModelVO> {
	Integer insertApprovalOne(T params);
	Integer updateApprovalOne(T params);
	
	T selectApprovalOne(Integer approvalId);
	List<T> selectApprovalList(T params);
	List<T> selectApprovalListForProcessing();
}
