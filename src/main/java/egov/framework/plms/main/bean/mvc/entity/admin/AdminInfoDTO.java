package egov.framework.plms.main.bean.mvc.entity.admin;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;

import egov.framework.plms.main.bean.mvc.entity.organization.abst.UserInfoModelDTO;
import egov.framework.plms.main.core.model.enums.user.DomainRole;
import egov.framework.plms.main.core.model.enums.user.ManagerRole;
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
public class AdminInfoDTO extends UserInfoModelDTO{
	private String officeCode;
	private String officeName;
	private String deptId; 
	private String deptName; 
	private String userId; 
	private String userName; 
	private String titleCode;
	private String titleName;
	private String positionCode;
	private String positionName;
	private String rankCode;
	private String rankName;
	private String personalCellPhone; 
	private String officeDeskPhone; 
	private String email; 
	private DomainRole domainRole;
	private ManagerRole positionRole;
	
	@Builder(builderClassName = "init", builderMethodName = "initDTO")
	public AdminInfoDTO(AdminInfoVO vo) {
		this.officeCode = vo.getOfficeCode();
		this.officeName = vo.getOfficeName();
		this.deptId = vo.getDeptId();
		this.deptName = vo.getDeptName();
		this.userId = vo.getUserId();
		this.userName = vo.getUserName();
		this.positionCode = vo.getPositionCode();
		this.positionName = vo.getPositionName();
		this.titleCode = vo.getTitleCode();
		this.titleName = vo.getTitleName();
		this.rankCode = vo.getRankCode();
		this.rankName = vo.getRankName();
		this.personalCellPhone = vo.getPersonalCellPhone();
		this.officeDeskPhone = vo.getOfficeDeskPhone();
		this.email = vo.getEmail();
		this.domainRole = vo.getDomainRole();
		this.positionRole = vo.getPositionRole();
	}

	@Override
	public AdminInfoVO convert() {
		return AdminInfoVO.initDTO().dto(this).build();
	}

	@Override
	public String getDeptCode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNameplate() {
		// TODO Auto-generated method stub
		return this.titleName + " " + this.userName;
	}
	
	@Override
	public String getDeptName() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.deptName).orElse("");
	}
	
	@Override
	public String getPositionName() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.positionName).orElse("");
	}
	
	@Override
	public String getPersonalCellPhone() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.personalCellPhone).orElse("");
	}
	
	@Override
	public String getOfficeDeskPhone() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.officeDeskPhone).orElse("");
	}
	
	@Override
	public String getEmail() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.email).orElse("");
	}

	@Override
	public void setDeptCode(String value) {
		// TODO Auto-generated method stub
		
	}
}
