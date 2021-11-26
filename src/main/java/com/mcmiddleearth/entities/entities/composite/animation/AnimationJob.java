package com.mcmiddleearth.entities.entities.composite.animation;

import com.mcmiddleearth.entities.entities.VirtualEntity;

public class AnimationJob {

    private final BakedAnimation animation;
    private final VirtualEntity.Payload payload;
    int delay;

    public AnimationJob(BakedAnimation animation, VirtualEntity.Payload payload, int delay) {
        this.animation = animation;
        this.payload = payload;
        this.delay = delay;
    }

    public void doTick() {
        if(animation!=null) {
            animation.doTick();
            if(payload !=null && animation.getCurrentFrame()==delay) {
                payload.execute();
            }
        }
    }

    public BakedAnimation getAnimation() {
        return animation;
    }
}
