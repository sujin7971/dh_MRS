package egov.framework.plms.main.bean.mvc.service.room.abst;

import java.util.List;
import java.util.Optional;

import egov.framework.plms.main.bean.mvc.entity.room.abst.RoomInfoModelVO;
import egov.framework.plms.main.bean.mvc.mapper.room.abst.RoomInfoAbstractMapper;
import egov.framework.plms.main.core.exception.ApiIllegalArgumentException;
import egov.framework.plms.main.core.exception.abst.ApiException;
import egov.framework.plms.main.core.model.enums.error.ErrorCode;
import egov.framework.plms.main.core.util.CommUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class RoomInfoAbstractService<T extends RoomInfoModelVO> {
	protected final RoomInfoAbstractMapper<T> mapper;
	
	public RoomInfoAbstractService(RoomInfoAbstractMapper<T> mapper) {
		this.mapper = mapper;
	}
	
	public boolean insertRoomOne(T params) {
		validateRoomInfoForInsert(params);
		try {
			Integer result = mapper.insertRoomOne(params);
			return (result == 0)?false:true;
		}catch(Exception e) {
			log.error("Failed to insert one RoomInfo with params: {}", params);
			log.error("Failed to insert one RoomInfo messages: {}", e.toString());
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean updateRoomOne(T params) {
		validateRoomInfoForUpdate(params);
		try {
			Integer result = mapper.updateRoomOne(params);
			return (result == 0)?false:true;
		}catch(Exception e) {
			log.error("Failed to update RoomInfo with params: {}", params);
			log.error("Failed to update RoomInfo messages: {}", e.toString());
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean updateRoomOneToDelete(Integer roomId) {
		try {
			Integer result = mapper.updateRoomOneToDelete(roomId);
			return (result == 0)?false:true;
		}catch(Exception e) {
			log.error("Failed to update one RoomInfo to delete with id: {}", roomId);
			log.error("Failed to update one RoomInfo to delete messages: {}", e.toString());
			e.printStackTrace();
			return false;
		}
	}
	
	public Optional<T> selectRoomOne(Integer roomId) {
		T room = mapper.selectRoomOne(roomId);
		return Optional.ofNullable(room);
	}
	
	public List<T> selectRoomList(T params){
		return mapper.selectRoomList(params);
	}
	
	protected void validateRoomInfoForInsert(T params) {
		try {
			validateRoomName(params.getRoomName());
			validateRoomDisable(params.getDisableYN());
			validateRoomDisableComment(params.getDisableComment());
			validateRoomNote(params.getRoomNote());
		}catch(ApiException e) {
			log.error("Error validation Room For Insert with params: {}, message: {}", params.toString(), e.getErrorCode().getMessage());
			throw e;
		}
	}
	
	protected void validateRoomInfoForUpdate(T params) {
		try {
			if(params.getRoomName() != null) {
				validateRoomName(params.getRoomName());
			}
			if(params.getDisableYN() != null) {
				validateRoomDisable(params.getDisableYN());
			}
			if(params.getDisableComment() != null) {
				validateRoomDisableComment(params.getDisableComment());
			}
			if(params.getRoomNote() != null) {
				validateRoomNote(params.getRoomNote());
			}
		}catch(ApiException e) {
			log.error("Error validation Room For Update with params: {}, message: {}", params.toString(), e.getErrorCode().getMessage());
			throw e;
		}
	}
	
	protected void validateRoomName(String value) {
		if(value == null) {
			throw new ApiIllegalArgumentException(ErrorCode.ROOM.EMPTY_NAME);
		}
		if(!CommUtil.isValidLength(value, 25)) {
			throw new ApiIllegalArgumentException(ErrorCode.ROOM.INVALID_NAME);
		}
	}
	
	protected void validateRoomDisable(Character value) {
		if(value == null) {
			throw new ApiIllegalArgumentException(ErrorCode.ROOM.EMPTY_DISABLE);
		}
		if(value != 'Y' && value != 'N') {
			throw new ApiIllegalArgumentException(ErrorCode.ROOM.INVALID_DISABLE);
		}
	}
	
	protected void validateRoomDisableComment(String value) {
		if(!CommUtil.isEmpty(value) && !CommUtil.isValidLength(value, 500)) {
			throw new ApiIllegalArgumentException(ErrorCode.ROOM.EMPTY_DISABLE);
		}
	}
	
	protected void validateRoomNote(String value) {
		if(!CommUtil.isEmpty(value) && !CommUtil.isValidLength(value, 150)) {
			throw new ApiIllegalArgumentException(ErrorCode.ROOM.EMPTY_DISABLE);
		}
	}
}
