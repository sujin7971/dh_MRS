package egov.framework.plms.main.bean.mvc.mapper.organization;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.main.bean.mvc.entity.organization.DeptInfoVO;
import egov.framework.plms.main.bean.mvc.mapper.organization.abst.DeptInfoAbstractMapper;

@Mapper
public interface DeptInfoMapper extends DeptInfoAbstractMapper<DeptInfoVO>{
	Integer insertDeptInfoOne(DeptInfoVO params);
	Integer updateDeptInfoOne(DeptInfoVO params);
}
