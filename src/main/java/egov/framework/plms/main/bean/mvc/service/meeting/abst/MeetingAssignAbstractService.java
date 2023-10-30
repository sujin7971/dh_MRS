package egov.framework.plms.main.bean.mvc.service.meeting.abst;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import egov.framework.plms.main.bean.mvc.entity.common.StatVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingAssignModelVO;
import egov.framework.plms.main.bean.mvc.mapper.meeting.abst.MeetingAssignAbstractMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * 회의 통합 정보에 관한 추상 서비스 클래스입니다.
 * 이 클래스는 MeetingAssignModelVO 타입을 상속받은 VO 클래스를 통해 서비스를 제공합니다.
 * 실제 서비스 클래스는 이 추상 클래스를 상속받아 구현되며, 해당 VO 클래스에 대한 매퍼를 이용하여 데이터베이스와의 상호작용을 담당합니다.
 * 
 * @author mckim
 * @version 1.0
 * @since 2023. 5. 1
 * @param <T> MeetingAssignModelVO를 상속받은 VO 클래스의 타입
 */
@Slf4j
public abstract class MeetingAssignAbstractService<T extends MeetingAssignModelVO> {
	protected final MeetingAssignAbstractMapper<T> mapper;
	
	public MeetingAssignAbstractService(MeetingAssignAbstractMapper<T> mapper) {
		this.mapper = mapper;
	}
	
	public abstract T postMeetingAssign(T params);
	public abstract boolean putMeetingAssign(T params);
	public abstract boolean deleteMeetingAssign(Integer scheduleId);
	
	public Optional<T> getMeetingAssignOne(T params){
		T assign = mapper.getMeetingAssignOne(params);
		return Optional.ofNullable(assign);
	}
	
	public abstract Optional<T> getMeetingAssignOneByScheduleId(Integer scheduleId);
	
	public abstract Optional<T> getMeetingAssignOneByMeetingId(Integer meetingId);
	
	public Integer getMeetingAssignListCnt(T params){
		return mapper.getMeetingAssignListCnt(params);
	}
	
	public List<T> getMeetingAssignList(T params){
		try {
			return mapper.getMeetingAssignList(params);
		}catch(Exception e) {
			log.error("Failed to get MeetingAssign list with params: {}", params.toString());
			log.error("Failed to get MeetingAssign list messages: {}", e.toString());
			e.printStackTrace();
			return new ArrayList<T>();
		}
	}
	
	public List<T> getMeetingAssignListForPlanned(T params){
		return mapper.getMeetingAssignListForPlanned(params);
	}
	
	public List<StatVO> getMeetingAssignStatForPlanned(T params){
		return mapper.getMeetingAssignStatForPlanned(params);
	}
	
	public List<T> getMeetingAssignListForMonitoring(){
		return mapper.getMeetingAssignListForMonitoring();
	}
}
