package egov.framework.plms.sub.ewp.bean.mvc.controller.file;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Profile;
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
import egov.framework.plms.main.core.model.auth.ResourceAuthorityCollection;
import egov.framework.plms.main.core.model.enums.auth.FileAuth;
import egov.framework.plms.main.core.model.enums.auth.MeetingAuth;
import egov.framework.plms.main.core.model.enums.error.ErrorCode;
import egov.framework.plms.main.core.model.response.ResponseMessage;
import egov.framework.plms.sub.ewp.bean.component.auth.EwpResourceAuthorityProvider;
import egov.framework.plms.sub.ewp.bean.mvc.entity.file.MeetingFileInfoDTO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.file.MeetingFileInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.service.file.EwpMeetingFileInfoService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingInfoService;
import egov.framework.plms.sub.ewp.core.model.enums.FileRole;
import egov.framework.plms.sub.ewp.core.util.EwpSecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/ewp")
@RequiredArgsConstructor
@Profile("ewp")
public class EwpMeetingFileRestController {
	private final PassTokenProvider tokenProvider;
	
	private final EwpMeetingFileInfoService fileServ;
	private final EwpMeetingInfoService mtServ;
	private final EwpResourceAuthorityProvider authorityProvider;
	private final FileConfigProperties fileProperties;
	
	/**
	 * 여러 파일 업로드 요청.
	 * @param meetingKey : 파일을 업로드할 회의 번호 
	 * @param upFiles : 업로드할 파일 목록
	 * @return
	 */
	@PostMapping(value = "/meeting/{meetingKey}/file/{roleType}/list")
	public ResponseMessage postFileList(Authentication authentication, HttpSession session, @PathVariable Integer meetingKey, @PathVariable FileRole roleType, @RequestParam("list") @Nullable List<MultipartFile> files){
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingKey);
		if(authorityCollection.isEmpty()) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.NOT_FOUND)
					.message(ResponseMessage.MessageCode.FILE.POST_FAIL.value())
					.detail(ResponseMessage.DetailCode.MEETING.NOT_FOUND.value())
					.build();
		}
		MeetingAuth reqAuth = null;
		switch(roleType) {
			case MATERIAL:
				reqAuth = MeetingAuth.UPLOAD;
				break;
			case REPORT:
				reqAuth = MeetingAuth.REPORT;
				break;
			case VOICE:
				reqAuth = MeetingAuth.VOICE;
				break;
			case PHOTO:
				reqAuth = MeetingAuth.PHOTO;
				break;
		}
		if(reqAuth == null) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
					.message(ResponseMessage.MessageCode.FILE.POST_FAIL.value())
					.build();
		}
		if(!authorityCollection.hasAuthority(reqAuth)) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.FORBIDDEN)
					.message(ResponseMessage.MessageCode.FILE.POST_FAIL.value())
					.detail(ResponseMessage.DetailCode.MEETING.FORBIDDEN.value())
					.build();
		}
		return fileServ.uploadFileList(meetingKey, roleType, files);
	}
	
	/**
	 * 요청받은 모든 파일 삭제
	 * @param linkList : 삭제할 파일 링크목록
	 */
	@DeleteMapping(value = "/meeting/{meetingKey}/file/list")
	public ResponseMessage deleteFileList(Authentication authentication, HttpSession session, @PathVariable Integer meetingKey, @RequestBody List<Integer> delList) {
		for(Integer fileKey : delList) {
			ResourceAuthorityCollection authorityCollection = authorityProvider.getFileAuthorityCollection(authentication, fileKey);
			if(authorityCollection.isEmpty()) {
				return ResponseMessage.builder(ResponseMessage.StatusCode.NOT_FOUND)
						.message(ResponseMessage.MessageCode.FILE.DELETE_FAIL.value())
						.detail(ResponseMessage.DetailCode.FILE.NOT_FOUND.value())
						.data(fileKey)
						.build();
			}
			if(!authorityCollection.hasAuthority(FileAuth.DELETE)) {
				return ResponseMessage.builder(ResponseMessage.StatusCode.FORBIDDEN)
						.message(ResponseMessage.MessageCode.FILE.DELETE_FAIL.value())
						.detail(ResponseMessage.DetailCode.FILE.FORBIDDEN.value())
						.data(fileKey)
						.build();
			}
		}
		return fileServ.updateFileListToDelete(delList);
	}
	
	@GetMapping(value = "/meeting/{meetingKey}/file/material/list")
	public List<MeetingFileInfoDTO> getMeetingFileList(Authentication authentication, @PathVariable Integer meetingKey) throws AccessDeniedException {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingKey);
		if(authorityCollection.hasAuthority(MeetingAuth.VIEW)) {
			List<MeetingFileInfoVO> voList = fileServ.getMeetingFileList(MeetingFileInfoVO.builder().meetingKey(meetingKey).roleType(FileRole.MATERIAL).build());
			return voList.stream().map(vo -> vo.convert()).collect(Collectors.toList());
		}else {
			throw new AccessDeniedException(ErrorCode.FILE.READ_NO_AUTHORITY.getMessage());
		}
	}
	
	@GetMapping(value = "/meeting/{meetingKey}/file/shared/list")
	public List<MeetingFileInfoDTO> getMeetingSharedFileList(Authentication authentication, @PathVariable Integer meetingKey) throws AccessDeniedException {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingKey);
		if(authorityCollection.hasAuthority(MeetingAuth.VIEW)) {
			List<MeetingFileInfoVO> voList = fileServ.getMeetingSharedFileList(meetingKey);
			return voList.stream().map(vo -> vo.convert()).collect(Collectors.toList());
		}else {
			throw new AccessDeniedException(ErrorCode.FILE.READ_NO_AUTHORITY.getMessage());
		}
	}
	
	@GetMapping(value = "/meeting/{meetingKey}/file/private/list")
	public List<MeetingFileInfoDTO> getMeetingPrivateFileList(Authentication authentication, @PathVariable Integer meetingKey) throws AccessDeniedException {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingKey);
		if(authorityCollection.hasAuthority(MeetingAuth.VIEW)) {
			List<MeetingFileInfoVO> voList = fileServ.getMeetingPrivateFileList(meetingKey, EwpSecurityUtil.getLoginId());
			return voList.stream().map(vo -> vo.convert()).collect(Collectors.toList());
		}else {
			throw new AccessDeniedException(ErrorCode.FILE.READ_NO_AUTHORITY.getMessage());
		}
	}
	
	@GetMapping(value = "/meeting/{meetingKey}/file/all/list")
	public List<MeetingFileInfoDTO> getMeetingAllFileList(Authentication authentication, @PathVariable Integer meetingKey) throws AccessDeniedException {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingAuthorityCollection(meetingKey);
		if(authorityCollection.hasAuthority(MeetingAuth.VIEW)) {
			List<MeetingFileInfoVO> voList = fileServ.getMeetingFileList(MeetingFileInfoVO.builder().meetingKey(meetingKey).empKey(EwpSecurityUtil.getLoginId()).delYN('N').build());
			return voList.stream().map(vo -> vo.convert()).collect(Collectors.toList());
		}else {
			throw new AccessDeniedException(ErrorCode.FILE.READ_NO_AUTHORITY.getMessage());
		}
	}
}
