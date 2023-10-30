package egov.framework.plms.sub.lime.bean.mvc.mapper.organization;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.main.bean.mvc.entity.organization.DeptInfoVO;
import egov.framework.plms.main.bean.mvc.mapper.organization.abst.DeptInfoAbstractMapper;

@Mapper
public interface LimeDeptInfoMapper extends DeptInfoAbstractMapper<DeptInfoVO>{
	Integer insertDeptInfoOne(DeptInfoVO param);
	Integer updateDeptInfoOne(DeptInfoVO param);
	Integer updateDeptInfoOneToDelete(String deptId);
}
