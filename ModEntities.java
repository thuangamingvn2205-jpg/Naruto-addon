package com.example.narutomod.registry;

import com.example.narutomod.NarutoMod;
import com.example.narutomod.entity.KunaiEntity;
import com.example.narutomod.entity.ShurikenEntity;
import com.example.narutomod.entity.WoodCloneEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Hitbox lấy theo mod tham khảo Kageborne (đọc từ EntityType.Builder của nó):
 *  - shuriken / kunai: sized 0.4 x 0.4, eyeHeight 0.13, clientTrackingRange 4, updateInterval 20
 *  - clone / NPC người: sized 0.6 x 1.8, eyeHeight 1.62, clientTrackingRange 10, updateInterval 2
 */
public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, NarutoMod.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<ShurikenEntity>> SHURIKEN =
            ENTITY_TYPES.register("shuriken", () -> EntityType.Builder.<ShurikenEntity>of(ShurikenEntity::new, MobCategory.MISC)
                    .sized(0.4f, 0.4f).eyeHeight(0.13f).clientTrackingRange(4).updateInterval(20)
                    .build("shuriken"));

    public static final DeferredHolder<EntityType<?>, EntityType<KunaiEntity>> KUNAI =
            ENTITY_TYPES.register("kunai", () -> EntityType.Builder.<KunaiEntity>of(KunaiEntity::new, MobCategory.MISC)
                    .sized(0.4f, 0.4f).eyeHeight(0.13f).clientTrackingRange(4).updateInterval(20)
                    .build("kunai"));

    public static final DeferredHolder<EntityType<?>, EntityType<WoodCloneEntity>> WOOD_CLONE =
            ENTITY_TYPES.register("wood_clone", () -> EntityType.Builder.<WoodCloneEntity>of(WoodCloneEntity::new, MobCategory.MISC)
                    .sized(0.6f, 1.8f).eyeHeight(1.62f).clientTrackingRange(10).updateInterval(2).noSave()
                    .build("wood_clone"));

    private ModEntities() {}
}
