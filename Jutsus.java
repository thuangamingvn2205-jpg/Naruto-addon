package com.example.narutomod.jutsu;

import com.example.narutomod.data.Profile;
import com.example.narutomod.data.Rank;
import com.example.narutomod.data.Shinobi;
import com.example.narutomod.quest.QuestManager;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Predicate;

/** Danh sách nhẫn thuật + kiểm tra cấp bậc / lĩnh hội / chakra trước khi thi triển. */
public final class Jutsus {
    public record Def(String id, String name, Rank minRank, int cost, int cooldown, String unlockKey) {}

    public static final Def FIREBALL = new Def("fireball", "Hỏa độn: Hào Hỏa Cầu", Rank.GENIN, 25, 40, null);
    public static final Def RASENGAN = new Def("rasengan", "Rasengan", Rank.CHUNIN, 40, 60, null);

    // Mộc Độn (Wood Release) — cần đã lĩnh hội "mokuton" qua cốt truyện
    public static final Def WOOD_CLONE = new Def("wood_clone", "Mộc Độn: Mộc Phân Thân", Rank.CHUNIN, 40, 100, "mokuton");
    public static final Def WOOD_HOUSE = new Def("wood_house", "Mộc Độn: Tứ Trụ Gia", Rank.CHUNIN, 50, 200, "mokuton");
    public static final Def WOOD_PRISON = new Def("wood_prison", "Mộc Độn: Tứ Trụ Lao", Rank.JONIN, 60, 160, "mokuton");
    public static final Def WOOD_SPEAR = new Def("wood_spear", "Mộc Độn: Đại Thụ Thương", Rank.JONIN, 45, 80, "mokuton");
    public static final Def WOOD_DRAGON = new Def("wood_dragon", "Mộc Độn: Mộc Long", Rank.JONIN, 80, 200, "mokuton");
    public static final Def WOOD_FOREST = new Def("wood_forest", "Mộc Độn: Thụ Hải Giáng Thế", Rank.ELITE_JONIN, 120, 400, "mokuton");
    public static final Def WOOD_GOLEM = new Def("wood_golem", "Mộc Độn: Mộc Nhân", Rank.ELITE_JONIN, 140, 600, "mokuton");

    private Jutsus() {}

    /**
     * Kiểm tra điều kiện, chạy action; chỉ trừ chakra và tính nhiệm vụ nếu action trả về true.
     * Người chơi Creative được bỏ qua mọi điều kiện.
     */
    public static boolean run(ServerPlayer p, Def d, Predicate<ServerPlayer> action) {
        if (p.isCreative()) {
            if (!action.test(p)) return false;
            QuestManager.onUse(p, d.id());
            return true;
        }
        Profile pr = Shinobi.of(p);
        if (!pr.rank.atLeast(d.minRank())) {
            Shinobi.bar(p, d.name() + " cần cấp bậc " + d.minRank().display, ChatFormatting.RED);
            return false;
        }
        if (d.unlockKey() != null && !pr.unlocked.contains(d.unlockKey())) {
            Shinobi.bar(p, "Bạn chưa lĩnh hội Mộc Độn (hoàn thành nhiệm vụ \"Đội Yamato\")", ChatFormatting.RED);
            return false;
        }
        if (pr.chakra < d.cost()) {
            Shinobi.bar(p, "Không đủ chakra (" + (int) pr.chakra + "/" + d.cost() + ")", ChatFormatting.RED);
            return false;
        }
        if (!action.test(p)) return false;
        pr.chakra -= d.cost();
        Shinobi.dirty(p);
        QuestManager.onUse(p, d.id());
        return true;
    }
}
