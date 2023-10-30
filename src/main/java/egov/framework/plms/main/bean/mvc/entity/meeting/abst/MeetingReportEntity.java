package egov.framework.plms.main.bean.mvc.entity.meeting.abst;

import egov.framework.plms.main.core.model.enums.meeting.ReportStatus;

public abstract class MeetingReportEntity {
	public abstract Integer getMeetingId();
	public abstract String getReporterId();
	public abstract String getReportContents();
	public abstract Integer getReportFileId();
	public abstract ReportStatus getReportStatus();
}
