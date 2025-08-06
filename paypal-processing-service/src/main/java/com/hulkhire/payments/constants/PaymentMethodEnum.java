package com.hulkhire.payments.constants;

import lombok.Getter;

@Getter
public enum PaymentMethodEnum {
    APM(1, "APM");

    private final int id;
    private final String name;

    PaymentMethodEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static String fromId(Integer id) {
        if (id == null) return null;
        for (PaymentMethodEnum val : values()) {
            if (val.id == id) {
                return val.name;
            }
        }
        return null; // or "UNKNOWN_PAYMENT_METHOD" if you prefer
    }
}

