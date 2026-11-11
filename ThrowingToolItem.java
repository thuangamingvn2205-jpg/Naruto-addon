package com.example.narutomod.item;

import com.example.narutomod.entity.ThrownToolEntity;
import com.example.narutomod.quest.QuestManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.BiFunction;

/** Phi tiêu / Kunai ném: chuột phải để ném. */
public class ThrowingToolItem extends Item {
    private final BiFunction<Level, LivingEntity, ThrownToolEntity> factory;
    private final String questKey;
    private final float speed;
    private final int cooldown;

    public ThrowingToolItem(BiFunction<Level, LivingEntity, ThrownToolEntity> factory, String questKey,
                            float speed, int cooldown, Properties properties) {
        super(properties);
        this.factory = factory;
        this.questKey = questKey;
        this.speed = speed;
        this.cooldown = cooldown;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 0.6f, 1.6f);
        if (!level.isClientSide) {
            ThrownToolEntity e = factory.apply(level, player);
            e.setItem(stack);
            e.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0f, speed, 1.0f);
            level.addFreshEntity(e);
            if (player instanceof ServerPlayer sp) QuestManager.onUse(sp, questKey);
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        player.getCooldowns().addCooldown(this, cooldown);
        if (!player.getAbilities().instabuild) stack.shrink(1);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
