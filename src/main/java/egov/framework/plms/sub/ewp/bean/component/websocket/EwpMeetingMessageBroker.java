package egov.framework.plms.sub.ewp.bean.component.websocket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import egov.framework.plms.main.bean.component.monitoring.MeetingMonitoringManager;
import egov.framework.plms.main.bean.component.monitoring.MeetingProgressVO;
import egov.framework.plms.main.core.model.response.SocketMessage;
import egov.framework.plms.sub.ewp.bean.mvc.entity.file.MeetingFileInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingAttendeeDTO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingAttendeeVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingScheduleVO;
import egov.framework.plms.sub.ewp.bean.mvc.service.file.EwpMeetingFileInfoService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingAttendeeService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingInfoService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingScheduleService;
import egov.framework.plms.sub.ewp.bean.mvc.service.organization.EwpUserInfoService;
import egov.framework.plms.sub.ewp.core.model.enums.FileRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("ewp")
public class EwpMeetingMessageBroker {
	private final SimpMessagingTemplate template; //특정 subscriber에게 메세지를 전달
	private final MeetingMonitoringManager monitoringMng;
	
	private final EwpMeetingScheduleService scheServ;
	private final EwpMeetingInfoService mtServ;
	private final EwpMeetingAttendeeService attServ;
	private final EwpUserInfoService userServ;
	private final EwpMeetingFileInfoService fServ;
	
	public void broadCastMsg(Integer meetingKey, SocketMessage msg) {
		switch(msg.getMessageType()) {
			case NOTICE:
				sendNoticeMsg(meetingKey, msg);
				break;
			case UPDATE:
				sendUpdateMsg(meetingKey, msg);
				break;
			case SYNC:
				sendSyncMsg(meetingKey, msg);
				break;
			case REQUEST:
				sendRequestMsg(meetingKey, msg);
				break;
		}
	}
	
	public void sendNoticeMsg(Integer meetingKey, SocketMessage msg) {
		log.debug("메시지 대상: {}, 내용: {}", meetingKey, msg);
		switch(msg.getActionType()) {
			case JOIN:
			case LEAVE:{
				MeetingProgressVO progressVO = monitoringMng.getProgress(meetingKey);
				msg.setData(progressVO.convert());
			}
				break;
			case CONTROL:{
				if(msg.getData() == null) {
					// 스트리밍(화면제어) 종료
					boolean result = monitoringMng.endStream(meetingKey, msg.getSessionId());
					if(result == true) {
						msg.setData("end");
					}else {
						msg.setData(null);
					}
				}else {
					Map<String, Integer> pageData = (Map<String, Integer>) msg.getData();
					// 스트리밍(화면제어) 시작
					monitoringMng.startStream(meetingKey, msg.getSender(), msg.getSessionId(), pageData.get("fileKey"), pageData.get("pageno"));
					MeetingProgressVO progressVO = monitoringMng.getProgress(meetingKey);
					msg.setData(progressVO.convert());
				}
			}
				break;
			case HIGHLIGHT:{
				break;
			}
			default:
				break;
		}
		template.convertAndSend("/subscribe/room/"+meetingKey, msg);
	}
	
	public void sendSyncMsg(Integer meetingKey, SocketMessage msg) {
		switch(msg.getResourceType()) {
			case FILE:
				List<MeetingFileInfoVO> list = fServ.getMeetingFileList(MeetingFileInfoVO.builder().meetingKey(meetingKey).roleType(FileRole.MATERIAL).build());
				msg.setData(list.stream().map(vo -> vo.convert()).collect(Collectors.toList()));
				break;
			case ATTENDEE:
				List<EwpMeetingAttendeeVO> voList = attServ.getMeetingAttendeeListByMeeting(meetingKey);
				
				List<EwpMeetingAttendeeDTO> dtoList = voList.stream().map(vo -> 
					vo.toBuilder()
						.user(userServ.selectUserInfoOne(vo.getUserId()).orElse(null))
					.build().convert())
				.collect(Collectors.toList());
				msg.setData(dtoList);
				break;
			default:
				break;
		}
		template.convertAndSend("/subscribe/room/"+meetingKey, msg);
	}
	
	public void sendUpdateMsg(Integer meetingKey, SocketMessage msg) {
		switch(msg.getResourceType()) {
			case ATTENDEE:
				if(msg.getData() != null) {
					Optional<EwpMeetingAttendeeVO> attendeeOpt = attServ.getMeetingAttendeeOne((Integer) msg.getData());
					if(attendeeOpt.isPresent()) {
						EwpMeetingAttendeeVO attendeeVO = attendeeOpt.get();
						msg.setData(new ArrayList<>(Arrays.asList(attendeeVO.toBuilder().user(userServ.selectUserInfoOne(attendeeVO.getUserId()).orElse(null)).build().convert())));
					}
				}else {
					List<EwpMeetingAttendeeVO> voList = attServ.getMeetingAttendeeListByMeeting(meetingKey);
					List<EwpMeetingAttendeeDTO> dtoList = voList.stream().map(vo -> 
							vo.toBuilder()
							.user(userServ.selectUserInfoOne(vo.getUserId()).orElse(null))
						.build().convert())
					.collect(Collectors.toList());
					msg.setData(dtoList);
				}
				break;
			case SCHEDULE:
				Optional<EwpMeetingScheduleVO> scheduleOpt = scheServ.getMeetingScheduleOne((Integer) msg.getData());
				if(scheduleOpt.isPresent()) {
					EwpMeetingScheduleVO scheduleVO = scheduleOpt.get();					
					msg.setData(scheduleVO.convert());
				}
				break;
			case MEETING:
				Optional<EwpMeetingInfoVO> meetingOpt = mtServ.getMeetingInfoOne(meetingKey);
				if(meetingOpt.isPresent()) {
					EwpMeetingInfoVO meetingVO = meetingOpt.get();					
					msg.setData(meetingVO.convert());
				}
				break;
			case FILE:
				MeetingFileInfoVO fileVO = fServ.getFileOne((Integer) msg.getData());
				msg.setData(fileVO.convert());
				break;
			default:
				break;
		}
		template.convertAndSend("/subscribe/room/"+meetingKey, msg);
	}
	
	public void sendRequestMsg(Integer meetingKey, SocketMessage msg) {
		template.convertAndSend("/subscribe/room/"+meetingKey, msg);
	}

	public void streamMsg(Integer meetingKey, SocketMessage msg) {
		String streamer = monitoringMng.getStreamer(meetingKey);
		template.convertAndSend("/subscribe/room/"+meetingKey+"/stream/"+streamer, msg);
	}
}
