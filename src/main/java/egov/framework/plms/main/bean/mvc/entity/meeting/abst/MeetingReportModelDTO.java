package egov.framework.plms.main.bean.mvc.entity.meeting.abst;

import egov.framework.plms.main.core.model.able.Convertable;
import egov.framework.plms.main.core.model.enums.meeting.ReportStatus;

public abstract class MeetingReportModelDTO extends MeetingReportEntity implements Convertable<MeetingReportModelVO>{
	public abstract void setMeetingId(Integer value);
	public abstract void setReporterId(String value);
	public abstract void setReportContents(String value);
	public abstract void setReportFileId(Integer value);
	public abstract void setReportStatus(ReportStatus value);
}
