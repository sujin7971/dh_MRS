package egov.framework.plms.sub.ewp.bean.mvc.entity.file;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class MeetingFileConvertVO {
	private Integer cvtKey;
	private Integer fileKey;
	private Integer meetingKey;
	private Integer cvtPriority;
	private Integer cvtCount;
	private Integer cvtStep;
	private String errMsg;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime startDT;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime endDT;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime regDT;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime modDT;
}
