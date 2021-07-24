package com.mcmiddleearth.entities.protocol.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;

public class SimpleEntityAnimationPacket extends AbstractPacket {

    PacketContainer animation;

    public SimpleEntityAnimationPacket(int entityId) {
        animation = new PacketContainer(PacketType.Play.Server.ANIMATION);
        animation.getIntegers().write(0,entityId);
    }

    public void setAnimation(SimpleEntityAnimationPacket.AnimationType type) {
        animation.getBytes().write(0,type.animationId);
    }

    @Override
    public void send(Player recipient) {
        send(animation,recipient);
    }

    public enum AnimationType {
        SWING_MAIN_ARM     (0),
        TAKE_DAMAGE         (1),
        LEAVE_BED           (2),
        SWING_OFF_ARM       (3),
        CRITICAL            (4),
        CRITICAL_MAGIC      (5);

        byte animationId;

        AnimationType(int animationId) {
            this.animationId = (byte) animationId;
        }

        public byte getAnimationId() {
            return animationId;
        }
    }
}
