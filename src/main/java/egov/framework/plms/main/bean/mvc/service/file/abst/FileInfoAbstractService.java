package egov.framework.plms.main.bean.mvc.service.file.abst;

import java.io.File;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import egov.framework.plms.main.bean.component.common.abst.FileModelPathResolver;
import egov.framework.plms.main.bean.mvc.entity.file.FileDetailVO;
import egov.framework.plms.main.bean.mvc.entity.file.abst.FileDetailModelVO;
import egov.framework.plms.main.bean.mvc.mapper.file.abst.FileInfoAbstractMapper;
import egov.framework.plms.main.core.model.enums.file.FileCategory;
import egov.framework.plms.main.core.model.enums.user.DomainRole;
import egov.framework.plms.main.core.util.FileCommUtil;
import egov.framework.plms.main.core.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class FileInfoAbstractService<T extends FileDetailModelVO> {
	protected final FileInfoAbstractMapper<T> mapper;
	protected final FileModelPathResolver<T> filePathResolver;
	
	public FileInfoAbstractService(FileInfoAbstractMapper<T> mapper, FileModelPathResolver<T> filePathResolver) {
		this.mapper = mapper;
		this.filePathResolver = filePathResolver;
	}

	public Optional<T> uploadFile(MultipartFile multipartFile) {
		try {
			T fileVO = generateFileModel(multipartFile);
			if(!SecurityUtil.hasRole(DomainRole.DEV) && fileVO.getFileCategory() == FileCategory.UNKNOWN) {
				return Optional.empty();
			}
			File UPLOAD_FILE = filePathResolver.getUploadedFile(fileVO);
			if(this.writeFile(UPLOAD_FILE.getParent(), UPLOAD_FILE.getName(), multipartFile)) {
				boolean result = insertFileOne(fileVO);
				if(!result) {
					this.removeFile(UPLOAD_FILE.getParent(), UPLOAD_FILE.getName());
				}
				return Optional.ofNullable(fileVO);
			}else {
				return Optional.empty();
			}
		}catch(Exception e) {
			log.error("Failed to upload one multipartFile with : {}", multipartFile);
			log.error("Failed to upload one multipartFile messages: {}", e.toString());
			e.printStackTrace();
			return Optional.empty();
		}
	}

	/**
	 * 파일 정보 등록
	 * @param fileVO
	 * @return
	 */
	protected boolean insertFileOne(T fileVO) {
		try {
			Integer result = mapper.insertFileOne(fileVO);
			return (result == 0)?false:true;
		}catch(Exception e){
			log.error("Failed to insert one FileInfo with params: {}", fileVO);
			log.error("Failed to insert one FileInfo messages: {}", e.toString());
			return false;
		}
	}
	
	protected abstract T generateFileModel(MultipartFile multipartFile);
	
	/**
	 * 파일 정보 수정
	 * @param fileVO
	 * @return
	 */
	public boolean updateFileOne(T fileVO) {
		try {
			Integer result = mapper.updateFileOne(fileVO);
			return (result == 0)?false:true;
		}catch(Exception e){
			log.error("Failed to update one FileInfo with params: {}", fileVO);
			log.error("Failed to update one FileInfo messages: {}", e.toString());
			return false;
		}
	}
	
	/**
	 * 파일을 DB에서 삭제 처리. 해당 파일의 fileStatus가 {@link #FileStatus}가 DELETED_BY_LOGIC 인 경우에 삭제 가능.
	 * @param fileVO : 삭제할 파일 정보
	 * @return
	 */
	public boolean deleteFileOne(Integer fileId) {
		try {
			Integer result = mapper.deleteFileOne(fileId);
			return (result == 0)?false:true;
		}catch(Exception e){
			return false;
		}
	}
	
	/**
	 * 파일을 로직상 삭제 처리.
	 * @param fileVO : 삭제할 파일 정보
	 * @return
	 */
	public boolean updateFileOneToDelete(Integer fileId) {
		try {
			Integer result = mapper.updateFileOneToDelete(fileId);
			return (result == 0)?false:true;
		}catch(Exception e){
			return false;
		}
	}
	
	/**
	 * 파일이 디스크에서 삭제되었음을 표시
	 * @param fileId
	 * @return
	 */
	public boolean updateFileStatusToRemove(Integer fileId) {
		try {
			Integer result = mapper.updateFileStatusToRemove(fileId);
			return (result == 0)?false:true;
		}catch(Exception e){
			return false;
		}
	}
	
	public Optional<T> selectFileOne(Integer fileId) {
		T vo = mapper.selectFileOne(fileId);
		return Optional.ofNullable(vo);
	}
	
	public List<T> selectFileList(T param) {
		return mapper.selectFileList(param);
	}
	
	/**
	 * 디스크에 남아있는 삭제된 파일 목록을 조회
	 *
	 * @return 디스크에 남아있는 삭제된 파일 목록
	 */
	public List<T> selectDeletedFileListOnDisk() {
		return mapper.selectDeletedFileListOnDisk();
	}
	
	/**
	 * PDF파일 생성이 필요한 파일 목록을 조회
	 * @return
	 */
	public List<T> selectFilesToGeneratePdf() {
		return mapper.selectFilesToGeneratePdf();
	}
	
	/**
	 * WEBP파일 생성이 필요한 파일 목록을 조회
	 * @return
	 */
	public List<T> selectFilesToGenerateWebp() {
		return mapper.selectFilesToGenerateWebp();
	}
	
	/**
	 * 파일 다운로드를 위한 File Instance를 생성
	 *
	 * @param fileId 파일 식별자
	 * @return 다운로드를 위한 File Instance, 파일이 존재하지 않거나 상태가 유효하지 않을 경우 null을 반환합니다.
	 */
	public Optional<File> createFileDownloadInstance(Integer fileId) throws NoSuchElementException {
		Optional<T> fileModel = this.selectFileOne(fileId);
		return createFileDownloadInstance(fileModel.get());
	}
	
	public Optional<File> createFileDownloadInstance(T fileModel) throws NoSuchElementException {
		if(fileModel == null) {
			throw new NoSuchElementException();
		}
		File file = filePathResolver.getUploadedFile(fileModel);
		return Optional.ofNullable(file);
	}
	
	/**
	 * 파일 미리보기를 위한 File Instance를 생성
	 * PDF 또는 webp 파일이 이미 생성된 경우 해당 파일을 반환합니다.
	 *
	 * @param fileId 파일 식별자
	 * @return 미리보기를 위한 File Instance, 파일이 존재하지 않거나 상태가 유효하지 않거나 미리보기 형식이 생성되지 않은 경우 null을 반환합니다.
	 */
	public Optional<File> createFileViewInstance(Integer fileId) throws NoSuchElementException {
		Optional<T> fileModel = this.selectFileOne(fileId);
		return createFileViewInstance(fileModel.get());
	}
	
	public Optional<File> createFileViewInstance(T fileModel) throws NoSuchElementException {
		if(fileModel == null) {
			throw new NoSuchElementException();
		}
		File file = filePathResolver.getConvertedFileForPreview(fileModel);
		return Optional.ofNullable(file);
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
			return true;
		} catch (Exception e) {
			log.error("Failed to write File with save path: {}, file: {}", SAVE_FILE_PATH, file);
			log.error("Failed to write File messages: {}", e.toString());
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
