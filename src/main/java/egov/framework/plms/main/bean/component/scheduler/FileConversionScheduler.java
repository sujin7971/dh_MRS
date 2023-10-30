package egov.framework.plms.main.bean.component.scheduler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import egov.framework.plms.main.bean.component.common.abst.FileModelPathResolver;
import egov.framework.plms.main.bean.component.converter.DriveFileConvertManager;
import egov.framework.plms.main.bean.component.properties.FileConfigProperties;
import egov.framework.plms.main.bean.component.server.ProxyHelper;
import egov.framework.plms.main.bean.mvc.entity.file.FileConvertVO;
import egov.framework.plms.main.bean.mvc.entity.file.FileDetailVO;
import egov.framework.plms.main.bean.mvc.entity.file.FileInfoVO;
import egov.framework.plms.main.bean.mvc.entity.file.abst.FileDetailModelVO;
import egov.framework.plms.main.bean.mvc.service.file.FileCvtService;
import egov.framework.plms.main.bean.mvc.service.file.abst.FileInfoAbstractService;
import egov.framework.plms.main.core.model.enums.file.ConversionStatus;
import egov.framework.plms.main.core.model.enums.file.ConversionStep;
import egov.framework.plms.main.core.model.enums.file.ConversionType;
import egov.framework.plms.main.core.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "config.scheduledTasks.fileConversionScheduler", name="enabled", havingValue = "true", matchIfMissing = false)
public class FileConversionScheduler {
	private static final String PROCESS_SEARCH_CMD_STR = "wmic process get processid, caption, creationdate";
	private static final String PROCESS_KILL_CMD_STR = "taskkill /pid ";
	private static final String PROCESS_INFO_DIV_REGEXR_STR = "([A-Za-z.EXE.exe]+)[ ]+([0-9.+]+)[ ]+([0-9+]+)";
	private static final String PROCESS_SEARCH_NAME_WINWORD = "WINWORD.EXE";
	private static final String PROCESS_SEARCH_NAME_POWERPNT  = "POWERPNT.EXE";
	private static final String PROCESS_SEARCH_NAME_EXCEL = "EXCEL.EXE";
	private static final String PROCESS_SEARCH_NAME_HWP = "Hwp.exe";
	
	private final FileInfoAbstractService fileServ;
	private final FileModelPathResolver filePathResolver;
	private final DriveFileConvertManager driveCvtManager;
	private final FileCvtService driveCvtServ;
	
	private final FileConfigProperties fileProperties;
	private final ProxyHelper proxyHelper;
	@PostConstruct
	private void init() {
		driveCvtServ.updateUnfinishedFileConversionsToInit();
	}
	
	@Scheduled(cron="${config.scheduledTasks.fileConversionScheduler.fileConversionCron}")	
	private void fileConversionScheduler() {
		requestFilesGeneratePdf();
		requestFilesGenerateWebp();
		processFilesConvertRequest();
	}
	
	private void requestFilesGeneratePdf() {
		List<FileDetailModelVO> files = fileServ.selectFilesToGeneratePdf();
		files.forEach(fileModel -> {
			Integer fileId = fileModel.getFileId();
			Integer meetingId = fileModel.getRelatedEntityId();
			String sourcePath = filePathResolver.getUploadedFilePath(fileModel);
			String destinationPath = filePathResolver.getConvertedFilePathForPreview(fileModel);
			FileConvertVO convertVO = FileConvertVO.builder().fileId(fileId).conversionType(ConversionType.DOC_TO_PDF).sourcePath(sourcePath).destinationPath(destinationPath).build();
			driveCvtServ.insertCvtOne(convertVO);
			fileServ.updateFileOne(FileDetailVO.builder().fileId(fileId).conversionStatus(ConversionStatus.NOT_STARTED).build());
		});
	}
	
	private void requestFilesGenerateWebp() {
		List<FileDetailModelVO> files = fileServ.selectFilesToGenerateWebp();
		files.forEach(fileModel -> {
			Integer fileId = fileModel.getFileId();
			Integer meetingId = fileModel.getRelatedEntityId();
			String sourcePath = filePathResolver.getUploadedFilePath(fileModel);
			String destinationPath = filePathResolver.getConvertedFilePathForPreview(fileModel);
			FileConvertVO convertVO = FileConvertVO.builder().fileId(fileId).conversionType(ConversionType.IMAGE_TO_WEBP).sourcePath(sourcePath).destinationPath(destinationPath).build();
			driveCvtServ.insertCvtOne(convertVO);
			fileServ.updateFileOne(FileDetailVO.builder().fileId(fileId).conversionStatus(ConversionStatus.NOT_STARTED).build());
		});
	}
	
	private void processFilesConvertRequest() {
		List<FileConvertVO> driveCvtList = driveCvtServ.selectFilesToConvert();
		for(FileConvertVO cvt : driveCvtList) {
			ConversionType conversionType = cvt.getConversionType();
			switch(conversionType) {
			case DOC_TO_PDF:
				if(fileProperties.getConversionServer().isEnabled()) {
					sendFilesConvertRequest(cvt);
				}else {
					startFilesConvertRequest(cvt);
				}
				break;
			case PDF_TO_IMAGES:
			case IMAGE_TO_WEBP:
			case IMAGES_TO_PDF:
				startFilesConvertRequest(cvt);
				break;
			}
		}
		driveCvtManager.passCvtToProcessor();
	}
	
	private void sendFilesConvertRequest(FileConvertVO cvt) {
		// TODO Auto-generated method stub
		String baseUrl = fileProperties.getConversionServer().getBaseUrl();
		Map<String, String> pathVariables = new HashMap<>();
		pathVariables.put("trId", cvt.getCvtId().toString());
		Map converstStatus = proxyHelper.call(baseUrl, "/file-conversion/{trId}", HttpMethod.GET, pathVariables, Map.class);
		String resultCode = (String) converstStatus.get("resultCode");
		if(resultCode.equals(ConversionStep.NOT_QUEUED.getCode().toString())) {
			Map<String, Object> request = new HashMap<>();
			FileDetailModelVO fileVO = (FileDetailModelVO) fileServ.selectFileOne(cvt.getFileId()).get();
			request.put("trid", cvt.getCvtId());
			request.put("filecategory", fileVO.getFileCategory().getCode());
			request.put("conversiontype", cvt.getConversionType().name());
			request.put("sourcepath", cvt.getSourcePath());
			request.put("destinationpath", cvt.getDestinationPath());
			Map requestResult = proxyHelper.call(baseUrl, "/file-conversion", HttpMethod.POST, request, Map.class);
			log.info("cvt request result: {}", requestResult);
			resultCode = (String) converstStatus.get("resultCode");
			if(resultCode.equals("1")) {
				driveCvtServ.updateCvtOne(cvt.toBuilder().cvtStep(ConversionStep.QUEUED_OR_PROCESSING).build());
			}
		}else if(resultCode.equals(ConversionStep.SUCCEEDED.getCode().toString())) {
			driveCvtServ.updateCvtOne(cvt.toBuilder().cvtStep(ConversionStep.codeOf(Integer.parseInt(resultCode))).build());
			fileServ.updateFileOne(FileDetailVO.builder()
					.fileId(cvt.getFileId())
					.conversionStatus(ConversionStatus.COMPLETED)
					.pdfGeneratedYN('Y')
					.build());
		}else {
			driveCvtServ.updateCvtOne(cvt.toBuilder().cvtStep(ConversionStep.codeOf(Integer.parseInt(resultCode))).build());
		}
	}
	
	private void startFilesConvertRequest(FileConvertVO cvt) {
		try {
			driveCvtManager.addToQueue(cvt);
		}catch (Exception e) {
			log.error(e.toString(), e);
		}
	}
	
	@Scheduled(cron="${config.scheduledTasks.fileConversionScheduler.officeProcessCleanupCron}")
	private void officeProcessCleanupScheduler() {
		try {
			//실행 후 1시간 경과 프로세스 강제 제거
			processIdKill(PROCESS_SEARCH_NAME_WINWORD, 10); 
			processIdKill(PROCESS_SEARCH_NAME_POWERPNT, 10);
			processIdKill(PROCESS_SEARCH_NAME_EXCEL, 10);
			processIdKill(PROCESS_SEARCH_NAME_HWP, 10);

		} catch (ParseException e) {
			log.error(e.toString(), e);
		}
	}
	
	private void processIdKill(String killProcessName, int checkOverTime) throws ParseException {
		List<HashMap> resList = processSearch(killProcessName);
		for (HashMap processHashMap : resList) {
			log.info("processName: " + processHashMap.get("processName"));
			log.info("creationdate: " + processHashMap.get("creationdate").toString().trim());
			log.info("processid: " + processHashMap.get("processid"));
			LocalDateTime nowDT = LocalDateTime.now();
			String creationdateStr = processHashMap.get("creationdate").toString();
			LocalDateTime processDT = LocalDateTime.parse(creationdateStr.subSequence(0, creationdateStr.length() - 1), DateTimeUtil.format("yyyyMMddHHmmss"));
			LocalDateTime timeoutDT = processDT.plusMinutes(checkOverTime);
			if(nowDT.isAfter(timeoutDT)) {
				log.info(killProcessName + ", kill processid: " + processHashMap.get("processid").toString());
				processKill(processHashMap.get("processid").toString());
			}
		}
	}

	/**
	 * 해당 프로세스명의 process ID조회
	 * @param processName
	 * @return
	 */
	private List<HashMap> processSearch(String processName) {
		List<HashMap> resList = null;
		BufferedReader br = null;
		Scanner scanner = null;
		try {
			resList = new ArrayList<HashMap>();

			Process process = Runtime.getRuntime().exec(PROCESS_SEARCH_CMD_STR);
			br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			scanner = new Scanner(br);
			scanner.useDelimiter(System.getProperty("line.separator"));
			while (scanner.hasNext()) {
				String resLine = scanner.next();
				if (resLine.contains(processName)) {
					log.info(processName + ",  resLine: " + resLine);

					Pattern processPattern = Pattern.compile(PROCESS_INFO_DIV_REGEXR_STR);
					Matcher processeMatcher = processPattern.matcher(resLine);

					if (processeMatcher.find()) {
						log.info(processeMatcher.group(1) + ", " + processeMatcher.group(2) + ", " + processeMatcher.group(3));
						HashMap hashMap = new HashMap();
						hashMap.put("processName", processeMatcher.group(1));
						hashMap.put("creationdate", processeMatcher.group(2).substring(0, 15));
						hashMap.put("processid", processeMatcher.group(3));

						resList.add(hashMap);
					}
				}
			}
			scanner.close();
			br.close();
		} catch (Exception e) {
			log.error(e.toString(), e);
			scanner.close();
			try {
				br.close();
			} catch (IOException e1) {
				log.error(e.toString(), e);
			}
		}

		return resList;
	}

	/**
	 * 조회된 process kill 명령어 실행
	 * @param processId
	 */
	private void processKill(String processId) {
		try {
			String cmd = PROCESS_KILL_CMD_STR + processId + " /f";
			Runtime.getRuntime().exec(cmd);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
	}
}
