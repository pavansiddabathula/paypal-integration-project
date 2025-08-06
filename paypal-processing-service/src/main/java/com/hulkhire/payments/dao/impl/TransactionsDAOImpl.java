package com.hulkhire.payments.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.hulkhire.payments.DTO.TransactionDTO;
import com.hulkhire.payments.constants.TxnStatusEnum;
import com.hulkhire.payments.dao.interfaces.TransactionDAO;
import com.hulkhire.payments.entity.TransactionEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Repository
@Slf4j
@RequiredArgsConstructor

public class TransactionsDAOImpl implements TransactionDAO {
	// This is a placeholder for the TransactionsDAOImpl class.
	// The actual implementation will be provided in the future.

	private final NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	public List<TransactionEntity> loadTransactionsForRecon() {
		log.info("TransactionDAOImpl.loadTransactionsForRecon() called");
		
		String sql = "SELECT * FROM payments.Transaction " +
				"WHERE txnStatusId IN (:status1, :status2) " +
				"AND retryCount < :retryMax";

		Map<String, Object> params = new HashMap<>();
		params.put("status1", 3);
		params.put("status2", 4);
		params.put("retryMax", 3);

		List<TransactionEntity> txnsForRecon = jdbcTemplate.query(
				sql,
				params,
				BeanPropertyRowMapper.newInstance(TransactionEntity.class)
				);
		
		log.info("TransactionDAOImpl.loadTransactionsForRecon() - "
				+ "txnsForRecon.size(): {}", txnsForRecon.size());
		
		return txnsForRecon;
	}
	
	
	@Override
	public void updateTransactionForRecon(TransactionDTO txn) {
        String sql = "UPDATE payments.`Transaction` SET " +
                     "txnStatusId = :txnStatusId, " +
                     "retryCount = :retryCount, " +
                     "errorCode = :errorCode, " +
                     "errorMessage = :errorMessage " +
                     "WHERE id = :id";

        Map<String, Object> params = new HashMap<>();
        params.put("txnStatusId", TxnStatusEnum.fromName(txn.getTxnStatus()));
        params.put("retryCount", txn.getRetryCount());
        params.put("errorCode", txn.getErrorCode());
        params.put("errorMessage", txn.getErrorMessage());
        params.put("id", txn.getId());

        jdbcTemplate.update(sql, params);
    }
}