package egov.framework.plms.main.bean.mvc.entity.organization;

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
public class DeptInfoVO extends DeptInfoModelVO{
	private String officeCode; 
	private String officeName;
	private String deptCode;
	private String deptId; 
	private String deptName; 
	private String parentId; 
	private String parentCode;
	
	@Builder(builderClassName = "init", builderMethodName = "initVO")
	public DeptInfoVO(DeptInfoDTO dto) {
		this.officeCode = dto.getOfficeCode();
		this.officeName = dto.getOfficeName();
		this.parentCode = dto.getParentCode();
		this.parentId = dto.getParentId();
		this.deptCode = dto.getDeptCode();
		this.deptId = dto.getDeptId();
		this.deptName = dto.getDeptName();
	}
	
	public DeptInfoDTO convert() {
		return DeptInfoDTO.initDTO().vo(this).build();
	}

	@Override
	public String getParentCode() {
		// TODO Auto-generated method stub
		return this.parentCode;
	}
	
}
