package com.skinsync.bukkit;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.skinsync.common.SkinCache;
import com.skinsync.common.SkinData;
import com.skinsync.common.SkinFetcher;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class SkinLoginListener implements Listener {
    private final SkinSynchronizerBukkitPlugin plugin;

    public SkinLoginListener(SkinSynchronizerBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        SkinCache cache = plugin.getSkinCache();
        SkinData cached = cache.get(uuid);
        if (cached != null) {
            applySkin(player, cached);
            return;
        }
        // 异步获取皮肤
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    SkinData data = SkinFetcher.fetch(uuid);
                    if (data != null) {
                        cache.put(uuid, data);
                        Bukkit.getScheduler().runTask(plugin, () -> applySkin(player, data));
                    }
                } catch (Exception ignored) {}
            }
        }.runTaskAsynchronously(plugin);
    }

    private void applySkin(Player player, SkinData data) {
        try {
            GameProfile profile = ((CraftPlayer) player).getProfile();
            profile.getProperties().removeAll("textures");
            profile.getProperties().put("textures", new Property("textures", data.getValue(), data.getSignature()));
        } catch (Exception ignored) {}
    }
} 