package com.skinsync.velocity;

import com.skinsync.common.SkinCache;
import com.skinsync.common.SkinData;
import com.skinsync.common.SkinFetcher;
import com.velocitypowered.api.event.player.GameProfileRequestEvent;
import com.velocitypowered.api.util.GameProfile;

import java.util.UUID;

public class SkinLoginListener {
    public static void handle(GameProfileRequestEvent event, SkinSynchronizerVelocityPlugin plugin) {
        UUID uuid = event.getUniqueId();
        SkinCache cache = plugin.getSkinCache();
        SkinData cached = cache.get(uuid);
        if (cached != null) {
            applySkin(event, cached);
            return;
        }
        try {
            SkinData data = SkinFetcher.fetch(uuid);
            if (data != null) {
                cache.put(uuid, data);
                applySkin(event, data);
            }
        } catch (Exception ignored) {}
    }

    private static void applySkin(GameProfileRequestEvent event, SkinData data) {
        GameProfile profile = event.getGameProfile();
        profile.getProperties().put("textures", data.getValue(), data.getSignature());
        event.setGameProfile(profile);
    }
} 