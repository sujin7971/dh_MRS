package egov.framework.plms.main.core.util;

import java.util.Optional;

import org.apache.commons.io.FilenameUtils;

import egov.framework.plms.main.bean.mvc.entity.file.abst.FileDetailModelVO;


/**
 * 프로그램에서 공통으로 정의한 파일 이름 명명 규칙을 제공할 로직을 모아둔 클래스
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 */
public class FileNameUtil {
	/**
	 * 파일 저장 이름 입력 형식을 반환
	 * @param file
	 * @return UUID.xxx
	 */
	public static String getFileNameFormat(FileDetailModelVO file) {
		return Optional.ofNullable(file).map(vo -> vo.getUuid() + "." + FilenameUtils.getExtension(vo.getUploadedFileName())).orElse(null);
	}
	
	public static String getViewNameFormat(FileDetailModelVO file) {
		return Optional.ofNullable(file).map(vo -> vo.getUuid() + ".pdf").orElse(null);
	}
	
	/**
	 * 파일 저장 폴더 입력 형식을 반환
	 * @param file
	 * @return UUID
	 */
	public static String getFolderNameFormat(FileDetailModelVO file) {
		return Optional.ofNullable(file).map(vo -> FilenameUtils.getBaseName(vo.getUuid())).orElse(null);
	}
	
	/**
	 * 판서본 파일 저장 이름 입력 형식을 반환
	 * @param file
	 * @param userId
	 * @return
	 */
	public static String getCopyNameFormat(FileDetailModelVO file, String userId) {
		String FILE_NAME = userId.toString() + "_" + file.getUuid() + "." + FilenameUtils.getExtension(file.getUploadedFileName());
		
		return FILE_NAME;
	}
	
	/**
	 * 확장자를 포함한 기존 파일명에서 PDF확장자를 가진 파일명으로 반환
	 * @param orgName : orgName.xxx
	 * @return : orgName.pdf
	 */
	public static String getPdfNameFormat(String orgName) {
		String FILE_NAME = FilenameUtils.getBaseName(orgName) + ".pdf";
		
		return FILE_NAME;
	}
	
	public static String getWebpNameFormat(String orgName) {
		String FILE_NAME = FilenameUtils.getBaseName(orgName) + ".webp";
		
		return FILE_NAME;
	}
	
	/**
	 * 해당 페이지의 저장 파일명 형식 반환
	 * @param page
	 * @return
	 */
	public static String getPageNameFormat(Integer page) {
		String imgName = (page < 10)?"000"+(page):(page < 100)?"00"+(page):(page < 1000)?"0"+(page):Integer.toString(page);
		
		return imgName;
	}
}
