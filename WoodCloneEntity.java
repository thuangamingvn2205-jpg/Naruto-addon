package com.example.narutomod.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.EnumSet;
import java.util.UUID;

/** Mộc Phân Thân: hitbox 0.6 x 1.8, eyeHeight 1.62 (như NPC/clone của Kageborne). Tồn tại 30 giây. */
public class WoodCloneEntity extends PathfinderMob {
    private UUID ownerUuid;
    private int life = 20 * 30;

    public WoodCloneEntity(EntityType<? extends WoodCloneEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 24.0)
                .add(Attributes.MOVEMENT_SPEED, 0.34)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    public void setOwner(Player owner) {
        this.ownerUuid = owner.getUUID();
    }

    public boolean isOwner(LivingEntity e) {
        return ownerUuid != null && e.getUUID().equals(ownerUuid);
    }

    public Player getOwnerEntity() {
        return ownerUuid == null ? null : this.level().getPlayerByUUID(ownerUuid);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new FloatGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, true));
        goalSelector.addGoal(3, new FollowOwnerGoal(this));
        goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8f));
        goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new AssistOwnerGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Monster.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (--life <= 0 || getOwnerEntity() == null) {
                if (this.level() instanceof ServerLevel sl) {
                    sl.sendParticles(ParticleTypes.POOF, getX(), getY() + 1, getZ(), 15, 0.3, 0.6, 0.3, 0.04);
                }
                this.discard();
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof LivingEntity le && isOwner(le)) return false;
        return super.hurt(source, amount);
    }

    private static class FollowOwnerGoal extends Goal {
        private final WoodCloneEntity clone;

        FollowOwnerGoal(WoodCloneEntity clone) {
            this.clone = clone;
            setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            Player o = clone.getOwnerEntity();
            return o != null && clone.distanceToSqr(o) > 36;
        }

        @Override
        public boolean canContinueToUse() {
            Player o = clone.getOwnerEntity();
            return o != null && clone.distanceToSqr(o) > 9;
        }

        @Override
        public void tick() {
            Player o = clone.getOwnerEntity();
            if (o == null) return;
            clone.getNavigation().moveTo(o, 1.3);
            if (clone.distanceToSqr(o) > 1024) clone.moveTo(o.getX(), o.getY(), o.getZ(), o.getYRot(), 0f);
        }
    }

    private static class AssistOwnerGoal extends Goal {
        private final WoodCloneEntity clone;

        AssistOwnerGoal(WoodCloneEntity clone) {
            this.clone = clone;
            setFlags(EnumSet.of(Goal.Flag.TARGET));
        }

        private LivingEntity wanted() {
            Player o = clone.getOwnerEntity();
            if (o == null) return null;
            LivingEntity t = o.getLastHurtMob() != null ? o.getLastHurtMob() : o.getLastHurtByMob();
            return t != null && t.isAlive() && t != clone && !clone.isOwner(t) ? t : null;
        }

        @Override
        public boolean canUse() {
            return wanted() != null;
        }

        @Override
        public void start() {
            clone.setTarget(wanted());
        }
    }
}
