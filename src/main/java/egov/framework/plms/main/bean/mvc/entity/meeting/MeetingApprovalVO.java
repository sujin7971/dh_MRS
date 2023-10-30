package egov.framework.plms.main.bean.mvc.entity.meeting;

import java.time.LocalDateTime;

import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingApprovalModelVO;
import egov.framework.plms.main.core.model.enums.meeting.ApprovalStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class MeetingApprovalVO extends MeetingApprovalModelVO{
	private Integer approvalId;
	private String requesterId;
	private String decisionMakerId;
	private Integer scheduleId;
	private ApprovalStatus approvalStatus;
	private String approvalComment;
	private Character approvedYN;
	private LocalDateTime requestDateTime;
	private LocalDateTime processDateTime;
	
	@Builder(builderClassName = "init", builderMethodName = "initVO")
	public MeetingApprovalVO(MeetingApprovalDTO dto){
		this.approvalId = dto.getApprovalId();
		this.requesterId = dto.getRequesterId();
		this.decisionMakerId = dto.getDecisionMakerId();
		this.scheduleId = dto.getScheduleId();
		this.approvalStatus = dto.getApprovalStatus();
		this.approvalComment = dto.getApprovalComment();
		this.approvedYN = dto.getApprovedYN();
		this.requestDateTime = dto.getRequestDateTime();
		this.processDateTime = dto.getProcessDateTime();
	}
	
	@Override
	public MeetingApprovalDTO convert() {
		// TODO Auto-generated method stub
		return MeetingApprovalDTO.initDTO().vo(this).build();
	}
}
