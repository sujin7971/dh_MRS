package egov.framework.plms.main.bean.mvc.service.auth;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import egov.framework.plms.main.bean.mvc.entity.auth.ResourceAuthorityVO;
import egov.framework.plms.main.bean.mvc.entity.auth.ResourceGroupVO;
import egov.framework.plms.main.bean.mvc.entity.auth.ResourcePermissionVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingInfoModelVO;
import egov.framework.plms.main.bean.mvc.mapper.auth.AuthMapper;
import egov.framework.plms.main.bean.mvc.service.auth.abst.AbstractAuthService;
import egov.framework.plms.main.bean.mvc.service.meeting.abst.MeetingInfoAbstractService;
import egov.framework.plms.main.core.model.enums.meeting.AttendRole;
import egov.framework.plms.main.core.model.enums.user.DomainRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("MeetingAuthService")
@RequiredArgsConstructor
public class MeetingAuthService extends AbstractAuthService {
	private final AuthMapper mapper;
	@Override
	public Integer postResourcePermission(ResourcePermissionVO param) {
		return mapper.postMeetingPermission(param);
	}
	@Override
	public Integer postResourceGroup(ResourceGroupVO param) {
		return mapper.postMeetingGroup(param);
	}
	@Deprecated
	public Integer postDomainGroup(DomainRole domain) {
		return mapper.postMeetingGroup(ResourceGroupVO.builder().grpDiv("DOMAIN").grpCode(domain.getCode()).srcId(0).build());
	}
	@Transactional(rollbackFor = {Exception.class})
	public Integer postRoleGroup(AttendRole role, Integer meetingId, Integer permLvl) throws Exception{
		ResourcePermissionVO permVO = ResourcePermissionVO.builder().permLvl(permLvl).build();
		Integer result = postResourcePermission(permVO);
		if(result != 1) {
			return result;
		}
		Integer permId = permVO.getPermId();
		return mapper.postMeetingGroup(ResourceGroupVO.builder().permId(permId).grpDiv("ROLE").grpCode(role.getCode()).srcId(meetingId).build());
	}
	@Transactional(rollbackFor = {Exception.class})
	public Integer postDeptGroup(String deptId, Integer meetingId, Integer permLvl) throws Exception{
		ResourcePermissionVO permVO = ResourcePermissionVO.builder().permLvl(permLvl).build();
		Integer result = postResourcePermission(permVO);
		if(result != 1) {
			return result;
		}
		Integer permId = permVO.getPermId();
		return mapper.postMeetingGroup(ResourceGroupVO.builder().permId(permId).grpDiv("DEPT").grpCode(deptId).srcId(meetingId).build());
	}
	@Transactional(rollbackFor = {Exception.class})
	public Integer postUserGroup(String userId, Integer meetingId, Integer permLvl) throws Exception{
		ResourcePermissionVO permVO = ResourcePermissionVO.builder().permLvl(permLvl).build();
		Integer result = postResourcePermission(permVO);
		if(result != 1) {
			return result;
		}
		Integer permId = permVO.getPermId();
		return mapper.postMeetingGroup(ResourceGroupVO.builder().permId(permId).grpDiv("USER").grpCode(userId).srcId(meetingId).build());
	}
	@Override
	public Integer putResourcePermissionLvl(ResourcePermissionVO param) {
		return mapper.putMeetingPermissionLvl(param);
	}
	public Integer putResourcePermissionLvl(Integer permId, Integer permLvl) {
		return mapper.putMeetingPermissionLvl(ResourcePermissionVO.builder().permId(permId).permLvl(permLvl).build());
	}
	@Override
	public Integer deleteResourceGroup(String grpId) {
		ResourceGroupVO grpVO = getResourceGroupOne(grpId);
		if(grpVO == null) {
			return 0;
		}
		Integer permId = grpVO.getPermId();
		mapper.deleteMeetingPermission(permId);
		return mapper.deleteMeetingGroup(grpId);
	}
	@Deprecated
	public Integer deleteDomainGroup(DomainRole domain) {
		String grpId = ResourceGroupVO.generateId("DOMAIN", domain.getCode(), 0);
		return deleteResourceGroup(grpId);
	}
	public Integer deleteRoleGroup(AttendRole role, Integer meetingId) {
		String grpId = ResourceGroupVO.generateId("ROLE", role.getCode(), meetingId);
		return deleteResourceGroup(grpId);
	}
	public Integer deleteDeptGroup(String deptId, Integer meetingId) {
		String grpId = ResourceGroupVO.generateId("DEPT", deptId, meetingId);
		return deleteResourceGroup(grpId);
	}
	public Integer deleteUserGroup(String userId, Integer meetingId) {
		String grpId = ResourceGroupVO.generateId("USER", userId, meetingId);
		return deleteResourceGroup(grpId);
	}
	@Override
	public ResourcePermissionVO getResourcePermissionOne(Integer permId) {
		return mapper.getMeetingPermissionOne(permId);
	}
	public ResourcePermissionVO getRolePermissionOne(AttendRole role, Integer meetingId) {
		String grpId = ResourceGroupVO.generateId("ROLE", role.getCode(), meetingId);
		return getMeetingPermissionOne(grpId);
	}
	public ResourcePermissionVO getDeptPermissionOne(String deptId, Integer meetingId) {
		String grpId = ResourceGroupVO.generateId("DEPT", deptId, meetingId);
		return getMeetingPermissionOne(grpId);
	}
	public ResourcePermissionVO getUserPermissionOne(String userId, Integer meetingId) {
		String grpId = ResourceGroupVO.generateId("USER", userId, meetingId);
		return getMeetingPermissionOne(grpId);
	}
	private ResourcePermissionVO getMeetingPermissionOne(String grpId) {
		ResourceGroupVO grpVO = mapper.getMeetingGroupOne(grpId);
		if(grpVO == null) {
			return null;
		}
		return mapper.getMeetingPermissionOne(grpVO.getPermId());
	}
	@Override
	public ResourceGroupVO getResourceGroupOne(String grpId) {
		return mapper.getMeetingGroupOne(grpId);
	}
	public ResourceGroupVO getDomainGroupOne(DomainRole domain) {
		String grpId = ResourceGroupVO.generateId("DOMAIN", domain.getCode(), 0);
		return mapper.getMeetingGroupOne(grpId);
	}
	public ResourceGroupVO getRoleGroupOne(AttendRole role, Integer meetingId) {
		String grpId = ResourceGroupVO.generateId("ROLE", role.getCode(), meetingId);
		return mapper.getMeetingGroupOne(grpId);
	}
	public ResourceGroupVO getDeptGroupOne(String deptId, Integer meetingId) {
		String grpId = ResourceGroupVO.generateId("DEPT", deptId, meetingId);
		return mapper.getMeetingGroupOne(grpId);
	}
	public ResourceGroupVO getUserGroupOne(String userId, Integer meetingId) {
		String grpId = ResourceGroupVO.generateId("USER", userId, meetingId);
		return mapper.getMeetingGroupOne(grpId);
	}
	
	@Override
	public List<ResourceGroupVO> getResourceGroupList(Integer srcId) {
		if(srcId == 0) {
			return null;
		}
		return mapper.getMeetingGroupList(ResourceGroupVO.builder().srcId(srcId).build());
	}
	@Override
	public List<ResourceGroupVO> getResourceGroupList(ResourceGroupVO param){
		return mapper.getMeetingGroupList(param);
	}
	@Override
	public List<ResourceAuthorityVO> getResourceAuthorityList(){
		return mapper.getMeetingAuthorityList();
	}
}
