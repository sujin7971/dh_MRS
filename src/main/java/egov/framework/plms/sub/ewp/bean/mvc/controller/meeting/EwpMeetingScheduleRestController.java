package egov.framework.plms.sub.ewp.bean.mvc.controller.meeting;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import egov.framework.plms.main.core.exception.ApiDataOperationException;
import egov.framework.plms.main.core.exception.ApiNotFoundException;
import egov.framework.plms.main.core.exception.abst.ApiException;
import egov.framework.plms.main.core.model.auth.ResourceAuthorityCollection;
import egov.framework.plms.main.core.model.enums.auth.MeetingAuth;
import egov.framework.plms.main.core.model.enums.error.ErrorCode;
import egov.framework.plms.main.core.model.enums.meeting.MeetingStatus;
import egov.framework.plms.main.core.model.response.ResponseMessage;
import egov.framework.plms.main.core.util.DateTimeUtil;
import egov.framework.plms.sub.ewp.bean.component.auth.EwpResourceAuthorityProvider;
import egov.framework.plms.sub.ewp.bean.component.properties.EwpPropertiesProvider;
import egov.framework.plms.sub.ewp.bean.mvc.entity.assign.EwpRoomReqVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingAssignVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingScheduleDTO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingScheduleVO;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingAssignService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingScheduleService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpTiberoRoomReqService;
import egov.framework.plms.sub.ewp.bean.mvc.service.organization.EwpUserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/ewp")
@RequiredArgsConstructor
@Profile("ewp")
public class EwpMeetingScheduleRestController {
	private final EwpResourceAuthorityProvider authorityProvider;
	private final EwpPropertiesProvider propertiesProvider;
	
	private final EwpMeetingScheduleService scheServ;
	private final EwpMeetingAssignService assignServ;
	private final EwpTiberoRoomReqService reqServ;
	private final EwpUserInfoService userServ;
	/**
	 * 해당 사용신청 다음에 예정된 사용신청 정보 조회.
	 * @param skdKey 스케줄키
	 * @return 사용신청 정보 반환
	 */
	@GetMapping("/meeting/schedule/{skdKey}/next")
	public EwpMeetingScheduleDTO getMeetingScheduleNextOne(Model model, @PathVariable Integer skdKey) {
		Optional<EwpMeetingScheduleVO> scheOpt = scheServ.getMeetingScheduleOne(skdKey);
		if(scheOpt.isPresent()) {
			EwpMeetingScheduleVO nowScheVO = scheOpt.get();
			EwpMeetingScheduleVO nextScheVO = null;
			if(nowScheVO.getReqKey() != null) {
				EwpRoomReqVO reqVO = reqServ.getNextRoomReqOne(nowScheVO.getRoomType(), nowScheVO.getReqKey());
				nextScheVO = Optional.ofNullable(reqVO).map(EwpRoomReqVO::convert).map(EwpMeetingAssignVO::getMeetingSchedule).orElse(null);
			}else {
				nextScheVO = scheServ.getMeetingScheduleNextOne(skdKey).orElse(null);
			}
			return Optional.ofNullable(nextScheVO).map(vo -> vo.toBuilder().writer(userServ.selectUserInfoOne(vo.getWriterKey()).orElse(null)).build().convert()).orElse(null);
		}else {
			throw new ApiNotFoundException(ErrorCode.MEETING_SCHEDULE.NOT_FOUND);
		}
	}
	
	@PutMapping("/meeting/schedule/{skdKey}/extend/minutes/{minutes}")
	public ResponseMessage putMeetingScheduleExt(@PathVariable Integer skdKey, @PathVariable Integer minutes) {
		Optional<EwpMeetingAssignVO> assignOpt = assignServ.getMeetingAssignOneByScheduleId(skdKey);
		if(assignOpt.isPresent()) {
			EwpMeetingAssignVO assignVO = assignOpt.get();
			ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(assignVO.getMeetingKey());
			if(authorityCollection.isEmpty()) {
				return ResponseMessage.builder(ResponseMessage.StatusCode.FORBIDDEN)
						.message(ResponseMessage.MessageCode.ASSIGN.PUT_FAIL.value())
						.detail(ResponseMessage.DetailCode.ASSIGN.NOT_FOUND.value())
						.build();
			}
			if(!authorityCollection.hasAuthority(MeetingAuth.EXTEND)) {
				return ResponseMessage.builder(ResponseMessage.StatusCode.FORBIDDEN)
						.message(ResponseMessage.MessageCode.ASSIGN.PUT_FAIL.value())
						.detail(ResponseMessage.DetailCode.ASSIGN.FORBIDDEN.value())
						.build();
			}
			assignVO = assignVO.toBuilder().writerKey(assignVO.getWriterId()).meetingStatus(MeetingStatus.START).finishDateTime(assignVO.getFinishDateTime().plusMinutes(minutes)).build();
			try {
				boolean result = false;
				if(assignVO.getReqKey() != null) {
					EwpRoomReqVO reqVO = EwpRoomReqVO.generate(assignVO);
					result = reqServ.putRoomAssignWithoutValidation(assignVO.getRoomType(), reqVO);
				}
				result = assignServ.putMeetingAssign(assignVO);
				if(result) {
					return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
							.message(ResponseMessage.MessageCode.ASSIGN.PUT_SUCCESS.value())
							.build();
				}else {
					throw new ApiDataOperationException(ErrorCode.MEETING_SCHEDULE.UPDATE_FAILED);
				}
			}catch(ApiException e) {
				log.error(e.getMessage());
				return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
						.message(ResponseMessage.MessageCode.ASSIGN.PUT_FAIL.value())
						.detail(e.getErrorCode().getMessage())
						.build();
			}catch(Exception e) {
				log.error(e.getMessage());
				return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
						.message(ResponseMessage.MessageCode.ASSIGN.PUT_FAIL.value())
						.detail(ErrorCode.MEETING_SCHEDULE.UPDATE_FAILED.getMessage())
						.build();
			}
		}else {
			throw new ApiNotFoundException(ErrorCode.MEETING_SCHEDULE.NOT_FOUND);
		}
	}
	
	@PutMapping("/meeting/schedule/{skdKey}/finish")
	public ResponseMessage putMeetingScheduleFinish(@PathVariable Integer skdKey) {
		Optional<EwpMeetingAssignVO> assignOpt = assignServ.getMeetingAssignOneByScheduleId(skdKey);
		if(assignOpt.isPresent()) {
			EwpMeetingAssignVO assignVO = assignOpt.get();
			ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(assignVO.getMeetingKey());
			if(authorityCollection.isEmpty()) {
				return ResponseMessage.builder(ResponseMessage.StatusCode.FORBIDDEN)
						.message(ResponseMessage.MessageCode.ASSIGN.PUT_FAIL.value())
						.detail(ResponseMessage.DetailCode.ASSIGN.NOT_FOUND.value())
						.build();
			}
			if(!authorityCollection.hasAuthority(MeetingAuth.FINISH)) {
				return ResponseMessage.builder(ResponseMessage.StatusCode.FORBIDDEN)
						.message(ResponseMessage.MessageCode.ASSIGN.PUT_FAIL.value())
						.detail(ResponseMessage.DetailCode.ASSIGN.FORBIDDEN.value())
						.build();
			}
			assignVO = assignVO.toBuilder().writerKey(assignVO.getWriterId()).finishDateTime(DateTimeUtil.getTruncatedDateTime(propertiesProvider.getReserveProperties().getIntervalMinute())).expDateTime(LocalDateTime.now().plusMinutes(1)).build();
			try {
				boolean result = false;
				if(assignVO.getReqKey() != null) {
					EwpRoomReqVO reqVO = EwpRoomReqVO.generate(assignVO);
					result = reqServ.putRoomAssignWithoutValidation(assignVO.getRoomType(), reqVO);
				}
				result = assignServ.putMeetingAssign(assignVO);
				if(result) {
					return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
							.message(ResponseMessage.MessageCode.ASSIGN.PUT_SUCCESS.value())
							.build();
				}else {
					throw new ApiDataOperationException(ErrorCode.MEETING_SCHEDULE.UPDATE_FAILED);
				}
			}catch(ApiException e) {
				log.error(e.getMessage());
				return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
						.message(ResponseMessage.MessageCode.ASSIGN.PUT_FAIL.value())
						.detail(e.getErrorCode().getMessage())
						.build();
			}catch(Exception e) {
				log.error(e.getMessage());
				return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
						.message(ResponseMessage.MessageCode.ASSIGN.PUT_FAIL.value())
						.detail(ErrorCode.MEETING_SCHEDULE.UPDATE_FAILED.getMessage())
						.build();
			}
		}else {
			throw new ApiNotFoundException(ErrorCode.MEETING_SCHEDULE.NOT_FOUND);
		}
	}
}
