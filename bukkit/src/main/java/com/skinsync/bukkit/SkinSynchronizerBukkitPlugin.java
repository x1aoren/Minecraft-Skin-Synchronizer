package com.skinsync.bukkit;

import com.skinsync.common.SkinCache;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class SkinSynchronizerBukkitPlugin extends JavaPlugin {
    private static SkinSynchronizerBukkitPlugin instance;
    private SkinCache skinCache;

    @Override
    public void onEnable() {
        instance = this;
        skinCache = new SkinCache(new File(getDataFolder(), "skin-cache.dat"));
        getServer().getPluginManager().registerEvents(new SkinLoginListener(this), this);
    }

    public static SkinSynchronizerBukkitPlugin getInstance() {
        return instance;
    }

    public SkinCache getSkinCache() {
        return skinCache;
    }
} 