package egov.framework.plms.sub.ewp.bean.mvc.controller.admin;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import egov.framework.plms.main.bean.mvc.entity.admin.AdminRosterVO;
import egov.framework.plms.main.core.exception.ApiDataAccessDeniedException;
import egov.framework.plms.main.core.exception.ApiNotFoundException;
import egov.framework.plms.main.core.model.auth.ResourceAuthorityCollection;
import egov.framework.plms.main.core.model.enums.auth.MeetingAuth;
import egov.framework.plms.main.core.model.enums.error.ErrorCode;
import egov.framework.plms.main.core.model.enums.meeting.ApprovalStatus;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import egov.framework.plms.main.core.model.enums.user.DomainRole;
import egov.framework.plms.main.core.model.enums.user.ManagerRole;
import egov.framework.plms.main.core.util.DateTimeUtil;
import egov.framework.plms.sub.ewp.bean.component.auth.EwpResourceAuthorityProvider;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingScheduleVO;
import egov.framework.plms.sub.ewp.bean.mvc.service.admin.EwpAdminRosterService;
import egov.framework.plms.sub.ewp.bean.mvc.service.common.EwpCodeService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingScheduleService;
import egov.framework.plms.sub.ewp.core.model.login.EwpAuthenticationDetails;
import egov.framework.plms.sub.ewp.core.util.EwpSecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * 관리자 페이지 요청 처리 컨트롤러
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 */
@Slf4j
@Controller
@RequestMapping("/ewp")
@RequiredArgsConstructor
@Profile("ewp")
public class EwpManagerAdminController {
	private final EwpResourceAuthorityProvider authorityProvider;
	private final EwpCodeService codeServ;
	private final EwpAdminRosterService rosterServ;
	private final EwpMeetingScheduleService scheServ;
	
	/**
	 * 사용신청목록 관리 페이지
	 * @param authentication
	 * @param model
	 * @param roomType {@link RoomType#ALL_ROOM}, {@link RoomType#MEETING_ROOM}, {@link RoomType#EDU_ROOM}, {@link RoomType#HALL}
	 * @param approvalStatus {@link ApprovalStatus#REQUEST}, {@link ApprovalStatus#APPROVED}, {@link ApprovalStatus#CANCEL}, {@link ApprovalStatus#REJECT}
	 * @param startDate 기간 검색을 위한 시작일자(yyyy-MM-dd)
	 * @param endDate 기간 검색을 위한 종료일자(yyyy-MM-dd)
	 * @param host 회의 주관자. 조회시 동일 검색이 아닌 포함 검색 적용
	 * @param pageNo 페이지 번호
	 * @return
	 */
	@GetMapping("/manager/approval/manage/meeting")
	public String viewMeetingApprovalPage(Model model, 
			@RequestParam @Nullable RoomType roomType,
			@RequestParam @Nullable ApprovalStatus approvalStatus,
			@RequestParam @Nullable String startDate, 
			@RequestParam @Nullable String endDate, 
			@RequestParam @Nullable String host,
			@RequestParam @Nullable Integer pageNo
	) {
		EwpAuthenticationDetails userDetails = EwpSecurityUtil.getAuthenticationDetails();
		String loginId = userDetails.getUserId();
		Map<String, String> officeBook = codeServ.getOfficeBook();
		if(roomType == null || roomType == RoomType.ALL_ROOM) {
			if(userDetails.hasPosition(ManagerRole.MEETING_ROOM_MANAGER)) {
				roomType = RoomType.MEETING_ROOM;
			}else if(userDetails.hasPosition(ManagerRole.EDU_ROOM_MANAGER)) {
				roomType = RoomType.EDU_ROOM;
			}else if(userDetails.hasPosition(ManagerRole.HALL_MANAGER)) {
				roomType = RoomType.HALL;
			}
		}
		if(!userDetails.hasRole(DomainRole.DEV)) {
			List<AdminRosterVO> roomManagerList = rosterServ.getAllRoomManagerList(loginId);
			if(roomManagerList.isEmpty()) {
				throw new AccessDeniedException("사용신청 결재 권한이 없습니다.");
			}
			ManagerRole role = ManagerRole.getRoomManagerRole(roomType);
			Set<String> officeCodeSet = roomManagerList.stream().filter(entry -> entry.getManagerRole() == role).map(AdminRosterVO::getOfficeCode).collect(Collectors.toSet());
			officeBook = officeBook.entrySet().stream().filter(entry -> officeCodeSet.contains(entry.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		}
		model.addAttribute("officeBook", officeBook);
		model.addAttribute("approvalStatus", approvalStatus);
		model.addAttribute("startDate", DateTimeUtil.toFormattedDate(startDate));
		model.addAttribute("endDate", DateTimeUtil.toFormattedDate(endDate));
		model.addAttribute("roomType", roomType);
		model.addAttribute("host", host);
		model.addAttribute("pageNo", pageNo);
		return "/admin/manage/approval_manage";
	}
	
	/**
	 * 사용신청 스케줄에대한 결재 페이지
	 * @param authentication
	 * @param model
	 * @param skdKey
	 * @return
	 */
	@GetMapping("/manager/approval/meeting/assign/{skdKey}")
	public String approvalRoomAssignForm(Authentication authentication, Model model, @PathVariable Integer skdKey) {
		Optional<EwpMeetingScheduleVO> opt = scheServ.getMeetingScheduleOne(skdKey);
		if(!opt.isPresent()) {
			throw new ApiNotFoundException(ErrorCode.MEETING_ASSIGN.NOT_FOUND);
		}
		EwpMeetingScheduleVO scheVO = opt.get();
		ResourceAuthorityCollection authorityCollection = authorityProvider.getAssignApprovalAuthorityCollection(skdKey);
		if(authorityCollection.hasAuthority(MeetingAuth.VIEW)) {
			model.addAttribute("authorityCollection", authorityCollection);
			model.addAttribute("officeList", codeServ.getOfficeBook());
			model.addAttribute("skdKey", skdKey);
			return "/meeting/assign/assign_approval";
		}else {
			throw new ApiDataAccessDeniedException(ErrorCode.MEETING_APPROVAL.APPROVAL_NO_AUTHORITY);
		}
	}
}
