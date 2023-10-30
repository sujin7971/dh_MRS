package egov.framework.plms.sub.lime.bean.mvc.service.organization;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.mvc.entity.organization.UserAccountVO;
import egov.framework.plms.main.core.util.secure.SHA256Util;
import egov.framework.plms.sub.lime.bean.mvc.mapper.organization.LimeUserAccountMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile("lime")
@Primary
public class LimeUserAccountService {
	@Autowired
	private LimeUserAccountMapper mapper;
	
	public boolean insertUserAccount(String id) {
		return insertUserAccount(id, id);
	}
	
	public boolean insertUserAccount(String userId, String pw) {
		String salt = SHA256Util.generateBcryptSalt();
		String password = SHA256Util.getEncrypt(pw, salt);
		try {
			Integer result =  mapper.insertUserAccount(UserAccountVO.builder().userId(userId).salt(salt).userPw(password).build());
			return (result == 0)?false:true;
		}catch(Exception e) {
			return false;
		}
	}
	
	public boolean updateUserAccount(UserAccountVO param) {
		try {
			Integer result =  mapper.updateUserAccount(param);
			return (result == 0)?false:true;
		}catch(Exception e) {
			return false;
		}
	}
	
	public boolean updateUserPassword(String userId) {
		return updateUserPassword(userId, userId);
	}

	public boolean updateUserPassword(String userId, String password) {
		String salt = SHA256Util.generateBcryptSalt();
		password = SHA256Util.getEncrypt(password, salt);
		
		return updateUserAccount(UserAccountVO.builder().userId(userId).userPw(password).failedAttempts(0).build());
	}
	
	public Optional<UserAccountVO> selectUserAccountOne(String userId) {
		UserAccountVO accountVO = mapper.selectUserAccountOne(userId);
		return Optional.ofNullable(accountVO);
	}
	
	public List<UserAccountVO> selectUserAccountList(UserAccountVO param) {
		return mapper.selectUserAccountList(param);
	}
}
