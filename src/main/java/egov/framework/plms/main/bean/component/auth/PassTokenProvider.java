package egov.framework.plms.main.bean.component.auth;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import egov.framework.plms.main.core.model.enums.AuthCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

/**
 * 스케줄러가 DB에 갱신한 권한이 아닌 특정 목적을 위해 해당 세션 내에서 사용할 수 있는 권한을 발급해주는 컴포넌트.
 * @author mckim
 * @version 1.1
 * @since 2023. 2. 21
 */
@Slf4j
@Component
public class PassTokenProvider {

    private SecretKey key;

    private long validityInMilliseconds;
    
    @PostConstruct
    private void initSecretKey() {
    	SecureRandom random = new SecureRandom();
    	byte[] keyBytes = new byte[32];
    	random.nextBytes(keyBytes);
    	key = Keys.hmacShaKeyFor(keyBytes);
    	validityInMilliseconds = 5000;
    }

    /**
     * issurer소유의 subject에 대한 토큰을 발급한다.
     * 해당 토큰은 발급시간으로부터 5초 이내로 유효하다.
     * @param issurer 토큰 소유자
     * @param subject 토큰 발급 대상
     * @return
     */
    public String createToken(String issurer, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
        		.setIssuer(issurer)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }
    
    /**
     * issurer소유의 subject에 대한 특정 권한을 가진 토큰을 발급한다.
     * 해당 토큰은 발급시간으로부터 5초 이내로 유효하다.
     * @param issurer 토큰 소유자
     * @param subject 토큰 발급 대상
     * @param authSet 토큰이 가질 권한
     * @return
     */
    public String createToken(String issurer, String subject, Set<AuthCode> authSet) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + validityInMilliseconds);
        return Jwts.builder()
        		.setIssuer(issurer)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("authSet", authSet.stream().map(AuthCode::getCode).collect(Collectors.toSet()))
                .signWith(key)
                .compact();
    }
    
    /**
     * issurer소유의 subject에 대한 특정 권한을 가진 토큰을 발급한다.
     * 해당 토큰은 지정된 시간까지 유효하다.
     * @param issurer 토큰 소유자
     * @param subject 토큰 발급 대상
     * @param timeout 사용 기한
     * @param authSet 토큰이 가질 권한
     * @return
     */
    public String createToken(String issurer, String subject, LocalDateTime timeout, Set<AuthCode> authSet) {
        Instant instant = timeout.toInstant(ZoneOffset.UTC);
        Date expiryDate = Date.from(instant);
        return Jwts.builder()
        		.setIssuer(issurer)
                .setSubject(subject)
                .setIssuedAt(expiryDate)
                .setExpiration(expiryDate)
                .claim("authSet", authSet.stream().map(AuthCode::getCode).collect(Collectors.toSet()))
                .signWith(key)
                .compact();
    }

    /**
     * 소유자가 해당 토큰을 특정 대상에 대해 사용 가능한지 판별한다
     * @param token 판별할 토큰
     * @param issurer 판별을 요청한 요청자
     * @param subject 판별에 대한 요청대상
     * @return
     */
    public boolean validateToken(String token, String issurer, String subject) {
    	Jws<Claims> jws;
        try {
        	jws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        	Claims claims = jws.getBody();
        	String tokenIssure = claims.getIssuer();
        	String tokenSubject = claims.getSubject();
        	log.info("토큰 발행자: {}, 발급대상: {}, 요청자: {}, 요청대상: {}", tokenIssure, tokenSubject, issurer, subject);
        	if(tokenIssure.equals(issurer) && tokenSubject.equals(subject)) {
        		log.info("발급받은 패스 토큰 사용 가능");
        		return true;
        	}
        } catch(io.jsonwebtoken.ExpiredJwtException expiredException) {
        	log.info("패스 토큰 기간 만료");
        } catch (Exception ex) {
        	log.info("사용 불가능한 패스 토큰");
        }
        return false;
    }
    
    /**
     * 소유자가 해당 토큰을 특정 대상에 대해 요청한 권한을 사용 가능한지 판별한다
     * @param token 판별할 토큰
     * @param issurer 판별을 요청한 요청자
     * @param subject 판별에 대한 요청대상
     * @param auth 요청권한
     * @return
     */
    public boolean validateToken(String token, String issurer, String subject, AuthCode auth) {
    	Jws<Claims> jws;
        try {
        	jws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        	Claims claims = jws.getBody();
        	String tokenIssure = claims.getIssuer();
        	String tokenSubject = claims.getSubject();
        	List<String> authSet = (ArrayList<String>) claims.get("authSet");
        	log.info("토큰 발행자: {}, 발급대상: {}, 요청자: {}, 요청대상: {}, 발급권한: {}, 판별권한: {}", tokenIssure, tokenSubject, issurer, subject, authSet, auth);
        	if(tokenIssure.equals(issurer) && tokenSubject.equals(subject) && authSet.contains(auth.getCode())) {
        		log.info("발급받은 패스 토큰 사용 가능");
        		return true;
        	}
        } catch(io.jsonwebtoken.ExpiredJwtException expiredException) {
        	log.info("패스 토큰 기간 만료");
        } catch (Exception ex) {
        	ex.printStackTrace();
        	log.info("사용 불가능한 패스 토큰");
        }
        return false;
    }
    
    /**
     * 소유자가 해당 토큰을 특정 대상에 대해 요청한 권한을 사용 가능한지 판별한다
     * @param token 판별할 토큰
     * @param issurer 판별을 요청한 요청자
     * @param subject 판별에 대한 요청대상
     * @param auth 요청권한
     * @return
     */
    public List<String> getTokenAuthorities(String token, String issurer, String subject) {
    	Jws<Claims> jws;
    	List<String> authSet = new ArrayList<>();
        try {
        	jws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        	Claims claims = jws.getBody();
        	String tokenIssure = claims.getIssuer();
        	String tokenSubject = claims.getSubject();
        	if(tokenIssure.equals(issurer) && tokenSubject.equals(subject)) {
        		log.info("발급받은 패스 토큰 사용 가능");
        		authSet = (ArrayList<String>) claims.get("authSet");
        	}
        } catch(io.jsonwebtoken.ExpiredJwtException expiredException) {
        	log.info("패스 토큰 기간 만료");
        } catch (Exception ex) {
        	ex.printStackTrace();
        	log.info("사용 불가능한 패스 토큰");
        }
        return authSet;
    }
}