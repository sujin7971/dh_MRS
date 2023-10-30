package egov.framework.plms.sub.ewp.bean.mvc.mapper.maria.board;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.sub.ewp.bean.mvc.entity.board.EwpNoticeBoardVO;

@Mapper
public interface EwpNoticeBoardMapper {

	public List<EwpNoticeBoardVO> getNoticeList(Map<String, Object> params);
	public EwpNoticeBoardVO getNoticeOne(Integer noticeId);
	public Integer postNotice(EwpNoticeBoardVO params);
	public Integer deleteNotice(Integer noticeId);
	public Integer putNotice(EwpNoticeBoardVO params);
	
	//공지 고정/미고정 조회
	public List<EwpNoticeBoardVO> getFixedNoticeList(Map<String, Object> params);
	public List<EwpNoticeBoardVO> getNotfixedNoticeList(Map<String, Object> params);
	public List<EwpNoticeBoardVO> getNoticeList(EwpNoticeBoardVO params);
	public Integer getNoticeListCnt(EwpNoticeBoardVO params);
}
