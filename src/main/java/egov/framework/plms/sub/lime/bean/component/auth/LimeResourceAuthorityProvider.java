package egov.framework.plms.sub.lime.bean.component.auth;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import egov.framework.plms.main.bean.component.auth.MeetingAuthorityManager;
import egov.framework.plms.main.bean.component.auth.ResourceAuthorityProvider;
import egov.framework.plms.main.bean.component.common.AsyncProcessor;
import egov.framework.plms.main.bean.mvc.entity.file.FileDetailVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingAttendeeVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingReportVO;
import egov.framework.plms.main.core.model.auth.ResourceAuthorityCollection;
import egov.framework.plms.main.core.model.enums.AuthCode;
import egov.framework.plms.main.core.model.enums.auth.FileAuth;
import egov.framework.plms.main.core.model.enums.auth.MeetingAuth;
import egov.framework.plms.main.core.model.enums.auth.ReportAuth;
import egov.framework.plms.main.core.model.enums.file.RelatedEntityType;
import egov.framework.plms.main.core.model.enums.file.RelationType;
import egov.framework.plms.main.core.model.enums.meeting.ReportStatus;
import egov.framework.plms.main.core.model.enums.user.DomainRole;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingAttendeeVO;
import egov.framework.plms.sub.lime.bean.mvc.service.file.LimeFileInfoService;
import egov.framework.plms.sub.lime.bean.mvc.service.meeting.LimeMeetingAttendeeService;
import egov.framework.plms.sub.lime.bean.mvc.service.meeting.LimeMeetingReportService;
import egov.framework.plms.sub.lime.core.util.LimeSecurityUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("LimeResourceAuthorityProvider")
@Profile("lime")
@Primary
/**
 * 요청 자원에 대한 사용자의 권한 조회 후 제공
 * 
 * @author mckim
 *
 */
public class LimeResourceAuthorityProvider extends ResourceAuthorityProvider{
	@Autowired
	private MeetingAuthorityManager meetingAuthorityMng;
	@Autowired
	private LimeMeetingReportService rpServ;
	@Autowired
	private LimeMeetingAttendeeService attServ;
	@Autowired
	private LimeFileInfoService fileServ;
	@Autowired
	private AsyncProcessor asyncProcessor;
	
	public ResourceAuthorityCollection getMeetingFileAuthorityCollection(Integer fileId) {
		Optional<FileDetailVO> opt = fileServ.selectFileOne(fileId);
		if(!opt.isPresent()) {
			return ResourceAuthorityCollection.generateCollection(new HashSet<>());
		}
		FileDetailVO fileVO = opt.get();
		if(fileVO.getRelatedEntityType() != RelatedEntityType.MEETING) {
			return ResourceAuthorityCollection.generateCollection(new HashSet<>());
		}
		Integer meetingId = fileVO.getRelatedEntityId();
		RelationType relationType = fileVO.getRelationType();
		String loginId = LimeSecurityUtil.getLoginId();
		ResourceAuthorityCollection mtCollection = super.getMeetingAuthorityCollection(meetingId);
		ResourceAuthorityCollection fileCollection = null;
		Set<AuthCode> functionSet = new HashSet<>();	
		switch(relationType) {
			case MEETING_MATERIAL :
				if(LimeSecurityUtil.hasRole(DomainRole.MASTER_ADMIN) || LimeSecurityUtil.hasRole(DomainRole.SYSTEM_ADMIN)) {
					functionSet.add(FileAuth.READ);
					functionSet.add(FileAuth.VIEW);
					functionSet.add(FileAuth.DOWN);
					functionSet.add(FileAuth.DELETE);
				}
				if(mtCollection.hasAuthority(MeetingAuth.VIEW)) {
					functionSet.add(FileAuth.READ);
					functionSet.add(FileAuth.VIEW);
					functionSet.add(FileAuth.DOWN);
				}
				if(mtCollection.hasAuthority(MeetingAuth.UPDATE)) {
					functionSet.add(FileAuth.DELETE);
				}
				break;
			case MEETING_PHOTO:
				if(LimeSecurityUtil.hasRole(DomainRole.MASTER_ADMIN) || LimeSecurityUtil.hasRole(DomainRole.SYSTEM_ADMIN)) {
					functionSet.add(FileAuth.READ);
					functionSet.add(FileAuth.VIEW);
					functionSet.add(FileAuth.DOWN);
					functionSet.add(FileAuth.DELETE);
				}
				if(mtCollection.hasAuthority(MeetingAuth.VIEW)) {
					functionSet.add(FileAuth.READ);
					functionSet.add(FileAuth.VIEW);
					functionSet.add(FileAuth.DOWN);
				}
				if(mtCollection.hasAuthority(MeetingAuth.PHOTO)) {
					functionSet.add(FileAuth.DELETE);
				}
				break;
			case MEETING_VOICE:
				if(LimeSecurityUtil.hasRole(DomainRole.MASTER_ADMIN) || LimeSecurityUtil.hasRole(DomainRole.SYSTEM_ADMIN)) {
					functionSet.add(FileAuth.READ);
					functionSet.add(FileAuth.VIEW);
					functionSet.add(FileAuth.DOWN);
					functionSet.add(FileAuth.DELETE);
				}
				if(mtCollection.hasAuthority(MeetingAuth.VIEW)) {
					functionSet.add(FileAuth.READ);
					functionSet.add(FileAuth.VIEW);
					functionSet.add(FileAuth.DOWN);
				}
				if(mtCollection.hasAuthority(MeetingAuth.VOICE)) {
					functionSet.add(FileAuth.DELETE);
				}
				break;
			case MEETING_REPORT :
				if(LimeSecurityUtil.hasRole(DomainRole.MASTER_ADMIN) || LimeSecurityUtil.hasRole(DomainRole.SYSTEM_ADMIN)) {
					functionSet.add(FileAuth.READ);
					functionSet.add(FileAuth.VIEW);
					functionSet.add(FileAuth.DOWN);
				}
				if(mtCollection.hasAuthority(MeetingAuth.VIEW)) {
					functionSet.add(FileAuth.READ);
					functionSet.add(FileAuth.VIEW);
					functionSet.add(FileAuth.DOWN);
				}
				break;
			case MEETING_COPY :
			case MEETING_MEMO:
				if(loginId.equals(fileVO.getUploaderId())) {
					functionSet.add(FileAuth.READ);
					functionSet.add(FileAuth.VIEW);
					functionSet.add(FileAuth.DOWN);
					functionSet.add(FileAuth.DELETE);
				}
				break;
			default :
				break;
		}
		fileCollection = ResourceAuthorityCollection.generateCollection(functionSet);
		String authoritiesStr = Optional.of(fileCollection).map(ResourceAuthorityCollection::getAuthorities).map(value -> value.toString()).orElse("없음");
		log.info("사용자({})의 파일({})에 대한 권한: {}", loginId, fileId, authoritiesStr);
		return fileCollection;
	}
	
	/**
	 * 회의록에 대한 권한 제공
	 * @param userKey 사용자 고유키
	 * @param meetingKey 회의 고유키
	 * @return
	 */
	public ResourceAuthorityCollection getMeetingReportAuthorityCollection(Integer meetingId) {
		boolean result = super.waitResourceAuthorityCertificate(meetingId);
		if(!result) {
			return ResourceAuthorityCollection.generateCollection(new HashSet<>());
		}
		String userId = LimeSecurityUtil.getLoginId();
		ResourceAuthorityCollection mtCollection = getMeetingAuthorityCollection(userId, meetingId);
		ReportStatus reportStatus = ReportStatus.NEW;
		Optional<MeetingReportVO> reportOpt = rpServ.getMeetingReportOne(meetingId);
		if(reportOpt.isPresent()) {
			MeetingReportVO reportVO = reportOpt.get();
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
					functionSet.add(ReportAuth.FINISH);
					break;
				case FINISH:
					functionSet.add(ReportAuth.VIEW);
					break;
			}
		}
		ResourceAuthorityCollection reportCollection = ResourceAuthorityCollection.generateCollection(functionSet);
		String authoritiesStr = Optional.of(reportCollection).map(ResourceAuthorityCollection::getAuthorities).map(value -> value.toString()).orElse("없음");
		log.info("사용자({})의 회의({})의 회의록에 대한 권한: {}", userId, meetingId, authoritiesStr);
		return reportCollection;
	}
}
