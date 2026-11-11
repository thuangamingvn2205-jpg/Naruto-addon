package com.example.narutomod.client;

import com.example.narutomod.registry.ModEntities;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/** Chỉ được nạp trên client (xem NarutoMod). */
public final class NarutoClient {
    private NarutoClient() {}

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.SHURIKEN.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.KUNAI.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.WOOD_CLONE.get(), WoodCloneRenderer::new);
    }
}
