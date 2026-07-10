package com.yukile394.eclipsehollowwatcher.event;

/**
 * Catalogue of the random atmospheric/scare events Herobrine can trigger
 * around a tracked player. Each has a base weight used by
 * {@link ScareEventManager} for weighted random selection, and a minimum
 * mood level required before it becomes eligible (more intense events only
 * happen once Herobrine is sufficiently agitated).
 */
public enum ScareEvent {
    SILENCE_ONLY(30, 0),
    DISTANT_WHISPER(25, 0),
    FOOTSTEPS_BEHIND(20, 0),
    STATIC_RADIO(10, 1),
    HEARTBEAT_PULSE(18, 1),
    EYES_IN_DARKNESS(20, 1),
    WINDOW_PEEK(12, 2),
    MOUNTAIN_SILHOUETTE(10, 2),
    FOG_CREEP(15, 2),
    EYES_APPROACH(12, 3),
    VANISH_ON_TURN(14, 3),
    FULL_MANIFEST(6, 4);

    private final int baseWeight;
    private final int minimumMoodLevel;

    ScareEvent(int baseWeight, int minimumMoodLevel) {
        this.baseWeight = baseWeight;
        this.minimumMoodLevel = minimumMoodLevel;
    }

    public int getBaseWeight() {
        return baseWeight;
    }

    public int getMinimumMoodLevel() {
        return minimumMoodLevel;
    }
}
