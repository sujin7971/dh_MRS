package egov.framework.plms.sub.lime.bean.mvc.service.organization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.mvc.entity.organization.DeptInfoVO;
import egov.framework.plms.main.bean.mvc.service.organization.abst.DeptInfoAbstractService;
import egov.framework.plms.sub.lime.bean.mvc.mapper.organization.LimeDeptInfoMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile("lime")
@Primary
public class LimeDeptInfoService extends DeptInfoAbstractService<DeptInfoVO>{
	private final LimeDeptInfoMapper mapper;
	
	public LimeDeptInfoService(@Autowired LimeDeptInfoMapper mapper) {
		super(mapper);
		this.mapper = mapper;
	}
	
	public boolean insertDeptInfoOne(DeptInfoVO params) {
		try {
			Integer result = mapper.insertDeptInfoOne(params);
			return (result == 0)?false:true;
		}catch(Exception e) {
			return false;
		}
	}
	
	public boolean updateDeptInfoOne(DeptInfoVO params) {
		try {
			Integer result = mapper.updateDeptInfoOne(params);
			return (result == 0)?false:true;
		}catch(Exception e) {
			return false;
		}
	}
	
	public boolean updateDeptInfoOneToDelete(String deptId) {
		try {
			Integer result = mapper.updateDeptInfoOneToDelete(deptId);
			return (result == 0)?false:true;
		}catch(Exception e) {
			log.error("Failed to update DeptInfo one to Delete with id: {}", deptId);
			log.error("Failed to update DeptInfo one to Delete messages: {}", e.toString());
			return false;
		}
	}
}
