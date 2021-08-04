package com.mcmiddleearth.entities.ai.goal;

import com.mcmiddleearth.entities.ai.movement.MovementSpeed;

public interface Goal {

    void update();

    void doTick();

    int getUpdateInterval();

    boolean isFinished();

    MovementSpeed getMovementSpeed();
}
