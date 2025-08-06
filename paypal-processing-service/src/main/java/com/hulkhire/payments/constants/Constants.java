package com.hulkhire.payments.constants;


public class Constants {
	private Constants() {
		// Prevent instantiation
	}

	public static final String CLIENT_CREDENTIALS = "client_credentials";
	public static final String GRANT_TYPE = "grant_type";

	public static final String INTENT_CAPTURE = "CAPTURE";
	public static final String UA_PAY_NOW = "PAY_NOW";
	public static final String SP_NO_SHIPPING = "NO_SHIPPING";
	public static final String LANDING_PAGE_LOGIN = "LOGIN";
	public static final String PMP_IMMEDIATE_PAYMENT_REQUIRED = "IMMEDIATE_PAYMENT_REQUIRED";
	public static final String EMPTYSTRING = "";
	public static final String getOrderUrl ="http://localhost:8080/v1/paypal/order/checkout/{orderId}";
	public static final Integer  MAX_RETRY_ATTEMPT= 3;


}
