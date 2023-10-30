package egov.framework.plms.sub.lime.bean.mvc.controller.meeting;

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

import egov.framework.plms.main.bean.component.monitoring.MeetingMonitoringManager;
import egov.framework.plms.main.bean.mvc.entity.file.FileDetailVO;
import egov.framework.plms.main.core.exception.ApiDataAccessDeniedException;
import egov.framework.plms.main.core.exception.ApiNotFoundException;
import egov.framework.plms.main.core.model.auth.ResourceAuthorityCollection;
import egov.framework.plms.main.core.model.enums.auth.MeetingAuth;
import egov.framework.plms.main.core.model.enums.error.ErrorCode;
import egov.framework.plms.main.core.model.enums.file.RelationType;
import egov.framework.plms.sub.ewp.bean.mvc.entity.file.MeetingFileInfoVO;
import egov.framework.plms.sub.ewp.core.model.enums.FileRole;
import egov.framework.plms.sub.lime.bean.component.auth.LimeResourceAuthorityProvider;
import egov.framework.plms.sub.lime.bean.mvc.service.meeting.LimeMeetingFileInfoService;
import egov.framework.plms.sub.lime.bean.mvc.service.meeting.LimeMeetingNoteService;
import egov.framework.plms.sub.lime.core.util.LimeSecurityUtil;
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
@RequestMapping("/api/lime")
@RequiredArgsConstructor
@Profile("lime")
public class LimeMeetingNoteRestController {
	private final LimeMeetingNoteService noteServ;
	private final LimeMeetingFileInfoService fileServ;
	private final LimeResourceAuthorityProvider authorityProvider;
	private final MeetingMonitoringManager monitoringMng;
	
	/**
	 * 회의자료의 페이지 정보 요청
	 * @param meetingId
	 * @param fileId
	 * @return
	 */
	@GetMapping(value = "/meeting/{meetingId}/note/{fileId}/length")
	public Integer getNotePageLength(HttpSession session, @PathVariable Integer meetingId, @PathVariable Integer fileId){
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingId);
		if(authorityCollection.hasAuthority(MeetingAuth.ATTEND)) {
			FileDetailVO fileVO = fileServ.selectFileOne(fileId).orElse(null);
			if(fileVO == null || !fileVO.getRelatedEntityId().equals(meetingId) || fileVO.getRelationType() != RelationType.MEETING_MATERIAL) {
				throw new ApiNotFoundException(ErrorCode.FILE.NOT_FOUND);
			}
			return fileVO.getPageCount();
		}else {
			throw new ApiDataAccessDeniedException(ErrorCode.FILE.READ_NO_AUTHORITY);
		}		
	}
	
	/**
	 * 회의자료 페이지 원본 이미지 요청
	 * @param session
	 * @param meetingId
	 * @param fileId
	 * @param pageNo
	 * @return
	 * @throws IOException
	 */
	@GetMapping(value = "/meeting/{meetingId}/note/{fileId}/page/{pageNo}/image/source")
	public ResponseEntity<Resource> viewNotePageSourceImage(HttpSession session, @PathVariable Integer meetingId, @PathVariable Integer fileId, @PathVariable Integer pageNo) throws IOException {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingId);
		if(authorityCollection.hasAuthority(MeetingAuth.ATTEND)) {
			FileDetailVO fileVO = fileServ.selectFileOne(fileId).orElse(null);
			if(fileVO == null || !fileVO.getRelatedEntityId().equals(meetingId) || fileVO.getRelationType() != RelationType.MEETING_MATERIAL) {
				throw new ApiNotFoundException(ErrorCode.FILE.NOT_FOUND);
			}
			return noteServ.showNotePage(fileVO, pageNo);
		}else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	/**
	 * 회의자료 페이지 판서 이미지 요청
	 * @param session
	 * @param meetingId
	 * @param fileId
	 * @param pageNo
	 * @return
	 * @throws IOException
	 */
	@GetMapping(value = "/meeting/{meetingId}/note/{fileId}/page/{pageNo}/image/draw")
	public ResponseEntity<Resource> viewNotePageDrawImage(HttpSession session, @PathVariable Integer meetingId, @PathVariable Integer fileId, @PathVariable Integer pageNo) throws IOException {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingId);
		if(authorityCollection.hasAuthority(MeetingAuth.ATTEND)) {
			FileDetailVO fileVO = fileServ.selectFileOne(fileId).orElse(null);
			if(fileVO == null || !fileVO.getRelatedEntityId().equals(meetingId) || fileVO.getRelationType() != RelationType.MEETING_MATERIAL) {
				throw new ApiNotFoundException(ErrorCode.FILE.NOT_FOUND);
			}
			return noteServ.showNotePage(fileVO, LimeSecurityUtil.getLoginId(), pageNo);
		}else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	/**
	 * 회의자료 페이지 판서 저장
	 * @param session
	 * @param meetingId
	 * @param fileId
	 * @param pageNo
	 * @param image
	 * @return
	 * @throws IOException
	 */
	@PutMapping(value = "/meeting/{meetingId}/note/{fileId}/page/{pageNo}/image/draw")
	public boolean putNotePageDrawImage(HttpSession session, @PathVariable Integer meetingId, @PathVariable Integer fileId, @PathVariable Integer pageNo, @RequestBody @Nullable String image) throws IOException {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingId);
		if(authorityCollection.hasAuthority(MeetingAuth.ATTEND) || monitoringMng.getEntriedMeetingId(session.getId()) == meetingId) {
			FileDetailVO fileVO = fileServ.selectFileOne(fileId).orElse(null);
			if(fileVO == null || !fileVO.getRelatedEntityId().equals(meetingId) || fileVO.getRelationType() != RelationType.MEETING_MATERIAL) {
				throw new ApiNotFoundException(ErrorCode.FILE.NOT_FOUND);
			}
			return noteServ.drawNotePage(fileVO, LimeSecurityUtil.getLoginId(), pageNo, image);
		}else {
			throw new ApiDataAccessDeniedException(ErrorCode.FILE.READ_NO_AUTHORITY);
		}
	}
	
	/**
	 * 메모장 페이지 정보 요청
	 * @param session
	 * @param meetingId
	 * @param fileId
	 * @return
	 */
	@GetMapping(value = "/meeting/{meetingId}/memo/length")
	public Integer getMemoPageLength(HttpSession session, @PathVariable Integer meetingId){
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingId);
		if(authorityCollection.hasAuthority(MeetingAuth.ATTEND)) {
			return noteServ.getMemoPageLength(meetingId, LimeSecurityUtil.getLoginId());
		}else {
			throw new ApiDataAccessDeniedException(ErrorCode.FILE.READ_NO_AUTHORITY);
		}		
	}
	
	/**
	 * 메모장 페이지 원본 이미지 요청
	 * @param session
	 * @param meetingId
	 * @param fileId
	 * @param pageNo
	 * @return
	 * @throws IOException
	 */
	@GetMapping(value = "/meeting/{meetingId}/memo/page/image/source")
	public ResponseEntity<Resource> viewMemoPageSourceImage(HttpSession session, @PathVariable Integer meetingId) throws IOException {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingId);
		if(authorityCollection.hasAuthority(MeetingAuth.ATTEND)) {
			return noteServ.showMemoPage();
		}else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	/**
	 * 메모장 페이지 이미지 요청
	 * @param session
	 * @param meetingId
	 * @param fileId
	 * @param pageNo
	 * @return
	 * @throws IOException
	 */
	@GetMapping(value = "/meeting/{meetingId}/memo/page/{pageNo}/image/draw")
	public ResponseEntity<Resource> viewMemoPageDrawImage(HttpSession session, @PathVariable Integer meetingId, @PathVariable Integer pageNo) throws IOException {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingId);
		if(authorityCollection.hasAuthority(MeetingAuth.ATTEND)) {
			return noteServ.showMemoPage(meetingId, LimeSecurityUtil.getLoginId(), pageNo);
		}else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	/**
	 * 메모장 페이지 추가
	 * @param session
	 * @param meetingId
	 * @param fileId
	 * @param pageNo
	 * @param image
	 * @return
	 * @throws IOException
	 */
	@PostMapping(value = "/meeting/{meetingId}/memo/page/{pageNo}")
	public boolean postMemoPage(HttpSession session, @PathVariable Integer meetingId, @PathVariable Integer pageNo) throws IOException {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingId);
		if(authorityCollection.hasAuthority(MeetingAuth.ATTEND)) {
			return noteServ.addMemoPage(meetingId, LimeSecurityUtil.getLoginId(), pageNo);
		}else {
			throw new ApiDataAccessDeniedException(ErrorCode.FILE.READ_NO_AUTHORITY);
		}
	}
	
	/**
	 * 메모장 페이지 판서 저장
	 * @param session
	 * @param meetingId
	 * @param fileId
	 * @param pageNo
	 * @param image
	 * @return
	 * @throws IOException
	 */
	@PutMapping(value = "/meeting/{meetingId}/memo/page/{pageNo}/image/draw")
	public boolean putMemoPageDrawImage(HttpSession session, @PathVariable Integer meetingId, @PathVariable Integer pageNo, @RequestBody @Nullable String image) throws IOException {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingId);
		if(authorityCollection.hasAuthority(MeetingAuth.ATTEND) || monitoringMng.getEntriedMeetingId(session.getId()) == meetingId) {
			return noteServ.drawMemoPage(meetingId, LimeSecurityUtil.getLoginId(), pageNo, image);
		}else {
			throw new ApiDataAccessDeniedException(ErrorCode.FILE.READ_NO_AUTHORITY);
		}
	}
	
	/**
	 * 메모장 페이지 삭제
	 * @param session
	 * @param meetingId
	 * @param fileId
	 * @param pageNo
	 * @param image
	 * @return
	 * @throws IOException
	 */
	@DeleteMapping(value = "/meeting/{meetingId}/memo/page/{pageNo}")
	public boolean deleteMemoPage(HttpSession session, @PathVariable Integer meetingId, @PathVariable Integer pageNo) throws IOException {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingId);
		if(authorityCollection.hasAuthority(MeetingAuth.ATTEND)) {
			return noteServ.deleteMemoPage(meetingId, LimeSecurityUtil.getLoginId(), pageNo);
		}else {
			throw new ApiDataAccessDeniedException(ErrorCode.FILE.READ_NO_AUTHORITY);
		}
	}
}
