package egov.framework.plms.main.bean.mvc.service.meeting.abst;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import egov.framework.plms.main.bean.mvc.entity.common.StatVO;
import egov.framework.plms.main.bean.mvc.entity.common.abst.StatModelVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingInfoVO;
import egov.framework.plms.main.bean.mvc.mapper.meeting.abst.MeetingStatAbstractMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * 회의 통계에 관한 추상 서비스 클래스입니다.
 * 이 클래스는 StatModelVO 타입을 상속받은 VO 클래스를 통해 서비스를 제공합니다.
 * 실제 서비스 클래스는 이 추상 클래스를 상속받아 구현되며, 해당 VO 클래스에 대한 매퍼를 이용하여 데이터베이스와의 상호작용을 담당합니다.
 * 
 * @author mckim
 * @version 1.0
 * @since 2023. 5. 15
 * @param <T> StatModelVO를 상속받은 VO 클래스의 타입
 */
@Slf4j
public abstract class MeetingStatAbstractService<T extends StatModelVO> {
	protected final MeetingStatAbstractMapper<T> mapper;
	
	public MeetingStatAbstractService(MeetingStatAbstractMapper<T> mapper) {
		this.mapper = mapper;
	}
	
	/**
	 * 회사전체 용지 절약 통계 조회
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public abstract Integer getPaperlessStatForCompany(LocalDate startDate, LocalDate endDate);
	
	protected Integer getPaperlessStatForCompany(T params) {
		try {
			return mapper.getPaperlessStatForCompany(params);
		}catch(Exception e) {
			log.error("Failed to get PaperlessStatForCompany with Params: {}", params);
			log.error("Failed to get PaperlessStatForCompany messages: {}", e.toString());
			e.printStackTrace();
			return 0;
		}
	}
	/**
	 * 회사전체 총 회의 건 수 / 평균 회의시간 통계 조회
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public abstract Optional<T> getMeetingCountAndAverageDurationStatForCompany(LocalDate startDate, LocalDate endDate);
	
	protected Optional<T> getMeetingCountAndAverageDurationStatForCompany(T params) {
		try {
			T stat = mapper.getMeetingCountAndAverageDurationStatForCompany(params);
			return Optional.ofNullable(stat);
		}catch(Exception e) {
			log.error("Failed to get MeetingCountAndAverageDurationStatForCompany with Params: {}", params);
			log.error("Failed to get MeetingCountAndAverageDurationStatForCompany messages: {}", e.toString());
			e.printStackTrace();
			return Optional.empty();
		}
	}
	/**
	 * 회사전체 회의 개최가 많았던 부서 TOP5 통계 조회
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public abstract List<T> getTop5DepartmentWithMeetingForCompany(LocalDate startDate, LocalDate endDate);
	
	protected List<T> getTop5DepartmentWithMeetingForCompany(T params) {
		try {
			return mapper.getTop5DepartmentWithMeetingForCompany(params);
		}catch(Exception e) {
			log.error("Failed to get Top5DepartmentWithMeetingForCompany with Params: {}", params);
			log.error("Failed to get Top5DepartmentWithMeetingForCompany messages: {}", e.toString());
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 회사전체 월 회의 건 수 추이 통계 조회
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public abstract List<T> getMeetingMonthlyTrendForCompany(LocalDate startDate, LocalDate endDate);
	
	public List<T> getMeetingMonthlyTrendForCompany(T params) {
		try {
			return mapper.getMeetingMonthlyTrendForCompany(params);
		}catch(Exception e) {
			log.error("Failed to get MeetingMonthlyTrendForCompany with Params: {}", params);
			log.error("Failed to get MeetingMonthlyTrendForCompany messages: {}", e.toString());
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 개인 용지 절약 통계 조회
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public abstract Integer getPaperlessStatForPersonal(String userId, LocalDate startDate, LocalDate endDate);
	
	protected Integer getPaperlessStatForPersonal(T params) {
		try {
			return mapper.getPaperlessStatForPersonal(params);
		}catch(Exception e) {
			log.error("Failed to get PaperlessStatForPersonal with Params: {}", params);
			log.error("Failed to get PaperlessStatForPersonal messages: {}", e.toString());
			e.printStackTrace();
			return 0;
		}
	}
	/**
	 * 내가 진행한 회의 / 총 회의시간 통계 조회
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public abstract Optional<T> getMeetingCountAndTotalDurationStatForHosting(String userId, LocalDate startDate, LocalDate endDate);
	
	protected Optional<T> getMeetingCountAndTotalDurationStatForHosting(T params) {
		try {
			T stat = mapper.getMeetingCountAndTotalDurationStatForHosting(params);
			return Optional.ofNullable(stat);
		}catch(Exception e) {
			log.error("Failed to get MeetingCountAndTotalDurationStatForHosting with Params: {}", params);
			log.error("Failed to get MeetingCountAndTotalDurationStatForHosting messages: {}", e.toString());
			e.printStackTrace();
			return Optional.empty();
		}
	}
	/**
	 * 참여 회의 건 수 / 총 회의시간 통계 조회
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public abstract Optional<T> getMeetingCountAndTotalDurationStatForAttendance(String userId, LocalDate startDate, LocalDate endDate);
	
	protected Optional<T> getMeetingCountAndTotalDurationStatForAttendance(T params) {
		try {
			T stat = mapper.getMeetingCountAndTotalDurationStatForAttendance(params);
			return Optional.ofNullable(stat);
		}catch(Exception e) {
			log.error("Failed to get MeetingCountAndTotalDurationStatForAttendance with Params: {}", params);
			log.error("Failed to get MeetingCountAndTotalDurationStatForAttendance messages: {}", e.toString());
			e.printStackTrace();
			return Optional.empty();
		}
	}
	/**
	 * 개인 소속 사업소 회의 개최가 많았던 부서 TOP5 통계 조회
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public abstract List<T> getTop5DepartmentWithMeetingForOffice(String userId, LocalDate startDate, LocalDate endDate);

	protected List<T> getTop5DepartmentWithMeetingForOffice(T params) {
		try {
			return mapper.getTop5DepartmentWithMeetingForOffice(params);
		}catch(Exception e) {
			log.error("Failed to get Top5DepartmentWithMeetingForOffice with Params: {}", params);
			log.error("Failed to get Top5DepartmentWithMeetingForOffice messages: {}", e.toString());
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 개인 월 회의 건 수 추이 통계 조회
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public abstract List<T> getMeetingMonthlyTrendForPersonal(String userId, LocalDate startDate, LocalDate endDate);
	
	public List<T> getMeetingMonthlyTrendForPersonal(T params) {
		try {
			return mapper.getMeetingMonthlyTrendForPersonal(params);
		}catch(Exception e) {
			log.error("Failed to get MeetingMonthlyTrendForCompany with Params: {}", params);
			log.error("Failed to get MeetingMonthlyTrendForCompany messages: {}", e.toString());
			e.printStackTrace();
			return null;
		}
	}
}
