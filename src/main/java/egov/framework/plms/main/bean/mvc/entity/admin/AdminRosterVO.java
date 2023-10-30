package egov.framework.plms.main.bean.mvc.entity.admin;

import egov.framework.plms.main.core.model.enums.user.DomainRole;
import egov.framework.plms.main.core.model.enums.user.ManagerRole;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class AdminRosterVO {
	private String userId;
	private String officeCode;
	private String deptId;
	private DomainRole domainRole;
	private ManagerRole managerRole;
}
