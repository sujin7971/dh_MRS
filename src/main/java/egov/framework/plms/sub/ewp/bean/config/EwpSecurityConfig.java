package egov.framework.plms.sub.ewp.bean.config;

import java.util.Objects;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionEvent;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import egov.framework.plms.main.bean.component.login.CustomUsernamePasswordAuthenticationFilter;
import egov.framework.plms.main.bean.component.session.AuthenticationEntryPoint;
import egov.framework.plms.main.core.model.enums.user.LoginType;
import egov.framework.plms.sub.ewp.bean.component.login.EwpLoginFailureHandler;
import egov.framework.plms.sub.ewp.bean.component.login.EwpLoginSuccessHandler;
import egov.framework.plms.sub.ewp.bean.component.login.EwpLogoutHandler;
import egov.framework.plms.sub.ewp.bean.component.session.EwpSessionExpiredStrategy;
import egov.framework.plms.sub.ewp.core.model.login.EwpAuthenticationDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
@Profile("ewp")
public class EwpSecurityConfig extends WebSecurityConfigurerAdapter{

	private final EwpLoginSuccessHandler loginSuccessHandler;
	private final EwpLoginFailureHandler loginFailureHandler;
	private final EwpSessionExpiredStrategy expiredStrategy;
	private CustomUsernamePasswordAuthenticationFilter authenticationFilter;
	private final EwpLogoutHandler logoutHandler;
	private final ApplicationEventPublisher eventPublisher;
	@Bean
	public PasswordEncoder passwordEncoder(){
	    return new BCryptPasswordEncoder();
	}

    @Override
    public void configure(WebSecurity web) throws Exception
    {
        // 하위 파일 목록은 인증 무시 ( = 항상통과 )
        web.ignoring().antMatchers("/resources/**","/meetingtime/**",  "/css/**","/fonts/**", "/js/**", "/img/**", "/src/lib/**");
    }

	@Override
	protected void configure(HttpSecurity http) throws Exception {
    	expiredStrategy.setSessionRegistry(sessionRegistry());
    	/* 헤더에 추가 */
    	http.headers()
    		// clickjacking attack 방지
    			.frameOptions()
    			.sameOrigin()
    		.and()
    		// HSTS 적용
    		.headers()
    			.httpStrictTransportSecurity()
    			.maxAgeInSeconds(31536000)
    			.includeSubDomains(true)
    			.preload(true);
    	/* 로그인 설정 */
        http
        // 커스텀 Authentication토큰을 커스텀 토큰으로 변경하기 위한 필터 등록
        .addFilterBefore(authFilter(), UsernamePasswordAuthenticationFilter.class)
        // 페이지 권한 설정
        .authorizeRequests()
                .antMatchers("/error/**", "/ewp/display/**","/api/ewp/display/**", "/ewp/public/**", "/api/ewp/public/**").permitAll()
                .antMatchers("/login/**", "/api/ewp/proxy/**").anonymous()
                .antMatchers("/admin/system/**", "/ewp/admin/system/**", "/api/admin/system/**", "/api/ewp/admin/system/**").hasAnyRole("SYSTEM_ADMIN")
                .antMatchers("/admin/master/**", "/ewp/admin/master/**", "/api/admin/master/**", "/api/ewp/admin/master/**").hasAnyRole("MASTER_ADMIN")
                .antMatchers("/dev/**", "/ewp/dev/**", "/api/dev/**", "/api/ewp/dev/**").hasAnyRole("DEV")
                .antMatchers("/ewp/**", "/api/**", "/api/ewp/**").hasAnyRole("MEMBER")
                //.anyRequest().authenticated()
            .and() // 로그인 설정
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/ewp/home")
                .failureUrl("/login")
                .permitAll()
            .and() // 로그아웃 설정
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//                .addLogoutHandler(logoutHandler)
                .logoutSuccessUrl("/")
                //.logoutSuccessHandler(logoutSuccessHandler)
                .invalidateHttpSession(true)
                .permitAll()
            .and()
            	// 접근 거부된 페이지 요청
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler)
                //.accessDeniedPage("/error/403")
                // 권한 없는 사용자의 Ajax 요청
                .authenticationEntryPoint(new AuthenticationEntryPoint("/"))
            .and()
                .csrf()
				.ignoringAntMatchers("/login", "/api/test/**")
             ;

        /* 중복 로그인 방지 */
    	http.sessionManagement()
        .maximumSessions(1)// 사용자의 가용 세션 수. 테스트 10, 실사용 1
        .maxSessionsPreventsLogin(false)// true:나중에 접속한 사용자 로그인 방지, false:먼저 접속한 사용자 logout 처리
        .expiredSessionStrategy(expiredStrategy)
        .sessionRegistry(sessionRegistry());
    	
    	/* Session Fixation 방지 */
    	http.sessionManagement()
    	.sessionFixation()
    	.newSession();
    	http.sessionManagement()
        .addObjectPostProcessor(new ObjectPostProcessor<CompositeSessionAuthenticationStrategy>() {
            @Override
            public <O extends CompositeSessionAuthenticationStrategy> O postProcess(O object) {
                CompositeSessionAuthenticationStrategy strategy = (CompositeSessionAuthenticationStrategy) object;
                EwpSessionAuthenticationStrategy customStrategy = new EwpSessionAuthenticationStrategy(strategy, eventPublisher);
                authenticationFilter.setSessionAuthenticationStrategy(customStrategy);
                return object;
            }
        });
	}
	/**
	 * AccessDeniedException에 대한 처리
	 */
	private final AccessDeniedHandler accessDeniedHandler = 
			(request, response, accessDeniedException) -> {
				String ajaxHeader = request.getHeader("X-Requested-With");
				String errorUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
				String forwardUri = (String) request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
				Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
				log.info("헤더: {}, 에러코드: {}, Error URI: {}, Forward URI", ajaxHeader, status, errorUri, forwardUri);
				String msg = accessDeniedException.getMessage();
				RequestDispatcher dispatcher = request.getRequestDispatcher("/error/403?message="+msg);
				dispatcher.forward(request, response);
	};
    
	// 추가하지 않으면 사용자가 Logout 후 다시 Login 할 때 "Maximum sessions of 1 for this principal exceeded" 에러를 발생시키며 로그인 되지 않음
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    // 커스텀 Authentication토큰을 커스텀 토큰으로 변경하기 위한 필터 등록
    @Bean
    public CustomUsernamePasswordAuthenticationFilter authFilter() throws Exception {
    	authenticationFilter = new CustomUsernamePasswordAuthenticationFilter();
        authenticationFilter.setAuthenticationManager(authenticationManagerBean());
        authenticationFilter.setAuthenticationSuccessHandler(loginSuccessHandler);
        authenticationFilter.setAuthenticationFailureHandler(loginFailureHandler);
        return authenticationFilter;
    }
    
    public class EwpSessionAuthenticationStrategy implements SessionAuthenticationStrategy {
    	private CompositeSessionAuthenticationStrategy compositeStrategy;
    	private ChangeSessionIdAuthenticationStrategy fixationProtectionStrategy;
    	private NullAuthenticatedSessionStrategy nullAuthenticatedSessionStrategy;
    	private ApplicationEventPublisher eventPublisher;
    	public EwpSessionAuthenticationStrategy(CompositeSessionAuthenticationStrategy compositeStrategy, ApplicationEventPublisher eventPublisher) {
    		this.compositeStrategy = compositeStrategy;
    		this.eventPublisher = eventPublisher;
    		this.fixationProtectionStrategy = new ChangeSessionIdAuthenticationStrategy();
    		this.nullAuthenticatedSessionStrategy = new NullAuthenticatedSessionStrategy();
    	}

    	@Override
    	public void onAuthentication(Authentication authentication, HttpServletRequest request,
    			HttpServletResponse response) throws SessionAuthenticationException {
    		if (authentication != null && authentication.getDetails() instanceof EwpAuthenticationDetails) {
                EwpAuthenticationDetails details = (EwpAuthenticationDetails) authentication.getDetails();
                if (details.getLoginType() == LoginType.PLTE) {
                    compositeStrategy.onAuthentication(authentication, request, response);
                } else {
                    String oldSessionId = request.getRequestedSessionId();
                    log.info("이전 세션 ID: {}", oldSessionId);
                    fixationProtectionStrategy.onAuthentication(authentication, request, response);
                    String newSessionId = request.getRequestedSessionId();
                    log.info("현재 세션 ID: {}", newSessionId);
                    if (!Objects.equals(oldSessionId, newSessionId)) {
                        SessionFixationProtectionEvent event = new SessionFixationProtectionEvent(authentication, oldSessionId, newSessionId);
                        eventPublisher.publishEvent(event);
                    }
                }
            } else {
                nullAuthenticatedSessionStrategy.onAuthentication(authentication, request, response);
            }
    	}
    }
}
