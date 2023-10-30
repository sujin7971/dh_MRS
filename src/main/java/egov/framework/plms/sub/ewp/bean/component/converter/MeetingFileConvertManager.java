package egov.framework.plms.sub.ewp.bean.component.converter;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import egov.framework.plms.main.bean.component.converter.FileConvertProcessor;
import egov.framework.plms.main.bean.component.properties.FileConfigProperties;
import egov.framework.plms.main.bean.component.server.ProxyHelper;
import egov.framework.plms.main.core.model.enums.file.FileCategory;
import egov.framework.plms.main.core.util.FileNameUtil;
import egov.framework.plms.sub.ewp.bean.component.properties.EwpServerProperties;
import egov.framework.plms.sub.ewp.bean.component.repository.MeetingFileRepository;
import egov.framework.plms.sub.ewp.bean.component.server.EwpServerListener;
import egov.framework.plms.sub.ewp.bean.mvc.entity.file.MeetingFileConvertVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.file.MeetingFileInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.service.file.EwpFileDiskService;
import egov.framework.plms.sub.ewp.bean.mvc.service.file.EwpMeetingCvtService;
import egov.framework.plms.sub.ewp.bean.mvc.service.file.EwpMeetingFileInfoService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingInfoService;
import egov.framework.plms.sub.ewp.core.model.enums.FileRole;
import egov.framework.plms.sub.ewp.core.util.MeetingFilePathUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("ewp")
/**
 * 변환 대기열에 등록된 파일의 변환 처리를 {@link FileConvertProcessor}를 통해 처리하고 그 결과를 DB에 기록
 * 
 * @author mckim
 *
 */
public class MeetingFileConvertManager {
	
	private final MeetingFileRepository fileRepo;
	private final FileConvertProcessor fileConverter;
	
	private final EwpMeetingFileInfoService fileServ;
	private final EwpFileDiskService diskServ;
	private final EwpMeetingCvtService cvtServ;
	private final EwpMeetingInfoService mtServ;
	private final ServletContext context;
	private final FileConfigProperties fileProperties;
	
	private String REAL_PATH;//webapp경로
	private Integer CVT_MAX;//CVT_POOL의 용량
	private Integer IMAGE_DPI;
	
	private Map<Integer, MeetingFileConvertVO> CVT_POOL;//변환할 파일을 담아둘 컬렉션
	
	private final EwpServerProperties serverProperties;
	private final EwpServerListener listener;
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
	public boolean addToQueue(MeetingFileConvertVO cvt) {
		Integer fileKey = cvt.getFileKey();
		if(!fileRepo.nowQueuing(fileKey) && !CVT_POOL.containsKey(fileKey)) {
			Integer priority = cvt.getCvtPriority();
			fileRepo.addQueue(cvt, priority);
			cvtServ.putCvt(MeetingFileConvertVO.builder().cvtKey(cvt.getCvtKey()).cvtStep(1).startDT(LocalDateTime.now()).build());
		}
		return false;
	}
	
	/**
	 * 대기열에서 대기중인 변환 요청 <tt>변환기(Converter)</tt>에 전달. 남은 <tt>변환기(Converter)</tt>가 없거나 대기열에 남은 요청이 없을 때까지 진행.
	 */
	public void passCvtToProcessor() {
		while(CVT_POOL.size() < CVT_MAX) {
			MeetingFileConvertVO cvt = fileRepo.popQueue();
			if(cvt == null) {
				break;
			}
			CVT_POOL.put(cvt.getFileKey(), cvt);
			log.debug("<tt>변환기(Converter)</tt> 점유 상태 : {}/{}", CVT_POOL.size(), CVT_MAX);
			convertFile(cvt);
		}
	}
	
	/**
	 * 대기열로부터 전달받은 변환 요청 정보를 <tt>변환기(Converter)</tt> 매니저에 설정하고 파일 형태에 알맞은 <tt>변환기(Converter)</tt> 함수를 호출
	 * @param cvt : 처리할 변환 요청정보
	 */
	private void convertFile(MeetingFileConvertVO cvt) {
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
		private String ROOT_PATH;
		/** 변환 처리할 파일의 저장명 */
		private String FILE_NAME;
		/** 변환 처리할 파일 번호 */
		private Integer originalKey;
		/** 변환 처리할 파일 */
		private MeetingFileInfoVO srcFile;
		/** 변환 처리 정보를 담은 FileConvertVO 객체 */
		private MeetingFileConvertVO cvt;
		
		public FileConvertHandler(MeetingFileConvertVO cvt) {
			this.cvt = cvt;
			this.originalKey = cvt.getFileKey();
			this.srcFile = fileServ.getFileOne(originalKey);
			this.ROOT_PATH = MeetingFilePathUtil.getRootPath(mtServ.getMeetingInfoOne(srcFile.getMeetingKey()).get());
			this.FILE_NAME = FileNameUtil.getFileNameFormat(srcFile);
		}
		
		public void runCvtProcessor() {
			FileRole srcRole = srcFile.getRoleType();
			if(srcRole == FileRole.MATERIAL) {
				FileCategory mimeType = srcFile.getMimeType();
				String VIEW_PATH = MeetingFilePathUtil.getViewPath(ROOT_PATH);
				File view = new File(VIEW_PATH);
				if(!view.exists()) {
					view.mkdirs();
				}
				if(mimeType == FileCategory.HWP) {
					convertHWP();
				}else if(mimeType == FileCategory.PDF){
					convertPDF();
				}else if(mimeType == FileCategory.IMG){
					convertIMG();
				}else if(mimeType == FileCategory.WORD){
					convertWORD();
				}else if(mimeType == FileCategory.PPT){
					convertPPT();
				}else if(mimeType == FileCategory.EXCEL){
					convertEXCEL();
				}
			}else if(srcRole == FileRole.COPY) {
				convertBOOK();
			}else if(srcRole == FileRole.MEMO) {
				convertMEMO();
			}
		}
		private void convertHWP() {
			log.info("convertHWP[key : {}][name : {}][type : {}] - Start convertHWP", originalKey, srcFile.getOriginalName(), srcFile.getRoleType());
			cvtStart();
			String SOURCE_PATH = MeetingFilePathUtil.getMaterialFilePath(ROOT_PATH, FILE_NAME);
			String PDF_PATH = MeetingFilePathUtil.getViewFilePath(ROOT_PATH, FilenameUtils.getBaseName(FILE_NAME)+".pdf");
			String NOTE_PATH = MeetingFilePathUtil.getBookletSourceFolderPath(ROOT_PATH, FilenameUtils.getBaseName(FILE_NAME));
			fileConverter
			.convertHwpToPdf(REAL_PATH, SOURCE_PATH, PDF_PATH)
			.thenAccept(cvtResult -> {
				if(cvtResult != null) {
					log.info("convertHWP[key : {}][name : {}][type : {}] - cvtHwp2Img result : success", originalKey, srcFile.getOriginalName(), srcFile.getRoleType());
					fileConverter
					.convertPdfToImages(PDF_PATH, NOTE_PATH, IMAGE_DPI)
					.thenAccept(pageNum -> {
						if(pageNum > 0) {
							log.info("convertHWP[key : {}][name : {}][type : {}] - convertImagesToPdf result : success", originalKey, srcFile.getOriginalName(), srcFile.getRoleType());
							cvtSuccess(pageNum);
						}else {
							log.info("convertHWP[key : {}][name : {}][type : {}] - convertImagesToPdf fail : success", originalKey, srcFile.getOriginalName(), srcFile.getRoleType());
							cvtFail();
						}
						String IMAGE_COLLECTION_PATH = MeetingFilePathUtil.getBookletSourceFolderPath(ROOT_PATH, FilenameUtils.getBaseName(FILE_NAME));
						fileConverter
						.convertImagesToWebp(REAL_PATH, IMAGE_COLLECTION_PATH, IMAGE_COLLECTION_PATH, true);
					});
					
				}else {
					log.info("convertHWP[key : {}][name : {}][type : {}] - cvtHwp2Img result : fail", originalKey, srcFile.getOriginalName(), srcFile.getRoleType());
					cvtFail();
				}
			});
		}
		
		private void convertWORD() {
			log.info("convertWORD[key : {}][name : {}][type : {}] - Start convertWORD", originalKey, srcFile.getOriginalName(), srcFile.getRoleType());
			cvtStart();
			String SOURCE_PATH = MeetingFilePathUtil.getMaterialFilePath(ROOT_PATH, FILE_NAME);
			String PDF_PATH = MeetingFilePathUtil.getViewFilePath(ROOT_PATH, FilenameUtils.getBaseName(FILE_NAME)+".pdf");
			String NOTE_PATH = MeetingFilePathUtil.getBookletSourceFolderPath(ROOT_PATH, FilenameUtils.getBaseName(FILE_NAME));
			fileConverter
				.convertWordToPdf(REAL_PATH, SOURCE_PATH, PDF_PATH)
				.thenAccept(cvtResult -> {
					if(cvtResult != null) {
						log.info("convertWORD[key : {}][name : {}][type : {}] - convertWordToPdf result : success", originalKey, srcFile.getOriginalName(), srcFile.getRoleType());
						fileConverter
						.convertPdfToImages(PDF_PATH, NOTE_PATH, IMAGE_DPI)
						.thenAccept(pageNum -> {
							if(pageNum > 0) {
								log.info("convertWORD[key : {}][name : {}][type : {}] - convertPdfToImages result : success", originalKey, srcFile.getOriginalName(), srcFile.getRoleType());
								cvtSuccess(pageNum);
								String IMAGE_COLLECTION_PATH = MeetingFilePathUtil.getBookletSourceFolderPath(ROOT_PATH, FilenameUtils.getBaseName(FILE_NAME));
								fileConverter
								.convertImagesToWebp(REAL_PATH, IMAGE_COLLECTION_PATH, IMAGE_COLLECTION_PATH, true);
							}else {
								log.info("convertWORD[key : {}][name : {}][type : {}] - convertPdfToImages result : fail", originalKey, srcFile.getOriginalName(), srcFile.getRoleType());
								cvtFail();
							}
						});
					}else {
						log.info("convertWORD[key : {}][name : {}][type : {}] - convertWordToPdf result : fail", originalKey, srcFile.getOriginalName(), srcFile.getRoleType());
						cvtFail();
					}
				});
		}
		
		private void convertPPT() {
			log.info("convertPPT[key : {}][name : {}][type : {}] - Start convertPPT", originalKey, srcFile.getOriginalName(), srcFile.getRoleType());
			cvtStart();
			String SOURCE_PATH = MeetingFilePathUtil.getMaterialFilePath(ROOT_PATH, FILE_NAME);
			String PDF_PATH = MeetingFilePathUtil.getViewFilePath(ROOT_PATH, FilenameUtils.getBaseName(FILE_NAME)+".pdf");
			String NOTE_PATH = MeetingFilePathUtil.getBookletSourceFolderPath(ROOT_PATH, FilenameUtils.getBaseName(FILE_NAME));
			fileConverter
				.convertPptToPdf(REAL_PATH, SOURCE_PATH, PDF_PATH)
				.thenAccept(cvtResult -> {
					if(cvtResult != null) {
						log.info("convertPPT[key : {}][name : {}][type : {}] - convertPptToPdf result : success", originalKey, srcFile.getOriginalName(), srcFile.getRoleType());
						fileConverter
						.convertPdfToImages(PDF_PATH, NOTE_PATH, IMAGE_DPI)
						.thenAccept(pageNum -> {
							if(pageNum > 0) {
								log.info("convertPPT[key : {}][name : {}][type : {}] - convertPdfToImages result : success", originalKey, srcFile.getOriginalName(), srcFile.getRoleType());
								cvtSuccess(pageNum);
								String IMAGE_COLLECTION_PATH = MeetingFilePathUtil.getBookletSourceFolderPath(ROOT_PATH, FilenameUtils.getBaseName(FILE_NAME));
								fileConverter
								.convertImagesToWebp(REAL_PATH, IMAGE_COLLECTION_PATH, IMAGE_COLLECTION_PATH, true);
							}else {
								log.info("convertPPT[key : {}][name : {}][type : {}] - convertPdfToImages result : fail", originalKey, srcFile.getOriginalName(), srcFile.getRoleType());
								cvtFail();
							}
						});
					}else {
						log.info("convertPPT[key : {}][name : {}][type : {}] - convertPptToPdf result : success", originalKey, srcFile.getOriginalName(), srcFile.getRoleType());
						cvtFail();
					}
				});
		}
		
		private void convertEXCEL() {
			log.info("convertEXCEL[key : {}][name : {}][type : {}] - Start convertExcel", originalKey, srcFile.getOriginalName(), srcFile.getRoleType());
			cvtStart();
			String SOURCE_PATH = MeetingFilePathUtil.getMaterialFilePath(ROOT_PATH, FILE_NAME);
			String PDF_PATH = MeetingFilePathUtil.getViewFilePath(ROOT_PATH, FilenameUtils.getBaseName(FILE_NAME)+".pdf");
			String NOTE_PATH = MeetingFilePathUtil.getBookletSourceFolderPath(ROOT_PATH, FilenameUtils.getBaseName(FILE_NAME));
			fileConverter
				.convertExcelToPdf(REAL_PATH, SOURCE_PATH, PDF_PATH)
				.thenAccept(cvtResult -> {
					if(cvtResult != null) {
						log.info("convertEXCEL[key : {}][name : {}][type : {}] - convertExcelToPdf result : success", originalKey, srcFile.getOriginalName(), srcFile.getRoleType());
						fileConverter
						.convertPdfToImages(PDF_PATH, NOTE_PATH, IMAGE_DPI)
						.thenAccept(pageNum -> {
							if(pageNum > 0) {
								log.info("convertEXCEL[key : {}][name : {}][type : {}] - convertPdfToImages result : success", originalKey, srcFile.getOriginalName(), srcFile.getRoleType());
								cvtSuccess(pageNum);
								String IMAGE_COLLECTION_PATH = MeetingFilePathUtil.getBookletSourceFolderPath(ROOT_PATH, FilenameUtils.getBaseName(FILE_NAME));
								fileConverter
								.convertImagesToWebp(REAL_PATH, IMAGE_COLLECTION_PATH, IMAGE_COLLECTION_PATH, true);
							}else {
								log.info("convertEXCEL[key : {}][name : {}][type : {}] - convertPdfToImages result : fail", originalKey, srcFile.getOriginalName(), srcFile.getRoleType());
								cvtFail();
							}
						});
					}else {
						log.info("convertEXCEL[key : {}][name : {}][type : {}] - convertExcelToPdf result : fail", originalKey, srcFile.getOriginalName(), srcFile.getRoleType());
						cvtFail();
					}
			});
		}
		
		private void convertPDF() {
			log.info("convertPDF[key : {}][name : {}][type : {}] - Start convertPDF", originalKey, srcFile.getOriginalName(), srcFile.getRoleType());
			cvtStart();
			
			try {
				String SOURCE_PATH = MeetingFilePathUtil.getMaterialFilePath(ROOT_PATH, FILE_NAME);
				String VIEW_PATH = MeetingFilePathUtil.getViewFilePath(ROOT_PATH, FILE_NAME);
				String NOTE_PATH = MeetingFilePathUtil.getBookletSourceFolderPath(ROOT_PATH, FilenameUtils.getBaseName(FILE_NAME));
				FileUtils.copyFile(new File(SOURCE_PATH), new File(VIEW_PATH));
				fileConverter
				.convertPdfToImages(VIEW_PATH, NOTE_PATH, IMAGE_DPI)
				.thenAccept(pageNum -> {
					if(pageNum > 0) {
						log.info("convertPDF[key : {}][name : {}][type : {}] - convertPdfToImages result : success", originalKey, srcFile.getOriginalName(), srcFile.getRoleType());
						cvtSuccess(pageNum);
						String IMAGE_COLLECTION_PATH = MeetingFilePathUtil.getBookletSourceFolderPath(ROOT_PATH, FilenameUtils.getBaseName(FILE_NAME));
						fileConverter
						.convertImagesToWebp(REAL_PATH, IMAGE_COLLECTION_PATH, IMAGE_COLLECTION_PATH, true);
					}else {
						log.info("convertPDF[key : {}][name : {}][type : {}] - convertPdfToImages result : fail", originalKey, srcFile.getOriginalName(), srcFile.getRoleType());
						cvtFail();
					}
				});
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				cvtFail();
			}
		}
		
		private void convertIMG() {
			log.info("convertIMG[key : {}][name : {}][type : {}] - Start convertIMG", originalKey, srcFile.getOriginalName(), srcFile.getRoleType());
			cvtStart();
			try {
				String SOURCE_PATH = MeetingFilePathUtil.getMaterialFilePath(ROOT_PATH, FILE_NAME);
				String VIEW_PATH = MeetingFilePathUtil.getViewFilePath(ROOT_PATH, FILE_NAME);
				FileUtils.copyFile(new File(SOURCE_PATH), new File(VIEW_PATH));
				String NOTE_PATH = MeetingFilePathUtil.getBookletSourceFolderPath(ROOT_PATH, FilenameUtils.getBaseName(FILE_NAME));
				
				fileConverter
				.convertImagesToWebp(REAL_PATH, VIEW_PATH, VIEW_PATH, false)
				.thenAccept(cvtResult -> {
					if(cvtResult) {
						log.info("convertIMG[key : {}][name : {}][type : {}] - convertImagesToWebp result : success", originalKey, srcFile.getOriginalName(), srcFile.getRoleType());
						File bookletSourceFolder = new File(NOTE_PATH);
						if (!bookletSourceFolder.exists()) {
							bookletSourceFolder.mkdirs();
						}
						File resizeImage = new File(VIEW_PATH);
						File bookletImage = new File(NOTE_PATH + File.separator + "0001.webp");
						log.info("convertIMG[key : {}][name : {}][type : {}] - convertImagesToWebp resizeImage : {}, bookletImage: {}", originalKey, srcFile.getOriginalName(), srcFile.getRoleType(), resizeImage.getAbsolutePath(), bookletImage.getAbsolutePath());
						try {
							FileUtils.copyFile(resizeImage, bookletImage);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						cvtSuccess(1);
					}else {
						log.info("convertIMG[key : {}][name : {}][type : {}] - convertImagesToWebp result : fail", originalKey, srcFile.getOriginalName(), srcFile.getRoleType());
						cvtFail();
					}
				});
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				cvtFail();
			}
		}
		
		/**
		 * 개인이 판서한 판서본 PDF 파일 생성. 
		 * 다른 함수와 달리 srcFile은 생성할 PDF파일 정보를 담고 있을 뿐이고 파일 생성 자체는 원본 파일을 통해 얻은 책자 경로에 있는 이미지 파일을 FileConverter에 넘겨 PDF 파일을 생성.
		 */
		private void convertBOOK() {
			log.info("convertBOOK[key : {}][name : {}][type : {}] - Start convertBOOK", originalKey, srcFile.getOriginalName(), srcFile.getRoleType());
			// 생성할 srcFile의 회의 자료. 회의 자료를 통해 책자 경로를 얻는다.
			MeetingFileInfoVO orgFile = fileServ.getFileOne(srcFile.getOriginalKey());
			// 회의 자료 개인이 판서한 책자 경로
			String PRINTER_FOLDER_PATH = MeetingFilePathUtil.getPrinterPath(ROOT_PATH, srcFile.getUuid());
			
			// 개인 책자 폴더 및 이미지 목록
			File printerFolder = new File(PRINTER_FOLDER_PATH);
			
			
			Integer result = fileServ.putMeetingFile(MeetingFileInfoVO.builder()
					.fileKey(srcFile.getFileKey())
					.state(1)
					.build());
			if(result == 0) {
				try {
					FileUtils.deleteDirectory(printerFolder);
					log.info("convertBOOK[key : {}][name : {}][type : {}] - Delete PrintFolder : [{}] - DB삭제[변환 취소]", originalKey, srcFile.getOriginalName(), srcFile.getRoleType(), printerFolder.getPath());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					log.error(e.getMessage());
				}
				CVT_POOL.remove(originalKey);
			}
			
			fileConverter
			.convertImagesToWebp(REAL_PATH, PRINTER_FOLDER_PATH, PRINTER_FOLDER_PATH, true)
			.thenAccept(webpCvtRes -> {
				String COPY_PATH = MeetingFilePathUtil.getCopyRootPath(ROOT_PATH);
				String FILE_NAME = FileNameUtil.getCopyNameFormat(srcFile, srcFile.getEmpKey());
				File[] PRINT_FILE_LIST = printerFolder.listFiles();
				fileConverter
				.convertImagesToPdf(PRINT_FILE_LIST, COPY_PATH, FILE_NAME)
				.thenAccept(cvtResult -> {
					if(cvtResult) {
						log.info("convertBOOK[key : {}][name : {}][type : {}] - convertImagesToPdf result : success", originalKey, srcFile.getOriginalName(), srcFile.getRoleType());
						if(cvtSuccess(PRINT_FILE_LIST.length) == 0) {
							File copy = new File(MeetingFilePathUtil.getCopyFilePath(COPY_PATH, FILE_NAME));
							copy.delete();
						}
						try {
							FileUtils.deleteDirectory(printerFolder);
							log.info("convertBOOK[key : {}][name : {}][type : {}] - Delete PrintFolder : [{}] - 변환 완료", originalKey, srcFile.getOriginalName(), srcFile.getRoleType(), printerFolder.getPath());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							log.error(e.getMessage());
						}
					}else {
						log.info("convertBOOK[key : {}][name : {}][type : {}] - convertImagesToPdf result : fail", originalKey, srcFile.getOriginalName(), srcFile.getRoleType());
						if(cvtFail() == 0) {
							File copy = new File(MeetingFilePathUtil.getCopyFilePath(COPY_PATH, FILE_NAME));
							copy.delete();
							try {
								FileUtils.deleteDirectory(printerFolder);
								log.info("convertBOOK[key : {}][name : {}][type : {}] - Delete PrintFolder : [{}] - 변환 실패", originalKey, srcFile.getOriginalName(), srcFile.getRoleType(), printerFolder.getPath());
							} catch (IOException e) {
								// TODO Auto-generated catch block
								log.error(e.getMessage());
							}
						}
					}
				});
			});
			
		}
		
		private void convertMEMO() {
			log.info("convertBOOK[key : {}][name : {}][type : {}] - Start convertBOOK", originalKey, srcFile.getOriginalName(), srcFile.getRoleType());
			// 회의 자료 개인이 판서한 책자 경로
			String MEMO_FOLDER_PATH = MeetingFilePathUtil.getPrinterPath(ROOT_PATH, srcFile.getUuid());
			// 개인 책자 폴더 및 이미지 목록
			File printerFolder = new File(MEMO_FOLDER_PATH);
			File[] PRINT_FILE_LIST = printerFolder.listFiles();
			
			Integer result = fileServ.putMeetingFile(MeetingFileInfoVO.builder()
					.fileKey(srcFile.getFileKey())
					.state(1)
					.build());
			if(result == 0) {
				try {
					FileUtils.deleteDirectory(printerFolder);
					log.info("convertBOOK[key : {}][name : {}][type : {}] - Delete PrintFolder : [{}] - DB삭제[변환 취소]", originalKey, srcFile.getOriginalName(), srcFile.getRoleType(), printerFolder.getPath());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					log.error(e.getMessage());
				}
				CVT_POOL.remove(originalKey);
			}
			
			if(PRINT_FILE_LIST == null) {
				cvtFail();
				return;
			}
			
			String MEMO_ROOT_PATH = MeetingFilePathUtil.getMemoRootPath(ROOT_PATH);
			String MEMO_FILE_NAME = FileNameUtil.getCopyNameFormat(srcFile, srcFile.getEmpKey());
			
			fileConverter
			.convertImagesToPdf(PRINT_FILE_LIST, MEMO_ROOT_PATH, MEMO_FILE_NAME)
			.thenAccept(cvtResult -> {
				if(cvtResult) {
					log.info("convertBOOK[key : {}][name : {}][type : {}] - convertImagesToPdf result : success", originalKey, srcFile.getOriginalName(), srcFile.getRoleType());
					if(cvtSuccess(PRINT_FILE_LIST.length) == 0) {
						File copy = new File(MeetingFilePathUtil.getMemoFilePath(MEMO_ROOT_PATH, MEMO_FILE_NAME));
						copy.delete();
					}
					try {
						FileUtils.deleteDirectory(printerFolder);
						log.info("convertBOOK[key : {}][name : {}][type : {}] - Delete PrintFolder : [{}] - 변환 완료", originalKey, srcFile.getOriginalName(), srcFile.getRoleType(), printerFolder.getPath());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						log.error(e.getMessage());
					}
				}else {
					log.info("convertBOOK[key : {}][name : {}][type : {}] - convertImagesToPdf result : fail", originalKey, srcFile.getOriginalName(), srcFile.getRoleType());
					if(cvtFail() == 0) {
						File copy = new File(MeetingFilePathUtil.getMemoFilePath(MEMO_ROOT_PATH, MEMO_FILE_NAME));
						copy.delete();
						try {
							FileUtils.deleteDirectory(printerFolder);
							log.info("convertBOOK[key : {}][name : {}][type : {}] - Delete PrintFolder : [{}] - 변환 실패", originalKey, srcFile.getOriginalName(), srcFile.getRoleType(), printerFolder.getPath());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							log.error(e.getMessage());
						}
					}
				}
				
			});
		}
		
		private void cvtStart() {
			fileServ.putMeetingFile(MeetingFileInfoVO.builder()
					.fileKey(srcFile.getFileKey())
					.state(1)
					.build());
			if(srcFile.getRoleType() == FileRole.MATERIAL) {
				//msgBroker.broadCastMsg(srcFile.getMeetingKey(), SocketMessage.builder(MessageType.UPDATE).resourceType(ResourceType.FILE).data(srcFile.getFileKey()).build());
				noticeFileStateChanged(srcFile.getFileKey());
			}
		}
		
		private Integer cvtSuccess(Integer page) {
			CVT_POOL.remove(originalKey);
			MeetingFileInfoVO fileVO = fileServ.getFileOne(srcFile.getFileKey());
			if(fileVO.getDelYN() == 'Y') {
				diskServ.removeBookletFolder(fileVO);
				return 0;
			}
			fileServ.putMeetingFile(MeetingFileInfoVO.builder()
					.fileKey(srcFile.getFileKey())
					.page(page)
					.state(2)
					.build());
			if(srcFile.getRoleType() == FileRole.MATERIAL) {
				//msgBroker.broadCastMsg(srcFile.getMeetingKey(), SocketMessage.builder(MessageType.UPDATE).resourceType(ResourceType.FILE).data(srcFile.getFileKey()).build());
				noticeFileStateChanged(srcFile.getFileKey());
			}
			return cvtServ.putCvt(MeetingFileConvertVO.builder().cvtKey(cvt.getCvtKey()).cvtCount(cvt.getCvtCount()+1).cvtStep(2).endDT(LocalDateTime.now()).build());
		}
		
		private Integer cvtFail() {
			CVT_POOL.remove(originalKey);
			MeetingFileInfoVO fileVO = fileServ.getFileOne(srcFile.getFileKey());
			if(fileVO.getDelYN() == 'Y') {
				diskServ.removeBookletFolder(fileVO);
				return 0;
			}
			fileServ.putMeetingFile(MeetingFileInfoVO.builder()
					.fileKey(srcFile.getFileKey())
					.state(-1)
					.build());
			if(srcFile.getRoleType() == FileRole.MATERIAL) {
				//msgBroker.broadCastMsg(srcFile.getMeetingKey(), SocketMessage.builder(MessageType.UPDATE).resourceType(ResourceType.FILE).data(srcFile.getFileKey()).build());
				noticeFileStateChanged(srcFile.getFileKey());
			}
			return cvtServ.putCvt(MeetingFileConvertVO.builder().cvtKey(cvt.getCvtKey()).cvtCount(cvt.getCvtCount()+1).cvtStep(-1).endDT(LocalDateTime.now()).build());
		}
	}
	
	private void noticeFileStateChanged(Integer fileKey) {
		String HOST_IP = String.format("https://%s:%d", listener.getHost(), serverProperties.getPortTable().getServiceServer());
		String API_URI = String.format("/api/ewp/proxy/file/%d/update", fileKey);
		proxy.call(HOST_IP, API_URI, HttpMethod.GET, null);
	}
}
