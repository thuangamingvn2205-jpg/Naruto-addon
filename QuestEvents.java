package com.example.narutomod.quest;

import com.example.narutomod.data.Shinobi;
import com.example.narutomod.data.ShinobiState;
import com.example.narutomod.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public final class QuestEvents {
    private QuestEvents() {}

    public static void init() {
        NeoForge.EVENT_BUS.addListener(QuestEvents::onDeath);
        NeoForge.EVENT_BUS.addListener(QuestEvents::onServerTick);
        NeoForge.EVENT_BUS.addListener(QuestEvents::onLogin);
        NeoForge.EVENT_BUS.addListener(QuestEvents::onLogout);
    }

    /** Hạ gục: mục tiêu "kill" (kể cả khi đòn đánh đến từ phi tiêu/kunai của người chơi). */
    private static void onDeath(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer sp) {
            QuestManager.onKill(sp, event.getEntity());
        }
    }

    /** Quãng đường: mục tiêu "travel" (mỗi giây). */
    private static void onServerTick(ServerTickEvent.Post event) {
        if (event.getServer().getTickCount() % 20 != 0) return;
        for (ServerPlayer sp : event.getServer().getPlayerList().getPlayers()) QuestManager.tickTravel(sp);
    }

    /** Người chơi mới: tặng Sổ Nhiệm Vụ + phi tiêu để bắt đầu tại Học viện. */
    private static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        ShinobiState st = ShinobiState.get(sp.server);
        if (st.has(sp.getUUID())) return;
        st.get(sp.getUUID());
        st.setDirty();
        give(sp, new ItemStack(ModItems.MISSION_BOOK.get()));
        give(sp, new ItemStack(ModItems.SHURIKEN.get(), 16));
        Shinobi.msg(sp, "Chào mừng đến Học viện Ninja! Cầm Sổ Nhiệm Vụ, Shift + chuột phải để nhận nhiệm vụ đầu tiên.",
                ChatFormatting.GOLD);
        Shinobi.msg(sp, "Lệnh hữu ích: /naruto status, /naruto quest", ChatFormatting.GRAY);
    }

    private static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        QuestManager.forget(event.getEntity().getUUID());
    }

    private static void give(ServerPlayer sp, ItemStack stack) {
        if (!sp.getInventory().add(stack)) sp.drop(stack, false);
    }
}
