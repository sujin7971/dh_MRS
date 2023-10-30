package egov.framework.plms.main.bean.mvc.service.meeting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingApprovalVO;
import egov.framework.plms.main.bean.mvc.mapper.meeting.MeetingApprovalMapper;
import egov.framework.plms.main.bean.mvc.service.meeting.abst.MeetingApprovalAbstractService;
import egov.framework.plms.main.core.model.enums.meeting.ApprovalStatus;

@Service("MeetingApprovalService")
public class MeetingApprovalService extends MeetingApprovalAbstractService<MeetingApprovalVO>{
	public MeetingApprovalService(@Autowired MeetingApprovalMapper mapper) {
		super(mapper);
	}
	
	public boolean insertApprovalOne(String requesterId, Integer scheduleId, ApprovalStatus approvalStatus) {
		return insertApprovalOne(MeetingApprovalVO.builder().requesterId(requesterId).scheduleId(scheduleId).approvalStatus(approvalStatus).build());
	}
	
	public boolean insertApprovalOne(String requesterId, Integer scheduleId, ApprovalStatus approvalStatus, String approvalComment) {
		return insertApprovalOne(MeetingApprovalVO.builder().requesterId(requesterId).scheduleId(scheduleId).approvalStatus(approvalStatus).approvalComment(approvalComment).build());
	}
	
	public boolean updateApprovalOne(Integer approvalId, Character approvedYN) {
		return updateApprovalOne(MeetingApprovalVO.builder().approvalId(approvalId).approvedYN(approvedYN).build());
	}
	
	public boolean updateApprovalOne(Integer approvalId, Character approvedYN, String approvalComment) {
		return updateApprovalOne(MeetingApprovalVO.builder().approvalId(approvalId).approvedYN(approvedYN).approvalComment(approvalComment).build());
	}
}
