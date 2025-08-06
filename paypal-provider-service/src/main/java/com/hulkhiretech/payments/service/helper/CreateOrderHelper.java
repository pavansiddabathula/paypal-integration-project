package com.hulkhiretech.payments.service.helper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.constants.Constants;
import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.paypal.req.Amount;
import com.hulkhiretech.payments.paypal.req.CreateOrder;
import com.hulkhiretech.payments.paypal.req.ExperienceContext;
import com.hulkhiretech.payments.paypal.req.PaymentSource;
import com.hulkhiretech.payments.paypal.req.Paypal;
import com.hulkhiretech.payments.paypal.req.PurchaseUnit;
import com.hulkhiretech.payments.paypal.res.CreatOrderRes;
import com.hulkhiretech.payments.paypal.res.Link;
import com.hulkhiretech.payments.pojo.CreateOrderReq;
import com.hulkhiretech.payments.pojo.OrderRes;
import com.hulkhiretech.payments.utils.JsonUtil;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class CreateOrderHelper {
	

	
	
	
	public HttpRequest prepareHttpRequestForCreateOrder(CreateOrderReq req, String accessToken) {
		/*
		 * 1.call get AccessToken() API 
		 * 2.Call create order(_createOrder) API 
		 * 3.handle response Success or failure 
		 * 4.return response to controller
		 * 
		 */
    
		log.info("createOrder called|req:{}", req);
	
	



		// header object creation
		HttpHeaders headersobj = new HttpHeaders();
		headersobj.setBearerAuth(accessToken);
		headersobj.setContentType(MediaType.APPLICATION_JSON);
		headersobj.set("PayPal-Request-Id", UUID.randomUUID().toString());
		
		// Create Order object
		Amount amount = new Amount();
		amount.setCurrencyCode(req.getCurrency());
		amount.setValue(req.getAmount());
		
		// Create purchase unit
		PurchaseUnit purchaseUnit = new PurchaseUnit();
		purchaseUnit.setAmount(amount);
		
		// Create experience context
		ExperienceContext experienceContext = new ExperienceContext();
		experienceContext.setPaymentMethodPreference(Constants.PMP_IMMEDIATE_PAYMENT_REQUIRED);
		experienceContext.setLandingPage(Constants.LANDING_PAGE_LOGIN);
		experienceContext.setShippingPreference(Constants.SP_NO_SHIPPING);
		experienceContext.setUserAction(Constants.UA_PAY_NOW);
		experienceContext.setReturnUrl(req.getReturnUrl());
		experienceContext.setCancelUrl(req.getCancelUrl());
		// Create PayPal object
		Paypal paypal = new Paypal();
		paypal.setExperience(experienceContext);
		
		
		// Create payment source
		PaymentSource paymentSource = new PaymentSource();
		paymentSource.setPaypal(paypal);
		
		log.info("paymentSourceeeeeeeeeeeeee:" + paymentSource);
		
		// Create final payment request
		CreateOrder createOrder = new CreateOrder();
		createOrder.setIntent(Constants.INTENT_CAPTURE);
		createOrder.setTnref(req.getTnref());
		log.info("tnref is:" + req.getTnref());
		createOrder.setPurchaseUnits(List.of(purchaseUnit));
		createOrder.setPaymentSource(paymentSource);
		
		// Use Jackson to convert to JSON
		String requestBodyAsJson = JsonUtil.objectToJson(createOrder);
		log.info("requestBodyAsJsonnnnnnnnnnnn:" + requestBodyAsJson);
		
		
		

		// HttpRequest object creation
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setHttpmethod(HttpMethod.POST);
		httpRequest.setUrl(Constants.createOrderUrl);
		httpRequest.setHeaders(headersobj);
		httpRequest.setRequestBody(requestBodyAsJson);
		log.info("httpRequestttttttttttttttt:" + httpRequest);
		
        return httpRequest;
		
	}
	public OrderRes buildOrderRes(CreatOrderRes resObj) {
	    OrderRes orderRes = new OrderRes();

	    orderRes.setOrderId(resObj.getId());
	    orderRes.setPaypalStatus(resObj.getStatus());

	    if (resObj.getLinks() != null) {
	        Optional<String> redirectUrl = resObj.getLinks().stream()
	            .filter(link -> "payer-action".equalsIgnoreCase(link.getRel()))
	            .map(Link::getHref)
	            .findFirst();

	        orderRes.setRedirectUrl(redirectUrl.orElse(null));
	    } else {
	        orderRes.setRedirectUrl(null);
	    }

	    log.info("OrderRes built from PayPal response: {}", orderRes);
	    return orderRes;
	}


	

}
