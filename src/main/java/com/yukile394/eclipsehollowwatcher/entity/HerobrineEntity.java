package com.yukile394.eclipsehollowwatcher.entity;

import com.yukile394.eclipsehollowwatcher.entity.ai.HerobrineBrain;
import com.yukile394.eclipsehollowwatcher.entity.ai.HerobrineMood;
import com.yukile394.eclipsehollowwatcher.entity.ai.goals.AmbushCircleGoal;
import com.yukile394.eclipsehollowwatcher.entity.ai.goals.ClimbSurfaceGoal;
import com.yukile394.eclipsehollowwatcher.entity.ai.goals.RandomVanishGoal;
import com.yukile394.eclipsehollowwatcher.entity.ai.goals.StalkPlayerGoal;
import com.yukile394.eclipsehollowwatcher.sound.ModSounds;
import com.yukile394.eclipsehollowwatcher.util.PlayerMemory;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * The Hollow Watcher itself. Deliberately NOT a vanilla zombie/skeleton
 * reskin: it has its own attribute set, its own goal set (climbing, circling
 * behind the player, vanishing/re-appearing), and its own mood-driven scaling
 * that is read every tick from the tracked player's {@link PlayerMemory}.
 */
public class HerobrineEntity extends HostileEntity {

    private static final TrackedData<Integer> MOOD_LEVEL =
            DataTracker.registerData(HerobrineEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> MANIFESTING =
            DataTracker.registerData(HerobrineEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Float> ARM_LENGTH_SCALE =
            DataTracker.registerData(HerobrineEntity.class, TrackedDataHandlerRegistry.FLOAT);

    @Nullable
    private UUID trackedPlayerId;
    private int despawnCountdown = 20 * 25; // manifestations are temporary, ~25s unless refreshed

    public HerobrineEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 0;
        this.setPersistent();
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.MAX_HEALTH, 40.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.28)
                .add(EntityAttributes.FOLLOW_RANGE, 64.0)
                .add(EntityAttributes.ATTACK_DAMAGE, 6.0)
                .add(EntityAttributes.STEP_HEIGHT, 1.2);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new ClimbSurfaceGoal(this));
        this.goalSelector.add(2, new AmbushCircleGoal(this));
        this.goalSelector.add(3, new StalkPlayerGoal(this));
        this.goalSelector.add(4, new RandomVanishGoal(this));
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(MOOD_LEVEL, HerobrineMood.CALM.getLevel());
        builder.add(MANIFESTING, Boolean.TRUE);
        builder.add(ARM_LENGTH_SCALE, 1.0f);
    }

    public void setTrackedPlayer(UUID playerId) {
        this.trackedPlayerId = playerId;
        syncMoodFromMemory();
    }

    @Nullable
    public UUID getTrackedPlayerId() {
        return trackedPlayerId;
    }

    public void syncMoodFromMemory() {
        if (trackedPlayerId == null) return;
        PlayerMemory memory = HerobrineBrain.get(trackedPlayerId);
        if (memory == null) return;
        HerobrineMood mood = memory.getMood();
        this.dataTracker.set(MOOD_LEVEL, mood.getLevel());
        this.dataTracker.set(ARM_LENGTH_SCALE, mood.getArmLengthScale());
        this.setScale(mood.getHeightScale());
        var speedAttr = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.setBaseValue(0.28 * mood.getSpeedMultiplier());
        }
    }

    public HerobrineMood getMood() {
        return HerobrineMood.fromLevel(this.dataTracker.get(MOOD_LEVEL));
    }

    public float getArmLengthScale() {
        return this.dataTracker.get(ARM_LENGTH_SCALE);
    }

    public float getScale() {
        return getMood().getHeightScale();
    }

    @Override
    public void tick() {
        super.tick();
        syncMoodFromMemory();

        if (!this.getWorld().isClient) {
            if (despawnCountdown-- <= 0) {
                this.discard();
            }
        }
    }

    public void refreshLifetime() {
        this.despawnCountdown = 20 * 25;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.random.nextFloat() < 0.5f ? ModSounds.DEEP_BREATH : ModSounds.HEROBRINE_GROWL;
    }

    @Override
    protected SoundEvent getHurtSound(net.minecraft.entity.damage.DamageSource source) {
        return ModSounds.HEROBRINE_ANGRY_ROAR;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.HEROBRINE_ANGRY_ROAR;
    }

    @Override
    public boolean tryAttack(net.minecraft.server.world.ServerWorld world, net.minecraft.entity.Entity target) {
        boolean success = super.tryAttack(world, target);
        if (success && target instanceof LivingEntity livingTarget && trackedPlayerId != null) {
            PlayerMemory memory = HerobrineBrain.getOrCreate(trackedPlayerId);
            memory.registerHostileAction(0); // Herobrine attacking does not raise its own anger.
        }
        return success;
    }

    @Override
    protected void dropEquipment(ServerWorld world) {
        // Deliberately drop nothing; this is not meant to be farmed for loot.
    }

    @Override
    public boolean canPickUpLoot() {
        return false;
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (trackedPlayerId != null) {
            nbt.putUuid("TrackedPlayer", trackedPlayerId);
        }
        nbt.putInt("DespawnCountdown", despawnCountdown);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.containsUuid("TrackedPlayer")) {
            this.trackedPlayerId = nbt.getUuid("TrackedPlayer");
        }
        if (nbt.contains("DespawnCountdown")) {
            this.despawnCountdown = nbt.getInt("DespawnCountdown");
        }
    }
}
