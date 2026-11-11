package com.example.narutomod.entity;

import com.example.narutomod.registry.ModEntities;
import com.example.narutomod.registry.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class ShurikenEntity extends ThrownToolEntity {
    public ShurikenEntity(EntityType<? extends ShurikenEntity> type, Level level) {
        super(type, level);
    }

    public ShurikenEntity(Level level, LivingEntity owner) {
        super(ModEntities.SHURIKEN.get(), owner, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.SHURIKEN.get();
    }

    @Override
    protected float damageAmount() {
        return 5.0f;
    }
}
