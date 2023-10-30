package egov.framework.plms.main.bean.mvc.service.meeting.abst;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingInfoModelVO;
import egov.framework.plms.main.bean.mvc.mapper.meeting.abst.MeetingInfoAbstractMapper;
import lombok.extern.slf4j.Slf4j;


/**
 * 회의 설정에 관한 추상 서비스 클래스입니다.
 * 이 클래스는 MeetingInfoModelVO 타입을 상속받은 VO 클래스를 통해 서비스를 제공합니다.
 * 실제 서비스 클래스는 이 추상 클래스를 상속받아 구현되며, 해당 VO 클래스에 대한 매퍼를 이용하여 데이터베이스와의 상호작용을 담당합니다.
 * 
 * @author mckim
 * @version 1.0
 * @since 2023. 5. 1
 * @param <T> MeetingInfoModelVO를 상속받은 VO 클래스의 타입
 */
@Slf4j
public abstract class MeetingInfoAbstractService<T extends MeetingInfoModelVO> {
	protected final MeetingInfoAbstractMapper<T> mapper;
	
	public MeetingInfoAbstractService(MeetingInfoAbstractMapper<T> mapper) {
		this.mapper = mapper;
	}
	/**
	 * 회의 정보 등록
	 * @param meeting
	 * @return
	 * @throws Exception
	 */
	public boolean postMeetingInfo(T meeting) {
		try {
			Integer result = mapper.postMeetingInfo(meeting);
			return (result == 0)?false:true;
		}catch(Exception e){
			log.error("Failed to post MeetingInfo with params: {}", meeting.toString());
			log.error("Failed to post MeetingInfo messages: {}", e.toString());
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 회의 정보 수정
	 * @param meeting
	 * @return
	 */
	public boolean putMeetingInfo(T meeting) {
		try {
			Integer result = mapper.putMeetingInfo(meeting);
			return (result == 0)?false:true;
		}catch(Exception e){
			log.error("Failed to put MeetingInfo with params: {}", meeting.toString());
			log.error("Failed to put MeetingInfo messages: {}", e.toString());
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 회의 허용 권한 수정
	 * @param meetingId
	 * @param stickyBit
	 * @return
	 */
	public abstract boolean putMeetingStickyBit(Integer meetingId, Integer stickyBit);
	/**
	 * 회의 정보 삭제
	 * @param meetingId
	 * @return
	 */
	public boolean deleteMeetingInfo(Integer meetingId) {
		try {
			Integer result = mapper.deleteMeetingInfo(meetingId);
			return (result == 0)?false:true;
		}catch(Exception e){
			log.error("Failed to delete MeetingInfo with id: {}", meetingId);
			log.error("Failed to delete MeetingInfo messages: {}", e.toString());
			e.printStackTrace();
			return false;
		}
	}
	public Integer getMeetingStickyBit(Integer meetingId) {
		try {
			return mapper.getMeetingStickyBit(meetingId);
		}catch(Exception e) {
			log.error("Failed to get MeetingInfo stickyBit with id: {}", meetingId);
			log.error("Failed to get MeetingInfo stickyBit messages: {}", e.toString());
			e.printStackTrace();
			return 0;
		}
	}
	/**
	 * 회의 정보 조회
	 * @param meetingId
	 * @return
	 */
	public Optional<T> getMeetingInfoOne(Integer meetingId){
		try {
			T meeting = mapper.getMeetingInfoOne(meetingId);
			return Optional.ofNullable(meeting);
		}catch(Exception e){
			log.error("Failed to get one MeetingInfo with id: {}", meetingId);
			log.error("Failed to get one MeetingInfo messages: {}", e.toString());
			e.printStackTrace();
			return Optional.empty();
		}
	}
	/**
	 * 회의 목록 조회
	 * @param params
	 * @return
	 */
	public List<T> getMeetingInfoList(T params){
		try {
			return mapper.getMeetingInfoList(params);
		}catch(Exception e){
			log.error("Failed to get list MeetingInfo with params: {}", params.toString());
			log.error("Failed to get list MeetingInfo messages: {}", e.toString());
			e.printStackTrace();
			return new ArrayList<T>();
		}
	}
}
