package egov.framework.plms.main.bean.mvc.service.meeting.abst;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingInfoVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingScheduleModelVO;
import egov.framework.plms.main.bean.mvc.mapper.meeting.abst.MeetingScheduleAbstractMapper;
import lombok.extern.slf4j.Slf4j;


/**
 * 회의 일정에 관한 추상 서비스 클래스입니다.
 * 이 클래스는 MeetingScheduleModelVO 타입을 상속받은 VO 클래스를 통해 서비스를 제공합니다.
 * 실제 서비스 클래스는 이 추상 클래스를 상속받아 구현되며, 해당 VO 클래스에 대한 매퍼를 이용하여 데이터베이스와의 상호작용을 담당합니다.
 * 
 * @author mckim
 * @version 1.0
 * @since 2023. 5. 1
 * @param <T> MeetingScheduleModelVO를 상속받은 VO 클래스의 타입
 */
@Slf4j
public abstract class MeetingScheduleAbstractService<T extends MeetingScheduleModelVO> {
	protected final MeetingScheduleAbstractMapper<T> mapper;
	
	public MeetingScheduleAbstractService(MeetingScheduleAbstractMapper<T> mapper) {
		this.mapper = mapper;
	}
	/**
	 * 회의 스케줄 등록
	 * @param schedule {@link T}
	 * @return
	 * @throws Exception
	 */
	public boolean postMeetingSchedule(T schedule) {
		try {
			Integer result = mapper.postMeetingSchedule(schedule);
			return (result == 0)?false:true;
		}catch(Exception e){
			log.error("Failed to post MeetingSchedule with params: {}", schedule.toString());
			log.error("Failed to post MeetingSchedule messages: {}", e.toString());
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 회의 스케줄 수정
	 * @param schedule {@link T}
	 * @return
	 * @throws Exception
	 */
	public boolean putMeetingSchedule(T schedule) {
		try {
			Integer result = mapper.putMeetingSchedule(schedule);
			return (result == 0)?false:true;
		}catch(Exception e){
			log.error("Failed to put MeetingSchedule with params: {}", schedule.toString());
			log.error("Failed to put MeetingSchedule messages: {}", e.toString());
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 회의 스케줄 삭제
	 * @param skdId {@link T#skdKey}
	 * @return
	 */
	public boolean deleteMeetingSchedule(Integer scheduleId) {
		try {
			Integer result = mapper.deleteMeetingScheduleOne(scheduleId);
			return (result == 0)?false:true;
		}catch(Exception e){
			log.error("Failed to delete MeetingSchedule with id: {}", scheduleId);
			log.error("Failed to delete MeetingSchedule messages: {}", e.toString());
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 회의 스케줄 조회.
	 * @param skdId {@link T#skdKey}
	 * @return
	 */
	public Optional<T> getMeetingScheduleOne(Integer scheduleId) {
		try {
			T meeting = mapper.getMeetingScheduleOne(scheduleId);
			return Optional.ofNullable(meeting);
		}catch(Exception e){
			log.error("Failed to get one MeetingSchedule with id: {}", scheduleId);
			log.error("Failed to get one MeetingSchedule messages: {}", e.toString());
			e.printStackTrace();
			return Optional.empty();
		}
	}
	/**
	 * 해당 스케줄 다음 예정된 회의 스케줄 조회.
	 * @param skdId {@link T#skdKey}
	 * @return
	 */
	public Optional<T> getMeetingScheduleNextOne(Integer scheduleId) {
		try {
			T meeting = mapper.getMeetingScheduleNextOne(scheduleId);
			return Optional.ofNullable(meeting);
		}catch(Exception e){
			log.error("Failed to get next one MeetingSchedule with id: {}", scheduleId);
			log.error("Failed to get next one MeetingSchedule messages: {}", e.toString());
			e.printStackTrace();
			return Optional.empty();
		}
	}
	/**
	 * 회의 정보의 키 값{@link MeetingInfoVO#meetingKey} 을 외래키로 참조({@link T#meetingKey})하는 스케줄을 조회.
	 * @param meetingId {@link MeetingInfoVO#meetingKey}
	 * @return
	 */
	public Optional<T> getAssignedMeetingScheduleOne(Integer meetingId){
		try {
			T meeting = mapper.getAssignedMeetingScheduleOne(meetingId);
			return Optional.ofNullable(meeting);
		}catch(Exception e){
			log.error("Failed to get one assigned MeetingSchedule with id: {}", meetingId);
			log.error("Failed to get one assigned MeetingSchedule messages: {}", e.toString());
			e.printStackTrace();
			return Optional.empty();
		}
	}
	/**
	 * 종료된 회의 검색 기능.
	 * @param param
	 * @return
	 */
	public List<T> getMeetingScheduleList(T params){
		try {
			return mapper.getMeetingScheduleList(params);
		}catch(Exception e){
			log.error("Failed to get List MeetingSchedule with params: {}", params);
			log.error("Failed to get List MeetingSchedule messages: {}", e.toString());
			e.printStackTrace();
			return new ArrayList<T>();
		}
	}
}
