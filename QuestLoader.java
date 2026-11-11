package com.example.narutomod.quest;

import com.example.narutomod.NarutoMod;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Nạp data/narutomod/quests/story.json (có thể ghi đè bằng datapack). */
public class QuestLoader extends SimplePreparableReloadListener<List<Quest>> {
    @Override
    protected List<Quest> prepare(ResourceManager manager, ProfilerFiller profiler) {
        List<Quest> list = new ArrayList<>();
        Optional<Resource> res = manager.getResource(NarutoMod.id("quests/story.json"));
        if (res.isEmpty()) {
            NarutoMod.LOGGER.warn("Không tìm thấy data/narutomod/quests/story.json");
            return list;
        }
        try (Reader reader = new InputStreamReader(res.get().open(), StandardCharsets.UTF_8)) {
            for (JsonElement e : JsonParser.parseReader(reader).getAsJsonArray()) {
                try {
                    list.add(Quest.parse(e.getAsJsonObject()));
                } catch (RuntimeException ex) {
                    NarutoMod.LOGGER.warn("Bỏ qua một nhiệm vụ lỗi: {}", e, ex);
                }
            }
        } catch (Exception ex) {
            NarutoMod.LOGGER.error("Không đọc được story.json", ex);
        }
        return list;
    }

    @Override
    protected void apply(List<Quest> list, ResourceManager manager, ProfilerFiller profiler) {
        QuestManager.setQuests(list);
        NarutoMod.LOGGER.info("Đã nạp {} nhiệm vụ cốt truyện Naruto", list.size());
    }
}
