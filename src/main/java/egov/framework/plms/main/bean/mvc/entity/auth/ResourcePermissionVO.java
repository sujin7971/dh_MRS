package egov.framework.plms.main.bean.mvc.entity.auth;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
/**
 * 리소스에 대한 권한을 담을 모델
 * @author judi1
 *
 */
public class ResourcePermissionVO {
	private Integer permId;
	private Integer permLvl;
	private String permName;
	private String permDesc;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS") 
	@JsonDeserialize(using = LocalDateTimeDeserializer.class) 
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime regDateTime;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS") 
	@JsonDeserialize(using = LocalDateTimeDeserializer.class) 
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime modDateTime;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS") 
	@JsonDeserialize(using = LocalDateTimeDeserializer.class) 
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime expDateTime;
	private Character delYN;
}
