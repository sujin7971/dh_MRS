package egov.framework.plms.sub.ewp.bean.mvc.entity.organization;

import java.util.Optional;

import egov.framework.plms.main.bean.mvc.entity.organization.abst.UserInfoModelVO;
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
public class EwpUserInfoVO extends UserInfoModelVO {
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
	public EwpUserInfoVO(EwpUserInfoDTO dto) {
		this.officeCode = dto.getOfficeCode();
		this.officeName = dto.getOfficeName();
		this.deptId = dto.getDeptId();
		this.deptName = dto.getDeptName();
		this.deptHierarchyName = dto.getDeptHierarchyName();
		this.userId = dto.getUserId();
		this.userName = dto.getUserName();
		this.positionCode = dto.getPositionCode();
		this.positionName = dto.getPositionName();
		this.titleCode = dto.getTitleCode();
		this.titleName = dto.getTitleName();
		this.rankCode = dto.getRankCode();
		this.rankName = dto.getRankName();
		this.personalCellPhone = dto.getPersonalCellPhone();
		this.officeDeskPhone = dto.getOfficeDeskPhone();
		this.email = dto.getEmail();
	}

	@Override
	public EwpUserInfoDTO convert() {
		return EwpUserInfoDTO.initDTO().vo(this).build();
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
	
	public String getDeptHierarchyName() {
		// TODO Auto-generated method stub
		return Optional.ofNullable(this.deptHierarchyName).orElse("");
	}
	
	public String getUserKey() {
		return this.userId;
	}
}
