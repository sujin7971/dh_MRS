package egov.framework.plms.sub.ewp.bean.mvc.mapper.tibero.meeting;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.sub.ewp.bean.mvc.entity.assign.EwpRoomReqVO;

@Mapper
public interface HallReqMapper {
	Integer doInsertHallReq(EwpRoomReqVO param);
	Integer doUpdateHallReq(EwpRoomReqVO param);
	Integer doUpdateHallReqStatus(EwpRoomReqVO param);
	Integer doDeleteHallReq(EwpRoomReqVO param);
	Integer doDeleteFailedHallReq(Integer seqReq);
	EwpRoomReqVO getHallReqOne(Integer seqReq);
	List<EwpRoomReqVO> getHallReqList(EwpRoomReqVO param);
	Integer getHallReqListCnt(EwpRoomReqVO param);
	Integer checkHallReq5Day(EwpRoomReqVO param);
	EwpRoomReqVO getHallReqDetail(EwpRoomReqVO param);
	EwpRoomReqVO getNextHallReqOne(Integer seqReq);
	List<EwpRoomReqVO> getHallReqStatusList();
}
