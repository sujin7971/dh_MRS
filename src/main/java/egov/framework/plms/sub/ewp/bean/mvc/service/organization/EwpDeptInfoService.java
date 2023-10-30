package egov.framework.plms.sub.ewp.bean.mvc.service.organization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.mvc.service.organization.abst.DeptInfoAbstractService;
import egov.framework.plms.sub.ewp.bean.mvc.entity.organization.EwpDeptInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.mapper.oracle.organization.EwpDeptInfoMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile("ewp")
@Primary
public class EwpDeptInfoService extends DeptInfoAbstractService<EwpDeptInfoVO>{
	public EwpDeptInfoService(@Autowired EwpDeptInfoMapper mapper) {
		super(mapper);
	}
}
