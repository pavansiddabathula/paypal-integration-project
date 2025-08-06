package com.hulkhire.payments.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hulkhire.payments.DTO.TransactionDTO;
import com.hulkhire.payments.constants.Constants;
import com.hulkhire.payments.constants.ErrorCodeEnum;
import com.hulkhire.payments.constants.PaypalStatusEnum;
import com.hulkhire.payments.constants.TxnStatusEnum;
import com.hulkhire.payments.dao.interfaces.TransactionDAO;
import com.hulkhire.payments.exceptions.ProcessingServiceException;
import com.hulkhire.payments.http.HttpRequest;
import com.hulkhire.payments.http.HttpServiceEngine;
import com.hulkhire.payments.paypal.res.CreatOrderRes;
import com.hulkhire.payments.service.Helper.CaptureOrderHelper;
import com.hulkhire.payments.service.Helper.GetOrderHelper;
import com.hulkhire.payments.service.interfaces.ProviderHandler;
import com.hulkhire.payments.utils.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProviderHanlderImpl implements ProviderHandler {

	private final HttpServiceEngine httpServiceEngine;
	private final GetOrderHelper getOrderHelper;
	private final CaptureOrderHelper captureOrderHelper;
	private final TransactionDAO transactionDAO;
	
	
	public void recontransactionhelper(TransactionDTO txn) {
		String initialTxnStatus = txn.getTxnStatus();
		boolean isThereException = false;
		log.info("ReconAsync.reconTransactionAsync() called for txn: {}", txn);

		try {
			CreatOrderRes successObj = paypalgetOrder(txn.getProviderReference());

			txn.setRetryCount(txn.getRetryCount() + 1);

			PaypalStatusEnum statusEnum = PaypalStatusEnum.fromString(successObj.getStatus());

			switch (statusEnum) {
			case PAYER_ACTION_REQUIRED:
				log.info("ReconAsync.reconTransactionAsync() txn is PENDING, waiting for payment to be done");
				break;

			case APPROVED:
				log.info("ReconAsync.reconTransactionAsync() txn is APPROVED, calling paypalCaptureOrder");
				CreatOrderRes captureResult = paypalCaptureOrder(txn.getProviderReference());
				log.info("capture Results is ", captureResult);

				if (PaypalStatusEnum.COMPLETED.getName().equalsIgnoreCase(captureResult.getStatus())) {
					txn.setTxnStatus(TxnStatusEnum.SUCCESS.getName());
					log.info("ReconAsync.reconTransactionAsync() Capture successful, txn marked SUCCESS");
				} else {
					log.info("ReconAsync.reconTransactionAsync() Capture not completed, txn status unchanged");
				}

				log.info("ReconAsync.reconTransactionAsync() orderId: {}", txn.getProviderReference());
				break;

			case COMPLETED:
				txn.setTxnStatus(TxnStatusEnum.SUCCESS.getName());
				log.info("ReconAsync.reconTransactionAsync() txn is already COMPLETED, marked SUCCESS");
				break;

			default:
				log.warn("ReconAsync.reconTransactionAsync() Unexpected PayPal status: {} Default swtich case", statusEnum);
			}

		} catch (IllegalArgumentException e) {
			log.error("ReconAsync.reconTransactionAsync() - Invalid txn status received: {}", e.getMessage());
			isThereException = true;
		} catch (Exception e) {
			log.error("ReconAsync.reconTransactionAsync() in paypalHanlder- Exception occurred: {}", e.getMessage());
			isThereException = true;
		}

		// if initialTxnStatus is not equal to txn.getTxnStatus(), then call
		// transactionDAO.updateTransactionForRecon()
		if (!initialTxnStatus.equals(txn.getTxnStatus())) {
			log.info("PaypalProviderHandler.reconTransaction() - " + "initialTxnStatus: {}, txn.getTxnStatus(): {}",
					initialTxnStatus, txn.getTxnStatus());
			transactionDAO.updateTransactionForRecon(txn);
			return;
		}

		if (txn.getRetryCount() >= Constants.MAX_RETRY_ATTEMPT && !isThereException) {
			txn.setTxnStatus(TxnStatusEnum.FAILED.getName());
			txn.setErrorCode(ErrorCodeEnum.RECON_PAYMENT_FAILED.getCode());
			txn.setErrorMessage(ErrorCodeEnum.RECON_PAYMENT_FAILED.getMessage());
			log.info("ReconAsync.reconTransactionAsync() Max retries reached, txn marked FAILED");
		}

		//transactionDAO.updateTransactionForRecon(txn);
		//log.info("ReconAsync.reconTransactionAsync() Updated txn in DB: {}", txn);
	}

	

	@Override
	public CreatOrderRes paypalgetOrder(String orderId) {
		log.info("Starting PayPal Get Order for orderId: {}", orderId);
		try {
			HttpRequest httpRequest = getOrderHelper.buildHttpRequest(orderId);
			log.info("HTTP request built for getOrder: {}", httpRequest);

			ResponseEntity<String> orderResponse = httpServiceEngine.makeHttpCall(httpRequest);
			log.info("Received response from PayPal getOrder: {}", orderResponse);

			if (orderResponse.getStatusCode().is4xxClientError()) {
				log.error("4xx Exception from paypal GetOrder method from paypalHanlder", orderResponse.getStatusCode());
				throw new ProcessingServiceException(ErrorCodeEnum.CLIENT_ERROR.getCode(),
						ErrorCodeEnum.CLIENT_ERROR.getMessage(),
						HttpStatus.valueOf(orderResponse.getStatusCode().value()));
			}
			if (orderResponse.getStatusCode().is5xxServerError()) {
				log.error("5xx Exception from paypal Getordermethod from paypalHanlder", orderResponse.getStatusCode());
				throw new ProcessingServiceException(ErrorCodeEnum.UNABLE_TO_CONNECT_PAYPAL.getCode(),
						ErrorCodeEnum.UNABLE_TO_CONNECT_PAYPAL.getMessage(),
						HttpStatus.valueOf(orderResponse.getStatusCode().value()));
			}

			String responseBody = orderResponse.getBody();
			log.info("Response body from getOrder: {}", responseBody);

			CreatOrderRes paypalRes = JsonUtil.fromJson(responseBody, CreatOrderRes.class);
			log.info("Converted PayPal getOrder response: {}", paypalRes);

			getOrderHelper.validatePaypalResponse(paypalRes);
			getOrderHelper.buildOrderResponse(paypalRes);

			return paypalRes;

		} catch (Exception e) {
			log.error("Unknown Exception in paypalgetOrder: {}", e.getMessage());
			throw e;
		} finally {
			log.info("paypalgetOrder method completed");
		}
	}

	@Override
	public CreatOrderRes paypalCaptureOrder(String orderId) {
		log.info("Starting PayPal Capture Order for orderId: {}", orderId);
		try {
			HttpRequest httpRequest = captureOrderHelper.buildHttpRequest(orderId);
			log.info("HTTP request built for captureOrder: {}", httpRequest);

			ResponseEntity<String> captureResponse = httpServiceEngine.makeHttpCall(httpRequest);
			log.info("Received response from PayPal captureOrder: {}", captureResponse);

			if (captureResponse.getStatusCode().is4xxClientError()) {
				log.error("4xx Exception from paypal capturemethod from paypalHanlder", captureResponse.getStatusCode());
				throw new ProcessingServiceException(ErrorCodeEnum.CLIENT_ERROR.getCode(),
						ErrorCodeEnum.CLIENT_ERROR.getMessage(),
						HttpStatus.valueOf(captureResponse.getStatusCode().value()));
			}

			if (captureResponse.getStatusCode().is5xxServerError()) {
				log.info("5xx Exception from paypal capturemethod from paypalHanlder ", captureResponse.getStatusCode());
				throw new ProcessingServiceException(ErrorCodeEnum.UNABLE_TO_CONNECT_PAYPAL.getCode(),
						ErrorCodeEnum.UNABLE_TO_CONNECT_PAYPAL.getMessage(),
						HttpStatus.valueOf(captureResponse.getStatusCode().value()));
			}

			String responseBody = captureResponse.getBody();
			log.info("Response body from captureOrder: {}", responseBody);

			captureOrderHelper.validateResponseBody(responseBody);
			CreatOrderRes paypalRes = captureOrderHelper.parsePaypalResponse(responseBody);
			captureOrderHelper.validatePaypalResponse(paypalRes);
			captureOrderHelper.buildOrderResponse(paypalRes);

			return paypalRes;

		} catch (Exception e) {
			log.error("Unknown Exception in paypalCaptureOrder: {}", e.getMessage());
			throw e;
		} finally {
			log.info("paypalCaptureOrder method completed");
		}
	}
}
