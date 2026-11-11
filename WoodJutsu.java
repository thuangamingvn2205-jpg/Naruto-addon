package com.example.narutomod.jutsu;

import com.example.narutomod.registry.ModBlocks;
import com.example.narutomod.registry.ModEntities;
import com.example.narutomod.entity.WoodCloneEntity;
import com.example.narutomod.util.Scheduler;
import com.example.narutomod.util.TempBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** Các nhẫn thuật Mộc Độn (Wood Release). Mọi hàm trả về true nếu thi triển thành công. */
public final class WoodJutsu {
    private WoodJutsu() {}

    private static BlockState wood() {
        return ModBlocks.MOKUTON_WOOD.get().defaultBlockState();
    }

    private static BlockState leaf() {
        return ModBlocks.MOKUTON_LEAF.get().defaultBlockState();
    }

    private static boolean isAlly(ServerPlayer p, LivingEntity e) {
        if (e == p) return true;
        if (e instanceof WoodCloneEntity c && c.isOwner(p)) return true;
        if (e instanceof IronGolem g && g.isPlayerCreated()) return true;
        if (e instanceof TamableAnimal t && t.isOwnedBy(p)) return true;
        return e instanceof Player && !p.server.isPvpAllowed();
    }

    private static void hurt(ServerPlayer p, LivingEntity e, float dmg) {
        e.hurt(p.serverLevel().damageSources().playerAttack(p), dmg);
    }

    private static void knock(LivingEntity e, Vec3 dir, double h, double y) {
        e.setDeltaMovement(e.getDeltaMovement().add(dir.x * h, y, dir.z * h));
        e.hurtMarked = true;
    }

    private static void burst(ServerLevel w, Vec3 c, int n) {
        w.sendParticles(ParticleTypes.HAPPY_VILLAGER, c.x, c.y, c.z, n, 0.35, 0.35, 0.35, 0.02);
    }

    private static Vec3 sideOf(Vec3 dir) {
        Vec3 s = dir.cross(new Vec3(0, 1, 0));
        if (s.lengthSqr() < 1e-4) s = new Vec3(1, 0, 0);
        return s.normalize();
    }

    // ---------------------------------------------------------------- Mộc Long
    public static boolean dragon(ServerPlayer p) {
        ServerLevel w = p.serverLevel();
        MinecraftServer server = w.getServer();
        final Vec3 eye = p.getEyePosition();
        final Vec3 dir = p.getLookAngle().normalize();
        final Vec3 side = sideOf(dir);
        final Set<UUID> hit = new HashSet<>();
        w.playSound(null, p.blockPosition(), SoundEvents.RAVAGER_ROAR, SoundSource.PLAYERS, 1f, 0.7f);
        for (int i = 0; i < 28; i++) {
            final int idx = i;
            Scheduler.later(server, i, () -> {
                Vec3 c = eye.add(dir.scale(2.0 + idx * 0.9))
                        .add(side.scale(Math.sin(idx * 0.55) * 1.4))
                        .add(0, -0.5 + Math.cos(idx * 0.55) * 0.6, 0);
                TempBlocks.place(w, BlockPos.containing(c), idx == 27 ? leaf() : wood(), 50);
                burst(w, c, 4);
                AABB box = new AABB(c, c).inflate(1.7);
                for (LivingEntity e : w.getEntitiesOfClass(LivingEntity.class, box, en -> en.isAlive() && !isAlly(p, en))) {
                    if (hit.add(e.getUUID())) {
                        hurt(p, e, 14f);
                        knock(e, dir, 1.0, 0.35);
                        e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 3));
                    }
                }
            });
        }
        return true;
    }

    // ---------------------------------------------------------------- Đại Thụ Thương (Great Forest)
    public static boolean spear(ServerPlayer p) {
        ServerLevel w = p.serverLevel();
        MinecraftServer server = w.getServer();
        final Vec3 eye = p.getEyePosition();
        final Vec3 dir = p.getLookAngle().normalize();
        final Vec3 side = sideOf(dir);
        final Set<UUID> hit = new HashSet<>();
        w.playSound(null, p.blockPosition(), SoundEvents.WOOD_BREAK, SoundSource.PLAYERS, 1f, 0.6f);
        for (int d = 2; d <= 9; d++) {
            final int dist = d;
            Scheduler.later(server, d - 2, () -> {
                for (int lane = -1; lane <= 1; lane++) {
                    Vec3 c = eye.add(dir.scale(dist)).add(side.scale(lane * 0.9)).add(0, -0.4, 0);
                    TempBlocks.place(w, BlockPos.containing(c), wood(), 40);
                    burst(w, c, 2);
                }
                Vec3 mid = eye.add(dir.scale(dist));
                for (LivingEntity e : w.getEntitiesOfClass(LivingEntity.class, new AABB(mid, mid).inflate(1.6),
                        en -> en.isAlive() && !isAlly(p, en))) {
                    if (hit.add(e.getUUID())) {
                        hurt(p, e, 11f);
                        knock(e, dir, 0.8, 0.3);
                    }
                }
            });
        }
        return true;
    }

    // ---------------------------------------------------------------- Tứ Trụ Lao (Four-Pillar Prison)
    public static boolean prison(ServerPlayer p) {
        LivingEntity t = Targeting.look(p, 24);
        if (t == null || isAlly(p, t)) {
            p.displayClientMessage(Component.literal("Không có mục tiêu trong tầm nhìn.").withStyle(ChatFormatting.RED), true);
            return false;
        }
        ServerLevel w = p.serverLevel();
        MinecraftServer server = w.getServer();
        final BlockPos c = t.blockPosition();
        final int life = 200;
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                boolean edge = Math.abs(dx) == 2 || Math.abs(dz) == 2;
                for (int dy = 0; dy <= 4; dy++) {
                    boolean roof = dy == 4;
                    if (!edge && !roof) continue;
                    final BlockPos bp = c.offset(dx, dy, dz);
                    Scheduler.later(server, dy * 2, () -> TempBlocks.place(w, bp, wood(), life));
                }
            }
        }
        hurt(p, t, 4f);
        t.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, life, 2));
        t.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, life, 1));
        w.playSound(null, c, SoundEvents.WOOD_PLACE, SoundSource.PLAYERS, 1f, 0.5f);
        return true;
    }

    // ---------------------------------------------------------------- Tứ Trụ Gia (Four-Pillar House)
    public static boolean house(ServerPlayer p) {
        ServerLevel w = p.serverLevel();
        MinecraftServer server = w.getServer();
        Direction f = p.getDirection();
        Direction r = f.getClockWise();
        BlockPos center = p.blockPosition().relative(f, 5);
        final int life = 6000;
        for (int a = -3; a <= 3; a++) {
            for (int b = -3; b <= 3; b++) {
                boolean wall = Math.abs(a) == 3 || Math.abs(b) == 3;
                for (int y = 0; y <= 4; y++) {
                    boolean roof = y == 4;
                    if (!wall && !roof) continue;
                    if (wall && !roof && a == 0 && b == -3 && y <= 1) continue; // cửa ra vào
                    final BlockPos bp = center.relative(r, a).relative(f, b).above(y);
                    Scheduler.later(server, y * 2 + Math.abs(a), () -> TempBlocks.place(w, bp, wood(), life));
                }
            }
        }
        final BlockPos lamp = center.above(3);
        Scheduler.later(server, 14, () -> TempBlocks.place(w, lamp,
                Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true), life));
        w.playSound(null, center, SoundEvents.WOOD_PLACE, SoundSource.PLAYERS, 1f, 0.8f);
        return true;
    }

    // ---------------------------------------------------------------- Thụ Hải Giáng Thế (Deep Forest Emergence)
    public static boolean forest(ServerPlayer p) {
        ServerLevel w = p.serverLevel();
        MinecraftServer server = w.getServer();
        RandomSource rnd = w.random;
        final BlockPos o = p.blockPosition();
        for (int i = 0; i < 18; i++) {
            double ang = rnd.nextDouble() * Math.PI * 2;
            double dist = 2.5 + rnd.nextDouble() * 6.5;
            int x = o.getX() + (int) Math.round(Math.cos(ang) * dist);
            int z = o.getZ() + (int) Math.round(Math.sin(ang) * dist);
            int y = w.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            int h = 5 + rnd.nextInt(5);
            int start = i * 2;
            for (int j = 0; j < h; j++) {
                final BlockPos bp = new BlockPos(x, y + j, z);
                Scheduler.later(server, start + j, () -> TempBlocks.place(w, bp, wood(), 400));
            }
            final BlockPos top = new BlockPos(x, y + h, z);
            Scheduler.later(server, start + h, () -> {
                for (int dx = -2; dx <= 2; dx++)
                    for (int dy = -1; dy <= 1; dy++)
                        for (int dz = -2; dz <= 2; dz++)
                            if (dx * dx + dy * dy * 2 + dz * dz <= 6) TempBlocks.place(w, top.offset(dx, dy, dz), leaf(), 360);
            });
        }
        // Rễ cây trói và gây sát thương lên kẻ địch xung quanh
        for (int k = 0; k < 5; k++) {
            Scheduler.later(server, 20 * k, () -> {
                AABB box = new AABB(o).inflate(9);
                for (LivingEntity e : w.getEntitiesOfClass(LivingEntity.class, box, en -> en.isAlive() && !isAlly(p, en))) {
                    e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 3));
                    hurt(p, e, 3f);
                    w.sendParticles(ParticleTypes.HAPPY_VILLAGER, e.getX(), e.getY() + 0.5, e.getZ(), 6, 0.3, 0.5, 0.3, 0.02);
                }
            });
        }
        w.playSound(null, o, SoundEvents.GRASS_BREAK, SoundSource.PLAYERS, 1.5f, 0.5f);
        return true;
    }

    // ---------------------------------------------------------------- Mộc Phân Thân
    public static boolean clone(ServerPlayer p) {
        ServerLevel w = p.serverLevel();
        long count = w.getEntitiesOfClass(WoodCloneEntity.class, p.getBoundingBox().inflate(96), c -> c.isOwner(p)).size();
        if (count >= 3) {
            p.displayClientMessage(Component.literal("Bạn chỉ có thể duy trì tối đa 3 Mộc Phân Thân.").withStyle(ChatFormatting.RED), true);
            return false;
        }
        WoodCloneEntity c = ModEntities.WOOD_CLONE.get().create(w);
        if (c == null) return false;
        Vec3 off = p.getLookAngle().scale(1.5);
        c.moveTo(p.getX() + off.x, p.getY(), p.getZ() + off.z, p.getYRot(), 0f);
        c.setOwner(p);
        w.addFreshEntity(c);
        w.sendParticles(ParticleTypes.POOF, c.getX(), c.getY() + 1, c.getZ(), 20, 0.4, 0.6, 0.4, 0.05);
        w.playSound(null, p.blockPosition(), SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.PLAYERS, 1f, 1f);
        return true;
    }

    // ---------------------------------------------------------------- Mộc Nhân (Wood Golem)
    public static boolean golem(ServerPlayer p) {
        ServerLevel w = p.serverLevel();
        IronGolem g = EntityType.IRON_GOLEM.create(w);
        if (g == null) return false;
        Vec3 off = p.getLookAngle().scale(3.0);
        g.moveTo(p.getX() + off.x, p.getY(), p.getZ() + off.z, p.getYRot(), 0f);
        g.setPlayerCreated(true);
        g.setCustomName(Component.literal("Mộc Nhân (Mokujin)"));
        g.setCustomNameVisible(true);
        AttributeInstance hp = g.getAttribute(Attributes.MAX_HEALTH);
        if (hp != null) hp.setBaseValue(150);
        g.setHealth(150);
        w.addFreshEntity(g);
        w.sendParticles(ParticleTypes.HAPPY_VILLAGER, g.getX(), g.getY() + 1.5, g.getZ(), 40, 1, 1.5, 1, 0.05);
        w.playSound(null, p.blockPosition(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.PLAYERS, 1f, 0.5f);
        Scheduler.laterCleanup(w.getServer(), 20 * 60, g::discard);
        return true;
    }
}
