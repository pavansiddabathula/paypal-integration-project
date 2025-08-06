package com.hulkhiretech.payments.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hulkhiretech.payments.Exceptions.PaypalProviderException;
import com.hulkhiretech.payments.constants.ErrorCodeEnum;
import com.hulkhiretech.payments.pojo.CreateOrderReq;
import com.hulkhiretech.payments.pojo.OrderRes;
import com.hulkhiretech.payments.service.interfaces.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v1/paypal/order")
@ResponseStatus(org.springframework.http.HttpStatus.CREATED)
public class PaymentController {

	private final PaymentService paymentService;

	@PostMapping()
	public OrderRes creatorder(@RequestBody CreateOrderReq req) {

		log.info("createOrderReqqq:{}", req);
		OrderRes orderres =paymentService.createOrder(req);;
		
		return orderres;
	}
	@GetMapping({"/checkout", "/checkout/{orderId}"}) 
	public OrderRes getOrder(@PathVariable(required = false) String orderId) {
	    log.info("getOrderReqqq:{}", orderId);
	    
	    if(orderId == null || orderId.isEmpty()) {
	        log.error("Order ID is null or empty");
	        throw new PaypalProviderException(ErrorCodeEnum.CLIENT_ERROR.getCode(),
	                ErrorCodeEnum.CLIENT_ERROR.getMessage(),HttpStatus.BAD_REQUEST);
	    }

	    return paymentService.getOrder(orderId);
	}

	@PostMapping("capture/{orderId}")
	public OrderRes captureOrder(@PathVariable String orderId) {
	log.info("captureOrderReqqq:{}", orderId);
		return paymentService.captureOrder(orderId);
	}


}
