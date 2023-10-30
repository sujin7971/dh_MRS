package egov.framework.plms.sub.lime.bean.mvc.controller.room;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import egov.framework.plms.main.bean.mvc.entity.room.RoomInfoDTO;
import egov.framework.plms.main.bean.mvc.entity.room.RoomInfoVO;
import egov.framework.plms.main.core.exception.abst.ApiException;
import egov.framework.plms.main.core.model.enums.error.ErrorCode;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import egov.framework.plms.main.core.model.response.ResponseMessage;
import egov.framework.plms.main.core.model.response.ResponseMessage.StatusCode;
import egov.framework.plms.sub.lime.bean.mvc.service.room.LimeRoomInfoService;
import egov.framework.plms.sub.lime.core.util.LimeSecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/lime")
@RequiredArgsConstructor
@Profile("lime")
public class LimeRoomRestController {
	private final LimeRoomInfoService rmServ;
	
	/**
	 * 장소 등록
	 * @param roomDTO
	 * @return
	 */
	@PostMapping("/admin/system/room")
	public ResponseMessage insertRoomOne(RoomInfoDTO roomDTO) {
		String loginId = LimeSecurityUtil.getLoginId();
		roomDTO.setWriterId(loginId);
		try {
			RoomInfoVO params = roomDTO.convert();
			boolean result = rmServ.insertRoomOne(params);
			if(result) {
				return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
						.data(params).build();
			}else {
				return ResponseMessage.builder(StatusCode.UNPROCESSABLE_ENTITY)
						.message(ErrorCode.ROOM.INFO_POST_FAILED.getMessage()).build()
						.detail(ErrorCode.SYSTEM_ERROR.getMessage());
			}
		}catch(ApiException e) {
			return ResponseMessage.builder(StatusCode.BAD_REQUEST)
					.message(ErrorCode.ROOM.INFO_POST_FAILED.getMessage())
					.detail(e.getErrorCode().getMessage())
					.build();
		}
	}
	
	/**
	 * 장소 정보 수정
	 * @param roomDTO
	 * @return
	 */
	@PutMapping("/admin/system/room")
	public ResponseMessage updateRoomOne(RoomInfoDTO roomDTO) {
		try {
			boolean result = rmServ.updateRoomOne(roomDTO.convert());
			if(result) {
				return ResponseMessage.builder(ResponseMessage.StatusCode.OK).build();
			}else {
				return ResponseMessage.builder(StatusCode.UNPROCESSABLE_ENTITY)
						.message(ErrorCode.ROOM.INFO_UPDATE_FAILED.getMessage()).build()
						.detail(ErrorCode.SYSTEM_ERROR.getMessage());
			}
		}catch(ApiException e) {
			return ResponseMessage.builder(StatusCode.BAD_REQUEST)
					.message(ErrorCode.ROOM.INFO_UPDATE_FAILED.getMessage())
					.detail(e.getErrorCode().getMessage())
					.build();
		}
	}
	
	@DeleteMapping("/admin/system/room/{roomId}")
	public ResponseMessage updateRoomOneToDelete(@PathVariable Integer roomId) {
		try {
			boolean result = rmServ.updateRoomOneToDelete(roomId);
			if(result) {
				return ResponseMessage.builder(ResponseMessage.StatusCode.OK).build();
			}else {
				return ResponseMessage.builder(StatusCode.UNPROCESSABLE_ENTITY)
						.message(ErrorCode.ROOM.INFO_DELETE_FAILED.getMessage())
						.detail(ErrorCode.SYSTEM_ERROR.getMessage()).build();
			}
		}catch(ApiException e) {
			return ResponseMessage.builder(StatusCode.BAD_REQUEST)
					.message(ErrorCode.ROOM.INFO_DELETE_FAILED.getMessage())
					.detail(e.getErrorCode().getMessage())
					.build();
		}
	}
	
	@GetMapping("/room/{roomId}")
	public RoomInfoDTO selectRoomOne(@PathVariable Integer roomId) {
		return rmServ.selectRoomOne(roomId).map(RoomInfoVO::convert).orElse(null);
	}
	
	/**
	 * 이용가능한 회의실/강의실/강당 목록 요청.
	 * 
	 * @return 이용가능한 회의실 목록
	 */
	@GetMapping("/room/rentable/list")
	public List<RoomInfoDTO> getRentableRoomList(
			@RequestParam @Nullable RoomType roomType
		) {
		List<RoomInfoVO> voList = rmServ.selectRoomList(RoomInfoVO.builder().roomType(roomType).disableYN('N').delYN('N').build());
		return voList.stream().map(RoomInfoVO::convert).map(RoomInfoDTO::filterForEssential).collect(Collectors.toList());
	}
	
	/**
	 * 미인증 사용자의 장소 목록 조회
	 * @param roomType
	 * @param officeCode
	 * @return
	 */
	@GetMapping("/public/room/display/list")
	public List<RoomInfoDTO> getRoomPublicListForDisplay(@RequestParam @Nullable RoomType roomType) {
		List<RoomInfoVO> voList = rmServ.selectRoomList(RoomInfoVO.builder().roomType(roomType).disableYN('N').delYN('N').build());
		return voList.stream().map(RoomInfoVO::convert).map(RoomInfoDTO::filterForEssential).collect(Collectors.toList());
	}
	
	/**
	 * 이용가능한 회의실/강의실/강당 목록 요청.
	 * 
	 * @return 이용가능한 회의실 목록
	 */
	@GetMapping("/admin/system/room/list")
	public List<RoomInfoDTO> selectRoomList(
			@RequestParam @Nullable RoomType roomType,
			@RequestParam @Nullable Character disableYN
		) {
		List<RoomInfoVO> voList = rmServ.selectRoomList(RoomInfoVO.builder().roomType(roomType).disableYN(disableYN).delYN('N').build());
		return voList.stream().map(RoomInfoVO::convert).collect(Collectors.toList());
	}
}
