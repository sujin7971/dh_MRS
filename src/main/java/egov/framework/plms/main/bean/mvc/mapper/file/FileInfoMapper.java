package egov.framework.plms.main.bean.mvc.mapper.file;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.main.bean.mvc.entity.file.FileDetailVO;
import egov.framework.plms.main.bean.mvc.mapper.file.abst.FileInfoAbstractMapper;

@Mapper
public interface FileInfoMapper extends FileInfoAbstractMapper<FileDetailVO>{
}
