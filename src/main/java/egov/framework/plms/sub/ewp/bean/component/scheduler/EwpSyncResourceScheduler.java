package egov.framework.plms.sub.ewp.bean.component.scheduler;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import egov.framework.plms.sub.ewp.bean.mvc.entity.assign.EwpRoomReqVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingAssignVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingScheduleVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.room.EwpRoomInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.service.common.EwpCodeService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingApprovalService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingAssignService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingInfoService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingScheduleService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpTiberoRoomReqService;
import egov.framework.plms.sub.ewp.bean.mvc.service.room.EwpMariaRoomInfoService;
import egov.framework.plms.sub.ewp.bean.mvc.service.room.EwpTiberoRoomInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @FileName : MeetingRoomReqStatusScheduler.java
 * @Project : EWP PLMS SYSTEM
 * @Date : 2022. 11. 21. 
 * @작성자 : hb
 * @변경이력 :
 * @프로그램 설명 : 회의관리 시스템과 동서발전 회의실/강의실/강당 배정상태 동기화를 위한 조회 처리 스케쥴
 */

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "config.scheduledTasks.syncResourceScheduler", name="enabled", havingValue = "true", matchIfMissing = false)
@Profile("ewp")
public class EwpSyncResourceScheduler {
	private final EwpCodeService codeServ;
	private final EwpTiberoRoomReqService reqServ;
	private final EwpTiberoRoomInfoService tiberoRmServ;
	private final EwpMariaRoomInfoService mariaRmServ;
	private final EwpMeetingScheduleService scheServ;
	private final EwpMeetingInfoService mtServ;
	private final EwpMeetingAssignService assignServ;
	private final EwpMeetingApprovalService appServ;
	/**
	 * 경영지원서비스와 장소 및 회의 신청 데이터 동기화
	 */		
	@Scheduled(cron="${config.scheduledTasks.syncResourceScheduler.cron}")
	private void meetingRoomReqStatusScheduler() {
		syncRoom();
		syncMeeting();
	}
	
	private void syncRoom() {
		try {
			Set<String> codeSet = codeServ.getEntriedOfficeCodeSet();
			codeSet.forEach(code -> {
				List<EwpRoomInfoVO> roomList = tiberoRmServ.selectRoomList(EwpRoomInfoVO.builder().officeCode(code).build());
				for(EwpRoomInfoVO roomVO: roomList) {
					mariaRmServ.insertRoomOne(roomVO);
				}
			});
		}catch(Exception e) {
			log.error(e.toString(), e);
		}
	}
	
	private void syncMeeting() {
		Set<String> codeSet = codeServ.getEntriedOfficeCodeSet();
		//회의실 조회
		try {
			List<EwpRoomReqVO> roomAssignList = reqServ.getMeetingRoomReqStatusList();//최근 30일 동서발전 회의실 사용신청 테이블 조회
			roomAssignList = roomAssignList.stream().filter(vo -> codeSet.contains(vo.getSaupsoCd())).collect(Collectors.toList());
			syncAssignData(roomAssignList);
		}catch (Exception e) {
			log.error(e.toString(), e);
		}
		
		/**
		 * 회의관리 시스템과 강의실 배정요청 상태 동기화를 위한 조회 처리 스케쥴
		 */
		try {
			List<EwpRoomReqVO> roomAssignList = reqServ.getEduRoomReqStatusList(); //최근 30일 동서발전 강의실 사용신청 테이블 조회
			roomAssignList = roomAssignList.stream().filter(vo -> codeSet.contains(vo.getSaupsoCd())).collect(Collectors.toList());
			syncAssignData(roomAssignList);
		}catch (Exception e) {
			log.error(e.toString(), e);
		}
		
		/**
		 * 회의관리 시스템과 강당 배정요청 상태 동기화를 위한 조회 처리 스케쥴
		 */
		try {
			List<EwpRoomReqVO> roomAssignList = reqServ.getHallRoomReqStatusList(); //최근 30일 동서발전 강당 사용신청 테이블 조회
			roomAssignList = roomAssignList.stream().filter(vo -> codeSet.contains(vo.getSaupsoCd())).collect(Collectors.toList());
			syncAssignData(roomAssignList);
		}catch (Exception e) {
			log.error(e.toString(), e);
		}		
	}
	
	private void syncAssignData(List<EwpRoomReqVO> roomAssignList) {
		for(EwpRoomReqVO externalReqData : roomAssignList) {
			try {
				if(externalReqData.getSeqReq() != null) {
					Optional<EwpMeetingAssignVO> voOpt = assignServ.getMeetingAssignOneByRequest(externalReqData.getRoomType(), externalReqData.getSeqReq());
					// 사용신청 테이블 내용 PLMS EETING_SCHEDULE 테이블의 미존재 시 insert 처리
					if (voOpt.isPresent()) { //update
						EwpMeetingAssignVO innerData = voOpt.get();
					    EwpMeetingAssignVO externalData = externalReqData.convert();
					    if(externalData.getAppStatus() != innerData.getAppStatus()) {
					    	appServ.insertApprovalOne("SYSTEM", innerData.getSkdKey(), externalData.getAppStatus(), externalData.getAppComment());
					    	continue;
					    }
						if(isMeetingScheduleChange(innerData, externalData)) {
							EwpMeetingScheduleVO updateScheduleVO = externalData.getMeetingSchedule().toBuilder().skdKey(innerData.getSkdKey()).skdType(null).build();
							scheServ.putMeetingSchedule(updateScheduleVO);
							log.info("sync schedule {}", innerData.getSkdKey());
						}
						if(isMeetingInfoChange(innerData, externalData)) {
							EwpMeetingInfoVO updateMeetingVO = externalData.getMeetingInfo().toBuilder().meetingKey(innerData.getMeetingKey()).meetingStatus(null).build();
							mtServ.putMeetingInfo(updateMeetingVO);
							log.info("sync meeting {}", innerData.getMeetingKey());
						}
					}else {//insert
						EwpMeetingAssignVO externalData = externalReqData.convert();
						assignServ.postMeetingAssign(externalData);
					}
				}
			}catch (Exception e) {
				log.error(e.toString(), e);
			}
		}
	}
	
	private boolean isMeetingScheduleChange(EwpMeetingAssignVO innerData, EwpMeetingAssignVO externalData) {
		//Tibero 사용신청 테이블과 plms MEETING_SCHEDULE 테이블간 차이 비교
		if(!externalData.getAttendeeCnt().equals(innerData.getAttendeeCnt())) {
			log.info("sync required - innerData AttendeeCnt: {}, externalData AttendeeCnt: {}", innerData.getAttendeeCnt(), externalData.getAttendeeCnt());
			return true;
		}
		if(!externalData.getOfficeCode().equals(innerData.getOfficeCode())) {
			log.info("sync required - innerData OfficeCode: {}, externalData OfficeCode: {}", innerData.getOfficeCode(), externalData.getOfficeCode());
			return true;
		}
		if(!externalData.getDeptId().equals(innerData.getDeptId())) {
			log.info("sync required - innerData DeptId: {}, externalData DeptId: {}", innerData.getDeptId(), externalData.getDeptId());
			return true;
		}
		if(externalData.getDelYN() != innerData.getDelYN()) {
			log.info("sync required - innerData DelYN: {}, externalData DelYN: {}", innerData.getDelYN(), externalData.getDelYN());
			return true;
		}
		if(!externalData.getSkdHost().equals(innerData.getSkdHost())) {
			log.info("sync required - innerData SkdHost: {}, externalData SkdHost: {}", innerData.getSkdHost(), externalData.getSkdHost());
			return true;
		}
		if(!externalData.getBeginDateTime().equals(innerData.getBeginDateTime())) {
			log.info("sync required - innerData BeginDateTime: {}, externalData BeginDateTime: {}", innerData.getBeginDateTime(), externalData.getBeginDateTime());
			return true;
		}
		if(!externalData.getFinishDateTime().equals(innerData.getFinishDateTime())) {
			log.info("sync required - innerData FinishDateTime: {}, externalData FinishDateTime: {}", innerData.getFinishDateTime(), externalData.getFinishDateTime());
			return true;
		}
		return false;
	}
	
	private boolean isMeetingInfoChange(EwpMeetingAssignVO innerData, EwpMeetingAssignVO externalData) {
		//Tibero 사용신청 테이블과 plms MEETING_INFO 테이블간 차이 비교
		if(!externalData.getTitle().equals(innerData.getTitle())) {
			log.info("sync required - innerData Title: {}, externalData Title: {}", innerData.getTitle(), externalData.getTitle());
			return true;
		}
		if(!externalData.getContents().equals(innerData.getContents())) {
			log.info("sync required - innerData Contents: {}, externalData Contents: {}", innerData.getContents(), externalData.getContents());
			return true;
		}
	    return false;
	}
}


