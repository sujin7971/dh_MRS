package egov.framework.plms.main.bean.mvc.entity.meeting.abst;

import java.time.LocalDate;
import java.time.LocalDateTime;

import egov.framework.plms.main.core.model.enums.meeting.RoomType;

public abstract class MeetingArchiveEntity {
	public abstract String getWriterId();
	public abstract Integer getScheduleId();
	public abstract Integer getMeetingId();
	public abstract RoomType getRoomType();
	public abstract Integer getRoomId();
	public abstract LocalDate getHoldingDate();
	public abstract LocalDateTime getBeginDateTime();
	public abstract LocalDateTime getFinishDateTime();
	public abstract String getScheduleHost();
	public abstract String getTitle();
}
