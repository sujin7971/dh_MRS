package egov.framework.plms.sub.ewp.bean.mvc.service.meeting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.mvc.service.meeting.abst.MeetingApprovalAbstractService;
import egov.framework.plms.main.core.model.enums.meeting.ApprovalStatus;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingApprovalVO;
import egov.framework.plms.sub.ewp.bean.mvc.mapper.maria.meeting.EwpMeetingApprovalMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile("ewp")
@Primary
/**
 * 결재 요청 등록 및 처리 서비스
 * 처리에 대한 상세 로직은{@link AssignApprovalScheduler AssignApprovalScheduler}의 {@link AssignApprovalScheduler#assignApprovalProcessor() AssignApprovalScheduler.assignApprovalProcessor}참고
 * 
 * @author mckim
 *
 */
public class EwpMeetingApprovalService extends MeetingApprovalAbstractService<EwpMeetingApprovalVO>{
	public EwpMeetingApprovalService(@Autowired EwpMeetingApprovalMapper mapper) {
		super(mapper);
	}
	
	/**
	 * 결재요청 등록. 이미 해당 사용신청의 처리가 완료되지 않은 결재요청이 등록되어있는경우 등록 불가
	 * @param officerId 결재자 키
	 * @param roomType {@link RoomRole}
	 * @param reqKey 사용신청키
	 * @param skdKey 스케줄키
	 * @param status 요청한 결재 구분(승인/취소/불가)
	 * @param comment 결재 멘트
	 * @return
	 */
	public boolean insertApprovalOne(String officerId, Integer skdKey, ApprovalStatus status, String comment) {
		EwpMeetingApprovalVO appVO = EwpMeetingApprovalVO.builder().userKey(officerId).skdKey(skdKey).appStatus(status).appComment(comment).build();
		return insertApprovalOne(appVO);
	}
	
	/**
	 * {@link EwpMeetingApprovalService#approvalAssign}
	 * @param officerId 결재자 키
	 * @param roomType {@link RoomRole}
	 * @param reqKey 사용신청키
	 * @param skdKey 스케줄키
	 * @param status 요청한 결재 구분(승인/취소/불가)
	 * @return
	 */
	public boolean insertApprovalOne(String officerId, Integer skdKey, ApprovalStatus status) {
		EwpMeetingApprovalVO appVO = EwpMeetingApprovalVO.builder().userKey(officerId).skdKey(skdKey).appStatus(status).build();
		return insertApprovalOne(appVO);
	}
	
	/**
	 * 결재요청 처리. 처리결과가 N인경우 결재요청을 사용취소로 변경
	 * @param appId 결재할 요청키
	 * @param appYN 결재 처리 여부
	 * @param successYN 결재 처리 결과
	 * @return
	 */
	public boolean updateApprovalOne(Integer appId, Character successYN) {
		return updateApprovalOne(EwpMeetingApprovalVO.builder().appId(appId).successYN(successYN).build());
	}
	
	/**
	 * 결재요청 처리. 처리결과가 N인경우 결재요청을 사용취소로 변경
	 * @param appId 결재할 요청키
	 * @param appYN 결재 결과
	 * @param successYN 결재 처리 결과
	 * @param appComment 결재 코멘트
	 * @return
	 */
	public boolean updateApprovalOne(Integer appId, Character successYN, String appComment) {
		return updateApprovalOne(EwpMeetingApprovalVO.builder().appId(appId).successYN(successYN).appComment(appComment).build());
	}
}
