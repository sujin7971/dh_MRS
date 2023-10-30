package egov.framework.plms.sub.ewp.bean.mvc.entity.meeting;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import egov.framework.plms.main.bean.mvc.entity.file.abst.FileDetailModelVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingArchiveModelDTO;
import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingArchiveModelVO;
import egov.framework.plms.main.core.model.able.Pageable;
import egov.framework.plms.main.core.model.enums.common.OrderColumn;
import egov.framework.plms.main.core.model.enums.common.OrderDirection;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import egov.framework.plms.sub.ewp.bean.mvc.entity.file.MeetingFileInfoDTO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.file.MeetingFileInfoVO;
import egov.framework.plms.sub.ewp.core.model.enums.FileRole;
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
public class EwpMeetingArchiveVO extends MeetingArchiveModelVO<MeetingFileInfoVO> implements Pageable{
	private String userKey;
	private Integer skdKey;
	private String officeCode;
	private String deptId;
	private Integer reqKey;
	private RoomType roomType;
	private Integer roomKey;
	private Integer meetingKey;
	private LocalDate holdingDate;
	private LocalDateTime beginDateTime;
	private LocalDateTime finishDateTime;
	private String skdHost;
	private String title;
	
	private FileRole roleType;
	private String originalName;
	private List<MeetingFileInfoVO> files;
	
	/********paging********/
	private Integer rowNum;
	private LocalDate startDate;
	private LocalDate endDate;
	private Integer pageNo;
	private Integer pageCnt;
	
	private OrderColumn orderColumn;
	private OrderDirection orderDirection;
	
	@Builder(builderClassName = "init", builderMethodName = "initVO")
	public EwpMeetingArchiveVO(EwpMeetingArchiveDTO dto){
		this.userKey = dto.getUserKey();
		this.skdKey = dto.getSkdKey();
		this.officeCode = dto.getOfficeCode();
		this.deptId = dto.getDeptId();
		this.reqKey = dto.getReqKey();
		this.roomType = dto.getRoomType();
		this.roomKey = dto.getRoomKey();
		this.meetingKey = dto.getMeetingKey();
		this.holdingDate = dto.getHoldingDate();
		this.beginDateTime = dto.getBeginDateTime();
		this.finishDateTime = dto.getFinishDateTime();
		this.skdHost = dto.getSkdHost();
		this.title = dto.getTitle();
		this.roleType = dto.getRoleType();
		this.originalName = dto.getOriginalName();
		this.files = Optional.ofNullable(dto.getFiles()).map(files -> files.stream().map(MeetingFileInfoDTO::convert).collect(Collectors.toList())).orElse(null);
		
		this.rowNum = dto.getRowNum();
		this.startDate = dto.getStartDate();
		this.endDate = dto.getEndDate();
		this.pageNo = dto.getPageNo();
		this.pageCnt = dto.getPageCnt();
	}

	@Override
	public EwpMeetingArchiveDTO convert() {
		return EwpMeetingArchiveDTO.initDTO().vo(this).build();
	}

	@Override
	public String getWriterId() {
		// TODO Auto-generated method stub
		return this.userKey;
	}

	@Override
	public Integer getScheduleId() {
		// TODO Auto-generated method stub
		return this.skdKey;
	}

	@Override
	public Integer getMeetingId() {
		// TODO Auto-generated method stub
		return this.meetingKey;
	}

	@Override
	public Integer getRoomId() {
		// TODO Auto-generated method stub
		return this.roomKey;
	}

	@Override
	public String getScheduleHost() {
		// TODO Auto-generated method stub
		return this.skdHost;
	}
}
