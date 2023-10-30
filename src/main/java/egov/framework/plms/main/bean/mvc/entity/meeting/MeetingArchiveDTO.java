package egov.framework.plms.main.bean.mvc.entity.meeting;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;

import egov.framework.plms.main.bean.mvc.entity.file.FileDetailDTO;
import egov.framework.plms.main.bean.mvc.entity.file.FileDetailVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingArchiveModelDTO;
import egov.framework.plms.main.core.model.able.Pageable;
import egov.framework.plms.main.core.model.enums.common.OrderColumn;
import egov.framework.plms.main.core.model.enums.common.OrderDirection;
import egov.framework.plms.main.core.model.enums.file.RelationType;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MeetingArchiveDTO extends MeetingArchiveModelDTO<FileDetailDTO> implements Pageable {
	private String writerId;
	private Integer scheduleId;
	private String officeCode;
	private String deptId;
	private RoomType roomType;
	private Integer roomId;
	private Integer meetingId;
	private LocalDate holdingDate;
	private LocalDateTime beginDateTime;
	private LocalDateTime finishDateTime;
	private String scheduleHost;
	private String title;
	
	private RelationType relationType;
	private String uploadedFileName;
	private List<FileDetailDTO> files;
	
	/********paging********/
	private Integer rowNum;
	private LocalDate startDate;
	private LocalDate endDate;
	private Integer pageNo;
	private Integer pageCnt;
	
	private OrderColumn orderColumn;
	private OrderDirection orderDirection;
	
	@Builder(builderClassName = "init", builderMethodName = "initDTO")
	public MeetingArchiveDTO(MeetingArchiveVO vo){
		this.writerId = vo.getWriterId();
		this.scheduleId = vo.getScheduleId();
		this.officeCode = vo.getOfficeCode();
		this.deptId = vo.getDeptId();
		this.roomType = vo.getRoomType();
		this.roomId = vo.getRoomId();
		this.meetingId = vo.getMeetingId();
		this.holdingDate = vo.getHoldingDate();
		this.beginDateTime = vo.getBeginDateTime();
		this.finishDateTime = vo.getFinishDateTime();
		this.scheduleHost = vo.getScheduleHost();
		this.title = vo.getTitle();
		this.relationType = vo.getRelationType();
		this.uploadedFileName = vo.getUploadedFileName();
		this.files = Optional.ofNullable(vo.getFiles()).map(files -> files.stream().map(FileDetailVO::convert).collect(Collectors.toList())).orElse(null);
		
		this.rowNum = vo.getRowNum();
		this.startDate = vo.getStartDate();
		this.endDate = vo.getEndDate();
		this.pageNo = vo.getPageNo();
		this.pageCnt = vo.getPageCnt();
	}

	@Override
	public MeetingArchiveVO convert() {
		return MeetingArchiveVO.initVO().dto(this).build();
	}
}
