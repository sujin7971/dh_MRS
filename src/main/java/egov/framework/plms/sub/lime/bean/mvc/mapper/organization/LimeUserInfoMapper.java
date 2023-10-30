package egov.framework.plms.sub.lime.bean.mvc.mapper.organization;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.main.bean.mvc.entity.organization.UserInfoVO;
import egov.framework.plms.main.bean.mvc.mapper.organization.abst.UserInfoAbstractMapper;

@Mapper
public interface LimeUserInfoMapper extends UserInfoAbstractMapper<UserInfoVO>{
	Integer insertUserInfoOne(UserInfoVO param);
	Integer updateUserInfoOne(UserInfoVO param);
	Integer updateUserInfoOneToDelete(String userId);
	Integer deleteUserInfoOne(String userId);
}
