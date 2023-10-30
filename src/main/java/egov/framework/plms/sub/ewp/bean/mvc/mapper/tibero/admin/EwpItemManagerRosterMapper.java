package egov.framework.plms.sub.ewp.bean.mvc.mapper.tibero.admin;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.sub.ewp.bean.mvc.entity.admin.EwpItemManagerRosterVO;

@Mapper
public interface EwpItemManagerRosterMapper {
	//배정승인자 검색
	List<EwpItemManagerRosterVO> getItemManagerList(EwpItemManagerRosterVO param);
}
