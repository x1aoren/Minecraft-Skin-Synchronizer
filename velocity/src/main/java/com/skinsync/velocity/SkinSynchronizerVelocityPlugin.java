package com.skinsync.velocity;

import com.skinsync.common.SkinCache;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.GameProfileRequestEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.google.inject.Inject;

import java.io.File;

@Plugin(id = "skinsynchronizer", name = "SkinSynchronizer", version = "1.0", authors = {"YourName"})
public class SkinSynchronizerVelocityPlugin {
    private final ProxyServer server;
    private final SkinCache skinCache;

    @Inject
    public SkinSynchronizerVelocityPlugin(ProxyServer server) {
        this.server = server;
        this.skinCache = new SkinCache(new File("plugins/SkinSynchronizer/skin-cache.dat"));
        server.getEventManager().register(this, this);
    }

    public SkinCache getSkinCache() {
        return skinCache;
    }

    @Subscribe
    public void onGameProfileRequest(GameProfileRequestEvent event) {
        server.getScheduler().buildTask(this, () -> SkinLoginListener.handle(event, this)).schedule();
    }
} 