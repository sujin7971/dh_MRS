package egov.framework.plms.sub.ewp.bean.mvc.service.file;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import egov.framework.plms.sub.ewp.bean.mvc.entity.file.MeetingFileConvertVO;
import egov.framework.plms.sub.ewp.bean.mvc.mapper.maria.file.EwpMeetingFileCvtMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 1.원본/판서본/메모 파일 등록시 트리거를 통해 변환 요청을 등록하는 방식으로 변경되었습니다.<br>
 * 트리거: TR_FILE_INFO_AF_INSERT<br><br>
 * 
 * 2.파일 삭제시 변환 요청이 등록 되어 있는 경우 트리거를 통해 해당 요청을 삭제하는 방식으로 변경되었습니다.<br>
 * 트리거: TR_MEETING_SCHEDULE_AF_DELETE, TR_MEETING_SCHEDULE_DELYN_AF_UPDATE<br><br>
 * 
 * @author mckim
 *
 */
@Slf4j
@Service
@Profile("ewp")
@RequiredArgsConstructor
public class EwpMeetingCvtService {
	private final EwpMeetingFileCvtMapper cvtMapper;
	
	public Integer putCvt(MeetingFileConvertVO vo) {
		return cvtMapper.putCvt(vo);
	}

	public Integer putUnfinishedCvtInit() {
		return cvtMapper.putUnfinishedCvtInit();
	}
	
	public List<MeetingFileConvertVO> getReqCvtList() {
		return cvtMapper.getReqCvtList();
	}
	
	public List<MeetingFileConvertVO> getCvtListToProcess(MeetingFileConvertVO param) {
		return cvtMapper.getCvtListToProcess(param);
	}

}
