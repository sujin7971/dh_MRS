package egov.framework.plms.sub.lime.bean.mvc.mapper.meeting;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.main.bean.mvc.entity.file.FileDetailVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingArchiveVO;

@Mapper
public interface LimeMeetingFileInfoMapper {

	List<FileDetailVO> getMeetingFileList(FileDetailVO vo);
	List<FileDetailVO> getMeetingSharedFileList(FileDetailVO vo);
	List<FileDetailVO> getMeetingPrivateFileList(FileDetailVO vo);
	List<FileDetailVO> selectMeetingMaterialFilesToGenerateImages();
	List<FileDetailVO> selectMeetingCopyFilesToIntegrateImages();
	List<FileDetailVO> selectMeetingCopyFilesToGeneratePdf();
	List<FileDetailVO> selectMeetingMemoFilesToIntegrateImages();
	List<FileDetailVO> selectMeetingMemoFilesToGeneratePdf();
	List<MeetingArchiveVO> selectMeetingArchiveList(MeetingArchiveVO param);
}
