package com.example.narutomod.registry;

import com.example.narutomod.NarutoMod;
import com.example.narutomod.entity.KunaiEntity;
import com.example.narutomod.entity.ShurikenEntity;
import com.example.narutomod.item.FireballJutsuItem;
import com.example.narutomod.item.JutsuScrollItem;
import com.example.narutomod.item.MissionBookItem;
import com.example.narutomod.item.RamenItem;
import com.example.narutomod.item.RasenganItem;
import com.example.narutomod.item.ThrowingToolItem;
import com.example.narutomod.jutsu.Jutsus;
import com.example.narutomod.jutsu.WoodJutsu;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Predicate;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(NarutoMod.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NarutoMod.MOD_ID);

    public static final DeferredItem<Item> KUNAI = ITEMS.register("kunai",
            () -> new SwordItem(Tiers.IRON, new Item.Properties().attributes(SwordItem.createAttributes(Tiers.IRON, 1, -1.6f))));

    public static final DeferredItem<Item> SHURIKEN = ITEMS.register("shuriken",
            () -> new ThrowingToolItem(ShurikenEntity::new, "shuriken", 2.0f, 6, new Item.Properties().stacksTo(16)));

    public static final DeferredItem<Item> THROWING_KUNAI = ITEMS.register("throwing_kunai",
            () -> new ThrowingToolItem(KunaiEntity::new, "kunai", 2.2f, 8, new Item.Properties().stacksTo(16)));

    public static final DeferredItem<Item> KONOHA_HEADBAND = ITEMS.register("konoha_headband",
            () -> new ArmorItem(ModArmorMaterials.KONOHA, ArmorItem.Type.HELMET,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(20))));

    public static final DeferredItem<Item> KAGE_HAT = ITEMS.register("kage_hat",
            () -> new ArmorItem(ModArmorMaterials.KAGE, ArmorItem.Type.HELMET,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(40)).fireResistant()));

    public static final DeferredItem<Item> MISSION_BOOK = ITEMS.register("mission_book",
            () -> new MissionBookItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> RASENGAN_SCROLL = ITEMS.register("rasengan_scroll",
            () -> new RasenganItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> FIREBALL_SCROLL = ITEMS.register("fireball_scroll",
            () -> new FireballJutsuItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> WOOD_CLONE_SCROLL = scroll("wood_clone_scroll", Jutsus.WOOD_CLONE, WoodJutsu::clone);
    public static final DeferredItem<Item> WOOD_HOUSE_SCROLL = scroll("wood_house_scroll", Jutsus.WOOD_HOUSE, WoodJutsu::house);
    public static final DeferredItem<Item> WOOD_PRISON_SCROLL = scroll("wood_prison_scroll", Jutsus.WOOD_PRISON, WoodJutsu::prison);
    public static final DeferredItem<Item> WOOD_SPEAR_SCROLL = scroll("wood_spear_scroll", Jutsus.WOOD_SPEAR, WoodJutsu::spear);
    public static final DeferredItem<Item> WOOD_DRAGON_SCROLL = scroll("wood_dragon_scroll", Jutsus.WOOD_DRAGON, WoodJutsu::dragon);
    public static final DeferredItem<Item> WOOD_FOREST_SCROLL = scroll("wood_forest_scroll", Jutsus.WOOD_FOREST, WoodJutsu::forest);
    public static final DeferredItem<Item> WOOD_GOLEM_SCROLL = scroll("wood_golem_scroll", Jutsus.WOOD_GOLEM, WoodJutsu::golem);

    public static final DeferredItem<Item> RAMEN = ITEMS.register("ramen",
            () -> new RamenItem(new Item.Properties().stacksTo(16).food(
                    new FoodProperties.Builder()
                            .nutrition(10)
                            .saturationModifier(1.2f)
                            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20 * 45, 0), 1.0f)
                            .build())));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = TABS.register("naruto",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.narutomod.main"))
                    .icon(() -> new ItemStack(KONOHA_HEADBAND.get()))
                    .displayItems((params, out) -> {
                        out.accept(MISSION_BOOK.get());
                        out.accept(KUNAI.get());
                        out.accept(THROWING_KUNAI.get());
                        out.accept(SHURIKEN.get());
                        out.accept(KONOHA_HEADBAND.get());
                        out.accept(KAGE_HAT.get());
                        out.accept(RASENGAN_SCROLL.get());
                        out.accept(FIREBALL_SCROLL.get());
                        out.accept(WOOD_CLONE_SCROLL.get());
                        out.accept(WOOD_HOUSE_SCROLL.get());
                        out.accept(WOOD_PRISON_SCROLL.get());
                        out.accept(WOOD_SPEAR_SCROLL.get());
                        out.accept(WOOD_DRAGON_SCROLL.get());
                        out.accept(WOOD_FOREST_SCROLL.get());
                        out.accept(WOOD_GOLEM_SCROLL.get());
                        out.accept(RAMEN.get());
                    })
                    .build());

    private ModItems() {}

    private static DeferredItem<Item> scroll(String name, Jutsus.Def def, Predicate<ServerPlayer> action) {
        return ITEMS.register(name, () -> new JutsuScrollItem(def, action, new Item.Properties().stacksTo(1)));
    }
}
