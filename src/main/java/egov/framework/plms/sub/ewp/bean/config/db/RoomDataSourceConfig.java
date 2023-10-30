package egov.framework.plms.sub.ewp.bean.config.db;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import egov.framework.plms.main.bean.component.properties.DataSourceProperties;
import egov.framework.plms.sub.ewp.core.model.enums.SeqMenu;
import lombok.extern.slf4j.Slf4j;


/**
 * 동서발전 장소및 장소사용신청 티베로 DB 연결
 * @author mckim
 * @version 1.0
 * @since 2023. 2. 15
 */
@Slf4j
@ConditionalOnProperty(prefix = "spring.datasource.room", name = "enabled", havingValue = "true", matchIfMissing = true)
@Configuration("ewpRoomDataSourceConfig")
@MapperScan(basePackages = {"egov.framework.plms.sub.ewp.bean.mvc.mapper.tibero.**.*"}, sqlSessionFactoryRef="sqlSessionFactory2")/*멀티DB사용시 mapper클래스파일 스켄용 basePackages를 DB별로 따로설정*/
@EnableTransactionManagement
@Profile("ewp")
public class RoomDataSourceConfig {
    
	@Bean(name="roomDataSourceProperties")
	@ConfigurationProperties(prefix = "spring.datasource.room")
	public DataSourceProperties getDataSourceProperties() {
		return new DataSourceProperties();
	}
	
    @Bean(name="roomDataSource")
	public DataSource dataSource(@Qualifier("roomDataSourceProperties") DataSourceProperties dataSourceProperties) {
    	HikariConfig hikariConfig = new HikariConfig();
		 
        hikariConfig.setUsername(dataSourceProperties.getUsername());
        hikariConfig.setPassword(dataSourceProperties.getPassword());
        hikariConfig.setJdbcUrl(dataSourceProperties.getJdbcUrl());
        hikariConfig.setDriverClassName(dataSourceProperties.getDriverClassName());
        hikariConfig.setMaximumPoolSize(5); 
 
        return new HikariDataSource(hikariConfig);
	}

	// sqlSessionFactoryBean
	@Bean(name="sqlSessionFactory2")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("roomDataSource") DataSource roomDataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        //DataSource 주입
        sessionFactory.setDataSource(roomDataSource);
        sessionFactory.setTypeAliasesPackage("com.lime.meeting.model");
        //mapper 주입
        //Resource[] res = new PathMatchingResourcePatternResolver().getResources("classpath:mapper/ewp/**/*.xml");
        Resource[] res = new PathMatchingResourcePatternResolver().getResources("classpath:mapper/tibero/ewp/**/*.xml");
        sessionFactory.setMapperLocations(res);
        sessionFactory.setTypeHandlers(new TypeHandler[] {
        	new SeqMenu.TypeHandler(),
        });
        return sessionFactory.getObject();
    }
}