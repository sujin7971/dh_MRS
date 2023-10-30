package egov.framework.plms.main.bean.mvc.mapper.admin;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.main.bean.mvc.entity.admin.AdminRosterVO;

@Mapper
public interface ManagerRosterMapper {

	List<AdminRosterVO> getManagerAdminList(AdminRosterVO param);
	Integer postManagerAdmin(AdminRosterVO param);
	Integer deleteManagerAdmin(AdminRosterVO param);
}
