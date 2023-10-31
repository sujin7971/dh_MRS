package egov.framework.plms.sub.lime.bean.mvc.mapper.board;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.sub.lime.bean.mvc.entity.board.LimeNoticeBoardVO;

@Mapper
public interface LimeNoticeBoardMapper {

	public List<LimeNoticeBoardVO> getNoticeList(Map<String, Object> params);
	public LimeNoticeBoardVO getNoticeOne(Integer noticeId);
	public Integer postNotice(LimeNoticeBoardVO params);
	public Integer deleteNotice(Integer noticeId);
	public Integer putNotice(LimeNoticeBoardVO params);
	
	//공지 고정/미고정 조회
	public List<LimeNoticeBoardVO> getFixedNoticeList(Map<String, Object> params);
	public List<LimeNoticeBoardVO> getNotfixedNoticeList(Map<String, Object> params);
	public List<LimeNoticeBoardVO> getNoticeList(LimeNoticeBoardVO params);
	public Integer getNoticeListCnt(LimeNoticeBoardVO params);
}
