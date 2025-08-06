package com.hulkhire.payments.service.Helper;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import com.hulkhire.payments.constants.ErrorCodeEnum;
import com.hulkhire.payments.exceptions.ProcessingServiceException;
import com.hulkhire.payments.http.HttpRequest;
import com.hulkhire.payments.paypal.res.CreatOrderRes;
import com.hulkhire.payments.paypal.res.Link;
import com.hulkhire.payments.pojo.OrderRes;
import com.hulkhire.payments.utils.JsonUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CaptureOrderHelper {

    @Value("${paypal.captureurl}")
    public String captureOrderUrl;

    public HttpRequest buildHttpRequest(String orderId) {
        String finalUrl = captureOrderUrl.replace("{orderId}", orderId);
        log.info("Capture Order URL built: {}", finalUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type", "application/json");

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setHttpmethod(HttpMethod.POST);
        httpRequest.setUrl(finalUrl);
        httpRequest.setHeaders(headers);
        httpRequest.setRequestBody("");

        log.info("HTTP Request built for Capture Order: {}", httpRequest);
        return httpRequest;
    }

    public void validateResponseBody(String responseBody) {
        log.info("Validating response body...");
        if (responseBody == null || responseBody.trim().isEmpty()) {
            log.error("Empty response received from PayPal capture call");
            throw new ProcessingServiceException(
                ErrorCodeEnum.EMPTY_PAYPAL_RESPONSE.getCode(),
                ErrorCodeEnum.EMPTY_PAYPAL_RESPONSE.getMessage(),
                org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
        log.info("Response body validation passed.");
    }

    public CreatOrderRes parsePaypalResponse(String responseBody) {
        log.info("Parsing PayPal capture response...");
        CreatOrderRes res = JsonUtil.fromJson(responseBody, CreatOrderRes.class);
        log.info("Parsed response: {}", res);
        return res;
    }

    public void validatePaypalResponse(CreatOrderRes paypalRes) {
        log.info("Validating parsed PayPal capture response...");
        if (paypalRes == null ||
            paypalRes.getId() == null || paypalRes.getId().isEmpty() ||
            paypalRes.getStatus() == null || paypalRes.getStatus().isEmpty()) {
            log.error("Invalid PayPal capture response: {}", paypalRes);
            throw new ProcessingServiceException(
                ErrorCodeEnum.INVALID_PAYPAL_RESPONSE.getCode(),
                ErrorCodeEnum.INVALID_PAYPAL_RESPONSE.getMessage(),
                org.springframework.http.HttpStatus.BAD_REQUEST
            );
        }
        log.info("PayPal capture response validation passed.");
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
