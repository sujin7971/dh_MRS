package egov.framework.plms.sub.ewp.bean.mvc.mapper.maria.file;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.sub.ewp.bean.mvc.entity.file.MeetingFileConvertVO;

@Mapper
public interface EwpMeetingFileCvtMapper {
	Integer putCvt(MeetingFileConvertVO vo);
	Integer putCvtStep(MeetingFileConvertVO c);
	Integer putUnfinishedCvtInit();
	
	List<MeetingFileConvertVO> getCvtSearchResult(MeetingFileConvertVO param);
	List<MeetingFileConvertVO> getReqCvtList();
	List<MeetingFileConvertVO> getCvtListToProcess(MeetingFileConvertVO param);
}
