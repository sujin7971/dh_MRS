package egov.framework.plms.sub.lime.bean.component.websocket;

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
import egov.framework.plms.main.bean.mvc.entity.file.FileDetailVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingAttendeeDTO;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingAttendeeVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingInfoVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingScheduleVO;
import egov.framework.plms.main.core.model.enums.file.RelatedEntityType;
import egov.framework.plms.main.core.model.enums.file.RelationType;
import egov.framework.plms.main.core.model.response.SocketMessage;
import egov.framework.plms.sub.lime.bean.mvc.service.file.LimeFileInfoService;
import egov.framework.plms.sub.lime.bean.mvc.service.meeting.LimeMeetingAttendeeService;
import egov.framework.plms.sub.lime.bean.mvc.service.meeting.LimeMeetingInfoService;
import egov.framework.plms.sub.lime.bean.mvc.service.meeting.LimeMeetingScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("lime")
public class LimeMeetingMessageBroker {
	private final SimpMessagingTemplate template; //특정 subscriber에게 메세지를 전달
	private final MeetingMonitoringManager monitoringMng;
	
	private final LimeMeetingScheduleService scheServ;
	private final LimeMeetingInfoService mtServ;
	private final LimeMeetingAttendeeService attServ;
	private final LimeFileInfoService fileServ;
	
	public void broadCastMsg(Integer meetingId, SocketMessage msg) {
		switch(msg.getMessageType()) {
			case NOTICE:
				sendNoticeMsg(meetingId, msg);
				break;
			case UPDATE:
				sendUpdateMsg(meetingId, msg);
				break;
			case SYNC:
				sendSyncMsg(meetingId, msg);
				break;
			case REQUEST:
				sendRequestMsg(meetingId, msg);
				break;
		}
	}
	
	public void sendNoticeMsg(Integer meetingId, SocketMessage msg) {
		log.debug("메시지 대상: {}, 내용: {}", meetingId, msg);
		switch(msg.getActionType()) {
			case JOIN:
			case LEAVE:{
				MeetingProgressVO progressVO = monitoringMng.getProgress(meetingId);
				msg.setData(progressVO.convert());
			}
				break;
			case CONTROL:{
				if(msg.getData() == null) {
					// 스트리밍(화면제어) 종료
					boolean result = monitoringMng.endStream(meetingId, msg.getSessionId());
					if(result == true) {
						msg.setData("end");
					}else {
						msg.setData(null);
					}
				}else {
					Map<String, Integer> pageData = (Map<String, Integer>) msg.getData();
					// 스트리밍(화면제어) 시작
					monitoringMng.startStream(meetingId, msg.getSender(), msg.getSessionId(), pageData.get("fileKey"), pageData.get("pageno"));
					MeetingProgressVO progressVO = monitoringMng.getProgress(meetingId);
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
		template.convertAndSend("/subscribe/room/"+meetingId, msg);
	}
	
	public void sendSyncMsg(Integer meetingId, SocketMessage msg) {
		switch(msg.getResourceType()) {
			case FILE:
				List<FileDetailVO> list = fileServ.selectFileList(FileDetailVO.builder().relatedEntityType(RelatedEntityType.MEETING).relatedEntityId(meetingId).relationType(RelationType.MEETING_MATERIAL).build());
				msg.setData(list.stream().map(vo -> vo.convert()).collect(Collectors.toList()));
				break;
			case ATTENDEE:
				List<MeetingAttendeeVO> voList = attServ.getMeetingAttendeeListByMeeting(meetingId);
				List<MeetingAttendeeDTO> dtoList = voList.stream().map(e -> e.convert()).collect(Collectors.toList());
				msg.setData(dtoList);
				break;
			default:
				break;
		}
		template.convertAndSend("/subscribe/room/"+meetingId, msg);
	}
	
	public void sendUpdateMsg(Integer meetingId, SocketMessage msg) {
		switch(msg.getResourceType()) {
			case ATTENDEE:
				if(msg.getData() != null) {
					Optional<MeetingAttendeeVO> attOpt = attServ.getMeetingAttendeeOne((Integer) msg.getData());
					if(attOpt.isPresent()) {
						MeetingAttendeeVO attendeeVO = attOpt.get();
						msg.setData(new ArrayList<>(Arrays.asList(attendeeVO.convert())));
					}
				}else {
					List<MeetingAttendeeVO> voList = attServ.getMeetingAttendeeListByMeeting(meetingId);
					List<MeetingAttendeeDTO> dtoList = voList.stream().map(e -> e.convert()).collect(Collectors.toList());
					msg.setData(dtoList);
				}
				break;
			case SCHEDULE:
				Optional<MeetingScheduleVO> scheOpt = scheServ.getMeetingScheduleOne((Integer) msg.getData());
				if(scheOpt.isPresent()) {
					MeetingScheduleVO scheduleVO = scheOpt.get();
					msg.setData(scheduleVO.convert());
				}
				break;
			case MEETING:
				Optional<MeetingInfoVO> meetOpt = mtServ.getMeetingInfoOne(meetingId);
				if(meetOpt.isPresent()) {
					MeetingInfoVO meetingVO = meetOpt.get();
					msg.setData(meetingVO.convert());
				}
				break;
			case FILE:
				Optional<FileDetailVO> fileVO = fileServ.selectFileOne((Integer) msg.getData());
				msg.setData(fileVO.map(FileDetailVO::convert).orElse(null));
				break;
			default:
				break;
		}
		template.convertAndSend("/subscribe/room/"+meetingId, msg);
	}
	
	public void sendRequestMsg(Integer meetingId, SocketMessage msg) {
		template.convertAndSend("/subscribe/room/"+meetingId, msg);
	}

	public void streamMsg(Integer meetingId, SocketMessage msg) {
		String streamer = monitoringMng.getStreamer(meetingId);
		template.convertAndSend("/subscribe/room/"+meetingId+"/stream/"+streamer, msg);
	}
}
