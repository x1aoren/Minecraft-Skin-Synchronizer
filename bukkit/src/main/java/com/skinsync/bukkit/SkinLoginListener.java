package com.skinsync.bukkit;

import com.skinsync.common.SkinCache;
import com.skinsync.common.SkinData;
import com.skinsync.common.SkinFetcher;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SkinLoginListener implements Listener {
    private final SkinSynchronizerBukkitPlugin plugin;
    private final SkinFetcher skinFetcher;

    public SkinLoginListener(SkinSynchronizerBukkitPlugin plugin) {
        this.plugin = plugin;
        this.skinFetcher = plugin.getSkinFetcher();
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
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                SkinData data = skinFetcher.fetch(uuid);
                if (data != null) {
                    cache.put(uuid, data);
                    Bukkit.getScheduler().runTask(plugin, () -> applySkin(player, data));
                }
            } catch (Exception e) {
                plugin.getLogger().warning("[SkinSynchronizer] 获取皮肤数据失败: " + e.getMessage());
            }
        });
    }

    private void applySkin(Player player, SkinData data) {
        try {
            PlayerProfile profile = player.getPlayerProfile();
            Set<ProfileProperty> properties = new HashSet<>(profile.getProperties());
            properties.removeIf(p -> p.getName().equals("textures"));
            properties.add(new ProfileProperty("textures", data.getValue(), data.getSignature()));
            profile.setProperties(properties);
            player.setPlayerProfile(profile);
        } catch (Exception e) {
            plugin.getLogger().warning("[SkinSynchronizer] 应用皮肤失败: " + e.getMessage());
        }
    }
} 