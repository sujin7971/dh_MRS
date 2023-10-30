package egov.framework.plms.sub.ewp.bean.component.scheduler;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import egov.framework.plms.main.bean.mvc.service.file.FileCvtService;
import egov.framework.plms.sub.ewp.bean.component.converter.MeetingFileConvertManager;
import egov.framework.plms.sub.ewp.bean.mvc.entity.file.MeetingFileConvertVO;
import egov.framework.plms.sub.ewp.bean.mvc.service.file.EwpMeetingCvtService;
import egov.framework.plms.sub.ewp.bean.mvc.service.file.EwpMeetingFileInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "config.scheduledTasks.fileConversionScheduler", name="enabled", havingValue = "true", matchIfMissing = false)
@Profile("ewp")
public class EwpMeetingFileConversionScheduler {
	private final EwpMeetingFileInfoService fileServ;
	private final EwpMeetingCvtService meetingCvtServ;
	private final MeetingFileConvertManager meetingCvtManager;
	@PostConstruct
	private void init() {
		meetingCvtServ.putUnfinishedCvtInit();
	}
	
	@Scheduled(cron="${config.scheduledTasks.fileConversionScheduler.fileConversionCron}")	
	private void scheduler() {
		List<MeetingFileConvertVO> meetingCvtList = meetingCvtServ.getCvtListToProcess(null);
		meetingCvtList.isEmpty();
		for(MeetingFileConvertVO cvt : meetingCvtList) {
			try {
				meetingCvtManager.addToQueue(cvt);
			}catch (Exception e) {
				log.error(e.toString(), e);
			}
		}
		meetingCvtManager.passCvtToProcessor();
	}
}
