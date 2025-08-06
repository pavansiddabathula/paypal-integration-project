package com.hulkhire.payments.constants;

import lombok.Getter;

@Getter
public enum TxnStatusEnum {
    CREATED(1, "CREATED"),
    INITIATED(2, "INITIATED"),
    PENDING(3, "PENDING"),
    APPROVED(4, "APPROVED"),
    SUCCESS(5, "SUCCESS"),
    FAILED(6, "FAILED");

    private final int id;
    private final String name;

    TxnStatusEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Existing method: code -> name
    public static String fromId(Integer id) {
        if (id == null) return null;
        for (TxnStatusEnum val : values()) {
            if (val.id == id) return val.name;
        }
        return null;
    }

    // ✅ New method: name/message -> code
    public static Integer fromName(String name) {
        if (name == null) return null;
        for (TxnStatusEnum val : values()) {
            if (val.name.equalsIgnoreCase(name)) return val.id;
        }
        return null;
    }
}
