package com.mcmiddleearth.entities.entities;

import com.mcmiddleearth.entities.exception.InvalidLocationException;
import com.mcmiddleearth.entities.protocol.packets.SimpleEntityAnimationPacket;
import com.mcmiddleearth.entities.protocol.packets.SimpleEntityStatusPacket;
import com.mcmiddleearth.entities.protocol.packets.SimpleLivingEntitySpawnPacket;

public class SimpleLivingEntity extends SimpleEntity {

    protected SimpleEntityAnimationPacket animationPacket;

    private ActionType animation = null;

    public SimpleLivingEntity(int entityId, VirtualEntityFactory factory) throws InvalidLocationException {
        super(entityId, factory);
        spawnPacket = new SimpleLivingEntitySpawnPacket(this);
        animationPacket = new SimpleEntityAnimationPacket(entityId);
    }

    @Override
    public void doTick() {
        if(animation!=null) {
            switch(animation) {
                case HURT:
                    animationPacket.setAnimation(SimpleEntityAnimationPacket.AnimationType.TAKE_DAMAGE);
                    animationPacket.send(getViewers());
                    break;
                case ATTACK:
                    animationPacket.setAnimation(SimpleEntityAnimationPacket.AnimationType.SWING_MAIN_ARM);
                    animationPacket.send(getViewers());
                    break;
                case DEATH:
                    ((SimpleEntityStatusPacket) statusPacket).setStatusCode(SimpleEntityStatusPacket.StatusCode.ENTITY_DEATH);
                    statusPacket.send(getViewers());
                    break;
            }
            animation = null;
        }
        super.doTick();
    }

    @Override
    public void playAnimation(ActionType type) {
        this.animation = type;
    }

}
