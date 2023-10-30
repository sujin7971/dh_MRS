package egov.framework.plms.sub.lime.bean.mvc.service.meeting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingScheduleVO;
import egov.framework.plms.main.bean.mvc.service.meeting.abst.MeetingScheduleAbstractService;
import egov.framework.plms.sub.lime.bean.mvc.mapper.meeting.LimeMeetingScheduleMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile("lime")
@Primary
public class LimeMeetingScheduleService extends MeetingScheduleAbstractService<MeetingScheduleVO> {
	private LimeMeetingScheduleMapper mapper;
	
	public LimeMeetingScheduleService(@Autowired LimeMeetingScheduleMapper mapper) {
		super(mapper);
		this.mapper = mapper;
	}
}
