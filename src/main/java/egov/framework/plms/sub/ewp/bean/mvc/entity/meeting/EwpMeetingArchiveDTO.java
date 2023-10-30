package egov.framework.plms.sub.ewp.bean.mvc.entity.meeting;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingArchiveModelDTO;
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
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class EwpMeetingArchiveDTO extends MeetingArchiveModelDTO<MeetingFileInfoDTO> implements Pageable {
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
	private List<MeetingFileInfoDTO> files;
	
	/********paging********/
	private Integer rowNum;
	private LocalDate startDate;
	private LocalDate endDate;
	private Integer pageNo;
	private Integer pageCnt;
	
	private OrderColumn orderColumn;
	private OrderDirection orderDirection;
	
	@Builder(builderClassName = "init", builderMethodName = "initDTO")
	public EwpMeetingArchiveDTO(EwpMeetingArchiveVO vo){
		this.userKey = vo.getUserKey();
		this.skdKey = vo.getSkdKey();
		this.officeCode = vo.getOfficeCode();
		this.deptId = vo.getDeptId();
		this.reqKey = vo.getReqKey();
		this.roomType = vo.getRoomType();
		this.roomKey = vo.getRoomKey();
		this.meetingKey = vo.getMeetingKey();
		this.holdingDate = vo.getHoldingDate();
		this.beginDateTime = vo.getBeginDateTime();
		this.finishDateTime = vo.getFinishDateTime();
		this.skdHost = vo.getSkdHost();
		this.title = vo.getTitle();
		this.roleType = vo.getRoleType();
		this.originalName = vo.getOriginalName();
		this.files = Optional.ofNullable(vo.getFiles()).map(files -> files.stream().map(MeetingFileInfoVO::convert).collect(Collectors.toList())).orElse(null);
		
		this.rowNum = vo.getRowNum();
		this.startDate = vo.getStartDate();
		this.endDate = vo.getEndDate();
		this.pageNo = vo.getPageNo();
		this.pageCnt = vo.getPageCnt();
	}

	@Override
	public EwpMeetingArchiveVO convert() {
		return EwpMeetingArchiveVO.initVO().dto(this).build();
	}

	@Override
	public void setWriterId(String value) {
		// TODO Auto-generated method stub
		this.userKey = value;
	}

	@Override
	public void setScheduleId(Integer value) {
		// TODO Auto-generated method stub
		this.skdKey = value;
	}

	@Override
	public void setMeetingId(Integer value) {
		// TODO Auto-generated method stub
		this.meetingKey = value;
	}

	@Override
	public void setRoomId(Integer value) {
		// TODO Auto-generated method stub
		this.roomKey = value;
	}

	@Override
	public void setScheduleHost(String value) {
		// TODO Auto-generated method stub
		this.skdHost =  value;
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
