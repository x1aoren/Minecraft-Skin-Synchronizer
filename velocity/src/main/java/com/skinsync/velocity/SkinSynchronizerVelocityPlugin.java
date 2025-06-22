package com.skinsync.velocity;

import com.google.inject.Inject;
import com.skinsync.common.SkinCache;
import com.skinsync.common.SkinFetcher;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.GameProfileRequestEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Plugin(id = "skinsynchronizer", name = "SkinSynchronizer", version = "1.0", authors = {"x1aoren"})
public class SkinSynchronizerVelocityPlugin {
    private final ProxyServer server;
    private final SkinCache skinCache;
    private final SkinFetcher skinFetcher;
    private final Path dataDirectory;

    @Inject
    public SkinSynchronizerVelocityPlugin(ProxyServer server, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.dataDirectory = dataDirectory;

        CommentedConfigurationNode config = loadConfig();

        String cacheFileName = config.node("cache-file").getString("skin-cache.dat");
        int apiTimeout = config.node("api-timeout").getInt(5000);
        long cacheExpireMinutes = config.node("cache-expire-minutes").getLong(1440);

        this.skinCache = new SkinCache(new File(dataDirectory.toFile(), cacheFileName), cacheExpireMinutes);
        this.skinFetcher = new SkinFetcher(apiTimeout);
        
        server.getEventManager().register(this, this);
    }

    private CommentedConfigurationNode loadConfig() {
        try {
            Path configPath = dataDirectory.resolve("config.yml");
            if (Files.notExists(configPath)) {
                try (InputStream in = getClass().getResourceAsStream("/config.yml")) {
                    Files.createDirectories(dataDirectory);
                    Files.copy(in, configPath);
                }
            }
            YamlConfigurationLoader loader = YamlConfigurationLoader.builder().path(configPath).build();
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return YamlConfigurationLoader.builder().build().createNode();
        }
    }

    public SkinCache getSkinCache() {
        return skinCache;
    }

    public SkinFetcher getSkinFetcher() {
        return skinFetcher;
    }

    @Subscribe
    public void onGameProfileRequest(GameProfileRequestEvent event) {
        server.getScheduler().buildTask(this, () -> SkinLoginListener.handle(event, this)).schedule();
    }
} 