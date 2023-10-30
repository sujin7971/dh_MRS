package egov.framework.plms.main.bean.mvc.controller.common;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import egov.framework.plms.main.core.exception.ApiBadRequestException;
import egov.framework.plms.main.core.exception.ApiDataAccessDeniedException;
import egov.framework.plms.main.core.exception.ApiNotFoundException;
import egov.framework.plms.main.core.util.CommUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionController {
	
	@ExceptionHandler({BadRequestException.class})
	public void badRequestError(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.info("BadRequestException- message: {}", e.getMessage());
		response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
	}
	@ExceptionHandler({NotFoundException.class})
	public void notFoundError(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.info("NotFoundException- message: {}", e.getMessage());
		response.sendError(HttpStatus.NOT_FOUND.value(), e.getMessage());
	}
	
	@ExceptionHandler(AccessDeniedException.class)
    public void handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
		String ajaxHeader = request.getHeader("X-Requested-With");
		if (!"XMLHttpRequest".equals(ajaxHeader)) {
			response.sendRedirect("/error/403?message="+CommUtil.getBrowserEncodedText(request, e.getMessage()));
		}else {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}
    }
	
	@ExceptionHandler(ApiDataAccessDeniedException.class)
	public void handleApiDataAccessDeniedException(ApiDataAccessDeniedException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
		String ajaxHeader = request.getHeader("X-Requested-With");
		if (!"XMLHttpRequest".equals(ajaxHeader)) {
			response.sendRedirect("/error/403?message="+CommUtil.getBrowserEncodedText(request, e.getMessage()));
		}else {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
		}
	}
	
	@ExceptionHandler(ApiNotFoundException.class)
    public void handleApiNotFoundException(ApiNotFoundException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
		String ajaxHeader = request.getHeader("X-Requested-With");
		if (!"XMLHttpRequest".equals(ajaxHeader)) {
			response.sendRedirect("/error/404?message="+CommUtil.getBrowserEncodedText(request, e.getMessage()));
		}else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
		}
    }
	
	@ExceptionHandler(ApiBadRequestException.class)
    public void handleApiNotFoundException(ApiBadRequestException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
		String ajaxHeader = request.getHeader("X-Requested-With");
		if (!"XMLHttpRequest".equals(ajaxHeader)) {
			response.sendRedirect("/error/400?message="+CommUtil.getBrowserEncodedText(request, e.getMessage()));
		}else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		}
    }
}
