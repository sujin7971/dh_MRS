package egov.framework.plms.sub.lime.bean.mvc.service.meeting;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingAttendeeVO;
import egov.framework.plms.main.bean.mvc.service.meeting.abst.MeetingAttendeeAbstractService;
import egov.framework.plms.main.core.model.enums.meeting.AttendRole;
import egov.framework.plms.sub.lime.bean.mvc.mapper.meeting.LimeMeetingAttendeeMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile("lime")
@Primary
public class LimeMeetingAttendeeService extends MeetingAttendeeAbstractService<MeetingAttendeeVO>{
	private final LimeMeetingAttendeeMapper mapper;
	
	public LimeMeetingAttendeeService(@Autowired LimeMeetingAttendeeMapper mapper) {
		super(mapper);
		this.mapper = mapper;
	}

	@Override
	public boolean deleteMeetingAttendeeAll(Integer meetingId) {
		return super.deleteMeetingAttendeeList(MeetingAttendeeVO.builder().meetingId(meetingId).build());
	}
	@Override
	public Integer getAttendId(String userId, Integer meetingId) {
		return mapper.getAttendId(MeetingAttendeeVO.builder().userId(userId).meetingId(meetingId).build());
	}
	/**
	 * 해당 참석자 정보에 유저 상세 정보 조인
	 * @param attendee 조인을 할 참석자 정보
	 * @return
	 */
	private MeetingAttendeeVO getJoinedAttendeeOne(MeetingAttendeeVO attendeeVO) {
		return attendeeVO;
	}
	
	@Override
	public List<MeetingAttendeeVO> getMeetingAttendeeListByUser(String userId) {
		return getMeetingAttendeeList(MeetingAttendeeVO.builder().userId(userId).build());
	}
	@Override
	public List<MeetingAttendeeVO> getMeetingAttendeeListByUser(String userId, AttendRole attendRole) {
		return getMeetingAttendeeList(MeetingAttendeeVO.builder().userId(userId).attendRole(attendRole).build());
	}
	@Override
	public List<MeetingAttendeeVO> getMeetingAttendeeListByMeeting(Integer meetingId) {
		return getMeetingAttendeeList(MeetingAttendeeVO.builder().meetingId(meetingId).build());
	}
	@Override
	public List<MeetingAttendeeVO> getMeetingAttendeeListByMeeting(Integer meetingId, AttendRole attendRole) {
		return getMeetingAttendeeList(MeetingAttendeeVO.builder().meetingId(meetingId).attendRole(attendRole).build());
	}
	@Override
	public List<MeetingAttendeeVO> getMeetingAttendeeList(MeetingAttendeeVO attendee) {
		List<MeetingAttendeeVO> resultList = Optional.ofNullable(attendee).map(param -> mapper.getMeetingAttendeeList(attendee)).orElse(null);
		return Optional.ofNullable(resultList).map(list -> list.stream().map(vo -> getJoinedAttendeeOne(vo)).collect(Collectors.toList())).orElse(null);
	}
}
