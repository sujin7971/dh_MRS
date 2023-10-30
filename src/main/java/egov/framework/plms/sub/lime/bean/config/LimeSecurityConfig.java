package egov.framework.plms.sub.lime.bean.config;

import javax.servlet.RequestDispatcher;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import egov.framework.plms.main.bean.component.login.CustomUsernamePasswordAuthenticationFilter;
import egov.framework.plms.main.bean.component.session.AuthenticationEntryPoint;
import egov.framework.plms.sub.lime.bean.component.login.LimeLoginFailureHandler;
import egov.framework.plms.sub.lime.bean.component.login.LimeLoginSuccessHandler;
import egov.framework.plms.sub.lime.bean.component.session.LimeSessionExpiredStrategy;
import egov.framework.plms.sub.lime.core.model.session.LimeSessionAuthenticationStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
@Profile("lime")
public class LimeSecurityConfig extends WebSecurityConfigurerAdapter{

	private final LimeLoginSuccessHandler loginSuccessHandler;
	private final LimeLoginFailureHandler loginFailureHandler;
	private final LimeSessionExpiredStrategy expiredStrategy;
	private CustomUsernamePasswordAuthenticationFilter authenticationFilter;
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
                .antMatchers("/error/**", "/api/user/**").permitAll()//로그인
                .antMatchers("/login/**").anonymous()
                .antMatchers("/admin/system/**", "/lime/admin/system/**", "/api/admin/system/**", "/api/lime/admin/system/**").hasAnyRole("SYSTEM_ADMIN")
                .antMatchers("/admin/master/**", "/lime/admin/master/**", "/api/admin/master/**", "/api/lime/admin/master/**").hasAnyRole("MASTER_ADMIN")
                .antMatchers("/dev/**", "/lime/dev/**", "/api/dev/**", "/api/lime/dev/**").hasAnyRole("DEV")
                .antMatchers("/lime/**", "/api/**", "/api/lime/**").hasAnyRole("MEMBER")
                //.anyRequest().authenticated()
            .and() // 로그인 설정
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/home")
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
                LimeSessionAuthenticationStrategy customStrategy = new LimeSessionAuthenticationStrategy(strategy, eventPublisher);
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
}
