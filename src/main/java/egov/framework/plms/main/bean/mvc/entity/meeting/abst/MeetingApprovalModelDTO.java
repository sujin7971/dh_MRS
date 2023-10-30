package egov.framework.plms.main.bean.mvc.entity.meeting.abst;

import java.time.LocalDateTime;

import egov.framework.plms.main.core.model.able.Convertable;
import egov.framework.plms.main.core.model.enums.meeting.ApprovalStatus;

public abstract class MeetingApprovalModelDTO extends MeetingApprovalEntity implements Convertable<MeetingApprovalModelVO> {
	public abstract void setApprovalId(Integer value);
	public abstract void setRequesterId(String value);
	public abstract void setDecisionMakerId(String value);
	public abstract void setScheduleId(Integer value);
	public abstract void setApprovalStatus(ApprovalStatus value);
	public abstract void setApprovalComment(String value);
	public abstract void setApprovedYN(Character value);
	public abstract void setRequestDateTime(LocalDateTime value);
	public abstract void setProcessDateTime(LocalDateTime value);
}
