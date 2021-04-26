package com.mcmiddleearth.entities.ai.goals;

public interface Goal {

    void update();

    void doTick();

    int getUpdateInterval();

    boolean isFinished();
}
