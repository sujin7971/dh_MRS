package egov.framework.plms.main.core.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;


/**
 * 공통 필요 로직을 모아둔 클래스
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 */
@Slf4j
public class CommUtil {
	/**
	 * <p>
	 * String이 비었거나("") 혹은 null 인지 검증한다.
	 * </p>
	 *
	 * <pre>
	 *  StringUtil.isEmpty(null)      = true
	 *  StringUtil.isEmpty("")        = true
	 *  StringUtil.isEmpty(" ")       = false
	 *  StringUtil.isEmpty("bob")     = false
	 *  StringUtil.isEmpty("  bob  ") = false
	 * </pre>
	 *
	 * @param str - 체크 대상 스트링오브젝트이며 null을 허용함
	 * @return <code>true</code> - 입력받은 String 이 빈 문자열 또는 null인 경우
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}
	
	public static boolean isEmpty(Object obj) {
		if(obj == null) {
			return true;
		}
		if(obj instanceof Collection) {
			return ((Collection) obj).isEmpty();
		}else {
			return obj.equals("");
		}
	}
	
	public static boolean isValidSize(Object obj, Integer size) {
		try {
			if(obj instanceof Collection) {
				return ((Collection) obj).size() <= size;
			}else if(obj instanceof String) {
				return Integer.parseInt(((String) obj)) <= size;
			}else if(obj instanceof CharSequence) {
				return Integer.parseInt(((CharSequence) obj).toString()) <= size;
			}else if(obj instanceof Integer) {
				return ((Integer) obj) <= size;
			}else {
				return false;
			}
		}catch(NumberFormatException e) {
			return false;
		}
	}
	
	public static boolean isValidLength(Object obj, Integer size) {
		if(obj instanceof Collection) {
			return ((Collection) obj).size() <= size;
		}else if(obj instanceof String) {
			return ((String) obj).length() <= size;
		}else if(obj instanceof CharSequence) {
			return ((CharSequence) obj).length() <= size;
		}else if(obj instanceof Integer) {
			return ((Integer) obj).toString().length() <= size;
		}else {
			return false;
		}
	}
	
	public static boolean isValidName(String name) {
		String regex = "^(?=.*[A-Za-zㄱ-ㅎㅏ-ㅣ가-힣])[A-Za-zㄱ-ㅎㅏ-ㅣ가-힣]{2,10}$"; 
		Pattern p = Pattern.compile(regex); 
		Matcher m = p.matcher(name); 
		if(m.matches()) { 
			return true;
		} return false;
	}
	
	public static boolean isValidPassword(String password) {
		String regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[?!@#$%])[A-Za-z\\d?!@#$%]{8,16}$"; 
		Pattern p = Pattern.compile(regex); 
		Matcher m = p.matcher(password); 
		if(m.matches()) { 
			return true;
		} return false;
	}
	
	public static boolean isValidMail(String mail) {
		String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$"; 
		Pattern p = Pattern.compile(regex); 
		Matcher m = p.matcher(mail); 
		if(m.matches()) { 
			return true;
		} return false;
	}
	
	public static boolean isNumeric(String str) {
	    return Pattern.matches("^[0-9]*$", str);
	}
	
	public static String getUUID() {
		UUID fuuid = UUID.randomUUID();
		return fuuid.toString();
	}
	
	public static String getUUID(Integer length) {
		UUID fuuid = UUID.randomUUID();
		String simpleUUID = fuuid.toString().replace("-","").substring(0,length);
		
		return simpleUUID;
	}
	
	public static String getIp(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
		    ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
		    ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
		    ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
		    ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
		    ip = request.getRemoteAddr();
		}
		int index = ip.indexOf(",");
		if (index != -1) {
		    ip = ip.substring(0, index);
		}
		return ip.trim();
    }
	/**
	 * 입력된 문자열을 기본 길이인 20자로 줄입니다.
	 * 만약 입력 문자열의 길이가 20자를 초과하면 초과하는 부분은 '...'로 대체합니다.
	 *
	 * @param input 길이를 줄일 입력 문자열
	 * @return 길이가 줄어든 문자열 또는 원본 문자열
	 */
	public static String truncateString(String input) {
		return truncateString(input, 20);
	}
	/**
	 * 입력된 문자열을 지정된 길이로 줄입니다.
	 * 만약 입력 문자열의 길이가 지정된 길이를 초과하면 초과하는 부분은 '...'로 대체합니다.
	 *
	 * @param input 길이를 줄일 입력 문자열
	 * @param length 문자열을 줄일 길이
	 * @return 길이가 줄어든 문자열 또는 원본 문자열
	 */
	public static String truncateString(String input, Integer length) {
	    if (input.length() > length) {
	        return input.substring(0, length) + "...";
	    } else {
	        return input;
	    }
	}
	/**
	 * 입력된 문자열을 기본 바이트 길이인 60바이트로 줄입니다.
	 * ASCII 문자 (숫자, 알파벳, 일부 특수 문자)는 1바이트로, 그 외의 문자 (한글, 다른 다국어 문자 및 특수 문자)는 2바이트로 계산합니다.
	 *
	 * @param input 바이트 길이를 줄일 입력 문자열
	 * @return 바이트 길이가 줄어든 문자열 또는 원본 문자열
	 */
	public static String truncateStringByBytes(String input) {
	    return truncateStringByBytes(input, 60);
	}

	/**
	 * 입력된 문자열을 지정된 바이트 길이로 줄입니다.
	 * ASCII 문자 (숫자, 알파벳, 일부 특수 문자)는 1바이트로, 그 외의 문자 (한글, 다른 다국어 문자 및 특수 문자)는 2바이트로 계산합니다.
	 *
	 * @param input 바이트 길이를 줄일 입력 문자열
	 * @param byteLength 문자열을 줄일 바이트 길이
	 * @return 바이트 길이가 줄어든 문자열 또는 원본 문자열
	 */
	public static String truncateStringByBytes(String input, int byteLength) {
	    int currentLength = 0;
	    int endIndex = 0;
	    
	    for (int i = 0; i < input.length(); i++) {
	        char ch = input.charAt(i);
	        if (ch < 128) {
	            currentLength += 1; // ASCII 문자 (숫자, 알파벳, 일부 특수 문자)는 1바이트로 계산
	        } else {
	            currentLength += 2; // 그 외의 문자 (한글, 다른 다국어 문자 및 특수 문자)는 2바이트로 계산
	        }
	        
	        if (currentLength > byteLength) {
	            break;
	        }
	        
	        endIndex = i + 1;
	    }
	    
	    if (endIndex < input.length()) {
	        return input.substring(0, endIndex) + "...";
	    } else {
	        return input;
	    }
	}
	
	public static String getBrowserEncodedText(HttpServletRequest request, String text) {
		String encodedFileName = "";
		boolean isMSIE = request.getHeader("user-agent").indexOf("MSIE") != -1
				|| request.getHeader("user-agent").indexOf("Trident") != -1;
		try {
			if (isMSIE) {
					encodedFileName = URLEncoder.encode(text, "utf-8");
				encodedFileName = encodedFileName.replaceAll("\\+", "%20");
			} else {
				encodedFileName = new String(text.getBytes("utf-8"), "ISO-8859-1");
			}
		} catch (UnsupportedEncodingException e) {
			encodedFileName = text;
		}
		return encodedFileName;
	}
	
}
