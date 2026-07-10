package com.yukile394.eclipsehollowwatcher.entity.ai.goals;

import com.yukile394.eclipsehollowwatcher.entity.HerobrineEntity;
import com.yukile394.eclipsehollowwatcher.network.ModNetworking;
import com.yukile394.eclipsehollowwatcher.sound.ModSounds;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;
import java.util.Random;

/**
 * Checks whether the tracked player is currently looking directly at
 * Herobrine. If not, there is a small per-tick chance it vanishes and
 * reappears somewhere else nearby - the classic "you looked away and now
 * it's gone / somewhere new" beat.
 */
public class RandomVanishGoal extends Goal {

    private static final Random RANDOM = new Random();

    private final HerobrineEntity herobrine;

    public RandomVanishGoal(HerobrineEntity herobrine) {
        this.herobrine = herobrine;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (herobrine.getTrackedPlayerId() == null) return false;
        PlayerEntity player = herobrine.getWorld().getPlayerByUuid(herobrine.getTrackedPlayerId());
        if (player == null) return false;
        return !isPlayerLookingAt(player, herobrine) && RANDOM.nextInt(140) == 0;
    }

    @Override
    public boolean shouldContinue() {
        return false;
    }

    @Override
    public void start() {
        if (!(herobrine.getWorld().getPlayerByUuid(herobrine.getTrackedPlayerId()) instanceof ServerPlayerEntity player)) {
            return;
        }

        ModNetworking.sendAmbientCue(player, "eclipsehollowwatcher:herobrine_teleport", 0.9f, 1.0f);

        double angle = RANDOM.nextDouble() * Math.PI * 2;
        double distance = 14 + RANDOM.nextInt(16);
        double x = player.getX() + Math.cos(angle) * distance;
        double z = player.getZ() + Math.sin(angle) * distance;
        BlockPos surface = herobrine.getWorld()
                .getTopPosition(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING, BlockPos.ofFloored(x, player.getY(), z));

        herobrine.refreshPositionAndAngles(surface.getX() + 0.5, surface.getY(), surface.getZ() + 0.5, 0, 0);
    }

    private boolean isPlayerLookingAt(PlayerEntity player, HerobrineEntity target) {
        Vec3d toTarget = target.getPos().subtract(player.getEyePos()).normalize();
        Vec3d look = player.getRotationVec(1.0f);
        double dot = toTarget.dotProduct(look);
        return dot > 0.85; // roughly within the player's central view cone
    }
}
