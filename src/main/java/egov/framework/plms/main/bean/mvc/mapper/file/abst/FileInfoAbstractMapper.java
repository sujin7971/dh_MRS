package egov.framework.plms.main.bean.mvc.mapper.file.abst;

import java.util.List;

import egov.framework.plms.main.bean.mvc.entity.file.FileDetailVO;
import egov.framework.plms.main.bean.mvc.entity.file.abst.FileDetailModelVO;

public interface FileInfoAbstractMapper<T extends FileDetailModelVO> {
	Integer insertFileOne(T vo);
	
	Integer updateFileOne(T vo);
	Integer updateFileOneToDelete(Integer fileId);
	Integer updateFileStatusToRemove(Integer fileId);
	Integer deleteFileOne(Integer fileId);
	
	T selectFileOne(Integer fileId);
	List<T> selectFileList(T vo);
	List<T> selectDeletedFileListOnDisk();
	List<T> selectFilesToGeneratePdf();
	List<T> selectFilesToGenerateWebp();
}
