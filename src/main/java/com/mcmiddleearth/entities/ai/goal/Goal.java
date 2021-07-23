package com.mcmiddleearth.entities.ai.goal;

public interface Goal {

    void update();

    void doTick();

    int getUpdateInterval();

    boolean isFinished();
}
