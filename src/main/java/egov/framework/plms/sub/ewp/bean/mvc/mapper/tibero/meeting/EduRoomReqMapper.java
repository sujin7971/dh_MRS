package egov.framework.plms.sub.ewp.bean.mvc.mapper.tibero.meeting;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.sub.ewp.bean.mvc.entity.assign.EwpRoomReqVO;

@Mapper
public interface EduRoomReqMapper {
	Integer doInsertEduRoomReq(EwpRoomReqVO param);
	Integer doUpdateEduRoomReq(EwpRoomReqVO param);
	Integer doUpdateEduRoomReqStatus(EwpRoomReqVO param);
	Integer doDeleteEduRoomReq(EwpRoomReqVO param);
	Integer doDeleteFailedEduRoomReq(Integer seqReq);
	EwpRoomReqVO getEduRoomReqOne(Integer seqReq);
	List<EwpRoomReqVO> getEduRoomReqList(EwpRoomReqVO param);
	Integer getEduRoomReqListCnt(EwpRoomReqVO param);
	Integer checkEduRoomReq5Day(EwpRoomReqVO param);
	EwpRoomReqVO getEduRoomReqDetail(EwpRoomReqVO param);
	EwpRoomReqVO getNextEduRoomReqOne(Integer seqReq);
	List<EwpRoomReqVO> getEduRoomReqStatusList();
}
