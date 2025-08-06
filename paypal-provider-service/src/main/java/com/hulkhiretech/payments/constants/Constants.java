package com.hulkhiretech.payments.constants;


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
	public static final String createOrderUrl="https://api-m.sandbox.paypal.com/v2/checkout/orders";
	public static final String EMPTYSTRING = "";
	public static final String GetOrderUrl = "https://api-m.sandbox.paypal.com/v2/checkout/orders/{orderId}";

	public static final String CaptureOrderUrl="https://api-m.sandbox.paypal.com/v2/checkout/orders/{orderId}/capture";


}
