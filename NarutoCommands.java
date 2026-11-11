package com.example.narutomod.command;

import com.example.narutomod.data.Profile;
import com.example.narutomod.data.Rank;
import com.example.narutomod.data.Shinobi;
import com.example.narutomod.quest.QuestManager;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class NarutoCommands {
    private NarutoCommands() {}

    public static void init() {
        NeoForge.EVENT_BUS.addListener(NarutoCommands::register);
    }

    private static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("naruto")
                .then(Commands.literal("status").executes(ctx -> {
                    ServerPlayer p = ctx.getSource().getPlayerOrException();
                    Profile pr = Shinobi.of(p);
                    Shinobi.msg(p, "Cấp bậc: " + pr.rank.display + "  |  Lv." + pr.level() + "  |  XP " + pr.xp,
                            ChatFormatting.GOLD);
                    Shinobi.msg(p, "Chakra: " + (int) pr.chakra + "/" + pr.maxChakra() + "  |  Ryo: " + pr.ryo,
                            ChatFormatting.AQUA);
                    Shinobi.msg(p, "Nhiệm vụ đã xong: " + pr.completed.size() + "/" + QuestManager.all().size()
                            + (pr.unlocked.isEmpty() ? "" : "  |  Đã lĩnh hội: " + String.join(", ", pr.unlocked)),
                            ChatFormatting.GRAY);
                    return 1;
                }))
                .then(Commands.literal("quest")
                        .executes(ctx -> {
                            QuestManager.status(ctx.getSource().getPlayerOrException());
                            return 1;
                        })
                        .then(Commands.literal("abandon").executes(ctx -> {
                            QuestManager.abandon(ctx.getSource().getPlayerOrException());
                            return 1;
                        })))
                .then(Commands.literal("admin")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.literal("rank")
                                .then(Commands.argument("rank", StringArgumentType.word()).executes(ctx -> {
                                    ServerPlayer p = ctx.getSource().getPlayerOrException();
                                    Rank r = Rank.fromId(StringArgumentType.getString(ctx, "rank"));
                                    if (r == null) {
                                        Shinobi.msg(p, "Rank hợp lệ: academy, genin, chunin, jonin, elite_jonin, kage",
                                                ChatFormatting.RED);
                                        return 0;
                                    }
                                    Profile pr = Shinobi.of(p);
                                    pr.rank = r;
                                    pr.chakra = pr.maxChakra();
                                    Shinobi.dirty(p);
                                    Shinobi.msg(p, "Đã đặt cấp bậc: " + r.display, ChatFormatting.GOLD);
                                    return 1;
                                })))
                        .then(Commands.literal("xp")
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1)).executes(ctx -> {
                                    Shinobi.addXp(ctx.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(ctx, "amount"));
                                    return 1;
                                })))
                        .then(Commands.literal("unlock")
                                .then(Commands.argument("key", StringArgumentType.word()).executes(ctx -> {
                                    ServerPlayer p = ctx.getSource().getPlayerOrException();
                                    Shinobi.of(p).unlocked.add(StringArgumentType.getString(ctx, "key"));
                                    Shinobi.dirty(p);
                                    Shinobi.msg(p, "Đã mở khóa: " + StringArgumentType.getString(ctx, "key"), ChatFormatting.LIGHT_PURPLE);
                                    return 1;
                                })))
                        .then(Commands.literal("complete").executes(ctx -> {
                            QuestManager.forceComplete(ctx.getSource().getPlayerOrException());
                            return 1;
                        }))
                        .then(Commands.literal("chakra").executes(ctx -> {
                            ServerPlayer p = ctx.getSource().getPlayerOrException();
                            Profile pr = Shinobi.of(p);
                            pr.chakra = pr.maxChakra();
                            Shinobi.dirty(p);
                            return 1;
                        }))
                        .then(Commands.literal("reset").executes(ctx -> {
                            ServerPlayer p = ctx.getSource().getPlayerOrException();
                            Profile pr = Shinobi.of(p);
                            pr.rank = Rank.ACADEMY;
                            pr.xp = 0;
                            pr.ryo = 0;
                            pr.completed.clear();
                            pr.unlocked.clear();
                            pr.activeQuest = "";
                            pr.progress = new int[0];
                            pr.chakra = pr.maxChakra();
                            Shinobi.dirty(p);
                            Shinobi.msg(p, "Đã reset toàn bộ tiến trình shinobi.", ChatFormatting.RED);
                            return 1;
                        }))));
    }
}
