package egov.framework.plms.main.bean.mvc.entity.organization.abst;

import egov.framework.plms.main.core.model.able.Convertable;
import egov.framework.plms.main.core.model.able.Filterable;

public abstract class UserInfoModelDTO extends UserInfoEntity implements Convertable<UserInfoModelVO>, Filterable<UserInfoModelDTO>{
	/** 사용자 소속 회사 또는 사업소의 고유키 */
	public abstract void setOfficeCode(String value);
	/** 사용자 소속 회사 또는 사업소의 이름 */
	public abstract void setOfficeName(String value);
	/** 사용자 소속 부서의 코드 */
	public abstract void setDeptCode(String value);
	/** 사용자 소속 부서의 고유키 */
	public abstract void setDeptId(String value);
	/** 사용자 소속 부서명 */
	public abstract void setDeptName(String value);
	/** 사용자의 ID */
	public abstract void setUserId(String value);
	/** 사용자명 */
	public abstract void setUserName(String value);
	/** 사용자의 직위 명칭 */
	public abstract void setTitleName(String value);
	/** 사용자의 직위 코드 */
	public abstract void setTitleCode(String value);
	/** 사용자의 직책 명칭 */
	public abstract void setPositionName(String value);
	/** 사용자의 직책 코드 */
	public abstract void setPositionCode(String value);
	/** 사용자의 직급 명칭 */
	public abstract void setRankName(String value);
	/** 사용자의 직급 코드 */
	public abstract void setRankCode(String value);
	/** 사용자 메일주소 */
	public abstract void setEmail(String value);
	/** 사용자 개인 전화번호 */
	public abstract void setPersonalCellPhone(String value);
	/** 사용자 내선 전화번호 */
	public abstract void setOfficeDeskPhone(String value);
	
	/**
	 * 다음 값들을 필터링합니다<br>
	 * {@link #officeCode}, {@link #deptId}, {@link #userId}
	 * <br>{@link #personalCellPhone}, {@link #email}
	 */
	@Override
	public UserInfoModelDTO filterForEssential() {
		UserInfoModelDTO filteredDTO = filterForBasic();
		filteredDTO.setOfficeCode(null);
		filteredDTO.setDeptId(null);
		filteredDTO.setUserId(null);
		return filteredDTO;
	}
	
	@Override
	public UserInfoModelDTO filterForBasic() {
		UserInfoModelDTO filteredDTO = filterForDetailed();
		return filteredDTO;
	}

	/**
	 * 다음 값들을 필터링합니다<br>
	 * {@link #personalCellPhone}, {@link #email}
	 */
	@Override
	public UserInfoModelDTO filterForDetailed() {
		UserInfoModelDTO filteredDTO = filterForSensitive();
		filteredDTO.setPersonalCellPhone(null);
		filteredDTO.setEmail(null);
		return filteredDTO;
	}

	@Override
	public UserInfoModelDTO filterForSensitive() {
		UserInfoModelDTO filteredDTO = filterForHighest();
		return filteredDTO;
	}

	@Override
	public UserInfoModelDTO filterForHighest() {
		UserInfoModelDTO filteredDTO = this;
		return filteredDTO;
	}
}
