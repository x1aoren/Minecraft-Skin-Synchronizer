package com.skinsync.bukkit;

import com.skinsync.common.SkinCache;
import com.skinsync.common.SkinFetcher;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class SkinSynchronizerBukkitPlugin extends JavaPlugin {
    private static SkinSynchronizerBukkitPlugin instance;
    private SkinCache skinCache;
    private SkinFetcher skinFetcher;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig(); // Creates a default config.yml if one doesn't exist
        
        // Load configuration
        String cacheFileName = getConfig().getString("cache-file", "skin-cache.dat");
        int apiTimeout = getConfig().getInt("api-timeout", 5000);
        long cacheExpireMinutes = getConfig().getLong("cache-expire-minutes", 1440);

        // Initialize components
        this.skinCache = new SkinCache(new File(getDataFolder(), cacheFileName), cacheExpireMinutes);
        this.skinFetcher = new SkinFetcher(apiTimeout);
        
        getServer().getPluginManager().registerEvents(new SkinLoginListener(this), this);
        getLogger().info("SkinSynchronizer has been enabled.");
    }

    public static SkinSynchronizerBukkitPlugin getInstance() {
        return instance;
    }

    public SkinCache getSkinCache() {
        return skinCache;
    }

    public SkinFetcher getSkinFetcher() {
        return skinFetcher;
    }
} 