package com.example.narutomod.quest;

import com.example.narutomod.data.Rank;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/** Một nhiệm vụ cốt truyện, đọc từ data/narutomod/quests/story.json. */
public final class Quest {
    public String id = "";
    public String arc = "";
    public String title = "";
    public String story = "";
    public Rank minRank = Rank.ACADEMY;
    public final List<Objective> objectives = new ArrayList<>();
    public final List<SpawnSpec> spawns = new ArrayList<>();
    public final Rewards rewards = new Rewards();

    /** type: kill | collect | use | travel */
    public static final class Objective {
        public String type = "kill";
        public String target = "";
        public String name = "";
        public String text = "";
        public int count = 1;
    }

    public static final class SpawnSpec {
        public String entity = "minecraft:vindicator";
        public String name = "";
        public int count = 1;
        public float health = 0;
        public double distance = 14;
    }

    public static final class ItemReward {
        public String id = "";
        public int count = 1;
    }

    public static final class Rewards {
        public int xp = 0;
        public int ryo = 0;
        public String promote = "";
        public final List<String> unlock = new ArrayList<>();
        public final List<ItemReward> items = new ArrayList<>();
    }

    private static String s(JsonObject o, String k, String d) {
        return o.has(k) ? o.get(k).getAsString() : d;
    }

    private static int i(JsonObject o, String k, int d) {
        return o.has(k) ? o.get(k).getAsInt() : d;
    }

    private static double dbl(JsonObject o, String k, double d) {
        return o.has(k) ? o.get(k).getAsDouble() : d;
    }

    public static Quest parse(JsonObject o) {
        Quest q = new Quest();
        q.id = s(o, "id", "");
        q.arc = s(o, "arc", "");
        q.title = s(o, "title", q.id);
        q.story = s(o, "story", "");
        Rank r = Rank.fromId(s(o, "rank", "academy"));
        if (r != null) q.minRank = r;

        if (o.has("objectives")) {
            for (JsonElement e : o.getAsJsonArray("objectives")) {
                JsonObject j = e.getAsJsonObject();
                Objective ob = new Objective();
                ob.type = s(j, "type", "kill");
                ob.target = s(j, "target", "");
                ob.name = s(j, "name", "");
                ob.text = s(j, "text", "");
                ob.count = Math.max(1, i(j, "count", 1));
                q.objectives.add(ob);
            }
        }
        if (o.has("spawns")) {
            for (JsonElement e : o.getAsJsonArray("spawns")) {
                JsonObject j = e.getAsJsonObject();
                SpawnSpec sp = new SpawnSpec();
                sp.entity = s(j, "entity", "minecraft:vindicator");
                sp.name = s(j, "name", "");
                sp.count = Math.max(1, i(j, "count", 1));
                sp.health = (float) dbl(j, "health", 0);
                sp.distance = dbl(j, "distance", 14);
                q.spawns.add(sp);
            }
        }
        if (o.has("rewards")) {
            JsonObject j = o.getAsJsonObject("rewards");
            q.rewards.xp = i(j, "xp", 0);
            q.rewards.ryo = i(j, "ryo", 0);
            q.rewards.promote = s(j, "promote", "");
            if (j.has("unlock")) for (JsonElement e : j.getAsJsonArray("unlock")) q.rewards.unlock.add(e.getAsString());
            if (j.has("items")) {
                for (JsonElement e : j.getAsJsonArray("items")) {
                    JsonObject it = e.getAsJsonObject();
                    ItemReward ir = new ItemReward();
                    ir.id = s(it, "id", "");
                    ir.count = Math.max(1, i(it, "count", 1));
                    q.rewards.items.add(ir);
                }
            }
        }
        return q;
    }
}
