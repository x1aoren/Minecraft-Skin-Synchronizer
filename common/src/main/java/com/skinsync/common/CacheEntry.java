package com.skinsync.common;

import java.io.Serializable;

public class CacheEntry implements Serializable {
    private static final long serialVersionUID = 1L;
    private final SkinData skinData;
    private final long timestamp;

    public CacheEntry(SkinData skinData) {
        this.skinData = skinData;
        this.timestamp = System.currentTimeMillis();
    }

    public SkinData getSkinData() {
        return skinData;
    }

    public boolean isExpired(long expireAfterMillis) {
        if (expireAfterMillis <= 0) {
            return false; // 0 or negative means never expire
        }
        return (System.currentTimeMillis() - timestamp) > expireAfterMillis;
    }
} 