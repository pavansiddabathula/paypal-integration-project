package com.hulkhiretech.payments.service.helper;

import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.Exceptions.PaypalProviderException;
import com.hulkhiretech.payments.constants.Constants;
import com.hulkhiretech.payments.constants.ErrorCodeEnum;
import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.paypal.res.CreatOrderRes;
import com.hulkhiretech.payments.paypal.res.Link;
import com.hulkhiretech.payments.pojo.OrderRes;
import com.hulkhiretech.payments.service.TokenService;
import com.hulkhiretech.payments.utils.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CaptureOrderHelper {

	private final TokenService tokenService;

	/**
	 * Prepares the HTTP request to call PayPal's Capture Order API
	 */
	public HttpRequest prepareHttpRequestForCaptureOrder(String orderId, String accessToken) {
		String captureOrderUrl = Constants.CaptureOrderUrl.replace("{orderId}", orderId);
		log.info("CaptureOrderHelper: prepareHttpRequestForCaptureOrder | orderId: {}", captureOrderUrl);
		log.info("CaptureOrderHelper: prepareHttpRequestForCaptureOrder | captureOrderUrl: {}", captureOrderUrl);

		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);
		headers.setContentType(MediaType.APPLICATION_JSON);
		log.info("CaptureOrderHelper: prepareHttpRequestForCaptureOrder | headers: {}", headers);

		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setUrl(captureOrderUrl);
		httpRequest.setHttpmethod(HttpMethod.POST);
		httpRequest.setHeaders(headers);
		httpRequest.setRequestBody(Constants.EMPTYSTRING);
		log.info("CaptureOrderHelper: prepareHttpRequestForCaptureOrder | httpRequest: {}", httpRequest);

		return httpRequest;
	}

	/**
	 * Retrieves a valid access token if not already available
	 */
	public String ensureAccessToken(String existingToken) {
		log.info("entered into ensureAccessToken method");
		if (existingToken == null || existingToken.isEmpty()) {
			log.info("Access token is null or empty, generating a new one.");
			String token = tokenService.getToken();
			log.info("New access token: {}", token);
			return token;
		}
		log.info("Exist from ensureAccessToken method");
		return existingToken;
		
		
	}

	/**
	 * Validates and parses PayPal's capture order response
	 */
	public CreatOrderRes validateAndParsePaypalResponse(ResponseEntity<String> response) {

		if (!response.getStatusCode().is2xxSuccessful()) {
			log.error("Non-successful response: {}", response.getStatusCode());
			throw new PaypalProviderException(
				ErrorCodeEnum.PAYPal.getCode(),
				ErrorCodeEnum.PAYPal.getMessage(),
				HttpStatus.valueOf(response.getStatusCode().value())
			);
		}

		String body = response.getBody();
		if (body == null) {
			log.error("PayPal returned null body");
			throw new PaypalProviderException(
				ErrorCodeEnum.EMPTY_PAYPAL_RESPONSE.getCode(),
				ErrorCodeEnum.EMPTY_PAYPAL_RESPONSE.getMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR
			);
		}

		CreatOrderRes res = JsonUtil.fromJson(body, CreatOrderRes.class);
		log.info("Parsed PayPal capture response: {}", res);
		if (res == null || res.getId() == null || res.getId().isEmpty()
				|| res.getStatus() == null || res.getStatus().isEmpty()) {
			log.error("Invalid or incomplete PayPal capture response: {}", res);
			throw new PaypalProviderException(
				ErrorCodeEnum.INVALID_PAYPAL_RESPONSE.getCode(),
				ErrorCodeEnum.INVALID_PAYPAL_RESPONSE.getMessage(),
				HttpStatus.BAD_REQUEST
			);
		}

		return res;
	}

	/**
	 * Builds internal order response object
	 */
	public OrderRes buildOrderResponse(CreatOrderRes resObj) {
		OrderRes orderRes = new OrderRes();
		orderRes.setOrderId(resObj.getId());
		orderRes.setPaypalStatus(resObj.getStatus());

		Optional<String> redirectUrl = resObj.getLinks().stream()
				.filter(link -> "payer-action".equalsIgnoreCase(link.getRel()))
				.map(Link::getHref)
				.findFirst();

		orderRes.setRedirectUrl(redirectUrl.orElse(null));
		log.info("CaptureOrderHelper: buildOrderResponse | orderRes: {}", orderRes);
		return orderRes;
	}
}
