package com.example.narutomod.quest;

import com.example.narutomod.data.Profile;
import com.example.narutomod.data.Rank;
import com.example.narutomod.data.Shinobi;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Toàn bộ logic nhiệm vụ: nhận, theo dõi tiến độ, nộp và trao thưởng. */
public final class QuestManager {
    private static final List<Quest> ORDER = new ArrayList<>();
    private static final Map<String, Quest> BY_ID = new HashMap<>();
    private static final Map<UUID, Vec3> LAST_POS = new HashMap<>();

    private QuestManager() {}

    public static void setQuests(List<Quest> list) {
        ORDER.clear();
        BY_ID.clear();
        ORDER.addAll(list);
        for (Quest q : list) BY_ID.put(q.id, q);
    }

    public static List<Quest> all() {
        return ORDER;
    }

    public static Quest next(Profile pr) {
        for (Quest q : ORDER) {
            if (!pr.completed.contains(q.id)) return q;
        }
        return null;
    }

    public static Quest active(Profile pr) {
        if (pr.activeQuest.isEmpty()) return null;
        Quest q = BY_ID.get(pr.activeQuest);
        if (q == null) {
            pr.activeQuest = "";
            pr.progress = new int[0];
        }
        return q;
    }

    // ------------------------------------------------------------------ nhận / xem

    /** Shift + chuột phải vào Sổ Nhiệm Vụ: nhận hoặc nộp nhiệm vụ. */
    public static void interact(ServerPlayer sp) {
        Profile pr = Shinobi.of(sp);
        Quest q = active(pr);
        if (q == null) accept(sp);
        else if (isComplete(sp, pr, q)) turnIn(sp);
        else status(sp);
    }

    public static void accept(ServerPlayer sp) {
        if (ORDER.isEmpty()) {
            Shinobi.msg(sp, "Chưa nạp được dữ liệu nhiệm vụ.", ChatFormatting.RED);
            return;
        }
        Profile pr = Shinobi.of(sp);
        Quest cur = active(pr);
        if (cur != null) {
            Shinobi.msg(sp, "Bạn đang làm nhiệm vụ: " + cur.title, ChatFormatting.YELLOW);
            return;
        }
        Quest q = next(pr);
        if (q == null) {
            Shinobi.msg(sp, "Bạn đã hoàn thành toàn bộ cốt truyện — từ Học viện đến Hokage!", ChatFormatting.GOLD);
            return;
        }
        if (!pr.rank.atLeast(q.minRank)) {
            Shinobi.msg(sp, "Nhiệm vụ kế tiếp \"" + q.title + "\" cần cấp bậc " + q.minRank.display + ".", ChatFormatting.RED);
            return;
        }
        pr.activeQuest = q.id;
        pr.progress = new int[q.objectives.size()];
        Shinobi.dirty(sp);
        Shinobi.msg(sp, "══ NHẬN NHIỆM VỤ ══", ChatFormatting.GOLD);
        printQuest(sp, pr, q);
        spawnBosses(sp, q);
        sp.playNotifySound(SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1f, 1f);
    }

    public static void status(ServerPlayer sp) {
        Profile pr = Shinobi.of(sp);
        Quest q = active(pr);
        if (q == null) {
            Quest n = next(pr);
            if (n == null) {
                Shinobi.msg(sp, "Bạn đã hoàn thành toàn bộ cốt truyện!", ChatFormatting.GOLD);
            } else {
                Shinobi.msg(sp, "Chưa có nhiệm vụ đang làm. Kế tiếp: [" + n.arc + "] " + n.title
                        + " (cần " + n.minRank.display + ")", ChatFormatting.YELLOW);
                Shinobi.msg(sp, "Shift + chuột phải vào Sổ Nhiệm Vụ để nhận.", ChatFormatting.GRAY);
            }
            return;
        }
        printQuest(sp, pr, q);
        if (isComplete(sp, pr, q)) {
            Shinobi.msg(sp, "✔ Đã đủ điều kiện! Shift + chuột phải vào Sổ Nhiệm Vụ để nộp.", ChatFormatting.GREEN);
        }
    }

    private static void printQuest(ServerPlayer sp, Profile pr, Quest q) {
        Shinobi.msg(sp, "[" + q.arc + "] " + q.title, ChatFormatting.AQUA);
        if (!q.story.isEmpty()) Shinobi.msg(sp, q.story, ChatFormatting.GRAY);
        for (int i = 0; i < q.objectives.size(); i++) {
            Quest.Objective ob = q.objectives.get(i);
            int have = progressOf(sp, pr, ob, i);
            boolean done = have >= ob.count;
            Shinobi.msg(sp, (done ? " ✔ " : " ◻ ") + ob.text + "  [" + Math.min(have, ob.count) + "/" + ob.count + "]",
                    done ? ChatFormatting.GREEN : ChatFormatting.WHITE);
        }
    }

    // ------------------------------------------------------------------ tiến độ

    private static int progressOf(ServerPlayer sp, Profile pr, Quest.Objective ob, int i) {
        if (ob.type.equals("collect")) return countItem(sp, ob.target);
        return i < pr.progress.length ? pr.progress[i] : 0;
    }

    public static boolean isComplete(ServerPlayer sp, Profile pr, Quest q) {
        for (int i = 0; i < q.objectives.size(); i++) {
            if (progressOf(sp, pr, q.objectives.get(i), i) < q.objectives.get(i).count) return false;
        }
        return true;
    }

    private static boolean nonCollectComplete(Profile pr, Quest q) {
        for (int i = 0; i < q.objectives.size(); i++) {
            Quest.Objective ob = q.objectives.get(i);
            if (ob.type.equals("collect")) continue;
            if (i >= pr.progress.length || pr.progress[i] < ob.count) return false;
        }
        return true;
    }

    private static void bump(ServerPlayer sp, Profile pr, Quest q, int i, int amount, boolean announce) {
        Quest.Objective ob = q.objectives.get(i);
        if (i >= pr.progress.length) return;
        if (pr.progress[i] >= ob.count) return;
        pr.progress[i] = Math.min(ob.count, pr.progress[i] + amount);
        Shinobi.dirty(sp);
        boolean justDone = pr.progress[i] >= ob.count;
        if (announce || justDone) {
            Shinobi.bar(sp, "[Nhiệm vụ] " + ob.text + ": " + pr.progress[i] + "/" + ob.count,
                    justDone ? ChatFormatting.GREEN : ChatFormatting.YELLOW);
        }
        if (justDone) {
            Shinobi.msg(sp, "✔ Hoàn thành mục tiêu: " + ob.text, ChatFormatting.GREEN);
            if (nonCollectComplete(pr, q) && isComplete(sp, pr, q)) {
                Shinobi.msg(sp, "★ Nhiệm vụ \"" + q.title + "\" đã đủ điều kiện! Shift + chuột phải Sổ Nhiệm Vụ để nộp.",
                        ChatFormatting.GOLD);
            }
        }
    }

    public static void onKill(ServerPlayer sp, LivingEntity killed) {
        if (killed instanceof Monster) Shinobi.addXp(sp, 3);
        Profile pr = Shinobi.of(sp);
        Quest q = active(pr);
        if (q == null) return;
        String id = BuiltInRegistries.ENTITY_TYPE.getKey(killed.getType()).toString();
        String name = killed.hasCustomName() ? killed.getCustomName().getString() : "";
        for (int i = 0; i < q.objectives.size(); i++) {
            Quest.Objective ob = q.objectives.get(i);
            if (!ob.type.equals("kill")) continue;
            if (!ob.target.isEmpty() && !ob.target.equals(id)) continue;
            if (!ob.name.isEmpty() && !name.contains(ob.name)) continue;
            bump(sp, pr, q, i, 1, true);
        }
    }

    public static void onUse(ServerPlayer sp, String key) {
        Profile pr = Shinobi.of(sp);
        Quest q = active(pr);
        if (q == null) return;
        for (int i = 0; i < q.objectives.size(); i++) {
            Quest.Objective ob = q.objectives.get(i);
            if (ob.type.equals("use") && ob.target.equals(key)) bump(sp, pr, q, i, 1, true);
        }
    }

    /** Gọi mỗi giây: cộng quãng đường đã đi cho mục tiêu "travel". */
    public static void tickTravel(ServerPlayer sp) {
        Vec3 now = sp.position();
        Vec3 last = LAST_POS.put(sp.getUUID(), now);
        if (last == null) return;
        double d = Math.sqrt((now.x - last.x) * (now.x - last.x) + (now.z - last.z) * (now.z - last.z));
        if (d < 0.5 || d > 64) return; // bỏ qua đứng yên và dịch chuyển tức thời
        Profile pr = Shinobi.of(sp);
        Quest q = active(pr);
        if (q == null) return;
        for (int i = 0; i < q.objectives.size(); i++) {
            Quest.Objective ob = q.objectives.get(i);
            if (ob.type.equals("travel")) bump(sp, pr, q, i, (int) Math.round(d), false);
        }
    }

    public static void forget(UUID id) {
        LAST_POS.remove(id);
    }

    // ------------------------------------------------------------------ nộp / thưởng

    public static void turnIn(ServerPlayer sp) {
        Profile pr = Shinobi.of(sp);
        Quest q = active(pr);
        if (q == null) {
            Shinobi.msg(sp, "Bạn chưa nhận nhiệm vụ nào.", ChatFormatting.YELLOW);
            return;
        }
        if (!isComplete(sp, pr, q)) {
            Shinobi.msg(sp, "Chưa hoàn thành tất cả mục tiêu.", ChatFormatting.RED);
            status(sp);
            return;
        }
        for (Quest.Objective ob : q.objectives) {
            if (ob.type.equals("collect")) removeItem(sp, ob.target, ob.count);
        }
        finish(sp, pr, q);
    }

    /** Dùng cho lệnh admin: hoàn thành ngay nhiệm vụ đang làm (hoặc nhiệm vụ kế tiếp). */
    public static void forceComplete(ServerPlayer sp) {
        Profile pr = Shinobi.of(sp);
        Quest q = active(pr);
        if (q == null) q = next(pr);
        if (q == null) {
            Shinobi.msg(sp, "Không còn nhiệm vụ nào.", ChatFormatting.YELLOW);
            return;
        }
        finish(sp, pr, q);
    }

    private static void finish(ServerPlayer sp, Profile pr, Quest q) {
        Quest.Rewards r = q.rewards;
        Shinobi.msg(sp, "══ HOÀN THÀNH: " + q.title + " ══", ChatFormatting.GOLD);
        pr.completed.add(q.id);
        pr.activeQuest = "";
        pr.progress = new int[0];
        pr.ryo += r.ryo;
        if (r.ryo > 0) Shinobi.msg(sp, "+ " + r.ryo + " Ryo", ChatFormatting.YELLOW);
        if (r.xp > 0) Shinobi.msg(sp, "+ " + r.xp + " XP", ChatFormatting.GREEN);
        for (Quest.ItemReward ir : r.items) {
            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(ir.id));
            if (item == Items.AIR) continue;
            ItemStack stack = new ItemStack(item, ir.count);
            Shinobi.msg(sp, "+ " + ir.count + " × " + stack.getHoverName().getString(), ChatFormatting.AQUA);
            if (!sp.getInventory().add(stack)) sp.drop(stack, false);
        }
        for (String key : r.unlock) {
            if (pr.unlocked.add(key)) Shinobi.msg(sp, "✦ Bạn đã lĩnh hội: " + keyName(key), ChatFormatting.LIGHT_PURPLE);
        }
        Shinobi.dirty(sp);
        Shinobi.addXp(sp, r.xp);
        Rank to = Rank.fromId(r.promote);
        if (to != null) Shinobi.promote(sp, to);
        sp.playNotifySound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 1f, 1f);
        Quest n = next(pr);
        if (n != null) {
            Shinobi.msg(sp, "Tiếp theo: [" + n.arc + "] " + n.title + " (cần " + n.minRank.display + ")", ChatFormatting.GRAY);
        } else {
            Shinobi.msg(sp, "★ Bạn đã đi hết cốt truyện: từ Học viện đến Hokage! ★", ChatFormatting.GOLD);
        }
    }

    public static void abandon(ServerPlayer sp) {
        Profile pr = Shinobi.of(sp);
        if (active(pr) == null) {
            Shinobi.msg(sp, "Bạn không có nhiệm vụ nào đang làm.", ChatFormatting.YELLOW);
            return;
        }
        pr.activeQuest = "";
        pr.progress = new int[0];
        Shinobi.dirty(sp);
        Shinobi.msg(sp, "Đã hủy nhiệm vụ. Nhận lại bằng Sổ Nhiệm Vụ (kẻ địch cũ có thể còn ngoài kia).", ChatFormatting.YELLOW);
    }

    private static String keyName(String key) {
        return switch (key) {
            case "mokuton" -> "Mộc Độn (Wood Release) — tế bào Hashirama";
            default -> key;
        };
    }

    // ------------------------------------------------------------------ kẻ địch / vật phẩm

    private static void spawnBosses(ServerPlayer sp, Quest q) {
        ServerLevel w = sp.serverLevel();
        for (Quest.SpawnSpec s : q.spawns) {
            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(s.entity));
            for (int i = 0; i < s.count; i++) {
                Entity e = type.create(w);
                if (!(e instanceof Mob mob)) continue;
                double ang = w.random.nextDouble() * Math.PI * 2;
                double dist = s.distance + w.random.nextDouble() * 3;
                int x = Mth.floor(sp.getX() + Math.cos(ang) * dist);
                int z = Mth.floor(sp.getZ() + Math.sin(ang) * dist);
                int y = w.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
                mob.moveTo(x + 0.5, y, z + 0.5, w.random.nextFloat() * 360f, 0f);
                mob.finalizeSpawn(w, w.getCurrentDifficultyAt(mob.blockPosition()), MobSpawnType.EVENT, null);
                if (!s.name.isEmpty()) {
                    mob.setCustomName(Component.literal(s.name));
                    mob.setCustomNameVisible(true);
                }
                if (s.health > 0) {
                    AttributeInstance a = mob.getAttribute(Attributes.MAX_HEALTH);
                    if (a != null) a.setBaseValue(s.health);
                    mob.setHealth(s.health);
                }
                // Không để mục tiêu nhiệm vụ cháy chết dưới nắng (sẽ không tính là bạn hạ gục)
                mob.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
                mob.setPersistenceRequired();
                mob.setTarget(sp);
                w.addFreshEntity(mob);
            }
        }
    }

    private static int countItem(ServerPlayer sp, String id) {
        Inventory inv = sp.getInventory();
        int c = 0;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if (!s.isEmpty() && BuiltInRegistries.ITEM.getKey(s.getItem()).toString().equals(id)) c += s.getCount();
        }
        return c;
    }

    private static void removeItem(ServerPlayer sp, String id, int amount) {
        Inventory inv = sp.getInventory();
        for (int i = 0; i < inv.getContainerSize() && amount > 0; i++) {
            ItemStack s = inv.getItem(i);
            if (s.isEmpty() || !BuiltInRegistries.ITEM.getKey(s.getItem()).toString().equals(id)) continue;
            int take = Math.min(amount, s.getCount());
            s.shrink(take);
            amount -= take;
        }
    }
}
