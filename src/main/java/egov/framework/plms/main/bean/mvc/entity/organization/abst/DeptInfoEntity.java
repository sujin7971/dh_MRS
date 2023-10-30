package egov.framework.plms.main.bean.mvc.entity.organization.abst;

/**
 * 부서 객체
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 */
public abstract class DeptInfoEntity {
	/** 소속 회사/사업소 고유키 */
	public abstract String getOfficeCode();
	/** 소속 회사/사업소 이름 */
	public abstract String getOfficeName();
	/** 상위 부서 고유키 */
	public abstract String getParentCode();
	/** 부서 고유코드 */
	public abstract String getDeptCode();
	/** 부서 고유키 */
	public abstract String getDeptId();
	/** 부서 이름 */
	public abstract String getDeptName();
}
