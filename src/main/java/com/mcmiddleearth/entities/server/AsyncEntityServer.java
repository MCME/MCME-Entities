package com.mcmiddleearth.entities.server;

import com.mcmiddleearth.entities.EntitiesPlugin;

public class AsyncEntityServer extends SyncEntityServer {

    private volatile int skippedTicks;

    private volatile boolean isTicking = false;

    public AsyncEntityServer(EntitiesPlugin plugin) {
        super(plugin);
    }

    public void doTick() {

    }

    public void doAsyncTick() {
        try {
            int lastSkippedTicks = 0;
            synchronized (this) {
                isTicking = true;
                lastSkippedTicks = skippedTicks;
                skippedTicks = 0;
            }

        } finally {
            isTicking = false;
        }
    }

    public boolean isTicking() {
        return isTicking;
    }

    public synchronized void skip() {
        skippedTicks++;
    }
}
