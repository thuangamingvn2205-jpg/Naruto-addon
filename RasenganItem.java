package com.example.narutomod.item;

import com.example.narutomod.jutsu.Jutsus;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** Chuột phải vào sinh vật để tung Rasengan: 14 sát thương + hất văng. Cần Chunin, tốn 40 chakra. */
public class RasenganItem extends Item {
    public RasenganItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        Level level = player.level();
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(player instanceof ServerPlayer sp)) return InteractionResult.PASS;
        boolean ok = Jutsus.run(sp, Jutsus.RASENGAN, p -> {
            target.hurt(level.damageSources().playerAttack(p), 14.0f);
            Vec3 dir = target.position().subtract(p.position()).multiply(1, 0, 1).normalize();
            target.setDeltaMovement(target.getDeltaMovement().add(dir.x * 2.2, 0.7, dir.z * 2.2));
            target.hurtMarked = true;
            ServerLevel sl = (ServerLevel) level;
            sl.sendParticles(ParticleTypes.ELECTRIC_SPARK, target.getX(), target.getY(0.5), target.getZ(), 40, 0.5, 0.5, 0.5, 0.25);
            sl.sendParticles(ParticleTypes.CLOUD, target.getX(), target.getY(0.5), target.getZ(), 20, 0.4, 0.4, 0.4, 0.1);
            level.playSound(null, target.getX(), target.getY(), target.getZ(),
                    SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.PLAYERS, 1.0f, 0.6f);
            return true;
        });
        if (ok) sp.getCooldowns().addCooldown(this, Jutsus.RASENGAN.cooldown());
        return ok ? InteractionResult.SUCCESS : InteractionResult.FAIL;
    }
}
