package egov.framework.plms.sub.lime.bean.mvc.service.board;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.mvc.mapper.file.FileInfoMapper;
import egov.framework.plms.main.bean.mvc.service.file.FileInfoService;
import egov.framework.plms.main.core.model.response.ResponseMessage;
import egov.framework.plms.sub.ewp.bean.mvc.entity.board.EwpNoticeBoardVO;
import egov.framework.plms.sub.lime.bean.mvc.entity.board.LimeNoticeBoardVO;
import egov.framework.plms.sub.lime.bean.mvc.mapper.board.LimeNoticeBoardMapper;
import egov.framework.plms.sub.lime.bean.mvc.service.organization.LimeUserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile("lime")
public class LimeNoticeBoardService {
	private final FileInfoService fileServ;
	private final LimeUserInfoService userServ;
	private final LimeNoticeBoardMapper limeNoticeMapper;
	private final FileInfoMapper fileMapper;
	
	//공지 리스트 조회
	public List<LimeNoticeBoardVO> getNoticeList(Map<String, Object> params){
		return limeNoticeMapper.getNoticeList(params);
	}
	
	//공지 상세 내용 조회
	public LimeNoticeBoardVO getNoticeOne(Integer noticeId) {
		LimeNoticeBoardVO rtVO = limeNoticeMapper.getNoticeOne(noticeId);
		log.info("공지 상세내용 결과 ::{}",rtVO);
		return rtVO;
	}
	
	//공지 신규 등록
	public ResponseMessage postNotice(LimeNoticeBoardVO params) {
		Integer result = limeNoticeMapper.postNotice(params);
		if(result == 1) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
					.message(ResponseMessage.MessageCode.NOTICE.POST_SUCCESS.value())
					.data(params.getNoticeId())
					.build();
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
					.message(ResponseMessage.MessageCode.NOTICE.POST_FAIL.value())
					.build();
		}
	}
	
	//공지 삭제 처리
	public ResponseMessage deleteNotice(Integer noticeId) {
		Integer result = limeNoticeMapper.deleteNotice(noticeId);
		if(result == 1) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
					.message(ResponseMessage.MessageCode.NOTICE.DELETE_SUCCESS.value())
					.build();
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
					.message(ResponseMessage.MessageCode.NOTICE.DELETE_FAIL.value())
					.build();
		}
	}
	//공지 수정 
	public ResponseMessage putNotice(LimeNoticeBoardVO params) {
		Integer result = limeNoticeMapper.putNotice(params);
		if(result == 1) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
					.message(ResponseMessage.MessageCode.NOTICE.PUT_SUCCESS.value())
					.build();
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
					.message(ResponseMessage.MessageCode.NOTICE.PUT_FAIL.value())
					.build();
		}
	}

	public List<LimeNoticeBoardVO> getNoticeList(LimeNoticeBoardVO params) {
		// TODO Auto-generated method stub
		List<LimeNoticeBoardVO> noticeList = limeNoticeMapper.getNoticeList(params);
		return noticeList.stream().map(notice -> notice.toBuilder().writer(userServ.selectUserInfoOne(notice.getWriterId()).orElse(null)).build()).collect(Collectors.toList());
	}
	public Integer getNoticeListCnt(LimeNoticeBoardVO params) {
		// TODO Auto-generated method stub
		return limeNoticeMapper.getNoticeListCnt(params);
	}
	
}
