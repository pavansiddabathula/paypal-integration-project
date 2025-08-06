package com.hulkhiretech.payments.constants;


import lombok.Getter;

@Getter
public enum ErrorCodeEnum {
  GENERIC_ERROR("40000", "server time out "),
  RESOURCE_NOT_FOUND("40002", "Given111111 or provided resource not found"),
  PAY("40002", "Paypal request timed out"),
  CLIENT_ERROR("40002", "Paypal request timed out"),
  UNAUTHORIZED_ACCESS("AUTH_001", "Unauthorized access. Please check your credentials or permissions."),
  UNABLE_TO_CONNECT_PAYPAL("40004", "Paypal service is currently unavailable. Please try again later"),
  PAYPal("30000", "PayPal error occurred"),
  EMPTY_PAYPAL_RESPONSE("400000", "Empty response from PayPal."),
  INVALID_PAYPAL_RESPONSE("400001", "Invalid or incomplete PayPal response."),
  UNEXPECTED_ERROR("400002", "Unexpected error occurred")
  // … add more as needed
  ;
  private final String code;
  private final String message;
  ErrorCodeEnum(String code, String message) { 
	  this.code = code; 
	  this.message = message; 

  }
} 