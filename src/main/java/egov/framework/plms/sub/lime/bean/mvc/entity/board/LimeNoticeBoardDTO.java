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
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class LimeNoticeBoardDTO implements Convertable<LimeNoticeBoardVO>{

	private Integer noticeId;
	private String writerId;
	private UserInfoDTO writer;
	private String officeCode;
	private String officeName;
	private Character fixYN;
	private String title;
	private String contents;
	private String regDateTime;
	private String modDateTime;
	private String delYN;
	
	private List<FileInfoDTO> fileList;
	
	/********paging********/
	private Integer rowNum;
	private String startDate;
	private String endDate;
	private Integer pageNo;
	private Integer pageCnt;
	
	@Builder(builderClassName = "init", builderMethodName = "initDTO")
	public LimeNoticeBoardDTO(LimeNoticeBoardVO vo) {
		this.noticeId = vo.getNoticeId();
		this.writerId = vo.getWriterId();
		this.writer = Optional.ofNullable(vo.getWriter()).map(UserInfoVO::convert).orElse(null);
		this.officeCode = vo.getOfficeCode();
		this.officeName = vo.getOfficeName();
		this.fixYN = vo.getFixYN();
		this.title = vo.getTitle();
		this.contents = vo.getContents();
		this.regDateTime = vo.getRegDateTime();
		this.modDateTime = vo.getModDateTime();
		this.delYN = vo.getDelYN();
		
		this.fileList = Optional.ofNullable(vo.getFileList()).map(list -> list.stream().map(FileInfoVO::convert).collect(Collectors.toList())).orElse(null);
		
		this.rowNum = vo.getRowNum();
		this.startDate = vo.getStartDate();
		this.endDate = vo.getEndDate();
		this.pageNo = vo.getPageNo();
		this.pageCnt = vo.getPageCnt();
	}
	
	@Override
	public LimeNoticeBoardVO convert() {
		// TODO Auto-generated method stub
		return LimeNoticeBoardVO.initVO().dto(this).build();
	}
}
