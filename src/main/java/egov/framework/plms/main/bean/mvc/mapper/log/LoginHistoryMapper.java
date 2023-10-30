package egov.framework.plms.main.bean.mvc.mapper.log;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.main.bean.mvc.entity.log.LoginHistoryVO;

@Mapper
public interface LoginHistoryMapper {
	Integer insertLoginHistory(LoginHistoryVO param);
	List<LoginHistoryVO> selectLoginHistoryList(LoginHistoryVO param);
}
