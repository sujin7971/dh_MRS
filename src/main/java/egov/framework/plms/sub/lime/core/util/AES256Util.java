package egov.framework.plms.sub.lime.core.util;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.codec.binary.Base64;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import egov.framework.plms.main.bean.component.properties.AESKeystoreProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AES-256 알고리즘을 처리할 클래스
 * AES : AES256
 * Secret Key : 32bytes(256bit)
 * 블록 암호 운용 방식(Block Cipher Mode) : CBC
 * Encode : Base64
 * @author mckim
 *
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Profile("lime")
public class AES256Util {
	private final AESKeystoreProperties keystoreProperties;
	/* KEYSTORE */
	/** KEYSTORE 위치 */
	private static String STORE_NAME;
	/** KEYSTORE 비밀번호 */
	private static String STORE_PWD;
	/** KEYSTORE 비밀키 명칭 */
	private static String KEY_ALIAS;
	/** KEYSTORE 비밀키 비밀번호 */
	private static String KEY_PWD;
	/* KEYSTORE */
	
	/* 암호화 요소 */
	/**Initial Vector*/
	private static String IV;
	/**암호화 비밀키*/
	private static Key keySpec;
	/* 암호화 요소 */
	
	@PostConstruct
	public void init() {
		IV = keystoreProperties.getIv();
		STORE_NAME = keystoreProperties.getKeystore().getName();
		STORE_PWD = keystoreProperties.getKeystore().getPwd();
		KEY_ALIAS = keystoreProperties.getKeystore().getAlias();
		KEY_PWD = keystoreProperties.getKeystore().getKeypwd();
		try {
			keySpec = getAESKey();
		}catch(Exception e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * AES256 으로 암호화 한다.
	 * 
	 * @param str 암호화할 문자열
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws GeneralSecurityException
	 * @throws UnsupportedEncodingException
	 */
	public static String encrypt(String str) {
		if(str == null || str.equals("")) {
			return null;
		}
		try {
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(IV.getBytes()));
			byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
			String enStr = new String(Base64.encodeBase64(encrypted));
			return enStr;
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
		
	}

	/**
	 * AES256으로 암호화된 txt 를 복호화한다.
	 * 
	 * @param str 복호화할 문자열
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws GeneralSecurityException
	 * @throws UnsupportedEncodingException
	 */
	public static String decrypt(String str) {
		if(str == null || str.equals("")) {
			return null;
		}
		try {
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(IV.getBytes()));
			byte[] byteStr = Base64.decodeBase64(str.getBytes());
			return new String(c.doFinal(byteStr), "UTF-8");
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
		
	}
	
	/**
	 * 암호화키를 .aeskeystore로부터 가져온다. 
	 * @return
	 * @throws Exception
	 */
	private static Key getAESKey() throws Exception {
		ClassPathResource cpr = new ClassPathResource(STORE_NAME);
        KeyStore keystore = KeyStore.getInstance("JCEKS");
        keystore.load(cpr.getInputStream(), STORE_PWD.toCharArray());
        Key key = keystore.getKey(KEY_ALIAS, KEY_PWD.toCharArray());
        return key;
    }

}
