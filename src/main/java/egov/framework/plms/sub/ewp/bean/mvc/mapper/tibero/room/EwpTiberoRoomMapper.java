package egov.framework.plms.sub.ewp.bean.mvc.mapper.tibero.room;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.transaction.annotation.Transactional;

import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import egov.framework.plms.sub.ewp.bean.mvc.entity.room.EwpRoomInfoVO;

/**
 * 장소 구분별로 테이블이 다르고 키는 같기 때문에 캐시 적용시 캐시명은 반드시 장소 구분별로 다르게 처리 할 것
 * 
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 30
 * @see {@link RoomType}
 */
@Mapper
public interface EwpTiberoRoomMapper {
	List<EwpRoomInfoVO> selectRoomList(EwpRoomInfoVO room);
	List<EwpRoomInfoVO> selectAllRoomList();
	
//	회의실
	//@Cacheable(value = "mr_roomList")
	List<EwpRoomInfoVO> selectMeetingRoomList(EwpRoomInfoVO room);
	@CacheEvict(value = "mr_roomList")
	Integer insertMeetingRoom(EwpRoomInfoVO room);
	@Caching(evict = {@CacheEvict(value = "mr_roomList"), @CacheEvict(value = "mr_room",  key = "#room.roomKey", condition="#room.roomKey!=null")})
	Integer updateMeetingRoomOne(EwpRoomInfoVO room);
	@Caching(evict = {@CacheEvict(value = "mr_roomList"), @CacheEvict(value = "mr_room",  key = "#room.roomKey", condition="#room.roomKey!=null")})
	Integer updateMeetingRoomOneToDelete(EwpRoomInfoVO room);
	//@Cacheable(value = "mr_room", key = "#roomKey", unless="#result == null", condition="#roomKey!=null")
	EwpRoomInfoVO selectMeetingRoomOne(Integer roomKey);
	
//	강의실
	//@Cacheable(value = "er_roomList")
	List<EwpRoomInfoVO> selectEduRoomList(EwpRoomInfoVO room);
	@CacheEvict(value = "er_roomList")
	Integer insertEduRoom(EwpRoomInfoVO room);
	@Caching(evict = {@CacheEvict(value = "er_roomList"), @CacheEvict(value = "er_room",  key = "#room.roomKey", condition="#room.roomKey!=null")})
	Integer updateEduRoomOne(EwpRoomInfoVO room);
	@Caching(evict = {@CacheEvict(value = "er_roomList"), @CacheEvict(value = "er_room",  key = "#room.roomKey", condition="#room.roomKey!=null")})
	Integer updateEduRoomOneToDelete(EwpRoomInfoVO room);
	//@Cacheable(value = "er_room", key = "#roomKey", unless="#result == null", condition="#roomKey!=null")
	EwpRoomInfoVO selectEduRoomOne(Integer roomKey);
	
//	강당
	//@Cacheable(value = "lh_roomList")
	List<EwpRoomInfoVO> selectHallList(EwpRoomInfoVO room);
	@CacheEvict(value = "lh_roomList")
	Integer insertHall(EwpRoomInfoVO room);
	@Caching(evict = {@CacheEvict(value = "lh_roomList"), @CacheEvict(value = "lh_room",  key = "#room.roomKey", condition="#room.roomKey!=null")})
	Integer updateHallOne(EwpRoomInfoVO room);
	@Caching(evict = {@CacheEvict(value = "lh_roomList"), @CacheEvict(value = "lh_room",  key = "#room.roomKey", condition="#room.roomKey!=null")})
	Integer updateHallOneToDelete(EwpRoomInfoVO room);
	//@Cacheable(value = "lh_room", key = "#roomKey", unless="#result == null", condition="#roomKey!=null")
	EwpRoomInfoVO selectHallOne(Integer roomKey);
}
