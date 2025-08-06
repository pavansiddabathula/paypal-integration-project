package com.hulkhiretech.payments.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    public ErrorResponse() {
		// TODO Auto-generated constructor stub
	}
	private String code;
    private String message;


}
