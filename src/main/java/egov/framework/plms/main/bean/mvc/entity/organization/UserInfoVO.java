package egov.framework.plms.main.bean.mvc.entity.organization;

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
public class UserInfoVO extends UserInfoModelVO {
	private String userId; 
	private String userName; 
	private String officeCode;
	private String officeName;
	private String deptCode;
	private String deptId; 
	private String deptName; 
	private String titleCode;
	private String titleName;
	private String positionCode;
	private String positionName;
	private String rankCode;
	private String rankName;
	private String personalCellPhone; 
	private String officeDeskPhone; 
	private String email; 
	private Character delYN;
	
	@Builder(builderClassName = "init", builderMethodName = "initDTO")
	public UserInfoVO(UserInfoDTO dto) {
		this.userId = dto.getUserId();
		this.userName = dto.getUserName();
		this.officeCode = dto.getOfficeCode();
		this.officeName = dto.getOfficeName();
		this.deptCode = dto.getDeptCode();
		this.deptId = dto.getDeptId();
		this.deptName = dto.getDeptName();
		this.positionCode = dto.getPositionCode();
		this.positionName = dto.getPositionName();
		this.titleCode = dto.getTitleCode();
		this.titleName = dto.getTitleName();
		this.rankCode = dto.getRankCode();
		this.rankName = dto.getRankName();
		this.personalCellPhone = dto.getPersonalCellPhone();
		this.officeDeskPhone = dto.getOfficeDeskPhone();
		this.email = dto.getEmail();
		this.delYN = dto.getDelYN();
	}

	@Override
	public UserInfoDTO convert() {
		return UserInfoDTO.initDTO().vo(this).build();
	}
	
	@Override
	public String getNameplate() {
		// TODO Auto-generated method stub
		return this.titleName + " " + this.userName;
	}
}
