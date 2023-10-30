package egov.framework.plms.main.bean.mvc.entity.meeting.abst;

import java.time.LocalDate;
import java.time.LocalDateTime;

import egov.framework.plms.main.core.model.able.Convertable;
import egov.framework.plms.main.core.model.enums.meeting.ApprovalStatus;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import egov.framework.plms.main.core.model.enums.meeting.ScheduleType;

public abstract class MeetingScheduleModelDTO extends MeetingScheduleEntity implements Convertable<MeetingScheduleModelVO>{
	public abstract void setScheduleId(Integer value);
	public abstract void setMeetingId(Integer value);
	public abstract void setExternalReservationId(String value);
	public abstract void setWriterId(String value);
	public abstract void setOfficeCode(String value);
	public abstract void setDeptId(String value);
	public abstract void setRoomId(Integer value);
	public abstract void setAttendeeCnt(Integer value);
	public abstract void setUserDefinedLocation(String value);
	public abstract void setScheduleHost(String value);
	public abstract void setScheduleType(ScheduleType value);
	public abstract void setApprovalStatus(ApprovalStatus value);
	public abstract void setRoomType(RoomType value);
	public abstract void setHoldingDate(LocalDate value);
	public abstract void setBeginDateTime(LocalDateTime value);
	public abstract void setFinishDateTime(LocalDateTime value);
	public abstract void setExpDateTime(LocalDateTime value);
}
