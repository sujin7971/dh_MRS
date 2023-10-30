package egov.framework.plms.sub.ewp.bean.mvc.entity.organization;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;

import egov.framework.plms.main.bean.mvc.entity.organization.abst.UserInfoModelDTO;
import egov.framework.plms.main.core.model.able.Filterable;
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
public class EwpUserInfoDTO extends UserInfoModelDTO {
	private String officeCode;
	private String officeName;
	private String deptId; 
	private String deptName; 
	private String deptHierarchyName;
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
	
	@Builder(builderClassName = "init", builderMethodName = "initDTO")
	public EwpUserInfoDTO(EwpUserInfoVO vo) {
		this.officeCode = vo.getOfficeCode();
		this.officeName = vo.getOfficeName();
		this.deptId = vo.getDeptId();
		this.deptName = vo.getDeptName();
		this.deptHierarchyName = vo.getDeptHierarchyName();
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
	}

	@Override
	public EwpUserInfoVO convert() {
		return EwpUserInfoVO.initDTO().dto(this).build();
	}

	@Override
	public void setDeptCode(String deptCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getDeptCode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNameplate() {
		// TODO Auto-generated method stub
		return getDeptHierarchyName() + " " + this.userName;
	}
	
	@Override
	public String getDeptName() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.deptName).orElse("");
	}
	
	public String getDeptHierarchyName() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.deptHierarchyName).orElse("");
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
	
	public String getUserKey() {
		return this.userId;
	}
	
	public void setUserKey(String userKey) {
		this.userId = userKey;
	}

	@Override
	public EwpUserInfoDTO filterForEssential() {
		EwpUserInfoDTO filteredDTO = (EwpUserInfoDTO) super.filterForEssential();
		return filteredDTO;
	}
	
	@Override
	public EwpUserInfoDTO filterForBasic() {
		EwpUserInfoDTO filteredDTO = (EwpUserInfoDTO) super.filterForBasic();
		return filteredDTO;
	}

	@Override
	public EwpUserInfoDTO filterForDetailed() {
		EwpUserInfoDTO filteredDTO = (EwpUserInfoDTO) super.filterForDetailed();
		return filteredDTO;
	}

	@Override
	public EwpUserInfoDTO filterForSensitive() {
		EwpUserInfoDTO filteredDTO = (EwpUserInfoDTO) super.filterForSensitive();
		return filteredDTO;
	}

	@Override
	public EwpUserInfoDTO filterForHighest() {
		EwpUserInfoDTO filteredDTO = (EwpUserInfoDTO) super.filterForHighest();
		return filteredDTO;
	}
}
