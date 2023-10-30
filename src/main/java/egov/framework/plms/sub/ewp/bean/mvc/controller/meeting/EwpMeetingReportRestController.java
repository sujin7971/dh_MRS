package egov.framework.plms.sub.ewp.bean.mvc.controller.meeting;

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

import egov.framework.plms.main.core.model.auth.ResourceAuthorityCollection;
import egov.framework.plms.main.core.model.enums.auth.ReportAuth;
import egov.framework.plms.main.core.model.enums.meeting.ReportStatus;
import egov.framework.plms.main.core.model.response.ErrorMessage;
import egov.framework.plms.main.core.model.response.ResponseMessage;
import egov.framework.plms.sub.ewp.bean.component.auth.EwpResourceAuthorityProvider;
import egov.framework.plms.sub.ewp.bean.mvc.entity.file.MeetingFileInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingReportDTO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingReportVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpReportOpinionDTO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpReportOpinionVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.organization.EwpUserInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.service.alarm.EwpAlarmWriteService;
import egov.framework.plms.sub.ewp.bean.mvc.service.file.EwpMeetingFileInfoService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingReportService;
import egov.framework.plms.sub.ewp.bean.mvc.service.organization.EwpUserInfoService;
import egov.framework.plms.sub.ewp.core.model.enums.FileRole;
import egov.framework.plms.sub.ewp.core.model.login.EwpAuthenticationDetails;
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
@RequestMapping("/api/ewp")
@RequiredArgsConstructor
@Profile("ewp")
public class EwpMeetingReportRestController {
	private final EwpResourceAuthorityProvider authorityProvider;
	
	private final EwpMeetingReportService rpServ;
	private final EwpUserInfoService userServ;
	private final EwpMeetingFileInfoService fileServ;
	private final EwpAlarmWriteService ewpAlarmServ;
	/**
	 * 회의록 작성
	 * 
	 * @param meetingKey
	 * @param data
	 * @return
	 */
	@PostMapping("/meeting/{meetingKey}/report")
	public ResponseMessage putMeetingReport(Authentication authentication, @PathVariable Integer meetingKey,
			EwpMeetingReportDTO reportDTO) {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingReportAuthorityCollection(meetingKey);
		if(authorityCollection.isEmpty()) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.NOT_FOUND)
					.message(ResponseMessage.MessageCode.REPORT.PUT_FAIL.value())
					.detail(ResponseMessage.DetailCode.MEETING.NOT_FOUND.value())
					.build();
		}
		EwpAuthenticationDetails userDetails = (EwpAuthenticationDetails) authentication.getDetails();
		String loginKey = userDetails.getUser().getUserKey();
		if(authorityCollection.hasAuthority(ReportAuth.UPDATE)) {
			if(reportDTO.getPdf() != null) {
				List<MeetingFileInfoVO> prevReportList = fileServ.getMeetingFileList(MeetingFileInfoVO.builder().meetingKey(meetingKey).roleType(FileRole.REPORT).build());
				prevReportList.forEach(fileVO -> {
					fileServ.deleteFile(fileVO.getFileKey());
				});
				ResponseMessage fileRes = fileServ.uploadFile(meetingKey, FileRole.REPORT, reportDTO.getPdf());
				if(!fileRes.isSuccess()) {
					return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
							.message(ResponseMessage.MessageCode.REPORT.PUT_FAIL.value())
							.detail(ResponseMessage.DetailCode.REPORT.REPORT_UPLOAD.value())
							.build();
				}
			}
			EwpMeetingReportVO reportVO = reportDTO.convert();
			
			boolean success = rpServ.putMeetingReport(reportVO);
			if(success) {
				if(reportVO.getReportStatus() == ReportStatus.OPEN) {
					/* 의견 요청 메일 발송 */
					ewpAlarmServ.sendReportAlarm(meetingKey);
				}
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
	 * @param meetingKey : 회의번호
	 * @return 성공시 2, 실패시 0
	 */
	@DeleteMapping("/meeting/{meetingKey}/report")
	public ResponseMessage deleteMeetingReport(Authentication authentication, @PathVariable Integer meetingKey) {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingReportAuthorityCollection(meetingKey);
		if(authorityCollection.isEmpty()) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.NOT_FOUND)
					.message(ResponseMessage.MessageCode.REPORT.DELETE_FAIL.value())
					.detail(ResponseMessage.DetailCode.MEETING.NOT_FOUND.value())
					.build();
		}
		if(authorityCollection.hasAuthority(ReportAuth.DELETE)) {
			boolean result = rpServ.deleteMeetingReport(meetingKey);
			if(result) {
				rpServ.deleteMeetingReportOpnAll(meetingKey);
				fileServ.deleteFile(fileServ.getMeetingFileList(MeetingFileInfoVO.builder().meetingKey(meetingKey).roleType(FileRole.REPORT).build()).get(0).getFileKey());
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
	 * 회의록 의견 등록
	 * @param authentication
	 * @param meetingKey 회의 고유키
	 * @param comment 의견
	 * @return
	 */
	@PostMapping("/meeting/{meetingKey}/report/opinion")
	public ResponseMessage postReportOpn(Authentication authentication, @PathVariable Integer meetingKey, @RequestBody String comment) {
		EwpAuthenticationDetails userDetails = (EwpAuthenticationDetails) authentication.getDetails();
		EwpUserInfoVO user = userDetails.getUser();
		EwpReportOpinionVO params = EwpReportOpinionVO.builder().meetingKey(meetingKey).writerKey(user.getUserId()).writerName(user.getUserName()).writerTel(user.getOfficeDeskPhone()).comment(comment).build();
		boolean result = rpServ.postMeetingReportOpn(params);
		if(result) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
					.message(ResponseMessage.MessageCode.REPORTOPN.POST_SUCCESS.value())
					.data(params.convert())
					.build();
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
					.message(ResponseMessage.MessageCode.REPORTOPN.POST_FAIL.value())
					.build();
		}
	}
	
	/**
	 * 회의록 의견 삭제
	 * @param authentication
	 * @param meetingKey 회의 고유키
	 * @param opnId 회의록 의견 고유키
	 * @return
	 */
	@DeleteMapping("/meeting/{meetingKey}/report/opinion/{opnId}")
	public ResponseMessage deleteReportOpn(Authentication authentication, @PathVariable Integer meetingKey, @PathVariable Integer opnId) {
		EwpAuthenticationDetails userDetails = (EwpAuthenticationDetails) authentication.getDetails();
		String loginKey = userDetails.getUser().getUserKey();
		boolean result = rpServ.deleteMeetingReportOpn(meetingKey, opnId, loginKey);
		if(result) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
					.message(ResponseMessage.MessageCode.REPORTOPN.DELETE_SUCCESS.value())
					.build();
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
					.message(ResponseMessage.MessageCode.REPORTOPN.DELETE_FAIL.value())
					.build();
		}
	}

	/**
	 * 회의록 요청. 회의록이 존재하지 않는 경우 회의록 생성 후 리턴
	 * @param meetingKey : 회의 고유키
	 * @return
	 */
	@GetMapping("/meeting/{meetingKey}/report")
	public EwpMeetingReportDTO getMeetingReportOne(Authentication authentication, @PathVariable Integer meetingKey) {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingReportAuthorityCollection(meetingKey);
		if(authorityCollection.hasAuthority(ReportAuth.VIEW) || authorityCollection.hasAuthority(ReportAuth.OPINION)) {
			EwpAuthenticationDetails userDetails = (EwpAuthenticationDetails) authentication.getDetails();
			String loginKey = userDetails.getUser().getUserKey();
			Optional<EwpMeetingReportVO> reportOpt = rpServ.getMeetingReportOne(meetingKey);
			if(!reportOpt.isPresent()) {
				rpServ.postMeetingReport(EwpMeetingReportVO.builder()
						.meetingKey(meetingKey)
						.reporterKey(loginKey)
						.build());
				reportOpt = rpServ.getMeetingReportOne(meetingKey);
			}
			return reportOpt.map(vo -> vo.toBuilder().reporter(userServ.selectUserInfoOne(vo.getReporterId()).orElse(null)).build()).get().convert();
		}else {
			throw new AccessDeniedException("");
		}
	}
	
	@GetMapping("/meeting/{meetingKey}/report/opinion")
	public List<EwpReportOpinionDTO> getMeetingReportOpinionList(Authentication authentication, @PathVariable Integer meetingKey) {
		ResourceAuthorityCollection authorityCollection = authorityProvider.getMeetingReportAuthorityCollection(meetingKey);
		if(authorityCollection.hasAuthority(ReportAuth.VIEW) || authorityCollection.hasAuthority(ReportAuth.OPINION)) {
			EwpAuthenticationDetails userDetails = (EwpAuthenticationDetails) authentication.getDetails();
			List<EwpReportOpinionVO> voList = rpServ.getReportOpnList(meetingKey);
			return voList.stream().map(EwpReportOpinionVO::convert).collect(Collectors.toList());
		}else {
			throw new AccessDeniedException("");
		}
	}
}
