package com.hulkhire.payments.service;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.hulkhire.payments.DTO.TransactionDTO;
import com.hulkhire.payments.dao.interfaces.TransactionDAO;
import com.hulkhire.payments.entity.TransactionEntity;
import com.hulkhire.payments.utils.converters.TransactionMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReconService {
	private final TransactionDAO transactionDAO;
	private final ReconAsync reconAsync;
	
	// This class is responsible for handling reconciliation processes
	// It will interact with the DAO layer to fetch and process data
	// It will also handle any business logic related to reconciliation
	//@Scheduled(cron = "0 */1 * * * *")
	public void reconTransactions() {
		log.info("ReconService.recon() called");
		// Add your reconciliation logic here
		
		/*
		 * Get List<TransactionEntity> from DB, for 
		 */
		List<TransactionEntity> txnForRecon = transactionDAO.loadTransactionsForRecon();
		log.info("ReconService.recon() - txnForRecon.size(): {}", txnForRecon.size());

		List<TransactionDTO> dtoList = TransactionMapper.INSTANCE.toDTOList(txnForRecon);
		log.info("ReconService.recon() - dtoList.size(): {}", dtoList.size());
	
		// iterate through each transaction, & perform reconciliation
		dtoList.forEach(txn -> {
			log.info("ReconService.recon() - txn: {}", txn);
			// Call the async method to perform reconciliation
			reconAsync.reconTransactionAsync(txn);
		});
		
		
	}
		
}