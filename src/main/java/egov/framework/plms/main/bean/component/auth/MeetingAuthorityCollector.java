package egov.framework.plms.main.bean.component.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import egov.framework.plms.main.bean.mvc.entity.auth.ResourceAuthorityVO;
import egov.framework.plms.main.bean.mvc.entity.auth.ResourceGroupVO;
import egov.framework.plms.main.bean.mvc.entity.auth.ResourcePermissionVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingAttendeeModelVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingInfoModelVO;
import egov.framework.plms.main.bean.mvc.service.auth.MeetingAuthService;
import egov.framework.plms.main.bean.mvc.service.meeting.abst.MeetingAttendeeAbstractService;
import egov.framework.plms.main.bean.mvc.service.meeting.abst.MeetingInfoAbstractService;
import egov.framework.plms.main.core.model.enums.AuthCode;
import egov.framework.plms.main.core.model.enums.meeting.AttendRole;
import egov.framework.plms.main.core.model.enums.user.DomainRole;
import egov.framework.plms.main.core.model.login.AuthenticationDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 회의에 대한 권한을 수집하여 제공하는 클래스
 * 
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 * @see {@link AbstractAuthorityCollector}
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MeetingAuthorityCollector extends AbstractAuthorityCollector {
	private final MeetingAuthService authServ;

	private final MeetingAttendeeAbstractService attServ;
	private final MeetingInfoAbstractService mtServ;

	private List<ResourceAuthorityVO> meetingAuthorityList;
	private Map<String, Integer> meetingAuthorityMap;

	@PostConstruct
	private void init() {
		meetingAuthorityMap = new HashMap<>();
		meetingAuthorityList = authServ.getResourceAuthorityList();
		for (ResourceAuthorityVO authVO : meetingAuthorityList) {
			meetingAuthorityMap.put(authVO.getAuthCode(), authVO.getAuthVal());
		}
	}

	@Override
	public Integer getAuthorityValue(AuthCode authCode) {
		return meetingAuthorityMap.get(authCode.getCode());
	}

	@Override
	public Integer getAuthorityValue(Collection<AuthCode> authCodeCollection) {
		Set<String> authCodeSet = authCodeCollection.stream().map(e -> e.getCode()).collect(Collectors.toSet());
		Set<Integer> authValueSet = authCodeSet.stream().map(meetingAuthorityMap::get).collect(Collectors.toSet());

		return bitWiseOR(authValueSet);
	}

	/**
	 * 사용자의 회의에 대한 모든 권한 리스트 반환
	 * 
	 * @param authentication
	 * @param meetingId
	 * @return
	 */
	@Override
	public List<ResourceAuthorityVO> getPrivilegeAuthorityList(Authentication authentication, Integer meetingId) {
		List<ResourceAuthorityVO> privilegeAuthorityList = new ArrayList<>();

		List<ResourceAuthorityVO> generalAuthorityList = getGeneralAuthorityList(authentication, meetingId);
		privilegeAuthorityList.addAll(generalAuthorityList);
		List<ResourceAuthorityVO> domainAuthorityList = getDomainAuthorityList(authentication);
		privilegeAuthorityList.addAll(domainAuthorityList);
		return privilegeAuthorityList;
	}

	@Override
	public List<ResourceAuthorityVO> getGeneralAuthorityList(Authentication authentication, Integer meetingId) {
		Set<Integer> permLvlSet = new HashSet<>();
		Integer deptPermLvl = getDeptAuthorityPermissionLvl(authentication, meetingId);
		permLvlSet.add(deptPermLvl);

		AuthenticationDetails userDetails = (AuthenticationDetails) authentication.getDetails();
		String userId = userDetails.getUserId();

		Integer rolePermLvl = getRoleAuthorityPermissionLvl(userId, meetingId);
		permLvlSet.add(rolePermLvl);
		Integer userPermLvl = getUserAuthorityPermissionLvl(userId, meetingId);
		permLvlSet.add(userPermLvl);

		Integer totalPermLvl = bitWiseOR(permLvlSet);
		Integer filteredPermLvl = getFilteredPermissionLvl(meetingId, totalPermLvl);

		return meetingAuthorityList.stream().filter(auth -> hasBit(filteredPermLvl, auth.getAuthVal()))
				.collect(Collectors.toList());
	}

	@Override
	public List<ResourceAuthorityVO> getPersonalAuthorityList(String userId, Integer meetingId) {
		Set<Integer> permLvlSet = new HashSet<>();

		Integer rolePermLvl = getRoleAuthorityPermissionLvl(userId, meetingId);
		permLvlSet.add(rolePermLvl);
		Integer userPermLvl = getUserAuthorityPermissionLvl(userId, meetingId);
		permLvlSet.add(userPermLvl);

		Integer totalPermLvl = bitWiseOR(permLvlSet);
		Integer filteredPermLvl = getFilteredPermissionLvl(meetingId, totalPermLvl);

		return meetingAuthorityList.stream().filter(auth -> hasBit(filteredPermLvl, auth.getAuthVal()))
				.collect(Collectors.toList());
	}

	/**
	 * 사용자 역할에 따라 기본으로 부여되는 권한 조회. 권한 필터링을 거치지 않음.
	 * 
	 * @param authentication 사용제에게 부여된 역할 권한 조회를 위한 인증 객체
	 * @return 권한 객체 모음
	 */
	private List<ResourceAuthorityVO> getDomainAuthorityList(Authentication authentication) {
		Integer authBit = getDomainAuthorityPermissionLvl(authentication);
		List<ResourceAuthorityVO> domainAuthList = meetingAuthorityList.stream()
				.filter(auth -> hasBit(authBit, auth.getAuthVal())).collect(Collectors.toList());
		log.info("사용자 기본 권한: {}",
				domainAuthList.stream().map(vo -> vo.getAuthCode()).collect(Collectors.toSet()).toString());
		return domainAuthList;
	}

	/**
	 * 사용자 역할에 따라 기본으로 부여되는 권한 조회. 권한 필터링을 거치지 않음.
	 * 
	 * @param authentication 사용제에게 부여된 역할 권한 조회를 위한 인증 객체
	 * @return 권한 비트값
	 */
	private Integer getDomainAuthorityPermissionLvl(Authentication authentication) {
		Set<DomainRole> userRoleSet = getAllDomainRole(authentication);
		List<ResourceGroupVO> grpList = userRoleSet.stream().map(userRole -> authServ.getDomainGroupOne(userRole))
				.collect(Collectors.toList());
		if (grpList.isEmpty()) {
			return 0;
		}
		Set<Integer> permLvlSet = grpList.stream().map(grp -> getGroupPermissionLvl(grp)).collect(Collectors.toSet());
		return bitWiseOR(permLvlSet);
	}

	private List<ResourceAuthorityVO> getPositionAuthorityList(Authentication authentication) {
		Integer authBit = getPositionAuthorityPermissionLvl(authentication);
		List<ResourceAuthorityVO> domainAuthList = meetingAuthorityList.stream()
				.filter(auth -> hasBit(authBit, auth.getAuthVal())).collect(Collectors.toList());
		log.info("사용자 기본 권한: {}",
				domainAuthList.stream().map(vo -> vo.getAuthCode()).collect(Collectors.toSet()).toString());
		return domainAuthList;
	}

	private Integer getPositionAuthorityPermissionLvl(Authentication authentication) {
		Set<DomainRole> userRoleSet = getAllPositionRole(authentication);
		List<ResourceGroupVO> grpList = userRoleSet.stream().map(userRole -> authServ.getDomainGroupOne(userRole))
				.collect(Collectors.toList());
		if (grpList.isEmpty()) {
			return 0;
		}
		Set<Integer> permLvlSet = grpList.stream().map(grp -> getGroupPermissionLvl(grp)).collect(Collectors.toSet());
		return bitWiseOR(permLvlSet);
	}

	/**
	 * 사용자 부서가 회의에 가진 권한 조회
	 * 
	 * @param authentication 사용자가 속한 부서를 조회할 인증 객체
	 * @param meetingId      회의 고유키
	 * @return 권한 객체 모음
	 */
	public List<ResourceAuthorityVO> getDeptAuthorityList(Authentication authentication, Integer meetingId) {
		Integer permLvl;
		if (meetingId == null) {
			permLvl = 0;
		} else {
			permLvl = getDeptAuthorityPermissionLvl(authentication, meetingId);
		}
		return meetingAuthorityList.stream().filter(auth -> hasBit(permLvl, auth.getAuthVal()))
				.collect(Collectors.toList());
	}

	/**
	 * 사용자 부서가 회의에 가진 권한 조회
	 * 
	 * @param authentication 사용자가 속한 부서를 조회할 인증 객체
	 * @param meetingId      회의 고유키
	 * @return 권한 비트값
	 */
	private Integer getDeptAuthorityPermissionLvl(Authentication authentication, Integer meetingId) {
		AuthenticationDetails userDetails = (AuthenticationDetails) authentication.getDetails();
		String deptId = userDetails.getDeptId();
		ResourceGroupVO grpVO = authServ.getDeptGroupOne(deptId, meetingId);
		Integer permLvl = getGroupPermissionLvl(grpVO);
		log.info("부서({})의 회의({})에 대한 권한: {}", deptId, meetingId, permLvl);
		return permLvl;
	}

	/**
	 * 회의에 대한 참석자 역할에 따라 부여된 권한 조회
	 * 
	 * @param userId    사용자 로그인 ID
	 * @param meetingId 회의 고유키
	 * @return 권한 객체모음
	 */
	public List<ResourceAuthorityVO> getRoleAuthorityList(String userId, Integer meetingId) {
		Integer permLvl;
		if (meetingId == null) {
			permLvl = 0;
		} else {
			permLvl = getRoleAuthorityPermissionLvl(userId, meetingId);
		}
		return meetingAuthorityList.stream().filter(auth -> hasBit(permLvl, auth.getAuthVal()))
				.collect(Collectors.toList());
	}

	/**
	 * 회의에 대한 참석자 역할에 따라 부여된 권한 조회
	 * 
	 * @param userId    사용자 로그인 ID
	 * @param meetingId 회의 고유키
	 * @return 권한 비트값
	 */
	private Integer getRoleAuthorityPermissionLvl(String userId, Integer meetingId) {
		Optional<MeetingInfoModelVO> meetingOpt = mtServ.getMeetingInfoOne(meetingId);
		Optional<MeetingAttendeeModelVO> attOpt = attServ.getMeetingAttendeeOne(userId, meetingId);
		Integer totalLvl = 0;
		if (attOpt.isPresent()) {
			MeetingAttendeeModelVO attVO = attOpt.get();
			if (attVO.getAssistantYN() == 'Y') {
				totalLvl = getGroupPermissionLvl(authServ.getRoleGroupOne(AttendRole.ASSISTANT, meetingId));
				log.info("역할({})의 회의({})에 대한 권한: {}", AttendRole.ASSISTANT, meetingId, totalLvl);
			} else {
				totalLvl = getGroupPermissionLvl(authServ.getRoleGroupOne(attVO.getAttendRole(), meetingId));
				log.info("역할({})의 회의({})에 대한 권한: {}", attVO.getAttendRole(), meetingId, totalLvl);
			}
		}
		if (meetingOpt.isPresent()) {
			MeetingInfoModelVO meetingVO = meetingOpt.get();
			String writerId = meetingVO.getWriterId();
			if (userId.equals(writerId)) {
				Integer permLvl = getGroupPermissionLvl(authServ.getRoleGroupOne(AttendRole.WRITER, meetingId));
				log.info("역할({})의 회의({})에 대한 권한: {}", AttendRole.WRITER, meetingId, permLvl);
				totalLvl = bitWiseOR(totalLvl, permLvl);
			}
		}
		return totalLvl;
	}

	/**
	 * 사용자 회의에 대해 직접 보유한 권한 조회
	 * 
	 * @param userId    사용자 로그인 ID
	 * @param meetingId 회의 고유키
	 * @return 권한 객체모음
	 */
	public List<ResourceAuthorityVO> getUserAuthorityList(String userId, Integer meetingId) {
		Integer permLvl;
		if (meetingId == null) {
			permLvl = 0;
		} else {
			permLvl = getUserAuthorityPermissionLvl(userId, meetingId);
		}
		return meetingAuthorityList.stream().filter(auth -> hasBit(permLvl, auth.getAuthVal()))
				.collect(Collectors.toList());
	}

	/**
	 * 사용자 회의에 대해 직접 보유한 권한 조회
	 * 
	 * @param userId    사용자 로그인 ID
	 * @param meetingId 회의 고유키
	 * @return 권한 비트값
	 */
	private Integer getUserAuthorityPermissionLvl(String userId, Integer meetingId) {
		ResourceGroupVO grpVO = authServ.getUserGroupOne(userId.toString(), meetingId);
		Integer permLvl = getGroupPermissionLvl(grpVO);
		log.info("유저({})의 회의({})에 대한 권한: {}", userId, meetingId, permLvl);
		return permLvl;
	}

	/**
	 * 해당 그룹이 보유한 권한의 비트값 조회
	 * 
	 * @param grpVO 그룹 객체
	 * @return 권한 비트값
	 */
	private Integer getGroupPermissionLvl(ResourceGroupVO grpVO) {
		if (grpVO == null) {
			return 0;
		}
		ResourcePermissionVO permVO = authServ.getResourcePermissionOne(grpVO.getPermId());
		return permVO.getPermLvl();
	}

	/**
	 * 권한 비트값을 대상 회의가 허용하는 권한 범위에 맞춰 조정하여 반환
	 * 
	 * @param meetingId 회의 고유키
	 * @param permLvl   권한 모음 비트값
	 * @return 허용 범위내로 조정된 권한 모음 비트값
	 */
	private Integer getFilteredPermissionLvl(Integer meetingId, Integer permLvl) {
		Integer stickyBit = mtServ.getMeetingStickyBit(meetingId);
		Integer filteredLvl = bitWiseAND(permLvl, stickyBit);
		log.info("회의({})- 허용 권한: {} AND 나의 권한: {} = 최종 권한: {}", meetingId, stickyBit, permLvl, filteredLvl);
		return filteredLvl;
	}
}
