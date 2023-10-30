package egov.framework.plms.main.bean.component.server;

import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ProxyHelper {
	private final RestTemplate restTemplate;

    public ProxyHelper() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {

        this.restTemplate = createRestTemplate(true);
    }
    
    /**
     * SSL 옵션에 따라 RestTemplate Bean을 생성하여 반환한다.
     * @param ignoreSSL SSL을 무시할지여부. false인경우 대상 호스트의 SSL 인증서가 유효하지 않은 경우 통신불가.
     * @return
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     */
    private RestTemplate createRestTemplate(boolean ignoreSSL) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
    	if(ignoreSSL) {
    		TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
    		 
            SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                    .loadTrustMaterial(null, acceptingTrustStrategy)
                    .build();
     
            SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
     
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLSocketFactory(csf)
                    .build();
     
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setHttpClient(httpClient);
    	    
    	    return new RestTemplate(requestFactory);
    	  }
    	  else {
    	    return new RestTemplate();
    	  }
    }

    /**
     * Without path variables and request body<br>
     * <pre>
     * {@code
     * ResponseEntity<String> response = proxyHelper.call(baseUrl, uri, method, String.class);
     * }
     * </pre>
     * @param <T>
     * @param baseUrl
     * @param uri
     * @param method
     * @param responseType
     * @return
     */
    public <T> T call(String baseUrl, String uri, HttpMethod method, Class<T> responseType) {
        URI targetUri = UriComponentsBuilder.fromUriString(baseUrl)
                .path(uri)
                .build().toUri();
        return restTemplate.exchange(targetUri, method, null, responseType).getBody();
    }

    /**
     * With path variables<br>
     * <pre>
     * {@code
     * Map<String, String> pathVariables = Map.of("id", "19008597");
     * response = proxyHelper.call(baseUrl, uri, method, pathVariables, String.class);
     * }
     * </pre>
     * @param <T>
     * @param baseUrl
     * @param uri
     * @param method
     * @param pathVariables
     * @param responseType
     * @return
     */
    public <T> T call(String baseUrl, String uri, HttpMethod method, Map<String, String> pathVariables, Class<T> responseType) {
        URI targetUri = UriComponentsBuilder.fromUriString(baseUrl)
                .path(uri)
                .buildAndExpand(pathVariables)
                .toUri();
        return restTemplate.exchange(targetUri, method, null, responseType).getBody();
    }
    
    /**
     * With request body<br>
     * <pre>
     * {@code
     * Object requestBody = Map.of("param1", "value1", "param2", "value2");
     * response = proxyHelper.call(baseUrl, uri, method, requestBody, String.class);
     * }
     * </pre>
     * @param <T>
     * @param baseUrl
     * @param uri
     * @param method
     * @param requestBody
     * @param responseType
     * @return
     */
    public <T> T call(String baseUrl, String uri, HttpMethod method, Object requestBody, Class<T> responseType) {
        URI targetUri = UriComponentsBuilder.fromUriString(baseUrl)
                .path(uri)
                .build().toUri();
        return restTemplate.exchange(targetUri, method, new HttpEntity<>(requestBody), responseType).getBody();
    }

    /**
     * With path variables and request body<br>
     * <pre>
     * {@code
     * response = proxyHelper.call(baseUrl, uri, method, pathVariables, requestBody, String.class);
     * }
     * </pre>
     * @param <T>
     * @param baseUrl
     * @param uri
     * @param method
     * @param pathVariables
     * @param requestBody
     * @param responseType
     * @return
     */
    public <T> T call(String baseUrl, String uri, HttpMethod method, Map<String, String> pathVariables, Object requestBody, Class<T> responseType) {
        URI targetUri = UriComponentsBuilder.fromUriString(baseUrl)
                .path(uri)
                .buildAndExpand(pathVariables)
                .toUri();
        return restTemplate.exchange(targetUri, method, new HttpEntity<>(requestBody), responseType).getBody();
    }
}