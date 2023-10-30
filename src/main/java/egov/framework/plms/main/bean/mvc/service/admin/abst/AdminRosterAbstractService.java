package egov.framework.plms.main.bean.mvc.service.admin.abst;

import java.util.List;

import egov.framework.plms.main.bean.mvc.entity.admin.AdminRosterVO;
import egov.framework.plms.main.bean.mvc.mapper.admin.DomainRosterMapper;
import egov.framework.plms.main.bean.mvc.mapper.admin.ManagerRosterMapper;
import egov.framework.plms.main.bean.mvc.service.meeting.abst.MeetingInfoAbstractService;
import egov.framework.plms.main.core.model.enums.user.DomainRole;
import egov.framework.plms.main.core.model.enums.user.ManagerRole;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AdminRosterAbstractService {
	protected final DomainRosterMapper domainMapper;
	protected final ManagerRosterMapper managerMapper;
	
	public AdminRosterAbstractService(DomainRosterMapper domainMapper, ManagerRosterMapper managerMapper) {
		this.domainMapper = domainMapper;
		this.managerMapper = managerMapper;
	}
	
	public List<AdminRosterVO> getAllDomainAdminList(){
		return getDomainAdminList(AdminRosterVO.builder().build());
	}
	
	public List<AdminRosterVO> getDomainAdminList(String userId){
		return getDomainAdminList(AdminRosterVO.builder().userId(userId).build());
	}
	
	public List<AdminRosterVO> getDomainAdminList(DomainRole domainRole){
		return getDomainAdminList(AdminRosterVO.builder().domainRole(domainRole).build());
	}
	
	public List<AdminRosterVO> getDomainAdminList(AdminRosterVO param){
		List<AdminRosterVO> adminList = domainMapper.getDomainAdminList(param);
		return adminList;
	}
	
	public List<AdminRosterVO> getManagerAdminList(String userId){
		return getManagerAdminList(userId, null, null);
	}
	
	public List<AdminRosterVO> getManagerAdminList(ManagerRole managerRole){
		return getManagerAdminList(null, null, managerRole);
	}
	
	public List<AdminRosterVO> getManagerAdminList(String officeCode, ManagerRole managerRole){
		return getManagerAdminList(null, officeCode, managerRole);
	}
	
	public List<AdminRosterVO> getManagerAdminList(String userId, String officeCode, ManagerRole managerRole){
		return getManagerAdminList(AdminRosterVO.builder().userId(userId).officeCode(officeCode).managerRole(managerRole).build());
	}
	
	public List<AdminRosterVO> getManagerAdminList(AdminRosterVO param){
		List<AdminRosterVO> adminList = managerMapper.getManagerAdminList(param);
		return adminList;
	}
	
	public boolean postDomainAdmin(String userId, DomainRole domainRole) {
		try {
			Integer result = domainMapper.postDomainAdmin(AdminRosterVO.builder().userId(userId).domainRole(domainRole).build());;
			return (result == 0)?false:true;
		}catch(Exception e){
			log.error("Failed to post DomainAdmin with userId: {}, domainRole: {}", userId, domainRole);
			log.error("Failed to post DomainAdmin messages: {}", e.toString());
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean deleteDomainAdmin(String userId, DomainRole domainRole) {
		try {
			Integer result = domainMapper.deleteDomainAdmin(AdminRosterVO.builder().userId(userId).domainRole(domainRole).build());;
			return (result == 0)?false:true;
		}catch(Exception e){
			log.error("Failed to delete DomainAdmin with userId: {}, domainRole: {}", userId, domainRole);
			log.error("Failed to delete DomainAdmin messages: {}", e.toString());
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean postManagerAdmin(String userId, ManagerRole managerRole) {
		return postManagerAdmin(userId, null, managerRole);
	}
	
	public boolean postManagerAdmin(String userId, String officeCode, ManagerRole managerRole) {
		try {
			Integer result = managerMapper.postManagerAdmin(AdminRosterVO.builder().userId(userId).officeCode(officeCode).managerRole(managerRole).build());;
			return (result == 0)?false:true;
		}catch(Exception e){
			log.error("Failed to post ManagerAdmin with userId: {}, managerRole: {}", userId, managerRole);
			log.error("Failed to post ManagerAdmin messages: {}", e.toString());
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean deleteManagerAdmin(String userId, ManagerRole managerRole) {
		return deleteManagerAdmin(userId, null, managerRole);
	}
	
	public boolean deleteManagerAdmin(String userId, String officeCode, ManagerRole managerRole) {
		try {
			Integer result = managerMapper.deleteManagerAdmin(AdminRosterVO.builder().userId(userId).officeCode(officeCode).managerRole(managerRole).build());;
			return (result == 0)?false:true;
		}catch(Exception e){
			log.error("Failed to delete ManagerAdmin with userId: {}, managerRole: {}", userId, managerRole);
			log.error("Failed to delete ManagerAdmin messages: {}", e.toString());
			e.printStackTrace();
			return false;
		}
	}
}
