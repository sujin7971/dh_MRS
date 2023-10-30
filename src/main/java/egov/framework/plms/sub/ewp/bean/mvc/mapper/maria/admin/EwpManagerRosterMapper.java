package egov.framework.plms.sub.ewp.bean.mvc.mapper.maria.admin;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.main.bean.mvc.entity.admin.AdminRosterVO;

@Mapper
public interface EwpManagerRosterMapper {

	List<AdminRosterVO> getAllRoomManagerList(AdminRosterVO param);
}
