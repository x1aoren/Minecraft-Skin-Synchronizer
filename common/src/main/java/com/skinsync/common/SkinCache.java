package com.skinsync.common;

import java.io.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SkinCache {
    private final Map<UUID, SkinData> cache = new ConcurrentHashMap<>();
    private final File cacheFile;

    public SkinCache(File cacheFile) {
        this.cacheFile = cacheFile;
        load();
    }

    public SkinData get(UUID uuid) {
        return cache.get(uuid);
    }

    public void put(UUID uuid, SkinData data) {
        cache.put(uuid, data);
        save();
    }

    private void load() {
        if (!cacheFile.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cacheFile))) {
            Object obj = ois.readObject();
            if (obj instanceof Map) {
                Map<?,?> map = (Map<?,?>) obj;
                for (Map.Entry<?,?> entry : map.entrySet()) {
                    if (entry.getKey() instanceof UUID && entry.getValue() instanceof SkinData) {
                        cache.put((UUID) entry.getKey(), (SkinData) entry.getValue());
                    }
                }
            }
        } catch (Exception ignored) {}
    }

    private void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(cacheFile))) {
            oos.writeObject(cache);
        } catch (Exception ignored) {}
    }
} 