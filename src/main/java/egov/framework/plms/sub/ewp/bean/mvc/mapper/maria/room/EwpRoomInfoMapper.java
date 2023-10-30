package egov.framework.plms.sub.ewp.bean.mvc.mapper.maria.room;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.main.bean.mvc.mapper.room.abst.RoomInfoAbstractMapper;
import egov.framework.plms.sub.ewp.bean.mvc.entity.room.EwpRoomInfoVO;

@Mapper
public interface EwpRoomInfoMapper extends RoomInfoAbstractMapper<EwpRoomInfoVO>{
	/**
	 * 동서발전은 회의실, 강의실, 강당 유형별로 테이블을 구분하기때문에 고유키값만으로는 판별 불가<br>
	 * {@link #updateRoomOneToDelete(EwpRoomInfoVO params)}을 사용할것.
	 */
	@Deprecated
	Integer updateRoomOneToDelete(Integer roomId);
	/**
	 * 장소유형과 고유키값을 통해 장소데이터 삭제.{@link #EwpRoomInfoVO}의 {@code roomKey}와{@code roomType}값 필요
	 * @param params
	 * @return
	 */
	Integer updateRoomOneToDelete(EwpRoomInfoVO params);
	/**
	 * 동서발전은 회의실, 강의실, 강당 유형별로 테이블을 구분하기때문에 고유키값만으로는 판별 불가<br>
	 * {@link #selectRoomOne(EwpRoomInfoVO params)}을 사용할것.
	 */
	@Deprecated
	EwpRoomInfoVO selectRoomOne(Integer roomId);
	/**
	 * 장소유형과 고유키값을 통해 장소데이터 조회.{@link #EwpRoomInfoVO}의 {@code roomKey}와{@code roomType}값 필요 
	 * @param params
	 * @return
	 */
	EwpRoomInfoVO selectRoomOne(EwpRoomInfoVO params);
}
