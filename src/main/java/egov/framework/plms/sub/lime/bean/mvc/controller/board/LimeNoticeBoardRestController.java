package egov.framework.plms.sub.lime.bean.mvc.controller.board;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import egov.framework.plms.main.bean.mvc.service.file.FileInfoService;
import egov.framework.plms.main.bean.mvc.service.file.FileRelationService;
import egov.framework.plms.main.core.model.enums.file.RelatedEntityType;
import egov.framework.plms.main.core.model.enums.file.RelationType;
import egov.framework.plms.main.core.model.response.ResponseMessage;
import egov.framework.plms.sub.lime.bean.mvc.entity.board.LimeNoticeBoardDTO;
import egov.framework.plms.sub.lime.bean.mvc.entity.board.LimeNoticeBoardVO;
import egov.framework.plms.sub.lime.bean.mvc.service.board.LimeNoticeBoardService;
import egov.framework.plms.sub.lime.core.model.login.LimeAuthenticationDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/lime")
@RequiredArgsConstructor
@Profile("lime")
public class LimeNoticeBoardRestController {

	@Autowired
	private LimeNoticeBoardService noticeServ;
	private final FileInfoService fileServ;
	private final FileRelationService relationServ;
	
	@PostMapping("/admin/notice")
	public ResponseMessage postNotice(Authentication authentication, LimeNoticeBoardDTO params) {
		LimeAuthenticationDetails details = (LimeAuthenticationDetails) authentication.getDetails();
		String loginId = details.getUserId();
		return noticeServ.postNotice(params.convert().toBuilder().writerId(loginId).build());
	}
	
	@DeleteMapping("/admin/notice/{noticeId}")
	public ResponseMessage delNoticeOne(@PathVariable Integer noticeId) {
		return noticeServ.deleteNotice(noticeId);
	}
	
	@PutMapping("/admin/notice/{noticeId}")
	public ResponseMessage putNoticeOne(@PathVariable Integer noticeId, LimeNoticeBoardDTO params) {
		return noticeServ.putNotice(params.convert().toBuilder().noticeId(noticeId).build());
	}
	
	@GetMapping("/notice/{noticeId}")
	public LimeNoticeBoardDTO getNoticeOne(@PathVariable Integer noticeId) {
		LimeNoticeBoardVO vo = noticeServ.getNoticeOne(noticeId);
		return Optional.ofNullable(vo).map(LimeNoticeBoardVO::convert).orElse(null);
	}
	
	@GetMapping("/notice/list")
	public List<LimeNoticeBoardDTO> getNoticeList(
			@RequestParam @Nullable String officeCode,
			@RequestParam @Nullable String title,
			@RequestParam @Nullable Character fixYN,
			@RequestParam @Nullable String startDate, 
			@RequestParam @Nullable String endDate,
			@RequestParam @Nullable Integer pageNo,
			@RequestParam @Nullable Integer pageCnt) {
		List<LimeNoticeBoardVO> voList = noticeServ.getNoticeList(LimeNoticeBoardVO.builder().officeCode(officeCode).title(title).fixYN(fixYN).startDate(startDate).endDate(endDate).pageNo(pageNo).pageCnt(pageCnt).build());
		return voList.stream().map(LimeNoticeBoardVO::convert).collect(Collectors.toList());
	}
	
	@GetMapping("/notice/list/cnt")
	public Integer getNoticeListCnt(
			@RequestParam @Nullable String officeCode,
			@RequestParam @Nullable String title,
			@RequestParam @Nullable Character fixYN,
			@RequestParam @Nullable String startDate, 
			@RequestParam @Nullable String endDate,
			@RequestParam @Nullable Integer pageNo,
			@RequestParam @Nullable Integer pageCnt) {
		return noticeServ.getNoticeListCnt(LimeNoticeBoardVO.builder().officeCode(officeCode).title(title).fixYN(fixYN).startDate(startDate).endDate(endDate).pageNo(pageNo).pageCnt(pageCnt).build());
	}
	
	/**
	 * 공지사항 파일 업로드 요청.
	 * @param meetingKey : 파일을 업로드할 회의 번호 
	 * @param upFiles : 업로드할 파일 목록
	 * @return
	 */
	@PostMapping(value = "/admin/notice/{noticeId}/file/list")
	public ResponseMessage postNoticeBoardFileList(@PathVariable Integer noticeId, @RequestBody @Nullable List<Integer> files){
		Set<Integer> successSet = files.stream().map(fileId -> {
			boolean result = relationServ.insertFileRelationOne(fileId, RelatedEntityType.NOTICE_BOARD, noticeId, RelationType.BOARD_ATTACHMENT);
			return (result)?fileId:null;
		}).filter(fileId -> fileId != null).collect(Collectors.toSet());
		return ResponseMessage.builder(ResponseMessage.StatusCode.OK).data(successSet).build();
	}
	
	/**
	 * 공지사항 파일 삭제 요청
	 * @param noticeId
	 * @param files
	 * @return
	 */
	@DeleteMapping(value = "/admin/notice/{noticeId}/file/list")
	public ResponseMessage deleteNoticeBoardFileList(@PathVariable Integer noticeId, @RequestBody @Nullable List<Integer> files){
		Set<Integer> successSet = files.stream().map(fileId -> {
			boolean result = relationServ.deleteFileRelationList(fileId, RelatedEntityType.NOTICE_BOARD, noticeId);
			return (result)?fileId:null;
		}).filter(fileId -> fileId != null).collect(Collectors.toSet());
		successSet.stream().forEach(fileId -> fileServ.updateFileOneToDelete(fileId));
		return ResponseMessage.builder(ResponseMessage.StatusCode.OK).data(successSet).build();
	}
}
