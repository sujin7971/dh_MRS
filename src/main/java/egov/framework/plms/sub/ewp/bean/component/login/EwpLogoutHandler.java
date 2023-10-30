package egov.framework.plms.sub.ewp.bean.component.login;

import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import egov.framework.plms.sub.ewp.bean.component.login.sso.EwpSSOLoginCheck;
import egov.framework.plms.sub.ewp.bean.component.properties.PlteProperties;
import egov.framework.plms.sub.ewp.bean.component.properties.SsoProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@RequiredArgsConstructor
@Profile("ewp")
public class EwpLogoutHandler implements LogoutHandler{

	private final EwpSSOLoginCheck ewpSSO;
	private final SsoProperties ssoProperties;
	private final PlteProperties plteProperties;
	
	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		// TODO Auto-generated method stub
		String connectIp = request.getRemoteAddr();

		if( !connectIp.contains( plteProperties.getRemoteIp() ) ) {
			log.info("SSO login check");
			//SSO 체크 부분
			try {
				String encData = ewpSSO.createRequestData(request);
				log.info("encData : {}",encData);
				//String result = ewpSSO.httpPost(encData);
				String result = ewpSSO.httpPost(URLEncoder.encode(encData));
				log.info("rs : {}",result);
				if(result == null) {
					log.info("sso not login");
					//request.getSession().setAttribute("result", "ewp");
					response.setStatus(HttpServletResponse.SC_OK);
				} else {
					response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
					result = ewpSSO.parsingData(result);
					log.info("result : {}" , result);
					String resultId = ewpSSO.getId(request, result);
					log.info("resultID : {}", resultId);
					request.getSession().setAttribute("result", resultId);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			log.info("not SSO login");
			response.setStatus(HttpServletResponse.SC_OK);
		}
	}

}
