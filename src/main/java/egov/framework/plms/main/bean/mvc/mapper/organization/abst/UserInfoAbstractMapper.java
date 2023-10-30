package egov.framework.plms.main.bean.mvc.mapper.organization.abst;

import java.util.List;

import egov.framework.plms.main.bean.mvc.entity.organization.abst.UserInfoModelVO;


public interface UserInfoAbstractMapper<T extends UserInfoModelVO> {
	T selectUserInfoOne(String userId);
	List<T> selectUserInfoList(T params);
}
