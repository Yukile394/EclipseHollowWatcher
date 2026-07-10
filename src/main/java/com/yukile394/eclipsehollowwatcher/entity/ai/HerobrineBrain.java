package com.yukile394.eclipsehollowwatcher.entity.ai;

import com.yukile394.eclipsehollowwatcher.util.PlayerMemory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton-style registry mapping each player to their own {@link PlayerMemory}.
 * This is what gives the illusion that Herobrine "remembers" a specific player
 * across sessions within the same server run (kept in memory; persisted to
 * NBT by the save system on world save/load, see SaveDataManager).
 */
public final class HerobrineBrain {

    private static final Map<UUID, PlayerMemory> MEMORIES = new ConcurrentHashMap<>();

    private HerobrineBrain() {
    }

    public static PlayerMemory getOrCreate(UUID playerId) {
        return MEMORIES.computeIfAbsent(playerId, PlayerMemory::new);
    }

    public static PlayerMemory get(UUID playerId) {
        return MEMORIES.get(playerId);
    }

    public static void remove(UUID playerId) {
        MEMORIES.remove(playerId);
    }

    public static Map<UUID, PlayerMemory> allMemories() {
        return MEMORIES;
    }

    public static void clearAll() {
        MEMORIES.clear();
    }
}
