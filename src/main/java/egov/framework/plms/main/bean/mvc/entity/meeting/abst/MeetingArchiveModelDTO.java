package egov.framework.plms.main.bean.mvc.entity.meeting.abst;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import egov.framework.plms.main.bean.mvc.entity.file.abst.FileDetailModelDTO;
import egov.framework.plms.main.core.model.able.Convertable;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;

public abstract class MeetingArchiveModelDTO<T extends FileDetailModelDTO> extends MeetingArchiveEntity implements Convertable<MeetingArchiveModelVO>{
	public abstract void setWriterId(String value);
	public abstract void setScheduleId(Integer value);
	public abstract void setMeetingId(Integer value);
	public abstract void setRoomType(RoomType value);
	public abstract void setRoomId(Integer value);
	public abstract void setHoldingDate(LocalDate value);
	public abstract void setBeginDateTime(LocalDateTime value);
	public abstract void setFinishDateTime(LocalDateTime value);
	public abstract void setScheduleHost(String value);
	public abstract void setTitle(String value);
	public abstract List<T> getFiles();
}
