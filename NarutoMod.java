package com.example.narutomod;

import com.example.narutomod.client.NarutoClient;
import com.example.narutomod.command.NarutoCommands;
import com.example.narutomod.data.ShinobiTick;
import com.example.narutomod.entity.WoodCloneEntity;
import com.example.narutomod.quest.QuestEvents;
import com.example.narutomod.quest.QuestLoader;
import com.example.narutomod.registry.ModArmorMaterials;
import com.example.narutomod.registry.ModBlocks;
import com.example.narutomod.registry.ModEntities;
import com.example.narutomod.registry.ModItems;
import com.example.narutomod.util.Scheduler;
import com.example.narutomod.util.TempBlocks;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import org.slf4j.Logger;

@Mod(NarutoMod.MOD_ID)
public class NarutoMod {
    public static final String MOD_ID = "narutomod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public NarutoMod(IEventBus modBus) {
        ModBlocks.BLOCKS.register(modBus);
        ModEntities.ENTITY_TYPES.register(modBus);
        ModArmorMaterials.MATERIALS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModItems.TABS.register(modBus);

        modBus.addListener(NarutoMod::onAttributes);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modBus.addListener(NarutoClient::registerRenderers);
        }

        Scheduler.init();
        TempBlocks.init();
        ShinobiTick.init();
        QuestEvents.init();
        NarutoCommands.init();
        NeoForge.EVENT_BUS.addListener(NarutoMod::onAddReloadListeners);
    }

    private static void onAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.WOOD_CLONE.get(), WoodCloneEntity.createAttributes().build());
    }

    private static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new QuestLoader());
    }
}
