package com.yukile394.eclipsehollowwatcher.event;

import com.yukile394.eclipsehollowwatcher.config.EclipseConfig;
import com.yukile394.eclipsehollowwatcher.entity.HerobrineSpawner;
import com.yukile394.eclipsehollowwatcher.entity.ai.HerobrineBrain;
import com.yukile394.eclipsehollowwatcher.entity.ai.HerobrineMood;
import com.yukile394.eclipsehollowwatcher.network.ModNetworking;
import com.yukile394.eclipsehollowwatcher.util.PlayerMemory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;
import java.util.Locale;
import java.util.Random;

public final class SummonPhraseListener {

    private static final Random RANDOM = new Random();

    // Turkish + English summon phrases. Matching is substring-based on the
    // lowercased message so small variations still trigger.
    private static final List<String> SUMMON_PHRASES = List.of(
            "herobrine",
            "neredesin",
            "cik ortaya", "çık ortaya",
            "gel buraya", "gel."
    );

    private SummonPhraseListener() {
    }

    /**
     * @return true if the chat message should be treated as a summon attempt
     * (used by the main event registration to decide whether to run this
     * logic at all).
     */
    public static boolean matches(String rawMessage) {
        String normalized = rawMessage.toLowerCase(Locale.forLanguageTag("tr-TR"));
        for (String phrase : SUMMON_PHRASES) {
            if (normalized.contains(phrase)) {
                return true;
            }
        }
        return false;
    }

    public static void handle(ServerPlayerEntity player, String rawMessage) {
        PlayerMemory memory = HerobrineBrain.getOrCreate(player.getUuid());
        memory.recordChatLine(rawMessage);

        if (!memory.canBeSummonedAgain()) {
            return; // cooldown: repeated spam-calling does not guarantee a reaction
        }

        if (RANDOM.nextFloat() > EclipseConfig.get().summonPhraseChance) {
            // Sometimes: absolutely nothing happens, which is scarier than something happening every time.
            return;
        }

        memory.markSummoned();
        HerobrineMood mood = memory.getMood();

        String normalized = rawMessage.toLowerCase(Locale.forLanguageTag("tr-TR"));
        if (normalized.contains("neredesin") && RANDOM.nextFloat() < 0.5f) {
            triggerBlindnessAndStare(player, mood);
            return;
        }

        int roll = RANDOM.nextInt(6);

        switch (roll) {
            case 0 -> ModNetworking.sendAmbientCue(player, "eclipsehollowwatcher:whisper_name_call", 0.7f, 1.0f);
            case 1 -> {
                ModNetworking.sendScreenEffect(player, "BLINDNESS_PULSE", 30, 0.4f);
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 30, 0));
            }
            case 2 -> ModNetworking.sendScreenEffect(player, "FOG", 160, 0.5f);
            case 3 -> ModNetworking.sendAmbientCue(player, "eclipsehollowwatcher:heartbeat_slow", 0.6f, 1.0f);
            case 4 -> {
                ModNetworking.sendScreenEffect(player, "FOG", 260, 0.7f);
                ModNetworking.sendAmbientCue(player, "eclipsehollowwatcher:whisper_2", 0.6f, 0.95f);
            }
            case 5 -> HerobrineSpawner.manifestNear(player, mood);
            default -> { /* nothing */ }
        }
    }

    /**
     * "Herobrine, neredesin?" sequence: the whole screen goes black for a
     * few seconds (vanilla Blindness), a heartbeat plays under it, and as
     * the blindness fades out two glowing eyes are already close in front
     * of the player, holding still and "looking" at them for several more
     * seconds before the encounter ends.
     */
    private static void triggerBlindnessAndStare(ServerPlayerEntity player, HerobrineMood mood) {
        int blindnessTicks = 70; // ~3.5s full darkness
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, blindnessTicks, 0, false, false));
        ModNetworking.sendScreenEffect(player, "FOG", blindnessTicks + 100, 0.9f);
        ModNetworking.sendAmbientCue(player, "eclipsehollowwatcher:heartbeat_fast", 0.75f, 0.95f);

        // Place the staring eyes now, close and directly ahead, so that the
        // instant blindness starts lifting the player is already being
        // watched from just a few blocks away.
        var look = player.getRotationVec(1.0f);
        double distance = 5.5;
        double x = player.getX() + look.x * distance;
        double z = player.getZ() + look.z * distance;
        double y = player.getEyeY() - 0.2;
        int staringDuration = blindnessTicks + 100; // stays roughly 5s after blindness fully lifts
        ModNetworking.sendDistantEyes(player, x, y, z, staringDuration, false);
        ModNetworking.sendAmbientCue(player, "eclipsehollowwatcher:herobrine_growl",
                Math.min(1.0f, 0.7f + mood.getLevel() * 0.08f), 0.8f);
    }
}
