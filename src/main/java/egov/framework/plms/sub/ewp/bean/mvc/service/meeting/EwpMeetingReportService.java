package egov.framework.plms.sub.ewp.bean.mvc.service.meeting;

import java.util.List;

import org.apache.commons.text.StringEscapeUtils;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.mvc.service.meeting.abst.MeetingReportAbstractService;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingReportVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpReportOpinionVO;
import egov.framework.plms.sub.ewp.bean.mvc.mapper.maria.meeting.EwpMeetingReportMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile("ewp")
@Primary
public class EwpMeetingReportService extends MeetingReportAbstractService<EwpMeetingReportVO>{
	private final EwpMeetingReportMapper mapper;
	protected final PolicyFactory opinionPolicy;
	public EwpMeetingReportService(@Autowired EwpMeetingReportMapper mapper) {
		super(mapper);
		this.mapper = mapper;
		this.opinionPolicy = new HtmlPolicyBuilder().toFactory();
	}
	
	@Override
	public EwpMeetingReportVO sanitizeContents(EwpMeetingReportVO report) {
		String escapedContents = report.getReportContents();
		String unescapedContents = StringEscapeUtils.unescapeHtml4(escapedContents);
		String safeContents = super.reportPolicy.sanitize(unescapedContents);
		return report.toBuilder().contents(safeContents).build();
	}
	
	public EwpReportOpinionVO sanitizeOpinion(EwpReportOpinionVO opinion) {
		String escapedContents = opinion.getComment();
		String unescapedContents = StringEscapeUtils.unescapeHtml4(escapedContents);
		String safeContents = this.opinionPolicy.sanitize(unescapedContents);
		return opinion.toBuilder().comment(safeContents).build();
	}
	
	/**
	 * 작성된 의견 리스트 출력
	 * @param params
	 * @return
	 */
	public List<EwpReportOpinionVO> getReportOpnList(Integer meetingKey){
		return mapper.getMeetingReportOpnList(meetingKey);
	}
	
	/**
	 * 회의록 의견 등록
	 * @param opnVO
	 */
	public boolean postMeetingReportOpn(EwpReportOpinionVO opnVO) {
		try {
			Integer result = mapper.postMeetingReportOpn(sanitizeOpinion(opnVO));
			return (result == 0)?false:true;
		}catch(Exception e){
			log.error("Failed to post MeetingReportOpn with params: {}", opnVO.toString());
			log.error("Failed to post MeetingReportOpn messages: {}", e.toString());
			return false;
		}
	}
	
	/**
	 * 회의록 의견 삭제
	 * @param meetingKey 회의 고유키
	 * @param opnId 의견 고유키
	 * @param userKey 작성자 고유키
	 * @return
	 */
	public boolean deleteMeetingReportOpn(Integer meetingKey, Integer opnId, String userKey) {
		EwpReportOpinionVO params = EwpReportOpinionVO.builder().meetingKey(meetingKey).writerKey(userKey).opnId(opnId).build();
		try {
			Integer result = mapper.deleteMeetingReportOpn(params);
			return (result == 0)?false:true;
		}catch(Exception e){
			log.error("Failed to delete MeetingReportOpn with params: {}", params);
			log.error("Failed to delete MeetingReportOpn messages: {}", e.toString());
			return false;
		}
	}
	
	/**
	 * 회의록 모든 의견 삭제
	 * @param meetingKey 회의 고유키
	 * @return
	 */
	public boolean deleteMeetingReportOpnAll(Integer meetingKey) {
		try {
			Integer result = mapper.deleteMeetingReportOpnAll(meetingKey);
			return (result == 0)?false:true;
		}catch(Exception e){
			log.error("Failed to delete all MeetingReportOpn with id: {}", meetingKey);
			log.error("Failed to delete all MeetingReportOpn messages: {}", e.toString());
			return false;
		}
	}
}
