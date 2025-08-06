package com.hulkhiretech.payments.paypal.res;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
@JsonIgnoreProperties(ignoreUnknown = true) 
@Data
public class PaymentSource {
	    private PayPal paypal;

}
