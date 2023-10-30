package egov.framework.plms.main.bean.mvc.service.meeting.abst;

import java.util.Optional;

import org.apache.commons.text.StringEscapeUtils;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingReportModelVO;
import egov.framework.plms.main.bean.mvc.mapper.meeting.abst.MeetingReportAbstractMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * 회의 회의록에 관한 추상 서비스 클래스입니다.
 * 이 클래스는 MeetingReportModelVO 타입을 상속받은 VO 클래스를 통해 서비스를 제공합니다.
 * 실제 서비스 클래스는 이 추상 클래스를 상속받아 구현되며, 해당 VO 클래스에 대한 매퍼를 이용하여 데이터베이스와의 상호작용을 담당합니다.
 * 
 * @author mckim
 * @version 1.0
 * @since 2023. 5. 1
 * @param <T> MeetingReportModelVO를 상속받은 VO 클래스의 타입
 */
@Slf4j
public abstract class MeetingReportAbstractService<T extends MeetingReportModelVO> {
	protected final MeetingReportAbstractMapper<T> mapper;
	protected final PolicyFactory reportPolicy;
	
	public MeetingReportAbstractService(MeetingReportAbstractMapper<T> mapper) {
		this.mapper = mapper;
		this.reportPolicy = new HtmlPolicyBuilder()
				.allowUrlProtocols("http", "https")
				.allowElements(
					"a", "abbr", "acronym", "address", "area", 
					"b", "big", "blockquote", "br", "button", 
					"caption", "center", "cite", "code", "col", 
					"colgroup", "dd", "del", "dfn", "dir", 
					"div", "dl", "dt", "em", "fieldset", 
					"font", "form", "h1", "h2", "h3", 
					"h4", "h5", "h6", "hr", "i", 
					"img", "input", "ins", "kbd", "label", 
					"legend", "li", "map", "menu", "ol", 
					"optgroup", "option", "p", "pre", "q", 
					"s", "samp", "select", "small", "span", 
					"strike", "strong", "sub", "sup", "table", 
					"tbody", "td", "textarea", "tfoot", "th", 
					"thead", "tr", "tt", "u", "ul", "var")
				.allowAttributes("id", "class", "name", "data", "value", "style")
					.globally()
				.allowAttributes("src", "alt", "border", "cellpadding", "cellspacing", "style", "width")
					.onElements("img")
				.allowAttributes("style", "width", "summary")
					.onElements("table")
				.allowAttributes("style", "width")
					.onElements("td", "th")
				.toFactory();
	}
	
	public abstract T sanitizeContents(T report);
	
	public boolean postMeetingReport(T report) {
		try {
			Integer result = mapper.postMeetingReport(sanitizeContents(report));
			return (result == 0)?false:true;
		}catch(Exception e){
			log.error("Failed to post MeetingReport with params: {}", report.toString());
			log.error("Failed to post MeetingReport messages: {}", e.toString());
			return false;
		}
	}
	
	public boolean putMeetingReport(T report) {
		try {
			Integer result = mapper.putMeetingReport(sanitizeContents(report));
			return (result == 0)?false:true;
		}catch(Exception e){
			log.error("Failed to put MeetingReport with params: {}", report.toString());
			log.error("Failed to put MeetingReport messages: {}", e.toString());
			return false;
		}
	}
	
	public boolean deleteMeetingReport(Integer meetingId) {
		try {
			Integer result = mapper.deleteMeetingReport(meetingId);
			return (result == 0)?false:true;
		}catch(Exception e){
			log.error("Failed to delete MeetingReport with id: {}", meetingId);
			log.error("Failed to delete MeetingReport messages: {}", e.toString());
			return false;
		}
	}
	
	public Optional<T> getMeetingReportOne(Integer meetingId){
		try {
			T meeting = mapper.getMeetingReportOne(meetingId);
			return Optional.ofNullable(meeting);
		}catch(Exception e){
			log.error("Failed to get one MeetingReport with id: {}", meetingId);
			log.error("Failed to get one MeetingReport messages: {}", e.toString());
			return Optional.empty();
		}
	}
}
