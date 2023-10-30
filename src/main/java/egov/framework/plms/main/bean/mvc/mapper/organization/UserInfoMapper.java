package egov.framework.plms.main.bean.mvc.mapper.organization;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.main.bean.mvc.entity.organization.UserInfoVO;
import egov.framework.plms.main.bean.mvc.mapper.organization.abst.UserInfoAbstractMapper;

@Mapper
public interface UserInfoMapper extends UserInfoAbstractMapper<UserInfoVO> {
}
