package egov.framework.plms.sub.ewp.bean.mvc.controller.meeting;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import egov.framework.plms.main.bean.component.auth.PassTokenProvider;
import egov.framework.plms.main.bean.component.monitoring.MeetingMonitoringManager;
import egov.framework.plms.main.bean.component.properties.FileConfigProperties;
import egov.framework.plms.main.core.exception.ApiDataAccessDeniedException;
import egov.framework.plms.main.core.exception.ApiNotFoundException;
import egov.framework.plms.main.core.model.auth.ResourceAuthorityCollection;
import egov.framework.plms.main.core.model.enums.auth.MeetingAuth;
import egov.framework.plms.main.core.model.enums.error.ErrorCode;
import egov.framework.plms.sub.ewp.bean.component.auth.EwpResourceAuthorityProvider;
import egov.framework.plms.sub.ewp.bean.mvc.entity.file.MeetingFileInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.service.file.EwpMeetingFileInfoService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingInfoService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingNoteService;
import egov.framework.plms.sub.ewp.core.model.enums.FileRole;
import egov.framework.plms.sub.ewp.core.util.EwpSecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * 노트,메모장 판서를 위한 요청을 처리할 컨트롤러
 * @author mckim
 * @version 1.0
 * @since 2023. 5. 10
 */
@Slf4j
@RestController
@RequestMapping("/api/ewp")
@RequiredArgsConstructor
@Profile("ewp")
public class EwpMeetingNoteRestController {
	private final EwpMeetingNoteService noteServ;
	private final EwpMeetingFileInfoService fileServ;
	private final EwpResourceAuthorityProvider authorityProvider;
	private final MeetingMonitoringManager monitoringMng;
	
	/**
	 * 회의자료의 페이지 정보 요청
	 * @param meetingKey
	 * @param fileKey
	 * @return
	 */
	@GetMapping(value = "/meeting/{meetingKey}/note/{fileKey}/length")
	public Integer getNotePageLength(HttpSession session, @PathVariable Integer meetingKey, @PathVariable Integer fileKey){
		MeetingFileInfoVO fileVO = fileServ.getFileOne(fileKey);
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingKey);
		if(authorityCollection.hasAuthority(MeetingAuth.ATTEND)) {
			if(fileVO == null || !fileVO.getMeetingKey().equals(meetingKey)) {
				throw new ApiNotFoundException(ErrorCode.FILE.NOT_FOUND);
			}
			return fileVO.getPage();
		}else {
			throw new ApiDataAccessDeniedException(ErrorCode.FILE.READ_NO_AUTHORITY);
		}		
	}
	
	/**
	 * 회의자료 페이지 원본 이미지 요청
	 * @param session
	 * @param meetingKey
	 * @param fileKey
	 * @param pageNo
	 * @return
	 * @throws IOException
	 */
	@GetMapping(value = "/meeting/{meetingKey}/note/{fileKey}/page/{pageNo}/image/source")
	public ResponseEntity<Resource> viewNotePageSourceImage(HttpSession session, @PathVariable Integer meetingKey, @PathVariable Integer fileKey, @PathVariable Integer pageNo) throws IOException {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingKey);
		if(authorityCollection.hasAuthority(MeetingAuth.ATTEND)) {
			MeetingFileInfoVO fileVO = fileServ.getFileOne(fileKey);
			if(fileVO == null || !fileVO.getMeetingKey().equals(meetingKey) || fileVO.getRoleType() != FileRole.MATERIAL) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			return noteServ.showNotePage(fileVO, pageNo);
		}else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	/**
	 * 회의자료 페이지 판서 이미지 요청
	 * @param session
	 * @param meetingKey
	 * @param fileKey
	 * @param pageNo
	 * @return
	 * @throws IOException
	 */
	@GetMapping(value = "/meeting/{meetingKey}/note/{fileKey}/page/{pageNo}/image/draw")
	public ResponseEntity<Resource> viewNotePageDrawImage(HttpSession session, @PathVariable Integer meetingKey, @PathVariable Integer fileKey, @PathVariable Integer pageNo) throws IOException {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingKey);
		if(authorityCollection.hasAuthority(MeetingAuth.ATTEND)) {
			MeetingFileInfoVO fileVO = fileServ.getFileOne(fileKey);
			if(fileVO == null || !fileVO.getMeetingKey().equals(meetingKey) || fileVO.getRoleType() != FileRole.MATERIAL) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			return noteServ.showNotePage(fileVO, EwpSecurityUtil.getLoginId(), pageNo);
		}else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	/**
	 * 회의자료 페이지 판서 저장
	 * @param session
	 * @param meetingKey
	 * @param fileKey
	 * @param pageNo
	 * @param image
	 * @return
	 * @throws IOException
	 */
	@PutMapping(value = "/meeting/{meetingKey}/note/{fileKey}/page/{pageNo}/image/draw")
	public boolean putNotePageDrawImage(HttpSession session, @PathVariable Integer meetingKey, @PathVariable Integer fileKey, @PathVariable Integer pageNo, @RequestBody @Nullable String image) throws IOException {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingKey);
		if(authorityCollection.hasAuthority(MeetingAuth.ATTEND) || monitoringMng.getEntriedMeetingId(session.getId()) == meetingKey) {
			MeetingFileInfoVO fileVO = fileServ.getFileOne(fileKey);
			if(fileVO == null || !fileVO.getMeetingKey().equals(meetingKey) || fileVO.getRoleType() != FileRole.MATERIAL) {
				throw new ApiNotFoundException(ErrorCode.FILE.NOT_FOUND);
			}
			return noteServ.drawNotePage(fileVO, EwpSecurityUtil.getLoginId(), pageNo, image);
		}else {
			throw new ApiDataAccessDeniedException(ErrorCode.FILE.READ_NO_AUTHORITY);
		}
	}
	
	/**
	 * 메모장 페이지 정보 요청
	 * @param session
	 * @param meetingKey
	 * @param fileKey
	 * @return
	 */
	@GetMapping(value = "/meeting/{meetingKey}/memo/length")
	public Integer getMemoPageLength(HttpSession session, @PathVariable Integer meetingKey){
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingKey);
		if(authorityCollection.hasAuthority(MeetingAuth.ATTEND)) {
			return noteServ.getMemoPageLength(meetingKey, EwpSecurityUtil.getLoginId());
		}else {
			throw new ApiDataAccessDeniedException(ErrorCode.FILE.READ_NO_AUTHORITY);
		}		
	}
	
	/**
	 * 메모장 페이지 원본 이미지 요청
	 * @param session
	 * @param meetingKey
	 * @param fileKey
	 * @param pageNo
	 * @return
	 * @throws IOException
	 */
	@GetMapping(value = "/meeting/{meetingKey}/memo/page/image/source")
	public ResponseEntity<Resource> viewMemoPageSourceImage(HttpSession session, @PathVariable Integer meetingKey) throws IOException {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingKey);
		if(authorityCollection.hasAuthority(MeetingAuth.ATTEND)) {
			return noteServ.showMemoPage();
		}else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	/**
	 * 메모장 페이지 이미지 요청
	 * @param session
	 * @param meetingKey
	 * @param fileKey
	 * @param pageNo
	 * @return
	 * @throws IOException
	 */
	@GetMapping(value = "/meeting/{meetingKey}/memo/page/{pageNo}/image/draw")
	public ResponseEntity<Resource> viewMemoPageDrawImage(HttpSession session, @PathVariable Integer meetingKey, @PathVariable Integer pageNo) throws IOException {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingKey);
		if(authorityCollection.hasAuthority(MeetingAuth.ATTEND)) {
			return noteServ.showMemoPage(meetingKey, EwpSecurityUtil.getLoginId(), pageNo);
		}else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	/**
	 * 메모장 페이지 추가
	 * @param session
	 * @param meetingKey
	 * @param fileKey
	 * @param pageNo
	 * @param image
	 * @return
	 * @throws IOException
	 */
	@PostMapping(value = "/meeting/{meetingKey}/memo/page/{pageNo}")
	public boolean postMemoPage(HttpSession session, @PathVariable Integer meetingKey, @PathVariable Integer pageNo) throws IOException {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingKey);
		if(authorityCollection.hasAuthority(MeetingAuth.ATTEND)) {
			return noteServ.addMemoPage(meetingKey, EwpSecurityUtil.getLoginId(), pageNo);
		}else {
			throw new ApiDataAccessDeniedException(ErrorCode.FILE.READ_NO_AUTHORITY);
		}
	}
	
	/**
	 * 메모장 페이지 판서 저장
	 * @param session
	 * @param meetingKey
	 * @param fileKey
	 * @param pageNo
	 * @param image
	 * @return
	 * @throws IOException
	 */
	@PutMapping(value = "/meeting/{meetingKey}/memo/page/{pageNo}/image/draw")
	public boolean putMemoPageDrawImage(HttpSession session, @PathVariable Integer meetingKey, @PathVariable Integer pageNo, @RequestBody @Nullable String image) throws IOException {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingKey);
		if(authorityCollection.hasAuthority(MeetingAuth.ATTEND) || monitoringMng.getEntriedMeetingId(session.getId()) == meetingKey) {
			return noteServ.drawMemoPage(meetingKey, EwpSecurityUtil.getLoginId(), pageNo, image);
		}else {
			throw new ApiDataAccessDeniedException(ErrorCode.FILE.READ_NO_AUTHORITY);
		}
	}
	
	/**
	 * 메모장 페이지 삭제
	 * @param session
	 * @param meetingKey
	 * @param fileKey
	 * @param pageNo
	 * @param image
	 * @return
	 * @throws IOException
	 */
	@DeleteMapping(value = "/meeting/{meetingKey}/memo/page/{pageNo}")
	public boolean deleteMemoPage(HttpSession session, @PathVariable Integer meetingKey, @PathVariable Integer pageNo) throws IOException {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingKey);
		if(authorityCollection.hasAuthority(MeetingAuth.ATTEND)) {
			return noteServ.deleteMemoPage(meetingKey, EwpSecurityUtil.getLoginId(), pageNo);
		}else {
			throw new ApiDataAccessDeniedException(ErrorCode.FILE.READ_NO_AUTHORITY);
		}
	}
}
