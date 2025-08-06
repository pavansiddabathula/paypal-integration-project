package com.hulkhiretech.payments.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.Exceptions.PaypalProviderException;
import com.hulkhiretech.payments.constants.ErrorCodeEnum;
import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.http.HttpServiceEngine;
import com.hulkhiretech.payments.paypal.res.CreatOrderRes;
import com.hulkhiretech.payments.pojo.CreateOrderReq;
import com.hulkhiretech.payments.pojo.OrderRes;
import com.hulkhiretech.payments.service.TokenService;
import com.hulkhiretech.payments.service.helper.CaptureOrderHelper;
import com.hulkhiretech.payments.service.helper.CreateOrderHelper;
import com.hulkhiretech.payments.service.helper.GetOrderHelper;
import com.hulkhiretech.payments.service.interfaces.PaymentService;
import com.hulkhiretech.payments.utils.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
	private final HttpServiceEngine httpServiceEngine;
    private final GetOrderHelper getOrderHelper;

	private final CreateOrderHelper createOrderHelper;
	private final CaptureOrderHelper captureOrderHelper;
	private final TokenService tokenService;
	public String accessToken = null;

	@Override
	public OrderRes getOrder(String orderId) {
	    log.info("getOrder called | orderId: {}", orderId);

	    // Step 1: Ensure token
	    accessToken = getOrderHelper.ensureAccessToken(accessToken);

	    // Step 2: Prepare HTTP request
	    HttpRequest httpRequest = getOrderHelper.prepareHttpRequestForGetOrder(orderId, accessToken);

	    // Step 3: Make HTTP call
	    ResponseEntity<String> orderResponse = httpServiceEngine.makeHttpCall(httpRequest);

	    // Step 4: Handle errors for 4xx and 5xx
	    HttpStatus status =HttpStatus.valueOf(orderResponse.getStatusCode().value());
	    if (status.is4xxClientError() || status.is5xxServerError()) {
	        throw new PaypalProviderException(
	            status.is4xxClientError() ? ErrorCodeEnum.CLIENT_ERROR.getCode() : ErrorCodeEnum.UNABLE_TO_CONNECT_PAYPAL.getCode(),
	            status.is4xxClientError() ? ErrorCodeEnum.CLIENT_ERROR.getMessage() : ErrorCodeEnum.UNABLE_TO_CONNECT_PAYPAL.getMessage(),
	            status
	        );
	    }

	    // Step 5: Validate and parse response
	    CreatOrderRes resObj = getOrderHelper.validateAndParsePaypalResponse(orderResponse);

	    // Step 6: Convert response to internal format
	    OrderRes orderRes = getOrderHelper.buildOrderResponse(resObj);

	    log.info("getOrder completed | orderRes: {}", orderRes);
	    return orderRes;
	}

	@Override
    public OrderRes captureOrder(String orderId) {
		log.info("Entered capturePaypalOrder method with orderId: {}", orderId);
    	try {
    		accessToken = captureOrderHelper.ensureAccessToken(accessToken);
    		log.info("CaptureOrderHelper: captureOrder | accessToken: {}", accessToken);
    		HttpRequest httpRequest = captureOrderHelper.prepareHttpRequestForCaptureOrder(orderId, accessToken);
    		log.info("CaptureOrderHelper: captureOrder | httpRequest: {}", httpRequest);
    		
    		ResponseEntity<String> captureResponse = httpServiceEngine.makeHttpCall(httpRequest);
    		log.info("CaptureOrderHelper: captureOrder | captureResponse: {}", captureResponse);

    		if (captureResponse.getStatusCode().is4xxClientError()) {
    			log.info("yes its 4xx error it hitted this method ", captureResponse.getStatusCode());
    			throw new PaypalProviderException(
    				ErrorCodeEnum.CLIENT_ERROR.getCode(),
    				ErrorCodeEnum.CLIENT_ERROR.getMessage(),
    				HttpStatus.valueOf(captureResponse.getStatusCode().value())
    			);
    		}

    		if (captureResponse.getStatusCode().is5xxServerError()) {
    			log.info("yes its 5xx error it hitted this method ", captureResponse.getStatusCode());
    			throw new PaypalProviderException(
    				ErrorCodeEnum.UNABLE_TO_CONNECT_PAYPAL.getCode(),
    				ErrorCodeEnum.UNABLE_TO_CONNECT_PAYPAL.getMessage(),
    				HttpStatus.valueOf(captureResponse.getStatusCode().value())
    			);
    		}

    		CreatOrderRes paypalRes = captureOrderHelper.validateAndParsePaypalResponse(captureResponse);
    		log.info("Captured PayPal response: {}", paypalRes);
    		OrderRes orderRes = captureOrderHelper.buildOrderResponse(paypalRes);

    		log.info("Captured PayPal Order Successfully: {}", orderRes);

    		return orderRes;

    	} finally {
    		log.info("capturePaypalOrder method completed");
    	}
    }

	@Override
	public OrderRes createOrder(CreateOrderReq req) {
	    log.info("createOrder called | req: {}", req);

	    String accessToken = tokenService.getToken();

	    HttpRequest httpRequest = createOrderHelper.prepareHttpRequestForCreateOrder(req, accessToken);
	    ResponseEntity<String> orderResponse = httpServiceEngine.makeHttpCall(httpRequest);
	    HttpStatus status =HttpStatus.valueOf(orderResponse.getStatusCode().value());

	    if (!status.is2xxSuccessful()) {
	        throw new PaypalProviderException(
	            ErrorCodeEnum.PAYPal.getCode(),
	            ErrorCodeEnum.PAYPal.getMessage(),
	            status
	        );
	    }

	    String responseBody = orderResponse.getBody();

	    if (responseBody == null || responseBody.isEmpty()) {
	        throw new PaypalProviderException(
	            ErrorCodeEnum.EMPTY_PAYPAL_RESPONSE.getCode(),
	            ErrorCodeEnum.EMPTY_PAYPAL_RESPONSE.getMessage(),
	            HttpStatus.INTERNAL_SERVER_ERROR
	        );
	    }

	    CreatOrderRes resObj = JsonUtil.fromJson(responseBody, CreatOrderRes.class);

	    if (resObj == null || resObj.getId() == null || resObj.getStatus() == null) {
	        throw new PaypalProviderException(
	            ErrorCodeEnum.INVALID_PAYPAL_RESPONSE.getCode(),
	            ErrorCodeEnum.INVALID_PAYPAL_RESPONSE.getMessage(),
	            HttpStatus.BAD_REQUEST
	        );
	    }

	    return createOrderHelper.buildOrderRes(resObj);
	}


}
