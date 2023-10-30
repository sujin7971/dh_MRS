package egov.framework.plms.main.bean.mvc.service.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.mvc.mapper.admin.DomainRosterMapper;
import egov.framework.plms.main.bean.mvc.mapper.admin.ManagerRosterMapper;
import egov.framework.plms.main.bean.mvc.service.admin.abst.AdminRosterAbstractService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AdminRosterService extends AdminRosterAbstractService {
	
	public AdminRosterService(@Autowired DomainRosterMapper domainMapper, @Autowired ManagerRosterMapper managerMapper) {
		// TODO Auto-generated constructor stub
		super(domainMapper, managerMapper);
	}
}
