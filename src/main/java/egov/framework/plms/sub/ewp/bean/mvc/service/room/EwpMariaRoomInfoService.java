package egov.framework.plms.sub.ewp.bean.mvc.service.room;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.mvc.service.room.abst.RoomInfoAbstractService;
import egov.framework.plms.main.core.exception.ApiDataDisabledException;
import egov.framework.plms.main.core.exception.ApiIllegalArgumentException;
import egov.framework.plms.main.core.exception.ApiNotFoundException;
import egov.framework.plms.main.core.model.enums.error.ErrorCode;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import egov.framework.plms.main.core.util.CommUtil;
import egov.framework.plms.sub.ewp.bean.mvc.entity.organization.EwpDeptInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.room.EwpRoomInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.room.EwpRoomPermissionVO;
import egov.framework.plms.sub.ewp.bean.mvc.mapper.maria.room.EwpRoomInfoMapper;
import egov.framework.plms.sub.ewp.bean.mvc.mapper.maria.room.EwpRoomPermissionMapper;
import egov.framework.plms.sub.ewp.bean.mvc.mapper.oracle.organization.EwpDeptInfoMapper;
import egov.framework.plms.sub.ewp.bean.mvc.mapper.tibero.room.EwpTiberoRoomMapper;
import egov.framework.plms.sub.ewp.bean.mvc.service.common.EwpCodeService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile("ewp")
@Primary
public class EwpMariaRoomInfoService extends RoomInfoAbstractService<EwpRoomInfoVO>{
	@Autowired
	private EwpCodeService codeServ;
	private EwpRoomInfoMapper mariaRmMapper;
	@Autowired
	private EwpTiberoRoomMapper tiberoRmMapper;
	@Autowired
	private EwpRoomPermissionMapper permMapper;
	@Autowired
	private EwpDeptInfoMapper deptMapper;
	
	public EwpMariaRoomInfoService(@Autowired EwpRoomInfoMapper mapper) {
		super(mapper);
		this.mariaRmMapper = mapper;
	}
	
	protected void validateRoomInfoForInsert(EwpRoomInfoVO params) {
		super.validateRoomInfoForInsert(params);
		validateRoomType(params.getRoomType());
		validateOfficeCode(params.getOfficeCode());
	}
	
	protected void validateRoomInfoForUpdate(EwpRoomInfoVO params) {
		super.validateRoomInfoForUpdate(params);
		if(params.getRoomType() != null) {
			validateRoomType(params.getRoomType());
		}
	}
	
	private void validateRoomType(RoomType value) {
		if(value == null) {
			throw new ApiIllegalArgumentException(ErrorCode.ROOM.EMPTY_TYPE);
		}
		if(value == RoomType.ALL_ROOM) {
			throw new ApiIllegalArgumentException(ErrorCode.ROOM.INVALID_TYPE);
		}
	}
	
	private void validateOfficeCode(String value) {
		if(value == null) {
			throw new ApiIllegalArgumentException(ErrorCode.ROOM.EMPTY_OFFICE);
		}
		if(!codeServ.isEntriedOffice(value)) {
			throw new ApiIllegalArgumentException(ErrorCode.ROOM.INVALID_OFFICE);
		}
	}
	
	@Override
	public boolean updateRoomOne(EwpRoomInfoVO params) {
		Optional<EwpRoomInfoVO> roomOpt = this.selectRoomOne(params.getRoomType(), params.getRoomKey());
		if(!roomOpt.isPresent()) {
			throw new ApiNotFoundException(ErrorCode.ROOM.NOT_FOUND);
		}
		EwpRoomInfoVO roomVO = roomOpt.get();
		if(roomVO.getSyncYN() == 'Y') {
			super.validateRoomInfoForUpdate(params);
			try {
				switch(roomVO.getRoomType()) {
					case MEETING_ROOM:
						tiberoRmMapper.updateMeetingRoomOne(params);
						break;
					case EDU_ROOM:
						tiberoRmMapper.updateEduRoomOne(params);
						break;
					case HALL:
						tiberoRmMapper.updateHallOne(params);
						break;
				}
			}catch(Exception e) {
				log.error("Failed to put Sync RoomInfo with params: {}", params);
				log.error("Failed to put Sync RoomInfo messages: {}", e.toString());
				e.printStackTrace();
				return false;
			}
		}
		return super.updateRoomOne(params);
	}
	
	@Deprecated
	public boolean updateRoomOneToDelete(Integer roomId) {
		return false;
	}
	
	public boolean updateRoomOneToDelete(RoomType roomType, Integer roomKey) {
		Optional<EwpRoomInfoVO> roomOpt = this.selectRoomOne(roomType, roomKey);
		if(!roomOpt.isPresent()) {
			throw new ApiNotFoundException(ErrorCode.ROOM.NOT_FOUND);
		}
		EwpRoomInfoVO roomVO = roomOpt.get();
		if(roomVO.getSyncYN() == 'Y') {
			try {
				switch(roomVO.getRoomType()) {
					case MEETING_ROOM:
						tiberoRmMapper.updateMeetingRoomOneToDelete(roomVO);
						break;
					case EDU_ROOM:
						tiberoRmMapper.updateMeetingRoomOneToDelete(roomVO);
						break;
					case HALL:
						tiberoRmMapper.updateMeetingRoomOneToDelete(roomVO);
						break;
				}
			}catch(Exception e) {
				log.error("Failed to update one Sync RoomInfo to delete with params: {}", roomVO);
				log.error("Failed to update one Sync RoomInfo to delete messages: {}", e.toString());
				e.printStackTrace();
				return false;
			}
		}
		try {
			Integer result = mariaRmMapper.updateRoomOneToDelete(roomVO);
			return (result == 0)?false:true;
		}catch(Exception e) {
			log.error("Failed to update one RoomInfo to delete with params: {}", roomVO);
			log.error("Failed to update one RoomInfo to delete messages: {}", e.toString());
			e.printStackTrace();
			return false;
		}
	}
	
	@Deprecated
	public Optional<EwpRoomInfoVO> selectRoomOne(Integer roomId) {
		return Optional.empty();
	}
	
	public Optional<EwpRoomInfoVO> selectRoomOne(RoomType roomType, Integer roomId){
		EwpRoomInfoVO room = mariaRmMapper.selectRoomOne(EwpRoomInfoVO.builder().roomType(roomType).roomKey(roomId).build());
		return Optional.ofNullable(room);
	}
	
	/**
	 * 장소 대여 가능 부서 등록
	 * @param List<EwpRoomPermissionDTO>
	 * @return ResponseMessage
	 */
	public boolean postRoomPermissionList(List<EwpRoomPermissionVO> params) {
		params = params.stream().map(entry -> {
			List<String> subDeptList = deptMapper.selectRecursiveSubDeptInfoList(entry.getDeptId()).stream().map(EwpDeptInfoVO::getDeptId).collect(Collectors.toList());
			return entry.toBuilder().subDeptList(subDeptList).build();
		}).collect(Collectors.toList());
		try {
			Integer result = permMapper.postRoomPermissionList(params);
			return (result == 0)?false:true;
		}catch(Exception e) {
			log.error("Failed to post list RoomPermission with params: {}", params.toString());
			log.error("Failed to post list RoomPermission messages: {}", e.toString());
			return false;
		}
	}
	
	/**
	 * 장소 대여 가능 부서 삭제
	 * @param EwpRoomPermissionVO
	 * @return ResponseMessage
	 */
	public boolean deleteRoomPermissionAll(EwpRoomPermissionVO params) {
		try {
			Integer result = permMapper.deleteRoomPermissionAll(params);
			return true;
		}catch(Exception e) {
			log.error("Failed to delete one RoomPermission with params: {}", params.toString());
			log.error("Failed to delete one RoomPermission messages: {}", e.toString());
			return false;
		}
	}
	
	/**
	 * 장소 대여 가능 부서 조회
	 * @param EwpRoomPermissionVO
	 * @return List<EwpRoomPermissionVO>
	 */
	public List<EwpRoomPermissionVO> getRoomPermissionList(EwpRoomPermissionVO param) {
		return permMapper.getRoomPermissionList(param);
	}
	
	/**
	 * 해당 부서가 사용신청 가능한 장소인지 판별
	 * @param deptId 부서코드
	 * @param roomType 장소유형
	 * @param roomKey 장소키
	 * @return
	 */
	public boolean isRoomPermitted(String deptId, RoomType roomType, Integer roomKey) throws ApiNotFoundException, ApiDataDisabledException{
		Optional<EwpRoomInfoVO> roomOpt = this.selectRoomOne(roomType, roomKey);
		if(!roomOpt.isPresent()) {
			throw new ApiNotFoundException(ErrorCode.ROOM.NOT_FOUND);
		}
		EwpRoomInfoVO roomVO = roomOpt.get();
		if(roomVO.getDelYN() == 'Y') {
			throw new ApiNotFoundException(ErrorCode.ROOM.NOT_FOUND);
		}
		if(!roomVO.isEnable()) {
			throw new ApiDataDisabledException(ErrorCode.ROOM.DISABLED);
		}
		EwpRoomPermissionVO param = EwpRoomPermissionVO.builder().roomType(roomType).roomKey(roomKey).build();
		List<EwpRoomPermissionVO> permList = getRoomPermissionList(param);
		if(CommUtil.isEmpty(permList)) {
			return true;
		}else {
			for(EwpRoomPermissionVO perm : permList) {
				List<String> deptSet = perm.getSubDeptList();
				if(deptSet != null && deptSet.contains(deptId)) {
					return true;
				}
			}
			return false;
		}
	}
	
	/**
	 * 해당 장소목록중 해당 부서 사용자가 사용신청 가능한 장소만 필터링하여 반환
	 * @param deptId 소속 부서 고유키
	 * @param roomList 장소 목록
	 * @return 해당 부서 소속 사용자가 사용신청 가능한 장소 목록
	 */
	public List<EwpRoomInfoVO> getPermittedRoomList(String deptId, List<EwpRoomInfoVO> roomList) {
		return roomList;
	}
}
