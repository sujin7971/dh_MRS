package egov.framework.plms.main.bean.component.auth;

import java.util.Collection;

import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;

import egov.framework.plms.main.core.model.auth.ResourceAuthorityCollection;
import egov.framework.plms.main.core.model.enums.AuthCode;
import egov.framework.plms.main.core.model.enums.meeting.AttendRole;

/**
 * 자원이 허용할 권한을 수정하고 그룹(사용자/역할 등)의 해당 자원에 대한 권한을 제공/수정/박탈 한다<br>
 * 
 * @author mckim
 */
public abstract class AbstractAuthorityManager {
	/**
	 * 해당 비트값이 전달한 권한을 가지고 있는지 여부를 판별한다
	 * @param stickyBit 권한 모음 비트값
	 * @param authCode 조회할 권한
	 * @return 권한을 가지고 있다면 <code>true<code>, 그렇지 않다면 <code>false<code>
	 */
	abstract public boolean isBitHasAuthority(Integer stickyBit, AuthCode authCode);
	/**
	 * 해당 자원의 권한 부여 처리가 완료되었는지 판별한다
	 * @param srcId 자원 고유키
	 * @return 권한 부여 처리가 되었다면 <code>true<code>, 그렇지 않다면 <code>false<code>
	 */
	abstract public boolean isResourceAuthorityCertificated(Integer srcId);
	/**
	 * 사용자가 해당 자원에 대해 소유한 모든 권한 조회
	 * @param authentication 사용자 인증객체
	 * @param srcId 자원 고유키
	 * @return 권한 집합을 소유한 ResourceAuthorityCollection 객체
	 */
	abstract public ResourceAuthorityCollection getResourceAuthorityCollection(Authentication authentication, Integer srcId);
	
	/**
	 * 자원이 허용할 권한을 변경한다
	 * @param srcId 자원 고유키
	 * @param authCode 허용할 권한
	 */
	abstract public void changeResourceStickyBit(Integer srcId, AuthCode authCode);
	/**
	 * 자원이 허용할 권한을 변경한다
	 * @param srcId 자원 고유키
	 * @param authCodeCollection 허용할 권한 모음
	 */
	abstract public void changeResourceStickyBit(Integer srcId, Collection<AuthCode> authCodeCollection);
	/**
	 * 자원이 해당 권한을 허용한다
	 * @param srcId 자원 고유키
	 * @param authCode 허용할 권한
	 */
	abstract public void increaseResourceStickyBit(Integer srcId, AuthCode authCode);
	/**
	 * 자원이 해당 권한 모음을 허용한다
	 * @param srcId 자원 고유키
	 * @param authCodeCollection 허용할 권한 모음
	 */
	abstract public void increaseResourceStickyBit(Integer srcId, Collection<AuthCode> authCodeCollection);
	/**
	 * 자원이 해당 권한을 불허한다
	 * @param srcId 자원 고유키
	 * @param authCode 불허할 권한
	 */
	abstract public void reduceResourceStickyBit(Integer srcId, AuthCode authCode);
	/**
	 * 자원이 해당 권한 모음을 불허한다
	 * @param srcId 자원 고유키
	 * @param authCodeCollection 불허할 권한 모음
	 */
	abstract public void reduceResourceStickyBit(Integer srcId, Collection<AuthCode> authCodeCollection);
	/**
	 * 참석자에게 해당 자원에 대한 권한을 제공한다
	 * @param role 자원에 대한 사용자의 역할
	 * @param srcId 자원 고유키
	 * @param authCode 제공할 권한
	 * 
	 * @see {@link AttendRole}
	 */
	abstract public void providePermissionForAttendee(@NonNull AttendRole role, @NonNull Integer srcId, @NonNull AuthCode authCode);
	/**
	 * 참석자에게 해당 자원에 대한 권한 모음을 제공한다
	 * @param role 자원에 대한 사용자의 역할
	 * @param srcId 자원 고유키
	 * @param authCodeCollection 제공할 권한 모음
	 * 
	 * @see {@link AttendRole}
	 */
	abstract public void providePermissionForAttendee(@NonNull AttendRole role, @NonNull Integer srcId, @NonNull Collection<AuthCode> authCodeCollection);
	/**
	 * 부서에게 해당 자원에 대한 권한을 제공한다
	 * @param deptId 부서 코드
	 * @param srcId 자원 고유키
	 * @param authCode 제공할 권한
	 */
	abstract public void providePermissionForDept(@NonNull String deptId, @NonNull Integer srcId, @NonNull AuthCode authCode);
	/**
	 * 부서에게 해당 자원에 대한 권한 모음을 제공한다
	 * @param deptId 부서 코드
	 * @param srcId 자원 고유키
	 * @param authCode 제공할 권한 모음
	 */
	abstract public void providePermissionForDept(@NonNull String deptId, @NonNull Integer srcId, @NonNull Collection<AuthCode> authCodeCollection);
	/**
	 * 사용자에게 해당 자원에 대한 권한을 제공한다
	 * @param userId 사용자 로그인ID
	 * @param srcId 자원 고유키
	 * @param authCode 제공할 권한
	 */
	abstract public void providePermissionForUser(@NonNull String userId, @NonNull Integer srcId, @NonNull AuthCode authCode);
	/**
	 * 사용자에게 해당 자원에 대한 권한 모음을 제공한다
	 * @param userId 사용자 로그인ID
	 * @param srcId 자원 고유키
	 * @param authCodeCollection 제공할 권한 모음
	 */
	abstract public void providePermissionForUser(@NonNull String userId, @NonNull Integer srcId, @NonNull Collection<AuthCode> authCodeCollection);
	/**
	 * 참석자의 해당 자원에 대한 권한을 박탇한다
	 * @param role 자원에 대한 사용자의 역할
	 * @param srcId 자원 고유키
	 */
	abstract public void removePermissionForAttendee(@NonNull AttendRole role, @NonNull Integer srcId);
	/**
	 * 부서의 해당 자원에 대한 권한을 박탈한다
	 * @param deptId 부서 코드
	 * @param srcId 자원 고유키
	 */
	abstract public void removePermissionForDept(@NonNull String deptId, @NonNull Integer srcId);
	/**
	 * 사용자의 해당 자원에 대한 권한을 박탈한다
	 * @param userId 사용자 로그인ID
	 * @param srcId 자원 고유키
	 */
	abstract public void removePermissionForUser(@NonNull String userId, @NonNull Integer srcId);
	/**
	 * 해당 자원이 제공한 모든 권한을 박탈한다
	 * @param srcId 자원 고유키
	 */
	abstract public void removeResourceAllGroup(@NonNull Integer srcId);
	/**
	 * 참석자의 해당 자원에 대한 권한을 변경한다
	 * @param role 자원에 대한 사용자의 역할
	 * @param srcId 자원 고유키
	 * @param authCode 변경될 권한
	 * 
	 * @see {@link AttendRole}
	 */
	abstract public void changePermissionForAttendee(@NonNull AttendRole role, @NonNull Integer srcId, @NonNull AuthCode authCode);
	/**
	 * 참석자의 해당 자원에 대한 권한을 변경한다
	 * @param role 자원에 대한 사용자의 역할
	 * @param srcId 자원 고유키
	 * @param authCodeCollection 변경될 권한 모음
	 * 
	 * @see {@link AttendRole}
	 */
	abstract public void changePermissionForAttendee(@NonNull AttendRole role, @NonNull Integer srcId, @NonNull Collection<AuthCode> authCodeCollection);
	/**
	 * 부서의 해당 자원에 대한 권한을 변경한다
	 * @param deptId 부서 고유키
	 * @param srcId 자원 고유키
	 * @param authCode 변경될 권한
	 */
	abstract public void changePermissionForDept(@NonNull String deptId, @NonNull Integer srcId, @NonNull AuthCode authCode);
	/**
	 * 부서의 해당 자원에 대한 권한을 변경한다
	 * @param deptId 부서 고유키
	 * @param srcId 자원 고유키
	 * @param authCodeCollection 변경될 권한 모음
	 */
	abstract public void changePermissionForDept(@NonNull String deptId, @NonNull Integer srcId, @NonNull Collection<AuthCode> authCodeCollection);
	/**
	 * 사용자의 해당 자원에 대한 권한을 변경한다
	 * @param userId 사용자 로그인 ID
	 * @param srcId 자원 고유키
	 * @param authCode 변경될 권한
	 */
	abstract public void changePermissionForUser(@NonNull String userId, @NonNull Integer srcId, @NonNull AuthCode authCode);
	/**
	 * 사용자의 해당 자원에 대한 권한을 변경한다
	 * @param userId 사용자 로그인 ID
	 * @param srcId 자원 고유키
	 * @param authCodeCollection 변경될 권한 모음
	 */
	abstract public void changePermissionForUser(@NonNull String userId, @NonNull Integer srcId, @NonNull Collection<AuthCode> authCodeCollection);
}
