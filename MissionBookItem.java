package com.example.narutomod.item;

import com.example.narutomod.quest.QuestManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Chuột phải: xem nhiệm vụ. Shift + chuột phải: nhận nhiệm vụ kế tiếp / nộp nhiệm vụ đã xong. */
public class MissionBookItem extends Item {
    public MissionBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) return InteractionResultHolder.success(stack);
        if (player instanceof ServerPlayer sp) {
            if (sp.isShiftKeyDown()) QuestManager.interact(sp);
            else QuestManager.status(sp);
        }
        return InteractionResultHolder.success(stack);
    }
}
