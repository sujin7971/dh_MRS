package egov.framework.plms.main.bean.mvc.entity.meeting;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingApprovalModelDTO;
import egov.framework.plms.main.core.model.enums.meeting.ApprovalStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MeetingApprovalDTO extends MeetingApprovalModelDTO {
	private Integer approvalId;
	private String requesterId;
	private String decisionMakerId;
	private Integer scheduleId;
	private ApprovalStatus approvalStatus;
	private String approvalComment;
	private Character approvedYN;
	private LocalDateTime requestDateTime;
	private LocalDateTime processDateTime;
	
	@Builder(builderClassName = "init", builderMethodName = "initDTO")
	public MeetingApprovalDTO(MeetingApprovalVO vo){
		this.approvalId = vo.getApprovalId();
		this.requesterId = vo.getRequesterId();
		this.decisionMakerId = vo.getDecisionMakerId();
		this.scheduleId = vo.getScheduleId();
		this.approvalStatus = vo.getApprovalStatus();
		this.approvalComment = vo.getApprovalComment();
		this.approvedYN = vo.getApprovedYN();
		this.requestDateTime = vo.getRequestDateTime();
		this.processDateTime = vo.getProcessDateTime();
	}
	
	@Override
	public MeetingApprovalVO convert() {
		// TODO Auto-generated method stub
		return MeetingApprovalVO.initVO().dto(this).build();
	}
}
