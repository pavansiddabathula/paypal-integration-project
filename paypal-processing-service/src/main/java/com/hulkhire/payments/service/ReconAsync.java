package com.hulkhire.payments.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.hulkhire.payments.DTO.TransactionDTO;
import com.hulkhire.payments.service.impl.ProviderHanlderImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReconAsync {

	private final ProviderHanlderImpl providerHanlderImpl;

	@Async
	public void reconTransactionAsync(TransactionDTO txn) {
		
		if(providerHanlderImpl == null) {
			log.error("ReconAsync.reconTransactionAsyc() - "
                    + "providerHandler is null for txn: {}", txn);
            return;
		}
		providerHanlderImpl.recontransactionhelper(txn);
		
		
	}
		
}
