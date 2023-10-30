package egov.framework.plms.main.bean.mvc.service.meeting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingInfoVO;
import egov.framework.plms.main.bean.mvc.mapper.meeting.MeetingInfoMapper;
import egov.framework.plms.main.bean.mvc.service.meeting.abst.MeetingInfoAbstractService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("MeetingInfoService")
public class MeetingInfoService extends MeetingInfoAbstractService<MeetingInfoVO> {
	public MeetingInfoService(@Autowired MeetingInfoMapper mapper) {
		super(mapper);
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
