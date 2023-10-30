package egov.framework.plms.main.bean.mvc.entity.file;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import egov.framework.plms.main.core.model.enums.file.ConversionStep;
import egov.framework.plms.main.core.model.enums.file.ConversionType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class FileConvertVO {
	private Integer cvtId;
	private Integer fileId;
	private ConversionType conversionType;
	private Integer cvtPriority;
	private Integer cvtCount;
	private ConversionStep cvtStep;
	private String sourcePath;
	private String destinationPath;
	private String errMsg;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime startDateTime;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime endDateTime;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime regDateTime;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime modDateTime;
}
