package egov.framework.plms.sub.lime.bean.mvc.controller.meeting;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import egov.framework.plms.main.bean.mvc.entity.common.StatDTO;
import egov.framework.plms.main.bean.mvc.entity.common.StatVO;
import egov.framework.plms.main.bean.mvc.entity.organization.DeptInfoVO;
import egov.framework.plms.main.core.util.DateTimeUtil;
import egov.framework.plms.sub.lime.bean.mvc.service.meeting.LimeMeetingStatService;
import egov.framework.plms.sub.lime.bean.mvc.service.organization.LimeDeptInfoService;
import egov.framework.plms.sub.lime.core.util.LimeSecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/lime")
@RequiredArgsConstructor
@Profile("lime")
public class LimeMeetingStatRestController {
	private final LimeMeetingStatService serv;
	private final LimeDeptInfoService deptServ;
	
	/**
	 * 회사전체 용지 절약 통계 조회
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@GetMapping("/meeting/stat/paperless/company")
	public Integer getPaperlessStatForCompany(@RequestParam @Nullable String startDate, @RequestParam @Nullable String endDate) {
		return serv.getPaperlessStatForCompany(DateTimeUtil.toFormattedDateOrNull(startDate), DateTimeUtil.toFormattedDate(endDate));
	}
	
	/**
	 * 회사전체 총 회의 건 수 / 평균 회의시간 통계 조회
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@GetMapping("/meeting/stat/count-and-avgduration/company")
	public StatDTO getMeetingCountAndAverageDurationStatForCompany(@RequestParam @Nullable String startDate, @RequestParam @Nullable String endDate) {
		Optional<StatVO> opt = serv.getMeetingCountAndAverageDurationStatForCompany(DateTimeUtil.toFormattedDateOrNull(startDate), DateTimeUtil.toFormattedDate(endDate));
		return opt.map(StatVO::convert).orElse(null);
	}
	
	/**
	 * 회사전체 회의 개최가 많았던 부서 TOP5 통계 조회
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@GetMapping("/meeting/stat/top5-department/company")
	public List<StatDTO> getTop5DepartmentWithMeetingForCompany(@RequestParam @Nullable String startDate, @RequestParam @Nullable String endDate) {
		List<StatVO> list = serv.getTop5DepartmentWithMeetingForCompany(DateTimeUtil.toFormattedDateOrNull(startDate), DateTimeUtil.toFormattedDate(endDate));
		return list.stream().map(StatVO::convert).map(dto -> {
            dto.setRefName(deptServ.selectDeptInfoOne(dto.getRefId()).map(DeptInfoVO::getDeptName).orElse(null));
            return dto;
        }).collect(Collectors.toList());
	}
	
	/**
	 * 회사전체 전년도 대비 월 회의 건 수 추이 통계 조회
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@GetMapping("/meeting/stat/monthly-trend/company")
	public List<StatDTO> getMeetingMonthlyTrendWithPrevYearForCompany(@RequestParam @Nullable String startDate, @RequestParam @Nullable String endDate) {
		List<StatVO> list = serv.getMeetingMonthlyTrendWithPrevYearForCompany(DateTimeUtil.toFormattedDate(startDate), DateTimeUtil.toFormattedDate(endDate));
		return list.stream().map(StatVO::convert).collect(Collectors.toList());
	}
	
	/**
	 * 개인 용지 절약 통계 조회
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@GetMapping("/meeting/stat/paperless/personal")
	public Integer getPaperlessStatForPersonal(@RequestParam @Nullable String startDate, @RequestParam @Nullable String endDate) {
		return serv.getPaperlessStatForPersonal(LimeSecurityUtil.getLoginId(), DateTimeUtil.toFormattedDateOrNull(startDate), DateTimeUtil.toFormattedDate(endDate));
	}
	
	/**
	 * 내가 진행한 회의 / 총 회의시간 통계 조회
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@GetMapping("/meeting/stat/count-and-totalduration/hosting")
	public StatDTO getMeetingCountAndTotalDurationStatForHosting(@RequestParam @Nullable String startDate, @RequestParam @Nullable String endDate) {
		Optional<StatVO> opt = serv.getMeetingCountAndTotalDurationStatForHosting(LimeSecurityUtil.getLoginId(), DateTimeUtil.toFormattedDateOrNull(startDate), DateTimeUtil.toFormattedDate(endDate));
		return opt.map(StatVO::convert).orElse(null);
	}
	
	/**
	 * 참여 회의 건 수 / 총 회의시간 통계 조회
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@GetMapping("/meeting/stat/count-and-totalduration/attendance")
	public StatDTO getMeetingCountAndTotalDurationStatForAttendance(@RequestParam @Nullable String startDate, @RequestParam @Nullable String endDate) {
		Optional<StatVO> opt = serv.getMeetingCountAndTotalDurationStatForAttendance(LimeSecurityUtil.getLoginId(), DateTimeUtil.toFormattedDateOrNull(startDate), DateTimeUtil.toFormattedDate(endDate));
		return opt.map(StatVO::convert).orElse(null);
	}
	
	/**
	 * 개인 소속 사업소 회의 개최가 많았던 부서 TOP5 통계 조회
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@GetMapping("/meeting/stat/top5-department/office")
	public List<StatDTO> getTop5DepartmentWithMeetingForOffice(@RequestParam @Nullable String startDate, @RequestParam @Nullable String endDate) {
		List<StatVO> list = serv.getTop5DepartmentWithMeetingForOffice(LimeSecurityUtil.getOfficeCode(), DateTimeUtil.toFormattedDateOrNull(startDate), DateTimeUtil.toFormattedDate(endDate));
		return list.stream().map(StatVO::convert).map(dto -> {
            dto.setRefName(deptServ.selectDeptInfoOne(dto.getRefId()).map(DeptInfoVO::getDeptName).orElse(null));
            return dto;
        }).collect(Collectors.toList());
	}
	
	/**
	 * 개인 전년도 대비 월 회의 건 수 추이 통계 조회
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@GetMapping("/meeting/stat/monthly-trend/personal")
	public List<StatDTO> getMeetingMonthlyTrendWithPrevYearForPersonal(@RequestParam @Nullable String startDate, @RequestParam @Nullable String endDate) {
		List<StatVO> list = serv.getMeetingMonthlyTrendWithPrevYearForPersonal(LimeSecurityUtil.getLoginId(), DateTimeUtil.toFormattedDate(startDate), DateTimeUtil.toFormattedDate(endDate));
		return list.stream().map(StatVO::convert).collect(Collectors.toList());
	}
}
