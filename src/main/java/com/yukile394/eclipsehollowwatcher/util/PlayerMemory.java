package com.yukile394.eclipsehollowwatcher.util;

import com.yukile394.eclipsehollowwatcher.entity.ai.HerobrineMood;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;

/**
 * Tracks everything Herobrine "remembers" about a single player: emotional
 * bond (anger score), recently used scare events (so the same thing does not
 * repeat back to back), summon cooldown, and a short rolling log of chat
 * lines used to fake conversational memory.
 */
public class PlayerMemory {

    private static final int HISTORY_SIZE = 6;
    private static final int CHAT_LOG_SIZE = 10;
    private static final long SUMMON_COOLDOWN_MS = 45_000L;

    private final UUID playerId;
    private int angerScore = 0;
    private HerobrineMood mood = HerobrineMood.CALM;
    private long lastSummonMillis = 0L;
    private long lastEncounterMillis = 0L;
    private long lastMoodDecayMillis = System.currentTimeMillis();

    private final Deque<String> recentEvents = new ArrayDeque<>();
    private final Deque<String> recentChatLines = new ArrayDeque<>();

    public PlayerMemory(UUID playerId) {
        this.playerId = playerId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public HerobrineMood getMood() {
        return mood;
    }

    public void registerHostileAction(int severity) {
        angerScore = Math.min(100, angerScore + severity);
        recalculateMood();
        lastEncounterMillis = System.currentTimeMillis();
    }

    public void registerPeacefulTime(long deltaMillis) {
        // Every 90 seconds of no hostility, ease off slightly.
        lastMoodDecayMillis += deltaMillis;
        if (lastMoodDecayMillis >= 90_000L) {
            lastMoodDecayMillis = 0L;
            angerScore = Math.max(0, angerScore - 5);
            recalculateMood();
        }
    }

    private void recalculateMood() {
        int level;
        if (angerScore >= 80) level = HerobrineMood.ENRAGED.getLevel();
        else if (angerScore >= 55) level = HerobrineMood.ANGRY.getLevel();
        else if (angerScore >= 30) level = HerobrineMood.DISTURBED.getLevel();
        else if (angerScore >= 10) level = HerobrineMood.CURIOUS.getLevel();
        else level = HerobrineMood.CALM.getLevel();
        this.mood = HerobrineMood.fromLevel(level);
    }

    public boolean canBeSummonedAgain() {
        return System.currentTimeMillis() - lastSummonMillis >= SUMMON_COOLDOWN_MS;
    }

    public void markSummoned() {
        this.lastSummonMillis = System.currentTimeMillis();
    }

    public void recordEvent(String eventId) {
        if (recentEvents.size() >= HISTORY_SIZE) {
            recentEvents.removeFirst();
        }
        recentEvents.addLast(eventId);
    }

    public boolean wasRecentlyUsed(String eventId) {
        return recentEvents.contains(eventId);
    }

    public void recordChatLine(String line) {
        if (recentChatLines.size() >= CHAT_LOG_SIZE) {
            recentChatLines.removeFirst();
        }
        recentChatLines.addLast(line.toLowerCase());
    }

    public boolean hasRecentlyMentioned(String keyword) {
        return recentChatLines.stream().anyMatch(line -> line.contains(keyword));
    }

    public long getLastEncounterMillis() {
        return lastEncounterMillis;
    }
}
