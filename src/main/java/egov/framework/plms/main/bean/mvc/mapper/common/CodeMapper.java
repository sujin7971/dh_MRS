package egov.framework.plms.main.bean.mvc.mapper.common;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.main.bean.mvc.entity.common.CodeVO;

@Mapper
public interface CodeMapper {
	public Integer putComCode(CodeVO param);
	public CodeVO getComCodeOne(CodeVO param);
	public List<CodeVO> getComCodeList(CodeVO param);
}
