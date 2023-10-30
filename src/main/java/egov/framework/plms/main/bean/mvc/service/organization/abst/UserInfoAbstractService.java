package egov.framework.plms.main.bean.mvc.service.organization.abst;

import java.util.List;
import java.util.Optional;

import egov.framework.plms.main.bean.mvc.entity.organization.abst.UserInfoModelVO;
import egov.framework.plms.main.bean.mvc.mapper.organization.abst.UserInfoAbstractMapper;

public abstract class UserInfoAbstractService<T extends UserInfoModelVO> {
	protected final UserInfoAbstractMapper<T> mapper;
	
	public UserInfoAbstractService(UserInfoAbstractMapper<T> mapper) {
		this.mapper = mapper;
	}
	
	public Optional<T> selectUserInfoOne(String userId){
		T userVO = mapper.selectUserInfoOne(userId);
		return Optional.ofNullable(userVO);
	}
	
	public List<T> selectUserInfoList(T params){
		return mapper.selectUserInfoList(params);
	}
}
