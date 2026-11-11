package com.example.narutomod.item;

import com.example.narutomod.jutsu.Jutsus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** Hỏa độn: Hào Hỏa Cầu — 3 quả cầu lửa. Cần Genin, tốn 25 chakra. */
public class FireballJutsuItem extends Item {
    public FireballJutsuItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) return InteractionResultHolder.success(stack);
        if (player instanceof ServerPlayer sp) {
            boolean ok = Jutsus.run(sp, Jutsus.FIREBALL, p -> {
                Vec3 look = p.getLookAngle();
                Vec3 side = look.cross(new Vec3(0, 1, 0));
                for (int i = -1; i <= 1; i++) {
                    Vec3 dir = look.add(side.scale(i * 0.12)).normalize();
                    SmallFireball fb = new SmallFireball(EntityType.SMALL_FIREBALL, level);
                    fb.setOwner(p);
                    fb.setPos(p.getX() + look.x * 1.5, p.getEyeY() - 0.1 + look.y * 1.5, p.getZ() + look.z * 1.5);
                    fb.setDeltaMovement(dir.scale(1.2));
                    level.addFreshEntity(fb);
                }
                level.playSound(null, p.getX(), p.getY(), p.getZ(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0f, 0.8f);
                return true;
            });
            if (ok) sp.getCooldowns().addCooldown(this, Jutsus.FIREBALL.cooldown());
        }
        return InteractionResultHolder.success(stack);
    }
}
