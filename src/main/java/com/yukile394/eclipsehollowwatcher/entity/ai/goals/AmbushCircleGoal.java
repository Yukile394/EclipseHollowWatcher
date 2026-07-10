package com.yukile394.eclipsehollowwatcher.entity.ai.goals;

import com.yukile394.eclipsehollowwatcher.entity.HerobrineEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;
import java.util.Random;

/**
 * Only active once Herobrine is at least DISTURBED. Tries to path to a point
 * behind the tracked player so that when they turn around, something is
 * suddenly closer than expected.
 */
public class AmbushCircleGoal extends Goal {

    private static final Random RANDOM = new Random();

    private final HerobrineEntity herobrine;
    private int cooldownTicks;

    public AmbushCircleGoal(HerobrineEntity herobrine) {
        this.herobrine = herobrine;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (herobrine.getMood().getLevel() < 2) return false;
        if (cooldownTicks-- > 0) return false;
        if (herobrine.getTrackedPlayerId() == null) return false;
        PlayerEntity player = herobrine.getWorld().getPlayerByUuid(herobrine.getTrackedPlayerId());
        return player != null && player.isAlive() && herobrine.squaredDistanceTo(player) < 2500;
    }

    @Override
    public boolean shouldContinue() {
        return false; // one-shot repositioning, re-evaluated via cooldown
    }

    @Override
    public void start() {
        PlayerEntity player = herobrine.getWorld().getPlayerByUuid(herobrine.getTrackedPlayerId());
        if (player == null) return;

        Vec3d behind = player.getRotationVec(1.0f).multiply(-1).normalize();
        double distance = 3.5 + RANDOM.nextInt(3);
        Vec3d target = player.getPos().add(behind.multiply(distance));

        herobrine.getNavigation().startMovingTo(target.x, target.y, target.z, 1.35);
        cooldownTicks = 100 + RANDOM.nextInt(140);
    }
}
