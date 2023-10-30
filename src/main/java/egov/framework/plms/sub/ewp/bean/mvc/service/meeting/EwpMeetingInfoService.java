package egov.framework.plms.sub.ewp.bean.mvc.service.meeting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.mvc.service.meeting.abst.MeetingInfoAbstractService;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.mapper.maria.meeting.EwpMeetingInfoMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile("ewp")
@Primary
public class EwpMeetingInfoService extends MeetingInfoAbstractService<EwpMeetingInfoVO> {
	private EwpMeetingInfoMapper mapper;
	
	public EwpMeetingInfoService(@Autowired EwpMeetingInfoMapper mapper) {
		super(mapper);
		this.mapper = mapper;
	}

	@Override
	public boolean putMeetingStickyBit(Integer meetingId, Integer stickyBit) {
		try {
			Integer result = mapper.putMeetingStickyBit(EwpMeetingInfoVO.builder().meetingKey(meetingId).stickyBit(stickyBit).build());
			return (result == 0)?false:true;
		}catch(Exception e) {
			log.error("Failed to put MeetingInfo stickyBit with id: {} & value: {}", meetingId, stickyBit);
			log.error("Failed to put MeetingInfo stickyBit messages: {}", e.toString());
			e.printStackTrace();
			return false;
		}
	}
}
