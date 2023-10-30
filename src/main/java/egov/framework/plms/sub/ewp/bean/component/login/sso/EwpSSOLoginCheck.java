package egov.framework.plms.sub.ewp.bean.component.login.sso;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import egov.framework.plms.sub.ewp.bean.component.properties.SsoProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
@RequiredArgsConstructor
@Profile("ewp")
public class EwpSSOLoginCheck {
	private final SsoProperties ssoProperties;
	private static final String AES_128_COMMON_KEY = "EWP_BANDI_AES_128_COMMON_KEY!@#"; // 공통 키
	private static final int VALID_TIME = 300;	// reqeust 유효시간. 기본값은 60이지만 동서발전 서버상황에 맞춰 300으로 조정서 
	
	public String createRequestData(HttpServletRequest request) throws Exception {
		long create_date = System.currentTimeMillis();
		String random_str = getRandomStr();
		String user_access_ip = request.getRemoteAddr();
		String requestData = user_access_ip+"^"+ssoProperties.getClientId()+create_date+random_str;
		
		// create request data
		String encData = AES128encrypt(requestData, AES_128_COMMON_KEY);
		
		// 세션에 random_str을 담아둬야 함
		HttpSession session = request.getSession();
		session.setAttribute("request_random_str", random_str);
		// 요청 시간을 가지고 유효성 검증을 위해 create_date 세션에 저장
		session.setAttribute("request_create_date", String.valueOf(create_date));
		
		return encData;
	}
	
	public String getId(HttpServletRequest request, String encData) throws Exception {
		HttpSession session = request.getSession();
		String current_user_ip = request.getRemoteAddr();
		log.info("current_user_ip: {}", current_user_ip);
		String request_random_str = (String) session.getAttribute("request_random_str");
		log.info("request_random_str: {}", request_random_str);
		String response_random_str = encData.substring(0, 8);
		log.info("response_random_str: {}", response_random_str);
		String encStr = encData.substring(8, encData.length());
		log.info("encStr: {}", encStr);
		String responseKey = ssoProperties.getClientSecret()+request_random_str+response_random_str;
		log.info("responseKey: {}", responseKey);
		
		String decStr = AES128decrypt(encStr, responseKey);
		log.info("decStr: {}", decStr);
		String[] parsingDecStr = parsingDecData(decStr);
		log.info("parsingDecStr: {}", parsingDecStr);
		String request_create_date = (String) session.getAttribute("request_create_date");
		log.info("request_create_date: {}", request_create_date);
		if(!checkUserAccessIp(current_user_ip, parsingDecStr[0])) {
			// 아이피가 일치하지 않음 Exception 처리
			throw new Exception("접속 IP가 일치하지 않습니다.");
		}
		
		System.out.println(request_create_date+"=="+parsingDecStr[2]);
		if(!checkCreateTime(request_create_date, parsingDecStr[2], parsingDecStr[3], VALID_TIME)) {
			// 유효시간이 아님 Exception 처리
			throw new Exception("인증시간이 유효하지 않습니다.");
		}
		
		// 정상일 경우이므로
		return parsingDecStr[1];
	}
	
	public boolean checkCreateTime(String request_create_date, String response_request_create_date, String response_create_date, int validTime) throws Exception {
		// request_create_date : SSO 인증서버로 요청시 만든 create_time
		// response_request_create_date : SSO 인증서버에서 요청온 create_time을 그대로 돌려준 값
		// response_create_date : SSO 인증서버에서 응답시 생성한 create_time
		
		// 요청시 시간과 인증서버에서 받은 요청시간이 같은지 확인
		if(!request_create_date.equals(response_request_create_date)) {
			return false;
		}

		// 인증서버에서 응답Data 생성 시간이 유효한지 확인
		return validDate(response_create_date, validTime);
	}

	public boolean validDate(String date, int validTime) throws Exception {
		long currentTimeMillis = System.currentTimeMillis();
		long createTimeMillis = Long.parseLong(date);
		long diffTimeMillis = Math.abs(currentTimeMillis - createTimeMillis);
		long diffSeconds = (diffTimeMillis / 1000);
		if(diffSeconds < validTime) return true;
		return false;
	}

	public boolean checkUserAccessIp(String current_user_ip, String response_user_ip) throws Exception{	
		// response data에 있는 IP와 시스템에 접속한 사용자의 IP를 비교하여 valid check
		current_user_ip = current_user_ip.replaceAll("\\.", "").trim();
		response_user_ip = response_user_ip.replaceAll("\\.", "").trim();
		
		if(!current_user_ip.equals(response_user_ip)) {
			return false;
		}
		return true;
	}

	public String[] parsingDecData(String message) throws Exception {
		String[] result = new String[4];
		int len = message.length();
		
		result[0] = message.substring(0, len-26).split("\\^")[0];
		result[1] = message.substring(0, len-26).split("\\^")[1];
		result[2] = message.substring(len-26, len-13);
		result[3] = message.substring(len-13, len);

		return result;
	}
	
	public String getRandomStr() {
		String result = "";
		Random r = new Random();
		for(int i=0;i<8;i++) {
			result += String.valueOf(r.nextInt(10));
		}
		return result;
	}
	
	public String parsingData(String message) {
		message = message.split("\\:")[1];
		message = message.replaceAll("\"", "");
		message = message.replaceAll("}\\)", "");
		message = message.trim();
		return message;
	}
	
	public String AES128encrypt(String message, String key) throws Exception {	
		// java 정책상 default는 128
		byte[] keyBytes = new byte[16];
        int len= key.getBytes("UTF-8").length;
        
        if (len > keyBytes.length) len = keyBytes.length;
        System.arraycopy(key.getBytes("UTF-8"), 0, keyBytes, 0, len);
		
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
        
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE,keySpec,ivSpec);

        byte[] results = cipher.doFinal(message.getBytes("UTF-8"));
        Base64 encoder = new Base64();
        return encoder.encodeBase64String(results);
	}
	
	public String AES128decrypt(String message, String key) throws Exception {
		// java 정책상 default는 128
		byte[] keyBytes = new byte[16];
        int len= key.getBytes("UTF-8").length;
        
        if (len > keyBytes.length) len = keyBytes.length;
        System.arraycopy(key.getBytes("UTF-8"), 0, keyBytes, 0, len);
		
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
        
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
		
		Base64 base64 = new Base64();
		byte[] result = cipher.doFinal(base64.decodeBase64(message));
		return new String(result, "UTF-8");	
	}
	
	public String httpPost(String params) {
		String result = null;
		params = "data="+params;
		System.out.println("Parameters : "+params);
		
		try{
            URL url = new URL(ssoProperties.getValidationUrl());
            if(url.getProtocol().equals("http")) {
            	HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true); 
                connection.setDoInput(true);
                connection.setUseCaches(false); 
                
                PrintWriter out = new PrintWriter(connection.getOutputStream()); 
                
                out.print(params);
                out.flush();
                out.close();
                
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                String inputLine = "";

                while( (inputLine = in.readLine()) != null ){
                	System.out.println("inputLine = " + inputLine);
                	result = inputLine;
                }
                
                in.close();
            } else {
            	HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true); 
                connection.setDoInput(true);
                connection.setUseCaches(false); 
                
                PrintWriter  out = new PrintWriter(connection.getOutputStream()); 
                
                out.print(params);
                out.flush();
                out.close();
                
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                String inputLine = "";

                while( (inputLine = in.readLine()) != null ){
                	result = inputLine;
                }
                
                in.close();
            }
		}catch( Exception e ){
			result = e.getMessage();
		}

        return result;
	}
}
