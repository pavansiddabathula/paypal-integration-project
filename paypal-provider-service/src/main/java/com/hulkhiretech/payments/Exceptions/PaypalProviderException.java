package com.hulkhiretech.payments.Exceptions;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PaypalProviderException extends RuntimeException {
	 

	private static final long serialVersionUID = -9148641235254602675L;
	private final String errorCode;
	private final String errorMessage;
	
	private final HttpStatus httpStatus;
	public PaypalProviderException(String errorCode, String message,HttpStatus httpStatusCode) {
		super(message);
		this.errorCode = errorCode;
		this.errorMessage = message;
		this.httpStatus = httpStatusCode;
	}
	
	  // getter for errorCode
	}
