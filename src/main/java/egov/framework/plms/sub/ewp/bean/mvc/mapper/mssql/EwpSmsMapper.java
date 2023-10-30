package egov.framework.plms.sub.ewp.bean.mvc.mapper.mssql;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.transaction.annotation.Transactional;

import egov.framework.plms.sub.ewp.bean.mvc.entity.alarm.EwpSmsVO;

@Mapper
public interface EwpSmsMapper {
	Integer doSndSms(EwpSmsVO param);
}

