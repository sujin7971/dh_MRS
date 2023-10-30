package egov.framework.plms.sub.ewp.bean.mvc.entity.meeting;

import java.time.LocalDateTime;
import java.util.Optional;

import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingReportModelVO;
import egov.framework.plms.main.core.model.enums.meeting.ReportStatus;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import egov.framework.plms.sub.ewp.bean.mvc.entity.organization.EwpUserInfoDTO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.organization.EwpUserInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.room.EwpRoomInfoVO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class EwpMeetingReportVO extends MeetingReportModelVO {
	private Integer meetingKey;
	private Integer fileKey;
	private String contents;
	private String reporterKey;
	private EwpUserInfoVO reporter;
	private LocalDateTime modDateTime;
	private ReportStatus reportStatus;
	@Builder(builderClassName = "init", builderMethodName = "initVO")
	public EwpMeetingReportVO(EwpMeetingReportDTO dto) {
		this.meetingKey = dto.getMeetingKey();
		this.fileKey = dto.getFileKey();
		this.contents = dto.getContents();
		this.reporterKey = dto.getReporterKey();
		this.reporter = Optional.ofNullable(dto.getReporter()).map(EwpUserInfoDTO::convert).orElseGet(()->null);
		this.modDateTime = dto.getModDateTime();
		this.reportStatus = dto.getReportStatus();
	}

	@Override
	public EwpMeetingReportDTO convert() {
		return EwpMeetingReportDTO.initDTO().vo(this).build();
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
	public String getReportContents() {
		// TODO Auto-generated method stub
		return this.contents;
	}
}
