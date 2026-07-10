package com.yukile394.eclipsehollowwatcher.entity.ai.goals;

import com.yukile394.eclipsehollowwatcher.entity.HerobrineEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

/**
 * Grants spider-like wall climbing whenever Herobrine's navigation is blocked
 * by a horizontal collision, so it never gets stuck below the player and can
 * "find its own way around obstacles" as requested.
 */
public class ClimbSurfaceGoal extends Goal {

    private final HerobrineEntity herobrine;

    public ClimbSurfaceGoal(HerobrineEntity herobrine) {
        this.herobrine = herobrine;
        this.setControls(EnumSet.of(Control.JUMP, Control.MOVE));
    }

    @Override
    public boolean canStart() {
        return herobrine.horizontalCollision && !herobrine.isOnGround();
    }

    @Override
    public boolean shouldContinue() {
        return herobrine.horizontalCollision;
    }

    @Override
    public void tick() {
        Vec3d velocity = herobrine.getVelocity();
        herobrine.setVelocity(velocity.x, Math.max(velocity.y, 0.22), velocity.z);
        herobrine.fallDistance = 0;
    }
}
