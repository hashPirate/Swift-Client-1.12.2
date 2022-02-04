package me.pignol.swift.client.managers;

import me.pignol.swift.api.interfaces.Globals;
import me.pignol.swift.api.util.runnables.SafetyRunnable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class SafetyManager implements Globals {

    private static final SafetyManager INSTANCE = new SafetyManager();

    public static SafetyManager getInstance() {
        return INSTANCE;
    }

    final ExecutorService executor = Executors.newSingleThreadExecutor();
    final AtomicBoolean safe = new AtomicBoolean(false);

    public void update() {
        if (isNull())
            return;
        SafetyRunnable runnable = new SafetyRunnable(this, mc.world.loadedEntityList);
        executor.submit(runnable);
    }

    public void setSafe(boolean safe) {
        this.safe.set(safe);
    }

    public boolean isSafe() {
        return safe.get();
    }

}
