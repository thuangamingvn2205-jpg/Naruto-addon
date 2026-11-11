package com.example.narutomod.entity;

import com.example.narutomod.registry.ModEntities;
import com.example.narutomod.registry.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class KunaiEntity extends ThrownToolEntity {
    public KunaiEntity(EntityType<? extends KunaiEntity> type, Level level) {
        super(type, level);
    }

    public KunaiEntity(Level level, LivingEntity owner) {
        super(ModEntities.KUNAI.get(), owner, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.THROWING_KUNAI.get();
    }

    @Override
    protected float damageAmount() {
        return 7.0f;
    }
}
