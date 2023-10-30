package egov.framework.plms.sub.ewp.bean.mvc.entity.organization;

import egov.framework.plms.main.bean.mvc.entity.organization.abst.DeptInfoModelVO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class EwpDeptInfoVO extends DeptInfoModelVO{
	private String officeCode; 
	private String officeName;
	private String deptCode;
	private String deptId; 
	private String deptName; 
	private String parentId; 
	
	@Builder(builderClassName = "init", builderMethodName = "initVO")
	public EwpDeptInfoVO(EwpDeptInfoDTO dto) {
		this.officeCode = dto.getOfficeCode();
		this.officeName = dto.getOfficeName();
		this.deptId = dto.getDeptId();
		this.deptName = dto.getDeptName();
		this.parentId = dto.getParentId();
	}
	
	public EwpDeptInfoDTO convert() {
		return EwpDeptInfoDTO.initDTO().vo(this).build();
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

	@Override
	public String getDeptId() {
		return this.deptId;
	}
	
	public String getDeptName() {
		return this.deptName;
	}
}
