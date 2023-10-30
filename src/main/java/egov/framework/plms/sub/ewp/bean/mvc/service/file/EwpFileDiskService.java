package egov.framework.plms.sub.ewp.bean.mvc.service.file;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import egov.framework.plms.main.bean.component.properties.DrmConfigProperties;
import egov.framework.plms.main.bean.mvc.service.file.abst.FileDiskAbstractService;
import egov.framework.plms.main.core.model.enums.file.FileCategory;
import egov.framework.plms.main.core.util.DrmUtil;
import egov.framework.plms.main.core.util.FileCommUtil;
import egov.framework.plms.main.core.util.FileNameUtil;
import egov.framework.plms.sub.ewp.bean.mvc.entity.file.MeetingFileInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingInfoService;
import egov.framework.plms.sub.ewp.core.model.enums.FileRole;
import egov.framework.plms.sub.ewp.core.util.MeetingFilePathUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("EwpFileDiskService")
@Profile("ewp")
@Primary
public class EwpFileDiskService extends FileDiskAbstractService{
	@Autowired
	private EwpMeetingInfoService mtServ;
	public EwpFileDiskService(@Autowired DrmConfigProperties drmConfig) {
		super(drmConfig);
	}
	
	/**
	 * 파일 디스크에 저장
	 * @param fileVO : 저장할 파일 정보
	 * @param file : 저장할 파일
	 * @return
	 */
	public boolean writeFile(MeetingFileInfoVO fileVO, MultipartFile file) {
		if(!(file.getSize() > 0)) {
			return false;
		}
		Optional<EwpMeetingInfoVO> meetingInfoOpt = mtServ.getMeetingInfoOne(fileVO.getMeetingKey());
		if(!meetingInfoOpt.isPresent()) {
			return false;
		}
		String ROOT_PATH = MeetingFilePathUtil.getRootPath(meetingInfoOpt.get());
		String SAVE_PATH = "";
		if(fileVO.getRoleType() == FileRole.MATERIAL) {
			SAVE_PATH = MeetingFilePathUtil.getMaterialRootPath(ROOT_PATH);
		}else if(fileVO.getRoleType() == FileRole.VOICE) {
			SAVE_PATH = MeetingFilePathUtil.getVoiceRootPath(ROOT_PATH);
		}else if(fileVO.getRoleType() == FileRole.PHOTO) {
			SAVE_PATH = MeetingFilePathUtil.getPhotoRootPath(ROOT_PATH);
		}else if(fileVO.getRoleType() == FileRole.REPORT) {
			SAVE_PATH = MeetingFilePathUtil.getReportRootPath(ROOT_PATH);
		}else if(fileVO.getRoleType() == FileRole.COPY) {
			SAVE_PATH = MeetingFilePathUtil.getCopyRootPath(ROOT_PATH);
		}else if(fileVO.getRoleType() == FileRole.MEMO) {
			SAVE_PATH = MeetingFilePathUtil.getMemoRootPath(ROOT_PATH);
		}else {
			return false;
		}
		String SAVE_NAME = FileNameUtil.getFileNameFormat(fileVO);
		
		return writeFile(SAVE_PATH, SAVE_NAME, file);
	}
	
	
	/**
	 * 해당 회의의 파일 보관 폴더 삭제
	 * @param meetingKey : 삭제할 폴더가 속한 회의 고유키
	 * @return
	 */
	public boolean removeMeetingFolder(Integer meetingKey) {
		Optional<EwpMeetingInfoVO> meetingInfoOpt = mtServ.getMeetingInfoOne(meetingKey);
		if(meetingInfoOpt.isPresent()) {
			String ROOT_PATH = MeetingFilePathUtil.getRootPath(meetingInfoOpt.get());
			return removeFolder(ROOT_PATH);
		}else {
			return false;
		}
	}
	
	/**
	 * 파일 책자 삭제
	 * @param orgFile
	 * @return
	 */
	public boolean removeBookletFolder(MeetingFileInfoVO orgFile) {
		String ROOT_PATH = orgFile.getRootPath();
		String BOOKLET_FOLDER_PATH = MeetingFilePathUtil.getBookletSourceFolderPath(ROOT_PATH, orgFile.getUuid());
		return removeFolder(BOOKLET_FOLDER_PATH);
	}
	
	/**
	 * 개인 책자 삭제
	 * @param copyFile : 삭제할 책자의 판서본 파일
	 * @return
	 */
	public boolean removeUserBookletFolder(MeetingFileInfoVO copyFile) {
		Optional<EwpMeetingInfoVO> meetingInfoOpt = mtServ.getMeetingInfoOne(copyFile.getMeetingKey());
		if(meetingInfoOpt.isPresent()) {
			String ROOT_PATH = MeetingFilePathUtil.getRootPath(meetingInfoOpt.get());
			String BOOKLET_FOLDER_PATH = MeetingFilePathUtil.getBookletUserPath(ROOT_PATH, FilenameUtils.getBaseName(copyFile.getOriginalName()), copyFile.getEmpKey());
			return removeFolder(BOOKLET_FOLDER_PATH);
		}else {
			return false;
		}
		
	}
	
	/**
	 * 모든 책자 삭제
	 * @param copyFile : 삭제할 책자의 판서본 파일
	 * @return
	 */
	public boolean removeBookletRootFolder(Integer meetingKey) {
		Optional<EwpMeetingInfoVO> meetingInfoOpt = mtServ.getMeetingInfoOne(meetingKey);
		if(meetingInfoOpt.isPresent()) {
			String ROOT_PATH = MeetingFilePathUtil.getRootPath(meetingInfoOpt.get());
			String BOOKLET_ROOT_PATH = MeetingFilePathUtil.getBookletRootPath(ROOT_PATH);
			return removeFolder(BOOKLET_ROOT_PATH);
		}else {
			return false;
		}
	}

	public boolean removeViewFile(MeetingFileInfoVO orgFile) {
		String ROOT_PATH = orgFile.getRootPath();
		String VIEW_PATH = MeetingFilePathUtil.getViewFilePath(ROOT_PATH, FileNameUtil.getViewNameFormat(orgFile));
		return removeFile(VIEW_PATH);
	}
	
	/**
	 * 해당 파일 디스크에서 삭제
	 * @param fileVO : 삭제할 파일 정보
	 * @return
	 */
	public boolean removeFile(MeetingFileInfoVO fileVO) {
		Optional<EwpMeetingInfoVO> meetingInfoOpt = mtServ.getMeetingInfoOne(fileVO.getMeetingKey());
		if(!meetingInfoOpt.isPresent()) {
			return false;
		}
		String ROOT_PATH = MeetingFilePathUtil.getRootPath(meetingInfoOpt.get());
		String FILE_NAME = "";
		File FILE_PATH = null;
		File VIEW_PATH = null;
		if(fileVO.getRoleType() == FileRole.MATERIAL) {
			FILE_NAME = FileNameUtil.getFileNameFormat(fileVO);
			FILE_PATH = new File(MeetingFilePathUtil.getMaterialFilePath(ROOT_PATH, FILE_NAME));
			VIEW_PATH =  new File(MeetingFilePathUtil.getViewFilePath(ROOT_PATH, FileNameUtil.getPdfNameFormat(FILE_NAME)));
		}else if(fileVO.getRoleType() == FileRole.REPORT) {
			FILE_NAME = FileNameUtil.getFileNameFormat(fileVO);
			FILE_PATH = new File(MeetingFilePathUtil.getReportFilePath(ROOT_PATH, FILE_NAME));
		}else if(fileVO.getRoleType() == FileRole.COPY) {
			FILE_NAME = FileNameUtil.getCopyNameFormat(fileVO, fileVO.getEmpKey());
			FILE_PATH = new File(MeetingFilePathUtil.getCopyFilePath(ROOT_PATH, FILE_NAME));
		}else if(fileVO.getRoleType() == FileRole.MEMO) {
			FILE_NAME = FileNameUtil.getCopyNameFormat(fileVO, fileVO.getEmpKey());
			FILE_PATH = new File(MeetingFilePathUtil.getMemoFilePath(ROOT_PATH, FILE_NAME));
		}else if(fileVO.getRoleType() == FileRole.PHOTO) {
			FILE_NAME = FileNameUtil.getFileNameFormat(fileVO);
			FILE_PATH = new File(MeetingFilePathUtil.getPhotoFilePath(ROOT_PATH, FILE_NAME));
		}else if(fileVO.getRoleType() == FileRole.VOICE) {
			FILE_NAME = FileNameUtil.getFileNameFormat(fileVO);
			FILE_PATH = new File(MeetingFilePathUtil.getVoiceFilePath(ROOT_PATH, FILE_NAME));
		}
		log.info("Remove File - FILE_PATH: {}, FILE_NAME: {}", FILE_PATH, FILE_NAME);
		if (FILE_PATH.exists() && !FILE_PATH.isDirectory()) {
			FILE_PATH.delete();
			log.info("Remove File");
		}else if(fileVO.getMimeType() == FileCategory.IMG) {
			FILE_NAME = FilenameUtils.removeExtension(FILE_NAME)+".webp";
			FILE_PATH = new File(MeetingFilePathUtil.getMaterialFilePath(ROOT_PATH, FILE_NAME));
			FILE_PATH.delete();
		}
		if (VIEW_PATH != null && VIEW_PATH.exists() && !VIEW_PATH.isDirectory()) {
			VIEW_PATH.delete();
			log.info("Remove View File - VIEW_PATH: {}", VIEW_PATH);
		}
		String BOOKLET_ROOT_FOLDER = MeetingFilePathUtil.getBookletFolderPath(ROOT_PATH, FilenameUtils.getBaseName(FILE_NAME));
		if(BOOKLET_ROOT_FOLDER != null) {
			log.info("Remove View Booklet - BOOKLET_ROOT_FOLDER: {}", BOOKLET_ROOT_FOLDER);
			removeFolder(BOOKLET_ROOT_FOLDER);
		}
		return true;
	}
}
