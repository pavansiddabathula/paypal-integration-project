package com.hulkhire.payments.service.interfaces;

import org.springframework.stereotype.Service;

import com.hulkhire.payments.DTO.TransactionDTO;
import com.hulkhire.payments.paypal.res.CreatOrderRes;


@Service
public interface ProviderHandler {
	public CreatOrderRes paypalgetOrder(String orderId);
	public CreatOrderRes paypalCaptureOrder(String orderId);
	public void recontransactionhelper(TransactionDTO txn);
}
