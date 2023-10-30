package egov.framework.plms.main.bean.mvc.entity.meeting;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingAttendeeModelDTO;
import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingReportModelDTO;
import egov.framework.plms.main.bean.mvc.entity.organization.abst.UserInfoModelDTO;
import egov.framework.plms.main.bean.mvc.entity.organization.abst.UserInfoModelVO;
import egov.framework.plms.main.core.model.enums.meeting.ReportStatus;
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
public class MeetingReportDTO extends MeetingReportModelDTO {
	private Integer meetingId;
	private String reporterId;
	private UserInfoModelDTO reporter;
	private String reportContents;
	private Integer reportFileId;
	private ReportStatus reportStatus;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss") 
	@JsonDeserialize(using = LocalDateTimeDeserializer.class) 
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime modDateTime;
	private List<MeetingAttendeeModelDTO> attendeeList;
	
	private MultipartFile reportFile;
	
	@Builder(builderClassName = "init", builderMethodName = "initDTO")
	public MeetingReportDTO(MeetingReportVO vo) {
		this.meetingId = vo.getMeetingId();
		this.reportFileId = vo.getReportFileId();
		this.reportContents = vo.getReportContents();
		this.reporterId = vo.getReporterId();
		this.reporter = Optional.ofNullable(vo.getReporter()).map(UserInfoModelVO::convert).orElseGet(()->null);
		this.modDateTime = vo.getModDateTime();
		this.reportStatus = vo.getReportStatus();
	}

	@Override
	public MeetingReportVO convert() {
		return MeetingReportVO.initVO().dto(this).build();
	}
}
