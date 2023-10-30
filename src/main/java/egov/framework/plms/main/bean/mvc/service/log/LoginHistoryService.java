package egov.framework.plms.main.bean.mvc.service.log;

import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.component.properties.abst.CustomServerProperties;
import egov.framework.plms.main.bean.mvc.entity.log.LoginHistoryVO;
import egov.framework.plms.main.bean.mvc.mapper.log.LoginHistoryMapper;
import egov.framework.plms.main.core.model.enums.common.LoginResult;
import egov.framework.plms.main.core.model.enums.common.ServerType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service("LoginHistoryService")
public class LoginHistoryService {
	private final LoginHistoryMapper mapper;
	private final CustomServerProperties serverProperties;
	
	public boolean insertLoginHistory(String userId, String ipAddress, LoginResult loginResult) {
		try {
			ServerType serverType = serverProperties.getType();
			Integer result = mapper.insertLoginHistory(LoginHistoryVO.builder().userId(userId).ipAddress(ipAddress).serverType(serverType).loginResult(loginResult).build());
			return (result == 0)?false:true;
		}catch(Exception e) {
			return false;
		}
	}

}
