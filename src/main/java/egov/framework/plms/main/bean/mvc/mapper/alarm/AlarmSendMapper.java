package egov.framework.plms.main.bean.mvc.mapper.alarm;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.transaction.annotation.Transactional;

import egov.framework.plms.main.bean.mvc.entity.alarm.AlarmSendDTO;
import egov.framework.plms.main.bean.mvc.entity.alarm.AlarmSendVO;

@Mapper
public interface AlarmSendMapper {
	Integer doSndMsg(AlarmSendVO param);
	Integer updateAlarmSend(AlarmSendVO param);
	List<AlarmSendVO> getAlarmSendListForPending(AlarmSendVO param);
	List<AlarmSendVO> getAlarmSendListForUserPopup(AlarmSendVO params);
	
	List<AlarmSendVO> getMeetingInfoSendMonthList(AlarmSendVO param);
	List<AlarmSendVO> getMeetingInfoSendDayList(AlarmSendVO param);
	List<AlarmSendVO> getMeetingInfoSendHourList(AlarmSendVO param);
	
	Integer putAlarmReadYN(AlarmSendVO param);
	Integer putAllAlarmReadYN(String userKey);
	AlarmSendVO getAlarmOne(LocalDateTime alarmNo);
}

