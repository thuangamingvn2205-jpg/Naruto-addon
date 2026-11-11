package com.example.narutomod.data;

import com.example.narutomod.NarutoMod;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/** Hồi chakra và hiển thị thanh chakra trên action bar khi cầm vật phẩm của mod. */
public final class ShinobiTick {
    private ShinobiTick() {}

    public static void init() {
        NeoForge.EVENT_BUS.addListener(ShinobiTick::onServerTick);
    }

    private static void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        int t = server.getTickCount();
        if (t % 10 != 0) return;
        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            Profile pr = Shinobi.of(p);
            int max = pr.maxChakra();
            if (t % 20 == 0) {
                float regen = 1.5f + pr.level() * 0.15f;
                // Ngồi yên = tập trung chakra (hồi nhanh x3)
                if (p.isShiftKeyDown() && p.getDeltaMovement().horizontalDistanceSqr() < 0.0004) regen *= 3f;
                pr.chakra = Math.min(max, pr.chakra + regen);
            }
            if (pr.chakra > max) pr.chakra = max;
            if (t % 200 == 0) Shinobi.dirty(p);
            if (isMod(p.getMainHandItem()) || isMod(p.getOffhandItem())) {
                Shinobi.bar(p, "Chakra " + (int) pr.chakra + "/" + max + "   |   " + pr.rank.display
                        + " Lv." + pr.level(), ChatFormatting.AQUA);
            }
        }
    }

    private static boolean isMod(ItemStack s) {
        return !s.isEmpty() && BuiltInRegistries.ITEM.getKey(s.getItem()).getNamespace().equals(NarutoMod.MOD_ID);
    }
}
