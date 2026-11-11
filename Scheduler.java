package com.example.narutomod.util;

import com.example.narutomod.NarutoMod;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Hẹn giờ chạy tác vụ theo tick của server. */
public final class Scheduler {
    private record Task(long at, Runnable run, boolean cleanup) {}

    private static final List<Task> TASKS = new ArrayList<>();

    private Scheduler() {}

    public static void later(MinecraftServer s, int delay, Runnable r) {
        TASKS.add(new Task(s.getTickCount() + Math.max(0, delay), r, false));
    }

    /** Giống later, nhưng nếu server tắt trước giờ hẹn thì vẫn chạy (dùng để dọn dẹp). */
    public static void laterCleanup(MinecraftServer s, int delay, Runnable r) {
        TASKS.add(new Task(s.getTickCount() + Math.max(0, delay), r, true));
    }

    public static void init() {
        NeoForge.EVENT_BUS.addListener(Scheduler::onTick);
        NeoForge.EVENT_BUS.addListener(Scheduler::onStopping);
    }

    private static void onTick(ServerTickEvent.Post event) {
        if (TASKS.isEmpty()) return;
        long now = event.getServer().getTickCount();
        List<Task> due = new ArrayList<>();
        Iterator<Task> it = TASKS.iterator();
        while (it.hasNext()) {
            Task t = it.next();
            if (t.at() <= now) {
                due.add(t);
                it.remove();
            }
        }
        for (Task t : due) run(t);
    }

    private static void onStopping(ServerStoppingEvent event) {
        for (Task t : new ArrayList<>(TASKS)) {
            if (t.cleanup()) run(t);
        }
        TASKS.clear();
        TempBlocks.restoreAll();
    }

    private static void run(Task t) {
        try {
            t.run().run();
        } catch (Exception e) {
            NarutoMod.LOGGER.warn("Scheduled task failed", e);
        }
    }
}
