package egov.framework.plms.sub.ewp.bean.mvc.mapper.maria.file;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.cache.annotation.CacheEvict;

import egov.framework.plms.sub.ewp.bean.mvc.entity.file.MeetingFileInfoVO;

@Mapper
public interface EwpMeetingFileInfoMapper {

	Integer postMeetingFile(MeetingFileInfoVO vo);
	Integer putMeetingFile(MeetingFileInfoVO vo);
	Integer deleteMeetingFile(Integer fileKey);
	Integer updateFileOneToDelete(Integer fileKey);
	Integer updateFileStatusToRemove(Integer fileKey);
	//Integer deleteFileList(List<FileInfoVO> list);
	
	//@Cacheable(value = "file", key = "#fileKey")
	MeetingFileInfoVO getMeetingFileOne(Integer fileKey);
	MeetingFileInfoVO getMemoFileOne(MeetingFileInfoVO vo);
	List<MeetingFileInfoVO> getFileListToDeleteByExpiration();
	List<MeetingFileInfoVO> getDeletedFileListOnDisk();
	List<MeetingFileInfoVO> getMeetingFileList(MeetingFileInfoVO vo);
	List<MeetingFileInfoVO> getMeetingSharedFileList(MeetingFileInfoVO vo);
	List<MeetingFileInfoVO> getMeetingPrivateFileList(MeetingFileInfoVO vo);
}
