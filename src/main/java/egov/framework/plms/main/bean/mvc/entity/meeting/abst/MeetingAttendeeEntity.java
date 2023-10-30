package egov.framework.plms.main.bean.mvc.entity.meeting.abst;

import java.time.LocalDateTime;

import egov.framework.plms.main.bean.mvc.entity.organization.abst.UserInfoEntity;
import egov.framework.plms.main.core.model.enums.meeting.AttendRole;

public abstract class MeetingAttendeeEntity extends UserInfoEntity{
	public abstract AttendRole getAttendRole();
	public abstract Integer getAttendId();
	public abstract Integer getMeetingId();
	public abstract String getUserId();
	public abstract Character getAssistantYN();
	public abstract Character getAttendYN();
	public abstract LocalDateTime getAttendDateTime();
	public abstract Character getExitYN();
	public abstract LocalDateTime getExitDateTime();
	public abstract Character getSignYN();
	public abstract String getSignSrc();
	public abstract Character getDelYN();
	
}
