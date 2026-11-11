package com.example.narutomod.item;

import com.example.narutomod.data.Profile;
import com.example.narutomod.data.Shinobi;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/** Mì Ichiraku: hồi 10 thức ăn, +40 chakra, trả lại bát. */
public class RamenItem extends Item {
    public RamenItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        if (entity instanceof ServerPlayer sp) {
            Profile pr = Shinobi.of(sp);
            pr.chakra = Math.min(pr.maxChakra(), pr.chakra + 40);
            Shinobi.dirty(sp);
        }
        if (entity instanceof Player player) {
            if (player.getAbilities().instabuild) return result;
            ItemStack bowl = new ItemStack(Items.BOWL);
            if (result.isEmpty()) return bowl;
            if (!player.getInventory().add(bowl)) player.drop(bowl, false);
        }
        return result;
    }
}
