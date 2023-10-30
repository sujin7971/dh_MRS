package egov.framework.plms.main.bean.component.converter;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.springframework.stereotype.Component;

import egov.framework.plms.main.bean.component.common.abst.FileModelPathResolver;
import egov.framework.plms.main.bean.component.properties.FileConfigProperties;
import egov.framework.plms.main.bean.component.server.ProxyHelper;
import egov.framework.plms.main.bean.mvc.entity.file.FileConvertVO;
import egov.framework.plms.main.bean.mvc.entity.file.FileDetailVO;
import egov.framework.plms.main.bean.mvc.service.file.FileCvtService;
import egov.framework.plms.main.bean.mvc.service.file.FileInfoService;
import egov.framework.plms.main.bean.repository.DriveFileRepository;
import egov.framework.plms.main.core.model.enums.file.ConversionStatus;
import egov.framework.plms.main.core.model.enums.file.ConversionStep;
import egov.framework.plms.main.core.model.enums.file.ConversionType;
import egov.framework.plms.main.core.model.enums.file.FileCategory;
import egov.framework.plms.main.core.model.enums.file.FileStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
/**
 * 변환 대기열에 등록된 파일의 변환 처리를 {@link FileConvertProcessor}를 통해 처리하고 그 결과를 DB에 기록
 * 
 * @author mckim
 *
 */
public class DriveFileConvertManager {
	private final DriveFileRepository fileRepo;
	private final FileConvertProcessor fileConverter;
	
	private final FileInfoService fileServ;
	private final FileCvtService cvtServ;
	private final ServletContext context;
	private final FileConfigProperties fileProperties;
	private final FileModelPathResolver filePathResolver;
	
	private String REAL_PATH;//webapp경로
	private Integer CVT_MAX;//CVT_POOL의 용량
	private Integer IMAGE_DPI;
	
	private Map<Integer, FileConvertVO> CVT_POOL;//변환할 파일을 담아둘 컬렉션
	
	private final ProxyHelper proxy;
	
	@PostConstruct
	private void init() {
		CVT_MAX = fileProperties.getCvt().getPoolMax();
		IMAGE_DPI = fileProperties.getCvt().getImgDpi();
		
		CVT_POOL = new HashMap<>();
		REAL_PATH = context.getRealPath("/");
	}
	
	/**
	 * 변환 대기열에 새로운 변환 요청 추가. 대기열에 추가된 경우 <tt>true</tt> 반환.
	 * @param cvt : 대기열에 추가할 변환요청 정보
	 * @return 대기열에 추가된 경우 <tt>true</tt>, 이미 대기열에 있는 경우 <tt>false</tt>
	 */
	public boolean addToQueue(FileConvertVO cvt) {
		Integer fileId = cvt.getFileId();
		if(!fileRepo.nowQueuing(fileId) && !CVT_POOL.containsKey(fileId)) {
			Integer priority = cvt.getCvtPriority();
			fileRepo.addQueue(cvt, priority);
			cvtServ.updateCvtOne(FileConvertVO.builder().cvtId(cvt.getCvtId()).cvtStep(ConversionStep.QUEUED_OR_PROCESSING).startDateTime(LocalDateTime.now()).build());
			fileServ.updateFileOne(FileDetailVO.builder().fileId(fileId).conversionStatus(ConversionStatus.NOT_STARTED).build());
		}
		return false;
	}
	
	/**
	 * 대기열에서 대기중인 변환 요청 <tt>변환기(Converter)</tt>에 전달. 남은 <tt>변환기(Converter)</tt>가 없거나 대기열에 남은 요청이 없을 때까지 진행.
	 */
	public void passCvtToProcessor() {
		while(CVT_POOL.size() < CVT_MAX) {
			FileConvertVO cvt = fileRepo.popQueue();
			if(cvt == null) {
				break;
			}
			CVT_POOL.put(cvt.getFileId(), cvt);
			log.debug("<tt>변환기(Converter)</tt> 점유 상태 : {}/{}", CVT_POOL.size(), CVT_MAX);
			convertFile(cvt);
		}
	}
	
	/**
	 * 대기열로부터 전달받은 변환 요청 정보를 <tt>변환기(Converter)</tt> 매니저에 설정하고 파일 형태에 알맞은 <tt>변환기(Converter)</tt> 함수를 호출
	 * @param cvt : 처리할 변환 요청정보
	 */
	private void convertFile(FileConvertVO cvt) {
		FileConvertHandler handler = new FileConvertHandler(cvt);
		handler.runCvtProcessor();
	}
	
	/**
	 * <tt>변환기(Converter)</tt>를 통해 변환 요청을 처리할 매니저 클래스
	 * <br>-변환 요청 객체로부터 변환에 필요한 정보를 가져와 설정
	 * <br>-변환 요청 처리가 완료되는대로 그 결과를 DB에 기록하고 다음 변환 요청을 처리할 수 있도록 <tt>변환기(Converter)</tt>를 반납한다.
	 * @author k
	 *
	 */
	private class FileConvertHandler{
		/** 변환 처리할 파일이 속해있는 회의의 파일 저장경로 최상위 루트 */
		private String SOURCE_FOLDER;
		/** 변환 처리할 파일의 저장명 */
		private String SOURCE_NAME;
		private String DESTINATION_FOLDER;
		private String DESTINATION_NAME;
		/** 변환 처리할 파일 */
		private Integer fileId;
		private FileDetailVO srcFile;
		/** 변환 처리 정보를 담은 FileConvertVO 객체 */
		private FileConvertVO cvt;
		
		public FileConvertHandler(FileConvertVO cvt) {
			this.cvt = cvt;
			this.fileId = cvt.getFileId();
			this.srcFile = fileServ.selectFileOne(fileId).orElse(null);
		}
		
		public void runCvtProcessor() {
			ConversionType conversionType = this.cvt.getConversionType();
			switch(conversionType) {
				case DOC_TO_PDF:
					generatePdfFromDoc();
					break;
				case PDF_TO_IMAGES:
					generatePdfPageImages();
					break;
				case IMAGES_TO_PDF:
					generatePdfFromImages();
					break;
				case IMAGE_TO_WEBP:
					convertImagesToWebp();
					break;
			}
		}
		
		private void generatePdfFromDoc() {
			String SOURCE_PATH = cvt.getSourcePath();
			String DESTINATION_PATH = cvt.getDestinationPath();
			File SOURCE_FILE = new File(SOURCE_PATH);
			this.SOURCE_FOLDER = SOURCE_FILE.getParent();
			this.SOURCE_NAME = SOURCE_FILE.getName();
			File DESTINATION_FILE = new File(DESTINATION_PATH);
			this.DESTINATION_FOLDER = DESTINATION_FILE.getParent();
			this.DESTINATION_NAME = DESTINATION_FILE.getName();
			FileCategory fileCategory = this.srcFile.getFileCategory();
			fileServ.updateFileOne(FileDetailVO.builder().fileId(this.fileId).conversionStatus(ConversionStatus.DOC_TO_PDF).build());
			switch(fileCategory) {
				case HWP:
					generatePdfFromHwp();
					break;
				case WORD:
					generatePdfFromWord();
					break;
				case PPT:
					generatePdfFromPpt();
					break;
				case EXCEL:
					generatePdfFromExcel();
					break;
			}
		}
		private void generatePdfFromHwp() {
			cvtStart(ConversionStatus.DOC_TO_PDF);
			fileConverter
			.convertHwpToPdf(REAL_PATH, this.SOURCE_FOLDER, this.SOURCE_NAME, this.DESTINATION_FOLDER, this.DESTINATION_NAME)
			.thenAccept(cvtResult -> {
				if(cvtResult != null) {
					generatePdfFromDocSuccess(cvtResult);
				}else {
					cvtFail();
				}
			});
		}
		
		private void generatePdfFromWord() {
			cvtStart(ConversionStatus.DOC_TO_PDF);
			fileConverter
				.convertWordToPdf(REAL_PATH, this.SOURCE_FOLDER, this.SOURCE_NAME, this.DESTINATION_FOLDER, this.DESTINATION_NAME)
				.thenAccept(cvtResult -> {
					if(cvtResult != null) {
						generatePdfFromDocSuccess(cvtResult);
					}else {
						cvtFail();
					}
				});
		}
		
		private void generatePdfFromPpt() {
			cvtStart(ConversionStatus.DOC_TO_PDF);
			fileConverter
				.convertPptToPdf(REAL_PATH, this.SOURCE_FOLDER, this.SOURCE_NAME, this.DESTINATION_FOLDER, this.DESTINATION_NAME)
				.thenAccept(cvtResult -> {
					if(cvtResult != null) {
						generatePdfFromDocSuccess(cvtResult);
					}else {
						cvtFail();
					}
				});
		}
		
		private void generatePdfFromExcel() {
			cvtStart(ConversionStatus.DOC_TO_PDF);
			fileConverter
				.convertExcelToPdf(REAL_PATH, this.SOURCE_FOLDER, this.SOURCE_NAME, this.DESTINATION_FOLDER, this.DESTINATION_NAME)
				.thenAccept(cvtResult -> {
					if(cvtResult != null) {
						generatePdfFromDocSuccess(cvtResult);
					}else {
						cvtFail();
					}
			});
		}
		
		private void generatePdfPageImages() {
			fileServ.updateFileOne(FileDetailVO.builder().fileId(this.fileId).conversionStatus(ConversionStatus.PDF_TO_IMAGES).build());
			String SOURCE_PATH = cvt.getSourcePath();
			String DESTINATION_PATH = cvt.getDestinationPath();
			cvtStart(ConversionStatus.PDF_TO_IMAGES);
			fileConverter
			.convertPdfToImages(SOURCE_PATH, DESTINATION_PATH, IMAGE_DPI)
			.thenAccept(pageNum -> {
				if(pageNum > 0) {
					generatePdfPageImagesSuccess(pageNum);
					fileConverter
					.convertImagesToWebp(REAL_PATH, DESTINATION_PATH, DESTINATION_PATH, true);
				}else {
					cvtFail();
				}
			});
		}
		
		private void generatePdfFromImages() {
			fileServ.updateFileOne(FileDetailVO.builder().fileId(this.fileId).conversionStatus(ConversionStatus.IMAGES_TO_PDF).build());
			String SOURCE_PATH = cvt.getSourcePath();
			String DESTINATION_PATH = cvt.getDestinationPath();
			cvtStart(ConversionStatus.PDF_TO_IMAGES);
			fileConverter
			.convertImagesToPdf(SOURCE_PATH, DESTINATION_PATH)
			.thenAccept(cvtResult -> {
				if(cvtResult) {
					generatePdfFromImagesSuccess();
				}else {
					cvtFail();
				}
			});
		}
		
		private void convertImagesToWebp() {
			fileServ.updateFileOne(FileDetailVO.builder().fileId(this.fileId).conversionStatus(ConversionStatus.IMAGE_TO_WEBP).build());
			String SOURCE_PATH = cvt.getSourcePath();
			String DESTINATION_PATH = cvt.getDestinationPath();
			cvtStart(ConversionStatus.IMAGE_TO_WEBP);
			fileConverter
			.convertImagesToWebp(REAL_PATH, SOURCE_PATH, DESTINATION_PATH, false)
			.thenAccept(cvtResult -> {
				if(cvtResult) {
					cvtImageToWebpSuccess(1);
				}else {
					cvtFail();
				}
			});
		}
		
		private void cvtStart(ConversionStatus conversionStatus) {
			fileServ.updateFileOne(FileDetailVO.builder()
					.fileId(srcFile.getFileId())
					.conversionStatus(conversionStatus)
					.build());
		}
		
		private boolean generatePdfFromDocSuccess(Integer page) {
			CVT_POOL.remove(fileId);
			fileServ.updateFileOne(FileDetailVO.builder()
					.fileId(srcFile.getFileId())
					.conversionStatus(ConversionStatus.COMPLETED)
					.pdfGeneratedYN('Y')
					.pageCount(page)
					.build());
			return cvtServ.updateCvtOne(FileConvertVO.builder().cvtId(cvt.getCvtId()).cvtCount(cvt.getCvtCount()+1).cvtStep(ConversionStep.SUCCEEDED).endDateTime(LocalDateTime.now()).build());
		}
		private boolean generatePdfPageImagesSuccess(Integer page) {
			CVT_POOL.remove(fileId);
			fileServ.updateFileOne(FileDetailVO.builder()
					.fileId(srcFile.getFileId())
					.conversionStatus(ConversionStatus.COMPLETED)
					.pageImagesGeneratedYN('Y')
					.pageCount(page)
					.build());
			return cvtServ.updateCvtOne(FileConvertVO.builder().cvtId(cvt.getCvtId()).cvtCount(cvt.getCvtCount()+1).cvtStep(ConversionStep.SUCCEEDED).endDateTime(LocalDateTime.now()).build());
		}
		private boolean generatePdfFromImagesSuccess() {
			CVT_POOL.remove(fileId);
			fileServ.updateFileOne(FileDetailVO.builder()
					.fileId(srcFile.getFileId())
					.fileStatus(FileStatus.EXISTS)
					.conversionStatus(ConversionStatus.COMPLETED)
					.pdfGeneratedYN('Y')
					.build());
			return cvtServ.updateCvtOne(FileConvertVO.builder().cvtId(cvt.getCvtId()).cvtCount(cvt.getCvtCount()+1).cvtStep(ConversionStep.SUCCEEDED).endDateTime(LocalDateTime.now()).build());
		}
		private boolean cvtImageToWebpSuccess(Integer page) {
			CVT_POOL.remove(fileId);
			fileServ.updateFileOne(FileDetailVO.builder()
					.fileId(srcFile.getFileId())
					.conversionStatus(ConversionStatus.COMPLETED)
					.webpGeneratedYN('Y')
					.pageCount(page)
					.build());
			return cvtServ.updateCvtOne(FileConvertVO.builder().cvtId(cvt.getCvtId()).cvtCount(cvt.getCvtCount()+1).cvtStep(ConversionStep.SUCCEEDED).endDateTime(LocalDateTime.now()).build());
		}
		
		private boolean cvtFail() {
			CVT_POOL.remove(fileId);
			fileServ.updateFileOne(FileDetailVO.builder()
					.fileId(srcFile.getFileId())
					.conversionStatus(ConversionStatus.FAILED)
					.build());
			return cvtServ.updateCvtOne(FileConvertVO.builder().cvtId(cvt.getCvtId()).cvtCount(cvt.getCvtCount()+1).cvtStep(ConversionStep.FAILED).endDateTime(LocalDateTime.now()).build());
		}
	}
}
