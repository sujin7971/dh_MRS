package egov.framework.plms.sub.ewp.bean.mvc.service.organization;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.mvc.service.organization.abst.UserInfoAbstractService;
import egov.framework.plms.sub.ewp.bean.mvc.entity.organization.EwpUserInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.mapper.oracle.organization.EwpUserInfoMapper;
import egov.framework.plms.sub.ewp.bean.mvc.service.common.EwpCodeService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile("ewp")
@Primary
public class EwpUserInfoService extends UserInfoAbstractService<EwpUserInfoVO>{
	public EwpUserInfoService(@Autowired EwpUserInfoMapper mapper) {
		super(mapper);
	}
	@Autowired
	private EwpCodeService codeServ;
	
	@Override
	public Optional<EwpUserInfoVO> selectUserInfoOne(String userId) {
		EwpUserInfoVO userVO = super.selectUserInfoOne(userId).map(user -> user.toBuilder().officeName(codeServ.getOfficeName(user.getOfficeCode())).build()).orElse(null);
		return Optional.ofNullable(userVO);
	}
	
	@Override
	public List<EwpUserInfoVO> selectUserInfoList(EwpUserInfoVO searchParam) {
		List<EwpUserInfoVO> userList = mapper.selectUserInfoList(searchParam);
		return userList.stream().map(user -> user.toBuilder().officeName(codeServ.getOfficeName(user.getOfficeCode())).build()).collect(Collectors.toList());
	}
}
