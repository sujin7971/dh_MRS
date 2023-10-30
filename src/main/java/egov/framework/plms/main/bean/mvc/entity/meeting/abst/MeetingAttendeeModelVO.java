package egov.framework.plms.main.bean.mvc.entity.meeting.abst;

import java.time.LocalDate;

import egov.framework.plms.main.core.model.able.Convertable;
import egov.framework.plms.main.core.model.enums.meeting.AttendRole;

public abstract class MeetingAttendeeModelVO extends MeetingAttendeeEntity implements Convertable<MeetingAttendeeModelDTO> {
	public abstract String getTempPassword();
	public abstract LocalDate getPasswordExpiryDate();
}
