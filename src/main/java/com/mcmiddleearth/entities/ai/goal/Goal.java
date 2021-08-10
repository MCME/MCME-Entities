package com.mcmiddleearth.entities.ai.goal;

import com.mcmiddleearth.entities.api.MovementSpeed;
import org.bukkit.util.Vector;

public interface Goal {

    void update();

    void doTick();

    int getUpdateInterval();

    boolean isFinished();

    MovementSpeed getMovementSpeed();

    int getUpdateRandom();

    Vector getDirection();

    boolean hasRotation();

    boolean hasHeadRotation();

    float getHeadYaw();

    float getHeadPitch();

    float getYaw();
    float getPitch();
    float getRoll();
}
