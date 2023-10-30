package egov.framework.plms.sub.ewp.bean.mvc.controller.alarm;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import egov.framework.plms.main.bean.mvc.entity.alarm.AlarmSendDTO;
import egov.framework.plms.main.bean.mvc.entity.alarm.AlarmSendVO;
import egov.framework.plms.main.core.exception.abst.ApiException;
import egov.framework.plms.sub.ewp.bean.mvc.service.alarm.EwpAlarmSendService;
import egov.framework.plms.sub.ewp.core.util.EwpSecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ewp")
@Profile("ewp")
public class EwpAlarmRestController {
	private final EwpAlarmSendService alarmSendService;
	
	@GetMapping("/alarm/popup/list")
	public List<AlarmSendVO> getAlarmSendListForUserPopup(AlarmSendDTO params) {
		params.setUserKey(EwpSecurityUtil.getLoginId());
		List<AlarmSendVO> rlist = alarmSendService.getAlarmSendListForUserPopup(params.convert());
		return rlist;
	}
	
	@PutMapping("/alarm/{alarmNoStr}/read")
	public void readAlarm(@PathVariable String alarmNoStr) {
		DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd HH:mm:ss")
                .appendFraction(ChronoField.MICRO_OF_SECOND, 0, 6, true) // 마이크로초 지원
                .toFormatter();
		try {
			LocalDateTime alarmNo = LocalDateTime.parse(alarmNoStr, formatter);
			String loginId = EwpSecurityUtil.getLoginId();
			Optional<AlarmSendVO> opt = alarmSendService.getAlarmOne(alarmNo);
			if(opt.isPresent()) {
				if(loginId.equals(opt.get().getUserKey())) {
					alarmSendService.putAlarmReadYN(alarmNo, loginId);
				}
			}
		}catch(ApiException e) {
			
		}catch(Exception e) {
			
		}
	}
	
}
