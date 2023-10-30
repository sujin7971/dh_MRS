package egov.framework.plms.sub.ewp.bean.component.scheduler;

import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import egov.framework.plms.main.bean.component.properties.ReserveConfigProperties;
import egov.framework.plms.main.bean.mvc.entity.common.CodeVO;
import egov.framework.plms.main.core.model.enums.meeting.ApprovalStatus;
import egov.framework.plms.sub.ewp.bean.mvc.entity.assign.EwpRoomReqVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingApprovalVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingScheduleVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.room.EwpRoomInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.service.alarm.EwpAlarmWriteService;
import egov.framework.plms.sub.ewp.bean.mvc.service.common.EwpCodeService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingApprovalService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingAttendeeService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingScheduleService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpTiberoRoomReqService;
import egov.framework.plms.sub.ewp.bean.mvc.service.room.EwpMariaRoomInfoService;
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
@Profile("ewp")
public class EwpMeetingApprovalScheduler {
	private final ReserveConfigProperties reserveProperties;
	private final EwpCodeService commonServ;
	private final EwpTiberoRoomReqService tbreqServ;
	private final EwpMariaRoomInfoService rmServ;
	private final EwpMeetingApprovalService appServ;
	private final EwpAlarmWriteService alarmServ;
	private final EwpMeetingScheduleService scheServ;
	private final EwpMeetingAttendeeService attServ;
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
		List<CodeVO> officeComCodeList = commonServ.getComCodeList(CodeVO.builder().classCd("CD001").build());
		officeComCodeList.stream().forEach(codeVO -> {
			String officeCode = codeVO.getDtlCd();
			String approvalMode = codeVO.getEtcCol1();
			if(approvalMode.equals("AUTH_AUTO")) {
				String officerId = "SYSTEM";
				List<EwpMeetingScheduleVO> scheduleList = scheServ.getMeetingScheduleList(EwpMeetingScheduleVO.builder().officeCode(officeCode).skdStatus(ApprovalStatus.REQUEST).delYN('N').build());
				scheduleList.stream().forEach(scheduleVO -> {
					LocalDate dueDate = scheduleVO.getHoldingDate();
					EwpRoomInfoVO roomVO = rmServ.selectRoomOne(scheduleVO.getRoomType(), scheduleVO.getRoomKey()).orElse(null);
					ApprovalStatus appStatus = ApprovalStatus.APPROVED;
					String appComment = "자동 승인";
					if(roomVO == null || dueDate.isBefore(todayLD)) {
						appStatus = ApprovalStatus.CANCELED;
						appComment = "자동 취소";
					}else if(roomVO.getDelYN() == 'Y') {
						appStatus = ApprovalStatus.REJECTED;
						appComment = roomVO.getRoomName()+"은(는) 더 이상 사용신청이 불가합니다.";
					}else if(roomVO.getRentYN() == 'N') {
						appStatus = ApprovalStatus.REJECTED;
						appComment = roomVO.getRentReason();
					}
					boolean appRes = appServ.insertApprovalOne(officerId, scheduleVO.getSkdKey(), appStatus, appComment);
					if(appRes) {
						log.info("결재 요청 자동 등록- 담당자: {}, 장소타입: {}, 신청키: {}, 스케줄키: {}, 결재요청: {}, 결재코멘트: {}", officerId, scheduleVO.getRoomType(), scheduleVO.getReqKey(), scheduleVO.getSkdKey(), appStatus, appComment);
					}
				});
			}
		});
	}
	/**
	 * 등록된 사용신청 결재 요청 처리
	 */
	private void processReqApproval() {
		List<EwpMeetingApprovalVO> appList = appServ.selectApprovalListForProcessing();
		for(EwpMeetingApprovalVO appVO : appList) {
			EwpMeetingScheduleVO scheduleVO = scheServ.getMeetingScheduleOne(appVO.getSkdKey()).orElse(null);
			if(scheduleVO == null) {
				appServ.updateApprovalOne(appVO.getAppId(), 'N', "회의정보를 찾을 수 없음");
				continue;
			}
			log.info("결재 처리 대상- 담당자: {}, 장소타입: {}, 스케줄키: {}, 결재요청: {}, 결재코멘트: {}", appVO.getUserKey(), scheduleVO.getRoomType(), appVO.getSkdKey(), appVO.getAppStatus(), appVO.getAppComment());
			try {
				boolean isSucess = true;
				try {
					if(scheduleVO.getReqKey() != null) {
						if(appVO.getAppStatus() == ApprovalStatus.DELETE) { // 삭제 요청
							tbreqServ.deleteRoomReq(scheduleVO.getRoomType(), scheduleVO.getReqKey(), appVO.getUserKey());
						}else {
							tbreqServ.putAssignStatus(scheduleVO.getRoomType(), scheduleVO.getReqKey(), appVO.getUserKey(), EwpRoomReqVO.getAppStatus(appVO.getAppStatus()), appVO.getAppComment());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					isSucess = false;
				} finally {
					if(isSucess){
						appServ.updateApprovalOne(appVO.getAppId(), 'Y');
						alarmServ.changeStatus(appVO);
					}else {
						appServ.updateApprovalOne(appVO.getAppId(), 'N', "동서발전 동기화 실패");
					}
				}
			}catch (Exception e) {
				log.error(e.toString(), e);
			}
		}
	}
}
