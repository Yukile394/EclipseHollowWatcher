package com.yukile394.eclipsehollowwatcher.entity;

import com.yukile394.eclipsehollowwatcher.entity.ai.HerobrineMood;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Random;

public final class HerobrineSpawner {

    private static final Random RANDOM = new Random();

    private HerobrineSpawner() {
    }

    /**
     * Spawns (or relocates an existing) Herobrine entity somewhere in view-ish
     * distance of the player, out of direct line of sight where possible, then
     * assigns it to track that player's mood/memory.
     */
    public static HerobrineEntity manifestNear(ServerPlayerEntity player, HerobrineMood mood) {
        ServerWorld world = (ServerWorld) player.getWorld();

        List<HerobrineEntity> existing = world.getEntitiesByType(
                ModEntities.HEROBRINE,
                player.getBoundingBox().expand(80),
                e -> player.getUuid().equals(e.getTrackedPlayerId())
        );

        HerobrineEntity herobrine;
        if (!existing.isEmpty()) {
            herobrine = existing.get(0);
            herobrine.refreshLifetime();
        } else {
            herobrine = new HerobrineEntity(ModEntities.HEROBRINE, world);
            world.spawnEntity(herobrine);
        }

        BlockPos spawnPos = findManifestPosition(player);
        herobrine.refreshPositionAndAngles(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0, 0);
        herobrine.setTrackedPlayer(player.getUuid());
        herobrine.lookAtEntity(player, 180f, 180f);
        return herobrine;
    }

    private static BlockPos findManifestPosition(ServerPlayerEntity player) {
        Vec3d look = player.getRotationVec(1.0f);
        double distance = 10 + RANDOM.nextInt(10);
        // Prefer a spot roughly in front of the player so it "appears" in view,
        // but offset enough that it is not standing directly on top of them.
        double angleOffset = (RANDOM.nextDouble() - 0.5) * (Math.PI / 3);
        double angle = Math.atan2(look.z, look.x) + angleOffset;
        double x = player.getX() + Math.cos(angle) * distance;
        double z = player.getZ() + Math.sin(angle) * distance;

        ServerWorld world = (ServerWorld) player.getWorld();
        BlockPos base = BlockPos.ofFloored(x, player.getY(), z);
        BlockPos surface = world.getTopPosition(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING, base);
        return surface;
    }
}
