package egov.framework.plms.main.bean.mvc.service.meeting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingScheduleVO;
import egov.framework.plms.main.bean.mvc.mapper.meeting.MeetingScheduleMapper;
import egov.framework.plms.main.bean.mvc.service.meeting.abst.MeetingScheduleAbstractService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("MeetingScheduleService")
public class MeetingScheduleService extends MeetingScheduleAbstractService<MeetingScheduleVO> {
	public MeetingScheduleService(@Autowired MeetingScheduleMapper mapper) {
		super(mapper);
	}
}
