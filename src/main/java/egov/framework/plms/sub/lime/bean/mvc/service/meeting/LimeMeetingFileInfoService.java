package egov.framework.plms.sub.lime.bean.mvc.service.meeting;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import egov.framework.plms.main.bean.mvc.entity.file.FileDetailVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingArchiveVO;
import egov.framework.plms.main.bean.mvc.mapper.file.abst.FileInfoAbstractMapper;
import egov.framework.plms.main.bean.mvc.service.file.FileRelationService;
import egov.framework.plms.main.core.model.enums.file.ConversionStatus;
import egov.framework.plms.main.core.model.enums.file.FileCategory;
import egov.framework.plms.main.core.model.enums.file.FileStatus;
import egov.framework.plms.main.core.model.enums.file.RelatedEntityType;
import egov.framework.plms.main.core.model.enums.file.RelationType;
import egov.framework.plms.main.core.util.CommUtil;
import egov.framework.plms.main.core.util.FileCommUtil;
import egov.framework.plms.main.core.util.SecurityUtil;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingArchiveVO;
import egov.framework.plms.sub.lime.bean.component.file.LimeFileModelPathResolver;
import egov.framework.plms.sub.lime.bean.mvc.mapper.meeting.LimeMeetingFileInfoMapper;
import egov.framework.plms.sub.lime.bean.mvc.mapper.meeting.LimeMeetingInfoMapper;
import egov.framework.plms.sub.lime.bean.mvc.service.file.LimeFileInfoService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author mckim
 * @version 1.0
 * @since 2023. 2. 15
 */
@Slf4j
@Service
@Profile("lime")
public class LimeMeetingFileInfoService extends LimeFileInfoService {
	@Autowired
	private LimeMeetingFileInfoMapper mtFileMapper;
	@Autowired
	private LimeMeetingInfoMapper infoMapper;
	@Autowired
	private FileRelationService relationServ;
	
	public LimeMeetingFileInfoService(FileInfoAbstractMapper<FileDetailVO> mapper, @Autowired LimeFileModelPathResolver filePathResolver) {
		super(mapper, filePathResolver);
		// TODO Auto-generated constructor stub
	}
	protected FileDetailVO generateFileModel(MultipartFile multipartFile) {
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
	
	public List<FileDetailVO> getMeetingFileList(Integer meetingId, String uploaderId) {
		return mtFileMapper.getMeetingFileList(FileDetailVO.builder()
				.relatedEntityType(RelatedEntityType.MEETING)
				.relatedEntityId(meetingId)
				.uploaderId(uploaderId).build());
	}
	
	/**
	 * 회의의 모든 파일 목록
	 * @param meetingKey
	 * @return
	 */
	public List<FileDetailVO> getMeetingFileList(Integer meetingId, RelationType relationType) {
		return mtFileMapper.getMeetingFileList(FileDetailVO.builder()
				.relatedEntityType(RelatedEntityType.MEETING)
				.relatedEntityId(meetingId)
				.relationType(relationType).build());
	}
	
	public List<FileDetailVO> getMeetingSharedFileList(Integer meetingId) {
		return mtFileMapper.getMeetingSharedFileList(FileDetailVO.builder()
				.relatedEntityType(RelatedEntityType.MEETING)
				.relatedEntityId(meetingId).build());
	}
	
	public List<FileDetailVO> getMeetingPrivateFileList(Integer meetingId, String userId) {
		return mtFileMapper.getMeetingPrivateFileList(FileDetailVO.builder()
				.relatedEntityType(RelatedEntityType.MEETING)
				.relatedEntityId(meetingId)
				.uploaderId(userId).build());
	}
	
	public List<FileDetailVO> getMeetingPrivateFileList(Integer meetingId, String userId, RelationType relationType) {
		return mtFileMapper.getMeetingPrivateFileList(FileDetailVO.builder()
				.relatedEntityType(RelatedEntityType.MEETING)
				.relatedEntityId(meetingId)
				.relationType(relationType)
				.uploaderId(userId).build());
	}
	
	/**
	 * 파일함에 표시할 회의 목록 및 회의 파일 조회<br>
	 * 
	 * 개인 파일함 검색인 경우 {@link EwpMeetingArchiveVO}의 <code>userKey<code>에 사용자 고유키 설정<br>
	 * 부서 파일함 검색인 경우 {@link EwpMeetingArchiveVO}의 <code>deptId<code>에 사용자 소속 부서 고유키 설정<br>
	 * @param param
	 * @return
	 */
	public List<MeetingArchiveVO> selectMeetingArchiveList(MeetingArchiveVO param){
		return mtFileMapper.selectMeetingArchiveList(param);
	}
	
	/**
	 * 판서를 위한 페이지별 이미지를 생성해야하는 회의자료 목록 조회
	 * @return
	 */
	public List<FileDetailVO> selectMeetingMaterialFilesToGenerateImages() {
		return mtFileMapper.selectMeetingMaterialFilesToGenerateImages();
	}
	
	/**
	 * 판서한 이미지의 webp 변환이 필요한 판서본 목록 조회
	 * @return
	 */
	public List<FileDetailVO> selectMeetingCopyFilesToIntegrateImages() {
		return mtFileMapper.selectMeetingCopyFilesToIntegrateImages();
	}
	
	/**
	 * PDF 파일을 생성해야하는 판서본 목록 조회
	 * @return
	 */
	public List<FileDetailVO> selectMeetingCopyFilesToGeneratePdf() {
		return mtFileMapper.selectMeetingCopyFilesToGeneratePdf();
	}
	
	/**
	 * 메모장 페이지별 이미지를 변환처리 및 이동해야하 하는 목록 조회
	 * @return
	 */
	public List<FileDetailVO> selectMeetingMemoFilesToIntegrateImages() {
		return mtFileMapper.selectMeetingMemoFilesToIntegrateImages();
	}
	
	/**
	 * 메모장 파일을 생성해야하는 메모 목록 조회
	 * @return
	 */
	public List<FileDetailVO> selectMeetingMemoFilesToGeneratePdf() {
		return mtFileMapper.selectMeetingMemoFilesToGeneratePdf();
	}
	
	public void generateCopyEditionFromNote(Integer meetingId, String userId) {
		List<FileDetailVO> materialList = getMeetingFileList(meetingId, RelationType.MEETING_MATERIAL);
		Map<Integer, Integer> copySourceMap = getMeetingPrivateFileList(meetingId, userId, RelationType.MEETING_COPY).stream().collect(Collectors.toMap(FileDetailVO::getSourceId,  FileDetailVO::getFileId));
		for(FileDetailVO material : materialList) {
			String SOURCE_FOLDER_PATH = filePathResolver.getOriginalImageCollectionPath(material);
			String NOTE_FOLDER_PATH = filePathResolver.getAttendeeWrittenImagePath(material, userId);
			
			File SOURCE_FOLDER = new File(SOURCE_FOLDER_PATH);
			File NOTE_FOLDER = new File(NOTE_FOLDER_PATH);
			if(!SOURCE_FOLDER.exists() || SOURCE_FOLDER.listFiles().length == 0 || !NOTE_FOLDER.exists()) {
				continue;
			}
			//사용자의 새로운 판서본을 생성하는 경우 해당 회의자료를 통해 생성된 기존 판서본을 제거
			Integer existingCopyFileId = copySourceMap.get(material.getFileId());
			if(existingCopyFileId != null) {
				updateFileOneToDelete(existingCopyFileId);
			}
			insertCopyEditionFileOne(material, userId);
		}
	}
	
	public void insertCopyEditionFileOne(FileDetailVO material, String uploaderId) {
		Integer sourceId = material.getFileId();
		String simpleUUID = CommUtil.getUUID(12);
		String uploadedFileName = FilenameUtils.getBaseName(material.getFileLabel()) + ".pdf";
		String fileLabel = FilenameUtils.getBaseName(uploadedFileName);
		String fileExt = FilenameUtils.getExtension(uploadedFileName);
		FileDetailVO infoVO = FileDetailVO.builder()
				.uploaderId(uploaderId)
				.sourceId(sourceId)
				.fileCategory(FileCategory.PDF)
				.fileStatus(FileStatus.FILE_PENDING)
				.pageCount(material.getPageCount())
				.pdfGeneratedYN('N')
				.conversionStatus(ConversionStatus.NO_REQUEST)
				.uploadedFileName(uploadedFileName).fileLabel(fileLabel).fileExt(fileExt)
				.uuid(simpleUUID).build();
		boolean insertInfoResult = super.insertFileOne(infoVO);
		if(!insertInfoResult) {
			return;
		}
		Integer fileId = infoVO.getFileId();
		boolean insertRelationResult = relationServ.insertFileRelationOne(fileId, RelatedEntityType.MEETING, material.getRelatedEntityId(), RelationType.MEETING_COPY);
		if(!insertRelationResult) {
			super.deleteFileOne(fileId);
		}
	}
	
	public void generateMemo(Integer meetingId, String userId) {
		String MEMO_FOLDER_PATH = filePathResolver.getMeetingMemoImageCollectionPath(meetingId, userId);
		File MEMO_FOLDER = new File(MEMO_FOLDER_PATH);
		File[] memoPages = MEMO_FOLDER.listFiles();
		if(memoPages != null && memoPages.length == 0) {
			return;
		}
		//사용자의 새로운 메모 파일을 생성하는 경우 해당 회의의 기존 메모 파일을 제거
		List<FileDetailVO> memoFileList = getMeetingPrivateFileList(meetingId, userId, RelationType.MEETING_MEMO);
		memoFileList.forEach((fileModel) -> {
			Integer fileId = fileModel.getFileId();
			updateFileOneToDelete(fileId);
		});
		insertMemoFileOne(meetingId, userId);
	}
	
	public void insertMemoFileOne(Integer meetingId, String uploaderId) {
		String simpleUUID = CommUtil.getUUID(12);
		String uploadedFileName = "메모장.pdf";
		String fileLabel = FilenameUtils.getBaseName(uploadedFileName);
		String fileExt = FilenameUtils.getExtension(uploadedFileName);
		FileDetailVO infoVO = FileDetailVO.builder()
				.uploaderId(uploaderId)
				.fileCategory(FileCategory.PDF)
				.fileStatus(FileStatus.FILE_PENDING)
				.pdfGeneratedYN('N')
				.conversionStatus(ConversionStatus.NO_REQUEST)
				.uploadedFileName(uploadedFileName).fileLabel(fileLabel).fileExt(fileExt)
				.uuid(simpleUUID).build();
		boolean insertInfoResult = super.insertFileOne(infoVO);
		if(!insertInfoResult) {
			return;
		}
		Integer fileId = infoVO.getFileId();
		boolean insertRelationResult = relationServ.insertFileRelationOne(fileId, RelatedEntityType.MEETING, meetingId, RelationType.MEETING_MEMO);
		if(!insertRelationResult) {
			super.deleteFileOne(fileId);
		}
	}
	
	private FileDetailVO generateReportFileModel(Integer meetingId, MultipartFile multipartFile) {
		FileDetailVO fileVO = generateFileModel(multipartFile);
		return fileVO.toBuilder()
				.uploadedFileName("회의록.pdf").fileLabel("회의록").fileExt("pdf")
				.conversionStatus(ConversionStatus.NO_REQUEST)
				.relatedEntityType(RelatedEntityType.MEETING).relatedEntityId(meetingId).relationType(RelationType.MEETING_REPORT).build();
	}
	
	public Optional<FileDetailVO> uploadReportFile(Integer meetingId, MultipartFile multipartFile) {
		try {
			FileDetailVO fileVO = generateReportFileModel(meetingId, multipartFile);
			String REPORT_FILE_PATH = filePathResolver.getUploadedFilePath(fileVO);
			if(this.writeFile(REPORT_FILE_PATH, multipartFile)) {
				boolean insertInfoResult = insertFileOne(fileVO);
				if(!insertInfoResult) {
					this.removeFile(REPORT_FILE_PATH);
					return Optional.empty();
				}
				boolean insertRelationResult = relationServ.insertFileRelationOne(fileVO.getFileId(), RelatedEntityType.MEETING, meetingId, RelationType.MEETING_REPORT);
				if(!insertRelationResult) {
					super.deleteFileOne(fileVO.getFileId());
					return Optional.empty();
				}
				return Optional.ofNullable(fileVO);
			}else {
				return Optional.empty();
			}
		}catch(Exception e) {
			log.error("Failed to upload one multipartFile with : {}", multipartFile);
			log.error("Failed to upload one multipartFile messages: {}", e.toString());
			return Optional.empty();
		}
	}
}
