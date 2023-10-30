package egov.framework.plms.sub.ewp.bean.mvc.service.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.core.model.enums.file.FileCategory;
import egov.framework.plms.main.core.util.FileNameUtil;
import egov.framework.plms.sub.ewp.bean.mvc.entity.file.MeetingFileInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingInfoService;
import egov.framework.plms.sub.ewp.core.model.enums.FileRole;
import egov.framework.plms.sub.ewp.core.util.MeetingFilePathUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile("ewp")
@RequiredArgsConstructor
public class EwpMeetingFileLinkService {
	private final EwpMeetingInfoService mtServ;

	/**
	 * 요청 파일 다운로드. 현재 안드로이드 앱에서는 파일 다운로드시 실행되는 Android Download Manager(서드파티 앱)가 서버에 신뢰성있는 SSL연결을 하지 못하기 때문에 HTTP 요청을 통해서만 다운로드 가능.
	 * @param fileVO : 다운로드 요청한 파일 정보
	 * @throws Exception 
	 */
	public ResponseEntity<Resource> fileDownload(HttpServletRequest request, MeetingFileInfoVO fileVO) {
		String ROOT_PATH = getMeetingRootPath(fileVO.getMeetingKey());
		String ORIGINAL_FILE_NAME = "";
		String FILE_NAME = "";
		File FILE_PATH = null;
		String MIME_STR = "";
		if(fileVO.getRoleType() == FileRole.MATERIAL) {
			if(fileVO.getMimeType() == FileCategory.IMG) {
				ORIGINAL_FILE_NAME = fileVO.getOriginalName();
				FILE_NAME = FileNameUtil.getFileNameFormat(fileVO);
				FILE_PATH = new File(MeetingFilePathUtil.getMaterialFilePath(ROOT_PATH, FILE_NAME));
				if(FILE_PATH == null || !FILE_PATH.exists()) {
					ORIGINAL_FILE_NAME = FilenameUtils.removeExtension(ORIGINAL_FILE_NAME)+".webp";
					FILE_NAME = FilenameUtils.removeExtension(FILE_NAME)+".webp";
					FILE_PATH = new File(MeetingFilePathUtil.getMaterialFilePath(ROOT_PATH, FILE_NAME));
				}
			}else {
				ORIGINAL_FILE_NAME = fileVO.getOriginalName();
				FILE_NAME = FilenameUtils.getBaseName(FileNameUtil.getFileNameFormat(fileVO)) + ".pdf";
				FILE_PATH = new File(MeetingFilePathUtil.getViewFilePath(ROOT_PATH, FILE_NAME));
			}
			switch(fileVO.getMimeType()) {
				case PDF:
					MIME_STR = "application/pdf";
					break;
				case HWP:
					MIME_STR = "application/x-hwp";
					break;
				case IMG:
					MIME_STR = "image/png";
					break;
				case EXCEL:
					MIME_STR = "application/vnd.ms-excel";
					break;
				case PPT:
					MIME_STR = "application/vnd.ms-powerpoint";
					break;
				case WORD:
					MIME_STR = "application/msword";
					break;
			}
		}else if(fileVO.getRoleType() == FileRole.REPORT) {
			ORIGINAL_FILE_NAME = fileVO.getOriginalName();
			FILE_NAME = FileNameUtil.getFileNameFormat(fileVO);
			FILE_PATH = new File(MeetingFilePathUtil.getReportFilePath(ROOT_PATH, FILE_NAME));
			MIME_STR = "application/pdf";
		}else if(fileVO.getRoleType() == FileRole.COPY) {
			ORIGINAL_FILE_NAME = fileVO.getOriginalName();
			FILE_NAME = FileNameUtil.getCopyNameFormat(fileVO, fileVO.getEmpKey());
			FILE_PATH = new File(MeetingFilePathUtil.getCopyFilePath(ROOT_PATH, FILE_NAME));
			MIME_STR = "application/pdf";
		}else if(fileVO.getRoleType() == FileRole.MEMO) {
			ORIGINAL_FILE_NAME = fileVO.getOriginalName();
			FILE_NAME = FileNameUtil.getCopyNameFormat(fileVO, fileVO.getEmpKey());
			FILE_PATH = new File(MeetingFilePathUtil.getMemoFilePath(ROOT_PATH, FILE_NAME));
			MIME_STR = "application/pdf";
		}else if(fileVO.getRoleType() == FileRole.PHOTO) {
			ORIGINAL_FILE_NAME = fileVO.getOriginalName();
			FILE_NAME = FileNameUtil.getFileNameFormat(fileVO);
			FILE_PATH = new File(MeetingFilePathUtil.getPhotoFilePath(ROOT_PATH, FILE_NAME));
			MIME_STR = "image/png";
		}else if(fileVO.getRoleType() == FileRole.VOICE) {
			ORIGINAL_FILE_NAME = fileVO.getOriginalName();
			FILE_NAME = FileNameUtil.getFileNameFormat(fileVO);
			FILE_PATH = new File(MeetingFilePathUtil.getVoiceFilePath(ROOT_PATH, FILE_NAME));
			MIME_STR = "audio/mp3";
		}
		try {
			Path filePath = FILE_PATH.toPath();
			InputStreamResource resource = new InputStreamResource(Files.newInputStream(filePath));
			HttpHeaders headers = new HttpHeaders();
			String encodedFileName = getBrowserEncodedFileName(request, ORIGINAL_FILE_NAME);
			headers.setContentType(MediaType.parseMediaType(MIME_STR));
			headers.setContentDisposition(ContentDisposition.builder("attachment").filename(encodedFileName).build());
			headers.setContentLength(FILE_PATH.length());
			/*
			headers.add("Access-Control-Allow-Origin", "*");
			headers.add("Access-Control-Allow-Methods", "GET, POST, PUT");
			headers.add("Access-Control-Allow-Headers", "Content-Type");
			
			headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
			headers.add("Set-Cookie", "fileDownload=true; path='/");
			headers.add("Pragma", "no-cache");
			headers.add("Expires", "0");
			*/
			return new ResponseEntity<>(resource, headers, HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<>(null, null, HttpStatus.CONFLICT);
		}
	}
	
	private String getBrowserEncodedFileName(HttpServletRequest request, String fileName) {
		String encodedFileName = "";
		boolean isMSIE = request.getHeader("user-agent").indexOf("MSIE") != -1
				|| request.getHeader("user-agent").indexOf("Trident") != -1;
		try {
			if (isMSIE) {
					encodedFileName = URLEncoder.encode(fileName, "utf-8");
				encodedFileName = encodedFileName.replaceAll("\\+", "%20");
			} else {
				encodedFileName = new String(fileName.getBytes("utf-8"), "ISO-8859-1");
			}
		} catch (UnsupportedEncodingException e) {
			encodedFileName = fileName;
		}
		return encodedFileName;
	}
	
	/**
	 * 요청 파일 조회
	 * @param fileKey
	 * @return
	 * @throws IOException
	 */
	public ResponseEntity<Resource> fileView(MeetingFileInfoVO fileVO) throws IOException {	
		String ROOT_PATH = getMeetingRootPath(fileVO.getMeetingKey());
		String FILE_NAME = "";
		File FILE_PATH = null;
		if(fileVO.getRoleType() == FileRole.MATERIAL) {
			if(fileVO.getMimeType() == FileCategory.IMG) {
				FILE_NAME = FileNameUtil.getFileNameFormat(fileVO);
				FILE_PATH = new File(MeetingFilePathUtil.getMaterialFilePath(ROOT_PATH, FILE_NAME));
				if(FILE_PATH == null || !FILE_PATH.exists()) {
					FILE_NAME = FilenameUtils.removeExtension(FILE_NAME)+".webp";
					FILE_PATH = new File(MeetingFilePathUtil.getMaterialFilePath(ROOT_PATH, FILE_NAME));
				}
			}else {
				FILE_NAME = FilenameUtils.getBaseName(FileNameUtil.getFileNameFormat(fileVO)) + ".pdf";
				FILE_PATH = new File(MeetingFilePathUtil.getViewFilePath(ROOT_PATH, FILE_NAME));
			}
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
		
		Path path = FILE_PATH.toPath();
		
		HttpHeaders headers = new HttpHeaders();
		if(fileVO.getMimeType() == FileCategory.IMG) {
			headers.add(HttpHeaders.CONTENT_TYPE, "image/png");
		}else if(fileVO.getMimeType() == FileCategory.AUDIO) {
			headers.add(HttpHeaders.CONTENT_TYPE, "audio/mpeg");
		}else {
			headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
		}
		
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline");

		Resource resource = new InputStreamResource(Files.newInputStream(path));
		return new ResponseEntity<>(resource, headers, HttpStatus.OK);
	}
	
	@Cacheable(value = "RootPath", key = "#meetingKey", unless="#result == null", condition="#meetingKey!=null")
	private String getMeetingRootPath(Integer meetingKey) {
		Optional<EwpMeetingInfoVO> meetingInfoOpt = mtServ.getMeetingInfoOne(meetingKey);
		if(!meetingInfoOpt.isPresent()) {
			return null;
		}
		return MeetingFilePathUtil.getRootPath(meetingInfoOpt.get());
	}
}
