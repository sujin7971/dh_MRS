package egov.framework.plms.main.bean.component.login;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
/**
 * LoginAuthProvider에게 제공할 Authentication의 Detail객체를 로그인 폼에서 Custom 파라미터를 전달받기 위해 개조한 Custom Detail객체로 변경.
 * CustomDetail을 Authentication객체의 getDeatils()함수에 매핑할 CustomWebAuthenticationDetailsSource 을 detail로 설정.
 * @author mckim
 */
public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  @Autowired
  CustomWebAuthenticationDetailsSource customWebAuthenticationDetailsSource;

  @Override
  protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
    super.setDetails(request, authRequest);
    authRequest.setDetails(customWebAuthenticationDetailsSource.buildDetails(request));
  }
}
