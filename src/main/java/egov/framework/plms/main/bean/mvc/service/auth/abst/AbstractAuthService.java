package egov.framework.plms.main.bean.mvc.service.auth.abst;

import java.util.List;

import egov.framework.plms.main.bean.mvc.entity.auth.ResourceAuthorityVO;
import egov.framework.plms.main.bean.mvc.entity.auth.ResourceGroupVO;
import egov.framework.plms.main.bean.mvc.entity.auth.ResourcePermissionVO;

public abstract class AbstractAuthService {
	abstract public Integer postResourcePermission(ResourcePermissionVO param);
	abstract public Integer postResourceGroup(ResourceGroupVO param);
	
	abstract public Integer putResourcePermissionLvl(ResourcePermissionVO param);
	abstract public Integer deleteResourceGroup(String grpId);
	
	abstract public ResourcePermissionVO getResourcePermissionOne(Integer permId);
	abstract public ResourceGroupVO getResourceGroupOne(String grpId);
	abstract public List<ResourceGroupVO> getResourceGroupList(Integer srcId);
	abstract public List<ResourceGroupVO> getResourceGroupList(ResourceGroupVO param);
	abstract public List<ResourceAuthorityVO> getResourceAuthorityList();
}
