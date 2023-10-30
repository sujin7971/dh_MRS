package egov.framework.plms.main.bean.config.db;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import egov.framework.plms.main.bean.component.properties.DataSourceProperties;
import egov.framework.plms.main.core.model.enums.common.OrderColumn;
import egov.framework.plms.main.core.model.enums.common.OrderDirection;
import egov.framework.plms.main.core.model.enums.file.ConversionStatus;
import egov.framework.plms.main.core.model.enums.file.ConversionStep;
import egov.framework.plms.main.core.model.enums.file.FileCategory;
import egov.framework.plms.main.core.model.enums.file.FileStatus;
import egov.framework.plms.main.core.model.enums.file.RelatedEntityType;
import egov.framework.plms.main.core.model.enums.file.RelationType;
import egov.framework.plms.main.core.model.enums.meeting.ApprovalStatus;
import egov.framework.plms.main.core.model.enums.meeting.AttendRole;
import egov.framework.plms.main.core.model.enums.meeting.MeetingStatus;
import egov.framework.plms.main.core.model.enums.meeting.ReportStatus;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import egov.framework.plms.main.core.model.enums.meeting.ScheduleType;
import egov.framework.plms.main.core.model.enums.user.AccountStatus;
import egov.framework.plms.main.core.model.enums.user.DomainRole;
import egov.framework.plms.main.core.model.enums.user.ManagerRole;
import egov.framework.plms.main.core.mybatis.ListStringTypeHandler;
import egov.framework.plms.sub.ewp.core.model.enums.FileRole;

/**
 * 회의관리시스템 내부 마리아 DB 연결
 * @author mckim
 * @version 1.0
 * @since 2023. 2. 15
 */
@ConditionalOnProperty(prefix = "spring.datasource.plms", name = "enabled", havingValue = "true", matchIfMissing = true)
@Configuration
@MapperScan(
		basePackages = {"egov.framework.plms.main.bean.mvc.mapper.**/*", "egov.framework.plms.sub.lime.bean.mvc.mapper.**/*", "egov.framework.plms.sub.ewp.bean.mvc.mapper.maria.**/*"}, sqlSessionFactoryRef="sqlSessionFactory")/*멀티DB사용시 mapper클래스파일 스켄용 basePackages를 DB별로 따로설정*/
@EnableTransactionManagement
public class PlmsDataSourceConfig {

	@Bean(name="plmsDataSourceProperties")
	@ConfigurationProperties(prefix = "spring.datasource.plms")
	public DataSourceProperties getDataSourceProperties() {
		return new DataSourceProperties();
	}
	
	@Bean(name="plmsDataSource")
	public DataSource dataSource(@Qualifier("plmsDataSourceProperties") DataSourceProperties dataSourceProperties) {
		HikariConfig hikariConfig = new HikariConfig();
		 
        hikariConfig.setUsername(dataSourceProperties.getUsername());
        hikariConfig.setPassword(dataSourceProperties.getPassword());
        hikariConfig.setJdbcUrl(dataSourceProperties.getJdbcUrl());
        hikariConfig.setDriverClassName(dataSourceProperties.getDriverClassName());
        hikariConfig.setMaximumPoolSize(25); 
 
        return new HikariDataSource(hikariConfig);
	}
	
    // sqlSessionFactoryBean
    @Bean
    public SqlSessionFactory sqlSessionFactory(@Qualifier("plmsDataSource") DataSource plmsDataSource, ApplicationContext applicationContext) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        //DataSource 주입
        sessionFactory.setDataSource(plmsDataSource);
        sessionFactory.setConfigLocation(applicationContext.getResource("classpath:mybatis-config.xml"));
        sessionFactory.setTypeAliasesPackage("com.lime.meeting.model");
        //mapper 주입
        Resource[] res = new PathMatchingResourcePatternResolver().getResources("classpath:mapper/maria/**/*.xml");
        sessionFactory.setMapperLocations(res);
        sessionFactory.setTypeHandlers(new TypeHandler[] {
    		new AccountStatus.TypeHandler(),
    		new DomainRole.TypeHandler(),
    		new ManagerRole.TypeHandler(),
    		new AttendRole.TypeHandler(),
    		new RoomType.TypeHandler(),
    		new MeetingStatus.TypeHandler(),
    		new OrderDirection.TypeHandler(),
    		new OrderColumn.TypeHandler(),
        	new ApprovalStatus.TypeHandler(),
        	new ScheduleType.TypeHandler(),
        	new ReportStatus.TypeHandler(),
            new FileRole.TypeHandler(),
            new FileCategory.TypeHandler(),
            new FileStatus.TypeHandler(),
            new ConversionStatus.TypeHandler(),
            new ConversionStep.TypeHandler(),
            new RelatedEntityType.TypeHandler(),
            new RelationType.TypeHandler(),
            new ListStringTypeHandler()
        });
        return sessionFactory.getObject();
    }
    
    @Bean(name = "mariaTransactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("plmsDataSource") DataSource plmsDataSource) {
        return new DataSourceTransactionManager(plmsDataSource);
    }
}