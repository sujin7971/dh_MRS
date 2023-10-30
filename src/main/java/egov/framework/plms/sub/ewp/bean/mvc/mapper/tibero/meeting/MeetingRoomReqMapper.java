package egov.framework.plms.sub.ewp.bean.mvc.mapper.tibero.meeting;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.sub.ewp.bean.mvc.entity.assign.EwpRoomReqVO;

@Mapper
public interface MeetingRoomReqMapper {
	Integer doInsertMeetingRoomReq(EwpRoomReqVO param);
	Integer doUpdateMeetingRoomReq(EwpRoomReqVO param);
	Integer doUpdateMeetingRoomReqStatus(EwpRoomReqVO param);
	Integer doDeleteMeetingRoomReq(EwpRoomReqVO param);
	Integer doDeleteFailedMeetingRoomReq(Integer seqReq);
	EwpRoomReqVO getMeetingRoomReqOne(Integer seqReq);
	List<EwpRoomReqVO> getMeetingRoomReqList(EwpRoomReqVO param);
	Integer getMeetingRoomReqListCnt(EwpRoomReqVO param);
	Integer checkMeetingRoomReq5Day(EwpRoomReqVO param);
	EwpRoomReqVO getMeetingRoomReqDetail(EwpRoomReqVO param);
	EwpRoomReqVO getNextMeetingRoomReqOne(Integer seqReq);
	List<EwpRoomReqVO> getMeetingRoomReqStatusList();
}
