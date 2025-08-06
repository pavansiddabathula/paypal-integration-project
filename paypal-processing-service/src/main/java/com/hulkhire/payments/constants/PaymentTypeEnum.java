package com.hulkhire.payments.constants;

import lombok.Getter;

@Getter
public enum PaymentTypeEnum {
    SALE(1, "SALE");

    private final int id;
    private final String name;

    PaymentTypeEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static String fromId(Integer id) {
        if (id == null) return null;
        for (PaymentTypeEnum val : values()) {
            if (val.id == id) return val.name;
        }
        return null;
    }
}
