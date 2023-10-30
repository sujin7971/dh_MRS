package egov.framework.plms.main.bean.mvc.service.meeting.abst;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingAttendeeModelVO;
import egov.framework.plms.main.bean.mvc.mapper.meeting.abst.MeetingAttendeeAbstractMapper;
import egov.framework.plms.main.core.model.enums.meeting.AttendRole;
import lombok.extern.slf4j.Slf4j;

/**
 * 회의 참석자에 관한 추상 서비스 클래스입니다.
 * 이 클래스는 MeetingAttendeeModelVO 타입을 상속받은 VO 클래스를 통해 서비스를 제공합니다.
 * 실제 서비스 클래스는 이 추상 클래스를 상속받아 구현되며, 해당 VO 클래스에 대한 매퍼를 이용하여 데이터베이스와의 상호작용을 담당합니다.
 * 
 * @author mckim
 * @version 1.0
 * @since 2023. 5. 2
 * @param <T> MeetingAttendeeModelVO를 상속받은 VO 클래스의 타입
 * @see {@link AttendRole}
 */
@Slf4j
public abstract class MeetingAttendeeAbstractService<T extends MeetingAttendeeModelVO> {
	
	protected final MeetingAttendeeAbstractMapper<T> mapper;

    public MeetingAttendeeAbstractService(MeetingAttendeeAbstractMapper<T> mapper) {
        this.mapper = mapper;
    }
	/**
	 * 참석자 등록
	 * @param attendee
	 * @return
	 */
	public boolean postMeetingAttendee(T attendee) {
		try {
			Integer result = mapper.postMeetingAttendee(attendee);
			return (result == 0)?false:true;
		}catch(Exception e){
			log.error("Failed to post MeetingAttendee with params: {}", attendee.toString());
			log.error("Failed to post MeetingAttendee messages: {}", e.toString());
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 참석자 수정
	 * @param attendee
	 * @return
	 */
	public boolean putMeetingAttendee(T attendee) {
		try {
			Integer result = mapper.putMeetingAttendee(attendee);
			return (result == 0)?false:true;
		}catch(Exception e){
			log.error("Failed to put MeetingAttendee with params: {}", attendee.toString());
			log.error("Failed to put MeetingAttendee messages: {}", e.toString());
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 참석자 삭제
	 * @param attendee
	 * @return
	 */
	public boolean deleteMeetingAttendeeOne(Integer attendId) {
		try {
			Integer result = mapper.deleteMeetingAttendeeOne(attendId);
			return (result == 0)?false:true;
		}catch(Exception e){
			log.error("Failed to delete one MeetingAttendee with id: {}", attendId);
			log.error("Failed to delete one MeetingAttendee messages: {}", e.toString());
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 회의에 등록된 참석자 모두 삭제
	 * @param meetingId
	 * @return
	 */
	public abstract boolean deleteMeetingAttendeeAll(Integer meetingId);
	/**
	 * 참석자 일괄 삭제
	 * @param attendee
	 * @return
	 */
	public boolean deleteMeetingAttendeeList(T attendee) {
		try {
			Integer result = mapper.deleteMeetingAttendeeList(attendee);
			return (result == 0)?false:true;
		}catch(Exception e){
			log.error("Failed to delete list MeetingAttendee with params: {}", attendee.toString());
			log.error("Failed to delete list MeetingAttendee messages: {}", e.toString());
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 해당 유저의 회의 참석키 조회
	 * @param userId
	 * @param meetingId
	 * @return
	 */
	public abstract Integer getAttendId(String userId, Integer meetingId);
	/**
	 * 참석 정보 조회
	 * @param attendId 참석키
	 * @return
	 */
	public Optional<T> getMeetingAttendeeOne(Integer attendId) {
		try {
		T attendeeVO = mapper.getMeetingAttendeeOne(attendId);
		return Optional.ofNullable(attendeeVO);
		}catch(Exception e) {
			log.error("Failed to get one MeetingAttendee with id: {}", attendId);
			log.error("Failed to get one MeetingAttendee messages: {}", e.toString());
			e.printStackTrace();
			return Optional.empty();
		}
	}
	/**
	 * 특정 유저의 해당 회의에 대한 참석 정보 조회
	 * @param userId 유저키
	 * @param meetingId 회의키
	 * @return
	 */
	public Optional<T> getMeetingAttendeeOne(String userId, Integer meetingId){
		Integer attendId = getAttendId(userId, meetingId);
		return getMeetingAttendeeOne(attendId);
	}
	/**
	 * 해당 유저의 모든 참석 조회
	 * @param userId 조회할 유저키
	 * @return
	 */
	public abstract List<T> getMeetingAttendeeListByUser(String userId);
	/**
	 * 해당 유저의 특정 역할의 참석 조회
	 * @param userId 조회할 유저키
	 * @param attendRole 참석 유형
	 * @return
	 */
	public abstract List<T> getMeetingAttendeeListByUser(String userId, AttendRole attendRole);
	/**
	 * 해당 회의의 모든 참석자 조회
	 * @param meetingId 조회할 회의키
	 * @return
	 */
	public abstract List<T> getMeetingAttendeeListByMeeting(Integer meetingId);
	/**
	 * 해당 회의의 특정 역할의 참석자 조회
	 * @param meetingId 조회할 회의키
	 * @param attendRole 참석 유형
	 * @return
	 */
	public abstract List<T> getMeetingAttendeeListByMeeting(Integer meetingId, AttendRole attendRole);
	/**
	 * 모든 참석자 대상 조회
	 * @param attendee 검색 조건
	 * @return
	 */
	public List<T> getMeetingAttendeeList(T params){
		try {
			return mapper.getMeetingAttendeeList(params);
		}catch(Exception e) {
			log.error("Failed to get list MeetingAttendee with params: {}", params.toString());
			log.error("Failed to get list MeetingAttendee messages: {}", e.toString());
			e.printStackTrace();
			return new ArrayList<T>();
		}
	}
}
