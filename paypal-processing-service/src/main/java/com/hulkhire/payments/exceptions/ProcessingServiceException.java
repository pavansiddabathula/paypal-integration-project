package com.hulkhire.payments.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ProcessingServiceException extends RuntimeException {
	 

	private static final long serialVersionUID = -9148641235254602675L;
	private final String errorCode;
	private final String errorMessage;
	
	private final HttpStatus httpStatus;
	public ProcessingServiceException(String errorCode, String message,HttpStatus httpStatus) {
		super(message);
		this.errorCode = errorCode;
		this.errorMessage = message;
		this.httpStatus = httpStatus;
	}
	
	  // getter for errorCode
	}
