package egov.framework.plms.sub.lime.bean.mvc.entity.board;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import egov.framework.plms.main.bean.mvc.entity.file.FileInfoDTO;
import egov.framework.plms.main.bean.mvc.entity.file.FileInfoVO;
import egov.framework.plms.main.bean.mvc.entity.organization.UserInfoDTO;
import egov.framework.plms.main.bean.mvc.entity.organization.UserInfoVO;
import egov.framework.plms.main.core.model.able.Convertable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class LimeNoticeBoardVO implements Convertable<LimeNoticeBoardDTO> {

	private Integer noticeId;
	private String writerId;
	private UserInfoVO writer;
	private String officeCode;
	private String officeName;
	private Character fixYN;
	private String title;
	private String contents;
	private String regDateTime;
	private String modDateTime;
	private String delYN;
	
	private List<FileInfoVO> fileList;
	
	/********paging********/
	private Integer rowNum;
	private String startDate;
	private String endDate;
	private Integer pageNo;
	private Integer pageCnt;
	
	@Builder(builderClassName = "init", builderMethodName = "initVO")
	public LimeNoticeBoardVO(LimeNoticeBoardDTO dto) {
		this.noticeId = dto.getNoticeId();
		this.writerId = dto.getWriterId();
		this.writer = Optional.ofNullable(dto.getWriter()).map(UserInfoDTO::convert).orElse(null);
		this.officeCode = dto.getOfficeCode();
		this.officeName = dto.getOfficeName();
		this.fixYN = dto.getFixYN();
		this.title = dto.getTitle();
		this.contents = dto.getContents();
		this.regDateTime = dto.getRegDateTime();
		this.modDateTime = dto.getModDateTime();
		this.delYN = dto.getDelYN();
		
		this.fileList = Optional.ofNullable(dto.getFileList()).map(list -> list.stream().map(FileInfoDTO::convert).collect(Collectors.toList())).orElse(null);
		
		this.rowNum = dto.getRowNum();
		this.startDate = dto.getStartDate();
		this.endDate = dto.getEndDate();
		this.pageNo = dto.getPageNo();
		this.pageCnt = dto.getPageCnt();
	}
	
	@Override
	public LimeNoticeBoardDTO convert() {
		// TODO Auto-generated method stub
		return LimeNoticeBoardDTO.initDTO().vo(this).build();
	}
	
	public Integer getOffset() {
		if(this.pageNo != null && this.pageCnt != null) {
			return (this.pageNo - 1) * this.pageCnt;
		}else {
			return null;
		}
	}
	
	public Integer getLimit() {
		if(this.pageCnt != null) {
			return this.pageCnt;
		}else {
			return 10;
		}
	}
	
}
