package egov.framework.plms.sub.ewp.bean.mvc.service.room;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import egov.framework.plms.main.core.model.response.ErrorMessage;
import egov.framework.plms.main.core.model.response.ResponseMessage;
import egov.framework.plms.sub.ewp.bean.mvc.entity.room.EwpRoomInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.mapper.tibero.meeting.AllRoomReqMapper;
import egov.framework.plms.sub.ewp.bean.mvc.mapper.tibero.room.EwpTiberoRoomMapper;
import egov.framework.plms.sub.ewp.bean.mvc.service.common.EwpCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile("ewp")
@Primary
public class EwpTiberoRoomInfoService {
	private final EwpCodeService codeServ;
	private final EwpTiberoRoomMapper ewpRoomMapper;
	private final AllRoomReqMapper ewpRoomReqMapper;
	
	/**
	 * 회의실 강의실 강당 통합 검색 (사업소, 장소 구분, 대여가능여부 로 검색)
	 * @param EwpRoomInfoVO
	 * @return List<EwpRoomInfoVO>
	 */
	public List<EwpRoomInfoVO> selectRoomList() {
		return ewpRoomMapper.selectRoomList(EwpRoomInfoVO.builder().build());
	}
	
	public List<EwpRoomInfoVO> selectRoomList(EwpRoomInfoVO room) {
		return ewpRoomMapper.selectRoomList(room);
	}
	
	/**
	 * 경영지원서비스에 등록된 모든 장소 요청
	 */
	public List<EwpRoomInfoVO> selectAllRoomList() {
		return ewpRoomMapper.selectAllRoomList();
	}
	
/*        회의실                */
	/**
	 * 장소 분류 및 회의키로 회의실 검색
	 * @param roomType
	 * @param roomKey
	 * @return
	 */
	public EwpRoomInfoVO getRoomOne(RoomType roomType, Integer roomKey) {
		EwpRoomInfoVO roomVO = null;
		if(roomType != null) {
			switch(roomType) {
			case MEETING_ROOM:
				roomVO = ewpRoomMapper.selectMeetingRoomOne(roomKey);
				break;
			case EDU_ROOM:
				roomVO = ewpRoomMapper.selectEduRoomOne(roomKey);
				break;
			case HALL:
				roomVO = ewpRoomMapper.selectHallOne(roomKey);
				break;	
			}
			if(roomVO == null) {
				return null;
			}
			roomVO.setOfficeName(codeServ.getOfficeName(roomVO.getOfficeCode()));
		}
		return roomVO;
	}
	
	public ResponseMessage postRoom(RoomType roomType, EwpRoomInfoVO room) {
		switch(roomType) {
			case MEETING_ROOM:
				return insertMeetingRoom(room);
			case EDU_ROOM:
				return insertEduRoom(room);
			case HALL:
				return insertHall(room);
			default:
				return 	ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
						.message(ResponseMessage.MessageCode.ROOM.POST_FAIL.value())
						.build();
		}
	}
	
	public ResponseMessage putRoom(RoomType roomType, EwpRoomInfoVO room) {
		switch(roomType) {
			case MEETING_ROOM:
				return updateMeetingRoomOne(room);
			case EDU_ROOM:
				return updateEduRoomOne(room);
			case HALL:
				return updateHallOne(room);
			default:
				return 	ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
						.message(ResponseMessage.MessageCode.ROOM.PUT_FAIL.value())
						.build();
		}
	}
	
	public ResponseMessage deleteRoom(RoomType roomType, Integer roomKey, String userId) {
		EwpRoomInfoVO roomVO = EwpRoomInfoVO.builder().roomType(roomType).roomKey(roomKey).modUser(userId).build();
		switch(roomType) {
			case MEETING_ROOM:
				return updateMeetingRoomOneToDelete(roomVO);
			case EDU_ROOM:
				return updateEduRoomOneToDelete(roomVO);
			case HALL:
				return updateHallOneToDelete(roomVO);
			default:
				return 	ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
						.message(ResponseMessage.MessageCode.ROOM.DELETE_FAIL.value())
						.build();
		}
	}
	
	/**
	 * 고유키로 회의실 검색 (사업소 이름 포함)
	 * @param EwpRoomInfoVO
	 * @return EwpRoomInfoVO
	 */
	public EwpRoomInfoVO selectMeetingRoomOne(Integer roomKey) {
		EwpRoomInfoVO roomVO = ewpRoomMapper.selectMeetingRoomOne(roomKey);
		if(roomVO == null) {
			return null;
		}
		roomVO.setOfficeName(codeServ.getOfficeName(roomVO.getOfficeCode()));
		return roomVO;
	}
	
	/**
	 * 이용 가능한 회의실 목록 반환
	 * @param EwpRoomInfoVO
	 * @return List<EwpRoomInfoVO>
	 */
	public List<EwpRoomInfoVO> selectMeetingRoomList(EwpRoomInfoVO room) {
		return ewpRoomMapper.selectMeetingRoomList(room);
	}
	
	/**
	 * 회의실 등록
	 * @param EwpRoomInfoVO
	 * @return ResponseMessage
	 */
	@CacheEvict(value = "roomList", allEntries = true)
	public ResponseMessage insertMeetingRoom(EwpRoomInfoVO room) {
		Integer result = ewpRoomMapper.insertMeetingRoom(room);
		if(result == 1) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
					.message(ResponseMessage.MessageCode.ROOM.POST_SUCCESS.value())
					.build();
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
					.message(ResponseMessage.MessageCode.ROOM.POST_FAIL.value())
					.error(ErrorMessage.builder(ErrorMessage.ErrorCode.CONFLICT_VALUE)
							.message(ErrorMessage.MessageCode.ROOM.CONFLICT_NAME.value())
							.build())
					.build();
		}
	}
	
	/**
	 * 회의실 정보 수정
	 * @param EwpRoomInfoVO
	 * @return ResponseMessage
	 */
	@CacheEvict(value = "roomList", allEntries = true)
	public ResponseMessage updateMeetingRoomOne(EwpRoomInfoVO room) {
		Integer result = ewpRoomMapper.updateMeetingRoomOne(room);
		if(result == 1) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
					.message(ResponseMessage.MessageCode.ROOM.PUT_SUCCESS.value())
					.build();
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
					.message(ResponseMessage.MessageCode.ROOM.PUT_FAIL.value())
					.error(ErrorMessage.builder(ErrorMessage.ErrorCode.CONFLICT_VALUE)
							.message(ErrorMessage.MessageCode.ROOM.CONFLICT_NAME.value())
							.build())
					.build();
		}
	}
	
	/**
	 * 회의실 삭제
	 * @param EwpRoomInfoVO
	 * @return ResponseMessage
	 */
	@CacheEvict(value = "roomList", allEntries = true)
	public ResponseMessage updateMeetingRoomOneToDelete(EwpRoomInfoVO room) {
//		회의실 존재 여부 체크
		EwpRoomInfoVO roomVO = selectMeetingRoomOne(room.getRoomKey());
		if(roomVO == null) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.NOT_FOUND)
					.message(ResponseMessage.MessageCode.ROOM.DELETE_FAIL.value())
					.detail(ResponseMessage.DetailCode.ROOM.NOT_FOUND.value())
					.build();
		}
//		회의실 예약 현황 체크
		Integer count = ewpRoomReqMapper.getMeetingRoomReqCount(room.getRoomKey());
		if(count != null && count != 0) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.FORBIDDEN)
					.message(ResponseMessage.MessageCode.ROOM.DELETE_FAIL.value())
					.detail(ResponseMessage.DetailCode.ROOM.PROTECTED_ENTITY.value())
					.build();
		}
//		회의실 삭제
		Integer result = ewpRoomMapper.updateMeetingRoomOneToDelete(room);
		if(result == 1) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
					.message(ResponseMessage.MessageCode.ROOM.DELETE_SUCCESS.value())
					.data(room.getRoomKey())
					.build();
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.UNPROCESSABLE_ENTITY)
					.message(ResponseMessage.MessageCode.ROOM.DELETE_FAIL.value())
					.detail(ResponseMessage.DetailCode.ROOM.UNPROCESSABLE_ENTITY.value())
					.data(room.getRoomKey())
					.build();
		}
	}
	
/*        강의실                */
	/**
	 * 강의실 고유 키로 강의실 조회
	 * @param roomKey
	 * @return EwpRoomInfoVO
	 */
	public EwpRoomInfoVO selectEduRoomOne(Integer roomKey) {
		EwpRoomInfoVO roomVO = ewpRoomMapper.selectEduRoomOne(roomKey);
		if(roomVO == null) {
			return null;
		}
		roomVO.setOfficeName(codeServ.getOfficeName(roomVO.getOfficeCode()));
		return roomVO;
	}
	
	/**
	 * 이용 가능한 강의실 목록 반환
	 * @param EwpRoomInfoVO
	 * @return List<EwpRoomInfoVO>
	 */
	public List<EwpRoomInfoVO> selectEduRoomList(EwpRoomInfoVO room) {
		return ewpRoomMapper.selectEduRoomList(room);
	}
	
	/**
	 * 강의실 등록
	 * @param EwpRoomInfoVO
	 * @return ResponseMessage
	 */
	@CacheEvict(value = "roomList", allEntries = true)
	public ResponseMessage insertEduRoom(EwpRoomInfoVO room) {
		Integer result = ewpRoomMapper.insertEduRoom(room);
		if(result == 1) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
					.message(ResponseMessage.MessageCode.ROOM.POST_SUCCESS.value())
					.build();
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
					.message(ResponseMessage.MessageCode.ROOM.POST_FAIL.value())
					.error(ErrorMessage.builder(ErrorMessage.ErrorCode.CONFLICT_VALUE)
							.message(ErrorMessage.MessageCode.ROOM.CONFLICT_NAME.value())
							.build())
					.build();
		}
	}
	
	/**
	 * 강의실 정보 수정
	 * @param EwpRoomInfoVO
	 * @return ResponseMessage
	 */
	@CacheEvict(value = "roomList", allEntries = true)
	public ResponseMessage updateEduRoomOne(EwpRoomInfoVO room) {
		Integer result = ewpRoomMapper.updateEduRoomOne(room);
		if(result == 1) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
					.message(ResponseMessage.MessageCode.ROOM.PUT_SUCCESS.value())
					.build();
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
					.message(ResponseMessage.MessageCode.ROOM.PUT_FAIL.value())
					.error(ErrorMessage.builder(ErrorMessage.ErrorCode.CONFLICT_VALUE)
							.message(ErrorMessage.MessageCode.ROOM.CONFLICT_NAME.value())
							.build())
					.build();
		}
	}
	
	/**
	 * 강의실 삭제
	 * @param EwpRoomInfoVO
	 * @return ResponseMessage
	 */
	@CacheEvict(value = "roomList", allEntries = true)
	public ResponseMessage updateEduRoomOneToDelete(EwpRoomInfoVO room) {
//		강의실 존재 여부 체크
		EwpRoomInfoVO roomVO = selectEduRoomOne(room.getRoomKey());
		if(roomVO == null) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.NOT_FOUND)
					.message(ResponseMessage.MessageCode.ROOM.DELETE_FAIL.value())
					.detail(ResponseMessage.DetailCode.ROOM.NOT_FOUND.value())
					.build();
		}
//		강의실 예약 목록 조회
		Integer count = ewpRoomReqMapper.getEduRoomReqCount(room.getRoomKey());
		if(count != null && count != 0) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.FORBIDDEN)
					.message(ResponseMessage.MessageCode.ROOM.DELETE_FAIL.value())
					.detail(ResponseMessage.DetailCode.ROOM.PROTECTED_ENTITY.value())
					.build();
		}
//		강의실 삭제
		Integer result = ewpRoomMapper.updateEduRoomOneToDelete(room);
		if(result == 1) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
					.message(ResponseMessage.MessageCode.ROOM.DELETE_SUCCESS.value())
					.data(room.getRoomKey())
					.build();
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.UNPROCESSABLE_ENTITY)
					.message(ResponseMessage.MessageCode.ROOM.DELETE_FAIL.value())
					.detail(ResponseMessage.DetailCode.ROOM.UNPROCESSABLE_ENTITY.value())
					.data(room.getRoomKey())
					.build();
		}
	}
	
/*        강당                */
	/**
	 * 장소키로 강당 1개 조회 (사업소 명 포함)
	 * @param roomKey
	 * @return EwpRoomInfoVO
	 */
	public EwpRoomInfoVO selectHallOne(Integer roomKey) {
		EwpRoomInfoVO roomVO = ewpRoomMapper.selectHallOne(roomKey);
		if(roomVO == null) {
			return null;
		}
		roomVO.setOfficeName(codeServ.getOfficeName(roomVO.getOfficeCode()));
		return roomVO;
	}
	
	/**
	 * 이용 가능한 강당 목록 반환
	 * @param EwpRoomInfoVO
	 * @return List<EwpRoomInfoVO>
	 */
	public List<EwpRoomInfoVO> selectHallList(EwpRoomInfoVO room) {
		return ewpRoomMapper.selectHallList(room);
	}
	
	/**
	 * 강당 등록
	 * @param EwpRoomInfoVO
	 * @return ResponseMessage
	 */
	@CacheEvict(value = "roomList", allEntries = true)
	public ResponseMessage insertHall(EwpRoomInfoVO room) {
		Integer result = ewpRoomMapper.insertHall(room);
		if(result == 1) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
					.message(ResponseMessage.MessageCode.ROOM.POST_SUCCESS.value())
					.build();
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
					.message(ResponseMessage.MessageCode.ROOM.POST_FAIL.value())
					.error(ErrorMessage.builder(ErrorMessage.ErrorCode.CONFLICT_VALUE)
							.message(ErrorMessage.MessageCode.ROOM.CONFLICT_NAME.value())
							.build())
					.build();
		}
	}
	
	/**
	 * 강당 정보 수정
	 * @param EwpRoomInfoVO
	 * @return ResponseMessage
	 */
	@CacheEvict(value = "roomList", allEntries = true)
	public ResponseMessage updateHallOne(EwpRoomInfoVO room) {
		Integer result = ewpRoomMapper.updateHallOne(room);
		if(result == 1) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
					.message(ResponseMessage.MessageCode.ROOM.PUT_SUCCESS.value())
					.build();
		} else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
					.message(ResponseMessage.MessageCode.ROOM.PUT_FAIL.value())
					.error(ErrorMessage.builder(ErrorMessage.ErrorCode.CONFLICT_VALUE)
							.message(ErrorMessage.MessageCode.ROOM.CONFLICT_NAME.value())
							.build())
					.build();
		}
	}
	
	/**
	 * 강당 삭제
	 * @param EwpRoomInfoVO
	 * @return ResponseMessage
	 */
	@CacheEvict(value = "roomList", allEntries = true)
	public ResponseMessage updateHallOneToDelete(EwpRoomInfoVO room) {
//		강당 존재 여부 체크
		EwpRoomInfoVO roomVO = selectHallOne(room.getRoomKey());
		if(roomVO == null) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.NOT_FOUND)
					.message(ResponseMessage.MessageCode.ROOM.DELETE_FAIL.value())
					.detail(ResponseMessage.DetailCode.ROOM.NOT_FOUND.value())
					.build();
		}
//		해당 장소 예약 리스트 체크
		Integer count = ewpRoomReqMapper.getHallReqCount(room.getRoomKey());
		if(count != null && count != 0) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.FORBIDDEN)
					.message(ResponseMessage.MessageCode.ROOM.DELETE_FAIL.value())
					.detail(ResponseMessage.DetailCode.ROOM.PROTECTED_ENTITY.value())
					.build();
		}
//		강당 삭제
		Integer result = ewpRoomMapper.updateHallOneToDelete(room);
		if(result == 1) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
					.message(ResponseMessage.MessageCode.ROOM.DELETE_SUCCESS.value())
					.data(room.getRoomKey())
					.build();
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.UNPROCESSABLE_ENTITY)
					.message(ResponseMessage.MessageCode.ROOM.DELETE_FAIL.value())
					.detail(ResponseMessage.DetailCode.ROOM.UNPROCESSABLE_ENTITY.value())
					.data(room.getRoomKey())
					.build();
		}
	}
}
