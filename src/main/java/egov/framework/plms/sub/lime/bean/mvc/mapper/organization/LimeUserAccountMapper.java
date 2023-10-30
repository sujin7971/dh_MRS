package egov.framework.plms.sub.lime.bean.mvc.mapper.organization;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.main.bean.mvc.entity.organization.UserAccountVO;

@Mapper
public interface LimeUserAccountMapper {
	Integer insertUserAccount(UserAccountVO param);
	Integer updateUserAccount(UserAccountVO param);
	UserAccountVO selectUserAccountOne(String userId);
	List<UserAccountVO> selectUserAccountList(UserAccountVO param);
}
