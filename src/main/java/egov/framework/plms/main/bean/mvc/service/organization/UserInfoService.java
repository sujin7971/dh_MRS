package egov.framework.plms.main.bean.mvc.service.organization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.mvc.entity.organization.UserInfoVO;
import egov.framework.plms.main.bean.mvc.mapper.organization.UserInfoMapper;
import egov.framework.plms.main.bean.mvc.service.organization.abst.UserInfoAbstractService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("UserInfoService")
public class UserInfoService extends UserInfoAbstractService<UserInfoVO>{
	public UserInfoService(@Autowired UserInfoMapper mapper) {
		super(mapper);
	}
}
