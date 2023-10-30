package egov.framework.plms.main.bean.mvc.service.file;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.mvc.entity.file.FileConvertVO;
import egov.framework.plms.main.bean.mvc.mapper.file.FileCvtMapper;
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
@RequiredArgsConstructor
public class FileCvtService {
	private final FileCvtMapper cvtMapper;
	
	public boolean insertCvtOne(FileConvertVO vo) {
		try {
			Integer result = cvtMapper.insertCvtOne(vo);
			return (result == 0)?false:true;
		}catch(Exception e){
			log.error("Failed to insert one FileCvt with params: {}", vo);
			log.error("Failed to insert one FileCvt messages: {}", e.toString());
			return false;
		}
	}
	
	public boolean updateCvtOne(FileConvertVO vo) {
		try {
			Integer result = cvtMapper.updateCvtOne(vo);
			return (result == 0)?false:true;
		}catch(Exception e){
			log.error("Failed to update one FileCvt with params: {}", vo);
			log.error("Failed to update one FileCvt messages: {}", e.toString());
			return false;
		}
	}

	public boolean updateUnfinishedFileConversionsToInit() {
		try {
			Integer result = cvtMapper.updateUnfinishedFileConversionsToInit();
			return (result == 0)?false:true;
		}catch(Exception e){
			log.error("Failed to updateUnfinishedFileConversionsToInit one FileCvt");
			log.error("Failed to updateUnfinishedFileConversionsToInit one FileCvt messages: {}", e.toString());
			return false;
		}
	}
	
	public Optional<FileConvertVO> selectCvtOne(Integer cvtId) {
		FileConvertVO cvt = cvtMapper.selectCvtOne(cvtId);
		return Optional.ofNullable(cvt);
	}
	
	public List<FileConvertVO> selectFilesToConvert() {
		return cvtMapper.selectFilesToConvert();
	}

}
