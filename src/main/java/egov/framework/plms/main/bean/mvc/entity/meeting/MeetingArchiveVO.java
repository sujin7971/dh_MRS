package egov.framework.plms.main.bean.mvc.entity.meeting;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import egov.framework.plms.main.bean.mvc.entity.file.FileDetailDTO;
import egov.framework.plms.main.bean.mvc.entity.file.FileDetailVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingArchiveModelVO;
import egov.framework.plms.main.core.model.able.Pageable;
import egov.framework.plms.main.core.model.enums.common.OrderColumn;
import egov.framework.plms.main.core.model.enums.common.OrderDirection;
import egov.framework.plms.main.core.model.enums.file.RelationType;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * file_info테이블에 meeting_schedule 테이블과 meeting_info테이블의 조인 뷰 테이블 view_meeting_archive_info의 데이터를 위한 VO 모델<br>
 * 파일함 페이지에서 데이터 조회 및 표시에 사용
 * @author mckim
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class MeetingArchiveVO extends MeetingArchiveModelVO<FileDetailVO> implements Pageable{
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
	private List<FileDetailVO> files;
	
	/********paging********/
	private Integer rowNum;
	private LocalDate startDate;
	private LocalDate endDate;
	private Integer pageNo;
	private Integer pageCnt;
	
	private OrderColumn orderColumn;
	private OrderDirection orderDirection;
	
	@Builder(builderClassName = "init", builderMethodName = "initVO")
	public MeetingArchiveVO(MeetingArchiveDTO dto){
		this.writerId = dto.getWriterId();
		this.scheduleId = dto.getScheduleId();
		this.officeCode = dto.getOfficeCode();
		this.deptId = dto.getDeptId();
		this.roomType = dto.getRoomType();
		this.roomId = dto.getRoomId();
		this.meetingId = dto.getMeetingId();
		this.holdingDate = dto.getHoldingDate();
		this.beginDateTime = dto.getBeginDateTime();
		this.finishDateTime = dto.getFinishDateTime();
		this.scheduleHost = dto.getScheduleHost();
		this.title = dto.getTitle();
		this.relationType = dto.getRelationType();
		this.uploadedFileName = dto.getUploadedFileName();
		this.files = Optional.ofNullable(dto.getFiles()).map(files -> files.stream().map(FileDetailDTO::convert).collect(Collectors.toList())).orElse(null);
		
		this.rowNum = dto.getRowNum();
		this.startDate = dto.getStartDate();
		this.endDate = dto.getEndDate();
		this.pageNo = dto.getPageNo();
		this.pageCnt = dto.getPageCnt();
	}

	@Override
	public MeetingArchiveDTO convert() {
		return MeetingArchiveDTO.initDTO().vo(this).build();
	}
	
}
