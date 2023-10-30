package egov.framework.plms.main.core.util.secure;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * BCroyt 알고리즘을 처리할 클래스
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 */
public class BCryptUtil {
	public static String getEncrypt(String source) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		return passwordEncoder.encode(source);
	}
}
