package egov.framework.plms.sub.lime.bean.mvc.service.meeting;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingArchiveVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingAssignVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingInfoVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingScheduleVO;
import egov.framework.plms.main.bean.mvc.service.meeting.abst.MeetingAssignAbstractService;
import egov.framework.plms.main.core.exception.ApiDataOperationException;
import egov.framework.plms.main.core.model.enums.error.ErrorCode;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingArchiveVO;
import egov.framework.plms.sub.lime.bean.mvc.mapper.meeting.LimeMeetingAssignMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile("lime")
@Primary
public class LimeMeetingAssignService extends MeetingAssignAbstractService<MeetingAssignVO> {
	private LimeMeetingAssignMapper mapper;
	@Autowired
	private LimeMeetingScheduleService scheServ;
	@Autowired
	private LimeMeetingInfoService mtServ;
	public LimeMeetingAssignService(@Autowired LimeMeetingAssignMapper mapper) {
		super(mapper);
		this.mapper = mapper;
	}

	@Override
	public MeetingAssignVO postMeetingAssign(MeetingAssignVO params) throws ApiDataOperationException {
		MeetingInfoVO meeting = params.getMeetingInfo();
		if(!mtServ.postMeetingInfo(meeting)) {
			throw new ApiDataOperationException(ErrorCode.MEETING_INFO.POST_FAILED);
		}
		Integer meetingId = meeting.getMeetingId();
		MeetingScheduleVO schedule = params.toBuilder().meetingId(meetingId).build().getMeetingSchedule();
		if(!scheServ.postMeetingSchedule(schedule)) {
			mtServ.deleteMeetingInfo(meetingId);
			throw new ApiDataOperationException(ErrorCode.MEETING_SCHEDULE.POST_FAILED);
		}
		Integer scheduleId = schedule.getScheduleId();
		return params.toBuilder().scheduleId(scheduleId).meetingId(meetingId).build();
	}

	@Override
	public boolean putMeetingAssign(MeetingAssignVO params) throws ApiDataOperationException {
		MeetingScheduleVO schedule = params.getMeetingSchedule();
		MeetingInfoVO meeting = params.getMeetingInfo();
		if(!mtServ.putMeetingInfo(meeting)) {
			throw new ApiDataOperationException(ErrorCode.MEETING_INFO.UPDATE_FAILED);
		}
		if(!scheServ.putMeetingSchedule(schedule)) {
			throw new ApiDataOperationException(ErrorCode.MEETING_SCHEDULE.UPDATE_FAILED);
		}
		return true;
	}

	@Override
	public boolean deleteMeetingAssign(Integer scheduleId) throws ApiDataOperationException {
		Optional<MeetingAssignVO> assignOpt = getMeetingAssignOneByScheduleId(scheduleId);
		if(assignOpt.isPresent()) {
			MeetingAssignVO assign = assignOpt.get();
			MeetingScheduleVO schedule = assign.getMeetingSchedule();
			MeetingInfoVO meeting = assign.getMeetingInfo();
			if(!mtServ.putMeetingInfo(meeting)) {
				throw new ApiDataOperationException(ErrorCode.MEETING_INFO.DELETE_FAILED);
			}
			if(!scheServ.putMeetingSchedule(schedule)) {
				throw new ApiDataOperationException(ErrorCode.MEETING_SCHEDULE.DELETE_FAILED);
			}
			return true;
		}else {
			throw new NoSuchElementException();
		}
	}
	
	@Override
	public Optional<MeetingAssignVO> getMeetingAssignOneByScheduleId(Integer scheduleId) {
		MeetingAssignVO params = MeetingAssignVO.builder().scheduleId(scheduleId).build();
		return super.getMeetingAssignOne(params);
	}

	@Override
	public Optional<MeetingAssignVO> getMeetingAssignOneByMeetingId(Integer meetingId) {
		MeetingAssignVO params = MeetingAssignVO.builder().meetingId(meetingId).build();
		return super.getMeetingAssignOne(params);
	}
}
