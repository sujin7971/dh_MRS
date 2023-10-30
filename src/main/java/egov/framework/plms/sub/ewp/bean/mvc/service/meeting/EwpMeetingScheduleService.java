package egov.framework.plms.sub.ewp.bean.mvc.service.meeting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.mvc.service.meeting.abst.MeetingScheduleAbstractService;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingScheduleVO;
import egov.framework.plms.sub.ewp.bean.mvc.mapper.maria.meeting.EwpMeetingScheduleMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile("ewp")
@Primary
public class EwpMeetingScheduleService extends MeetingScheduleAbstractService<EwpMeetingScheduleVO> {
	private EwpMeetingScheduleMapper mapper;
	
	public EwpMeetingScheduleService(@Autowired EwpMeetingScheduleMapper mapper) {
		super(mapper);
		this.mapper = mapper;
	}
}
