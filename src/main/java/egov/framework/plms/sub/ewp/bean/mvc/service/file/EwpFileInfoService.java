package egov.framework.plms.sub.ewp.bean.mvc.service.file;

import java.io.File;
import java.time.LocalDateTime;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import egov.framework.plms.main.bean.component.properties.DrmConfigProperties;
import egov.framework.plms.main.bean.mvc.entity.file.FileDetailVO;
import egov.framework.plms.main.bean.mvc.mapper.file.abst.FileInfoAbstractMapper;
import egov.framework.plms.main.bean.mvc.service.file.abst.FileInfoAbstractService;
import egov.framework.plms.main.core.model.enums.file.FileCategory;
import egov.framework.plms.main.core.util.CommUtil;
import egov.framework.plms.main.core.util.DrmUtil;
import egov.framework.plms.main.core.util.FileCommUtil;
import egov.framework.plms.main.core.util.SecurityUtil;
import egov.framework.plms.sub.ewp.bean.component.file.EwpFileModelPathResolver;
import lombok.extern.slf4j.Slf4j;

/**
 * @author mckim
 * @version 1.0
 * @since 2023. 2. 15
 */
@Slf4j
@Service
@Profile("ewp")
@Primary
public class EwpFileInfoService extends FileInfoAbstractService<FileDetailVO> {
	@Value("${config.file.upload-path}") 
	protected String UPLOAD_ROOT_PATH;
	@Autowired
	private DrmConfigProperties drmConfig;
	public EwpFileInfoService(FileInfoAbstractMapper<FileDetailVO> mapper, EwpFileModelPathResolver filePathResolver) {
		super(mapper, filePathResolver);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected FileDetailVO generateFileModel(MultipartFile multipartFile) {
		// TODO Auto-generated method stub
		String uploaderId = SecurityUtil.getLoginId();
		String simpleUUID = CommUtil.getUUID(12);
		String uploadedFileName = FilenameUtils.getName(multipartFile.getOriginalFilename());
		String fileLabel = FilenameUtils.getBaseName(multipartFile.getOriginalFilename());
		String fileExt = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
		FileCategory fileCategory = FileCommUtil.getFileCategory(multipartFile);
		Long fileSize = multipartFile.getSize();
		Character pdfGeneratedYN = (fileCategory == FileCategory.PDF)?'Y':'N';
		LocalDateTime nowDT = LocalDateTime.now();
		return FileDetailVO.builder()
				.uploaderId(uploaderId)
				.fileCategory(fileCategory)
				.uploadedFileName(uploadedFileName).fileLabel(fileLabel).fileExt(fileExt)
				.uuid(simpleUUID).fileSize(fileSize).pdfGeneratedYN(pdfGeneratedYN).uploadDateTime(nowDT).build();
	}

	public boolean writeFile(String SAVE_FILE_PATH, MultipartFile file) {
		try {
			log.info("Write File - SAVE_FILE_PATH: {}, FILE: {}", SAVE_FILE_PATH, file);
			super.writeFile(SAVE_FILE_PATH, file);
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
}
