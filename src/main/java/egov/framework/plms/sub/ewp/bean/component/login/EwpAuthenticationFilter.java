package egov.framework.plms.sub.ewp.bean.component.login;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import egov.framework.plms.main.bean.component.login.CustomWebAuthenticationDetailsSource;
import egov.framework.plms.sub.ewp.bean.component.login.sso.EwpSSOLoginCheck;
import egov.framework.plms.sub.ewp.bean.component.properties.SsoProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EwpAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	  @Autowired
	  CustomWebAuthenticationDetailsSource customWebAuthenticationDetailsSource;
	  @Autowired
	  EwpSSOLoginCheck ewpSSO;
	  @Autowired
	  SsoProperties ssoProperties;

	  @Override
	  protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
		super.setDetails(request, authRequest);
		authRequest.setDetails(customWebAuthenticationDetailsSource.buildDetails(request));
	  }
}
