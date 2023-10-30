package egov.framework.plms.sub.lime.bean.mvc.controller.meeting;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import egov.framework.plms.main.bean.component.auth.PassTokenProvider;
import egov.framework.plms.main.bean.component.properties.FileConfigProperties;
import egov.framework.plms.main.bean.mvc.entity.file.FileDetailDTO;
import egov.framework.plms.main.bean.mvc.entity.file.FileDetailVO;
import egov.framework.plms.main.bean.mvc.entity.file.FileInfoVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingArchiveDTO;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingArchiveVO;
import egov.framework.plms.main.bean.mvc.service.file.FileRelationService;
import egov.framework.plms.main.core.exception.ApiDataAccessDeniedException;
import egov.framework.plms.main.core.model.auth.ResourceAuthorityCollection;
import egov.framework.plms.main.core.model.enums.auth.FileAuth;
import egov.framework.plms.main.core.model.enums.auth.MeetingAuth;
import egov.framework.plms.main.core.model.enums.error.ErrorCode;
import egov.framework.plms.main.core.model.enums.file.RelatedEntityType;
import egov.framework.plms.main.core.model.enums.file.RelationType;
import egov.framework.plms.main.core.model.response.ResponseMessage;
import egov.framework.plms.main.core.util.DateTimeUtil;
import egov.framework.plms.main.core.util.FileCommUtil;
import egov.framework.plms.sub.ewp.core.model.enums.FileRole;
import egov.framework.plms.sub.lime.bean.component.auth.LimeResourceAuthorityProvider;
import egov.framework.plms.sub.lime.bean.mvc.service.file.LimeFileInfoService;
import egov.framework.plms.sub.lime.bean.mvc.service.meeting.LimeMeetingFileInfoService;
import egov.framework.plms.sub.lime.bean.mvc.service.meeting.LimeMeetingInfoService;
import egov.framework.plms.sub.lime.core.util.LimeSecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/lime")
@RequiredArgsConstructor
@Profile("lime")
public class LimeMeetingFileRestController {
	private final PassTokenProvider tokenProvider;
	private final FileConfigProperties fileProperties;
	private final LimeResourceAuthorityProvider authorityProvider;
	
	private final LimeMeetingInfoService mtServ;
	private final LimeMeetingFileInfoService mtFileServ;
	private final FileRelationService relationServ;
	
	/**
	 * 회의자료 업로드
	 * @param meetingId : 파일을 업로드할 회의 번호 
	 * @param upFiles : 업로드할 파일 목록
	 * @return
	 */
	@PostMapping(value = "/meeting/{meetingId}/file/material/list")
	public ResponseMessage postFileList(@PathVariable Integer meetingId, @RequestParam("list") @Nullable List<MultipartFile> files){
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingId);
		if(authorityCollection.isEmpty()) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.NOT_FOUND)
					.message(ResponseMessage.MessageCode.FILE.POST_FAIL.value())
					.detail(ResponseMessage.DetailCode.MEETING.NOT_FOUND.value())
					.build();
		}
		if(!authorityCollection.hasAuthority(MeetingAuth.UPLOAD)) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.FORBIDDEN)
					.message(ResponseMessage.MessageCode.FILE.POST_FAIL.value())
					.detail(ResponseMessage.DetailCode.MEETING.FORBIDDEN.value())
					.build();
		}
		Set<Integer> uploadSet = files.stream().map(file -> {
			return mtFileServ.uploadFile(file).map(FileDetailVO::getFileId).orElse(null);
		}).filter(result -> result != null).collect(Collectors.toSet());
		Set<Integer> successSet = uploadSet.stream().map(fileId -> {
			boolean result = relationServ.insertFileRelationOne(fileId, RelatedEntityType.MEETING, meetingId, RelationType.MEETING_MATERIAL);
			return (result)?fileId:null;
		}).filter(fileId -> fileId != null).collect(Collectors.toSet());
		return ResponseMessage.builder(ResponseMessage.StatusCode.OK).data(successSet).build();
	}
	
	/**
	 * 요청받은 회의자료 삭제
	 * @param linkList : 삭제할 파일 링크목록
	 */
	@DeleteMapping(value = "/meeting/{meetingId}/file/material/list")
	public ResponseMessage deleteFileList(@PathVariable Integer meetingId, @RequestBody List<Integer> files) {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingId);
		if(authorityCollection.isEmpty()) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.NOT_FOUND)
					.message(ResponseMessage.MessageCode.FILE.POST_FAIL.value())
					.detail(ResponseMessage.DetailCode.MEETING.NOT_FOUND.value())
					.build();
		}
		if(!authorityCollection.hasAuthority(MeetingAuth.UPLOAD)) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.FORBIDDEN)
					.message(ResponseMessage.MessageCode.FILE.POST_FAIL.value())
					.detail(ResponseMessage.DetailCode.MEETING.FORBIDDEN.value())
					.build();
		}
		Set<Integer> successSet = files.stream().map(fileId -> {
			boolean result = relationServ.deleteFileRelationList(fileId, RelatedEntityType.MEETING, meetingId, RelationType.MEETING_MATERIAL);
			return (result)?fileId:null;
		}).filter(fileId -> fileId != null).collect(Collectors.toSet());
		successSet.stream().forEach(fileId -> mtFileServ.updateFileOneToDelete(fileId));
		return ResponseMessage.builder(ResponseMessage.StatusCode.OK).data(successSet).build();
	}
	
	@GetMapping(value = "/meeting/{meetingId}/file/material/list")
	public List<FileDetailDTO> getMeetingFileList(Authentication authentication, @PathVariable Integer meetingId) throws AccessDeniedException {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingId);
		if(authorityCollection.hasAuthority(MeetingAuth.VIEW)) {
			List<FileDetailVO> voList = mtFileServ.getMeetingFileList(meetingId, RelationType.MEETING_MATERIAL);
			return voList.stream().map(vo -> vo.convert()).collect(Collectors.toList());
		}else {
			throw new AccessDeniedException(ErrorCode.FILE.READ_NO_AUTHORITY.getMessage());
		}
	}
	
	@GetMapping(value = "/meeting/{meetingId}/file/shared/list")
	public List<FileDetailDTO> getMeetingSharedFileList(Authentication authentication, @PathVariable Integer meetingId) throws AccessDeniedException {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingId);
		if(authorityCollection.hasAuthority(MeetingAuth.VIEW)) {
			List<FileDetailVO> voList = mtFileServ.getMeetingSharedFileList(meetingId);
			return voList.stream().map(vo -> vo.convert()).collect(Collectors.toList());
		}else {
			throw new AccessDeniedException(ErrorCode.FILE.READ_NO_AUTHORITY.getMessage());
		}
	}
	
	@GetMapping(value = "/meeting/{meetingId}/file/private/list")
	public List<FileDetailDTO> getMeetingPrivateFileList(Authentication authentication, @PathVariable Integer meetingId) throws AccessDeniedException {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingId);
		if(authorityCollection.hasAuthority(MeetingAuth.VIEW)) {
			List<FileDetailVO> voList = mtFileServ.getMeetingPrivateFileList(meetingId, LimeSecurityUtil.getLoginId());
			return voList.stream().map(vo -> vo.convert()).collect(Collectors.toList());
		}else {
			throw new AccessDeniedException(ErrorCode.FILE.READ_NO_AUTHORITY.getMessage());
		}
	}
	
	@GetMapping(value = "/meeting/{meetingId}/file/all/list")
	public List<FileDetailDTO> getMeetingAllFileList(Authentication authentication, @PathVariable Integer meetingId) throws AccessDeniedException {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingId);
		if(authorityCollection.hasAuthority(MeetingAuth.VIEW)) {
			List<FileDetailVO> voList = mtFileServ.getMeetingFileList(meetingId, LimeSecurityUtil.getLoginId());
			return voList.stream().map(vo -> vo.convert()).collect(Collectors.toList());
		}else {
			throw new AccessDeniedException(ErrorCode.FILE.READ_NO_AUTHORITY.getMessage());
		}
	}
	
	/**
	 * 파일 다운로드 요청. HttpServletResponse를 통해 전송한다. FileService에 파리미터를 모두 넘겨 처리.
	 * @param request : 브라우저를 구분하기 위한 HttpServletRequest 객체
	 * @param response : 파일을 담아 전송할 HttpServletResponse 객체
	 * @param link : 다운로드 요청한 파일 링크
	 * @throws Exception 
	 */
	@GetMapping(value = "/meeting/file/{fileId}/download")
	public ResponseEntity<Resource> downloadFile(@PathVariable Integer fileId){
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingFileAuthorityCollection(fileId);
		if(authorityCollection.hasAuthority(FileAuth.DOWN)) {
			FileDetailVO fileModel = mtFileServ.selectFileOne(fileId).get();
			File file = mtFileServ.createFileDownloadInstance(fileModel).orElse(null);
			return FileCommUtil.getFileDownloadResource(file, fileModel.getUploadedFileName());
		}else {
			throw new ApiDataAccessDeniedException(ErrorCode.FILE.DOWLOAD_NO_AUTHORITY);
		}
	}
	
	/**
	 * 파일 조회
	 * @param link : 조회할 파일 링크
	 * @return
	 * @throws IOException
	 */
	@GetMapping(value = "/meeting/file/{fileId}/view")
	public ResponseEntity<Resource> viewFile(@PathVariable Integer fileId) throws IOException{
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingFileAuthorityCollection(fileId);
		if(authorityCollection.hasAuthority(FileAuth.VIEW)) {
			return FileCommUtil.getFileResource(mtFileServ.createFileViewInstance(fileId).orElse(null));
		}else {
			throw new ApiDataAccessDeniedException(ErrorCode.FILE.PREVIEW_NO_AUTHORITY);
		}
	}
	

	/**
	 * 개인 파일함 페이지 검색 요청. 
	 * @param authentication 요청한 사용자의 고유키를 조회하기 위한 인증 객체
	 * @param scheduleHost 주관자. LIKE검색
	 * @param title 제목(사용목적). LIKE검색
	 * @param fileLabel 파일명. LIKE검색
	 * @param roleType {@link FileRole#COPY}을 요청 한 경우 판서본을 가진 회의에 대한 검색 결과를 제공.
	 * @param startDate 기간 검색을 위한 시작일자(yyyy-MM-dd)
	 * @param endDate 기간 검색을 위한 종료일자(yyyy-MM-dd)
	 * @param pageNo 페이지 번호
	 * @param pageCnt 페이지당 표시할 개수. pageNo는 유효하고 pageCnt가 NULL인 경우 10을 기본값으로 사용
	 * @return
	 */
	@GetMapping("/meeting/archive/manage/user")
	public List<MeetingArchiveDTO> getUserArchiveList(
			@RequestParam @Nullable String scheduleHost, 
			@RequestParam @Nullable String title, 
			@RequestParam @Nullable String fileLabel, 
			@RequestParam @Nullable RelationType relationType, 
			@RequestParam String startDate, 
			@RequestParam String endDate,
			@RequestParam @Nullable Integer pageNo,
			@RequestParam @Nullable Integer pageCnt) {
		String loginId = LimeSecurityUtil.getLoginId();
		LocalDate validSdate = DateTimeUtil.toFormattedDate(startDate);
		LocalDate validEdate = DateTimeUtil.toFormattedDate(endDate);
		List<MeetingArchiveVO> voList = mtFileServ.selectMeetingArchiveList(MeetingArchiveVO.builder()
				.writerId(loginId).scheduleHost(scheduleHost).title(title).uploadedFileName(fileLabel)
				.relationType(relationType).startDate(validSdate).endDate(validEdate).pageNo(pageNo).pageCnt(pageCnt).build());
		return voList.stream().map(MeetingArchiveVO::convert).collect(Collectors.toList());
	}
	
	/**
	 * 부서 파일함 페이지 검색 요청. 
	 * @param authentication 요청한 사용자의 소속 부서키를 조회하기 위한 인증 객체
	 * @param scheduleHost 주관자. LIKE검색
	 * @param title 제목(사용목적). LIKE검색
	 * @param fileLabel 파일명. LIKE검색
	 * @param startDate 기간 검색을 위한 시작일자(yyyy-MM-dd)
	 * @param endDate 기간 검색을 위한 종료일자(yyyy-MM-dd)
	 * @param pageNo 페이지 번호
	 * @param pageCnt 페이지당 표시할 개수. pageNo는 유효하고 pageCnt가 NULL인 경우 10을 기본값으로 사용
	 * @return
	 */
	@GetMapping("/meeting/archive/manage/dept")
	public List<MeetingArchiveDTO> getDeptArchiveList(
			@RequestParam @Nullable String scheduleHost, 
			@RequestParam @Nullable String title, 
			@RequestParam @Nullable String fileLabel, 
			@RequestParam String startDate, 
			@RequestParam String endDate,
			@RequestParam @Nullable Integer pageNo,
			@RequestParam @Nullable Integer pageCnt) {
		String deptId = LimeSecurityUtil.getDeptId();
		LocalDate validSdate = DateTimeUtil.toFormattedDate(startDate);
		LocalDate validEdate = DateTimeUtil.toFormattedDate(endDate);
		List<MeetingArchiveVO> voList = mtFileServ.selectMeetingArchiveList(MeetingArchiveVO.builder()
				.deptId(deptId).scheduleHost(scheduleHost).title(title).uploadedFileName(fileLabel)
				.startDate(validSdate).endDate(validEdate).pageNo(pageNo).pageCnt(pageCnt).build());
		return voList.stream().map(MeetingArchiveVO::convert).collect(Collectors.toList());
	}
	
	/**
	 * 관리자 부서 파일함 페이지 검색 요청. 
	 * @param authentication 요청한 사용자의 소속 부서키를 조회하기 위한 인증 객체
	 * @param scheduleHost 주관자. LIKE검색
	 * @param title 제목(사용목적). LIKE검색
	 * @param fileLabel 파일명. LIKE검색
	 * @param startDate 기간 검색을 위한 시작일자(yyyy-MM-dd)
	 * @param endDate 기간 검색을 위한 종료일자(yyyy-MM-dd)
	 * @param pageNo 페이지 번호
	 * @param pageCnt 페이지당 표시할 개수. pageNo는 유효하고 pageCnt가 NULL인 경우 10을 기본값으로 사용
	 * @return
	 */
	@GetMapping("/admin/master/meeting/archive/manage")
	public List<MeetingArchiveDTO> getAdminArchiveList( 
			@RequestParam @Nullable String scheduleHost, 
			@RequestParam @Nullable String title, 
			@RequestParam @Nullable String fileLabel, 
			@RequestParam String startDate, 
			@RequestParam String endDate,
			@RequestParam @Nullable Integer pageNo,
			@RequestParam @Nullable Integer pageCnt) {
		LocalDate validSdate = DateTimeUtil.toFormattedDate(startDate);
		LocalDate validEdate = DateTimeUtil.toFormattedDate(endDate);
		List<MeetingArchiveVO> voList = mtFileServ.selectMeetingArchiveList(MeetingArchiveVO.builder()
				.scheduleHost(scheduleHost).title(title).uploadedFileName(fileLabel)
				.startDate(validSdate).endDate(validEdate).pageNo(pageNo).pageCnt(pageCnt).build());
		return voList.stream().map(MeetingArchiveVO::convert).collect(Collectors.toList());
	}
}
