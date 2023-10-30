package egov.framework.plms.sub.ewp.bean.config.db;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import egov.framework.plms.main.bean.component.properties.DataSourceProperties;



/**
 * 동서발전 SMS 발송 MSSQL DB 연결
 * @author mckim
 * @version 1.0
 * @since 2023. 2. 15
 */
@ConditionalOnProperty(prefix = "spring.datasource.sms", name = "enabled", havingValue = "true", matchIfMissing = true)
@Configuration("ewpSMSDataSourceConfig")
@MapperScan(basePackages = {"egov.framework.plms.sub.ewp.bean.mvc.mapper.mssql"}, sqlSessionFactoryRef="sqlSessionFactory4")/*멀티DB사용시 mapper클래스파일 스켄용 basePackages를 DB별로 따로설정*/
@EnableTransactionManagement
@Profile("ewp")
public class SMSDataSourceConfig {

	@Bean(name="smsDataSourceProperties")
	@ConfigurationProperties(prefix = "spring.datasource.sms")
	public DataSourceProperties getDataSourceProperties() {
		return new DataSourceProperties();
	}
	
    @Bean(name="smsDataSource")
	public DataSource dataSource(@Qualifier("smsDataSourceProperties") DataSourceProperties dataSourceProperties) {
    	HikariConfig hikariConfig = new HikariConfig();
		 
        hikariConfig.setUsername(dataSourceProperties.getUsername());
        hikariConfig.setPassword(dataSourceProperties.getPassword());
        hikariConfig.setJdbcUrl(dataSourceProperties.getJdbcUrl());
        hikariConfig.setDriverClassName(dataSourceProperties.getDriverClassName());
        hikariConfig.setMaximumPoolSize(5); 
 
        return new HikariDataSource(hikariConfig);
	}

	// sqlSessionFactoryBean
	@Bean(name="sqlSessionFactory4")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("smsDataSource") DataSource smsDataSource, ApplicationContext applicationContext) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        //DataSource 주입
        sessionFactory.setDataSource(smsDataSource);
        sessionFactory.setConfigLocation(applicationContext.getResource("classpath:mybatis-config.xml"));
        //sessionFactory.setTypeAliasesPackage("com.lime.meeting.model");
        //mapper 주입
        Resource[] res = new PathMatchingResourcePatternResolver().getResources("classpath:mapper/mssql/ewp/**/*.xml");
        sessionFactory.setMapperLocations(res);
        return sessionFactory.getObject();
    }
}
