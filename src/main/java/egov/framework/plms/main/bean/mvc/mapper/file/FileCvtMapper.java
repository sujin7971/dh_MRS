package egov.framework.plms.main.bean.mvc.mapper.file;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.main.bean.mvc.entity.file.FileConvertVO;

@Mapper
public interface FileCvtMapper {
	Integer insertCvtOne(FileConvertVO vo);
	Integer updateCvtOne(FileConvertVO vo);
	Integer updateUnfinishedFileConversionsToInit();
	
	List<FileConvertVO> selectFilesToConvert();
	FileConvertVO selectCvtOne(Integer cvtId);
}
