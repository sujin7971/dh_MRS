package egov.framework.plms.sub.lime.bean.mvc.service.organization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.mvc.entity.organization.UserInfoVO;
import egov.framework.plms.main.bean.mvc.service.organization.abst.UserInfoAbstractService;
import egov.framework.plms.sub.lime.bean.mvc.mapper.organization.LimeUserInfoMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile("lime")
@Primary
public class LimeUserInfoService extends UserInfoAbstractService<UserInfoVO>{
	private LimeUserInfoMapper mapper;
	
	public LimeUserInfoService(@Autowired LimeUserInfoMapper mapper) {
		super(mapper);
		this.mapper = mapper;
	}
	
	public boolean insertUserInfoOne(UserInfoVO param) {
		try {
			Integer result =  mapper.insertUserInfoOne(param);
			return (result == 0)?false:true;
		}catch(Exception e) {
			return false;
		}
		
	}
	
	public boolean updateUserInfoOne(UserInfoVO param) {
		try {
			Integer result =  mapper.updateUserInfoOne(param);
			return (result == 0)?false:true;
		}catch(Exception e) {
			return false;
		}
		
	}
	
	public boolean updateUserInfoOneToDelete(String userId) {
		try {
			Integer result =  mapper.updateUserInfoOneToDelete(userId);
			return (result == 0)?false:true;
		}catch(Exception e) {
			return false;
		}
		
	}
	
	public boolean deleteUserInfoOne(String userId) {
		try {
			Integer result = mapper.deleteUserInfoOne(userId);
			return (result == 0)?false:true;
		}catch(Exception e) {
			return false;
		}
		
	}
	
}
