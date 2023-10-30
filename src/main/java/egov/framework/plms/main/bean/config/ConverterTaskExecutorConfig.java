package egov.framework.plms.main.bean.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class ConverterTaskExecutorConfig {
	@Bean(name = "cvtExecutor")
    public Executor converterAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("cvt-exe-pool");
        executor.initialize();
        return executor;
    }
	
	@Bean(name = "hwpExecutor")
    public Executor converterHWPAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("cvt_hwp-exe-pool");
        executor.initialize();
        return executor;
    }
	
	@Bean(name = "wordExecutor")
    public Executor converterWORDAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("cvt_word-exe-pool");
        executor.initialize();
        return executor;
    }
	
	@Bean(name = "pptExecutor")
    public Executor converterPPTAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("cvt_ppt-exe-pool");
        executor.initialize();
        return executor;
    }
	
	@Bean(name = "excelExecutor")
    public Executor converterEXCELAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("cvt_excel-exe-pool");
        executor.initialize();
        return executor;
    }
	
	@Bean(name = "pdfExecutor")
    public Executor converterPDFAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("cvt_pdf-exe-pool");
        executor.initialize();
        return executor;
    }
}
