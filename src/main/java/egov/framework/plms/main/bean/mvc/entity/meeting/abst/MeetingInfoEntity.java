package egov.framework.plms.main.bean.mvc.entity.meeting.abst;

import java.time.LocalDateTime;

import egov.framework.plms.main.core.model.enums.meeting.MeetingStatus;

public abstract class MeetingInfoEntity {
	/** 회의 고유키 */
	public abstract Integer getMeetingId();
	public abstract String getWriterId();
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
	public abstract LocalDateTime getRegDateTime();
}
