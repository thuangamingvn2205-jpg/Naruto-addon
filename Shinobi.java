package com.example.narutomod.data;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

/** Cửa vào chung để đọc/ghi dữ liệu shinobi của người chơi. */
public final class Shinobi {
    private Shinobi() {}

    public static Profile of(ServerPlayer p) {
        return ShinobiState.get(p.server).get(p.getUUID());
    }

    public static void dirty(ServerPlayer p) {
        ShinobiState.get(p.server).setDirty();
    }

    public static void msg(ServerPlayer p, String text, ChatFormatting color) {
        p.sendSystemMessage(Component.literal(text).withStyle(color));
    }

    public static void bar(ServerPlayer p, String text, ChatFormatting color) {
        p.displayClientMessage(Component.literal(text).withStyle(color), true);
    }

    public static void addXp(ServerPlayer p, int amount) {
        if (amount <= 0) return;
        Profile pr = of(p);
        int before = pr.level();
        pr.xp += amount;
        if (pr.level() > before) {
            pr.chakra = pr.maxChakra();
            msg(p, "★ Lên cấp! Bạn đạt Lv." + pr.level() + " (chakra tối đa " + pr.maxChakra() + ")", ChatFormatting.GREEN);
            p.playNotifySound(SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1f, 1f);
        }
        dirty(p);
    }

    public static boolean promote(ServerPlayer p, Rank to) {
        Profile pr = of(p);
        if (to == pr.rank || !to.atLeast(pr.rank)) return false;
        pr.rank = to;
        pr.chakra = pr.maxChakra();
        msg(p, "══ THĂNG CẤP ══ Bạn trở thành " + to.display + "!", ChatFormatting.GOLD);
        p.playNotifySound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 1f, 1f);
        dirty(p);
        return true;
    }
}
