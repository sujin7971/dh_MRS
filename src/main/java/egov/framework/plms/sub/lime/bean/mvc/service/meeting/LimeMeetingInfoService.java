package egov.framework.plms.sub.lime.bean.mvc.service.meeting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingInfoVO;
import egov.framework.plms.main.bean.mvc.service.meeting.abst.MeetingInfoAbstractService;
import egov.framework.plms.sub.lime.bean.mvc.mapper.meeting.LimeMeetingInfoMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile("lime")
@Primary
public class LimeMeetingInfoService extends MeetingInfoAbstractService<MeetingInfoVO> {
	private LimeMeetingInfoMapper mapper;
	
	public LimeMeetingInfoService(@Autowired LimeMeetingInfoMapper mapper) {
		super(mapper);
		this.mapper = mapper;
	}

	@Override
	public boolean putMeetingStickyBit(Integer meetingId, Integer stickyBit) {
		try {
			Integer result = mapper.putMeetingStickyBit(MeetingInfoVO.builder().meetingId(meetingId).stickyBit(stickyBit).build());
			return (result == 0)?false:true;
		}catch(Exception e) {
			log.error("Failed to put MeetingInfo stickyBit with id: {} & value: {}", meetingId, stickyBit);
			log.error("Failed to put MeetingInfo stickyBit messages: {}", e.toString());
			e.printStackTrace();
			return false;
		}
	}
}
