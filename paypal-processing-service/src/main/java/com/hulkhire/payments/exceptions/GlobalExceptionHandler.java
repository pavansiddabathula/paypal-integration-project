package com.hulkhire.payments.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.hulkhire.payments.constants.ErrorCodeEnum;
import com.hulkhire.payments.pojo.ErrorResponse;


@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ProcessingServiceException.class)
	public ResponseEntity<ErrorResponse> handlePaypalException(ProcessingServiceException ex) {
		ErrorResponse error = new ErrorResponse(
				ex.getErrorCode(),
				ex.getMessage()
				);
		return new ResponseEntity<>(error, ex.getHttpStatus());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
		ErrorResponse error = new ErrorResponse(
				ErrorCodeEnum.GENERIC_ERROR.getCode(),
				ErrorCodeEnum.GENERIC_ERROR.getMessage()
				);
		// Log actual stacktrace for debugging
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
