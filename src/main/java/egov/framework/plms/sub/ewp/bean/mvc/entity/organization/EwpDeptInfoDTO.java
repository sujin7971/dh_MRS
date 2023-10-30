package egov.framework.plms.sub.ewp.bean.mvc.entity.organization;

import egov.framework.plms.main.bean.mvc.entity.organization.abst.DeptInfoModelDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class EwpDeptInfoDTO extends DeptInfoModelDTO{
	private String officeCode;
	private String officeName;
	private String deptCode;
	private String deptId;
	private String deptName;
	private String parentId;

	@Builder(builderClassName = "init", builderMethodName = "initDTO")
	public EwpDeptInfoDTO(EwpDeptInfoVO vo) {
		this.officeCode = vo.getOfficeCode();
		this.officeName = vo.getOfficeName();
		this.deptId = vo.getDeptId();
		this.deptName = vo.getDeptName();
		this.parentId = vo.getParentId();
	}
	
	public EwpDeptInfoVO convert() {
		return EwpDeptInfoVO.initVO().dto(this).build();
	}
	
	public String getOfficeCode() {
		return this.officeCode;
	}
	
	public String getOfficeName() {
		return this.officeName;
	}
	
	public String getParentCode() {
		return this.parentId;
	}
	
	public String getDeptCode() {
		return this.deptCode;
	}
	
	public String getDeptName() {
		return this.deptName;
	}

	@Override
	public void setOfficeCode(String officeCode) {
		this.officeCode = officeCode;
	}

	@Override
	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	@Override
	public void setParentCode(String parentCode) {
		this.parentId = parentCode;
	}

	@Override
	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}

	@Override
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	@Override
	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	@Override
	public String getDeptId() {
		return this.deptId;
	}
}
