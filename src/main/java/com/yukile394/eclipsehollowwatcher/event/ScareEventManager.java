package com.yukile394.eclipsehollowwatcher.event;

import com.yukile394.eclipsehollowwatcher.config.EclipseConfig;
import com.yukile394.eclipsehollowwatcher.entity.ai.HerobrineBrain;
import com.yukile394.eclipsehollowwatcher.entity.ai.HerobrineMood;
import com.yukile394.eclipsehollowwatcher.network.ModNetworking;
import com.yukile394.eclipsehollowwatcher.sound.ModSounds;
import com.yukile394.eclipsehollowwatcher.util.PlayerMemory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Ticks once per server tick per player. Rolls a random chance to fire an
 * ambient/scare event, weighted by the player's current tracked mood and
 * excluding whatever was used most recently so nothing feels repetitive.
 */
public final class ScareEventManager {

    private static final Random RANDOM = new Random();
    private static final Map<UUID, Integer> TICKS_UNTIL_NEXT_ROLL = new ConcurrentHashMap<>();

    private ScareEventManager() {
    }

    public static void tick(ServerPlayerEntity player) {
        UUID id = player.getUuid();
        PlayerMemory memory = HerobrineBrain.getOrCreate(id);
        memory.registerPeacefulTime(50L); // 50ms per tick

        int remaining = TICKS_UNTIL_NEXT_ROLL.getOrDefault(id, rollNextInterval());
        remaining--;
        if (remaining <= 0) {
            attemptEvent(player, memory);
            remaining = rollNextInterval();
        }
        TICKS_UNTIL_NEXT_ROLL.put(id, remaining);
    }

    private static int rollNextInterval() {
        // Roughly every 30-140 seconds (600-2800 ticks), tunable via config.
        int min = EclipseConfig.get().minEventIntervalTicks;
        int max = EclipseConfig.get().maxEventIntervalTicks;
        return min + RANDOM.nextInt(Math.max(1, max - min));
    }

    private static void attemptEvent(ServerPlayerEntity player, PlayerMemory memory) {
        if (RANDOM.nextFloat() > EclipseConfig.get().eventTriggerChance) {
            return; // Sometimes: nothing happens at all.
        }

        ScareEvent chosen = pickWeightedEvent(memory);
        if (chosen == null) {
            return;
        }

        memory.recordEvent(chosen.name());
        dispatch(player, chosen, memory);
    }

    private static ScareEvent pickWeightedEvent(PlayerMemory memory) {
        int moodLevel = memory.getMood().getLevel();
        Map<ScareEvent, Integer> eligible = new EnumMap<>(ScareEvent.class);
        int total = 0;
        for (ScareEvent event : ScareEvent.values()) {
            if (event.getMinimumMoodLevel() > moodLevel) continue;
            if (memory.wasRecentlyUsed(event.name())) continue; // avoid repeats
            eligible.put(event, event.getBaseWeight());
            total += event.getBaseWeight();
        }
        if (eligible.isEmpty() || total <= 0) {
            return null;
        }
        int roll = RANDOM.nextInt(total);
        int cumulative = 0;
        for (Map.Entry<ScareEvent, Integer> entry : eligible.entrySet()) {
            cumulative += entry.getValue();
            if (roll < cumulative) {
                return entry.getKey();
            }
        }
        return null;
    }

    private static void dispatch(ServerPlayerEntity player, ScareEvent event, PlayerMemory memory) {
        World world = player.getWorld();
        switch (event) {
            case SILENCE_ONLY -> ModNetworking.sendAmbientCue(player, "eclipsehollowwatcher:sudden_silence", 1.0f, 1.0f);
            case DISTANT_WHISPER -> ModNetworking.sendAmbientCue(player, "eclipsehollowwatcher:whisper_1", 0.6f, randomPitch());
            case FOOTSTEPS_BEHIND -> {
                Vec3d behindPos = player.getPos().add(offsetBehind(player));
                world.playSound(null, behindPos.x, behindPos.y, behindPos.z,
                        ModSounds.FOOTSTEP_HEAVY, SoundCategory.HOSTILE, 0.8f, randomPitch());
            }
            case STATIC_RADIO -> ModNetworking.sendAmbientCue(player, "eclipsehollowwatcher:static_radio", 0.5f, 1.0f);
            case HEARTBEAT_PULSE -> ModNetworking.sendAmbientCue(player, "eclipsehollowwatcher:heartbeat_slow", 0.7f, 1.0f);
            case EYES_IN_DARKNESS -> spawnDistantEyes(player, false);
            case WINDOW_PEEK -> spawnDistantEyes(player, false);
            case MOUNTAIN_SILHOUETTE -> spawnDistantEyes(player, false);
            case FOG_CREEP -> ModNetworking.sendScreenEffect(player, "FOG", 200, 0.6f);
            case EYES_APPROACH -> spawnDistantEyes(player, true);
            case VANISH_ON_TURN -> ModNetworking.sendScreenEffect(player, "BLINDNESS_PULSE", 20, 0.3f);
            case FULL_MANIFEST -> triggerFullManifestation(player, memory);
        }
    }

    private static void spawnDistantEyes(ServerPlayerEntity player, boolean approaching) {
        Vec3d look = player.getRotationVec(1.0f);
        double distance = 18 + RANDOM.nextInt(20);
        double angleOffset = (RANDOM.nextDouble() - 0.5) * Math.PI * 0.9;
        double angle = Math.atan2(look.z, look.x) + angleOffset;
        double x = player.getX() + Math.cos(angle) * distance;
        double z = player.getZ() + Math.sin(angle) * distance;
        double y = player.getY() + RANDOM.nextInt(6) - 1;
        ModNetworking.sendDistantEyes(player, x, y, z, approaching ? 140 : 80, approaching);
        if (approaching) {
            ModNetworking.sendAmbientCue(player, "eclipsehollowwatcher:herobrine_growl", 0.8f, 0.9f);
        }
    }

    private static void triggerFullManifestation(ServerPlayerEntity player, PlayerMemory memory) {
        ModNetworking.sendScreenEffect(player, "FOG", 300, 0.85f);
        ModNetworking.sendAmbientCue(player, "eclipsehollowwatcher:herobrine_angry_roar",
                Math.min(1.0f, 0.6f + memory.getMood().getLevel() * 0.1f), 0.85f);
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 100, 0));
        com.yukile394.eclipsehollowwatcher.entity.HerobrineSpawner.manifestNear(player, memory.getMood());
    }

    private static Vec3d offsetBehind(ServerPlayerEntity player) {
        Vec3d back = player.getRotationVec(1.0f).multiply(-1);
        return new Vec3d(MathHelper.clamp(back.x * 4, -4, 4), 0, MathHelper.clamp(back.z * 4, -4, 4));
    }

    private static float randomPitch() {
        return 0.85f + RANDOM.nextFloat() * 0.3f;
    }
}
