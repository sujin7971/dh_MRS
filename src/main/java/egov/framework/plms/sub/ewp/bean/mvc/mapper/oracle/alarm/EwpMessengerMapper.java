package egov.framework.plms.sub.ewp.bean.mvc.mapper.oracle.alarm;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.sub.ewp.bean.mvc.entity.alarm.EwpMessengerDTO;

@Mapper
public interface EwpMessengerMapper {
	public int doSndMsg(EwpMessengerDTO param);
}
