package egov.framework.plms.main.bean.mvc.entity.meeting.abst;

import java.time.LocalDateTime;

import egov.framework.plms.main.core.model.enums.meeting.ApprovalStatus;

public abstract class MeetingApprovalEntity {
	public abstract Integer getApprovalId();
	public abstract String getRequesterId();
	public abstract String getDecisionMakerId();
	public abstract Integer getScheduleId();
	public abstract ApprovalStatus getApprovalStatus();
	public abstract String getApprovalComment();
	public abstract Character getApprovedYN();
	public abstract LocalDateTime getRequestDateTime();
	public abstract LocalDateTime getProcessDateTime();
}
