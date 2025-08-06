package com.hulkhire.payments.paypal.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreatOrderRes {
    private String tnref;

    @JsonProperty("orderId")
    private String id;

    @JsonProperty("paypalStatus")
    private String status;

    @JsonProperty("payment_source")
    private PaymentSource paymentSource;

    private List<Link> links;
}
