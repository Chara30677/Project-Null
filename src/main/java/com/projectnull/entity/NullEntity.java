package com.projectnull.entity;

import com.projectnull.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.UUID;

public class NullEntity extends Monster {
    private static final EntityDataAccessor<Optional<UUID>> TARGET_PLAYER =
            SynchedEntityData.defineId(NullEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    private static final int MAX_LIFETIME = 600;
    private static final int VANISH_DISTANCE_SQ = 25;

    private int lifetime;

    public NullEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.noPhysics = false;
        this.setSilent(true);
        this.setNoGravity(false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 999.0)
                .add(Attributes.MOVEMENT_SPEED, 0.12)
                .add(Attributes.FOLLOW_RANGE, 80.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    public void setTargetPlayer(UUID playerId) {
        this.entityData.set(TARGET_PLAYER, Optional.of(playerId));
    }

    public Optional<UUID> getTargetPlayerId() {
        return this.entityData.get(TARGET_PLAYER);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TARGET_PLAYER, Optional.empty());
    }

    @Override
    protected void registerGoals() {
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            return;
        }

        this.lifetime++;
        if (this.lifetime > MAX_LIFETIME) {
            this.vanish();
            return;
        }

        Player target = getTargetPlayer();
        if (target == null) {
            this.discard();
            return;
        }

        if (target.distanceToSqr(this) < VANISH_DISTANCE_SQ) {
            this.vanish();
            return;
        }

        this.getLookControl().setLookAt(target, 360.0F, 360.0F);

        if (isPlayerLookingAtMe(target)) {
            this.setDeltaMovement(Vec3.ZERO);
            if (this.lifetime % 40 == 0) {
                this.vanish();
            }
            return;
        }

        if (this.lifetime % 5 == 0) {
            this.getNavigation().moveTo(target, 0.5);
        }
    }

    private Player getTargetPlayer() {
        return getTargetPlayerId()
                .map(id -> this.level().getPlayerByUUID(id))
                .orElse(null);
    }

    private boolean isPlayerLookingAtMe(Player player) {
        Vec3 look = player.getViewVector(1.0F).normalize();
        Vec3 toNull = this.position().subtract(player.getEyePosition()).normalize();
        return look.dot(toNull) > 0.92;
    }

    private void vanish() {
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.playSound(
                    null,
                    BlockPos.containing(this.position()),
                    SoundEvents.AMBIENT_CAVE.value(),
                    SoundSource.AMBIENT,
                    1.0F,
                    0.5F
            );
        }
        this.discard();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Lifetime", this.lifetime);
        getTargetPlayerId().ifPresent(id -> tag.putUUID("TargetPlayer", id));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.lifetime = tag.getInt("Lifetime");
        if (tag.hasUUID("TargetPlayer")) {
            setTargetPlayer(tag.getUUID("TargetPlayer"));
        }
    }

    public static NullEntity createForPlayer(ServerLevel level, Player player, BlockPos spawnPos) {
        NullEntity entity = ModEntities.NULL.get().create(level);
        if (entity == null) {
            return null;
        }
        entity.moveTo(
                spawnPos.getX() + 0.5,
                spawnPos.getY(),
                spawnPos.getZ() + 0.5,
                level.random.nextFloat() * 360.0F,
                0.0F
        );
        entity.setTargetPlayer(player.getUUID());
        return entity;
    }
}
