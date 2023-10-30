package egov.framework.plms.main.bean.mvc.service.meeting;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingAssignVO;
import egov.framework.plms.main.bean.mvc.mapper.meeting.MeetingAssignMapper;
import egov.framework.plms.main.bean.mvc.service.meeting.abst.MeetingAssignAbstractService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("MeetingAssignService")
public class MeetingAssignService extends MeetingAssignAbstractService<MeetingAssignVO> {
	public MeetingAssignService(@Autowired MeetingAssignMapper mapper) {
		super(mapper);
	}

	@Override
	public MeetingAssignVO postMeetingAssign(MeetingAssignVO params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean putMeetingAssign(MeetingAssignVO params) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteMeetingAssign(Integer scheduleId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Optional<MeetingAssignVO> getMeetingAssignOneByScheduleId(Integer scheduleId) {
		// TODO Auto-generated method stub
		MeetingAssignVO params = MeetingAssignVO.builder().scheduleId(scheduleId).build();
		return super.getMeetingAssignOne(params);
	}

	@Override
	public Optional<MeetingAssignVO> getMeetingAssignOneByMeetingId(Integer meetingId) {
		// TODO Auto-generated method stub
		MeetingAssignVO params = MeetingAssignVO.builder().meetingId(meetingId).build();
		return super.getMeetingAssignOne(params);
	}
}
