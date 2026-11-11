package com.example.narutomod.registry;

import com.example.narutomod.NarutoMod;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Khối Mộc Độn: không rơi vật phẩm nên không thể "farm" gỗ bằng nhẫn thuật. */
public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(NarutoMod.MOD_ID);

    public static final DeferredBlock<Block> MOKUTON_WOOD = BLOCKS.register("mokuton_wood",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD)
                    .strength(2.0f, 3.0f).sound(SoundType.WOOD).noLootTable()));

    public static final DeferredBlock<Block> MOKUTON_LEAF = BLOCKS.register("mokuton_leaf",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT)
                    .strength(0.6f).sound(SoundType.GRASS).noLootTable()));

    private ModBlocks() {}
}
