package egov.framework.plms.main.bean.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


/**
 * 스프링 설정파일 application.yml의 암호화된 변수를 복화하 하기 위한 설정 컴포넌트. jasypt.encryptor.password 값을 복호화 키로 하여 진행한다<br>
 * ENC()로 쌓여있는 값을 복호화한다
 * 
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 */
@Slf4j
@Configuration
@ConfigurationProperties("jasypt.encryptor")
@Profile("lime")
public class JasyptConfig {
	@Setter
	private String password;

    @Bean(name = "jasyptVariableEncryptor")
    public StringEncryptor stringEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(password); // 암호화할 때 사용하는 키
        config.setAlgorithm("PBEWithMD5AndDES"); // 암호화 알고리즘
        config.setKeyObtentionIterations("1000"); // 반복할 해싱 회수
        config.setPoolSize("1"); // 인스턴스 pool
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator"); // salt 생성 클래스
        config.setStringOutputType("base64"); //인코딩 방식
        encryptor.setConfig(config);
        return encryptor;
    }
    
}