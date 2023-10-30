package egov.framework.plms.main.bean.mvc.entity.organization;

import com.fasterxml.jackson.annotation.JsonInclude;

import egov.framework.plms.main.bean.mvc.entity.organization.abst.UserInfoModelDTO;
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
public class UserInfoDTO extends UserInfoModelDTO{
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
	public UserInfoDTO(UserInfoVO vo) {
		this.userId = vo.getUserId();
		this.userName = vo.getUserName();
		this.officeCode = vo.getOfficeCode();
		this.officeName = vo.getOfficeName();
		this.deptCode = vo.getDeptCode();
		this.deptId = vo.getDeptId();
		this.deptName = vo.getDeptName();
		this.positionCode = vo.getPositionCode();
		this.positionName = vo.getPositionName();
		this.titleCode = vo.getTitleCode();
		this.titleName = vo.getTitleName();
		this.rankCode = vo.getRankCode();
		this.rankName = vo.getRankName();
		this.personalCellPhone = vo.getPersonalCellPhone();
		this.officeDeskPhone = vo.getOfficeDeskPhone();
		this.email = vo.getEmail();
		this.delYN = vo.getDelYN();
	}

	@Override
	public UserInfoVO convert() {
		return UserInfoVO.initDTO().dto(this).build();
	}

	@Override
	public String getNameplate() {
		// TODO Auto-generated method stub
		return this.titleName + " " + this.userName;
	}
	
	/**
	 * 다음 값들을 필터링합니다<br>
	 * {@link #officeCode}, {@link #deptId}, {@link #userId}
	 * <br>{@link #personalCellPhone}, {@link #email}
	 */
	@Override
	public UserInfoDTO filterForEssential() {
		UserInfoDTO filteredDTO = (UserInfoDTO) super.filterForEssential();
		return filteredDTO;
	}
	
	@Override
	public UserInfoDTO filterForBasic() {
		UserInfoDTO filteredDTO = (UserInfoDTO) super.filterForBasic();
		return filteredDTO;
	}

	/**
	 * 다음 값들을 필터링합니다<br>
	 * {@link #personalCellPhone}, {@link #email}
	 */
	@Override
	public UserInfoDTO filterForDetailed() {
		UserInfoDTO filteredDTO = (UserInfoDTO) super.filterForDetailed();
		return filteredDTO;
	}

	@Override
	public UserInfoDTO filterForSensitive() {
		UserInfoDTO filteredDTO = (UserInfoDTO) super.filterForSensitive();
		return filteredDTO;
	}

	@Override
	public UserInfoDTO filterForHighest() {
		UserInfoDTO filteredDTO = (UserInfoDTO) super.filterForHighest();
		return filteredDTO;
	}
}
