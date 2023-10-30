package egov.framework.plms.main.bean.mvc.service.meeting;

import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingReportVO;
import egov.framework.plms.main.bean.mvc.mapper.meeting.MeetingReportMapper;
import egov.framework.plms.main.bean.mvc.service.meeting.abst.MeetingReportAbstractService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("MeetingReportService")
public class MeetingReportService extends MeetingReportAbstractService<MeetingReportVO>{
	
	public MeetingReportService(@Autowired MeetingReportMapper mapper) {
		super(mapper);
	}

	@Override
	public MeetingReportVO sanitizeContents(MeetingReportVO report) {
		String escapedContents = report.getReportContents();
		String unescapedContents = StringEscapeUtils.unescapeHtml4(escapedContents);
		String safeContents = super.reportPolicy.sanitize(unescapedContents);
		return report.toBuilder().reportContents(safeContents).build();
	}
}
