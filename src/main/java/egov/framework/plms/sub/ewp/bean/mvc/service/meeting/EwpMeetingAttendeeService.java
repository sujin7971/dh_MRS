package egov.framework.plms.sub.ewp.bean.mvc.service.meeting;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.mvc.service.meeting.abst.MeetingAttendeeAbstractService;
import egov.framework.plms.main.core.model.enums.meeting.AttendRole;
import egov.framework.plms.main.core.model.response.SocketMessage;
import egov.framework.plms.main.core.model.response.SocketMessage.MessageType;
import egov.framework.plms.main.core.model.response.SocketMessage.ResourceType;
import egov.framework.plms.sub.ewp.bean.component.websocket.EwpMeetingMessageBroker;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingAttendeeVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingSecurityAgreementVO;
import egov.framework.plms.sub.ewp.bean.mvc.mapper.maria.meeting.EwpMeetingAttendeeMapper;
import egov.framework.plms.sub.ewp.bean.mvc.mapper.maria.meeting.EwpMeetingSecurityAgreementMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile("ewp")
@Primary
public class EwpMeetingAttendeeService extends MeetingAttendeeAbstractService<EwpMeetingAttendeeVO>{
	private final EwpMeetingAttendeeMapper attendeeMapper;
	@Autowired
	private EwpMeetingSecurityAgreementMapper securityAgreementMapper;
	@Autowired
	private EwpMeetingMessageBroker messageBroker;
	
	public EwpMeetingAttendeeService(@Autowired EwpMeetingAttendeeMapper mapper) {
		super(mapper);
		this.attendeeMapper = mapper;
	}
	
	@Override
	public boolean postMeetingAttendee(EwpMeetingAttendeeVO attendee) {
		// TODO Auto-generated method stub
		boolean result = super.postMeetingAttendee(attendee);
		if(result) {
			messageBroker.broadCastMsg(attendee.getMeetingKey(), SocketMessage.builder(MessageType.UPDATE).resourceType(ResourceType.ATTENDEE).data(attendee.getAttendId()).build());
		}
		return result;
	}

	@Override
	public boolean putMeetingAttendee(EwpMeetingAttendeeVO attendee) {
		// TODO Auto-generated method stub
		boolean result = super.putMeetingAttendee(attendee);
		if(result && attendee.getMeetingId() != null) {
			Integer attendKey = attendee.getAttendKey();
			if(attendKey == null) {
				attendKey = getAttendId(attendee.getUserId(), attendee.getMeetingKey());
			}
			messageBroker.broadCastMsg(attendee.getMeetingKey(), SocketMessage.builder(MessageType.UPDATE).resourceType(ResourceType.ATTENDEE).data(attendee.getAttendId()).build());
		}
		return result;
	}

	@Override
	public boolean deleteMeetingAttendeeAll(Integer meetingId) {
		return super.deleteMeetingAttendeeList(EwpMeetingAttendeeVO.builder().meetingKey(meetingId).build());
	}
	@Override
	public Integer getAttendId(String userId, Integer meetingId) {
		return attendeeMapper.getAttendId(EwpMeetingAttendeeVO.builder().userKey(userId).meetingKey(meetingId).build());
	}
	/**
	 * 해당 참석자 정보에 유저 상세 정보 조인
	 * @param attendee 조인을 할 참석자 정보
	 * @return
	 */
	private EwpMeetingAttendeeVO getJoinedAttendeeOne(EwpMeetingAttendeeVO attendeeVO) {
		return attendeeVO;
	}
	
	@Override
	public List<EwpMeetingAttendeeVO> getMeetingAttendeeListByUser(String userId) {
		return getMeetingAttendeeList(EwpMeetingAttendeeVO.builder().userKey(userId).build());
	}
	@Override
	public List<EwpMeetingAttendeeVO> getMeetingAttendeeListByUser(String userId, AttendRole attendRole) {
		return getMeetingAttendeeList(EwpMeetingAttendeeVO.builder().userKey(userId).attendRole(attendRole).build());
	}
	@Override
	public List<EwpMeetingAttendeeVO> getMeetingAttendeeListByMeeting(Integer meetingId) {
		return getMeetingAttendeeList(EwpMeetingAttendeeVO.builder().meetingKey(meetingId).build());
	}
	@Override
	public List<EwpMeetingAttendeeVO> getMeetingAttendeeListByMeeting(Integer meetingId, AttendRole attendRole) {
		return getMeetingAttendeeList(EwpMeetingAttendeeVO.builder().meetingKey(meetingId).attendRole(attendRole).build());
	}
	@Override
	public List<EwpMeetingAttendeeVO> getMeetingAttendeeList(EwpMeetingAttendeeVO attendee) {
		List<EwpMeetingAttendeeVO> resultList = Optional.ofNullable(attendee).map(param -> attendeeMapper.getMeetingAttendeeList(attendee)).orElse(null);
		return Optional.ofNullable(resultList).map(list -> list.stream().map(vo -> getJoinedAttendeeOne(vo)).collect(Collectors.toList())).orElse(null);
	}
	
	public boolean insertMeetingSecurityAgreementOne(Integer attendKey, Integer meeintKey, String signSrc) {
		try {
			Integer result = securityAgreementMapper.insertMeetingSecurityAgreementOne(EwpMeetingSecurityAgreementVO.builder().attendId(attendKey).meetingId(meeintKey).signSrc(signSrc).build());
			return (result == 0)?false:true;
		}catch(Exception e) {
			return false;
		}
	}
	
	public boolean deleteMeetingSecurityAgreementOne(Integer attendKey) {
		try {
			Integer result = securityAgreementMapper.deleteMeetingSecurityAgreementOne(attendKey);
			return (result == 0)?false:true;
		}catch(Exception e) {
			return false;
		}
	}
	
	public Optional<EwpMeetingSecurityAgreementVO> selectMeetingSecurityAgreementOne(Integer attendKey) {
		try {
			EwpMeetingSecurityAgreementVO result = securityAgreementMapper.selectMeetingSecurityAgreementOne(attendKey);
			return Optional.ofNullable(result);
		}catch(Exception e) {
			return Optional.empty();
		}
	}
	
	public List<EwpMeetingSecurityAgreementVO> selectMeetingSecurityAgreementAll(Integer meetingId) {
		return securityAgreementMapper.selectMeetingSecurityAgreementAll(meetingId);
	}
}
