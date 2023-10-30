package egov.framework.plms.sub.ewp.bean.mvc.controller.organization;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import egov.framework.plms.sub.ewp.bean.mvc.entity.organization.EwpDeptInfoDTO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.organization.EwpDeptInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.service.organization.EwpDeptInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/ewp")
@RequiredArgsConstructor
@Profile("ewp")
public class EwpDeptRestController {
	private final EwpDeptInfoService deptService;
	
	/**
	 * 부서 정보 조회
	 * @param deptId: 조회할 부서 아이디
	 * @return
	 */
	@GetMapping("/dept/{deptId}")
	public EwpDeptInfoDTO getDeptOne(@PathVariable String deptId) {
		Optional<EwpDeptInfoVO> opt = deptService.selectDeptInfoOne(deptId);
		return opt.map(EwpDeptInfoVO::convert).orElse(null);
	}
	
	/**
	 * 하위 부서 목록 조회
	 * @param deptId: 목록을 조회할 상위 부서 아이디
	 * @return
	 */
	@GetMapping("/dept/{deptId}/sub")
	public List<EwpDeptInfoDTO> selectSubDeptInfoList(@PathVariable String deptId) {
		List<EwpDeptInfoVO> subDeptList = deptService.selectSubDeptInfoList(deptId);
		return subDeptList.stream().map(dept -> dept.convert()).collect(Collectors.toList());
	}
}
