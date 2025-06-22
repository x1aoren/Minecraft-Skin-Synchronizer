package com.skinsync.velocity;

import com.skinsync.common.SkinCache;
import com.skinsync.common.SkinData;
import com.skinsync.common.SkinFetcher;
import com.velocitypowered.api.event.player.GameProfileRequestEvent;
import com.velocitypowered.api.util.GameProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SkinLoginListener {
    public static void handle(GameProfileRequestEvent event, SkinSynchronizerVelocityPlugin plugin) {
        UUID uuid = event.getGameProfile().getId();
        SkinCache cache = plugin.getSkinCache();
        SkinData cached = cache.get(uuid);
        if (cached != null) {
            applySkin(event, cached);
            return;
        }
        try {
            SkinData data = plugin.getSkinFetcher().fetch(uuid);
            if (data != null) {
                cache.put(uuid, data);
                applySkin(event, data);
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch skin for " + event.getGameProfile().getName() + ": " + e.getMessage());
        }
    }

    private static void applySkin(GameProfileRequestEvent event, SkinData data) {
        GameProfile profile = event.getGameProfile();
        List<GameProfile.Property> properties = new ArrayList<>(profile.getProperties());
        properties.removeIf(p -> p.getName().equals("textures"));
        properties.add(new GameProfile.Property("textures", data.getValue(), data.getSignature()));
        event.setGameProfile(profile.withProperties(properties));
    }
} 