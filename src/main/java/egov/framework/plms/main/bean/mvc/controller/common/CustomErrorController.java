package egov.framework.plms.main.bean.mvc.controller.common;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import egov.framework.plms.main.core.util.CommUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class CustomErrorController implements ErrorController {

	private static final String ERROR_PATH = "/error"; // configure 에서 Redirect 될 path

	@GetMapping(value = ERROR_PATH)
	public void error(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String ajaxHeader = request.getHeader("X-Requested-With");
		String forwardUri = (String) request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		String message = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
		if(!CommUtil.isEmpty(message)) {
			message = URLEncoder.encode(message, "UTF-8");
			log.info("에러 메시지: {}", message);
		}
		request.setAttribute("message", message);
		log.info("헤더: {}, 에러코드: {}, URI: {}", ajaxHeader, status, forwardUri);
		if (!"XMLHttpRequest".equals(ajaxHeader)) {
			if (status != null) {
				Integer statusCode = Integer.valueOf(status.toString());
				log.info("statusCode: {}", statusCode);
				if (statusCode.equals(HttpStatus.BAD_REQUEST.value())) {
					response.sendRedirect("/error/400?message="+message);
				} else if (statusCode.equals(HttpStatus.FORBIDDEN.value())) {
					response.sendRedirect("/error/403?message="+message);
				} else if (statusCode.equals(HttpStatus.NOT_FOUND.value())) {
					response.sendRedirect("/error/404?message="+message);
				} else if (statusCode.equals(HttpStatus.INTERNAL_SERVER_ERROR.value())) {
					response.sendRedirect("/error/500?message="+message);
				}else {
					response.sendRedirect("/error/400?message="+message);
				}
			}else {
				log.info("home redirect");
				response.sendRedirect("/home");
			}
		}else {
			// 401 Error를 반환
			if (status != null) {
				Integer statusCode = Integer.valueOf(status.toString());
				log.info("statusCode: {}", statusCode);
				if (statusCode.equals(HttpStatus.BAD_REQUEST.value())) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				} else if (statusCode.equals(HttpStatus.FORBIDDEN.value())) {
					response.sendError(HttpServletResponse.SC_FORBIDDEN);
				} else if (statusCode.equals(HttpStatus.NOT_FOUND.value())) {
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
				} else if (statusCode.equals(HttpStatus.INTERNAL_SERVER_ERROR.value())) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
			}else {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증되지 않은 사용자의 요청입니다. 로그인 페이지로 이동합니다.");
			}
		}
	}
	
	@GetMapping(value = "/error/400")
	public String badRequest(@RequestParam @Nullable String message, Model model) {
		log.info("400 error message: {}", message);
		model.addAttribute("message", message);
		return "/core/error/400";
	}

	@GetMapping(value = "/error/403")
	public String accessDenied(@RequestParam @Nullable String message, Model model) {
		log.info("403 error message: {}", message);
		model.addAttribute("message", message);
		return "/core/error/403";
	}

	@GetMapping(value = "/error/404")
	public String notFound(@RequestParam @Nullable String message, Model model) {
		log.info("404 error message: {}", message);
		model.addAttribute("message", message);
		return "/core/error/404";
	}
	
	@GetMapping(value = "/error/500")
	public String internalError(@RequestParam @Nullable String message, Model model) {
		log.info("500 error message: {}", message);
		model.addAttribute("message", message);
		return "/core/error/500";
	}

	@Override
	public String getErrorPath() {
		return ERROR_PATH;
	}
}
