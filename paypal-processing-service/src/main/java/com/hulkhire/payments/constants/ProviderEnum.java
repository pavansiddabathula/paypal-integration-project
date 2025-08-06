package com.hulkhire.payments.constants;

import lombok.Getter;

@Getter
public enum ProviderEnum {
    PAYPAL(1, "PAYPAL");

    private final int id;
    private final String name;

    ProviderEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static String fromId(Integer id) {
        if (id == null) return null;
        for (ProviderEnum val : values()) {
            if (val.id == id) return val.name;
        }
        return null;
    }
}
