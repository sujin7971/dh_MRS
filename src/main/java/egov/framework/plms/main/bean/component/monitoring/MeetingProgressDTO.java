package egov.framework.plms.main.bean.component.monitoring;

import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL) 
public class MeetingProgressDTO{
	private Integer meetingKey;
	private Set<String> entrySet;
	private String streamer;
	private String streamerSessionId;
	private Integer fileKey;
	private Integer pageno;
	@Deprecated
	private String screenJSON;
	
	@Builder(builderClassName = "init", builderMethodName = "initDTO")
	public MeetingProgressDTO(MeetingProgressVO vo) {
		this.meetingKey = vo.getMeetingKey();
		this.entrySet = (Set<String>) vo.getEntryMap().values().stream().collect(Collectors.toSet());
		this.streamer = vo.getStreamer();
		this.streamerSessionId = vo.getStreamerSessionId();
		this.fileKey = vo.getFileKey();
		this.pageno = vo.getPageno();
		this.screenJSON = vo.getScreenJSON();
	}
}
