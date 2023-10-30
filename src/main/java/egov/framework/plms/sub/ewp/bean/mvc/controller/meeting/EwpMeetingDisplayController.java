package egov.framework.plms.sub.ewp.bean.mvc.controller.meeting;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.ws.rs.NotFoundException;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import egov.framework.plms.sub.ewp.bean.mvc.entity.room.EwpRoomInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.service.common.EwpCodeService;
import egov.framework.plms.sub.ewp.bean.mvc.service.room.EwpMariaRoomInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * 현황판 페이지 요청 컨트롤러
 * @author mckim
 * @version 1.0
 * @since 2023. 5. 4
 */
@Slf4j
@Controller
@RequestMapping("/ewp")
@RequiredArgsConstructor
@Profile("ewp")
public class EwpMeetingDisplayController {
	private final EwpCodeService codeServ;
	private final EwpMariaRoomInfoService rmServ;
	@GetMapping("/display")
	public String roomDisplayHome(Model model) {
		model.addAttribute("officeList", codeServ.getOfficeBook());
		return "/meeting/display/displayHome";
	}
	
	/**
	 * 사업소별 회의실 현황판
	 * @param model
	 * @param officeCode
	 * @return
	 */
	@GetMapping("/display/{officeCode}/main")
	public String roomDisplay(Model model, @PathVariable String officeCode) {
		model.addAttribute("officeCode", officeCode);
		LocalDateTime nowDT = LocalDateTime.now();
		model.addAttribute("nowDT", nowDT);
		return "/meeting/display/displayMain";
	}
	
	/** 회의실 별 이용현황
	 * @param model
	 * @param officeCode
	 * @param roomType
	 * @param roomKey
	 * @return
	 */
	@GetMapping("/display/{officeCode}/{roomType}/{roomKey}")
	public String roomMeetingDetail(Model model, @PathVariable String officeCode, @PathVariable RoomType roomType, @PathVariable Integer roomKey) {
		log.info("회의실 별 이용현황 요청- 사업소: {}, 분류: {}, 장소키: {}", officeCode, roomType, roomKey);
		Optional<EwpRoomInfoVO> opt = rmServ.selectRoomOne(roomType, roomKey);
		if(!opt.isPresent() || opt.get().getDelYN() == 'Y') {
			throw new NotFoundException("삭제되거나 등록되지 않은 장소는 조회할 수 없습니다.");
		}
		EwpRoomInfoVO roomVO = opt.get();
		log.info("회의실 별 이용현황 요청- 장소: {}", roomVO);
		model.addAttribute("officeCode", officeCode);
		model.addAttribute("roomType", roomType);
		model.addAttribute("roomKey", roomKey);
		model.addAttribute("roomName", roomVO.getRoomName() );
		LocalDateTime nowDT = LocalDateTime.now();
		model.addAttribute("nowDT", nowDT);
		return "/meeting/display/displayDetail";
	}
}
