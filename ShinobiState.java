package com.example.narutomod.data;

import com.example.narutomod.NarutoMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShinobiState extends SavedData {
    private final Map<UUID, Profile> profiles = new HashMap<>();

    public boolean has(UUID id) {
        return profiles.containsKey(id);
    }

    public Profile get(UUID id) {
        return profiles.computeIfAbsent(id, k -> new Profile());
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        CompoundTag all = new CompoundTag();
        for (Map.Entry<UUID, Profile> e : profiles.entrySet()) {
            all.put(e.getKey().toString(), e.getValue().toNbt());
        }
        tag.put("players", all);
        return tag;
    }

    public static ShinobiState load(CompoundTag tag, HolderLookup.Provider registries) {
        ShinobiState state = new ShinobiState();
        CompoundTag all = tag.getCompound("players");
        for (String key : all.getAllKeys()) {
            try {
                state.profiles.put(UUID.fromString(key), Profile.fromNbt(all.getCompound(key)));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return state;
    }

    public static ShinobiState get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(ShinobiState::new, ShinobiState::load, null),
                NarutoMod.MOD_ID + "_shinobi");
    }
}
