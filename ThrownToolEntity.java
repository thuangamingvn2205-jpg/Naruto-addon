package com.example.narutomod.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/** Nền chung cho Shuriken / Kunai ném (hitbox 0.4 x 0.4, eyeHeight 0.13 khai báo ở ModEntities). */
public abstract class ThrownToolEntity extends ThrowableItemProjectile {
    protected ThrownToolEntity(EntityType<? extends ThrownToolEntity> type, Level level) {
        super(type, level);
    }

    protected ThrownToolEntity(EntityType<? extends ThrownToolEntity> type, LivingEntity owner, Level level) {
        super(type, owner, level);
    }

    protected abstract float damageAmount();

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        result.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), damageAmount());
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) this.discard();
    }
}
