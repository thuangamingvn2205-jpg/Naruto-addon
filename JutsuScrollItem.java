package com.example.narutomod.item;

import com.example.narutomod.jutsu.Jutsus;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Predicate;

/** Cuộn nhẫn thuật dùng chung: chuột phải để thi triển. */
public class JutsuScrollItem extends Item {
    private final Jutsus.Def def;
    private final Predicate<ServerPlayer> action;

    public JutsuScrollItem(Jutsus.Def def, Predicate<ServerPlayer> action, Properties properties) {
        super(properties);
        this.def = def;
        this.action = action;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) return InteractionResultHolder.success(stack);
        if (player instanceof ServerPlayer sp && Jutsus.run(sp, def, action)) {
            sp.getCooldowns().addCooldown(this, def.cooldown());
        }
        return InteractionResultHolder.success(stack);
    }
}
