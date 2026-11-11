package com.example.narutomod.registry;

import com.example.narutomod.NarutoMod;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;

/** Băng đô Làng Lá và Nón Kage. Texture: textures/models/armor/<tên>_layer_1.png */
public final class ModArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, NarutoMod.MOD_ID);

    public static final Holder<ArmorMaterial> KONOHA = MATERIALS.register("konoha", () -> make("konoha", 2, 12, 0f));
    public static final Holder<ArmorMaterial> KAGE = MATERIALS.register("kage", () -> make("kage", 4, 20, 2f));

    private ModArmorMaterials() {}

    private static ArmorMaterial make(String name, int helmetDefense, int enchantability, float toughness) {
        EnumMap<ArmorItem.Type, Integer> defense = new EnumMap<>(ArmorItem.Type.class);
        for (ArmorItem.Type t : ArmorItem.Type.values()) {
            defense.put(t, t == ArmorItem.Type.HELMET ? helmetDefense : 0);
        }
        return new ArmorMaterial(defense, enchantability, SoundEvents.ARMOR_EQUIP_IRON,
                () -> Ingredient.of(Items.IRON_INGOT),
                List.of(new ArmorMaterial.Layer(NarutoMod.id(name))), toughness, 0f);
    }
}
