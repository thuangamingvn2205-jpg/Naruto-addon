package com.example.narutomod.jutsu;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public final class Targeting {
    private Targeting() {}

    /** Sinh vật đang bị người chơi nhìn vào (trong tầm range). */
    public static LivingEntity look(ServerPlayer p, double range) {
        Vec3 start = p.getEyePosition();
        Vec3 dir = p.getLookAngle();
        Vec3 end = start.add(dir.scale(range));
        AABB box = p.getBoundingBox().expandTowards(dir.scale(range)).inflate(1.0);
        EntityHitResult r = ProjectileUtil.getEntityHitResult(p, start, end, box,
                e -> e instanceof LivingEntity && e.isAlive() && !e.isSpectator() && e != p, range * range);
        return r != null && r.getEntity() instanceof LivingEntity le ? le : null;
    }
}
