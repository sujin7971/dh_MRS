package egov.framework.plms.main.bean.mvc.entity.meeting;

import java.time.LocalDateTime;
import java.util.Optional;

import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingReportModelVO;
import egov.framework.plms.main.bean.mvc.entity.organization.abst.UserInfoModelDTO;
import egov.framework.plms.main.bean.mvc.entity.organization.abst.UserInfoModelVO;
import egov.framework.plms.main.bean.mvc.entity.room.abst.RoomInfoModelVO;
import egov.framework.plms.main.core.model.enums.meeting.ReportStatus;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class MeetingReportVO extends MeetingReportModelVO {
	private Integer meetingId;
	private String reporterId;
	private UserInfoModelVO reporter;
	private String reportContents;
	private Integer reportFileId;
	private ReportStatus reportStatus;
	private LocalDateTime modDateTime;
	@Builder(builderClassName = "init", builderMethodName = "initVO")
	public MeetingReportVO(MeetingReportDTO dto) {
		this.meetingId = dto.getMeetingId();
		this.reportContents = dto.getReportContents();
		this.reporterId = dto.getReporterId();
		this.reporter = Optional.ofNullable(dto.getReporter()).map(UserInfoModelDTO::convert).orElseGet(()->null);
		this.modDateTime = dto.getModDateTime();
		this.reportStatus = dto.getReportStatus();
	}


	@Override
	public MeetingReportDTO convert() {
		return MeetingReportDTO.initDTO().vo(this).build();
	}
}
