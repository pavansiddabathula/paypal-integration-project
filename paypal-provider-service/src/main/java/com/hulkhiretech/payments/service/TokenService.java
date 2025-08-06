package com.hulkhiretech.payments.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hulkhiretech.payments.constants.Constants;
import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.http.HttpServiceEngine;
import com.hulkhiretech.payments.paypal.OAuthToken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class TokenService {
	
	private final HttpServiceEngine httpServiceEngine;
	private final ObjectMapper objectMapper;
	
	@Value("${paypal.clientId}")
	private String clientId;
	@Value("${paypal.clientSecret}")
	private String clientSecret;
	@Value("${paypal.oAuthurl}")
	private String oAuthurl;
	
	
	
	
    public String getToken() {
    	
	     
		
		//log.info("getToken called");
		
		//log.info("url is :" + oAuthurl);
    	
    	
    	//header object creation
        HttpHeaders headersobj = new HttpHeaders();
        headersobj.setBasicAuth(clientId, clientSecret);
        headersobj.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        
        //form data object creation
        MultiValueMap<String, String> formParams = new LinkedMultiValueMap<>();
        formParams.add(Constants.GRANT_TYPE, Constants.CLIENT_CREDENTIALS);
        
        
        //HttpRequest object creation
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setHttpmethod(HttpMethod.POST);
        httpRequest.setUrl(oAuthurl);
        httpRequest.setHeaders(headersobj);
        httpRequest.setRequestBody(formParams);

        ResponseEntity<String> oAuthResponse = httpServiceEngine.makeHttpCall(httpRequest);
        String responseBody = oAuthResponse.getBody();
        log.info("ResponseBody inside TokenService class : " + responseBody);
        
        
        String accessToken = null;
        try {
        	OAuthToken oAuthobj =objectMapper.readValue(responseBody, OAuthToken.class);
        	accessToken = oAuthobj.getAccessToken();
        	log.info("Access Token inside TokenService class : " + accessToken);
        	}
        catch (Exception e) {
			e.printStackTrace();
		}
        log.info("Access Token inside TokenService class before return: " + accessToken);
        return accessToken;

    }
}
