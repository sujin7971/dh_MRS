package egov.framework.plms.sub.ewp.bean.component.scheduler;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import egov.framework.plms.sub.ewp.bean.mvc.service.file.EwpMeetingFileInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "config.scheduledTasks.fileMaintenanceScheduler", name="enabled", havingValue = "true", matchIfMissing = false)
@Profile("ewp")
public class EwpFileMaintenanceScheduler {
	private final EwpMeetingFileInfoService fileServ;
	
	@Scheduled(cron="${config.scheduledTasks.fileMaintenanceScheduler.cron}")
	private void scheduler() {
		fileServ.removeExpFileOnDisk();
		fileServ.removeDeletedFileOnDisk();
	}
}
