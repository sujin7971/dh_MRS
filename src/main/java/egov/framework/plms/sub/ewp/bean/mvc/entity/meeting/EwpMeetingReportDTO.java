package egov.framework.plms.sub.ewp.bean.mvc.entity.meeting;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingReportModelDTO;
import egov.framework.plms.main.core.model.enums.meeting.ReportStatus;
import egov.framework.plms.sub.ewp.bean.mvc.entity.organization.EwpUserInfoDTO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.organization.EwpUserInfoVO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class EwpMeetingReportDTO extends MeetingReportModelDTO {
	private Integer meetingKey;
	private Integer fileKey;
	private String contents;
	private String reporterKey;
	private EwpUserInfoDTO reporter;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss") 
	@JsonDeserialize(using = LocalDateTimeDeserializer.class) 
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime modDateTime;
	private ReportStatus reportStatus;
	private List<EwpMeetingAttendeeDTO> attendeeList;
	
	private MultipartFile pdf;
	
	@Builder(builderClassName = "init", builderMethodName = "initDTO")
	public EwpMeetingReportDTO(EwpMeetingReportVO vo) {
		this.meetingKey = vo.getMeetingKey();
		this.fileKey = vo.getFileKey();
		this.contents = vo.getContents();
		this.reporterKey = vo.getReporterKey();
		this.reporter = Optional.ofNullable(vo.getReporter()).map(EwpUserInfoVO::convert).orElseGet(()->null);
		this.modDateTime = vo.getModDateTime();
		this.reportStatus = vo.getReportStatus();
	}

	@Override
	public EwpMeetingReportVO convert() {
		return EwpMeetingReportVO.initVO().dto(this).build();
	}

	@Override
	public void setMeetingId(Integer value) {
		// TODO Auto-generated method stub
		this.meetingKey = value;
	}

	@Override
	public void setReporterId(String value) {
		// TODO Auto-generated method stub
		this.reporterKey = value;
	}

	@Override
	public void setReportFileId(Integer value) {
		// TODO Auto-generated method stub
		this.fileKey = value;
	}

	@Override
	public Integer getMeetingId() {
		// TODO Auto-generated method stub
		return this.meetingKey;
	}

	@Override
	public String getReporterId() {
		// TODO Auto-generated method stub
		return this.reporterKey;
	}

	@Override
	public Integer getReportFileId() {
		// TODO Auto-generated method stub
		return this.fileKey;
	}

	@Override
	public void setReportContents(String value) {
		// TODO Auto-generated method stub
		this.contents = value;
	}

	@Override
	public String getReportContents() {
		// TODO Auto-generated method stub
		return this.contents;
	}
}
