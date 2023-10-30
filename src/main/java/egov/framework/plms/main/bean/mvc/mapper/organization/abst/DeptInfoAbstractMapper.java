package egov.framework.plms.main.bean.mvc.mapper.organization.abst;

import java.util.List;

import egov.framework.plms.main.bean.mvc.entity.organization.abst.DeptInfoModelVO;

public interface DeptInfoAbstractMapper<T extends DeptInfoModelVO> {
	T selectDeptInfoOne(String deptId);
	List<T> selectSubDeptInfoList(String deptId);
	List<T> selectRecursiveSubDeptInfoList(String deptId);
	List<T> selectDeptInfoList(T params);
	List<T> selectAllDeptInfoList();
}
