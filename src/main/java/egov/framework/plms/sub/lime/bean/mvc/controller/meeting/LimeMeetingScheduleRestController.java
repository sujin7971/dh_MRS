package egov.framework.plms.sub.lime.bean.mvc.controller.meeting;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import egov.framework.plms.main.bean.component.properties.ReserveConfigProperties;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingAssignVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingScheduleDTO;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingScheduleVO;
import egov.framework.plms.main.core.exception.ApiDataOperationException;
import egov.framework.plms.main.core.exception.ApiNotFoundException;
import egov.framework.plms.main.core.exception.abst.ApiException;
import egov.framework.plms.main.core.model.auth.ResourceAuthorityCollection;
import egov.framework.plms.main.core.model.enums.auth.MeetingAuth;
import egov.framework.plms.main.core.model.enums.error.ErrorCode;
import egov.framework.plms.main.core.model.enums.meeting.MeetingStatus;
import egov.framework.plms.main.core.model.response.ResponseMessage;
import egov.framework.plms.main.core.util.DateTimeUtil;
import egov.framework.plms.sub.ewp.bean.mvc.entity.assign.EwpRoomReqVO;
import egov.framework.plms.sub.lime.bean.component.auth.LimeResourceAuthorityProvider;
import egov.framework.plms.sub.lime.bean.mvc.service.meeting.LimeMeetingAssignService;
import egov.framework.plms.sub.lime.bean.mvc.service.meeting.LimeMeetingScheduleService;
import egov.framework.plms.sub.lime.bean.mvc.service.organization.LimeUserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/lime")
@RequiredArgsConstructor
@Profile("lime")
public class LimeMeetingScheduleRestController {
	private final LimeResourceAuthorityProvider authorityProvider;
	private final ReserveConfigProperties reserveProperties;
	
	private final LimeMeetingScheduleService scheServ;
	private final LimeMeetingAssignService assignServ;
	private final LimeUserInfoService userServ;
	/**
	 * 해당 사용신청 다음에 예정된 사용신청 정보 조회.
	 * @param scheduleId 스케줄키
	 * @return 사용신청 정보 반환
	 */
	@GetMapping("/meeting/schedule/{scheduleId}/next")
	public MeetingScheduleDTO getMeetingScheduleNextOne(Model model, @PathVariable Integer scheduleId) {
		Optional<MeetingScheduleVO> scheOpt = scheServ.getMeetingScheduleOne(scheduleId);
		if(scheOpt.isPresent()) {
			MeetingScheduleVO nowScheVO = scheOpt.get();
			MeetingScheduleVO nextScheVO = null;
			nextScheVO = scheServ.getMeetingScheduleNextOne(scheduleId).orElse(null);
			return Optional.ofNullable(nextScheVO).map(vo -> vo.toBuilder().writer(userServ.selectUserInfoOne(vo.getWriterId()).orElse(null)).build().convert()).orElse(null);
		}else {
			throw new ApiNotFoundException(ErrorCode.MEETING_SCHEDULE.NOT_FOUND);
		}
	}
	
	@PutMapping("/meeting/schedule/{scheduleId}/extend/minutes/{minutes}")
	public ResponseMessage putMeetingScheduleExt(@PathVariable Integer scheduleId, @PathVariable Integer minutes) {
		Optional<MeetingAssignVO> assignOpt = assignServ.getMeetingAssignOneByScheduleId(scheduleId);
		if(assignOpt.isPresent()) {
			MeetingAssignVO assignVO = assignOpt.get();
			ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(assignVO.getMeetingId());
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
			assignVO = assignVO.toBuilder().writerId(assignVO.getWriterId()).meetingStatus(MeetingStatus.START).finishDateTime(assignVO.getFinishDateTime().plusMinutes(minutes)).build();
			try {
				boolean result = false;
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
	
	@PutMapping("/meeting/schedule/{scheduleId}/finish")
	public ResponseMessage putMeetingScheduleFinish(@PathVariable Integer scheduleId) {
		Optional<MeetingAssignVO> assignOpt = assignServ.getMeetingAssignOneByScheduleId(scheduleId);
		if(assignOpt.isPresent()) {
			MeetingAssignVO assignVO = assignOpt.get();
			ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(assignVO.getMeetingId());
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
			assignVO = assignVO.toBuilder().writerId(assignVO.getWriterId()).finishDateTime(DateTimeUtil.getTruncatedDateTime(reserveProperties.getIntervalMinute())).expDateTime(LocalDateTime.now().plusMinutes(1)).build();
			try {
				boolean result = false;
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
