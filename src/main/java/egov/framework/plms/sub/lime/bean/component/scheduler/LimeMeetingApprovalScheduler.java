package egov.framework.plms.sub.lime.bean.component.scheduler;

import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import egov.framework.plms.main.bean.component.properties.ReserveConfigProperties;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingApprovalVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingScheduleVO;
import egov.framework.plms.main.bean.mvc.entity.room.RoomInfoVO;
import egov.framework.plms.main.bean.mvc.service.meeting.MeetingApprovalService;
import egov.framework.plms.main.core.model.enums.meeting.ApprovalStatus;
import egov.framework.plms.sub.ewp.bean.mvc.entity.assign.EwpRoomReqVO;
import egov.framework.plms.sub.lime.bean.mvc.service.meeting.LimeMeetingScheduleService;
import egov.framework.plms.sub.lime.bean.mvc.service.room.LimeRoomInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * 사용신청 결재에 대한 처리를 담당할 스케줄러 컴포넌트. 추후 사업소별 자동승인 처리도 담당할 예정
 * 
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "config.scheduledTasks.meetingApprovalScheduler", name="enabled", havingValue = "true", matchIfMissing = false)
@Profile("lime")
public class LimeMeetingApprovalScheduler {
	private final ReserveConfigProperties reserveProperties;
	private final LimeRoomInfoService rmServ;
	private final MeetingApprovalService appServ;
	private final LimeMeetingScheduleService scheServ;
	/**
	 * 사용신청 결재요청 처리
	 */
	//@Scheduled(cron = "0/1 * * * * ?")
	@Scheduled(cron="${config.scheduledTasks.meetingApprovalScheduler.cron}")
	public void assignApprovalProcessor() {
		if(reserveProperties.isAutoApproval()) {
			processAutoApproval();
		}
		processReqApproval();
	}
	/**
	 * 사업소별 자동승인 정책에 따라 미결재된 사용신청 결재 요청 등록
	 */
	private void processAutoApproval() {
		LocalDate todayLD = LocalDate.now();
		List<MeetingScheduleVO> scheduleList = scheServ.getMeetingScheduleList(MeetingScheduleVO.builder().approvalStatus(ApprovalStatus.REQUEST).delYN('N').build());
		scheduleList.stream().forEach(scheduleVO -> {
			LocalDate dueDate = scheduleVO.getHoldingDate();
			RoomInfoVO roomVO = rmServ.selectRoomOne(scheduleVO.getRoomId()).orElse(null);
			ApprovalStatus appStatus = ApprovalStatus.APPROVED;
			String appComment = "자동 승인";
			if(roomVO == null || dueDate.isBefore(todayLD)) {
				appStatus = ApprovalStatus.CANCELED;
				appComment = "자동 취소";
			}else if(roomVO.getDelYN() == 'Y') {
				appStatus = ApprovalStatus.REJECTED;
				appComment = roomVO.getRoomName()+"은(는) 더 이상 사용신청이 불가합니다.";
			}else if(roomVO.getDisableYN() == 'Y') {
				appStatus = ApprovalStatus.REJECTED;
				appComment = roomVO.getDisableComment();
			}
			appServ.insertApprovalOne("SYSTEM", scheduleVO.getScheduleId(), appStatus, appComment);
		});
	}
	/**
	 * 등록된 사용신청 결재 요청 처리
	 */
	private void processReqApproval() {
		List<MeetingApprovalVO> appList = appServ.selectApprovalListForProcessing();
		for(MeetingApprovalVO appVO : appList) {
			MeetingScheduleVO scheduleVO = scheServ.getMeetingScheduleOne(appVO.getScheduleId()).orElse(null);
			if(scheduleVO == null) {
				appServ.updateApprovalOne(appVO.getApprovalId(), 'N', "회의정보를 찾을 수 없음");
				continue;
			}
			appServ.updateApprovalOne(appVO.getApprovalId(), 'Y');
		}
	}
}
