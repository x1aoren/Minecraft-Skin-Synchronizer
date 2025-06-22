package com.skinsync.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class SkinFetcher {
    private final int timeout;

    public SkinFetcher(int timeout) {
        this.timeout = timeout;
    }

    public SkinData fetch(UUID uuid) throws Exception {
        URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(timeout);
        connection.setReadTimeout(timeout);
        try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
            JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
            if (obj.has("properties")) {
                for (JsonElement el : obj.getAsJsonArray("properties")) {
                    JsonObject prop = el.getAsJsonObject();
                    if ("textures".equals(prop.get("name").getAsString())) {
                        String value = prop.get("value").getAsString();
                        String sig = prop.has("signature") ? prop.get("signature").getAsString() : null;
                        return new SkinData(value, sig);
                    }
                }
            }
        }
        return null;
    }
} 