package egov.framework.plms.sub.lime.bean.mvc.service.meeting;

import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingReportVO;
import egov.framework.plms.main.bean.mvc.service.meeting.abst.MeetingReportAbstractService;
import egov.framework.plms.sub.lime.bean.mvc.mapper.meeting.LimeMeetingReportMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile("lime")
@Primary
public class LimeMeetingReportService extends MeetingReportAbstractService<MeetingReportVO>{
	private final LimeMeetingReportMapper mapper;
	public LimeMeetingReportService(@Autowired LimeMeetingReportMapper mapper) {
		super(mapper);
		this.mapper = mapper;
	}
	
	@Override
	public MeetingReportVO sanitizeContents(MeetingReportVO report) {
		String escapedContents = report.getReportContents();
		String unescapedContents = StringEscapeUtils.unescapeHtml4(escapedContents);
		String safeContents = super.reportPolicy.sanitize(unescapedContents);
		return report.toBuilder().reportContents(safeContents).build();
	}
}
