package com.skinsync.common;

import java.io.Serializable;

public class SkinData implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String value;
    private final String signature;

    public SkinData(String value, String signature) {
        this.value = value;
        this.signature = signature;
    }

    public String getValue() {
        return value;
    }

    public String getSignature() {
        return signature;
    }
} 