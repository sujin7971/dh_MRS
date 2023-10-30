package egov.framework.plms.main.bean.mvc.entity.organization.abst;

/**
 * 사용자 객체
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 */
public abstract class UserInfoEntity {
	/** 사용자 소속 회사 또는 사업소의 고유키 */
	public abstract String getOfficeCode();
	/** 사용자 소속 회사 또는 사업소의 이름 */
	public abstract String getOfficeName();
	/** 사용자 소속 부서의 코드 */
	public abstract String getDeptCode();
	/** 사용자 소속 부서의 고유키 */
	public abstract String getDeptId();
	/** 사용자 소속 부서명 */
	public abstract String getDeptName();
	/** 사용자의 ID */
	public abstract String getUserId();
	/** 사용자명 */
	public abstract String getUserName();
	/** 사용자의 직위 명칭 */
	public abstract String getTitleName();
	/** 사용자의 직위 코드 */
	public abstract String getTitleCode();
	/** 사용자의 직책 명칭 */
	public abstract String getPositionName();
	/** 사용자의 직책 코드 */
	public abstract String getPositionCode();
	/** 사용자의 직급 명칭 */
	public abstract String getRankName();
	/** 사용자의 직급 코드 */
	public abstract String getRankCode();
	/** 사용자 메일주소 */
	public abstract String getEmail();
	/** 사용자 개인 전화번호 */
	public abstract String getPersonalCellPhone();
	/** 사용자 내선 전화번호 */
	public abstract String getOfficeDeskPhone();
	/** 사용자 표기방식 */
	public abstract String getNameplate();
}
