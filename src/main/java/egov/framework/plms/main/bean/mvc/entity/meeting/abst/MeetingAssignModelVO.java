package egov.framework.plms.main.bean.mvc.entity.meeting.abst;

import egov.framework.plms.main.core.model.able.Convertable;
import egov.framework.plms.main.core.model.enums.meeting.ApprovalStatus;
import egov.framework.plms.main.core.model.enums.meeting.MeetingStatus;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import egov.framework.plms.main.core.model.enums.meeting.ScheduleType;

public abstract class MeetingAssignModelVO extends MeetingAssignEntity implements Convertable<MeetingAssignModelDTO> {
	public abstract MeetingScheduleModelVO getMeetingSchedule();
	public abstract MeetingInfoModelVO getMeetingInfo();
}
