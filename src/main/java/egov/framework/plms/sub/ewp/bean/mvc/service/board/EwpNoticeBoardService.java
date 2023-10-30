package egov.framework.plms.sub.ewp.bean.mvc.service.board;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.mvc.entity.file.FileRelationVO;
import egov.framework.plms.main.bean.mvc.mapper.file.FileInfoMapper;
import egov.framework.plms.main.bean.mvc.service.file.FileInfoService;
import egov.framework.plms.main.bean.mvc.service.file.FileRelationService;
import egov.framework.plms.main.core.model.enums.file.RelatedEntityType;
import egov.framework.plms.main.core.model.enums.file.RelationType;
import egov.framework.plms.main.core.model.response.ResponseMessage;
import egov.framework.plms.sub.ewp.bean.mvc.entity.board.EwpNoticeBoardVO;
import egov.framework.plms.sub.ewp.bean.mvc.mapper.maria.board.EwpNoticeBoardMapper;
import egov.framework.plms.sub.ewp.bean.mvc.service.common.EwpCodeService;
import egov.framework.plms.sub.ewp.bean.mvc.service.organization.EwpUserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile("ewp")
public class EwpNoticeBoardService {
	private final EwpCodeService commServ;
	private final FileInfoService fileServ;
	private final EwpUserInfoService userServ;
	private final EwpNoticeBoardMapper ewpNoticeMapper;
	private final FileInfoMapper fileMapper;
	
	//공지 리스트 조회
	public List<EwpNoticeBoardVO> getNoticeList(Map<String, Object> params){
		return ewpNoticeMapper.getNoticeList(params);
	}
	
	//공지 상세 내용 조회
	public EwpNoticeBoardVO getNoticeOne(Integer noticeId) {
		EwpNoticeBoardVO rtVO = ewpNoticeMapper.getNoticeOne(noticeId);
		log.info("공지 상세내용 결과 ::{}",rtVO);
		return rtVO;
	}
	
	//공지 신규 등록
	public ResponseMessage postNotice(EwpNoticeBoardVO params) {
		Integer result = ewpNoticeMapper.postNotice(params);
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
		Integer result = ewpNoticeMapper.deleteNotice(noticeId);
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
	public ResponseMessage putNotice(EwpNoticeBoardVO params) {
		Integer result = ewpNoticeMapper.putNotice(params);
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

	public List<EwpNoticeBoardVO> getNoticeList(EwpNoticeBoardVO params) {
		// TODO Auto-generated method stub
		List<EwpNoticeBoardVO> noticeList = ewpNoticeMapper.getNoticeList(params);
		return noticeList.stream().map(notice -> notice.toBuilder().officeName(commServ.getOfficeName(notice.getOfficeCode())).writer(userServ.selectUserInfoOne(notice.getWriterId()).orElse(null)).build()).collect(Collectors.toList());
	}
	public Integer getNoticeListCnt(EwpNoticeBoardVO params) {
		// TODO Auto-generated method stub
		return ewpNoticeMapper.getNoticeListCnt(params);
	}
	
}
