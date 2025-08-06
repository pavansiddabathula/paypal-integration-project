package com.hulkhire.payments.service.Helper;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.hulkhire.payments.constants.Constants;
import com.hulkhire.payments.constants.ErrorCodeEnum;
import com.hulkhire.payments.exceptions.ProcessingServiceException;
import com.hulkhire.payments.http.HttpRequest;
import com.hulkhire.payments.paypal.res.CreatOrderRes;
import com.hulkhire.payments.paypal.res.Link;
import com.hulkhire.payments.pojo.OrderRes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
@RequiredArgsConstructor
public class GetOrderHelper {
	@Value("${paypal.getOrderUrl}")
	public String urlString;

    public HttpRequest buildHttpRequest(String orderId) {
    //	String getOrderUrl = Constants.getOrderUrl.replace("{orderId}", orderId);
    	String getOrderUrl=urlString.replace("{orderId}", orderId);
    	
        HttpHeaders headerobj = new HttpHeaders();
        headerobj.set("Medata-Type", MediaType.APPLICATION_JSON_VALUE);

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setHttpmethod(HttpMethod.GET);
        httpRequest.setUrl(getOrderUrl);
        httpRequest.setHeaders(headerobj);
        httpRequest.setRequestBody(Constants.EMPTYSTRING);

        return httpRequest;
    }

    public void validateResponseBody(String responseBody) {
        if (responseBody == null) {
            throw new ProcessingServiceException(
                ErrorCodeEnum.EMPTY_PAYPAL_RESPONSE.getCode(),
                ErrorCodeEnum.EMPTY_PAYPAL_RESPONSE.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

   /* public CreatOrderRes parsePaypalResponse(String responseBody) {
    	log.info("ResponseBody inside TokenService class : {}", responseBody);
        CreatOrderRes a=JsonUtil.fromJson(responseBody, CreatOrderRes.class);
        log.info("Converted PayPal response: {}", a);
        return a;
    }*/

    public void validatePaypalResponse(CreatOrderRes paypalRes) {
        if (paypalRes == null ||
            paypalRes.getId() == null || paypalRes.getId().isEmpty() ||
            paypalRes.getStatus() == null || paypalRes.getStatus().isEmpty()) {
        	log.error("Invalid PayPal response: {}", paypalRes);

            throw new ProcessingServiceException(
           
                ErrorCodeEnum.INVALID_PAYPAL_RESPONSE.getCode(),
                ErrorCodeEnum.INVALID_PAYPAL_RESPONSE.getMessage(),
                HttpStatus.BAD_REQUEST
            );
        }
    }

    public OrderRes buildOrderResponse(CreatOrderRes resObj) {
        log.info("Building order response from PayPal response: {}", resObj);
        OrderRes orderRes = new OrderRes();
        orderRes.setOrderId(resObj.getId());
        orderRes.setPaypalStatus(resObj.getStatus());

        // Handle links safely
        Optional<String> redirectUrl = Optional.empty();
        if (resObj.getLinks() != null) {
            redirectUrl = resObj.getLinks().stream()
                .filter(link -> "payer-action".equalsIgnoreCase(link.getRel()))
                .map(Link::getHref)
                .findFirst();
        }
        orderRes.setRedirectUrl(redirectUrl.orElse(null));
        log.info("Builder order method completed and returned");
        return orderRes;
    }

}
