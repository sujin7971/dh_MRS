package egov.framework.plms.sub.ewp.core.util;

import java.io.File;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingInfoModelVO;
import egov.framework.plms.sub.ewp.core.model.enums.FileRole;
import lombok.extern.slf4j.Slf4j;


/**
 * 프로그램에서 사용할 파일 유형별 경로를 제공할 로직을 모아둔 클래스
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 */
@Component
@Slf4j
public class MeetingFilePathUtil {
	@Value("${config.file.upload-path}")
	private String uploadPath;
	
	private static String UPLOAD_PATH;
	
	@Value("${config.file.upload-path}")
	public void setUploadPath(String uploadPath) {
		MeetingFilePathUtil.UPLOAD_PATH = uploadPath;
	}
	
	/**
	 * 회의의 모든 파일을 저장할 최상위 경로값(C:/B-PLMS/uploadFile/년/월/회의번호)
	 * @param meeting
	 * @return
	 */
	public static String getRootPath(MeetingInfoModelVO meeting) {
		LocalDateTime regDateTime = meeting.getRegDateTime();
		
		Integer year = regDateTime.getYear();
		Integer month = regDateTime.getMonth().getValue();
		
		String ROOT_PATH = UPLOAD_PATH;
		if (!ROOT_PATH.endsWith(File.separator)) {
			ROOT_PATH += File.separator;
		}
		
		String SAVE_PATH = UPLOAD_PATH + "meeting" + File.separator + year + File.separator +month + File.separator + meeting.getMeetingId();
		return SAVE_PATH;
	}
	
	/**
	 * 원본 폴더 경로(C:/B-PLMS/uploadFile/년/월/회의번호/MATERIAL)
	 * @param ROOT_PATH : 회의의 파일을 저장할 최상위 폴더 경로
	 * @return
	 */
	public static String getMaterialRootPath(String ROOT_PATH) {
		String SAVE_PATH = ROOT_PATH + File.separator + FileRole.MATERIAL.getCode();
		return SAVE_PATH;
	}
	
	/**
	 * 원본 파일 경로(C:/B-PLMS/uploadFile/년/월/회의번호/MATERIAL/파일명)
	 * @param ROOT_PATH : 회의의 파일을 저장할 최상위 폴더 경로
	 * @param FILE_NAME : 경로를 가져올 파일명
	 * @return
	 */
	public static String getMaterialFilePath(String ROOT_PATH, String FILE_NAME) {
		String SAVE_PATH = ROOT_PATH + File.separator + FileRole.MATERIAL.getCode() + File.separator + FILE_NAME;
		return SAVE_PATH;
	}
	
	/**
	 * 판서본 폴더 경로
	 * @param ROOT_PATH
	 * @return
	 */
	public static String getCopyRootPath(String ROOT_PATH) {
		String SAVE_PATH = ROOT_PATH + File.separator + "COPY";
		return SAVE_PATH;
	}
	
	/**
	 * 판서본 파일 경로(C:/B-PLMS/uploadFile/년/월/회의번호/COPY/파일명)
	 * @param ROOT_PATH : 회의의 파일을 저장할 최상위 폴더 경로
	 * @param FILE_NAME : 경로를 가져올 파일명
	 * @return
	 */
	public static String getCopyFilePath(String ROOT_PATH, String FILE_NAME) {
		String SAVE_PATH = ROOT_PATH + File.separator + "COPY" + File.separator + FILE_NAME;
		return SAVE_PATH;
	}
	
	/**
	 * 회의록 폴더 경로(C:/B-PLMS/uploadFile/년/월/회의번호/REPORT)
	 * @param ROOT_PATH
	 * @return
	 */
	public static String getReportRootPath(String ROOT_PATH) {
		String SAVE_PATH = ROOT_PATH + File.separator + "REPORT";
		return SAVE_PATH;
	}
	
	/**
	 * 회의록 파일 경로(C:/B-PLMS/uploadFile/년/월/회의번호/REPORT/파일명)
	 * @param ROOT_PATH : 회의의 파일을 저장할 최상위 폴더 경로
	 * @param FILE_NAME : 경로를 가져올 파일명
	 * @return
	 */
	public static String getReportFilePath(String ROOT_PATH, String FILE_NAME) {
		String SAVE_PATH = ROOT_PATH + File.separator + "REPORT" + File.separator + FILE_NAME;
		return SAVE_PATH;
	}
	
	/**
	 * 모든 회의자료의 책자를 보관한 최상위 폴더 경로(C:/B-PLMS/uploadFile/년/월/회의번호/COPY/BOOKLET)
	 * @param ROOT_PATH
	 * @return
	 */
	public static String getBookletRootPath(String ROOT_PATH) {
		String COPY_PATH = getCopyRootPath(ROOT_PATH);
		String BOOKLET_PATH = COPY_PATH + File.separator + "BOOKLET";
		return BOOKLET_PATH;
	}
	
	/**
	 * 특정 회의자료의 책자를 보관한 경로(C:/B-PLMS/uploadFile/년/월/회의번호/COPY/BOOKLET/폴더명)
	 * @param ROOT_PATH
	 * @param FOLDER_NAME
	 * @return
	 */
	public static String getBookletFolderPath(String ROOT_PATH, String FOLDER_NAME) {
		String COPY_PATH = getCopyRootPath(ROOT_PATH);
		String BOOKLET_PATH = COPY_PATH + File.separator + "BOOKLET" + File.separator + FOLDER_NAME;
		return BOOKLET_PATH;
	}
	
	/**
	 * 특정 회의자료의 책자 원본 폴더 경로(C:/B-PLMS/uploadFile/년/월/회의번호/COPY/BOOKLET/폴더명/SOURCE)
	 * @param ROOT_PATH : 회의의 파일을 저장할 최상위 폴더 경로
	 * @param FOLDER_NAME : 경로를 가져올 파일명
	 * @return
	 */
	public static String getBookletSourceFolderPath(String ROOT_PATH, String FOLDER_NAME) {
		String COPY_PATH = getCopyRootPath(ROOT_PATH);
		String BOOKLET_PATH = COPY_PATH + File.separator + "BOOKLET" + File.separator + FOLDER_NAME + File.separator + "SOURCE";
		return BOOKLET_PATH;
	}
	
	/**
	 * 책자 개인 폴더 경로(C:/B-PLMS/uploadFile/년/월/회의번호/COPY/BOOKLET/폴더명/사원번호)
	 * @param ROOT_PATH : 회의의 파일을 저장할 최상위 폴더 경로
	 * @param FOLDER_NAME : 경로를 가져올 파일명
	 * @param uploaderId : 개인 소유본을 가져올 사원번호
	 * @return
	 */
	public static String getBookletUserPath(String ROOT_PATH, String FOLDER_NAME, String uploaderId) {
		String COPY_PATH = getCopyRootPath(ROOT_PATH);
		String BOOKLET_PATH = COPY_PATH + File.separator + "BOOKLET" + File.separator + FOLDER_NAME + File.separator + uploaderId;
		return BOOKLET_PATH;
	}
	
	/**
	 * 생성할 판서본의 책자를 보관한 최상위 폴더 경로(C:/B-PLMS/uploadFile/년/월/회의번호/COPY/PRINTER)
	 * @param ROOT_PATH
	 * @return
	 */
	public static String getPrinterRootPath(String ROOT_PATH) {
		String PRINTER_PATH = ROOT_PATH + File.separator + "PRINTER";
		return PRINTER_PATH;
	}
	
	/**
	 * 생성할 판서본의 책자를 보관한 폴더 경로(C:/B-PLMS/uploadFile/년/월/회의번호/COPY/PRINTER/폴더명(UUID))
	 * @param ROOT_PATH
	 * @param FOLDER_NAME : UUID
	 * @return
	 */
	public static String getPrinterPath(String ROOT_PATH, String FOLDER_NAME) {
		String PRINTER_PATH = ROOT_PATH + File.separator + "PRINTER" + File.separator + FOLDER_NAME;
		return PRINTER_PATH;
	}
	
	/**
	 * 뷰 폴더 경로(C:/B-PLMS/uploadFile/년/월/회의번호/VIEW)
	 * @param ROOT_PATH : 회의의 파일을 저장할 최상위 폴더 경로
	 * @return
	 */
	public static String getViewPath(String ROOT_PATH) {
		String SAVE_PATH = ROOT_PATH + File.separator + "VIEW";
		return SAVE_PATH;
	}
	
	/**
	 * 뷰 파일 경로(C:/B-PLMS/uploadFile/년/월/회의번호/VIEW/파일명)
	 * @param ROOT_PATH : 회의의 파일을 저장할 최상위 폴더 경로
	 * @param FILE_NAME : 경로를 가져올 파일명
	 * @return
	 */
	public static String getViewFilePath(String ROOT_PATH, String FILE_NAME) {
		String SAVE_PATH = ROOT_PATH + File.separator + "VIEW" + File.separator + FILE_NAME;
		return SAVE_PATH;
	}
	
	/**
	 * 메모 폴더 경로(C:/B-PLMS/uploadFile/년/월/회의번호/MEMO)
	 * @param ROOT_PATH : 회의의 파일을 저장할 최상위 폴더 경로
	 * @return
	 */
	public static String getMemoRootPath(String ROOT_PATH) {
		String SAVE_PATH = ROOT_PATH + File.separator + FileRole.MEMO;
		return SAVE_PATH;
	}
	
	/**
	 * 메모 파일 경로(C:/B-PLMS/uploadFile/년/월/회의번호/MEMO/파일명)
	 * @param ROOT_PATH : 회의의 파일을 저장할 최상위 폴더 경로
	 * @param FILE_NAME : 경로를 가져올 파일명
	 * @return
	 */
	public static String getMemoFilePath(String ROOT_PATH, String FILE_NAME) {
		String SAVE_PATH = ROOT_PATH + File.separator + FileRole.MEMO + File.separator + FILE_NAME;
		return SAVE_PATH;
	}
	
	/**
	 * 사용자가 작성한 메모 이미지 파일을 보관해둘 최상위 경로(C:/B-PLMS/uploadFile/년/월/회의번호/MEMO/STORE)
	 * @param ROOT_PATH : 회의의 파일을 저장할 최상위 폴더 경로
	 * @return
	 */
	public static String getMemoStoreRootPath(String ROOT_PATH) {
		String SAVE_PATH = ROOT_PATH + File.separator + FileRole.MEMO + File.separator + "STORE";
		return SAVE_PATH;
	}
	
	/**
	 * 사용자가 작성한 메모 보관소 경로(C:/B-PLMS/uploadFile/년/월/회의번호/MEMO/STORE/사원번호)
	 * @param ROOT_PATH : 회의의 파일을 저장할 최상위 폴더 경로
	 * @param uploaderId : 개인 소유본을 가져올 사원번호
	 * @return
	 */
	public static String getMemoStorePath(String ROOT_PATH, String uploaderId) {
		String SAVE_PATH = ROOT_PATH + File.separator + FileRole.MEMO + File.separator + "STORE" + File.separator + uploaderId;
		return SAVE_PATH;
	}
	
	/**
	 * 사진 폴더 경로(C:/B-PLMS/uploadFile/년/월/회의번호/PHOTO)
	 * @param ROOT_PATH : 회의의 파일을 저장할 최상위 폴더 경로
	 * @return
	 */
	public static String getPhotoRootPath(String ROOT_PATH) {
		String SAVE_PATH = ROOT_PATH + File.separator + "PHOTO";
		return SAVE_PATH;
	}
	
	/**
	 * 사진 파일 경로(C:/B-PLMS/uploadFile/년/월/회의번호/PHOTO/파일명)
	 * @param ROOT_PATH : 회의의 파일을 저장할 최상위 폴더 경로
	 * @param FILE_NAME : 경로를 가져올 파일명
	 * @return
	 */
	public static String getPhotoFilePath(String ROOT_PATH, String FILE_NAME) {
		String SAVE_PATH = ROOT_PATH + File.separator + "PHOTO" + File.separator + FILE_NAME;
		return SAVE_PATH;
	}
	
	/**
	 * 음성 폴더 경로(C:/B-PLMS/uploadFile/년/월/회의번호/VOICE)
	 * @param ROOT_PATH : 회의의 파일을 저장할 최상위 폴더 경로
	 * @return
	 */
	public static String getVoiceRootPath(String ROOT_PATH) {
		String SAVE_PATH = ROOT_PATH + File.separator + "VOICE";
		return SAVE_PATH;
	}
	
	/**
	 * 음성 파일 경로(C:/B-PLMS/uploadFile/년/월/회의번호/VOICE/파일명)
	 * @param ROOT_PATH : 회의의 파일을 저장할 최상위 폴더 경로
	 * @param FILE_NAME : 경로를 가져올 파일명
	 * @return
	 */
	public static String getVoiceFilePath(String ROOT_PATH, String FILE_NAME) {
		String SAVE_PATH = ROOT_PATH + File.separator + "VOICE" + File.separator + FILE_NAME;
		return SAVE_PATH;
	}
}
