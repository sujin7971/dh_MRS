package egov.framework.plms.sub.ewp.bean.mvc.controller.room;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import egov.framework.plms.main.core.exception.abst.ApiException;
import egov.framework.plms.main.core.model.enums.error.ErrorCode;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import egov.framework.plms.main.core.model.enums.user.ManagerRole;
import egov.framework.plms.main.core.model.response.ErrorMessage;
import egov.framework.plms.main.core.model.response.ResponseMessage;
import egov.framework.plms.main.core.model.response.ResponseMessage.StatusCode;
import egov.framework.plms.main.core.util.CommUtil;
import egov.framework.plms.sub.ewp.bean.mvc.entity.organization.EwpDeptInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.room.EwpRoomInfoDTO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.room.EwpRoomInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.room.EwpRoomPermissionDTO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.room.EwpRoomPermissionVO;
import egov.framework.plms.sub.ewp.bean.mvc.service.organization.EwpDeptInfoService;
import egov.framework.plms.sub.ewp.bean.mvc.service.room.EwpMariaRoomInfoService;
import egov.framework.plms.sub.ewp.core.util.EwpSecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/ewp")
@RequiredArgsConstructor
@Profile("ewp")
public class EwpRoomRestController {
	private final EwpMariaRoomInfoService mariaRmServ;
	private final EwpDeptInfoService deptServ;
	
	/**
	 * 장소 등록
	 * @param roomDTO
	 * @return
	 */
	@PostMapping("/admin/system/room")
	public ResponseMessage insertRoomOne(EwpRoomInfoDTO roomDTO) {
		String userKey = EwpSecurityUtil.getLoginId();
		roomDTO.setRegUser(userKey);
		try {
			EwpRoomInfoVO params = roomDTO.convert();
			boolean result = mariaRmServ.insertRoomOne(params);
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
	public ResponseMessage updateRoomOne(EwpRoomInfoDTO roomDTO) {
		String userKey = EwpSecurityUtil.getLoginId();
		roomDTO.setModUser(userKey);
		try {
			boolean result = mariaRmServ.updateRoomOne(roomDTO.convert());
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
	
	@DeleteMapping("/admin/system/room/{roomType}/{roomKey}")
	public ResponseMessage updateRoomOneToDelete(@PathVariable RoomType roomType, @PathVariable Integer roomKey) {
		try {
			boolean result = mariaRmServ.updateRoomOneToDelete(roomType, roomKey);
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
	
	/**
	 * 회의실 대여 가능 부서 등록
	 * @param room
	 * @return 
	 * @return
	 */
	@PostMapping("/admin/system/room/permission/list")
	public ResponseMessage postRoomPermissionList(@RequestBody List<EwpRoomPermissionDTO> permissionDTOList) {
		boolean result = mariaRmServ.postRoomPermissionList(permissionDTOList.stream().map(EwpRoomPermissionDTO::convert).collect(Collectors.toList()));
		if(result) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
					.message(ResponseMessage.MessageCode.ROOM.POST_SUCCESS.value())
					.build();
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
					.message(ResponseMessage.MessageCode.ROOM.POST_FAIL.value())
					.error(ErrorMessage.builder(ErrorMessage.ErrorCode.CONFLICT_VALUE)
							.message(ErrorMessage.MessageCode.ROOM.UNPROCESSABLE_ENTITY.value())
							.build())
					.build();
		}
	}
	
	/**
	 * 회의실 대여 가능 부서 삭제
	 * @param roomKey
	 * @return
	 */
	@DeleteMapping("/admin/system/room/{roomType}/{roomKey}/permission/all")
	public ResponseMessage deleteRoomPermission(@PathVariable RoomType roomType, @PathVariable Integer roomKey) {
		boolean result = mariaRmServ.deleteRoomPermissionAll(EwpRoomPermissionVO.builder().roomType(roomType).roomKey(roomKey).build());
		if(result) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
					.message(ResponseMessage.MessageCode.ROOM.DELETE_SUCCESS.value())
					.data(roomKey)
					.build();
		} else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.UNPROCESSABLE_ENTITY)
					.message(ResponseMessage.MessageCode.ROOM.DELETE_FAIL.value())
					.detail(ResponseMessage.DetailCode.ROOM.UNPROCESSABLE_ENTITY.value())
					.data(roomKey)
					.build();
		}
	}
	
	
	@GetMapping("/room/{roomType}/{roomKey}")
	public EwpRoomInfoDTO selectRoomOne(@PathVariable RoomType roomType, @PathVariable Integer roomKey) {
		return mariaRmServ.selectRoomOne(roomType, roomKey).map(EwpRoomInfoVO::convert).orElse(null);
	}
	
	/**
	 * 이용가능한 회의실/강의실/강당 목록 요청.
	 * 
	 * @return 이용가능한 회의실 목록
	 */
	@GetMapping("/room/{roomType}/rentable/list")
	public List<EwpRoomInfoDTO> getRentableRoomList(@PathVariable RoomType roomType, @RequestParam String officeCode) {
		if(roomType == RoomType.ALL_ROOM) {
			roomType = null;
		}
		List<EwpRoomInfoVO> voList = mariaRmServ.selectRoomList(EwpRoomInfoVO.builder().officeCode(officeCode).roomType(roomType).rentYN('Y').delYN('N').build());
		if(!EwpSecurityUtil.hasPosition(ManagerRole.getRoomManagerRole(roomType))) {
			String deptId = EwpSecurityUtil.getDeptId();
			voList = voList.stream().filter((roomVO) -> {
				return mariaRmServ.isRoomPermitted(deptId, roomVO.getRoomType(), roomVO.getRoomKey());
			}).collect(Collectors.toList());
		}
		return voList.stream().map(EwpRoomInfoVO::convert).map(EwpRoomInfoDTO::filterForEssential).collect(Collectors.toList());
	}
	
	/**
	 * 미인증 사용자의 장소 목록 조회
	 * @param roomType
	 * @param officeCode
	 * @return
	 */
	@GetMapping("/public/room/{roomType}/display/list")
	public List<EwpRoomInfoDTO> getRoomPublicListForDisplay(@PathVariable RoomType roomType, @RequestParam String officeCode) {
		if(roomType == RoomType.ALL_ROOM) {
			roomType = null;
		}
		List<EwpRoomInfoVO> voList = mariaRmServ.selectRoomList(EwpRoomInfoVO.builder().officeCode(officeCode).roomType(roomType).rentYN('Y').delYN('N').build());
		return voList.stream().map(EwpRoomInfoVO::convert).map(EwpRoomInfoDTO::filterForEssential).collect(Collectors.toList());
	}
	
	/**
	 * 이용가능한 회의실/강의실/강당 목록 요청.
	 * 
	 * @return 이용가능한 회의실 목록
	 */
	@GetMapping("/admin/system/room/{roomType}/list")
	public List<EwpRoomInfoDTO> selectRoomList(
			@PathVariable RoomType roomType, 
			@RequestParam @Nullable String officeCode,
			@RequestParam @Nullable Character rentYN,
			@RequestParam @Nullable Character delYN
		) {
		if(roomType == RoomType.ALL_ROOM) {
			roomType = null;
		}
		List<EwpRoomInfoVO> voList = mariaRmServ.selectRoomList(EwpRoomInfoVO.builder().officeCode(officeCode).roomType(roomType).rentYN(rentYN).delYN(delYN).build());
		return voList.stream().map(EwpRoomInfoVO::convert).collect(Collectors.toList());
	}
	
	/**
	 * 회의실 대여 가능 부서 조회
	 * @param room
	 * @return 
	 * @return
	 */
	@GetMapping("/admin/system/room/{roomType}/{roomKey}/permission/list")
	public List<EwpRoomPermissionDTO> getRoomPermission(@PathVariable RoomType roomType, @PathVariable Integer roomKey) {
		List<EwpRoomPermissionVO> voList = mariaRmServ.getRoomPermissionList(EwpRoomPermissionVO.builder().roomType(roomType).roomKey(roomKey).build());
		return voList.stream().map(EwpRoomPermissionVO::convert).collect(Collectors.toList());
	}
}
