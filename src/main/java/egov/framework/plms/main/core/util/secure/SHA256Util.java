package egov.framework.plms.main.core.util.secure;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.springframework.security.crypto.bcrypt.BCrypt;

import lombok.extern.slf4j.Slf4j;


/**
 * SHA-256 알고리즘을 처리할 클래스
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 */
@Slf4j
public class SHA256Util {

	/**
	 * SHA-256 해시 알고리즘 적용
	 * @param source 원본
	 * @param salt(String) SALT 값
	 * @return
	 */
	public static String getEncrypt(String source, String salt) {
		return getEncrypt(source, salt.getBytes());
	}
	
	/**
	 * SHA-256 해시 알고리즘 적용
	 * @param str 해시 알고리즘을 적용할 문자열
	 * @param salt(byte[]) SALT 값
	 * @return
	 */
	public static String getEncrypt(String str, byte[] salt) {
		
		String result = "";
		
		byte[] source = str.getBytes();
		byte[] digest = new byte[source.length + salt.length];
		
		// digest = [source + salt]
		System.arraycopy(source, 0, digest, 0, source.length);
		System.arraycopy(salt, 0, digest, source.length, salt.length);
		
		try {
			// SHA-256 해시 알고리즘을 제공할 객체
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			// 암호 + 솔트 로 이루어진 byte배열 해시
			md.update(digest);
			// 해시 적용한 값 가져옴
			byte[] hashedDigest = md.digest();
			
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < hashedDigest.length; i++) {
				sb.append(Integer.toString((hashedDigest[i] & 0xFF) + 256, 16).substring(1));
			}
			
			result = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage());
		}
		
		return result;
	}
	
	/**
	 * SALT를 얻어온다.
	 * @return
	 */
	public static String generateBcryptSalt() {
		String salt = BCrypt.gensalt();
		return salt;
	}
	
}
