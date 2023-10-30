package egov.framework.plms.main.bean.mvc.service.file.abst;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import egov.framework.plms.main.bean.component.properties.DrmConfigProperties;
import egov.framework.plms.main.core.util.DrmUtil;
import egov.framework.plms.main.core.util.FileCommUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class FileDiskAbstractService {
	private DrmConfigProperties drmConfig;
	
	public FileDiskAbstractService(DrmConfigProperties drmConfig) {
		this.drmConfig = drmConfig;
	}
	
	public boolean writeFile(String SAVE_FOLDER_PATH, String SAVE_NAME, MultipartFile file) {
		return writeFile(SAVE_FOLDER_PATH+File.separator+SAVE_NAME, file);
	}
	
	public boolean writeFile(String SAVE_FILE_PATH, MultipartFile file) {
		try {
			File SAVE_FILE = new File(SAVE_FILE_PATH);
			File directory = SAVE_FILE.getParentFile();
			directory.mkdirs();
			log.info("Write File - SAVE_FILE_PATH: {}, FILE: {}", SAVE_FILE_PATH, file);
			FileCommUtil.copyFile(SAVE_FILE_PATH, file);
			if(drmConfig.isEnabled()) { //DRM 디코딩 처리 여부
				File encrytFile = FileCommUtil.convert(file);
				boolean decrpytRes = decryptFile(SAVE_FILE_PATH, encrytFile);
				if(!decrpytRes) {
					return false;
				}else {
					encrytFile.delete();
				}
			}
			return true;
		} catch (Exception e) {
			log.error("Failed to write File with save path: {}, file: {}", SAVE_FILE_PATH, file);
			log.error("Failed to write File messages: {}", e.toString());
			return false;
		}
	}
	
	public boolean decryptFile(String SAVE_FOLDER_PATH, String SAVE_NAME, File encrytFile) {
		return decryptFile(SAVE_FOLDER_PATH + File.separator + SAVE_NAME, encrytFile);
	}
	
	public boolean decryptFile(String SAVE_FILE_PATH, File encrytFile) {
        //DRM 디코딩 설치 서버에서 테스트 시 적용 
		DrmUtil drmUtil= new DrmUtil();
		//파일다운로드
		if(encrytFile != null){
			//DRM Check 및 변환
			int checkDrmCode = drmUtil.getFileTypeCode(encrytFile);
			if(checkDrmCode == 103){//FSN 파일입니다.
				// 암호화 된 파일 복호화
				encrytFile = drmUtil.DoExtract(encrytFile, drmConfig.getKeyPath(), drmConfig.getKey(), SAVE_FILE_PATH);
				if(encrytFile == null) {
					log.info("DRM 복호화 실패");
					return false;
				}
				log.info("DRM 복호화 성공");
				return true;
			}else if(checkDrmCode == 29){//암호화 파일이 아닙니다.
				log.info("DRM 복호화 예외- DRM 미적용 파일");
				return true;
			}else {
				log.info("DRM 복호화 실패- 지원 확장자가 아닌 파일");
				return false;
			}
		}else {
			return false;
		}
	}
	
	public boolean removeFile(String FILE_PATH) {
		try {
			File file = new File(FILE_PATH);
			log.info("Remove File - FILE_PATH: {}", FILE_PATH);
			if(file.exists() && !file.isDirectory()) {
				file.delete();
				return true;
			}
			return false;
		}catch(Exception e) {
			log.error("Failed to remove File with file path: {}", FILE_PATH);
			log.error("Failed to remove File messages: {}", e.toString());
			return false;
		}
	}
	
	public boolean removeFile(String FOLDER_PATH, String FILE_NAME) {
		try {
			File file = new File(FOLDER_PATH + File.separator + FILE_NAME);
			log.info("Remove File - FOLDER_PATH: {}, FILE_NAME: {}", FOLDER_PATH, FILE_NAME);
			if(file.exists() && !file.isDirectory()) {
				file.delete();
				return true;
			}
			return false;
		}catch(Exception e) {
			log.error("Failed to remove File with folder path: {}, file name: {}", FOLDER_PATH, FILE_NAME);
			log.error("Failed to remove File messages: {}", e.toString());
			return false;
		}
	}
	
	/**
	 * 해당 경로를 가진 폴더 삭제
	 * @param FOLDER_PATH : 삭제할 폴더 경로
	 * @return
	 */
	public boolean removeFolder(String FOLDER_PATH) {
		try {
			File folder = new File(FOLDER_PATH);
			FileUtils.deleteDirectory(folder);
			return true;
		} catch (Exception e) {
			log.error("Failed to remove Folder with folder path: {}", FOLDER_PATH);
			log.error("Failed to remove Folder messages: {}", e.toString());
			return false;
		}
	}
	
	public ResponseEntity<Resource> fileDownload(File file, String downloadName){
		return FileCommUtil.getFileDownloadResource(file, downloadName);
	}
	
	public ResponseEntity<Resource> fileView(File file){
		return FileCommUtil.getFileResource(file);
	}
}
