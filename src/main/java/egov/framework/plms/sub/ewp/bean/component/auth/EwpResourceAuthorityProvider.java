package egov.framework.plms.sub.ewp.bean.component.auth;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import egov.framework.plms.main.bean.component.auth.MeetingAuthorityManager;
import egov.framework.plms.main.bean.component.auth.ResourceAuthorityProvider;
import egov.framework.plms.main.bean.mvc.entity.admin.AdminRosterVO;
import egov.framework.plms.main.bean.mvc.entity.auth.ResourceAuthorityVO;
import egov.framework.plms.main.core.model.auth.ResourceAuthorityCollection;
import egov.framework.plms.main.core.model.enums.AuthCode;
import egov.framework.plms.main.core.model.enums.auth.AssignApprovalAuth;
import egov.framework.plms.main.core.model.enums.auth.FileAuth;
import egov.framework.plms.main.core.model.enums.auth.MeetingAuth;
import egov.framework.plms.main.core.model.enums.auth.ReportAuth;
import egov.framework.plms.main.core.model.enums.meeting.ReportStatus;
import egov.framework.plms.main.core.model.enums.user.DomainRole;
import egov.framework.plms.main.core.model.enums.user.ManagerRole;
import egov.framework.plms.main.core.model.login.AuthenticationDetails;
import egov.framework.plms.sub.ewp.bean.mvc.entity.file.MeetingFileInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingAssignVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingAttendeeVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingReportVO;
import egov.framework.plms.sub.ewp.bean.mvc.service.admin.EwpAdminRosterService;
import egov.framework.plms.sub.ewp.bean.mvc.service.file.EwpMeetingFileInfoService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingAssignService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingAttendeeService;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingReportService;
import egov.framework.plms.sub.ewp.core.model.login.EwpAuthenticationDetails;
import egov.framework.plms.sub.ewp.core.util.EwpSecurityUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 요청 자원에 대한 사용자의 권한 조회 후 제공
 * 
 * @author mckim
 *
 */
@Slf4j
@Component("EwpResourceAuthorityProvider")
@Profile("ewp")
@Primary
public class EwpResourceAuthorityProvider extends ResourceAuthorityProvider {
	@Autowired
	private MeetingAuthorityManager meetingAuthorityMng;
	@Autowired
	private EwpMeetingAssignService assignServ;
	@Autowired
	private EwpAdminRosterService adminServ;
	@Autowired
	private EwpMeetingFileInfoService fileServ;
	@Autowired
	private EwpMeetingReportService rpServ;
	@Autowired
	private EwpMeetingAttendeeService attServ;
	
	/**
	 * 사용신청에 대한 사용자의 결재 권한 제공. 작성자 및 사업소 승인 담당자로서의 권한을 모두 조회하여 제공한다.
	 * 
	 * @param authentication 사용자 로그인 ID와 부여된 {@link DomainRole} 조회
	 * @param skdKey 사용신청 스케줄키
	 * @return 권한 {@link ResourceAuthorityVO}를 담은 권한모음객체
	 */
	public ResourceAuthorityCollection getAssignApprovalAuthorityCollection(Integer skdKey) {
		EwpAuthenticationDetails userDetails = EwpSecurityUtil.getAuthenticationDetails();
		String loginKey = userDetails.getUserId();
		Set<AuthCode> functionSet = new HashSet<>();
		Optional<EwpMeetingAssignVO> assignOpt = assignServ.getMeetingAssignOneByScheduleId(skdKey);
		if(!assignOpt.isPresent()) {
			return ResourceAuthorityCollection.generateCollection(functionSet);
		}
		EwpMeetingAssignVO assignVO = assignOpt.get();
		log.info("사용신청 마리아DB 조회 결과: {}", assignVO);
		Set<AuthCode> bannedCollection = new HashSet<>();
		log.info("승인자 권한: {}", userDetails.getAuthorities().toString());
		String assingOfficeCode = assignVO.getOfficeCode();
		List<AdminRosterVO> managerList = adminServ.getManagerAdminList(loginKey, assingOfficeCode, ManagerRole.getRoomManagerRole(assignVO.getRoomType()));
		boolean inCharge = (managerList != null && managerList.size() != 0)?true:false;
		log.info("사용신청 사업소: {}, 사용신청 상태: {}", assingOfficeCode, assignVO.getAppStatus());
		log.info("요청 사용자: {}, , 담당 여부: {}", loginKey, inCharge);
		if(userDetails.hasRole(DomainRole.DEV)) {
			functionSet.add(AssignApprovalAuth.DELETE);
			switch(assignVO.getAppStatus()) {
				case REQUEST:
					functionSet.add(AssignApprovalAuth.APPROVAL);
					functionSet.add(AssignApprovalAuth.REJECT);
					break;
				case APPROVED:
					LocalDateTime nowDT = LocalDateTime.now();
					if(nowDT.isBefore(assignVO.getFinishDateTime())) {
						functionSet.add(AssignApprovalAuth.REJECT);
					}
					break;
				case REJECTED:
				case CANCELED:
					break;
				default:
					break;
			}
			functionSet.add(AssignApprovalAuth.VIEW);
		}
		if(userDetails.hasPosition(ManagerRole.APPROVAL_MANAGER)) {// 결재 담당자 권한
			switch(assignVO.getAppStatus()) {
				case REQUEST:
					functionSet.add(AssignApprovalAuth.APPROVAL);
					functionSet.add(AssignApprovalAuth.REJECT);
					break;
				case APPROVED:
					LocalDateTime nowDT = LocalDateTime.now();
					if(nowDT.isBefore(assignVO.getFinishDateTime())) {
						functionSet.add(AssignApprovalAuth.REJECT);
					}
					break;
				case REJECTED:
				case CANCELED:
					break;
				default:
					break;
			}
			functionSet.add(AssignApprovalAuth.VIEW);
		}
		if(loginKey.equals(assignVO.getWriterKey())) {// 작성자 권한
			switch(assignVO.getAppStatus()) {
				case REQUEST:
					functionSet.add(AssignApprovalAuth.CANCEL);
					break;
				case APPROVED:
					functionSet.add(AssignApprovalAuth.CANCEL);
					break;
				case REJECTED:
				case CANCELED:
					break;
				default:
					break;
			}
			functionSet.add(AssignApprovalAuth.VIEW);
		}
		functionSet.removeAll(bannedCollection);
		
		ResourceAuthorityCollection approvalCollection = ResourceAuthorityCollection.generateCollection(functionSet);
		
		String authoritiesStr = Optional.of(approvalCollection).map(ResourceAuthorityCollection::getAuthorities).map(value -> value.toString()).orElse("없음");
		String userName = userDetails.getUsername();
		log.info("사용자({})의 사용신청({})에 대한 결재 권한: {}", userName, assignVO.getSkdKey(), authoritiesStr);
		
		return approvalCollection;
	}
	
	/**
	 * 파일에 대한 사용자의 권한 제공. {@link FileRelationType}및 {@link getMeetingAuthorityCollection}를 통해 조회한 회의 권한을 바탕으로 파일권한을 제공.
	 * 
	 * @param authentication 사용자 로그인 ID와 부여된 {@link DomainRole} 조회
	 * @param fileKey 파일 고유키
	 * @return 권한 {@link ResourceAuthorityVO}를 담은 권한모음객체
	 */
	public ResourceAuthorityCollection getFileAuthorityCollection(Authentication authentication, Integer fileKey) {
		AuthenticationDetails userDetails = (AuthenticationDetails) authentication.getDetails();
		MeetingFileInfoVO fileVO = fileServ.getFileOne(fileKey);
		if(fileVO == null) {
			return null;
		}
		ResourceAuthorityCollection mtCollection;
		ResourceAuthorityCollection fileCollection = null;
		Set<AuthCode> functionSet = new HashSet<>();	
		switch(fileVO.getRoleType()) {
			case MATERIAL :
				if(userDetails.hasRole(DomainRole.MASTER_ADMIN) || userDetails.hasRole(DomainRole.SYSTEM_ADMIN)) {
					functionSet.add(FileAuth.READ);
					functionSet.add(FileAuth.VIEW);
					functionSet.add(FileAuth.DOWN);
					functionSet.add(FileAuth.DELETE);
				}
				mtCollection = meetingAuthorityMng.getResourceAuthorityCollection(authentication, fileVO.getMeetingKey());
				if(mtCollection.hasAuthority(MeetingAuth.VIEW)) {
					functionSet.add(FileAuth.READ);
					functionSet.add(FileAuth.VIEW);
					functionSet.add(FileAuth.DOWN);
				}
				if(mtCollection.hasAuthority(MeetingAuth.UPDATE)) {
					functionSet.add(FileAuth.DELETE);
				}
				if(!functionSet.isEmpty()) {
					fileCollection = ResourceAuthorityCollection.generateCollection(functionSet);
				}
				break;
			case PHOTO:
				if(userDetails.hasRole(DomainRole.MASTER_ADMIN) || userDetails.hasRole(DomainRole.SYSTEM_ADMIN)) {
					functionSet.add(FileAuth.READ);
					functionSet.add(FileAuth.VIEW);
					functionSet.add(FileAuth.DOWN);
					functionSet.add(FileAuth.DELETE);
				}
				mtCollection = meetingAuthorityMng.getResourceAuthorityCollection(authentication, fileVO.getMeetingKey());
				if(mtCollection.hasAuthority(MeetingAuth.VIEW)) {
					functionSet.add(FileAuth.READ);
					functionSet.add(FileAuth.VIEW);
					functionSet.add(FileAuth.DOWN);
				}
				if(mtCollection.hasAuthority(MeetingAuth.PHOTO)) {
					functionSet.add(FileAuth.DELETE);
				}
				if(!functionSet.isEmpty()) {
					fileCollection = ResourceAuthorityCollection.generateCollection(functionSet);
				}
				break;
			case VOICE:
				if(userDetails.hasRole(DomainRole.MASTER_ADMIN) || userDetails.hasRole(DomainRole.SYSTEM_ADMIN)) {
					functionSet.add(FileAuth.READ);
					functionSet.add(FileAuth.VIEW);
					functionSet.add(FileAuth.DOWN);
					functionSet.add(FileAuth.DELETE);
				}
				mtCollection = meetingAuthorityMng.getResourceAuthorityCollection(authentication, fileVO.getMeetingKey());
				if(mtCollection.hasAuthority(MeetingAuth.VIEW)) {
					functionSet.add(FileAuth.READ);
					functionSet.add(FileAuth.VIEW);
					functionSet.add(FileAuth.DOWN);
				}
				if(mtCollection.hasAuthority(MeetingAuth.VOICE)) {
					functionSet.add(FileAuth.DELETE);
				}
				if(!functionSet.isEmpty()) {
					fileCollection = ResourceAuthorityCollection.generateCollection(functionSet);
				}
				break;
			case REPORT :
				if(userDetails.hasRole(DomainRole.MASTER_ADMIN) || userDetails.hasRole(DomainRole.SYSTEM_ADMIN)) {
					functionSet.add(FileAuth.READ);
					functionSet.add(FileAuth.VIEW);
					functionSet.add(FileAuth.DOWN);
				}
				mtCollection = meetingAuthorityMng.getResourceAuthorityCollection(authentication, fileVO.getMeetingKey());
				if(mtCollection.hasAuthority(MeetingAuth.VIEW)) {
					functionSet.add(FileAuth.READ);
					functionSet.add(FileAuth.VIEW);
					functionSet.add(FileAuth.DOWN);
				}
				if(!functionSet.isEmpty()) {
					fileCollection = ResourceAuthorityCollection.generateCollection(functionSet);
				}
				break;
			case COPY :
			case MEMO:
				String loginKey = userDetails.getUserId();
				if(loginKey.equals(fileVO.getEmpKey())) {
					functionSet.add(FileAuth.READ);
					functionSet.add(FileAuth.VIEW);
					functionSet.add(FileAuth.DOWN);
					functionSet.add(FileAuth.DELETE);
				}
				if(!functionSet.isEmpty()) {
					fileCollection = ResourceAuthorityCollection.generateCollection(functionSet);
				}
				break;
			default :
				break;
		}
		String authoritiesStr = Optional.of(fileCollection).map(ResourceAuthorityCollection::getAuthorities).map(value -> value.toString()).orElse("없음");
		String userName = authentication.getName();
		log.info("사용자({})의 파일({})에 대한 권한: {}", userName, fileKey, authoritiesStr);
		return fileCollection;
	}
	
	/**
	 * 회의록에 대한 권한 제공
	 * @param authentication 사용자 인증 토큰
	 * @param meetingKey 회의 고유키
	 * @return
	 */
	public ResourceAuthorityCollection getMeetingReportAuthorityCollection(Integer meetingKey) {
		String loginKey = EwpSecurityUtil.getLoginId();
		return getMeetingReportAuthorityCollection(loginKey, meetingKey);
	}
	
	/**
	 * 회의록에 대한 권한 제공
	 * @param userKey 사용자 고유키
	 * @param meetingKey 회의 고유키
	 * @return
	 */
	public ResourceAuthorityCollection getMeetingReportAuthorityCollection(String userKey, Integer meetingKey) {
		boolean result = super.waitResourceAuthorityCertificate(meetingKey);
		if(!result) {
			return ResourceAuthorityCollection.generateCollection(new HashSet<>());
		}
		ResourceAuthorityCollection mtCollection = meetingAuthorityMng.getResourceAuthorityCollection(userKey, meetingKey);
		ReportStatus reportStatus = ReportStatus.NEW;
		Optional<EwpMeetingReportVO> reportOpt = rpServ.getMeetingReportOne(meetingKey);
		if(reportOpt.isPresent()) {
			EwpMeetingReportVO reportVO = reportOpt.get();
			reportStatus = reportVO.getReportStatus();
		}
		Set<AuthCode> functionSet = new HashSet<>();
		if(mtCollection.hasAuthority(MeetingAuth.REPORT)) {
			functionSet.add(ReportAuth.VIEW);
			switch(reportStatus) {
				case NEW:
					functionSet.add(ReportAuth.VIEW);
					functionSet.add(ReportAuth.UPDATE);
					if(!mtCollection.hasAuthority(MeetingAuth.ATTEND)) {
						functionSet.add(ReportAuth.OPEN);
					}
					break;
				case OPEN:
					functionSet.add(ReportAuth.VIEW);
					functionSet.add(ReportAuth.UPDATE);
					functionSet.add(ReportAuth.OPINION);
					functionSet.add(ReportAuth.FINISH);
					break;
				case FINISH:
					functionSet.add(ReportAuth.VIEW);
					break;
			}
		}else if(mtCollection.hasAuthority(MeetingAuth.VIEW)){
			Optional<EwpMeetingAttendeeVO> attOpt = attServ.getMeetingAttendeeOne(userKey, meetingKey);
			if(attOpt.isPresent()) {
				EwpMeetingAttendeeVO attendee = attOpt.get();
				if(attendee.getAttendYN() == 'Y') {
					switch(reportStatus) {
					case NEW:
						break;
					case OPEN:
						functionSet.add(ReportAuth.VIEW);
						functionSet.add(ReportAuth.OPINION);
						break;
					case FINISH:
						break;
					}
				}
			}
		}
		ResourceAuthorityCollection reportCollection = ResourceAuthorityCollection.generateCollection(functionSet);
		String authoritiesStr = Optional.of(reportCollection).map(ResourceAuthorityCollection::getAuthorities).map(value -> value.toString()).orElse("없음");
		log.info("사용자({})의 회의({})의 회의록에 대한 권한: {}", userKey, meetingKey, authoritiesStr);
		return reportCollection;
	}
}
