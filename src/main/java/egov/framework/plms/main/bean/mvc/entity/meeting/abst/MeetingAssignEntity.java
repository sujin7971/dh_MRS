package egov.framework.plms.main.bean.mvc.entity.meeting.abst;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import egov.framework.plms.main.core.model.enums.meeting.ApprovalStatus;
import egov.framework.plms.main.core.model.enums.meeting.MeetingStatus;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import egov.framework.plms.main.core.model.enums.meeting.ScheduleType;

public abstract class MeetingAssignEntity {
	public abstract Integer getScheduleId();
	public abstract Integer getMeetingId();
	public abstract String getExternalReservationId();
	public abstract String getWriterId();
	public abstract String getOfficeCode();
	public abstract String getDeptId();
	public abstract Integer getRoomId();
	public abstract Integer getAttendeeCnt();
	public abstract String getUserDefinedLocation();
	public abstract String getScheduleHost();
	public abstract LocalDate getHoldingDate();
	public abstract LocalTime getBeginTime();
	public abstract LocalTime getFinishTime();
	public abstract LocalDateTime getBeginDateTime();
	public abstract LocalDateTime getFinishDateTime();
	public abstract LocalDateTime getExpDateTime();
	public abstract ScheduleType getScheduleType();
	public abstract ApprovalStatus getApprovalStatus();
	public abstract RoomType getRoomType();
	public abstract MeetingStatus getMeetingStatus();
	public abstract String getTitle();
	public abstract String getContents();
	public abstract Character getSecretYN();
	public abstract Integer getHostSecuLvl();
	public abstract Integer getAttendeeSecuLvl();
	public abstract Integer getObserverSecuLvl();
	public abstract Character getElecYN();
	public abstract Character getMessengerYN();
	public abstract Character getMailYN();
	public abstract Character getSmsYN();
	public abstract Character getDelYN();
}
