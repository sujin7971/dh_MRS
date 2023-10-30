package egov.framework.plms.sub.ewp.bean.mvc.mapper.tibero.meeting;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.sub.ewp.bean.mvc.entity.assign.EwpRoomReqVO;

@Mapper
public interface AllRoomReqMapper {
//	회의실
	Integer getMeetingRoomReqCount(Integer roomKey);
	
//	강의실
	Integer getEduRoomReqCount(Integer roomKey);
	
//	강의실
	Integer getHallReqCount(Integer roomKey);
	
	//사용신청 테이블 통합검색
	Integer getRoomReqListCnt(EwpRoomReqVO param);
	List<EwpRoomReqVO> getRoomReqListSearch(EwpRoomReqVO param);
}
