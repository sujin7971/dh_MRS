package egov.framework.plms.main.bean.mvc.entity.meeting.abst;

import java.time.LocalDate;
import java.time.LocalDateTime;

import egov.framework.plms.main.core.model.enums.meeting.ApprovalStatus;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import egov.framework.plms.main.core.model.enums.meeting.ScheduleType;


/**
 * 스케줄 객체
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 */
public abstract class MeetingScheduleEntity {
	/** 스케줄 고유키 */
	public abstract Integer getScheduleId();
	/** 회의 고유키 */
	public abstract Integer getMeetingId();
	/** 외부 사용요청키 */
	public abstract String getExternalReservationId();
	/** 작성자 고유키 */
	public abstract String getWriterId();
	public abstract String getOfficeCode();
	public abstract String getDeptId();
	public abstract ScheduleType getScheduleType();
	public abstract ApprovalStatus getApprovalStatus();
	public abstract RoomType getRoomType();
	public abstract Integer getRoomId();
	public abstract Integer getAttendeeCnt();
	public abstract String getUserDefinedLocation();
	public abstract String getScheduleHost();
	/** 스케줄 예정일 */
	public abstract LocalDate getHoldingDate();
	/** 스케줄 시작일자 및 시간 */
	public abstract LocalDateTime getBeginDateTime();
	/** 스케줄 종료일자 및 시간 */
	public abstract LocalDateTime getFinishDateTime();
	/** 스케줄 만료일자 및 시간 */
	public abstract LocalDateTime getExpDateTime();
}
