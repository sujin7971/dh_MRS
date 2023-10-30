package egov.framework.plms.main.bean.mvc.entity.organization;

import com.fasterxml.jackson.annotation.JsonInclude;

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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeptInfoDTO extends DeptInfoModelDTO{
	private String officeCode;
	private String officeName;
	private String deptCode;
	private String deptId;
	private String deptName;
	private String parentCode;
	private String parentId;

	@Builder(builderClassName = "init", builderMethodName = "initDTO")
	public DeptInfoDTO(DeptInfoVO vo) {
		this.officeCode = vo.getOfficeCode();
		this.officeName = vo.getOfficeName();
		this.parentCode = vo.getParentCode();
		this.parentId = vo.getParentId();
		this.deptCode = vo.getDeptCode();
		this.deptId = vo.getDeptId();
		this.deptName = vo.getDeptName();
	}
	
	public DeptInfoVO convert() {
		return DeptInfoVO.initVO().dto(this).build();
	}
	@Override
	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}
	@Override
	public String getParentCode() {
		// TODO Auto-generated method stub
		return this.parentCode;
	}
}
