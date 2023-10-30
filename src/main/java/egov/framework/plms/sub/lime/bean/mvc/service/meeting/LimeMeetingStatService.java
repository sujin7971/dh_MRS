package egov.framework.plms.sub.lime.bean.mvc.service.meeting;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.mvc.entity.common.StatVO;
import egov.framework.plms.main.bean.mvc.service.meeting.abst.MeetingStatAbstractService;
import egov.framework.plms.sub.lime.bean.mvc.mapper.meeting.LimeMeetingStatMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile("lime")
@Primary
public class LimeMeetingStatService extends MeetingStatAbstractService<StatVO>{
	public LimeMeetingStatService(@Autowired LimeMeetingStatMapper mapper) {
		super(mapper);
	}
	@Override
	public Integer getPaperlessStatForCompany(LocalDate startDate, LocalDate endDate) {
		return super.getPaperlessStatForCompany(StatVO.builder().startDate(startDate).endDate(endDate).build());
	}
	@Override
	public Optional<StatVO> getMeetingCountAndAverageDurationStatForCompany(LocalDate startDate, LocalDate endDate) {
		return super.getMeetingCountAndAverageDurationStatForCompany(StatVO.builder().startDate(startDate).endDate(endDate).build());
	}
	@Override
	public List<StatVO> getTop5DepartmentWithMeetingForCompany(LocalDate startDate, LocalDate endDate) {
		return super.getTop5DepartmentWithMeetingForCompany(StatVO.builder().startDate(startDate).endDate(endDate).build());
	}
	@Override
	public List<StatVO> getMeetingMonthlyTrendForCompany(LocalDate startDate, LocalDate endDate) {
		return super.getMeetingMonthlyTrendForCompany(StatVO.builder().startDate(startDate).endDate(endDate).build());
	}
	public List<StatVO> getMeetingMonthlyTrendWithPrevYearForCompany(LocalDate startDate, LocalDate endDate) {
		return this.getMeetingMonthlyTrendWithPrevYear(super::getMeetingMonthlyTrendForCompany, null, startDate, endDate);
	}
	@Override
	public Integer getPaperlessStatForPersonal(String userId, LocalDate startDate, LocalDate endDate) {
		return super.getPaperlessStatForPersonal(StatVO.builder().userId(userId).startDate(startDate).endDate(endDate).build());
	}
	@Override
	public Optional<StatVO> getMeetingCountAndTotalDurationStatForHosting(String userId, LocalDate startDate, LocalDate endDate) {
		return super.getMeetingCountAndTotalDurationStatForHosting(StatVO.builder().userId(userId).startDate(startDate).endDate(endDate).build());
	}
	@Override
	public Optional<StatVO> getMeetingCountAndTotalDurationStatForAttendance(String userId, LocalDate startDate, LocalDate endDate) {
		return super.getMeetingCountAndTotalDurationStatForAttendance(StatVO.builder().userId(userId).startDate(startDate).endDate(endDate).build());
	}
	@Override
	public List<StatVO> getTop5DepartmentWithMeetingForOffice(String officeCode, LocalDate startDate, LocalDate endDate) {
		return super.getTop5DepartmentWithMeetingForOffice(StatVO.builder().officeCode(officeCode).startDate(startDate).endDate(endDate).build());
	}
	@Override
	public List<StatVO> getMeetingMonthlyTrendForPersonal(String userId, LocalDate startDate, LocalDate endDate) {
		return super.getMeetingMonthlyTrendForPersonal(StatVO.builder().userId(userId).startDate(startDate).endDate(endDate).build());
	}
	public List<StatVO> getMeetingMonthlyTrendWithPrevYearForPersonal(String userId, LocalDate startDate, LocalDate endDate) {
		return this.getMeetingMonthlyTrendWithPrevYear(super::getMeetingMonthlyTrendForPersonal, userId, startDate, endDate);
	}
	
	public List<StatVO> getMeetingMonthlyTrendWithPrevYear(
		    Function<StatVO, List<StatVO>> getMeetingMonthlyTrend,
		    String userId,
		    LocalDate startDate,
		    LocalDate endDate
		) {
		    startDate = startDate.with(TemporalAdjusters.firstDayOfMonth());
		    endDate = endDate.with(TemporalAdjusters.lastDayOfMonth());
		    List<StatVO> nowStatList = getMeetingMonthlyTrend.apply(StatVO.builder().userId(userId).startDate(startDate).endDate(endDate).build());
		    Map<String, Integer> nowStatMap = nowStatList.stream()
		    		.collect(Collectors.toMap(
		    		        vo -> vo.getRefYear() + "-" + vo.getRefMonth(),
		    		        StatVO::getStatValue1));

		    List<StatVO> prevStatList = getMeetingMonthlyTrend.apply(StatVO.builder().startDate(startDate.minusYears(1)).endDate(endDate.minusYears(1)).build());
		    Map<String, Integer> prevStatMap = prevStatList.stream()
		    		.collect(Collectors.toMap(
		    		        vo -> vo.getRefYear() + "-" + vo.getRefMonth(),
		    		        StatVO::getStatValue1));

		    List<StatVO> mergeList = new ArrayList<>();
		    while (!startDate.isAfter(endDate)) {
		    	Integer year = startDate.getYear();
		        Integer month = startDate.getMonthValue();
		        Integer nowCount = Optional.ofNullable(nowStatMap.get(year+"-"+month)).orElse(0);
		        Integer prevCount = Optional.ofNullable(prevStatMap.get((year-1)+"-"+month)).orElse(0);
		        mergeList.add(StatVO.builder().refYear(year).refMonth(month).statValue1(nowCount).statValue2(prevCount).build());
		        startDate = startDate.plusMonths(1);
		    }
		    return mergeList;
		}
}
