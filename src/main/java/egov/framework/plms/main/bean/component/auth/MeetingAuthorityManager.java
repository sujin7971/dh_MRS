package egov.framework.plms.main.bean.component.auth;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import egov.framework.plms.main.bean.mvc.entity.auth.ResourceAuthorityVO;
import egov.framework.plms.main.bean.mvc.entity.auth.ResourceGroupVO;
import egov.framework.plms.main.bean.mvc.entity.auth.ResourcePermissionVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingInfoModelVO;
import egov.framework.plms.main.bean.mvc.service.auth.MeetingAuthService;
import egov.framework.plms.main.bean.mvc.service.meeting.abst.MeetingInfoAbstractService;
import egov.framework.plms.main.core.model.auth.ResourceAuthorityCollection;
import egov.framework.plms.main.core.model.enums.AuthCode;
import egov.framework.plms.main.core.model.enums.meeting.AttendRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 회의에 대한 권한을 제어할 클래스
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 * @see {@link AbstractAuthorityManager}
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MeetingAuthorityManager extends AbstractAuthorityManager {
	private final MeetingAuthorityCollector authCollector;
	private final MeetingAuthService authServ;
	private final MeetingInfoAbstractService mtServ;
	@Override
	public boolean isBitHasAuthority(Integer stickyBit, AuthCode authCode) {
		return authCollector.hasBit(stickyBit, authCollector.getAuthorityValue(authCode));
	}
	
	@Override
	public boolean isResourceAuthorityCertificated(Integer meetingId) {
		Integer stickyBit = mtServ.getMeetingStickyBit(meetingId);
		return (stickyBit == 0)?false:true;
	}
	
	@Override
	public ResourceAuthorityCollection getResourceAuthorityCollection(Authentication authentication, Integer meetingId) {
		List<ResourceAuthorityVO> auth = authCollector.getPrivilegeAuthorityList(authentication, meetingId);
		return ResourceAuthorityCollection.builder().authorities(auth).build();
	}
	
	public ResourceAuthorityCollection getResourceAuthorityCollection(String userKey, Integer meetingId) {
		List<ResourceAuthorityVO> auth = authCollector.getPersonalAuthorityList(userKey, meetingId);
		return ResourceAuthorityCollection.builder().authorities(auth).build();
	}
	
	/**
	 * 회의가 허용하는 권한 변경
	 * @param meetingId : 권한을 수정할 회의
	 * @param authCode : 변강할 권한
	 */
	@Override
	public void changeResourceStickyBit(Integer meetingId, AuthCode authCode) {
		mtServ.putMeetingStickyBit(meetingId, authCollector.getAuthorityValue(authCode));
	}
	
	/**
	 * 회의가 허용하는 권한 변경
	 * @param meetingId : 권한을 수정할 회의
	 * @param authCodeCollection : 변경할 권한 집합
	 */
	@Override
	public void changeResourceStickyBit(Integer meetingId, Collection<AuthCode> authCodeCollection) {
		Set<Integer> bitSet = authCodeCollection.stream().map(e -> authCollector.getAuthorityValue(e)).collect(Collectors.toSet());
		mtServ.putMeetingStickyBit(meetingId, authCollector.bitWiseOR(bitSet));
	}
	
	/**
	 * 회의가 허용하는 권한 추가
	 * @param meetingId : 권한을 수정할 회의
	 * @param authCode : 추가할 권한
	 */
	@Override
	public void increaseResourceStickyBit(Integer meetingId, AuthCode authCode) {
		Integer oldStickyBit = mtServ.getMeetingStickyBit(meetingId);
		Integer newStickyBit = authCollector.bitWiseOR(oldStickyBit, authCollector.getAuthorityValue(authCode));
		mtServ.putMeetingStickyBit(meetingId, newStickyBit);
	}
	
	/**
	 * 회의가 허용하는 권한 추가
	 * @param meetingId : 권한을 수정할 회의
	 * @param authCodeCollection : 추가할 권한 집합
	 */
	@Override
	public void increaseResourceStickyBit(Integer meetingId, Collection<AuthCode> authCodeCollection) {
		Set<Integer> bitSet = authCodeCollection.stream().map(e -> authCollector.getAuthorityValue(e)).collect(Collectors.toSet());
		Integer oldStickyBit = mtServ.getMeetingStickyBit(meetingId);
		Integer newStickyBit = authCollector.bitWiseOR(oldStickyBit, authCollector.bitWiseOR(bitSet));
		mtServ.putMeetingStickyBit(meetingId, newStickyBit);
	}
	
	/**
	 * 회의가 허용하는 권한 삭제
	 * @param meetingId : 권한을 수정할 회의
	 * @param authCode : 삭제할 권한
	 */
	@Override
	public void reduceResourceStickyBit(Integer meetingId, AuthCode authCode) {
		Integer oldStickyBit = mtServ.getMeetingStickyBit(meetingId);
		Integer newStickyBit = authCollector.bitWiseMINUS(oldStickyBit, authCollector.getAuthorityValue(authCode));
		mtServ.putMeetingStickyBit(meetingId, newStickyBit);
	}
	
	/**
	 * 회의가 허용하는 권한 삭제
	 * @param meetingId : 권한을 수정할 회의
	 * @param authCodeCollection : 삭제할 권한 집합
	 */
	@Override
	public void reduceResourceStickyBit(Integer meetingId, Collection<AuthCode> authCodeCollection) {
		Set<Integer> bitSet = authCodeCollection.stream().map(e -> authCollector.getAuthorityValue(e)).collect(Collectors.toSet());
		Integer oldStickyBit = mtServ.getMeetingStickyBit(meetingId);
		Integer newStickyBit = authCollector.bitWiseMINUS(oldStickyBit, authCollector.bitWiseOR(bitSet));
		mtServ.putMeetingStickyBit(meetingId, newStickyBit);
	}
	
	@Override
	public void providePermissionForAttendee(@NonNull AttendRole role, @NonNull Integer meetingId, @NonNull AuthCode authCode) {
		try {
			log.info("참석자({})의 회의({})에 대한 권한 등록 - {}", role, meetingId, authCode);
			authServ.postRoleGroup(role, meetingId, authCollector.getAuthorityValue(authCode));
		}catch(Exception e) {
			log.error("참석자 {}의 회의 {}에 대한 권한이 이미 등록되어있습니다", role, meetingId);
			changePermissionForAttendee(role, meetingId, authCode);
		}
	}

	@Override
	public void providePermissionForAttendee(AttendRole role, Integer meetingId, Collection<AuthCode> authCodeCollection) {
		
		try {
			log.info("참석자({})의 회의({})에 대한 권한 등록 - {}", role, meetingId, authCodeCollection);
			authServ.postRoleGroup(role, meetingId, authCollector.getAuthorityValue(authCodeCollection));
		}catch(Exception e) {
			log.error("참석자 {}의 회의 {}에 대한 권한이 이미 등록되어있습니다", role, meetingId);
			changePermissionForAttendee(role, meetingId, authCodeCollection);
		}
	}

	@Override
	public void providePermissionForDept(String deptId, Integer meetingId, AuthCode authCode) {
		try {
			log.info("부서({})의 회의({})에 대한 권한 등록 - {}", deptId, meetingId, authCode);
			authServ.postDeptGroup(deptId, meetingId, authCollector.getAuthorityValue(authCode));
		}catch(Exception e) {
			log.error("부서 {}의 회의 {}에 대한 권한이 이미 등록되어있습니다", deptId, meetingId);
			changePermissionForDept(deptId, meetingId, authCode);
		}
	}

	@Override
	public void providePermissionForDept(String deptId, Integer meetingId, Collection<AuthCode> authCodeCollection) {
		try {
			log.info("부서({})의 회의({})에 대한 권한 등록 - {}", deptId, meetingId, authCodeCollection);
			authServ.postDeptGroup(deptId, meetingId, authCollector.getAuthorityValue(authCodeCollection));
		}catch(Exception e) {
			log.error("부서 {}의 회의 {}에 대한 권한이 이미 등록되어있습니다", deptId, meetingId);
			changePermissionForDept(deptId, meetingId, authCodeCollection);
		}
	}

	@Override
	public void providePermissionForUser(String userId, Integer meetingId, AuthCode authCode) {
		try {
			log.info("사용자({})의 회의({})에 대한 권한 등록 - {}", userId, meetingId, authCode);
			authServ.postUserGroup(userId, meetingId, authCollector.getAuthorityValue(authCode));
		}catch(Exception e) {
			log.error("사용자 {}의 회의 {}에 대한 권한이 이미 등록되어있습니다", userId, meetingId);
			changePermissionForUser(userId, meetingId, authCode);
		}
	}

	@Override
	public void providePermissionForUser(String userId, Integer meetingId, Collection<AuthCode> authCodeCollection) {
		try {
			log.info("사용자({})의 회의({})에 대한 권한 등록 - {}", userId, meetingId, authCodeCollection);
			authServ.postUserGroup(userId, meetingId, authCollector.getAuthorityValue(authCodeCollection));
		}catch(Exception e) {
			log.error("사용자 {}의 회의 {}에 대한 권한이 이미 등록되어있습니다", userId, meetingId);
			changePermissionForUser(userId, meetingId, authCodeCollection);
		}
	}
	
	@Override
	public void removeResourceAllGroup(Integer srcId) {
		List<ResourceGroupVO> grpList = authServ.getResourceGroupList(srcId);
		if(grpList == null || grpList.isEmpty()) {
			return;
		}
		grpList.stream().forEach(grp -> authServ.deleteResourceGroup(grp.getGrpId()));
	}

	@Override
	public void removePermissionForAttendee(AttendRole role, Integer meetingId) {
		authServ.deleteRoleGroup(role, meetingId);
	}

	@Override
	public void removePermissionForDept(String deptId, Integer meetingId) {
		authServ.deleteDeptGroup(deptId, meetingId);
	}

	@Override
	public void removePermissionForUser(String userId, Integer meetingId) {
		authServ.deleteUserGroup(userId, meetingId);
	}

	@Override
	public void changePermissionForAttendee(AttendRole role, Integer meetingId, AuthCode authCode) {
		ResourcePermissionVO permVO = authServ.getRolePermissionOne(role, meetingId);
		if(permVO == null) {
			providePermissionForAttendee(role, meetingId, authCode);
		}else {
			log.info("참석자({})의 회의({})에 대한 권한 변경 - {}", role, meetingId, authCode);
			authServ.putResourcePermissionLvl(permVO.getPermId(), authCollector.getAuthorityValue(authCode));
		}
	}

	@Override
	public void changePermissionForAttendee(AttendRole role, Integer meetingId, Collection<AuthCode> authCodeCollection) {
		ResourcePermissionVO permVO = authServ.getRolePermissionOne(role, meetingId);
		if(permVO == null) {
			providePermissionForAttendee(role, meetingId, authCodeCollection);
		}else {
			log.info("참석자({})의 회의({})에 대한 권한 변경 - {}", role, meetingId, authCodeCollection);
			authServ.putResourcePermissionLvl(permVO.getPermId(), authCollector.getAuthorityValue(authCodeCollection));
		}
	}

	@Override
	public void changePermissionForDept(String deptId, Integer meetingId, AuthCode authCode) {
		ResourcePermissionVO permVO = authServ.getDeptPermissionOne(deptId, meetingId);
		if(permVO == null) {
			providePermissionForDept(deptId, meetingId, authCode);
		}else {
			log.info("부서({})의 회의({})에 대한 권한 변경 - {}", deptId, meetingId, authCode);
			authServ.putResourcePermissionLvl(permVO.getPermId(), authCollector.getAuthorityValue(authCode));
		}
	}

	@Override
	public void changePermissionForDept(String deptId, Integer meetingId, Collection<AuthCode> authCodeCollection) {
		ResourcePermissionVO permVO = authServ.getDeptPermissionOne(deptId, meetingId);
		if(permVO == null) {
			providePermissionForDept(deptId, meetingId, authCodeCollection);
		}else {
			log.info("부서({})의 회의({})에 대한 권한 변경 - {}", deptId, meetingId, authCodeCollection);
			authServ.putResourcePermissionLvl(permVO.getPermId(), authCollector.getAuthorityValue(authCodeCollection));
		}
	}

	@Override
	public void changePermissionForUser(String userId, Integer meetingId, AuthCode authCode) {
		ResourcePermissionVO permVO = authServ.getUserPermissionOne(userId, meetingId);
		if(permVO == null) {
			providePermissionForUser(userId, meetingId, authCode);
		}else {
			log.info("사용자({})의 회의({})에 대한 권한 변경 - {}", userId, meetingId, authCode);
			authServ.putResourcePermissionLvl(permVO.getPermId(), authCollector.getAuthorityValue(authCode));
		}
	}

	@Override
	public void changePermissionForUser(String userId, Integer meetingId, Collection<AuthCode> authCodeCollection) {
		ResourcePermissionVO permVO = authServ.getUserPermissionOne(userId, meetingId);
		if(permVO == null) {
			providePermissionForUser(userId, meetingId, authCodeCollection);
		}else {
			log.info("사용자({})의 회의({})에 대한 권한 변경 - {}", userId, meetingId, authCodeCollection.toString());
			authServ.putResourcePermissionLvl(permVO.getPermId(), authCollector.getAuthorityValue(authCodeCollection));
		}
	}
}
