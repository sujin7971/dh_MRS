package egov.framework.plms.main.bean.mvc.service.organization.abst;

import java.util.List;
import java.util.Optional;

import egov.framework.plms.main.bean.mvc.entity.organization.abst.DeptInfoModelVO;
import egov.framework.plms.main.bean.mvc.mapper.organization.abst.DeptInfoAbstractMapper;

public class DeptInfoAbstractService<T extends DeptInfoModelVO> {
	protected final DeptInfoAbstractMapper<T> mapper;
	
	public DeptInfoAbstractService(DeptInfoAbstractMapper<T> mapper) {
		this.mapper = mapper;
	}
	
	public Optional<T> selectDeptInfoOne(String deptId){
		T deptVO = mapper.selectDeptInfoOne(deptId);
		return Optional.ofNullable(deptVO);
	}
	
	public List<T> selectSubDeptInfoList(String deptId){
		return mapper.selectSubDeptInfoList(deptId);
	}
	
	public List<T> selectRecursiveSubDeptInfoList(String deptId){
		return mapper.selectRecursiveSubDeptInfoList(deptId);
	}
	
	public List<T> selectDeptInfoList(T params){
		return mapper.selectDeptInfoList(params);
	}
	
	public List<T> selectAllDeptInfoList() {
		return mapper.selectAllDeptInfoList();
	}
}
