package egov.framework.plms.sub.ewp.bean.mvc.mapper.oracle.organization;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.main.bean.mvc.mapper.organization.abst.DeptInfoAbstractMapper;
import egov.framework.plms.sub.ewp.bean.mvc.entity.organization.EwpDeptInfoVO;

@Mapper
public interface EwpDeptInfoMapper extends DeptInfoAbstractMapper<EwpDeptInfoVO>{
}
