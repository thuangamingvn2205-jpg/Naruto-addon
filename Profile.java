package com.example.narutomod.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/** Dữ liệu shinobi của một người chơi (lưu theo thế giới). */
public class Profile {
    public Rank rank = Rank.ACADEMY;
    public int xp = 0;
    public int ryo = 0;
    public float chakra = 60f;
    public final Set<String> unlocked = new HashSet<>();
    public final Set<String> completed = new LinkedHashSet<>();
    public String activeQuest = "";
    public int[] progress = new int[0];

    public int level() {
        return Math.min(60, 1 + (int) Math.sqrt(xp / 50.0));
    }

    public int maxChakra() {
        return rank.baseChakra + level() * 5;
    }

    public CompoundTag toNbt() {
        CompoundTag n = new CompoundTag();
        n.putString("rank", rank.id);
        n.putInt("xp", xp);
        n.putInt("ryo", ryo);
        n.putFloat("chakra", chakra);
        n.put("unlocked", strings(unlocked));
        n.put("completed", strings(completed));
        n.putString("active", activeQuest);
        n.putIntArray("progress", progress);
        return n;
    }

    private static ListTag strings(Collection<String> c) {
        ListTag list = new ListTag();
        for (String s : c) list.add(StringTag.valueOf(s));
        return list;
    }

    public static Profile fromNbt(CompoundTag n) {
        Profile p = new Profile();
        Rank r = Rank.fromId(n.getString("rank"));
        if (r != null) p.rank = r;
        p.xp = n.getInt("xp");
        p.ryo = n.getInt("ryo");
        p.chakra = n.getFloat("chakra");
        ListTag u = n.getList("unlocked", Tag.TAG_STRING);
        for (int i = 0; i < u.size(); i++) p.unlocked.add(u.getString(i));
        ListTag c = n.getList("completed", Tag.TAG_STRING);
        for (int i = 0; i < c.size(); i++) p.completed.add(c.getString(i));
        p.activeQuest = n.getString("active");
        p.progress = n.getIntArray("progress");
        return p;
    }
}
