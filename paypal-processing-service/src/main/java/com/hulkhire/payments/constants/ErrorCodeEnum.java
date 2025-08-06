package com.hulkhire.payments.constants;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {

  GENERIC_ERROR("30000", "Server timeout occurred"),
  RESOURCE_NOT_FOUND("30001", "Given or provided resource not found"),
  PAY("30002", "PayPal request timed out"),
  CLIENT_ERROR("30003", "Client error while connecting to PayPal"),
  UNAUTHORIZED_ACCESS("30004", "Unauthorized access. Please check your credentials or permissions."),
  UNABLE_TO_CONNECT_PAYPAL("30005", "PayPal service is currently unavailable. Please try again later"),
  PAYPAL("30006", "PayPal error occurred"),
  EMPTY_PAYPAL_RESPONSE("30007", "Empty response from PayPal."),
  INVALID_PAYPAL_RESPONSE("30008", "Invalid or incomplete PayPal response."),
  UNEXPECTED_ERROR("30009", "Unexpected error occurred"),
  RECON_PAYMENT_FAILED("30010", "Recon payment failed due to a processing server error"),
  UNABLE_TO_CONNECT_PAYPAL_PROVIDER("30012", "Unable to connect to PayPal provider. Please try again later.");
;

  private final String code;
  private final String message;

  ErrorCodeEnum(String code, String message) {
    this.code = code;
    this.message = message;
  }
}
