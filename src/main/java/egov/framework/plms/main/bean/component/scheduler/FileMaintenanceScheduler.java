package egov.framework.plms.main.bean.component.scheduler;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import egov.framework.plms.main.bean.component.common.abst.FileModelPathResolver;
import egov.framework.plms.main.bean.mvc.entity.file.abst.FileDetailModelVO;
import egov.framework.plms.main.bean.mvc.service.file.FileDiskService;
import egov.framework.plms.main.bean.mvc.service.file.abst.FileInfoAbstractService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "config.scheduledTasks.fileMaintenanceScheduler", name="enabled", havingValue = "true", matchIfMissing = false)
public class FileMaintenanceScheduler {
	private final FileInfoAbstractService fileServ;
	private final FileDiskService diskServ;
	private final FileModelPathResolver filePathResolver;
	@Scheduled(cron="${config.scheduledTasks.fileMaintenanceScheduler.cron}")
	private void scheduler() {
		cleanDeletedFileOnDisk();
	}

	private void cleanDeletedFileOnDisk() {
		// TODO Auto-generated method stub
		List<FileDetailModelVO> fileModelList = fileServ.selectDeletedFileListOnDisk();
		log.info("fileList: {}", fileModelList);
		fileModelList.forEach(fileModel -> {
			String FOLDER_PATH = filePathResolver.getUploadedFilePath(fileModel);
			boolean isRemoved = diskServ.removeFolder(FOLDER_PATH);
			if(isRemoved) {
				fileServ.updateFileStatusToRemove(fileModel.getFileId());
			}
		});
	}
}
