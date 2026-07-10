package com.yukile394.eclipsehollowwatcher.entity.ai;

/**
 * Represents Herobrine's current emotional state. The mood escalates based on
 * how the tracked player behaves (attacking, insulting via chat, repeatedly
 * summoning, ignoring warnings) and decays slowly back toward CALM over time
 * if the player leaves it alone.
 */
public enum HerobrineMood {
    CALM(0, 1.0f, 1.0f, 1.0f, 1.0f),
    CURIOUS(1, 1.05f, 1.1f, 1.1f, 1.05f),
    DISTURBED(2, 1.3f, 1.25f, 1.3f, 1.4f),
    ANGRY(3, 1.8f, 1.5f, 1.6f, 2.2f),
    ENRAGED(4, 2.3f, 1.85f, 2.1f, 3.0f);

    private final int level;
    private final float heightScale;
    private final float speedMultiplier;
    private final float eyeGlowIntensity;
    private final float armLengthScale;

    HerobrineMood(int level, float heightScale, float speedMultiplier, float eyeGlowIntensity, float armLengthScale) {
        this.level = level;
        this.heightScale = heightScale;
        this.speedMultiplier = speedMultiplier;
        this.eyeGlowIntensity = eyeGlowIntensity;
        this.armLengthScale = armLengthScale;
    }

    public float getArmLengthScale() {
        return armLengthScale;
    }

    public int getLevel() {
        return level;
    }

    public float getHeightScale() {
        return heightScale;
    }

    public float getSpeedMultiplier() {
        return speedMultiplier;
    }

    public float getEyeGlowIntensity() {
        return eyeGlowIntensity;
    }

    public static HerobrineMood fromLevel(int level) {
        int clamped = Math.max(CALM.level, Math.min(ENRAGED.level, level));
        for (HerobrineMood mood : values()) {
            if (mood.level == clamped) {
                return mood;
            }
        }
        return CALM;
    }

    public HerobrineMood escalate() {
        return fromLevel(this.level + 1);
    }

    public HerobrineMood deescalate() {
        return fromLevel(this.level - 1);
    }
}
