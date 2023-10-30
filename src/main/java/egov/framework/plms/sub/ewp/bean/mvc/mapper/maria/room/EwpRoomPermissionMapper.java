package egov.framework.plms.sub.ewp.bean.mvc.mapper.maria.room;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.transaction.annotation.Transactional;

import egov.framework.plms.sub.ewp.bean.mvc.entity.room.EwpRoomPermissionVO;

@Mapper
public interface EwpRoomPermissionMapper {
// 회의실 사용 가능 부서
	List<EwpRoomPermissionVO> getRoomPermissionList(EwpRoomPermissionVO param);
	
	// 회의실 대여 가능 부서 등록
	Integer postRoomPermissionList(List<EwpRoomPermissionVO> list);
	// 회의실 대여 가능 부서 삭제
	Integer deleteRoomPermissionAll(EwpRoomPermissionVO room);
}
