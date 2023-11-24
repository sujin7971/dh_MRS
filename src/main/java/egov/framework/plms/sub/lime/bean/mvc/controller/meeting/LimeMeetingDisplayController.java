package egov.framework.plms.sub.lime.bean.mvc.controller.meeting;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.ws.rs.NotFoundException;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import egov.framework.plms.main.bean.mvc.entity.room.RoomInfoVO;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import egov.framework.plms.sub.ewp.bean.mvc.entity.room.EwpRoomInfoVO;
import egov.framework.plms.sub.lime.bean.mvc.service.room.LimeRoomInfoService;
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
@RequestMapping("/lime")
@RequiredArgsConstructor
@Profile("lime")
public class LimeMeetingDisplayController {
	private final LimeRoomInfoService rmServ;
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
	@GetMapping("/display/{roomId}")
	public String roomMeetingDetail(Model model, @PathVariable Integer roomId) {
		log.info("회의실 별 이용현황 요청- 장소키: {}", roomId);
		RoomInfoVO vo = RoomInfoVO.builder().roomId(roomId).build();
		Optional<RoomInfoVO> opt = rmServ.selectRoomOne(roomId);
		if(!opt.isPresent() || opt.get().getDelYN() == 'Y') {
			throw new NotFoundException("삭제되거나 등록되지 않은 장소는 조회할 수 없습니다.");
		}
		RoomInfoVO roomVO = opt.get();
		log.info("회의실 별 이용현황 요청- 장소: {}", roomVO);
		model.addAttribute("roomKey", roomId);
		model.addAttribute("roomName", roomVO.getRoomName() );
		LocalDateTime nowDT = LocalDateTime.now();
		model.addAttribute("nowDT", nowDT);
		return "/meeting/display/displayDetail";
	}
}
