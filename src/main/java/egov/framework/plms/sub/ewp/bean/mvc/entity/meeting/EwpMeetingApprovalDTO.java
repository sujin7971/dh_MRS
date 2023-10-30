package egov.framework.plms.sub.ewp.bean.mvc.entity.meeting;

import java.time.LocalDateTime;
import java.util.Optional;

import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingApprovalModelDTO;
import egov.framework.plms.main.core.model.enums.meeting.ApprovalStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * {@link EwpMeetingApprovalVO} 참조
 * @author k
 *
 */
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class EwpMeetingApprovalDTO extends MeetingApprovalModelDTO{
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

	@Builder(builderClassName = "init", builderMethodName = "initDTO")
	public EwpMeetingApprovalDTO(EwpMeetingApprovalVO vo) {
		Optional.ofNullable(vo).ifPresent(data -> {
			this.appId = data.getAppId();
			this.userKey = data.getUserKey();
			this.appStatus = data.getApprovalStatus();
			this.appComment = data.getAppComment();
			this.appYN = data.getAppYN();
			this.successYN = data.getSuccessYN();
		});
	}
	
	@Override
	public EwpMeetingApprovalVO convert() {
		return EwpMeetingApprovalVO.initVO().dto(this).build();
	}

	@Override
	public void setApprovalId(Integer value) {
		// TODO Auto-generated method stub
		this.appId = value;
	}

	@Override
	public void setRequesterId(String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDecisionMakerId(String value) {
		// TODO Auto-generated method stub
		this.userKey = value;
	}

	@Override
	public void setScheduleId(Integer value) {
		// TODO Auto-generated method stub
		this.skdKey = value;
	}

	@Override
	public void setApprovalStatus(ApprovalStatus value) {
		// TODO Auto-generated method stub
		this.appStatus = value;
	}

	@Override
	public void setApprovalComment(String value) {
		// TODO Auto-generated method stub
		this.appComment = value;
	}

	@Override
	public void setApprovedYN(Character value) {
		// TODO Auto-generated method stub
		this.successYN = value;
	}

	@Override
	public void setRequestDateTime(LocalDateTime value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setProcessDateTime(LocalDateTime value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Integer getApprovalId() {
		// TODO Auto-generated method stub
		return this.appId;
	}

	@Override
	public String getRequesterId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDecisionMakerId() {
		// TODO Auto-generated method stub
		return this.userKey;
	}

	@Override
	public Integer getScheduleId() {
		// TODO Auto-generated method stub
		return this.skdKey;
	}

	@Override
	public ApprovalStatus getApprovalStatus() {
		// TODO Auto-generated method stub
		return this.appStatus;
	}

	@Override
	public String getApprovalComment() {
		// TODO Auto-generated method stub
		return this.appComment;
	}

	@Override
	public Character getApprovedYN() {
		// TODO Auto-generated method stub
		return this.successYN;
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
