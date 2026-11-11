package com.example.narutomod.client;

import com.example.narutomod.NarutoMod;
import com.example.narutomod.entity.WoodCloneEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class WoodCloneRenderer extends HumanoidMobRenderer<WoodCloneEntity, HumanoidModel<WoodCloneEntity>> {
    private static final ResourceLocation TEXTURE = NarutoMod.id("textures/entity/wood_clone.png");

    public WoodCloneRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(WoodCloneEntity entity) {
        return TEXTURE;
    }
}
