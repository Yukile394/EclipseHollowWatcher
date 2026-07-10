package com.yukile394.eclipsehollowwatcher.entity.ai.goals;

import com.yukile394.eclipsehollowwatcher.entity.HerobrineEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

/**
 * Instead of charging directly at the player like a normal hostile mob,
 * Herobrine keeps a "watching" distance and only closes in when its mood is
 * high enough. This is what produces the "always being watched from just
 * outside comfortable range" feeling rather than a jumpscare rush.
 */
public class StalkPlayerGoal extends Goal {

    private final HerobrineEntity herobrine;
    private PlayerEntity target;
    private int repathCooldown;

    private static final double PREFERRED_DISTANCE_CALM = 14.0;
    private static final double PREFERRED_DISTANCE_ANGRY = 5.0;

    public StalkPlayerGoal(HerobrineEntity herobrine) {
        this.herobrine = herobrine;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (herobrine.getTrackedPlayerId() == null) return false;
        PlayerEntity player = herobrine.getWorld().getPlayerByUuid(herobrine.getTrackedPlayerId());
        if (player == null || !player.isAlive()) return false;
        this.target = player;
        return herobrine.squaredDistanceTo(player) < 6400; // within 80 blocks
    }

    @Override
    public boolean shouldContinue() {
        return target != null && target.isAlive()
                && herobrine.squaredDistanceTo(target) < 10000;
    }

    @Override
    public void tick() {
        herobrine.getLookControl().lookAt(target, 30.0f, 30.0f);

        double moodLevel = herobrine.getMood().getLevel();
        double preferredDistance = PREFERRED_DISTANCE_CALM
                - (PREFERRED_DISTANCE_CALM - PREFERRED_DISTANCE_ANGRY) * (moodLevel / 4.0);

        double currentDistance = herobrine.distanceTo(target);

        if (repathCooldown-- > 0) return;
        repathCooldown = 10;

        if (currentDistance > preferredDistance + 2.0) {
            herobrine.getNavigation().startMovingTo(target, 1.0 + moodLevel * 0.05);
        } else if (currentDistance < preferredDistance - 3.0) {
            // Too close for comfort at low mood: back off a step to stay "just out of reach".
            Vec3d away = herobrine.getPos().subtract(target.getPos()).normalize();
            Vec3d retreat = herobrine.getPos().add(away.multiply(4));
            herobrine.getNavigation().startMovingTo(retreat.x, retreat.y, retreat.z, 1.0);
        } else {
            herobrine.getNavigation().stop();
        }
    }

    @Override
    public void stop() {
        this.target = null;
        herobrine.getNavigation().stop();
    }
}
