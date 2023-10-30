package egov.framework.plms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
@ServletComponentScan
@SpringBootApplication
@EnableScheduling
@EnableCaching
public class PlmsApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(PlmsApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(PlmsApplication.class);
	}
}
