package egov.framework.plms.sub.lime.bean.component.scheduler;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import egov.framework.plms.main.bean.mvc.entity.file.FileConvertVO;
import egov.framework.plms.main.bean.mvc.entity.file.FileDetailVO;
import egov.framework.plms.main.bean.mvc.service.file.FileCvtService;
import egov.framework.plms.main.core.model.enums.file.ConversionStatus;
import egov.framework.plms.main.core.model.enums.file.ConversionType;
import egov.framework.plms.main.core.model.enums.file.FileCategory;
import egov.framework.plms.main.core.util.FileCommUtil;
import egov.framework.plms.sub.lime.bean.component.file.LimeFileModelPathResolver;
import egov.framework.plms.sub.lime.bean.mvc.service.meeting.LimeMeetingFileInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * 이 클래스는 회의 자료와 관련된 파일 변환 작업을 주기적으로 스케줄링 합니다. 
 * 필요한 변환 작업을 조회하고, 사전 처리 로직을 실행한 후 변환 요청을 등록합니다.
 * 변환 작업은 회의 자료의 이미지 생성, 판서본 파일의 이미지 통합, 판서본 파일의 PDF 생성 등을 포함합니다.
 * 
 * @author mckim
 * @version 1.0
 * @since 2023. 6. 15
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "config.scheduledTasks.fileConversionScheduler", name="enabled", havingValue = "true", matchIfMissing = false)
@Profile("lime")
public class LimeMeetingFileConversionScheduler {
	private final LimeMeetingFileInfoService fileServ;
	private final LimeFileModelPathResolver filePathResolver;
	private final FileCvtService driveCvtServ;
	
	/**
    * 스케줄러는 회의 자료의 이미지 생성, 판서본 파일의 이미지 통합, 판서본 파일의 PDF 생성을 주기적으로 실행합니다.
    * 실행 시점은 "config.scheduledTasks.fileConversionScheduler.fileConversionCron" 프로퍼티에 정의된 cron 표현식을 따릅니다.
    */
	@Scheduled(cron="${config.scheduledTasks.fileConversionScheduler.fileConversionCron}")	
	private void scheduler() {
		requestMeetingMaterialFilesToGenerateImages();
		requestMeetingCopyFilesToIntegrateImages();
		requestMeetingCopyFilesToGeneratePdf();
		requestMeetingMemoFilesToIntegrateImages();
		requestMeetingMemoFilesToGeneratePdf();
	}

	/**
	 * 이미지 생성이 필요한 등록된 회의 자료 목록을 조회하여 변환 요청을 등록.
	 *<br><br>
	 * 원본 파일이 이미지인 경우, 해당 이미지를 WEBP 형식으로 변환하는 작업을 요청. 
	 * 반면에 일반 문서의 경우 PDF 파일을 이미지로 변환하는 작업을 요청.
	 *<br><br>
	 * 주의: 일반 문서는 해당 문서에 대한 PDF 변환이 이미 완료된 경우에만 이미지 변환 작업을 요청.
	 */
	private void requestMeetingMaterialFilesToGenerateImages() {
		// TODO Auto-generated method stub
		List<FileDetailVO> files = fileServ.selectMeetingMaterialFilesToGenerateImages();
		files.forEach(fileModel -> {
			Integer fileId = fileModel.getFileId();
			Integer meetingId = fileModel.getRelatedEntityId();
			FileCategory fileCategory = fileModel.getFileCategory();
			if(fileCategory == FileCategory.IMG) {
				String sourcePath = filePathResolver.getUploadedFilePath(fileModel);
				String destinationPath = filePathResolver.getOriginalImageCollectionPath(fileModel);
				fileServ.updateFileOne(FileDetailVO.builder().fileId(fileId).conversionStatus(ConversionStatus.NOT_STARTED).build());
				FileConvertVO convertVO = FileConvertVO.builder().fileId(fileId).conversionType(ConversionType.IMAGE_TO_WEBP).sourcePath(sourcePath).destinationPath(destinationPath).build();
				driveCvtServ.insertCvtOne(convertVO);
			}else {
				String sourcePath = filePathResolver.getConvertedFilePathForPreview(fileModel);
				String destinationPath = filePathResolver.getOriginalImageCollectionPath(fileModel);
				fileServ.updateFileOne(FileDetailVO.builder().fileId(fileId).conversionStatus(ConversionStatus.NOT_STARTED).build());
				FileConvertVO convertVO = FileConvertVO.builder().fileId(fileId).conversionType(ConversionType.PDF_TO_IMAGES).sourcePath(sourcePath).destinationPath(destinationPath).build();
				driveCvtServ.insertCvtOne(convertVO);
			}
		});
	}
	
	/**
	 * 등록된 판서본 파일 중에서, 판서한 이미지 파일을 WEBP 형식으로 변환하고 통합 폴더로 이동해야 할 목록을 조회하여 변환 요청을 등록.
	 *<br><br>
	 * 이 메서드는 회의 참석자가 판서한 이미지를 변환하고, 판서본 PDF 파일 생성을 위해 필요한 이미지 파일을 모아둘 통합 폴더로 이동하는 작업을 담당.
	 */
	private void requestMeetingCopyFilesToIntegrateImages() {
		// TODO Auto-generated method stub
		List<FileDetailVO> files = fileServ.selectMeetingCopyFilesToIntegrateImages();
		files.forEach(copyFileModel -> {
			Integer sourceId = copyFileModel.getSourceId();
			FileDetailVO sourceFileModel = fileServ.selectFileOne(sourceId).get();
			Integer fileId = copyFileModel.getFileId();
			String userId = copyFileModel.getUploaderId();
			String NOTE_FOLDER_PATH = filePathResolver.getAttendeeWrittenImagePath(sourceFileModel, userId);
			String PRINT_FOLDER_PATH = filePathResolver.getIntegratedImagePathForPrint(copyFileModel);
			File PRINT_FOLDER = new File(PRINT_FOLDER_PATH);
			PRINT_FOLDER.mkdirs();
			fileServ.updateFileOne(FileDetailVO.builder().fileId(fileId).conversionStatus(ConversionStatus.NOT_STARTED).build());
			FileConvertVO convertVO = FileConvertVO.builder().fileId(fileId).conversionType(ConversionType.IMAGE_TO_WEBP).sourcePath(NOTE_FOLDER_PATH).destinationPath(PRINT_FOLDER_PATH).build();
			driveCvtServ.insertCvtOne(convertVO);
		});
	}
	
	/**
	 * 등록된 판서본 파일 중에서, 통합 폴더에 저장된 판서 이미지를 PDF 파일로 변환해야 할 목록을 조회하여 변환 요청을 등록.
	 *<br><br>
	 * 이 메서드는 원본 이미지 폴더로부터 통합 폴더 내 누락된 페이지를 가져와 저장하고, 
	 * 전체 페이지 이미지가 모아진 폴더를 PDF 파일로 변환하는 작업을 담당.
	 */
	private void requestMeetingCopyFilesToGeneratePdf() {
		// TODO Auto-generated method stub
		List<FileDetailVO> files = fileServ.selectMeetingCopyFilesToGeneratePdf();
		files.forEach(copyFileModel -> {
			Integer sourceId = copyFileModel.getSourceId();
			FileDetailVO sourceFileModel = fileServ.selectFileOne(sourceId).get();
			Integer fileId = copyFileModel.getFileId();
			String SOURCE_FOLDER_PATH = filePathResolver.getOriginalImageCollectionPath(sourceFileModel);
			String PRINT_FOLDER_PATH = filePathResolver.getIntegratedImagePathForPrint(copyFileModel);
			String COPY_FILE_PATH = filePathResolver.getUploadedFilePath(copyFileModel);
			File SOURCE_FOLDER = new File(SOURCE_FOLDER_PATH);
			File PRINT_FOLDER = new File(PRINT_FOLDER_PATH);
			try {
				FileCommUtil.copyFilesWithoutOverwriting(SOURCE_FOLDER, PRINT_FOLDER);
				fileServ.updateFileOne(FileDetailVO.builder().fileId(fileId).conversionStatus(ConversionStatus.NOT_STARTED).build());
				FileConvertVO convertVO = FileConvertVO.builder().fileId(fileId).conversionType(ConversionType.IMAGES_TO_PDF).sourcePath(PRINT_FOLDER_PATH).destinationPath(COPY_FILE_PATH).build();
				driveCvtServ.insertCvtOne(convertVO);
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		});
	}
	
	/**
	 * 등록된 메모장 파일 중에서, 메모한 이미지 파일을 WEBP 형식으로 변환하고 통합 폴더로 이동해야 할 목록을 조회하여 변환 요청을 등록.
	 *<br><br>
	 * 이 메서드는 회의 참석자가 메모한 이미지를 변환하고, 메모 PDF 파일 생성을 위해 필요한 이미지 파일을 모아둘 통합 폴더로 이동하는 작업을 담당.
	 */
	private void requestMeetingMemoFilesToIntegrateImages() {
		// TODO Auto-generated method stub
		List<FileDetailVO> files = fileServ.selectMeetingMemoFilesToIntegrateImages();
		files.forEach(memoFileModel -> {
			Integer fileId = memoFileModel.getFileId();
			String userId = memoFileModel.getUploaderId();
			String MEMO_FOLDER_PATH = filePathResolver.getMeetingMemoImageCollectionPath(memoFileModel.getRelatedEntityId(), userId);
			String PRINT_FOLDER_PATH = filePathResolver.getIntegratedImagePathForPrint(memoFileModel);
			File PRINT_FOLDER = new File(PRINT_FOLDER_PATH);
			PRINT_FOLDER.mkdirs();
			fileServ.updateFileOne(FileDetailVO.builder().fileId(fileId).conversionStatus(ConversionStatus.NOT_STARTED).build());
			FileConvertVO convertVO = FileConvertVO.builder().fileId(fileId).conversionType(ConversionType.IMAGE_TO_WEBP).sourcePath(MEMO_FOLDER_PATH).destinationPath(PRINT_FOLDER_PATH).build();
			driveCvtServ.insertCvtOne(convertVO);
		});
	}
	
	/**
	 * 등록된 메모장 파일 중에서, 통합 폴더에 저장된 메모한 이미지를 PDF 파일로 변환해야 할 목록을 조회하여 변환 요청을 등록.
	 *<br><br>
	 * 이 메서드는 전체 메모한 페이지 이미지가 모아진 폴더를 PDF 파일로 변환하는 작업을 담당.
	 */
	private void requestMeetingMemoFilesToGeneratePdf() {
		// TODO Auto-generated method stub
		List<FileDetailVO> files = fileServ.selectMeetingMemoFilesToGeneratePdf();
		files.forEach(memoFileModel -> {
			Integer fileId = memoFileModel.getFileId();
			String PRINT_FOLDER_PATH = filePathResolver.getIntegratedImagePathForPrint(memoFileModel);
			String MEMO_FILE_PATH = filePathResolver.getUploadedFilePath(memoFileModel);
			fileServ.updateFileOne(FileDetailVO.builder().fileId(fileId).conversionStatus(ConversionStatus.NOT_STARTED).build());
			FileConvertVO convertVO = FileConvertVO.builder().fileId(fileId).conversionType(ConversionType.IMAGES_TO_PDF).sourcePath(PRINT_FOLDER_PATH).destinationPath(MEMO_FILE_PATH).build();
			driveCvtServ.insertCvtOne(convertVO);
		});
	}
}
