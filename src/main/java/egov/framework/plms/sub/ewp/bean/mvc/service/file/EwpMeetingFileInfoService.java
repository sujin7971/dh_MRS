package egov.framework.plms.sub.ewp.bean.mvc.service.file;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import egov.framework.plms.main.core.model.enums.file.FileCategory;
import egov.framework.plms.main.core.model.response.ErrorMessage;
import egov.framework.plms.main.core.model.response.ResponseMessage;
import egov.framework.plms.main.core.util.CommUtil;
import egov.framework.plms.main.core.util.DateTimeUtil;
import egov.framework.plms.main.core.util.FileCommUtil;
import egov.framework.plms.main.core.util.FileNameUtil;
import egov.framework.plms.sub.ewp.bean.component.repository.MeetingFileRepository;
import egov.framework.plms.sub.ewp.bean.mvc.entity.file.MeetingFileConvertVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.file.MeetingFileInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.mapper.maria.file.EwpMeetingFileInfoMapper;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingInfoService;
import egov.framework.plms.sub.ewp.core.model.enums.FileRole;
import egov.framework.plms.sub.ewp.core.util.MeetingFilePathUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author mckim
 * @version 1.0
 * @since 2023. 2. 15
 */
/**
 * @author mckim
 * @version 1.0
 * @since 2023. 2. 15
 */
@Slf4j
@Service
@Profile("ewp")
@RequiredArgsConstructor
public class EwpMeetingFileInfoService {
	@Value("${config.file.upload-path}")
	private String UPLOAD_PATH;
	
	private final EwpFileDiskService diskServ;
	private final EwpMeetingCvtService cvtServ;
	private final EwpMeetingInfoService mtServ;
	
	private final EwpMeetingFileInfoMapper fileMapper;
	private final MeetingFileRepository fileRepo;
	
	/**
	 * 여러 파일 업로드 처리. 파일을 업로드할 회의마다 개별로 폴더를 만들어 관리하며 여러 페이지로 이루어진 파일일경우 페이지수를 DB에 등록한다.
	 * @param meetingKey : 파일을 업로드할 회의 번호
	 * @param files : 업로드할 파일 목록
	 * @return
	 */
	public ResponseMessage uploadFileList(Integer meetingKey, FileRole roleType, List<MultipartFile> files) {
		if(!(files.size() > 0)) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
					.message(ResponseMessage.MessageCode.FILE.POST_FAIL.value())
					.detail(ResponseMessage.DetailCode.FILE.REQ_PARAM.value())
					.build();
		}
		List<ErrorMessage> errors = new ArrayList<>();
		for (MultipartFile file : files) {
			ResponseMessage response = uploadFile(meetingKey, roleType, file);
			if(!response.isSuccess()) {
				errors.addAll(response.getError());
			}
		}
		return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
				.message(ResponseMessage.MessageCode.FILE.POST_SUCCESS.value())
				.error(errors)
				.build();
	}
	
	public ResponseMessage uploadFile(Integer meetingKey, FileRole roleType, MultipartFile file) {
		switch(roleType) {
			case MATERIAL:
				return uploadMaterial(meetingKey, file);
			case REPORT:
				return uploadReport(meetingKey, file);
			case PHOTO:
				return uploadPhoto(meetingKey, file);
			case VOICE:
				return uploadVoice(meetingKey, file);
			default:
				return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
						.message(ResponseMessage.MessageCode.FILE.POST_FAIL.value())
						.build();	
		}
	}
	
	@Cacheable(value = "RootPath", key = "#meetingKey", unless="#result == null", condition="#meetingKey!=null")
	private String getMeetingRootPath(Integer meetingKey) {
		Optional<EwpMeetingInfoVO> meetingInfoOpt = mtServ.getMeetingInfoOne(meetingKey);
		if(!meetingInfoOpt.isPresent()) {
			return null;
		}
		return MeetingFilePathUtil.getRootPath(meetingInfoOpt.get());
	}
	
	public ResponseMessage uploadMaterial(Integer meetingKey, MultipartFile file) {
		FileCategory mimeType = FileCommUtil.getFileCategory(file);
		if(mimeType == null) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
					.message(ResponseMessage.MessageCode.FILE.POST_FAIL.value())
					.detail(ResponseMessage.DetailCode.FILE.UNPROCESSABLE_ENTITY.value())
					.error(ErrorMessage.builder(ErrorMessage.ErrorCode.INVALID_VALUE)
						.message(ErrorMessage.MessageCode.FILE.INVALID_FORMAT.value())
						.data(MeetingFileInfoVO.builder().originalName(file.getOriginalFilename()).build())
						.build())
					.build();
		}
		String simpleUUID = CommUtil.getUUID(12);
		String foriginalName = FilenameUtils.getName(file.getOriginalFilename());
		String fileName = FilenameUtils.getBaseName(file.getOriginalFilename());
		String fileExt = FilenameUtils.getExtension(file.getOriginalFilename());
		Long fsize = file.getSize();
		MeetingFileInfoVO materialVO = MeetingFileInfoVO.builder()
				.originalName(foriginalName)
				.fileName(fileName)
				.fileExt(fileExt)
				.meetingKey(meetingKey)
				.roleType(FileRole.MATERIAL)
				.mimeType(mimeType)
				.uuid(simpleUUID)
				.size(fsize)
				.state(0)
				.page(1)
				.build();
		String ROOT_PATH = getMeetingRootPath(meetingKey);
		String SAVE_PATH = MeetingFilePathUtil.getMaterialRootPath(ROOT_PATH);
		String SAVE_NAME = FileNameUtil.getFileNameFormat(materialVO);
		if(diskServ.writeFile(SAVE_PATH, SAVE_NAME, file)) {
			ResponseMessage response = postFile(materialVO);
			if(!response.isSuccess()) {
				diskServ.removeFile(SAVE_PATH, SAVE_NAME);
			}
			return response;
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
					.message(ResponseMessage.MessageCode.FILE.POST_FAIL.value())
					.detail(ResponseMessage.DetailCode.FILE.UNPROCESSABLE_ENTITY.value())
					.error(ErrorMessage.builder(ErrorMessage.ErrorCode.INVALID_VALUE)
						.message(ErrorMessage.MessageCode.FILE.UNPROCESSABLE_ENTITY.value())
						.data(MeetingFileInfoVO.builder().originalName(file.getOriginalFilename()).build())
						.build())
					.build();
		}
	}
	
	public ResponseMessage uploadReport(Integer meetingKey, MultipartFile file) {
		FileCategory mimeType = FileCategory.PDF;
		String simpleUUID = CommUtil.getUUID(12);
		String foriginalName = "회의록.pdf";
		String fileName = "회의록";
		String fileExt = "pdf";
		Long fsize = file.getSize();
		MeetingFileInfoVO materialVO = MeetingFileInfoVO.builder()
				.originalName(foriginalName)
				.fileName(fileName)
				.fileExt(fileExt)
				.meetingKey(meetingKey)
				.roleType(FileRole.REPORT)
				.mimeType(mimeType)
				.uuid(simpleUUID)
				.size(fsize)
				.state(2)
				.page(1)
				.build();
		String ROOT_PATH = getMeetingRootPath(meetingKey);
		String SAVE_PATH = MeetingFilePathUtil.getReportRootPath(ROOT_PATH);
		String SAVE_NAME = FileNameUtil.getFileNameFormat(materialVO);
		if(diskServ.writeFile(SAVE_PATH, SAVE_NAME, file)) {
			ResponseMessage response = postFile(materialVO);
			if(!response.isSuccess()) {
				diskServ.removeFile(SAVE_PATH, SAVE_NAME);
			}
			return response;
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
					.message(ResponseMessage.MessageCode.FILE.POST_FAIL.value())
					.detail(ResponseMessage.DetailCode.FILE.UNPROCESSABLE_ENTITY.value())
					.error(ErrorMessage.builder(ErrorMessage.ErrorCode.INVALID_VALUE)
						.message(ErrorMessage.MessageCode.FILE.UNPROCESSABLE_ENTITY.value())
						.data(MeetingFileInfoVO.builder().originalName(file.getOriginalFilename()).build())
						.build())
					.build();
		}
	}
	
	public ResponseMessage uploadPhoto(Integer meetingKey, MultipartFile file) {
		FileCategory mimeType = FileCategory.IMG;
		String simpleUUID = CommUtil.getUUID(12);
		String foriginalName = "회의촬영_"+meetingKey+"_"+LocalDateTime.now().format(DateTimeUtil.format("yyyyMMdd_HHmmss"))+".img";
		String fileName = "회의촬영_"+meetingKey+"_"+LocalDateTime.now().format(DateTimeUtil.format("yyyyMMdd_HHmmss"));
		String fileExt = "img";
		Long fsize = file.getSize();
		MeetingFileInfoVO materialVO = MeetingFileInfoVO.builder()
				.originalName(foriginalName)
				.fileName(fileName)
				.fileExt(fileExt)
				.meetingKey(meetingKey)
				.roleType(FileRole.PHOTO)
				.mimeType(mimeType)
				.uuid(simpleUUID)
				.size(fsize)
				.state(2)
				.page(1)
				.build();
		String ROOT_PATH = getMeetingRootPath(meetingKey);
		String SAVE_PATH = MeetingFilePathUtil.getReportRootPath(ROOT_PATH);
		String SAVE_NAME = FileNameUtil.getFileNameFormat(materialVO);
		if(diskServ.writeFile(SAVE_PATH, SAVE_NAME, file)) {
			ResponseMessage response = postFile(materialVO);
			if(!response.isSuccess()) {
				diskServ.removeFile(SAVE_PATH, SAVE_NAME);
			}
			return response;
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
					.message(ResponseMessage.MessageCode.FILE.POST_FAIL.value())
					.detail(ResponseMessage.DetailCode.FILE.UNPROCESSABLE_ENTITY.value())
					.error(ErrorMessage.builder(ErrorMessage.ErrorCode.INVALID_VALUE)
						.message(ErrorMessage.MessageCode.FILE.UNPROCESSABLE_ENTITY.value())
						.data(MeetingFileInfoVO.builder().originalName(file.getOriginalFilename()).build())
						.build())
					.build();
		}
	}
	
	public ResponseMessage uploadVoice(Integer meetingKey, MultipartFile file) {
		FileCategory mimeType = FileCategory.AUDIO;
		String simpleUUID = CommUtil.getUUID(12);
		String foriginalName = "회의녹음_"+meetingKey+"_"+LocalDateTime.now().format(DateTimeUtil.format("yyyyMMdd_HHmmss"))+".mp3";
		String fileName = "회의녹음_"+meetingKey+"_"+LocalDateTime.now().format(DateTimeUtil.format("yyyyMMdd_HHmmss"));
		String fileExt = "mp3";
		Long fsize = file.getSize();
		MeetingFileInfoVO materialVO = MeetingFileInfoVO.builder()
				.originalName(foriginalName)
				.fileName(fileName)
				.fileExt(fileExt)
				.meetingKey(meetingKey)
				.roleType(FileRole.VOICE)
				.mimeType(mimeType)
				.uuid(simpleUUID)
				.size(fsize)
				.state(2)
				.page(1)
				.build();
		String ROOT_PATH = getMeetingRootPath(meetingKey);
		String SAVE_PATH = MeetingFilePathUtil.getReportRootPath(ROOT_PATH);
		String SAVE_NAME = FileNameUtil.getFileNameFormat(materialVO);
		if(diskServ.writeFile(SAVE_PATH, SAVE_NAME, file)) {
			ResponseMessage response = postFile(materialVO);
			if(!response.isSuccess()) {
				diskServ.removeFile(SAVE_PATH, SAVE_NAME);
			}
			return response;
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
					.message(ResponseMessage.MessageCode.FILE.POST_FAIL.value())
					.detail(ResponseMessage.DetailCode.FILE.UNPROCESSABLE_ENTITY.value())
					.error(ErrorMessage.builder(ErrorMessage.ErrorCode.INVALID_VALUE)
						.message(ErrorMessage.MessageCode.FILE.UNPROCESSABLE_ENTITY.value())
						.data(MeetingFileInfoVO.builder().originalName(file.getOriginalFilename()).build())
						.build())
					.build();
		}
	}
	
	/**
	 * 파일 업로드
	 * @param meetingKey : 파일이 속할 회의의 고유키
	 * @param file : 업로드할 파일
	 * @return
	 */
	private ResponseMessage postFile(MeetingFileInfoVO fileVO) {
		try {
			fileMapper.postMeetingFile(fileVO);
			return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
					.message(ResponseMessage.MessageCode.FILE.POST_SUCCESS.value())
					.build();
		}catch(Exception e) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.UNPROCESSABLE_ENTITY)
					.message(ResponseMessage.MessageCode.FILE.POST_FAIL.value())
					.build();
		}
	}
	
	/**
	 * 판서본 파일 생성
	 * @param schedule : 판서본이 속할 회의
	 * @param loginKey : 판서본을 생성할 참석자 사원 고유키
	 */
	public void postCopy(EwpMeetingInfoVO meeting, String loginKey) {
		Integer meetingKey = meeting.getMeetingKey();
		List<MeetingFileInfoVO> fileList = getMeetingFileList(MeetingFileInfoVO.builder()
				.meetingKey(meetingKey)
				.roleType(FileRole.MATERIAL)
				.build());
		for(MeetingFileInfoVO orgVO : fileList) {
			//회의의 자료가 저장된 경로
			String ROOT_PATH = MeetingFilePathUtil.getRootPath(meeting);
			String BOOKLET_ROOT_FOLDER_PATH = MeetingFilePathUtil.getBookletFolderPath(ROOT_PATH, FileNameUtil.getFolderNameFormat(orgVO));
			// 회의 자료 원본 책자 경로
			String BOOKLET_SOURCE_FOLDER_PATH = MeetingFilePathUtil.getBookletSourceFolderPath(ROOT_PATH, FileNameUtil.getFolderNameFormat(orgVO));
			log.debug("BOOKLET_ROOT_FOLDER_PATH : {}",BOOKLET_ROOT_FOLDER_PATH);
			// 사용자가 생성한 책자가 저장된 폴더
			File bookletRootFolder = new File(BOOKLET_ROOT_FOLDER_PATH);
			// 사용자 책자 폴더 목록
			File[] bookletList = bookletRootFolder.listFiles();
			// 원본 책자 폴더
			File bookletSourceFolder = new File(BOOKLET_SOURCE_FOLDER_PATH);
			if(bookletList == null) {
				log.debug("bookletList is null");
				continue;
			}
			log.debug("bookletList : {}",bookletList.toString());
			for(File bookletFolder : bookletList) {
				String bookletName = bookletFolder.getName();
				File[] pageList = bookletFolder.listFiles();
				log.debug("bookletFolder Folder Name : {}", bookletName);
				if(bookletName.equals("SOURCE")) {
					log.debug("원본 폴더는 패스");
					continue;
				}else if(pageList == null || pageList.length == 0) {
					log.debug("안에 판서한 이미지 없음");
					continue;
				}
				String empKey = bookletName;
				//전체 대상 변환이 아니고 현재 폴더의 소유자와 로그인한 사원의 사번이 다른 경우
				if(!loginKey.equals(empKey)) {
					log.debug("판서본 소유자 아님. 폴더 소유자 : {}, 로그인 : {}",empKey, loginKey);
					continue;
				}
				
				//동일 책자를 기반으로 이미 생성 요청된 판서본이 있는 경우 삭제
				deleteAllCopy(orgVO.getFileKey(), empKey);
				
				MeetingFileInfoVO copy = MeetingFileInfoVO.generateCopy(meetingKey, loginKey, orgVO);
				log.debug("copy vo : {}", copy);
				
				File printerFolder = new File(MeetingFilePathUtil.getPrinterPath(ROOT_PATH, copy.getUuid()));
				try {
					FileUtils.copyDirectory(bookletSourceFolder, printerFolder);
					FileUtils.copyDirectory(bookletFolder, printerFolder);
					fileMapper.postMeetingFile(copy);
				} catch (IOException e) {
					log.error(e.getMessage());
				}
			}
		}
	}
	
	public void postMemo(EwpMeetingInfoVO meeting, String userKey) {
		Integer meetingKey = meeting.getMeetingKey();

		
		//회의의 자료가 저장된 경로
		String ROOT_PATH = MeetingFilePathUtil.getRootPath(meeting);
		String USER_MEMO_FOLDER_PATH = MeetingFilePathUtil.getMemoStorePath(ROOT_PATH, userKey);
		
		MeetingFileInfoVO memoVO = getMemoFileOne(meetingKey, userKey);
		if(memoVO != null) {
			deleteMemo(ROOT_PATH, memoVO);
		}
		File memoFolder = new File(USER_MEMO_FOLDER_PATH);
		if(memoFolder == null) {
			return;
		}
		File[] memoPages = memoFolder.listFiles();
		if(memoPages != null && memoPages.length == 0) {
			return;
		}
		memoVO = MeetingFileInfoVO.generateMemo(meetingKey, userKey);
		File printerFolder = new File(MeetingFilePathUtil.getPrinterPath(ROOT_PATH, memoVO.getUuid()));
		try {
			FileUtils.copyDirectory(memoFolder, printerFolder);
			fileMapper.postMeetingFile(memoVO);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
	
	/**
	 * 파일 정보 수정
	 * @param file
	 * @return
	 */
	public Integer putMeetingFile(MeetingFileInfoVO file) {
		return fileMapper.putMeetingFile(file);
	}
	
	/**
	 * 파일 만료 처리. 디스크에서 파일을 삭제하고 DB에 파일 상태를 만료로 갱신.
	 * @param expList : 만료 처리할 파일 리스트
	 * @return
	 */
	public void removeExpFileOnDisk() {
		List<MeetingFileInfoVO> list = fileMapper.getFileListToDeleteByExpiration();
		for(MeetingFileInfoVO fileVO : list) {
			if(diskServ.removeFile(fileVO)) {
				fileRepo.removeQueue(fileVO.getFileKey());
				fileMapper.putMeetingFile(MeetingFileInfoVO.builder()
						.fileKey(fileVO.getFileKey())
						.state(-2)
						.build());
			}
		}
	}
	
	public void removeDeletedFileOnDisk() {
		List<MeetingFileInfoVO> list = fileMapper.getDeletedFileListOnDisk();
		for(MeetingFileInfoVO fileVO : list) {
			if(diskServ.removeFile(fileVO)) {
				fileRepo.removeQueue(fileVO.getFileKey());
				fileMapper.putMeetingFile(MeetingFileInfoVO.builder()
						.fileKey(fileVO.getFileKey())
						.state(-3)
						.build());
			}
		}
	}
	
	/**
	 * 회의의 업로드 폴더 삭제(회의 취소한 경우)
	 * @param meetingKey : 자료를 모두 삭제할 회의 번호
	 * @return
	 */
	public boolean deleteMeetingFolder(Integer meetingKey) {
		if(diskServ.removeMeetingFolder(meetingKey)) {
			List<MeetingFileInfoVO> volist = getMeetingFileList(MeetingFileInfoVO.builder()
					.meetingKey(meetingKey)
					.build());
			for(MeetingFileInfoVO vo : volist) {
				fileMapper.deleteMeetingFile(vo.getFileKey());
				fileRepo.removeQueue(vo.getFileKey());
			}
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * 파일 리스트 삭제 처리. DB와 디스크에서 모두 삭제한다.
	 * @param delList : 삭제할 파일 정보 리스트
	 * @return
	 */
	public ResponseMessage deleteFileList(List<Integer> delList) {
		if(!(delList.size() > 0)) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
					.message(ResponseMessage.MessageCode.FILE.DELETE_FAIL.value())
					.detail(ResponseMessage.DetailCode.FILE.REQ_PARAM.value())
					.build();
		}
		
		List<ErrorMessage> errors = new ArrayList<>();
		for (Integer fileKey : delList) {
			ResponseMessage response = deleteFile(fileKey);
			if(!response.isSuccess()) {
				List<ErrorMessage> deleteErrors = response.getError();
				errors.addAll(deleteErrors);
			}
		}
		return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
				.message(ResponseMessage.MessageCode.FILE.DELETE_SUCCESS.value())
				.error(errors)
				.build();
	}
	
	/**
	 * 파일 삭제 처리. DB와 디스크에서 모두 삭제한다.
	 * @param fileVO : 삭제할 파일 정보
	 * @return
	 */
	public ResponseMessage deleteFile(Integer fileKey) {
		deleteAllCopy(fileKey);
		MeetingFileInfoVO fileVO = getFileOne(fileKey);
		Integer result = fileMapper.deleteMeetingFile(fileKey);
		if(result != 0) {
			fileRepo.removeQueue(fileKey);
			
			return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
					.message(ResponseMessage.MessageCode.FILE.DELETE_SUCCESS.value())
					.build(); 
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.UNPROCESSABLE_ENTITY)
					.message(ResponseMessage.MessageCode.FILE.DELETE_FAIL.value())
					.error(ErrorMessage.builder(ErrorMessage.ErrorCode.NOT_UPDATED)
							.data(fileKey)
							.build())
					.build(); 
		}
	}
	
	public ResponseMessage updateFileListToDelete(List<Integer> delList) {
		if(!(delList.size() > 0)) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
					.message(ResponseMessage.MessageCode.FILE.DELETE_FAIL.value())
					.detail(ResponseMessage.DetailCode.FILE.REQ_PARAM.value())
					.build();
		}
		
		List<ErrorMessage> errors = new ArrayList<>();
		for (Integer fileKey : delList) {
			updateFileOneToDelete(fileKey);
		}
		return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
				.message(ResponseMessage.MessageCode.FILE.DELETE_SUCCESS.value())
				.error(errors)
				.build();
	}
	
	/**
	 * 파일을 로직상 삭제 처리.
	 * @param fileVO : 삭제할 파일 정보
	 * @return
	 */
	public boolean updateFileOneToDelete(Integer fileId) {
		try {
			Integer result = fileMapper.updateFileOneToDelete(fileId);
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
			Integer result = fileMapper.updateFileStatusToRemove(fileId);
			return (result == 0)?false:true;
		}catch(Exception e){
			return false;
		}
	}
	
	/**
	 * 해당 원본파일의 모든 판서본 삭제. 변환 진행중인 판서본의 경우 변환을 취소하고 프린트 폴더 삭제
	 * @param originalKey : 원본파일키
	 */
	private void deleteAllCopy(Integer originalKey) {
		MeetingFileInfoVO original = getFileOne(originalKey);
		String ROOT_PATH = getMeetingRootPath(original.getMeetingKey());
		List<MeetingFileInfoVO> copyList = getMeetingFileList(MeetingFileInfoVO.builder()
				.originalKey(originalKey)
				.roleType(FileRole.COPY)
				.build());
		if(copyList != null && copyList.size() > 0) {
			for(MeetingFileInfoVO copyVO : copyList) {
				deleteCopy(ROOT_PATH, copyVO);
			}
		}
	}
	/**
	 * 해당 원본파일의 소유주의 모든 판서본 삭제. 변환 진행중인 판서본의 경우 변환을 취소하고 프린트 폴더 삭제
	 * @param originalKey : 원본파일키
	 * @param empKey : 판서본 소유자키
	 */
	private void deleteAllCopy(Integer originalKey, String empKey) {
		MeetingFileInfoVO original = getFileOne(originalKey);
		String ROOT_PATH = getMeetingRootPath(original.getMeetingKey());
		List<MeetingFileInfoVO> copyList = getMeetingFileList(MeetingFileInfoVO.builder()
				.originalKey(originalKey)
				.empKey(empKey)
				.roleType(FileRole.COPY)
				.build());
		if(copyList != null && copyList.size() > 0) {
			for(MeetingFileInfoVO copyVO : copyList) {
				deleteCopy(ROOT_PATH, copyVO);
			}
		}
	}
	
	private void deleteCopy(String ROOT_PATH, MeetingFileInfoVO copyVO) {
		fileMapper.deleteMeetingFile(copyVO.getFileKey());
		fileRepo.removeQueue(copyVO.getFileKey());
		String PRINTER_FOLDER_PATH = MeetingFilePathUtil.getPrinterPath(ROOT_PATH, copyVO.getUuid());
		diskServ.removeFolder(PRINTER_FOLDER_PATH);
	}
	
	private void deleteMemo(String ROOT_PATH, MeetingFileInfoVO memoVO) {
		fileMapper.deleteMeetingFile(memoVO.getFileKey());
		fileRepo.removeQueue(memoVO.getFileKey());
		String PRINTER_FOLDER_PATH = MeetingFilePathUtil.getPrinterPath(ROOT_PATH, memoVO.getUuid());
		diskServ.removeFolder(PRINTER_FOLDER_PATH);
	}
	
	/**
	 * 회의 완전 종료후 모든 책자 삭제
	 * @param meeting
	 */
	public boolean deleteBookletFolderOnFinishedMeeting(Integer meetingKey) {
		List<MeetingFileConvertVO> cvtList = cvtServ.getCvtListToProcess(MeetingFileConvertVO.builder().meetingKey(meetingKey).build());
		if(cvtList.size() != 0) {
			//처리중인 변환 요청이 남은 경우 판서본 삭제 안함
			return false;
		}
		return diskServ.removeBookletRootFolder(meetingKey);
	}
	
	/**
	 * 파일 정보 객체 요청
	 * @param fileKey
	 * @return
	 */
	public MeetingFileInfoVO getFileOne(Integer fileKey) {
		return fileMapper.getMeetingFileOne(fileKey);
	}
	
	/**
	 * 회의 메모 정보 요청
	 * @param meetingKey: 메모를 조회할 회의키
	 * @param userKey: 메모 소유한 유저키
	 * @return
	 */
	public MeetingFileInfoVO getMemoFileOne(Integer meetingKey, String userKey) {
		return fileMapper.getMemoFileOne(MeetingFileInfoVO.builder().meetingKey(meetingKey).empKey(userKey).build());
	}
	
	
	/**
	 * 회의의 모든 파일 목록
	 * @param meetingKey
	 * @return
	 */
	public List<MeetingFileInfoVO> getMeetingFileList(MeetingFileInfoVO param) {
		return fileMapper.getMeetingFileList(param);
	}
	
	public List<MeetingFileInfoVO> getMeetingSharedFileList(Integer meetingKey) {
		return fileMapper.getMeetingSharedFileList(MeetingFileInfoVO.builder().meetingKey(meetingKey).build());
	}
	
	public List<MeetingFileInfoVO> getMeetingPrivateFileList(Integer meetingKey, String userKey) {
		return fileMapper.getMeetingPrivateFileList(MeetingFileInfoVO.builder().meetingKey(meetingKey).empKey(userKey).build());
	}
}
