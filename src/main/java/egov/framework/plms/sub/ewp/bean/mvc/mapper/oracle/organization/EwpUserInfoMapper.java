package egov.framework.plms.sub.ewp.bean.mvc.mapper.oracle.organization;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.main.bean.mvc.mapper.organization.abst.UserInfoAbstractMapper;
import egov.framework.plms.sub.ewp.bean.mvc.entity.organization.EwpUserInfoVO;

@Mapper
public interface EwpUserInfoMapper extends UserInfoAbstractMapper<EwpUserInfoVO> {
}
