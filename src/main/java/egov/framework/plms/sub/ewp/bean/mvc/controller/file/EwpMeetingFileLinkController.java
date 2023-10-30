package egov.framework.plms.sub.ewp.bean.mvc.controller.file;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;

import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import egov.framework.plms.main.core.model.auth.ResourceAuthorityCollection;
import egov.framework.plms.main.core.model.enums.auth.FileAuth;
import egov.framework.plms.sub.ewp.bean.component.auth.EwpResourceAuthorityProvider;
import egov.framework.plms.sub.ewp.bean.mvc.entity.file.MeetingFileInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.service.file.EwpMeetingFileInfoService;
import egov.framework.plms.sub.ewp.bean.mvc.service.file.EwpMeetingFileLinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@Profile("ewp")
public class EwpMeetingFileLinkController {
	private final EwpMeetingFileInfoService fileServ;
	private final EwpMeetingFileLinkService linkServ;
	
	private final EwpResourceAuthorityProvider authorityProvider;
	
	/**
	 * 파일 다운로드 요청. HttpServletResponse를 통해 전송한다. FileService에 파리미터를 모두 넘겨 처리.
	 * @param request : 브라우저를 구분하기 위한 HttpServletRequest 객체
	 * @param response : 파일을 담아 전송할 HttpServletResponse 객체
	 * @param link : 다운로드 요청한 파일 링크
	 * @throws Exception 
	 */
	@GetMapping(value = "/meeting/file/{fileKey}/download")
	public ResponseEntity<Resource> downloadFile(Authentication authentication, HttpServletRequest request, HttpServletResponse response, @PathVariable Integer fileKey){
		ResourceAuthorityCollection authorityCollection = authorityProvider.getFileAuthorityCollection(authentication, fileKey);
		if(authorityCollection.isEmpty()) {
			throw new BadRequestException("");
		}
		if(authorityCollection.hasAuthority(FileAuth.DOWN)) {
			MeetingFileInfoVO fileVO = fileServ.getFileOne(fileKey);
			return linkServ.fileDownload(request, fileVO);
		}else {
			throw new AccessDeniedException("");
		}
	}
	
	/**
	 * 파일 조회
	 * @param link : 조회할 파일 링크
	 * @return
	 * @throws IOException
	 */
	@GetMapping(value = "/meeting/file/{fileKey}/view")
	public ResponseEntity<Resource> viewFile(Authentication authentication, @PathVariable Integer fileKey) throws IOException{
		ResourceAuthorityCollection authorityCollection = authorityProvider.getFileAuthorityCollection(authentication, fileKey);
		if(authorityCollection.isEmpty()) {
			throw new BadRequestException("");
		}
		if(authorityCollection.hasAuthority(FileAuth.VIEW)) {
			MeetingFileInfoVO fileVO = fileServ.getFileOne(fileKey);
			return linkServ.fileView(fileVO);
		}else {
			throw new AccessDeniedException("");
		}
	}
	
	@ExceptionHandler(BadRequestException.class) 
	public void badRequestExceptionHandler(BadRequestException e, HttpServletRequest request, HttpServletResponse response) throws IOException { 
		request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.BAD_REQUEST);
		response.sendError(HttpServletResponse.SC_BAD_REQUEST);
	}
	
	@ExceptionHandler(AccessDeniedException.class) 
	public void illegalExceptionHandler(IllegalArgumentException e, HttpServletRequest request, HttpServletResponse response) throws IOException { 
		request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.FORBIDDEN);
		response.sendError(HttpServletResponse.SC_FORBIDDEN);
	}
}
