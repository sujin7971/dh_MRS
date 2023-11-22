package egov.framework.plms.sub.lime.bean.mvc.controller.organization;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import egov.framework.plms.main.bean.mvc.entity.organization.DeptInfoDTO;
import egov.framework.plms.main.bean.mvc.entity.organization.DeptInfoVO;
import egov.framework.plms.main.bean.mvc.entity.organization.UserInfoVO;
import egov.framework.plms.main.core.model.response.ResponseMessage;
import egov.framework.plms.main.core.model.response.ResponseMessage.StatusCode;
import egov.framework.plms.sub.lime.bean.mvc.service.organization.LimeDeptInfoService;
import egov.framework.plms.sub.lime.bean.mvc.service.organization.LimeUserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/lime")
@RequiredArgsConstructor
@Profile("lime")
public class LimeDeptRestController {
	private final LimeDeptInfoService deptService;
	private final LimeUserInfoService userService;
	
	@PostMapping("/admin/system/dept")
	public ResponseMessage insertDeptOne(DeptInfoDTO params) {
		log.info("insert dept : {}", params);
		boolean result = deptService.insertDeptInfoOne(params.convert());
		if(result) {
			return ResponseMessage.builder(StatusCode.OK).build();
		}else {
			return ResponseMessage.builder(StatusCode.BAD_REQUEST).build();
		}
	}
	
	@DeleteMapping("/admin/system/dept/{deptId}")
	public ResponseMessage deleteDeptOne(@PathVariable String deptId) {
		boolean deptResult = deptService.updateDeptInfoOneToDelete(deptId);
		if(deptResult) {
			UserInfoVO vo = UserInfoVO.builder().deptId(deptId).build();
			List<UserInfoVO> listVO = userService.selectUserInfoList(vo);
			if(listVO.size() > 0) {
				boolean userResult = userService.updateDeletedDeptUser(deptId);
				if(userResult) {
					return ResponseMessage.builder(StatusCode.OK).build();
				}else {
					return ResponseMessage.builder(StatusCode.BAD_REQUEST).build();
				}				
			} else {
				return ResponseMessage.builder(StatusCode.OK).build();
			}
		}else {
			return ResponseMessage.builder(StatusCode.BAD_REQUEST).build();
		}
	}
	
	/**
	 * 부서 정보 조회
	 * @param deptId: 조회할 부서 아이디
	 * @return
	 */
	@GetMapping("/dept/{deptId}")
	public DeptInfoDTO getDeptOne(@PathVariable String deptId) {
		Optional<DeptInfoVO> opt = deptService.selectDeptInfoOne(deptId);
		return opt.map(DeptInfoVO::convert).orElse(null);
	}
	
	
	
	/**
	 * 하위 부서 목록 조회
	 * @param deptId: 목록을 조회할 상위 부서 아이디
	 * @return
	 */
	@GetMapping("/dept/{deptId}/sub")
	public List<DeptInfoDTO> selectSubDeptInfoList(@PathVariable String deptId) {
		List<DeptInfoVO> subDeptList = deptService.selectSubDeptInfoList(deptId);
		return subDeptList.stream().map(dept -> dept.convert()).collect(Collectors.toList());
	}
	
	@GetMapping("/dept/{deptId}/sub/recursive")
	public List<DeptInfoDTO> selectRecursiveSubDeptInfoList(@PathVariable String deptId) {
		List<DeptInfoVO> subDeptList = deptService.selectRecursiveSubDeptInfoList(deptId);
		return subDeptList.stream().map(dept -> dept.convert()).collect(Collectors.toList());
	}
	
	@GetMapping("/dept/all")
	public List<DeptInfoDTO> selectAllDeptInfoList() {
		List<DeptInfoVO> subDeptList = deptService.selectAllDeptInfoList();
		return subDeptList.stream().map(dept -> dept.convert()).collect(Collectors.toList());
	}
}
