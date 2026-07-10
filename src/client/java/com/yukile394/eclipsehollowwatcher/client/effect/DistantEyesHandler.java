package com.yukile394.eclipsehollowwatcher.client.effect;

import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders the "two white eyes in the dark" cue using vanilla particles
 * (end rod particles give a bright, tight white glow that reads well from a
 * distance without needing a custom particle type or texture). If the
 * server marks the cue as "approaching", the eye pair drifts toward the
 * player over its lifetime.
 */
public final class DistantEyesHandler {

    private static final double EYE_SEPARATION = 0.35;

    private static final List<ActiveEyes> ACTIVE = new ArrayList<>();

    private DistantEyesHandler() {
    }

    public static void spawn(double x, double y, double z, int lifetimeTicks, boolean approaching) {
        ACTIVE.add(new ActiveEyes(x, y, z, lifetimeTicks, approaching));
    }

    public static void tick() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;

        ACTIVE.removeIf(eyes -> {
            eyes.remainingTicks--;

            if (eyes.approaching) {
                Vec3d toPlayer = client.player.getPos().subtract(eyes.x, eyes.y, eyes.z).normalize();
                double step = 0.06;
                eyes.x += toPlayer.x * step;
                eyes.y += toPlayer.y * step;
                eyes.z += toPlayer.z * step;
            }

            if (eyes.remainingTicks % 3 == 0) {
                Vec3d right = client.player.getRotationVec(1.0f).crossProduct(new Vec3d(0, 1, 0)).normalize();
                double ex = right.x * EYE_SEPARATION;
                double ez = right.z * EYE_SEPARATION;

                client.world.addParticle(ParticleTypes.END_ROD, eyes.x + ex, eyes.y + 1.6, eyes.z + ez, 0, 0, 0);
                client.world.addParticle(ParticleTypes.END_ROD, eyes.x - ex, eyes.y + 1.6, eyes.z - ez, 0, 0, 0);
            }

            return eyes.remainingTicks <= 0;
        });
    }

    private static final class ActiveEyes {
        double x, y, z;
        int remainingTicks;
        final boolean approaching;

        ActiveEyes(double x, double y, double z, int remainingTicks, boolean approaching) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.remainingTicks = remainingTicks;
            this.approaching = approaching;
        }
    }
}
