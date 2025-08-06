package com.hulkhiretech.payments.pojo;


import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class OrderRes {
	private String tnref;
	private String orderId;
	private String paypalStatus;
	private String redirectUrl;
}
