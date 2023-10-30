package egov.framework.plms.main.bean.component.monitoring;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import egov.framework.plms.main.core.model.able.Convertable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
/**
 * 회의 진행상황을 보관할 클래스
 * @author mckim
 * @see MeetingMonitoringManager
 * @since 2.0.0
 * @version 3.0.0
 */
public class MeetingProgressVO implements Convertable<MeetingProgressDTO> {
	// 회의키
	private Integer meetingKey;
	// 세션 ID 와 유저키 매칭
	private Map<String, String> entryMap;
	private String streamer;
	private String streamerSessionId;
	private Integer fileKey;
	private Integer pageno;
	@Deprecated
	private String screenJSON;
	@Deprecated
	private Integer enterCount;
	@Deprecated
	private Integer deadCount;
	
	private LocalDateTime expDateTime;

	@Override
	public MeetingProgressDTO convert() {
		return MeetingProgressDTO.initDTO().vo(this).build();
	}
	public void addEntry(String sessionId, String userId) {
		entryMap.put(sessionId, userId);
	}
	public void removeEntry(String sessionId) {
		entryMap.remove(sessionId);
	}
	public boolean hasEntry(String sessionId) {
		return entryMap.containsKey(sessionId);
	}
	public int getEntrySize() {
		return entryMap.size();
	}

	public Set<String> getEntryUserIdSet() {
		return new HashSet<String>(entryMap.values());
	}
}
