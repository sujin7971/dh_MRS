package egov.framework.plms.main.bean.mvc.mapper.auth;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import egov.framework.plms.main.bean.mvc.entity.auth.ResourceAuthorityVO;
import egov.framework.plms.main.bean.mvc.entity.auth.ResourceGroupVO;
import egov.framework.plms.main.bean.mvc.entity.auth.ResourcePermissionVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingInfoVO;

@Mapper
public interface AuthMapper {
	Integer postMeetingPermission(ResourcePermissionVO param);
	Integer postMeetingGroup(ResourceGroupVO param);
	
	@CacheEvict(value = "perm", key = "#param.permId", condition="#param.permId!=null")
	Integer putMeetingPermissionLvl(ResourcePermissionVO param);
	
	@CacheEvict(value = "perm", key = "#permId", condition="#permId!=null")
	Integer deleteMeetingPermission(Integer permId);
	@CacheEvict(value = "grp", key = "#grpId", condition="#grpId!=null")
	Integer deleteMeetingGroup(String grpId);
	
	@Cacheable(value = "meetingAuthorityList")
	List<ResourceAuthorityVO> getMeetingAuthorityList();
	@Cacheable(value = "fileAuthorityList")
	List<ResourceAuthorityVO> getFileAuthorityList();
	
	//@Cacheable(value = "perm", key = "#permId", unless="#result == null", condition="#permId!=null")
	ResourcePermissionVO getMeetingPermissionOne(Integer permId);
	//@Cacheable(value = "grp", key = "#grpId", unless="#result == null", condition="#grpId!=null")
	ResourceGroupVO getMeetingGroupOne(String grpId);
	List<ResourceGroupVO> getMeetingGroupList(ResourceGroupVO param);
} 
