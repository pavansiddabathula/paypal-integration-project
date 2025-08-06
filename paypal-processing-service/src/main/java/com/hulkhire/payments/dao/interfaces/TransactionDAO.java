package com.hulkhire.payments.dao.interfaces;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.hulkhire.payments.DTO.TransactionDTO;
import com.hulkhire.payments.entity.TransactionEntity;
@Repository
public  interface TransactionDAO {
	// This is a placeholder for the TransactionDAO interface.
	// The actual implementation will be provided in the future.
	
	public List<TransactionEntity> loadTransactionsForRecon();

	void updateTransactionForRecon(TransactionDTO txn);
}