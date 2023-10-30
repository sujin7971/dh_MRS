package egov.framework.plms.main.bean.mvc.mapper.admin;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.main.bean.mvc.entity.admin.AdminRosterVO;

@Mapper
public interface DomainRosterMapper {

	List<AdminRosterVO> getDomainAdminList(AdminRosterVO param);
	Integer postDomainAdmin(AdminRosterVO param);
	Integer deleteDomainAdmin(AdminRosterVO param);
}
