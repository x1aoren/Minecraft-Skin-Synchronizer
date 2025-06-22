package com.skinsync.common;

import java.io.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SkinCache {
    private final Map<UUID, CacheEntry> cache = new ConcurrentHashMap<>();
    private final File cacheFile;
    private final long expireAfterMillis;

    public SkinCache(File cacheFile, long expireAfterMinutes) {
        this.cacheFile = cacheFile;
        this.expireAfterMillis = expireAfterMinutes > 0 ? expireAfterMinutes * 60 * 1000 : -1;
        load();
    }

    public SkinData get(UUID uuid) {
        CacheEntry entry = cache.get(uuid);
        if (entry == null) {
            return null;
        }
        if (entry.isExpired(expireAfterMillis)) {
            cache.remove(uuid);
            // Optionally, trigger a background save here if the cache file should be updated frequently.
            return null;
        }
        return entry.getSkinData();
    }

    public void put(UUID uuid, SkinData data) {
        cache.put(uuid, new CacheEntry(data));
        save();
    }

    private void load() {
        if (!cacheFile.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cacheFile))) {
            Object obj = ois.readObject();
            if (obj instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) obj;
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    if (entry.getKey() instanceof UUID) {
                        if (entry.getValue() instanceof CacheEntry) {
                            CacheEntry cacheEntry = (CacheEntry) entry.getValue();
                            if (!cacheEntry.isExpired(expireAfterMillis)) {
                                cache.put((UUID) entry.getKey(), cacheEntry);
                            }
                        } else if (entry.getValue() instanceof SkinData) {
                            // Handle old cache format for backward compatibility
                            CacheEntry newEntry = new CacheEntry((SkinData) entry.getValue());
                            cache.put((UUID) entry.getKey(), newEntry);
                        }
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load skin cache file: " + cacheFile.getPath());
            e.printStackTrace();
        }
    }

    private void save() {
        try {
            File parentDir = cacheFile.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(cacheFile))) {
                oos.writeObject(new ConcurrentHashMap<>(this.cache));
            }
        } catch (IOException e) {
            System.err.println("Failed to save skin cache file: " + cacheFile.getPath());
            e.printStackTrace();
        }
    }
} 