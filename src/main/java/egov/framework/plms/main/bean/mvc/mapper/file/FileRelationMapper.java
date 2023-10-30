package egov.framework.plms.main.bean.mvc.mapper.file;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.main.bean.mvc.entity.file.FileRelationVO;

@Mapper
public interface FileRelationMapper {

	Integer insertFileRelationOne(FileRelationVO vo);
	Integer deleteFileRelationOne(Integer relationId);
	Integer deleteFileRelationList(FileRelationVO vo);
}
