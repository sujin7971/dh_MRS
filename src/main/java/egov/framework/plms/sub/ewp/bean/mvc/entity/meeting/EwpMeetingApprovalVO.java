package egov.framework.plms.sub.ewp.bean.mvc.entity.meeting;

import java.time.LocalDateTime;
import java.util.Optional;

import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingApprovalModelVO;
import egov.framework.plms.main.core.model.enums.meeting.ApprovalStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 사용신청 결재요청에 대한 객체.
 * 
 * @author mckim
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class EwpMeetingApprovalVO extends MeetingApprovalModelVO{
	/** 결재 요청키 */
	private Integer appId;
	/** 결재 담당자키 */
	private String userKey;
	/** 결재 대상 스케줄키 */
	private Integer skdKey;
	/** 결재 */
	private ApprovalStatus appStatus;
	/** 결재 코멘트 */
	private String appComment;
	/** 결재 처리 여부 */
	private Character appYN;
	/** 결재 처리 결과 */
	private Character successYN;
	
	@Builder(builderClassName = "init", builderMethodName = "initVO")
	public EwpMeetingApprovalVO(EwpMeetingApprovalDTO dto) {
		Optional.ofNullable(dto).ifPresent(data -> {
			this.appId = data.getAppId();
			this.userKey = data.getUserKey();
			this.appStatus = data.getAppStatus();
			this.appComment = data.getAppComment();
			this.appYN = data.getAppYN();
			this.successYN = data.getSuccessYN();
		});
	}
	
	@Override
	public EwpMeetingApprovalDTO convert() {
		return EwpMeetingApprovalDTO.initDTO().vo(this).build();
	}

	@Override
	public Integer getApprovalId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRequesterId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDecisionMakerId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getScheduleId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApprovalStatus getApprovalStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getApprovalComment() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Character getApprovedYN() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalDateTime getRequestDateTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalDateTime getProcessDateTime() {
		// TODO Auto-generated method stub
		return null;
	}

}
