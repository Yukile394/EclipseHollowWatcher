package com.yukile394.eclipsehollowwatcher.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Simple JSON-backed config so server owners can tune pacing without
 * recompiling: how often events can fire, how likely a summon phrase is to
 * actually trigger something, whether PvP-based anger escalation is enabled,
 * etc.
 */
public class EclipseConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static EclipseConfig instance;

    public int minEventIntervalTicks = 600;   // 30s
    public int maxEventIntervalTicks = 2800;  // ~140s
    public float eventTriggerChance = 0.65f;  // chance a scheduled roll actually fires something
    public float summonPhraseChance = 0.45f;  // chance a summon phrase gets any response at all
    public int summonCooldownSeconds = 45;
    public boolean angerFromAttackingHerobrine = true;
    public boolean angerFromInsultsInChat = true;
    public int maxAngerPerHostileAction = 15;

    public static EclipseConfig get() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    private static Path configPath() {
        return FabricLoader.getInstance().getConfigDir().resolve("eclipsehollowwatcher.json");
    }

    private static EclipseConfig load() {
        Path path = configPath();
        if (Files.exists(path)) {
            try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                EclipseConfig loaded = GSON.fromJson(reader, EclipseConfig.class);
                if (loaded != null) {
                    return loaded;
                }
            } catch (IOException ignored) {
                // fall through to defaults + resave below
            }
        }
        EclipseConfig fresh = new EclipseConfig();
        fresh.save();
        return fresh;
    }

    public void save() {
        try {
            Files.createDirectories(configPath().getParent());
            try (Writer writer = Files.newBufferedWriter(configPath(), StandardCharsets.UTF_8)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException ignored) {
            // Non-fatal: worst case we just keep using in-memory defaults this session.
        }
    }
}
