package egov.framework.plms.main.bean.config;

import java.util.List;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.multipart.support.MultipartFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.VersionResourceResolver;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.navercorp.lucy.security.xss.servletfilter.XssEscapeServletFilter;

import egov.framework.plms.main.bean.component.session.SessionInterceptor;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
	private final SessionInterceptor sessionInterceptor;
	/* form-data XSS 보안 */
	@Bean
	public FilterRegistrationBean<MultipartFilter> multipartFilterRegistrationBean() {
		FilterRegistrationBean<MultipartFilter> filterRegistration = new FilterRegistrationBean<MultipartFilter>();
		filterRegistration.setFilter(new MultipartFilter());
		filterRegistration.setOrder(0);
		filterRegistration.addUrlPatterns("/*");
		return filterRegistration;
	}
	
    @Bean
	public FilterRegistrationBean<XssEscapeServletFilter> filterRegistrationBean() {
		FilterRegistrationBean<XssEscapeServletFilter> filterRegistration = new FilterRegistrationBean<>();
		filterRegistration.setFilter(new XssEscapeServletFilter());
		filterRegistration.setOrder(1);
		filterRegistration.addUrlPatterns("/*");
		return filterRegistration;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// TODO Auto-generated method stub
		//WebMvcConfigurer.super.addInterceptors(registry);
		registry.addInterceptor(sessionInterceptor)
        .addPathPatterns("/**/*")
        .excludePathPatterns("/login","/api/emp/admin/mail","/init", "/api/emp/{empKey}/password");
	}
	
	@Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        
        objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        converter.setObjectMapper(objectMapper);
        converters.add(converter);
    }
	
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/core-assets/**", "/resources/front-end-assets/js/**", "/resources/front-end-assets/css/**")
	        .addResourceLocations("/resources/core-assets/", "/resources/front-end-assets/js/", "/resources/front-end-assets/css/")
	        .setCachePeriod(3600)
	        .resourceChain(true)
	        .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"));
    }
    /* JSON XSS 보안 */
    /*
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(escapingConverter());
    }

    @Bean
    public HttpMessageConverter<?> escapingConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.getFactory().setCharacterEscapes(new HTMLCharacterEscapes());

        MappingJackson2HttpMessageConverter escapingConverter =
                new MappingJackson2HttpMessageConverter();
        escapingConverter.setObjectMapper(objectMapper);

        return escapingConverter;
    }
	*/
}