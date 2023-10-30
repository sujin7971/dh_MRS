package egov.framework.plms.sub.lime.bean.mvc.controller.meeting;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.text.StringEscapeUtils;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import egov.framework.plms.main.bean.mvc.entity.file.FileDetailVO;
import egov.framework.plms.main.bean.mvc.entity.file.FileInfoVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingReportDTO;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingReportVO;
import egov.framework.plms.main.bean.mvc.service.file.FileRelationService;
import egov.framework.plms.main.core.model.auth.ResourceAuthorityCollection;
import egov.framework.plms.main.core.model.enums.auth.ReportAuth;
import egov.framework.plms.main.core.model.enums.file.RelatedEntityType;
import egov.framework.plms.main.core.model.enums.file.RelationType;
import egov.framework.plms.main.core.model.response.ErrorMessage;
import egov.framework.plms.main.core.model.response.ResponseMessage;
import egov.framework.plms.sub.ewp.bean.mvc.entity.file.MeetingFileInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpReportOpinionDTO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpReportOpinionVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.organization.EwpUserInfoVO;
import egov.framework.plms.sub.ewp.core.model.enums.FileRole;
import egov.framework.plms.sub.ewp.core.model.login.EwpAuthenticationDetails;
import egov.framework.plms.sub.lime.bean.component.auth.LimeResourceAuthorityProvider;
import egov.framework.plms.sub.lime.bean.mvc.service.meeting.LimeMeetingFileInfoService;
import egov.framework.plms.sub.lime.bean.mvc.service.meeting.LimeMeetingReportService;
import egov.framework.plms.sub.lime.bean.mvc.service.organization.LimeUserInfoService;
import egov.framework.plms.sub.lime.core.util.LimeSecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 회의록 AJAX 요청 처리 컨트롤러
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 28
 */
@Slf4j
@RestController
@RequestMapping("/api/lime")
@RequiredArgsConstructor
@Profile("lime")
public class LimeMeetingReportRestController {
	private final LimeResourceAuthorityProvider authorityProvider;
	private final LimeMeetingReportService rpServ;
	private final LimeUserInfoService userServ;
	private final LimeMeetingFileInfoService fileServ;
	/**
	 * 회의록 작성
	 * 
	 * @param meetingId
	 * @param data
	 * @return
	 */
	@PostMapping("/meeting/{meetingId}/report")
	public ResponseMessage putMeetingReport(@PathVariable Integer meetingId,
			MeetingReportDTO reportDTO) {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingReportAuthorityCollection(meetingId);
		if(authorityCollection.isEmpty()) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.NOT_FOUND)
					.message(ResponseMessage.MessageCode.REPORT.PUT_FAIL.value())
					.detail(ResponseMessage.DetailCode.MEETING.NOT_FOUND.value())
					.build();
		}
		String loginId = LimeSecurityUtil.getLoginId();
		if(authorityCollection.hasAuthority(ReportAuth.UPDATE)) {
			if(reportDTO.getReportFile() != null) {
				List<FileDetailVO> prevReportList = fileServ.getMeetingFileList(meetingId, RelationType.MEETING_REPORT);
				prevReportList.forEach(fileVO -> {
					fileServ.updateFileOneToDelete(fileVO.getFileId());
				});
				Optional<FileDetailVO> fileOpt = fileServ.uploadReportFile(meetingId, reportDTO.getReportFile());
				if(!fileOpt.isPresent()) {
					return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
							.message(ResponseMessage.MessageCode.REPORT.PUT_FAIL.value())
							.detail(ResponseMessage.DetailCode.REPORT.REPORT_UPLOAD.value())
							.build();
				}
			}
			String contents = reportDTO.getReportContents();
			String unescapeContents = StringEscapeUtils.unescapeHtml4(contents);
			log.info("contents: {}, unescapeContents: {}", contents, unescapeContents);
			PolicyFactory policy = new HtmlPolicyBuilder()
					.allowUrlProtocols("http", "https")
					.allowElements(
						"a", "abbr", "acronym", "address", "area", 
						"b", "big", "blockquote", "br", "button", 
						"caption", "center", "cite", "code", "col", 
						"colgroup", "dd", "del", "dfn", "dir", 
						"div", "dl", "dt", "em", "fieldset", 
						"font", "form", "h1", "h2", "h3", 
						"h4", "h5", "h6", "hr", "i", 
						"img", "input", "ins", "kbd", "label", 
						"legend", "li", "map", "menu", "ol", 
						"optgroup", "option", "p", "pre", "q", 
						"s", "samp", "select", "small", "span", 
						"strike", "strong", "sub", "sup", "table", 
						"tbody", "td", "textarea", "tfoot", "th", 
						"thead", "tr", "tt", "u", "ul", "var")
					.allowAttributes("id", "class", "name", "data", "value", "style")
						.globally()
					.allowAttributes("src", "alt", "border", "cellpadding", "cellspacing", "style", "width")
						.onElements("img")
					.allowAttributes("style", "width", "summary")
						.onElements("table")
					.allowAttributes("style", "width")
						.onElements("td", "th")
					.toFactory();
			String safeContents = policy.sanitize(unescapeContents);
			MeetingReportVO reportVO = reportDTO.convert().toBuilder().reporterId(loginId).reportContents(safeContents).build();
			
			boolean success = rpServ.putMeetingReport(reportVO);
			if(success) {
				return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
						.message(ResponseMessage.MessageCode.REPORT.PUT_SUCCESS.value())
						.data(reportVO)
						.build();
			}else {
				return ResponseMessage.builder(ResponseMessage.StatusCode.UNPROCESSABLE_ENTITY)
						.message(ResponseMessage.MessageCode.REPORT.PUT_FAIL.value())
						.error(ErrorMessage.builder(ErrorMessage.ErrorCode.NOT_UPDATED)
								.message(ErrorMessage.MessageCode.REPORT.UNPROCESSABLE_ENTITY.value())
								.build())
						.build();
			}
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.FORBIDDEN)
					.message(ResponseMessage.MessageCode.REPORT.PUT_FAIL.value())
					.detail(ResponseMessage.DetailCode.REPORT.FORBIDDEN.value())
					.build();
		}
	}
	
	/**
	 * 회의록 삭제
	 * 
	 * @param meetingId : 회의번호
	 * @return 성공시 2, 실패시 0
	 */
	@DeleteMapping("/meeting/{meetingId}/report")
	public ResponseMessage deleteMeetingReport(Authentication authentication, @PathVariable Integer meetingId) {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingReportAuthorityCollection(meetingId);
		if(authorityCollection.isEmpty()) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.NOT_FOUND)
					.message(ResponseMessage.MessageCode.REPORT.DELETE_FAIL.value())
					.detail(ResponseMessage.DetailCode.MEETING.NOT_FOUND.value())
					.build();
		}
		if(authorityCollection.hasAuthority(ReportAuth.DELETE)) {
			boolean result = rpServ.deleteMeetingReport(meetingId);
			if(result) {
				fileServ.updateFileOneToDelete(fileServ.getMeetingFileList(meetingId, RelationType.MEETING_REPORT).get(0).getFileId());
				return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
						.message(ResponseMessage.MessageCode.REPORT.DELETE_SUCCESS.value())
						.build();
			}else {
				return ResponseMessage.builder(ResponseMessage.StatusCode.UNPROCESSABLE_ENTITY)
						.message(ResponseMessage.MessageCode.REPORT.DELETE_FAIL.value())
						.error(ErrorMessage.builder(ErrorMessage.ErrorCode.NOT_UPDATED)
								.message(ErrorMessage.MessageCode.REPORT.UNPROCESSABLE_ENTITY.value())
								.build())
						.build();
			}
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.FORBIDDEN)
					.message(ResponseMessage.MessageCode.REPORT.DELETE_FAIL.value())
					.detail(ResponseMessage.DetailCode.REPORT.FORBIDDEN.value())
					.build();
		}
	}
	
	/**
	 * 회의록 요청. 회의록이 존재하지 않는 경우 회의록 생성 후 리턴
	 * @param meetingId : 회의 고유키
	 * @return
	 */
	@GetMapping("/meeting/{meetingId}/report")
	public MeetingReportDTO getMeetingReportOne(Authentication authentication, @PathVariable Integer meetingId) {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingReportAuthorityCollection(meetingId);
		if(authorityCollection.hasAuthority(ReportAuth.VIEW)) {
			String loginId = LimeSecurityUtil.getLoginId();
			Optional<MeetingReportVO> reportOpt = rpServ.getMeetingReportOne(meetingId);
			if(!reportOpt.isPresent()) {
				rpServ.postMeetingReport(MeetingReportVO.builder()
						.meetingId(meetingId)
						.reporterId(loginId)
						.build());
				reportOpt = rpServ.getMeetingReportOne(meetingId);
			}
			return reportOpt.map(vo -> vo.toBuilder().reporter(userServ.selectUserInfoOne(vo.getReporterId()).orElse(null)).build()).get().convert();
		}else {
			throw new AccessDeniedException("");
		}
	}
}
