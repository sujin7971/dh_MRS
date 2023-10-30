package egov.framework.plms.sub.lime.bean.mvc.service.file;

import java.time.LocalDateTime;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import egov.framework.plms.main.bean.mvc.entity.file.FileDetailVO;
import egov.framework.plms.main.bean.mvc.mapper.file.abst.FileInfoAbstractMapper;
import egov.framework.plms.main.bean.mvc.service.file.abst.FileInfoAbstractService;
import egov.framework.plms.main.core.model.enums.file.FileCategory;
import egov.framework.plms.main.core.util.CommUtil;
import egov.framework.plms.main.core.util.FileCommUtil;
import egov.framework.plms.main.core.util.SecurityUtil;
import egov.framework.plms.sub.lime.bean.component.file.LimeFileModelPathResolver;
import egov.framework.plms.sub.lime.bean.mvc.mapper.meeting.LimeMeetingInfoMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * @author mckim
 * @version 1.0
 * @since 2023. 2. 15
 */
@Slf4j
@Service
@Profile("lime")
@Primary
public class LimeFileInfoService extends FileInfoAbstractService<FileDetailVO> {
	@Value("${config.file.upload-path}") 
	protected String UPLOAD_ROOT_PATH;
	@Autowired
	private LimeMeetingInfoMapper mtInfoMapper;
	
	public LimeFileInfoService(FileInfoAbstractMapper<FileDetailVO> mapper, @Autowired LimeFileModelPathResolver filePathResolver) {
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
}
