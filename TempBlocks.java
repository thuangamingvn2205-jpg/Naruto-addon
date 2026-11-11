package com.example.narutomod.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Khối tạm thời của các nhẫn thuật Mộc Độn: tự biến mất và trả lại khối cũ. */
public final class TempBlocks {
    private static final class Entry {
        final ServerLevel level;
        final BlockPos pos;
        final Block placed;
        final BlockState original;
        final long expire;

        Entry(ServerLevel level, BlockPos pos, Block placed, BlockState original, long expire) {
            this.level = level;
            this.pos = pos;
            this.placed = placed;
            this.original = original;
            this.expire = expire;
        }
    }

    private static final List<Entry> ENTRIES = new ArrayList<>();

    private TempBlocks() {}

    public static void init() {
        NeoForge.EVENT_BUS.addListener(TempBlocks::onTick);
    }

    public static boolean place(ServerLevel w, BlockPos pos, BlockState state, int lifeTicks) {
        if (w.isOutsideBuildHeight(pos)) return false;
        BlockState cur = w.getBlockState(pos);
        if (!(cur.isAir() || cur.canBeReplaced())) return false;
        w.setBlock(pos, state, Block.UPDATE_ALL);
        ENTRIES.add(new Entry(w, pos.immutable(), state.getBlock(), cur, w.getServer().getTickCount() + lifeTicks));
        return true;
    }

    private static void onTick(ServerTickEvent.Post event) {
        if (ENTRIES.isEmpty()) return;
        long now = event.getServer().getTickCount();
        Iterator<Entry> it = ENTRIES.iterator();
        while (it.hasNext()) {
            Entry e = it.next();
            if (e.expire <= now) {
                restore(e, true);
                it.remove();
            }
        }
    }

    public static void restoreAll() {
        for (Entry e : ENTRIES) restore(e, false);
        ENTRIES.clear();
    }

    private static void restore(Entry e, boolean fx) {
        if (e.level.getBlockState(e.pos).is(e.placed)) {
            e.level.setBlock(e.pos, e.original, Block.UPDATE_ALL);
            if (fx) {
                e.level.sendParticles(ParticleTypes.COMPOSTER, e.pos.getX() + 0.5, e.pos.getY() + 0.5,
                        e.pos.getZ() + 0.5, 3, 0.25, 0.25, 0.25, 0.02);
            }
        }
    }
}
