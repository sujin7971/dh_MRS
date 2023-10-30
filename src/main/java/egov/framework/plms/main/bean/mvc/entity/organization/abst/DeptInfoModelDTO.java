package egov.framework.plms.main.bean.mvc.entity.organization.abst;

import egov.framework.plms.main.core.model.able.Convertable;

public abstract class DeptInfoModelDTO extends DeptInfoEntity implements Convertable<DeptInfoModelVO> {
	public abstract void setOfficeCode(String officeCode);
	public abstract void setOfficeName(String officeName);
	public abstract void setParentCode(String parentCode);
	public abstract void setDeptCode(String deptCode);
	public abstract void setDeptId(String deptId);
	public abstract void setDeptName(String deptName);
}
