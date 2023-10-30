package egov.framework.plms.main.bean.mvc.mapper.meeting.abst;

import java.util.List;

import egov.framework.plms.main.bean.mvc.entity.common.StatVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingAssignModelVO;

public interface MeetingAssignAbstractMapper<T extends MeetingAssignModelVO> {
	T getMeetingAssignOne(T params);
	List<T> getMeetingAssignList(T params);
	Integer getMeetingAssignListCnt(T params);
	List<StatVO> getMeetingAssignStatForPlanned(T params);
	List<T> getMeetingAssignListForPlanned(T params);
	List<T> getMeetingAssignListForMonitoring();
}
