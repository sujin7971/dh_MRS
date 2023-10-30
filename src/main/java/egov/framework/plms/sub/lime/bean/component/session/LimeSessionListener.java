package egov.framework.plms.sub.lime.bean.component.session;

import java.util.List;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import egov.framework.plms.main.bean.component.monitoring.MeetingMonitoringManager;
import egov.framework.plms.main.bean.component.session.SessionFixationProtectionEventListener;
import egov.framework.plms.main.bean.component.session.SessionManager;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingAttendeeVO;
import egov.framework.plms.main.bean.mvc.service.meeting.MeetingAttendeeService;
import egov.framework.plms.main.core.model.enums.user.LoginType;
import egov.framework.plms.main.core.model.login.AuthenticationDetails;
import egov.framework.plms.main.core.model.response.SocketMessage;
import egov.framework.plms.main.core.model.response.SocketMessage.ActionType;
import egov.framework.plms.main.core.model.response.SocketMessage.MessageType;
import egov.framework.plms.sub.ewp.bean.component.websocket.EwpMeetingMessageBroker;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebListener
@Profile("lime")
/**
 * 세션의 생성과 소멸을 감지하는 이벤트 리스너.
 * 세션 소멸시 SessionManager 컴포넌트에서 소유주 정보를 제거하고 소멸된 세션의 소유주가 외부 참석자 인 경우 초대받은 회의가 없을시 관련 데이터 모두를 DB에서 삭제 처리.
 * Autowired와 부모 생성자 호출이 아닌 일반적인 방식으로 빈 객체를 바인딩할 경우 WAR에서 SessionListener의 빈 객체 생성이 되지 않음
 * @author mckim
 *
 */
public class LimeSessionListener implements HttpSessionListener {
	@Autowired
	private MeetingMonitoringManager monitoringMng;
	@Autowired
	private SessionManager sessionMng;
	//@Autowired(required = false)
	//private GuestService gstServ;
	@Autowired
	private MeetingAttendeeService attServ;
	
	
	public LimeSessionListener() {
		super();
    }
	
	/**
	 * 세션 생성 이벤트. Session Fixation 방지 정책으로 인해 생성된 세션이 바로 교체되기때문에 Authentication토큰은 조회 불가.<br>
	 * {@link SessionFixationProtectionEventListener SessionFixationProtectionEventListener}의 
	 * {@link SessionFixationProtectionEventListener#onApplicationEvent(org.springframework.security.web.authentication.session.SessionFixationProtectionEvent) SessionFixationProtectionEventListener.onApplicationEvent}에서 
	 * Authentication 토큰 조회
	 */
    @Override
    public void sessionCreated(HttpSessionEvent hse) {
    	HttpSession session = hse.getSession();
    	log.info("세션 생성- sessionId: {}", session.getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent hse) {
    	String sessionId = hse.getSession().getId();
    	log.info("세션 종료- sessionId: {}", sessionId);
    	if(sessionMng != null) {
    		sessionMng.removeSessionInformation(sessionId);
    	}
    	if(monitoringMng == null) {
    		return;
    	}
    	Integer meetingKey = monitoringMng.removeEntry(sessionId);
    	if(meetingKey != null) {
    		//broker.broadCastMsg(meetingKey, SocketMessage.builder(MessageType.NOTICE).actionType(ActionType.LEAVE).build());
    	}
    	//세션에 할당된 인증 토큰 객체
    	Object securityContextObject =  hse.getSession().getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
    	if(securityContextObject != null){
    	    SecurityContext securityContext = (SecurityContext)securityContextObject;
    	    Authentication authentication = securityContext.getAuthentication();
        	AuthenticationDetails detail = (AuthenticationDetails) authentication.getDetails();
        	sessionMng.removeSessionOwner(hse.getSession().getId());
        	if(detail.getLoginType() == LoginType.GUEST) {
        		String guestKey = detail.getUserId();
    			List<MeetingAttendeeVO> list = attServ.getMeetingAttendeeListByUser(guestKey);
    			if(list.size() == 0) {
    				//gstServ.deleteGuestInfo(guestKey);
    			}
    		}
    	}
    	
    }
}
