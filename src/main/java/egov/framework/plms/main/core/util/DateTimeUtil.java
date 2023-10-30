package egov.framework.plms.main.core.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

import org.springframework.ui.Model;

import lombok.extern.slf4j.Slf4j;


/**
 * 날짜 객체 <-> String 상호 변경을 처리해줄 로직을 모아둔 클래스
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 */
@Slf4j
public class DateTimeUtil {
	public static boolean isAfterNow(LocalDateTime checkDT) {
		LocalDateTime nowDT = LocalDateTime.now();
		log.debug("isBeforeNow - checkDT: {}, nowDT: {}", checkDT, nowDT);
		return checkDT.isAfter(nowDT);
	}
	/**
     * 파라미터로 패턴을 전달받지 않고 "yyyy-MM-dd" 패턴을 적용한 formatter를 생성한다.<br>
     * {@link DateTimeFormatter DateTimeFormatter}의 
     * {@link DateTimeFormatter#ofPattern(String) DateTimeFormatter.ofPattern} 을 사용하여 formatter를 생성하므로 자세한 사항은 링크를 참조.
     * 
     * @return "yyyy-MM-dd" 패턴을 적용하여 생성한 formatter.
     */
	public static DateTimeFormatter format() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return formatter;
	}
	
	/**
     * 파라미터로 넘긴 패턴을 기반으로 formatter를 생성한다.<br>
     * {@link DateTimeFormatter DateTimeFormatter}의 
     * {@link DateTimeFormatter#ofPattern(String) DateTimeFormatter.ofPattern} 을 사용하여 formatter를 생성하므로 자세한 사항은 링크를 참조.
     *
     * @param 사용할 null이 아닌 패턴값 
     * @return 전달받은 패턴을 기반으로 생성한 formatter. 올바르지 않은 패턴값을 전달받은 경우 "yyyy-MM-dd" 패턴을 적용하여 생성한 formatter.
     * @see DateTimeFormatterBuilder#appendPattern(String)
     */
	public static DateTimeFormatter format(String pattern) {
		DateTimeFormatter formatter;
		try {
			formatter = DateTimeFormatter.ofPattern(pattern);
		}catch(IllegalArgumentException e) {
			log.debug("IllegalArgumentException", e);
			formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		}
		return formatter;
	}
	
	/**
     * 전달받은 날짜 문자열 <code>date</code>로부터 "yyyy-MM-dd" 패턴이 적용된 {@link LocalDate}객체를 반환한다.<br>
     * <code>date</code>이 LocalDate이 지원하지 않는 형식의 문자열인경우 현재 날짜를 기반으로 {@link LocalDate}객체를 반환한다.<br>
     * {@link LocalDate LocalDate}의 
     * {@link LocalDate#parse(CharSequence) LocalDate.parse} 을 사용하여 전달받은 날짜를 검증하므로 자세한 사항은 링크를 참조.
     *
     * @param date 사용할 날짜
     * @return 전달받은 날짜 문자열에 "yyyy-MM-dd" 패턴을 적용한 문자열
     * @see DateTimeUtil#format()
     */
	public static LocalDate toFormattedDate(String date) {
		LocalDate ld;
		try {
			ld = LocalDate.parse(date, DateTimeUtil.format());
		}catch(NullPointerException e) {
			log.debug("NullPointerException", e);
			ld = LocalDate.now();
		}catch(DateTimeParseException e) {
			log.debug("DateTimeParseException", e);
			ld = LocalDate.now();
		}
		return ld;
	}
	
	/**
     * 전달받은 날짜 문자열 <code>date</code>로부터 "yyyy-MM-dd" 패턴이 적용된 {@link LocalDate}객체를 반환한다.<br>
     * <code>date</code>이 LocalDate이 지원하지 않는 형식의 문자열인경우 <code>NULL</code>을 반환한다.<br>
     * {@link LocalDate LocalDate}의 
     * {@link LocalDate#parse(CharSequence) LocalDate.parse} 을 사용하여 전달받은 날짜를 검증하므로 자세한 사항은 링크를 참조.
     *
     * @param date 사용할 날짜
     * @return 전달받은 날짜 문자열에 "yyyy-MM-dd" 패턴을 적용한 문자열, 실패한 경우 <code>NULL</code>
     * @see DateTimeUtil#format()
     */
	public static LocalDate toFormattedDateOrNull(String date) {
	    LocalDate ld;
	    try {
	        ld = LocalDate.parse(date, DateTimeUtil.format());
	    } catch (NullPointerException e) {
	        log.debug("NullPointerException", e);
	        ld = null;
	    } catch (DateTimeParseException e) {
	        log.debug("DateTimeParseException", e);
	        ld = null;
	    }
	    return ld;
	}
	
	/**
     * 전달받은 날짜 문자열 <code>date</code>로부터 입력한 <code>pattern</code> 패턴이 적용된 {@link LocalDate}객체를 반환한다.<br>
     * <code>date</code>이 LocalDate이 지원하지 않는 형식의 문자열인경우 현재 날짜를 기반으로 {@link LocalDate}객체를 반환한다.<br>
     * <code>pattern</code>이 DateTimeFormatter이 지원하지 않는 형식의 문자열인경우 "yyyy-MM-dd"패턴을 적용한다.<br>
     * {@link LocalDate LocalDate}의 
     * {@link LocalDate#parse(CharSequence) LocalDate.parse} 을 사용하여 전달받은 날짜를 검증하므로 자세한 사항은 링크를 참조.
     *
     * @param date 사용할 날짜
     * @param pattern 적용할 null이 아닌 패턴
     * @return 전달받은 날짜 문자열에 {@link pattern} 패턴을 적용한 문자열
     * @see DateTimeUtil#format(String)
     */
	public static LocalDate toFormattedDate(String date, String pattern) {
		LocalDate ld;
		try {
			ld = LocalDate.parse(date, DateTimeUtil.format(pattern));
		}catch(NullPointerException e) {
			log.debug("NullPointerException", e);
			ld = LocalDate.now();
		}catch(DateTimeParseException e) {
			log.debug("DateTimeParseException", e);
			ld = LocalDate.now();
		}
		return ld;
	}
	
	/**
     * 전달받은 날짜 문자열 <code>date</code>로부터 입력한 <code>pattern</code> 패턴이 적용된 {@link LocalDate}객체를 반환한다.<br>
     * <code>date</code>이 LocalDate이 지원하지 않는 형식의 문자열인경우 현재 날짜를 기반으로 {@link LocalDate}객체를 반환한다.<br>
     * <code>pattern</code>이 DateTimeFormatter이 지원하지 않는 형식의 문자열인경우 <code>NULL</code>을 반환한다.<br>
     * {@link LocalDate LocalDate}의 
     * {@link LocalDate#parse(CharSequence) LocalDate.parse} 을 사용하여 전달받은 날짜를 검증하므로 자세한 사항은 링크를 참조.
     *
     * @param date 사용할 날짜
     * @param pattern 적용할 null이 아닌 패턴
     * @return 전달받은 날짜 문자열에 {@link pattern} 패턴을 적용한 문자열, 실패한 경우 <code>NULL</code>
     * @see DateTimeUtil#format(String)
     */
	public static LocalDate toFormattedDateOrNull(String date, String pattern) {
		LocalDate ld;
		try {
			ld = LocalDate.parse(date, DateTimeUtil.format(pattern));
		}catch(NullPointerException e) {
			log.debug("NullPointerException", e);
			ld = null;
	    } catch (DateTimeParseException e) {
	        log.debug("DateTimeParseException", e);
	        ld = null;
	    }
	    return ld;
	}
	
	/**
     * "HH:mm" 패턴이 적용된 시간 문자열 <code>time</code>로부터  {@link LocalTime}객체를 반환한다.<br>
     * <code>time</code>이 "HH:mm" 패턴 형식의  문자열이 아닌 경우 현재 시간을 기반으로 {@link LocalTime}객체를 반환한다.<br>
     * {@link LocalTime LocalTime}의 
     * {@link LocalTime#parse(CharSequence) LocalTime.parse} 을 사용하여 전달받은 시간을 검증하므로 자세한 사항은 링크를 참조.
     *
     * @param date 사용할 날짜
     * @return 전달받은 날짜 문자열에 "yyyy-MM-dd" 패턴을 적용한 문자열
     * @see DateTimeUtil#format()
     */
	public static LocalTime toFormattedTime(String time) {
		LocalTime lt;
		try {
			lt = LocalTime.parse(time, DateTimeUtil.format("HH:mm"));
		}catch(NullPointerException e) {
			log.debug("NullPointerException", e);
			lt = LocalTime.now();
		}catch(DateTimeParseException e) {
			log.debug("DateTimeParseException", e);
			lt = LocalTime.now();
		}
		return lt;
	}
	
	/**
     * <code>pattern<code> 패턴이 적용된 시간 문자열 <code>time</code>로부터  {@link LocalTime}객체를 반환한다.<br>
     * <code>time</code>이 <code>pattern<code> 형식의  문자열이 아닌 경우 현재 시간을 기반으로 {@link LocalTime}객체를 반환한다.<br>
     * {@link LocalTime LocalTime}의 
     * {@link LocalTime#parse(CharSequence) LocalTime.parse} 을 사용하여 전달받은 시간을 검증하므로 자세한 사항은 링크를 참조.
     *
     * @param date 사용할 날짜
     * @return 전달받은 날짜 문자열에 "yyyy-MM-dd" 패턴을 적용한 문자열
     * @see DateTimeUtil#format()
     */
	public static LocalTime toFormattedTime(String time, String pattern) {
		LocalTime lt;
		try {
			lt = LocalTime.parse(time, DateTimeUtil.format("HH:mm"));
		}catch(NullPointerException e) {
			log.debug("NullPointerException", e);
			lt = LocalTime.now();
		}catch(DateTimeParseException e) {
			log.debug("DateTimeParseException", e);
			lt = LocalTime.now();
		}
		return lt;
	}
	
	/**
	 * 주어진 시간 범위내를 만족하는 값을 반환한다.
	 * @param nowTime 대상 시간
	 * @param minTime 최소 시간
	 * @param maxTime 최대 시간
	 * @return
	 */
	public static LocalTime getWithinRangeTime(LocalTime nowTime, LocalTime minTime, LocalTime maxTime) {
		if(nowTime.isBefore(minTime)) {
			return minTime;
		}else if(nowTime.isAfter(maxTime)) {
			return maxTime;
		}
		return nowTime;
	}
	
	/**
	 * 모델 객체에 검색을 위한 적절한 기간을 설정한다. 전달받은 기간이 없는 경우 당일 기준 2주 전 ~ 2주 후 를 검색 기간으로 설정한다.
	 * @param model
	 * @param dateStrOne 일자 1. <code>NULL<code>이거나 적합한 날짜 형식이 아닌 경우 당일로 취급한다.
	 * @param dateStrTwo 일자 2. <code>NULL<code>이거나 적합한 날짜 형식이 아닌 경우 당일로 취급한다.
	 */
	public static void setModelOfDatePeriod(Model model, String dateStrOne, String dateStrTwo) {
		LocalDate dateOne = DateTimeUtil.toFormattedDate(dateStrOne);
		LocalDate dateTwo = DateTimeUtil.toFormattedDate(dateStrTwo);
		if(dateOne.isAfter(dateTwo)) {
			model.addAttribute("startDate", dateTwo);
			model.addAttribute("endDate", dateOne);
		}else if(dateTwo.isAfter(dateOne)) {
			model.addAttribute("startDate", dateOne);
			model.addAttribute("endDate", dateTwo);
		}else {
			model.addAttribute("startDate", dateOne.minusWeeks(2));
			model.addAttribute("endDate", dateTwo.plusWeeks(2));
		}
	}
	
	/**
	 * 모델 객체에 예약 시간 범위를 설정한다. 전달받은 시간이 없는 경우 현재 시간 기준으로 지정. 설정된 시간이 interval에 부합하지 않는 경우 조정 처리.
	 * @param model
	 * @param timeOneStr 시간 문자열 1. <code>NULL<code>이거나 적합한 날짜 형식이 아닌 경우 현재시간으로 취급한다.
	 * @param timeTwoStr 시간 문자열 2. <code>NULL<code>이거나 적합한 날짜 형식이 아닌 경우 현재시간으로 취급한다.
	 * @param interval 예약 시간 단위(분). 설정된 시간이 시간 단위와 맞지 않는 경우 조정하기 위한 값.
	 */
	public static void setModelOfTimePeriod(Model model, String timeOneStr, String timeTwoStr, int interval, LocalTime minTime, LocalTime maxTime) {
		log.debug("timeStr 1: {}, timeStr 2: {}", timeOneStr, timeTwoStr);
		LocalTime timeOne = DateTimeUtil.toFormattedTime(timeOneStr);
		timeOne = getWithinRangeTime(timeOne, minTime, maxTime);
		int timeOneMinute = timeOne.getMinute();
		LocalTime timeTwo = DateTimeUtil.toFormattedTime(timeTwoStr);
		timeTwo = getWithinRangeTime(timeTwo, minTime, maxTime);
		int timeTwoMinute = timeTwo.getMinute();
		log.debug("timeOne: {}, timeTwo: {}, interval: {}", timeOne.toString(), timeTwo.toString(), interval);
		if(timeOne.getMinute() % interval != 0) {
			log.debug("timeOne.getMinute() % interval: {}", timeOne.getMinute() % interval);
			timeOne = timeOne.withMinute(timeOneMinute / interval * interval);
		}
		if(timeTwo.getMinute() % interval != 0) {
			log.debug("timeTwo.getMinute() % interval: {}", timeTwo.getMinute() % interval);
			timeTwo = timeTwo.withMinute(timeTwoMinute / interval * interval);
		}
		if(timeOne.isAfter(timeTwo)) {
			model.addAttribute("startTime", timeTwo.format(DateTimeUtil.format("HH:mm")));
			model.addAttribute("endTime", timeOne.format(DateTimeUtil.format("HH:mm")));
		}else if(timeTwo.isAfter(timeOne)) {
			model.addAttribute("startTime", timeOne.format(DateTimeUtil.format("HH:mm")));
			model.addAttribute("endTime", timeTwo.format(DateTimeUtil.format("HH:mm")));
		}else {
			model.addAttribute("startTime", timeOne.format(DateTimeUtil.format("HH:mm")));
			model.addAttribute("endTime", timeTwo.plusHours(3).format(DateTimeUtil.format("HH:mm")));
		}
	}
	
	/**
	 * 분단위 한계값 limit의 배수인 LocalDateTime을 지정단 한계값 단위로 잘라 반환.
	 * 이때 반환값은 현재 시간을 넘지 않는다.
	 * @param minuteLimit 결과를 제한할 분단위 한계 값
	 * @return 한계값을 배수로 갖는 LocalDateTime
	 */
	public static LocalDateTime getTruncatedDateTime(long minuteLimit) {
        return getTruncatedDateTime(LocalDateTime.now(), minuteLimit, ChronoUnit.MINUTES);
    }
	
	/**
	 * 분단위 한계값 limit의 배수인 LocalDateTime을 지정단 한계값 단위로 잘라 반환.
	 * 이때 반환값은 dateTime을 넘지 않는다.
	 * @param dateTime 한계값을 적용할 시간
	 * @param minuteLimit 결과를 제한할 분단위 한계 값
	 * @return 한계값을 배수로 갖는 LocalDateTime
	 */
	public static LocalDateTime getTruncatedDateTime(LocalDateTime dateTime, long minuteLimit) {
        return getTruncatedDateTime(dateTime, minuteLimit, ChronoUnit.MINUTES);
    }
	
	/**
	 * unit단위 한계값 limit의 배수인 LocalDateTime을 지정단 한계값 단위로 잘라 반환.
	 * 이때 반환값은 현재 시간을 넘지 않는다.
	 * @param limit 결과를 제한할 분단위 한계 값
	 * @param unit 한계값의 시간단위
	 * @return 한계값을 배수로 갖는 LocalDateTime
	 */
	public static LocalDateTime getTruncatedDateTime(long limit, ChronoUnit unit) {
        return getTruncatedDateTime(LocalDateTime.now(), limit, unit);
    }
	
	/**
	 * unit단위 한계값 limit의 배수인 LocalDateTime을 지정단 한계값 단위로 잘라 반환.
	 * 이때 반환값은 dateTime을 넘지 않는다.
	 * @param dateTime 한계값을 적용할 시간
	 * @param limit 결과를 제한할 한계 값
	 * @param unit 한계값의 시간단위
	 * @return 한계값을 배수로 갖는 LocalDateTime
	 */
	public static LocalDateTime getTruncatedDateTime(LocalDateTime dateTime, long limit, ChronoUnit unit) {
        LocalDateTime result = dateTime.truncatedTo(unit);
        long value = unit.between(LocalDateTime.MIN, result);
        long remainder = value % limit;
        long toSubtract = remainder == 0 ? limit : remainder;
        result = result.minus(toSubtract, unit);
        return result;
    }
}
