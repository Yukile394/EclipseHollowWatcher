package com.yukile394.eclipsehollowwatcher.client.effect;

import java.util.Random;

/**
 * Holds the client-local state for whatever screen effect the server most
 * recently asked us to play. Ticked down every client tick by
 * EclipseHollowWatcherClient; read by the HUD renderer, the fog callback,
 * and ScreenEffectMixin (camera shake).
 */
public final class ScreenEffectHandler {

    public static final Random RANDOM = new Random();

    private static String activeEffect = "NONE";
    private static int remainingTicks = 0;
    private static int totalTicks = 0;
    private static float intensity = 0.0f;

    private ScreenEffectHandler() {
    }

    public static void startEffect(String effectType, int durationTicks, float effectIntensity) {
        activeEffect = effectType;
        remainingTicks = durationTicks;
        totalTicks = Math.max(1, durationTicks);
        intensity = effectIntensity;
    }

    public static void tick() {
        if (remainingTicks > 0) {
            remainingTicks--;
            if (remainingTicks <= 0) {
                activeEffect = "NONE";
                intensity = 0.0f;
            }
        }
    }

    private static float progressRemaining() {
        if (totalTicks <= 0) return 0f;
        return (float) remainingTicks / (float) totalTicks;
    }

    public static boolean isFogActive() {
        return "FOG".equals(activeEffect) && remainingTicks > 0;
    }

    public static float getFogIntensity() {
        return isFogActive() ? intensity * progressRemaining() : 0f;
    }

    public static boolean isVignetteActive() {
        return "VIGNETTE".equals(activeEffect) && remainingTicks > 0;
    }

    public static float getVignetteAlpha() {
        return isVignetteActive() ? intensity * progressRemaining() : 0f;
    }

    public static float getCurrentShakeIntensity() {
        return "SCREEN_SHAKE".equals(activeEffect) && remainingTicks > 0
                ? intensity * progressRemaining()
                : 0f;
    }

    public static boolean isBlindnessPulseActive() {
        return "BLINDNESS_PULSE".equals(activeEffect) && remainingTicks > 0;
    }
}
