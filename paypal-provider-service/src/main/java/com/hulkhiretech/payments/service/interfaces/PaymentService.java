package com.hulkhiretech.payments.service.interfaces;



import com.hulkhiretech.payments.pojo.CreateOrderReq;
import com.hulkhiretech.payments.pojo.OrderRes;

public interface PaymentService {

	public OrderRes createOrder(CreateOrderReq req);
	public OrderRes getOrder(String orderId);
	public OrderRes captureOrder(String orderId);
	
	

}
